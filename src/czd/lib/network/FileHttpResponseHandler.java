package czd.lib.network;

import android.os.Message;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHttpResponseHandler extends AsyncHttpResponseHandler {
	private File file;
	protected boolean append = false;
	private long current=0;

	public FileHttpResponseHandler(String filepath) {
		super();
		if(filepath.length()>0){
			file=new File(filepath);
			if(append && file.exists()){
				current=file.length();
			}
		}
	}

	public void onSuccess(File file) {

	}

	public void onSuccess(int statusCode, File file) {
		onSuccess(file);
	}

	public void onFailure(Throwable error, File file) {
		onFailure(error);
	}

	protected void sendSuccessMessage(int statusCode, File file) {
		sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] { statusCode, file }));
	}

	protected void sendFailureMessage(Throwable e, File file) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { e, file }));
	}

	protected void handleSuccessMessage(int statusCode, File file) {
		onSuccess(statusCode, file);
	}

	protected void handleFailureMessage(Throwable e, File file) {
		onFailure(e, file);
	}

	protected void handleMessage(Message msg) {
		Object[] response;
		switch (msg.what) {
		case SUCCESS_MESSAGE:
			response = (Object[]) msg.obj;
			handleSuccessMessage(((Integer) response[0]).intValue(), (File) response[1]);
			break;
		case FAILURE_MESSAGE:
			response = (Object[]) msg.obj;
			handleFailureMessage((Throwable) response[0], (File) response[1]);
			break;
		case PROGRESS_MESSAGE:
			response = (Object[]) msg.obj;
			handleProgressMessage(((Long) response[0]).longValue(), ((Long) response[1]).longValue());
			break;
		default:
			super.handleMessage(msg);
			break;
		}
	}

	void sendResponseMessage(HttpResponse response) {
		if (!cancel) {
			StatusLine status = response.getStatusLine();

			if(status.getStatusCode()==416){
				sendProgressMessage(current, current);
				sendSuccessMessage(status.getStatusCode(), file);
			}
			else if (status.getStatusCode() >= 300) {
				sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), file);
			}
			else {
				InputStream is = null;
				FileOutputStream os = null;
				try {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						if (!file.exists()) {
							file.createNewFile();
						}
						else if (!file.canWrite()) {
							sendFailureMessage(new IOException("Writing Permission Denied:"+file.getName()), file);
							return;
						}
						
						Header[] headers=response.getHeaders("Content-Range");
						if(headers.length==0){
							append=false;
							current=0;
						}
						
						os=new FileOutputStream(file, append);
						is = entity.getContent();
						long count = entity.getContentLength()+current;
						int readLen = 0;
						byte[] buffer = new byte[4096];
						while (!cancel && current < count && (readLen = is.read(buffer, 0, 4096)) > 0) {
							os.write(buffer, 0, readLen);
							current += readLen;
							sendProgressMessage(current, count);
						}
						os.flush();
						if (!cancel) {
							append=false;
							sendSuccessMessage(status.getStatusCode(), file);
						}
					}
				} catch (IOException e) {
					sendFailureMessage(e, "IOException");
				} finally {
					try {
						is.close();
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void setAppend(boolean append) {
		this.append=true;
	}
	
	public long getSkipSize(){
		return current;
	}
}
