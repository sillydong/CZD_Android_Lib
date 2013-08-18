package czd.lib.view.abslistview.stable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class StableGridView extends GridView {

	public StableGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StableGridView(Context context) {
		super(context);
	}

	public StableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expendSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expendSpec);
	}

}
