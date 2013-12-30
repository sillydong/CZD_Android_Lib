package czd.lib.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class AsyncHttpResponseHandler {
	protected static final int SUCCESS_MESSAGE = 0;
	protected static final int FAILURE_MESSAGE = 1;
	protected static final int START_MESSAGE = 2;
	protected static final int FINISH_MESSAGE = 3;
	protected static final int PROGRESS_MESSAGE = 4;

	private Handler handler;
	protected boolean cancel = false;

	public AsyncHttpResponseHandler() {
		// Set up a handler to post events back to the correct thread if possible
		if (Looper.myLooper() != null)
		{
			handler = new MessageHandler(this);
		}
	}

	private static class MessageHandler extends Handler {
		WeakReference<AsyncHttpResponseHandler> handlers;

		public MessageHandler(AsyncHttpResponseHandler handler) {
			this.handlers = new WeakReference<AsyncHttpResponseHandler>(handler);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AsyncHttpResponseHandler handler = handlers.get();
			handler.handleMessage(msg);
		}
	}

	public void onStart() {
	}

	public void onFinish() {
	}

	public void onSuccess(String content) {
	}

	public void onSuccess(int statusCode, String content) {
		onSuccess(content);
	}

	public void onFailure(Throwable error) {
	}

	public void onFailure(Throwable error, String content) {
		// By default, call the deprecated onFailure(Throwable) for compatibility
		onFailure(error);
	}

	public void onProgress(long current, long total) {
	}

	protected void sendSuccessMessage(int statusCode, String responseBody) {
		sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, responseBody}));
	}

	protected void sendFailureMessage(Throwable e, String responseBody) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
	}

	protected void sendFailureMessage(Throwable e, byte[] responseBody) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
	}

	protected void sendStartMessage() {
		cancel = false;
		sendMessage(obtainMessage(START_MESSAGE, null));
	}

	protected void sendFinishMessage() {
		sendMessage(obtainMessage(FINISH_MESSAGE, null));
	}

	protected void sendProgressMessage(long current, long total) {
		sendMessage(obtainMessage(PROGRESS_MESSAGE, new Object[]{current, total}));
	}

	protected void handleSuccessMessage(int statusCode, String responseBody) {
		onSuccess(statusCode, responseBody);
	}

	protected void handleFailureMessage(Throwable e, String responseBody) {
		onFailure(e, responseBody);
	}

	protected void handleProgressMessage(long current, long total) {
		onProgress(current, total);
	}

	protected void handleMessage(Message msg) {
		Object[] response;

		switch (msg.what)
		{
			case SUCCESS_MESSAGE:
				response = (Object[])msg.obj;
				handleSuccessMessage(((Integer)response[0]).intValue(), (String)response[1]);
				break;
			case FAILURE_MESSAGE:
				response = (Object[])msg.obj;
				handleFailureMessage((Throwable)response[0], (String)response[1]);
				break;
			case START_MESSAGE:
				onStart();
				break;
			case FINISH_MESSAGE:
				onFinish();
				break;
			case PROGRESS_MESSAGE:
				response = (Object[])msg.obj;
				onProgress(((Long)response[0]).longValue(), ((Long)response[1]).longValue());
				break;
		}
	}

	protected void sendMessage(Message msg) {
		if (handler != null)
		{
			handler.sendMessage(msg);
		}
		else
		{
			handleMessage(msg);
		}
	}

	protected Message obtainMessage(int responseMessage, Object response) {
		Message msg = null;
		if (handler != null)
		{
			msg = this.handler.obtainMessage(responseMessage, response);
		}
		else
		{
			msg = new Message();
			msg.what = responseMessage;
			msg.obj = response;
		}
		return msg;
	}

	// Interface to AsyncHttpRequest
	void sendResponseMessage(HttpResponse response) {
		if (!cancel)
		{
			StatusLine status = response.getStatusLine();
			String responseBody = null;
			try
			{
				HttpEntity entity = null;
				HttpEntity temp = response.getEntity();
				if (temp != null)
				{
					entity = new BufferedHttpEntity(temp);
					responseBody = EntityUtils.toString(entity, "UTF-8");
				}
			} catch (IOException e)
			{
				sendFailureMessage(e, (String)null);
				return;
			}

			if (status.getStatusCode() >= 300)
			{
				sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
			}
			else
			{
				sendSuccessMessage(status.getStatusCode(), responseBody);
			}
		}
	}

	public void cancel() {
		this.cancel = true;
	}
}
