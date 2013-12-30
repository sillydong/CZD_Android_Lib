package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class SmartImageTask implements Runnable {
	private static final int BITMAP_READY = 0;
	public static final int BITMAP_LOADING = 1;

	private OnCompleteHandler onCompleteHandler;
	private SmartImage image;
	private Context context;
	private int width = 0, height = 0;

	public static class OnCompleteHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BITMAP_READY)
			{
				onComplete((Bitmap)msg.obj);
			}
			else if (msg.what == BITMAP_LOADING)
			{
				Object[] data = (Object[])msg.obj;
				onProgress(((Long)data[0]).longValue(), ((Long)data[1]).longValue());
			}
		}

		public void onComplete(Bitmap bitmap) {
		}

		;

		public void onProgress(long current, long total) {
		}

		;
	}

	public abstract static class OnCompleteListener {
		public abstract void onComplete(boolean success);

		public abstract void onProgress(long current, long total);
	}

	public SmartImageTask(Context context, SmartImage image) {
		this.image = image;
		this.context = context;
	}

	public SmartImageTask(Context context, SmartImage image, int width, int height) {
		this.image = image;
		this.context = context;
		this.width = width;
		this.height = height;
	}

	@Override
	public void run() {
		if (!Thread.currentThread().isInterrupted() && image != null)
		{
			if (image instanceof WebImage && onCompleteHandler != null)
			{
				if (width != 0 && height != 0)
				{
					complete(((WebImage)image).getBitmap(context, width, height, onCompleteHandler));
				}
				else
				{
					complete(((WebImage)image).getBitmap(context, onCompleteHandler));
				}
			}
			else
			{
				complete(image.getBitmap(context));
			}
		}
	}

	public void setOnCompleteHandler(OnCompleteHandler handler) {
		this.onCompleteHandler = handler;
	}

	public void cancel() {
		if (!Thread.currentThread().isInterrupted())
		{
			Thread.currentThread().interrupt();
		}
	}

	public void complete(Bitmap bitmap) {
		if (onCompleteHandler != null && !Thread.currentThread().isInterrupted())
		{
			onCompleteHandler.sendMessage(onCompleteHandler.obtainMessage(BITMAP_READY, bitmap));
		}
	}
}
