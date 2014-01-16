package czd.lib.view.waterfall;

import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;

public abstract interface WaterfallItem {

	int m_un = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	int m_mo = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
	int m_ex = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);

	public abstract int getItemHeight();

	public abstract void setOnClickListener(OnClickListener listener);

	public abstract void setLayoutParams();

	public abstract void recycle();

	public abstract void load();

	public abstract boolean isLoaded();

	public abstract Object getData();

}
