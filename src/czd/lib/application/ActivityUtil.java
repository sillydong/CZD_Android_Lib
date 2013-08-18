package czd.lib.application;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Window;
import android.view.WindowManager;
import czd.lib.R;

public class ActivityUtil {
	public static final int ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	public static final int ORIENTATION_PROTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	public static final int ORIENTATION_SENSOR = ActivityInfo.SCREEN_ORIENTATION_SENSOR;

	public static void setFullScreen(Activity activity, boolean full) {
		hideTitleBar(activity);
		Window window = activity.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		if (full) {
			params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		}
		else {
			params.flags &= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		}
		window.setAttributes(params);
		window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	public static void hideTitleBar(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	public static void setOrientation(Activity activity, int orientation) {
		activity.setRequestedOrientation(orientation);
	}

	public static void hideInput(Activity activity) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public static void adjustInput(Activity activity) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	public static void switchActivity(Activity activity, Class<?> cls, boolean anim) {
		Intent intent = new Intent(activity, cls);
		activity.startActivity(intent);
		if (anim) {
			activity.overridePendingTransition(R.anim.shift_right_in, R.anim.shift_left_out);

		}
	}

	public static void switchActivity(Activity activity, Intent intent, boolean anim) {
		activity.startActivity(intent);
		if (anim) {
			activity.overridePendingTransition(R.anim.shift_right_in, R.anim.shift_left_out);

		}
	}

	public static void switchActivityBack(Activity activity, boolean anim) {
		activity.finish();
		if (anim) {
			activity.overridePendingTransition(R.anim.shift_left_in, R.anim.shift_right_out);
		}
	}

	public static void switchActivityResult(Activity activity, int requestCode, Intent intent, boolean anim, boolean forward) {
		activity.startActivityForResult(intent, requestCode);
		if (anim) {
			if (forward) {
				activity.overridePendingTransition(R.anim.shift_right_in, R.anim.shift_left_out);
			}
			else {
				activity.finish();
				activity.overridePendingTransition(R.anim.shift_left_in, R.anim.shift_right_out);
			}
		}
	}
}
