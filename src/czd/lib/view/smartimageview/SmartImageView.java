package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import czd.lib.R;
import czd.lib.cache.BitmapCache;
import czd.lib.view.ViewUtil;
import czd.lib.view.smartimageview.SmartImageTask.OnCompleteListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SmartImageView extends FrameLayout {
	protected static ExecutorService threadPool = Executors.newFixedThreadPool(3);
	protected static Map<Context,List<WeakReference<Future<?>>>> requestMap = new WeakHashMap<Context,List<WeakReference<Future<?>>>>();

	protected boolean loading = false;
	protected SmartImageTask currentTask;
	protected Context context;
	protected Future<?> request;

	protected ImageView imageview;
	protected LinearLayout progress;
	private int width = 0, height = 0;
	private String file;

	private Bitmap imagebitmap;

	public SmartImageView(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	public SmartImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	public SmartImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initView();
	}

	private void initView() {
		imageview = new ImageView(this.context);
		imageview.setScaleType(ScaleType.CENTER_CROP);
		imageview.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageview.setVisibility(View.INVISIBLE);
		this.addView(imageview);
		progress = new LinearLayout(context);
		progress = (LinearLayout)ViewUtil.viewById(context, R.layout.common_loading_frame);
		progress.setVisibility(View.INVISIBLE);
		this.addView(progress);
	}

	// Helpers to set image by URL
	public void setImageUrl(String url) {
		setImage(new WebImage(url));
	}

	public void setImageUrl(String url, final boolean useloading, SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url), useloading, completeListener);
	}

	public void setImageUrl(String url, final boolean useloading, final Integer fallbackResource) {
		setImage(new WebImage(url), useloading, fallbackResource);
	}

	public void setImageUrl(String url, final boolean useloading, final Integer fallbackResource, OnCompleteListener completeListener) {
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

	public void setImageResource(int resId) {
		imageview.setImageResource(resId);
		imageview.setVisibility(View.VISIBLE);
	}

	public void setImageDrawable(Drawable drawable) {
		imageview.setImageDrawable(drawable);
		imageview.setVisibility(View.VISIBLE);
	}

	// Set image using SmartImage object
	public void setImage(final SmartImage image) {
		setImage(image, false, null, null);
	}

	public void setImage(final SmartImage image, final boolean useloading, final OnCompleteListener completeListener) {
		setImage(image, useloading, null, completeListener);
	}

	public void setImage(final SmartImage image, final boolean useloading, final Integer fallbackResource) {
		setImage(image, useloading, fallbackResource, null);
	}

	public void setImage(final SmartImage image, final boolean useloading, final Integer fallbackResource, final OnCompleteListener completeListener) {
		if (!loading || Thread.currentThread().isInterrupted())
		{
			loading = true;

			if (useloading)
				progress.setVisibility(View.VISIBLE);

			// Cancel any existing tasks for this image view
			if (currentTask != null)
			{
				currentTask.cancel();
				currentTask = null;
			}

			// Set up the new task
			//currentTask = new SmartImageTask(context, image, width, height);
			currentTask = new SmartImageTask(context, image);
			currentTask.setOnCompleteHandler(new SmartImageTask.OnCompleteHandler() {
				@Override
				public void onComplete(Bitmap bitmap) {
					if (loading)
					{
						if (useloading)
						{
							progress.setVisibility(View.INVISIBLE);
						}
						file = BitmapCache.getInstance().getRealName(image.toString());
						if (bitmap != null && !bitmap.isRecycled())
						{
							width = bitmap.getWidth();
							height = bitmap.getHeight();
							imageview.setAdjustViewBounds(false);
							imageview.setImageBitmap(bitmap);
							if (completeListener != null)
							{
								completeListener.onComplete(true);
							}
						}
						else
						{
							// Set fallback resource
							if (fallbackResource != null)
							{
								imageview.setAdjustViewBounds(false);
								imageview.setImageResource(fallbackResource);
							}
							if (completeListener != null)
							{
								completeListener.onComplete(false);
							}
							loading = false;
						}
						imageview.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onProgress(long current, long total) {
					if (loading && completeListener != null)
					{
						completeListener.onProgress(current, total);
					}
				}
			});

			// Run the task in a threadpool
			request = threadPool.submit(currentTask);
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

	public File getBitmapFile() {
		return new File(file);
	}

	public void recycle() {
		loading = false;
		if (request != null)
		{
			request.cancel(true);
			if (context != null)
			{
				List<WeakReference<Future<?>>> requestList = requestMap.get(context);
				if (requestList != null)
				{
					requestList.remove(request);
				}
			}
		}
		imageview.setVisibility(View.INVISIBLE);
		if (!imagebitmap.isRecycled())
		{
			imagebitmap.recycle();
		}
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

	public static void cancelAllTasks() {
		requestMap.clear();
		threadPool.shutdownNow();
		threadPool = Executors.newFixedThreadPool(3);
	}
}
