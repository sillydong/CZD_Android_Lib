package czd.lib.view.waterfall;

import android.view.ViewGroup;

public abstract interface WaterfallScrollListener {
	public void onRefresh();

	public abstract void onScroll(ViewGroup view, int l, int t, int oldl, int oldt);

	public abstract void onTop(int t);

	public abstract void outTop(int t);

	public abstract void onBottom(int t);

	public abstract void outBottom(int t);

	public abstract void onStop(int position);

}
