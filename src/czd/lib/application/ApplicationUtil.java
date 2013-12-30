package czd.lib.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import czd.lib.network.AsyncHttpClient;

public class ApplicationUtil extends Application {
	public static Context application_context;
	public static Resources r;
	public static String imei;
	public static AsyncHttpClient client;

	@Override
	public void onCreate() {
		super.onCreate();
		application_context = getApplicationContext();
		r = application_context.getResources();
		imei = DeviceUtil.getPhoneInfo(application_context).get("imei");
		client = new AsyncHttpClient();
		client.setTimeout(2000);
	}
}
