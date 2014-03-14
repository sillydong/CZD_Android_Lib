package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import czd.lib.R;
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

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 14-3-10
 * Time: 下午8:59
 */
public abstract class AbsSmartView<T extends ImageView> extends FrameLayout {
	protected static ExecutorService threadPool = Executors.newFixedThreadPool(3);
	protected static Map<Context,List<WeakReference<Future<?>>>> requestMap = new WeakHashMap<Context,List<WeakReference<Future<?>>>>();

	protected static final int MSG_START = 1;
	protected static final int MSG_FINISH = 2;
	protected static final int MSG_SUCCESS = 3;
	protected static final int MSG_FAILURE = 4;
	protected static final int MSG_PROGRESS = 5;

	protected Future<?> currentTask;
	protected Context context;
	protected T imageview;
	protected ProgressCircle progress;
	protected SmartImage image;
	
	protected static String useragent="";

	protected ImageView.ScaleType scaletype = ImageView.ScaleType.CENTER_INSIDE;

	public AbsSmartView(Context context) {
		super(context);
		this.context = context;
		init(null);
	}

	public AbsSmartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	public AbsSmartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init(attrs);
	}

	protected abstract void init(AttributeSet attrs);

	public void setImageBitmap(Bitmap bitmap) {
		this.imageview.setImageBitmap(bitmap);
	}

	public void setImageDrawable(Drawable drawable) {
		this.imageview.setImageDrawable(drawable);
	}

	public void setImageResource(int resId) {
		this.imageview.setImageResource(resId);
	}
	
	public static void setUserAgent(String useragent){
		AbsSmartView.useragent=useragent;
	}

	public void setImageUrl(String url, final SmartImageListener listener) {
		setImage(new WebImage(url), listener);
	}

	public void setImageContact(long contactId, final SmartImageListener listener) {
		setImage(new ContactImage(contactId), listener);
	}

	public void setImageVideo(File file, final SmartImageListener listener) {
		setImage(new VideoImage(file), listener);
	}

	public void setImage(SmartImage image, final SmartImageListener listener) {
		this.image = image;
		if (this.image!=null && (currentTask == null || currentTask.isCancelled() || currentTask.isDone()))
		{
			currentTask = threadPool.submit(new SmartTask(context, this.image, new ViewHandler() {
				@Override
				public void start() {
					super.start();
					onStart(listener);
				}

				@Override
				public void finish() {
					super.finish();
					onFinish(listener);
				}

				@Override
				public void success(Bitmap bitmap) {
					super.success(bitmap);
					if (bitmap != null && !bitmap.isRecycled())
						onSuccess(bitmap, listener);
					else
						failure();
				}

				@Override
				public void failure() {
					super.failure();
					onFailure(listener);
				}

				@Override
				public void progress(long current, long total) {
					super.progress(current, total);
					onProgress(current, total, listener);
				}
			}));

			if (context != null)
			{
				List<WeakReference<Future<?>>> requestList = requestMap.get(context);
				if (requestList == null)
				{
					requestList = new LinkedList<WeakReference<Future<?>>>();
					requestMap.put(context, requestList);
				}

				requestList.add(new WeakReference<Future<?>>(currentTask));
			}
		}
	}

	protected void onStart(SmartImageListener listener) {
		//Log.v("Pull", "task start");
		progress.setVisibility(View.VISIBLE);
		if (listener != null)
			listener.onStart();
	}

	protected void onFinish(SmartImageListener listener) {
		//Log.v("Pull", "task finish");
		progress.setVisibility(View.INVISIBLE);
		if (listener != null)
			listener.onFinish();
	}

	protected void onSuccess(Bitmap bitmap, SmartImageListener listener) {
		//Log.v("Pull", "task success");
		//will cause requestLayout() improperly called warn
		imageview.setScaleType(scaletype);
		imageview.setImageBitmap(bitmap);
		imageview.setVisibility(View.VISIBLE);
		if (listener != null)
			listener.onSuccess();
	}

	protected void onFailure(SmartImageListener listener) {
		//Log.v("Pull", "task failure");
		//will cause requestLayout() improperly called warn
		imageview.setScaleType(ImageView.ScaleType.CENTER);
		imageview.setImageResource(R.drawable.image);
		imageview.setVisibility(View.VISIBLE);
		if (listener != null)
			listener.onFailure();
	}

	protected void onProgress(long current, long total, SmartImageListener listener) {
		//Log.v("Pull", "task progress");
		progress.setMax(total);
		progress.setProgress(current);
		if (listener != null)
			listener.onProgress(current, total);
	}

	public void recycle() {
		if (currentTask != null && !currentTask.isCancelled())
			currentTask.cancel(true);
		if(this.image!=null)
			this.image.recycle();
		progress.setVisibility(View.INVISIBLE);
		imageview.setVisibility(View.INVISIBLE);
	}

	public void cancel() {
		recycle();
	}

	public boolean isCanceled() {
		return currentTask.isCancelled();
	}

	public boolean isDone() {
		return currentTask.isDone();
	}

	public static void cancelTasks(Context context) {
		List<WeakReference<Future<?>>> requestList = requestMap.get(context);
		if (requestList != null)
		{
			for (WeakReference<Future<?>> requestRef : requestList)
			{
				Future<?> request = requestRef.get();
				if (request != null && !request.isCancelled())
				{
					request.cancel(true);
				}
			}
		}
		requestMap.remove(context);
		System.gc();
	}

	public static void cancelAllTasks() {
		for (List<WeakReference<Future<?>>> requestList : requestMap.values())
		{
			if (requestList != null)
			{
				for (WeakReference<Future<?>> requestRef : requestList)
				{
					Future<?> request = requestRef.get();
					if (request != null && !request.isCancelled())
					{
						request.cancel(true);
					}
				}
			}
		}
		requestMap.clear();
		System.gc();
	}

	protected class SmartTask implements Runnable {


		private SmartImage image;
		private Context context;
		private AbsSmartView.ViewHandler handler;


		public SmartTask(Context context, SmartImage image, AbsSmartView.ViewHandler handler) {
			this.context = context;
			this.image = image;
			this.handler = handler;
		}

		@Override
		public void run() {
			if (!Thread.currentThread().isInterrupted() && image != null && handler != null)
			{
				image.getBitmap(context, handler);
			}
		}
	}

	protected static class ViewHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what)
			{
				case MSG_START:
					start();
					break;
				case MSG_FINISH:
					finish();
					break;
				case MSG_SUCCESS:
					success((Bitmap)msg.obj);
					break;
				case MSG_FAILURE:
					failure();
					break;
				case MSG_PROGRESS:
					progress(msg.arg1, msg.arg2);
					break;
			}
		}

		public void start() {

		}

		public void finish() {

		}

		public void success(Bitmap bitmap) {

		}

		public void failure() {

		}

		public void progress(long current, long total) {

		}
	}

	public abstract interface SmartImageListener {
		public abstract void onStart();

		public abstract void onFinish();

		public abstract void onSuccess();

		public abstract void onFailure();

		public abstract void onProgress(long current, long total);
	}
}
