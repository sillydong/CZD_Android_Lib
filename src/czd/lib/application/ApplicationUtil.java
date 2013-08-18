package czd.lib.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import czd.lib.io.DeviceUtil;
import czd.lib.network.AsyncHttpClient;
import czd.lib.view.smartimageview.WebImageCache;

public class ApplicationUtil extends Application{
	public static Context application_context;
	public static Resources r;
	public static String imei;
	public static WebImageCache webImageCache;
	public static AsyncHttpClient client;
	
	@Override
	public void onCreate() {
		super.onCreate();
		application_context=getApplicationContext();
		r=application_context.getResources();
		imei = DeviceUtil.getPhoneInfo(application_context).get("imei");
		webImageCache=new WebImageCache(application_context);
		client=new AsyncHttpClient();
		client.setTimeout(2000);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		Log.v("Pull","ontrim");
		webImageCache.clear(false);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.v("Pull","onlow");
		webImageCache.clear(false);
	}
}
