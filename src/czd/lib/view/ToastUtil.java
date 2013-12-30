package czd.lib.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import czd.lib.R;
import czd.lib.application.DeviceUtil;

public class ToastUtil {
	public static void showToast(Context context, int string_id) {
		showToast(context, string_id, Toast.LENGTH_SHORT, 0);
	}

	public static void showToast(Context context, int string_id, int image_id) {
		showToast(context, string_id, Toast.LENGTH_SHORT, image_id);
	}

	public static void showToast(Context context, int string_id, int duration, int image_id) {
		showToast(context, context.getString(string_id), duration, image_id);
	}

	public static void showToast(Context context, String content) {
		if (content.length() > 0)
		{
			showToast(context, content, Toast.LENGTH_SHORT, 0);
		}
	}

	public static void showToast(Context context, String content, int image_id) {
		if (content.length() > 0)
		{
			showToast(context, content, Toast.LENGTH_SHORT, image_id);
		}
	}

	public static void showToast(Context context, String content, int duration, int image_id) {
		if ((content != null) && (!content.equalsIgnoreCase("")))
		{
			View localView = LayoutInflater.from(context).inflate(R.layout.common_toast_layout, null);
			((TextView)localView.findViewById(R.id.common_toast_textview)).setText(content);
			if (image_id != 0)
			{
				((ImageView)localView.findViewById(R.id.common_toast_imageview)).setImageResource(image_id);
				((ImageView)localView.findViewById(R.id.common_toast_imageview)).setVisibility(View.VISIBLE);
			}
			Toast localToast = new Toast(context);
			localToast.setGravity(Gravity.CENTER, 0, DeviceUtil.getScreenSize().heightPixels / 6);
			localToast.setView(localView);
			localToast.setDuration(duration);
			localToast.show();
		}
	}
}
