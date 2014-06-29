package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Project: oasap
 * User: chenzhidong
 * Date: 14-6-29
 * Time: 16:58
 */
public class CaptureImage implements SmartImage{
	private String url;
	private String cookie="";
	private boolean cancel=false;

	public CaptureImage(String url) {
		this.url = url.replaceAll(" ", "%20");
	}
	
	@Override
	public Bitmap getBitmap(Context context, SmartImageTask.OnCompleteHandler handler) {
		return getImageFromUrl(this.url,handler);
	}
	
	private Bitmap getImageFromUrl(String url,SmartImageTask.OnCompleteHandler handler){
		InputStream is = null;
		try
		{
			HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
			conn.setConnectTimeout(3000);
			conn.setUseCaches(true);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Cookie",cookie);
			int code=conn.getResponseCode();
			cookie=conn.getHeaderField("Set-Cookie");
			if(code>300)
				return null;
			else
			{
				is=conn.getInputStream();
				BitmapFactory.Options o=new BitmapFactory.Options();
				o.inInputShareable=true;
				o.inPurgeable=true;
				return BitmapFactory.decodeStream(is,new Rect(0,0,0,0),o);
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try{
				if(is!=null)
					is.close();
			}catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void cancel() {
		cancel=true;
		Thread.currentThread().interrupt();
	}
}
