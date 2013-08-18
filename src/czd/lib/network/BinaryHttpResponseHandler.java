package czd.lib.network;

import android.os.Message;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class BinaryHttpResponseHandler extends AsyncHttpResponseHandler {
	// Allow images by default
	private static String[] mAllowedContentTypes = new String[] { "image/jpeg", "image/png" };

	public BinaryHttpResponseHandler() {
		super();
	}

	public BinaryHttpResponseHandler(String[] allowedContentTypes) {
		this();
		mAllowedContentTypes = allowedContentTypes;
	}

	public void onSuccess(byte[] binaryData) {
	}

	public void onSuccess(int statusCode, byte[] binaryData) {
		onSuccess(binaryData);
	}

	public void onFailure(Throwable error, byte[] binaryData) {
		// By default, call the deprecated onFailure(Throwable) for compatibility
		onFailure(error);
	}

	protected void sendSuccessMessage(int statusCode, byte[] responseBody) {
		sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] { statusCode, responseBody }));
	}

	protected void sendFailureMessage(Throwable e, byte[] responseBody) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { e, responseBody }));
	}

	protected void handleSuccessMessage(int statusCode, byte[] responseBody) {
		onSuccess(statusCode, responseBody);
	}

	protected void handleFailureMessage(Throwable e, byte[] responseBody) {
		onFailure(e, responseBody);
	}

	// Methods which emulate android's Handler and Message methods
	protected void handleMessage(Message msg) {
		Object[] response;
		switch (msg.what) {
		case SUCCESS_MESSAGE:
			response = (Object[]) msg.obj;
			handleSuccessMessage(((Integer) response[0]).intValue(), (byte[]) response[1]);
			break;
		case FAILURE_MESSAGE:
			response = (Object[]) msg.obj;
			handleFailureMessage((Throwable) response[0], (byte[]) response[1]);
			break;
		default:
			super.handleMessage(msg);
			break;
		}
	}

	// Interface to AsyncHttpRequest
	void sendResponseMessage(HttpResponse response) {
		if(!cancel){
			StatusLine status = response.getStatusLine();
			Header[] contentTypeHeaders = response.getHeaders("Content-Type");
			byte[] responseBody = null;
			if (contentTypeHeaders.length != 1) {
				//malformed/ambiguous HTTP Header, ABORT!
				sendFailureMessage(new HttpResponseException(status.getStatusCode(), "None, or more than one, Content-Type Header found!"), responseBody);
				return;
			}
			Header contentTypeHeader = contentTypeHeaders[0];
			boolean foundAllowedContentType = false;
			for (String anAllowedContentType : mAllowedContentTypes) {
				if (anAllowedContentType.equals(contentTypeHeader.getValue())) {
					foundAllowedContentType = true;
				}
			}
			if (!foundAllowedContentType) {
				//Content-Type not in allowed list, ABORT!
				sendFailureMessage(new HttpResponseException(status.getStatusCode(), "Content-Type not allowed!"), responseBody);
				return;
			}
			try {
				HttpEntity entity = null;
				HttpEntity temp = response.getEntity();
				if (temp != null) {
					entity = new BufferedHttpEntity(temp);
				}
				responseBody = EntityUtils.toByteArray(entity);
			} catch (IOException e) {
				sendFailureMessage(e, (byte[]) null);
				return;
			}

			if (status.getStatusCode() >= 300) {
				sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
			}
			else {
				sendSuccessMessage(status.getStatusCode(), responseBody);
			}
		}
	}
}
