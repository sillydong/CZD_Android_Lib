package czd.lib.view.abslistview.stable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class StableListView extends ListView {

	public StableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StableListView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expendSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expendSpec);
	}
}
