package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class SmartImageTask implements Runnable {
	public static final int MSG_START = 1;
	public static final int MSG_SUCCESS = 3;
	public static final int MSG_FAILURE = 4;
	public static final int MSG_PROGRESS = 5;

	private OnCompleteHandler onCompleteHandler;
	private SmartImage image;
	private Context context;
	private int width = 0, height = 0;
	
	private boolean cancel=false;

	public static class OnCompleteHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what)
			{
				case MSG_START:
					onStart();
					break;
				case MSG_FAILURE:
					onFailure();
					break;
				case MSG_PROGRESS:
					Object[] progress = (Object[])msg.obj;
					onProgress((Long)progress[0], (Long)progress[1]);
					break;
				case MSG_SUCCESS:
					onSuccess((Bitmap)msg.obj);
					break;
			}
		}

		public void onStart() {
		}

		public void onSuccess(Bitmap bitmap) {
		}

		public void onFailure() {
		}

		public void onProgress(long current, long total) {
		}
	}

	public SmartImageTask(Context context, SmartImage image, OnCompleteHandler handler) {
		this.image = image;
		this.context = context;
		this.onCompleteHandler = handler;
	}

	public SmartImageTask(Context context, SmartImage image, int width, int height, OnCompleteHandler handler) {
		this.image = image;
		this.context = context;
		this.width = width;
		this.height = height;
		this.onCompleteHandler = handler;
	}

	@Override
	public synchronized void run() {
		if (!cancel && !Thread.currentThread().isInterrupted() && image != null && onCompleteHandler != null)
		{
			onCompleteHandler.sendEmptyMessage(MSG_START);
			if (image instanceof WebImage && width != 0 && height != 0)
				complete(((WebImage)image).getBitmap(context, width, height, onCompleteHandler));
			else
				complete(image.getBitmap(context, onCompleteHandler));
		}
	}

	private void complete(Bitmap bitmap) {
		if (!cancel && !Thread.currentThread().isInterrupted() && onCompleteHandler != null )
		{
			if (bitmap != null && !bitmap.isRecycled())
				onCompleteHandler.sendMessage(onCompleteHandler.obtainMessage(MSG_SUCCESS, bitmap));
			else
				onCompleteHandler.sendEmptyMessage(MSG_FAILURE);
		}
	}

	public void cancel() {
		if(!cancel)
		{
			this.cancel = true;
			image.cancel();
			Thread.currentThread().interrupt();
		}
	}
}
