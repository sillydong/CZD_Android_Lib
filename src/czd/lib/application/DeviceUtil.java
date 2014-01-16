package czd.lib.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import czd.lib.view.ToastUtil;

import java.util.HashMap;

public class DeviceUtil {
	public static boolean networkStatus() {
		ConnectivityManager manager = (ConnectivityManager)ApplicationUtil.application_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == State.CONNECTED || mobile == State.CONNECTED)
			return true;
		else
			return false;
	}

	public static boolean isWIFIConnected() {
		ConnectivityManager manager = (ConnectivityManager)ApplicationUtil.application_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == State.CONNECTED)
			return true;
		else
			return false;
	}

	public static boolean isWIFIActive() {
		return ((WifiManager)ApplicationUtil.application_context.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
	}

	public static void setWIFIActive(boolean active) {
		((WifiManager)ApplicationUtil.application_context.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(active);
	}

	public static HashMap<String,String> getPhoneInfo(Context context) {
		HashMap<String,String> info = new HashMap<String,String>();
		TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		info.put("number", manager.getLine1Number());
		String imsi = manager.getSubscriberId();
		if (imsi != null)
		{
			if (imsi.startsWith("46000") || imsi.startsWith("46002"))
				info.put("provider", "移动");
			else if (imsi.startsWith("46001"))
				info.put("provider", "联通");
			else if (imsi.startsWith("46003"))
				info.put("provider", "电信");
			else
				info.put("provider", "其他");
		}
		else
			info.put("provider", null);
		info.put("imei", manager.getDeviceId());
		info.put("version", manager.getDeviceSoftwareVersion());
		return info;
	}

	public static String getDeviceBrief() {
		return Build.BRAND + "|" + Build.MODEL + "|" + Build.DEVICE + "|" + Build.VERSION.RELEASE + "|" + Build.VERSION.SDK_INT;
	}

	public static void call(Activity activity, String number, boolean alert) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(String.format("tel:%s", number)));
		try
		{
			activity.startActivity(intent);
		} catch (Exception e)
		{
			e.printStackTrace();
			if (alert)
				ToastUtil.showToast(activity, "由于安全软件限制，无法拨打电话");
		}
	}

	public static void sms(Activity activity, String number, String content, boolean alert) {
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms://"));
		intent.putExtra("address", number);
		intent.putExtra("sms_body", content);
		try
		{
			activity.startActivity(intent);
		} catch (Exception e)
		{
			e.printStackTrace();
			if (alert)
				ToastUtil.showToast(activity, "由于安全软件限制，无法发送短信");
		}
	}

	public static DisplayMetrics getScreenSize() {
		DisplayMetrics disp = new DisplayMetrics();
		((WindowManager)ApplicationUtil.application_context.getSystemService("window")).getDefaultDisplay().getMetrics(disp);
		return disp;
	}

	public static void keepScreenOn(Activity activity) {
		Window window = activity.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

}
