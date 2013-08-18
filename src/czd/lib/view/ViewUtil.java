package czd.lib.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

public class ViewUtil {
	public static View viewById(Activity activity, int id) {
		return activity.getLayoutInflater().inflate(id, null);
	}

	public static View viewById(Context context, int id) {
		return LayoutInflater.from(context).inflate(id, null);
	}

	public static int dpToPx(Context context, float px) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
	}

	public static int pxToDp(Context context, float dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, context.getResources().getDisplayMetrics());
	}

	public static void fixBackgroundRepeat(View view) {
		if (view != null) {
			BitmapDrawable background = (BitmapDrawable) view.getBackground();
			if (background != null) {
				background.mutate();
				background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			}
			view.requestLayout();
		}
	}

}
