package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import czd.lib.application.ApplicationUtil;
import czd.lib.cache.BitmapCache;
import czd.lib.data.ImageUtil;
import czd.lib.network.BinaryHttpResponseHandler;
import czd.lib.network.PersistentCookieStore;
import czd.lib.network.RangeFileAsyncHttpResponseHandler;
import czd.lib.network.SyncHttpClient;
import org.apache.http.Header;

import java.io.File;

//import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 14-3-10
 * Time: 上午9:55
 */
public class WebImage implements SmartImage {
	private String url;
	private boolean usecache = true;
	private Bitmap image;

	protected static SyncHttpClient client;

	public WebImage(String url) {
		this.url = url.replaceAll(" ","%20");
		initClient();
	}

	public WebImage(String url, boolean usecache) {
		this.url = url.replaceAll(" ", "%20");
		this.usecache = usecache;
		initClient();
	}

	private void initClient() {
		if (client == null)
		{
			client = new SyncHttpClient();
			client.setTimeout(2000);
			client.setCookieStore(new PersistentCookieStore(ApplicationUtil.application_context));
			client.setMaxRetriesAndTimeout(2, 2000);
			if(!AbsSmartView.useragent.equals(""))
				client.setUserAgent(AbsSmartView.useragent);
			client.setEnableRedirects(true);
		}
	}

	@Override
	public void getBitmap(Context context, final AbsSmartView.ViewHandler handler) {
		if (client != null && (image == null || image.isRecycled()))
		{
			if (usecache)
			{
				image = BitmapCache.getInstance().get(url);
				if (image == null || image.isRecycled())
				{
					client.get(context, url, new RangeFileAsyncHttpResponseHandler(new File(BitmapCache.getInstance().getRealName(url))) {
						@Override
						public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
							//Log.v("Pull", "image file failure");
							if (!Thread.currentThread().isInterrupted() && handler != null)
								handler.sendEmptyMessage(SmartImageView.MSG_FAILURE);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers, File file) {
							//Log.v("Pull", "image file success");
							if (!Thread.currentThread().isInterrupted())
							{
								image = ImageUtil.getBitmapFromFile(file);
								if (image != null && !image.isRecycled())
								{
									if (handler != null)
										handler.sendMessage(handler.obtainMessage(SmartImageView.MSG_SUCCESS, image));
								}
								else
								{
									if (handler != null)
										handler.sendEmptyMessage(SmartImageView.MSG_FAILURE);
								}
							}
						}

						@Override
						public void onStart() {
							super.onStart();
							//Log.v("Pull", "image file start");
							if (!Thread.currentThread().isInterrupted() && handler != null)
								handler.sendEmptyMessage(SmartImageView.MSG_START);
						}

						@Override
						public void onFinish() {
							super.onFinish();
							//Log.v("Pull", "image file finish");
							if (!Thread.currentThread().isInterrupted() && handler != null)
								handler.sendEmptyMessage(SmartImageView.MSG_FINISH);
						}

						@Override
						public void onProgress(int bytesWritten, int totalSize) {
							super.onProgress(bytesWritten, totalSize);
							//Log.v("Pull", "image file progress");
							if (!Thread.currentThread().isInterrupted() && handler != null)
								handler.sendMessage(handler.obtainMessage(SmartImageView.MSG_PROGRESS, bytesWritten, totalSize));
						}
					});
				}
				else
				{
					if (!Thread.currentThread().isInterrupted() && handler != null)
					{
						//Log.v("Pull", "use cache");
						handler.sendEmptyMessage(SmartImageView.MSG_START);
						handler.sendMessage(handler.obtainMessage(SmartImageView.MSG_SUCCESS, image));
						handler.sendEmptyMessage(SmartImageView.MSG_FINISH);
					}
				}
			}
			else
			{
				client.get(context, url, new BinaryHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
						//Log.v("Pull", "image binary success");
						if (!Thread.currentThread().isInterrupted())
						{
							BitmapFactory.Options o = new BitmapFactory.Options();
							o.inPurgeable = true;
							o.inInputShareable = true;
							image = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length, o);
							if (image != null && !image.isRecycled())
							{
								if (handler != null)
									handler.sendMessage(handler.obtainMessage(SmartImageView.MSG_SUCCESS, image));
							}
							else
							{
								if (handler != null)
									handler.sendEmptyMessage(SmartImageView.MSG_FAILURE);
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
						//Log.v("Pull", "image binary failure");
						if (!Thread.currentThread().isInterrupted() && handler != null)
							handler.sendEmptyMessage(SmartImageView.MSG_FAILURE);
					}

					@Override
					public void onStart() {
						super.onStart();
						//Log.v("Pull", "image binary start");
						if (!Thread.currentThread().isInterrupted() && handler != null)
							handler.sendEmptyMessage(SmartImageView.MSG_START);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						//Log.v("Pull", "image binary finish");
						if (!Thread.currentThread().isInterrupted() && handler != null)
							handler.sendEmptyMessage(SmartImageView.MSG_FINISH);
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						super.onProgress(bytesWritten, totalSize);
						//Log.v("Pull", "image binary progress");
						if (!Thread.currentThread().isInterrupted() && handler != null)
							handler.sendMessage(handler.obtainMessage(SmartImageView.MSG_PROGRESS, bytesWritten, totalSize));
					}
				});
			}
		}
	}

	@Override
	public void recycle() {
		//Log.v("Pull", "image recycle");
		if (this.image != null && !this.image.isRecycled())
			this.image.recycle();
		this.image = null;
	}

	@Override
	public String toString() {
		return url;
	}
}
