package czd.lib.view.smartimageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import czd.lib.application.ApplicationUtil;
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
	protected static Map<Context, List<WeakReference<Future<?>>>> requestMap = new WeakHashMap<Context, List<WeakReference<Future<?>>>>();

	protected boolean loading = false;
	protected SmartImageTask currentTask;
	protected Context context;
	protected Future<?> request;

	protected GestureImageView imageview;
	protected ProgressCircle progress;
	private int width = 0, height = 0;
	private String file;

	public SmartGestureImageView(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	public SmartGestureImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	public SmartGestureImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initView();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initView() {
		imageview = new GestureImageView(this.context);
		imageview.setScaleType(ScaleType.CENTER_CROP);
		imageview.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageview.setVisibility(View.INVISIBLE);
		imageview.setRecycle(true);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			imageview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
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

	public void setImageUrl(String url, final boolean useloading, SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url), useloading, completeListener);
	}

	public void setImageUrl(String url, final boolean useloading, final Integer fallbackResource) {
		setImage(new WebImage(url), useloading, fallbackResource);
	}

	public void setImageUrl(String url, final boolean useloading, final Integer fallbackResource, SmartImageTask.OnCompleteListener completeListener) {
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

	public void setImage(final SmartImage image, final boolean useloading, final SmartImageTask.OnCompleteListener completeListener) {
		setImage(image, useloading, null, completeListener);
	}

	public void setImage(final SmartImage image, final boolean useloading, final Integer fallbackResource) {
		setImage(image, useloading, fallbackResource, null);
	}

	public void setImage(final SmartImage image, final boolean useloading, final Integer fallbackResource, final SmartImageTask.OnCompleteListener completeListener) {
		if (!loading || Thread.currentThread().isInterrupted()) {
			loading = true;

			progress.setVisibility(View.VISIBLE);

			// Cancel any existing tasks for this image view
			if (currentTask != null) {
				currentTask.cancel();
				currentTask = null;
			}

			// Set up the new task
			currentTask = new SmartImageTask(context, image);
			currentTask.setOnCompleteHandler(new SmartImageTask.OnCompleteHandler() {
				@Override
				public void onComplete(Bitmap bitmap) {
					if (loading) {
						if (useloading) {
							progress.setVisibility(View.INVISIBLE);
						}
						file = ApplicationUtil.webImageCache.getFilePath(image.toString());
						if (bitmap != null && !bitmap.isRecycled()) {
							width = bitmap.getWidth();
							height = bitmap.getHeight();
							imageview.setAdjustViewBounds(false);
							imageview.setImageBitmap(bitmap);
							if (completeListener != null) {
								completeListener.onComplete(true);
							}
						}
						else {
							// Set fallback resource
							if (fallbackResource != null) {
								imageview.setAdjustViewBounds(false);
								imageview.setImageResource(fallbackResource);
							}
							if (completeListener != null) {
								completeListener.onComplete(false);
							}
							loading = false;
						}
						imageview.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onProgress(long current, long total) {
					if (loading) {
						progress.setMax(total);
						progress.setProgress(current);
						if (completeListener != null) {
							completeListener.onProgress(current, total);
						}
					}
				}
			});

			// Run the task in a threadpool
			request = threadPool.submit(currentTask);
			if (request != null && context != null) {
				List<WeakReference<Future<?>>> requestList = requestMap.get(context);
				if (requestList == null) {
					requestList = new LinkedList<WeakReference<Future<?>>>();
					requestMap.put(context, requestList);
				}

				requestList.add(new WeakReference<Future<?>>(request));
			}
		}

	}

	public int[] getImageSize() {
		return new int[] { width, height };
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
		loading = false;
		if (request != null) {
			request.cancel(true);
			if (context != null) {
				List<WeakReference<Future<?>>> requestList = requestMap.get(context);
				if (requestList != null) {
					requestList.remove(request);
				}
			}
		}
		imageview.setVisibility(View.INVISIBLE);
		imageview.recycle();
		width = 0;
		height = 0;
	}

	public void cancel() {
		recycle();
	}

	public static void cancelTasks(Context context, boolean mayInterruptIfRunning) {
		List<WeakReference<Future<?>>> requestList = requestMap.get(context);
		if (requestList != null) {
			for (WeakReference<Future<?>> requestRef : requestList) {
				Future<?> request = requestRef.get();
				if (request != null) {
					request.cancel(mayInterruptIfRunning);
				}
			}
		}
		requestMap.remove(context);
	}

	public static void cancelAllTasks() {
		requestMap.clear();
		threadPool.shutdownNow();
		threadPool = Executors.newSingleThreadExecutor();
	}
}
