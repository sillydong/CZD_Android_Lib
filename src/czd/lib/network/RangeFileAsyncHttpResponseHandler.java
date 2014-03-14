package czd.lib.network;

import android.util.Log;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 14-3-3
 * Time: 下午10:46
 */
public abstract class RangeFileAsyncHttpResponseHandler extends FileAsyncHttpResponseHandler {
	private static final String LOG_TAG = "RangeFileAsyncHttpResponseHandler";

	private long current=0;
	private boolean append = false;

	public RangeFileAsyncHttpResponseHandler(File file) {
		super(file);
	}

	@Override
	public void sendResponseMessage(HttpResponse response) throws IOException {
		if (!Thread.currentThread().isInterrupted())
		{
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() == 416)
			{
				//already finished
				if (!Thread.currentThread().isInterrupted())
					sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), null);
			}
			else if (status.getStatusCode() >= 300)
			{
				if (!Thread.currentThread().isInterrupted())
					sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), null, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
			}
			else
			{
				if (!Thread.currentThread().isInterrupted())
				{
					Header[] headers = response.getHeaders("Content-Range");
					if (headers == null || headers.length == 0)
					{
						append = false;
						current = 0;
					}
					else
					{
						for (Header h : headers)
						{
							Log.v(LOG_TAG, h.getName() + " : " + h.getValue());
						}
					}
					sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), getResponseData(response.getEntity()));
				}
				
			}
		}
	}

	@Override
	protected byte[] getResponseData(HttpEntity entity) throws IOException {
		if (entity != null)
		{
			InputStream instream = entity.getContent();
			long contentLength = entity.getContentLength()+current;
			FileOutputStream buffer = new FileOutputStream(getTargetFile(), append);
			if (instream != null)
			{
				try
				{
					byte[] tmp = new byte[BUFFER_SIZE];
					int l;
					while (current<contentLength && (l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted())
					{
						current += l;
						buffer.write(tmp, 0, l);
						sendProgressMessage((int)current, (int)contentLength);
					}
				} finally
				{
					instream.close();
					buffer.flush();
					buffer.close();
				}
			}
		}
		return null;
	}

	public void updateRequestHeaders(HttpUriRequest uriRequest) {
		if (mFile.exists() && mFile.canWrite())
			current = mFile.length();
		if (current > 0)
		{
			append = true;
			uriRequest.setHeader("Range", "bytes=" + current + "-");
		}
	}

}
