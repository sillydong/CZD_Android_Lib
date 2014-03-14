package czd.lib.view.smartimageview;

import android.content.Context;
import android.util.AttributeSet;
import czd.lib.view.gif.GifView;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 14-3-11
 * Time: 下午2:29
 */
public class SmartGifImageView extends AbsSmartView<GifView>{
	public SmartGifImageView(Context context) {
		super(context);
	}

	public SmartGifImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SmartGifImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void init(AttributeSet attrs) {
		
	}
}
