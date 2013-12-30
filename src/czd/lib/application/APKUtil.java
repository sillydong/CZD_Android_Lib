package czd.lib.application;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import czd.lib.data.PreferenceUtil;

import java.io.File;

public class APKUtil {
	public static boolean exist(String package_name) {
		if (package_name.length() > 0)
		{
			try
			{
				ApplicationInfo info = ApplicationUtil.application_context.getPackageManager().getApplicationInfo(package_name, PackageManager.GET_UNINSTALLED_PACKAGES);
				return info != null;
			} catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void install(Activity activity, File apk_file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apk_file), "application/vnd.android.package-archive");
		activity.startActivity(intent);
	}

	public static void launch(Activity activity, String package_name) throws Exception {
		Intent intent = activity.getPackageManager().getLaunchIntentForPackage(package_name);
		if (intent != null)
		{
			activity.startActivity(intent);
		}
		else
		{
			throw new Exception("Package Not Found");
		}
	}

	public static boolean isFirstLaunch(Activity activity) {
		try
		{
			PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			int currentVersion = info.versionCode;
			int lastVersion = PreferenceUtil.getIntPreference(activity, "version_record", "last_version");
			if (currentVersion > lastVersion)
			{
				PreferenceUtil.writeIntPreference(activity, "version_record", "last_version", currentVersion);
				return true;
			}
		} catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
