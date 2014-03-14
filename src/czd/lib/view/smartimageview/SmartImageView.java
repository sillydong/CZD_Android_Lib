package czd.lib.view.smartimageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import czd.lib.view.progress.ProgressCircle;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 14-3-10
 * Time: 上午9:54
 */
public class SmartImageView extends AbsSmartView<ImageView> {


	public SmartImageView(Context context) {
		super(context);
	}

	public SmartImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SmartImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void init(AttributeSet attrs) {
		if (attrs == null)
			imageview = new ImageView(context);
		else
			imageview = new ImageView(context, attrs);
		imageview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageview.setVisibility(View.INVISIBLE);
		this.addView(imageview);
		this.scaletype=imageview.getScaleType();

		progress = new ProgressCircle(context);
		progress.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		progress.setVisibility(View.INVISIBLE);
		progress.showText(false);
		this.addView(progress);
	}
}
