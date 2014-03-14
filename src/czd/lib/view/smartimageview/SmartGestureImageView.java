package czd.lib.view.smartimageview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import czd.lib.R;
import czd.lib.view.gestureimageview.GestureImageView;
import czd.lib.view.progress.ProgressCircle;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 14-3-10
 * Time: 下午6:03
 */
public class SmartGestureImageView extends AbsSmartView<GestureImageView> {


	public SmartGestureImageView(Context context) {
		super(context);
	}

	public SmartGestureImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SmartGestureImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void init(AttributeSet attrs) {
		if (attrs == null)
			imageview = new GestureImageView(this.context);
		else
			imageview = new GestureImageView(this.context, attrs);
		imageview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageview.setVisibility(View.INVISIBLE);
		imageview.setRecycle(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			imageview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		this.addView(imageview);
		this.scaletype = imageview.getScaleType();

		progress = new ProgressCircle(context);
		progress.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		progress.setVisibility(View.INVISIBLE);
		progress.showText(false);
		this.addView(progress);
	}

	@Override
	protected void onFailure(SmartImageListener listener) {
		//Log.v("Pull", "task failure");
		imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageview.setImageResource(R.drawable.image);
		imageview.setVisibility(View.VISIBLE);
		imageview.setEnabled(false);
		if (listener != null)
			listener.onFailure();
	}

	public float getCurrentScale() {
		return imageview.getScale();
	}

	public void setScale(float scale) {
		imageview.handleScale(scale);
	}

}
