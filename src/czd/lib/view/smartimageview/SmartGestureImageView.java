package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import czd.lib.cache.BitmapCache;
import czd.lib.view.gestureimageview.GestureImageView;
import czd.lib.view.progress.ProgressCircle;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SmartGestureImageView extends FrameLayout {
	protected static ExecutorService threadPool = Executors.newSingleThreadExecutor();
	protected static Map<Context,List<WeakReference<Future<?>>>> requestMap = new WeakHashMap<Context,List<WeakReference<Future<?>>>>();

	protected boolean loading = false;
	protected SmartImageTask currentTask;
	protected Context context;

	protected GestureImageView imageview;
	protected ProgressCircle progress;
	protected int width = 0, height = 0;
	protected String file;
	protected boolean useloading = false;

	protected Bitmap imagebitmap;

	public SmartGestureImageView(Context context) {
		super(context);
		this.context = context;
		initView(null);
	}

	public SmartGestureImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(attrs);
	}

	public SmartGestureImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initView(attrs);
	}

	private void initView(AttributeSet attrs) {
		if (attrs == null)
			imageview = new GestureImageView(this.context);
		else
			imageview = new GestureImageView(this.context, attrs);
		imageview.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageview.setVisibility(View.INVISIBLE);
		imageview.setRecycle(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			imageview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		this.addView(imageview);
		progress = new ProgressCircle(this.context);
		progress.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		progress.setVisibility(View.INVISIBLE);
		progress.showText(true);
		this.addView(progress);
	}

	// Helpers to set image by URL
	public void setImageUrl(String url) {
		setImage(new WebImage(url));
	}

	public void setImageUrl(String url, final boolean useloading, SmartImageListener completeListener) {
		setImage(new WebImage(url), useloading, completeListener);
	}

	public void setImageUrl(String url, final boolean useloading, final Integer fallbackResource) {
		setImage(new WebImage(url), useloading, fallbackResource);
	}

	public void setImageUrl(String url, final boolean useloading, final Integer fallbackResource, SmartImageListener completeListener) {
		setImage(new WebImage(url), useloading, fallbackResource, completeListener);
	}

	// Helpers to set image by contact address book id
	public void setImageContact(long contactId) {
		setImage(new ContactImage(contactId));
	}

	public void setImageContact(long contactId, final Integer fallbackResource) {
		setImage(new ContactImage(contactId), false, fallbackResource);
	}

	public void setImageContact(long contactId, final Integer fallbackResource, final Integer loadingResource) {
		setImage(new ContactImage(contactId), false, fallbackResource);
	}

	// Set image using SmartImage object
	public void setImage(final SmartImage image) {
		setImage(image, false, null, null);
	}

	public void setImage(final SmartImage image, final boolean useloading, final SmartImageListener completeListener) {
		setImage(image, useloading, null, completeListener);
	}

	public void setImage(final SmartImage image, final boolean useloading, final Integer fallbackResource) {
		setImage(image, useloading, fallbackResource, null);
	}

	public void setImage(final SmartImage image, final boolean useloading, final Integer fallbackResource, final SmartImageListener completeListener) {
		if ((imagebitmap == null || imagebitmap.isRecycled()) && !loading)
		{
			this.useloading = useloading;
			currentTask = new SmartImageTask(context, image, new SmartImageTask.OnCompleteHandler() {
				@Override
				public void onStart() {
					super.onStart();
					if (!loading)
					{
						loading = true;
						if (useloading)
							progress.setVisibility(View.VISIBLE);

						if (completeListener != null)
							completeListener.onStart();
					}
				}

				@Override
				public void onFailure() {
					super.onFailure();
					if (loading)
					{
						loading = false;
						if (useloading)
							progress.setVisibility(View.INVISIBLE);

						if (fallbackResource != null)
						{
							imageview.setAdjustViewBounds(false);
							imageview.setImageResource(fallbackResource);
							imageview.setVisibility(View.VISIBLE);
						}

						if (completeListener != null)
							completeListener.onFailure();
					}
				}

				@Override
				public void onSuccess(Bitmap bitmap) {
					super.onSuccess(bitmap);
					if (loading)
					{
						loading = false;
						if (useloading)
							progress.setVisibility(View.INVISIBLE);

						file = BitmapCache.getInstance().getRealName(image.toString());
						imagebitmap = bitmap;
						width = imagebitmap.getWidth();
						height = imagebitmap.getHeight();
						imageview.setAdjustViewBounds(false);
						imageview.setImageBitmap(imagebitmap);
						imageview.setVisibility(View.VISIBLE);

						if (completeListener != null)
							completeListener.onSuccess();
					}
				}

				@Override
				public void onProgress(long current, long total) {
					super.onProgress(current, total);
					if (loading)
					{
						progress.setMax(total);
						progress.setProgress(current);
						if (completeListener != null)
							completeListener.onProgress(current, total);
					}

				}
			});

			Future<?> request = threadPool.submit(currentTask);
			if (request != null && context != null)
			{
				List<WeakReference<Future<?>>> requestList = requestMap.get(context);
				if (requestList == null)
				{
					requestList = new LinkedList<WeakReference<Future<?>>>();
					requestMap.put(context, requestList);
				}

				requestList.add(new WeakReference<Future<?>>(request));
			}
		}

	}

	public int[] getImageSize() {
		return new int[]{width, height};
	}

	public float getCurrentScale() {
		return imageview.getScale();
	}

	public void setScale(float scale) {
		imageview.handleScale(scale);
	}

	public File getBitmapFile() {
		return new File(file);
	}

	public void recycle() {
		if (loading)
		{
			loading = false;
			if (useloading)
				progress.setVisibility(View.INVISIBLE);

		}
		if (currentTask != null)
			currentTask.cancel();

		imageview.setVisibility(View.INVISIBLE);
		if (imagebitmap != null && !imagebitmap.isRecycled())
		{
			imagebitmap.recycle();
		}
		imagebitmap = null;
		width = 0;
		height = 0;
	}

	public void cancel() {
		recycle();
	}

	public static void cancelTasks(Context context, boolean mayInterruptIfRunning) {
		List<WeakReference<Future<?>>> requestList = requestMap.get(context);
		if (requestList != null)
		{
			for (WeakReference<Future<?>> requestRef : requestList)
			{
				Future<?> request = requestRef.get();
				if (request != null)
				{
					request.cancel(mayInterruptIfRunning);
				}
			}
		}
		requestMap.remove(context);
	}

	public static void cancelAllTasks(boolean mayInterruptIfRunning) {
		for (List<WeakReference<Future<?>>> requestList : requestMap.values())
		{
			if (requestList != null)
			{
				for (WeakReference<Future<?>> requestRef : requestList)
				{
					Future<?> request = requestRef.get();
					if (request != null && !request.isCancelled())
					{
						request.cancel(mayInterruptIfRunning);
					}
				}
			}
		}
		requestMap.clear();
		//threadPool.shutdownNow();
		//threadPool = Executors.newSingleThreadExecutor();
	}
}
