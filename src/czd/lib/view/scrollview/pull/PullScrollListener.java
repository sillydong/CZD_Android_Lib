package czd.lib.view.scrollview.pull;

import android.widget.ScrollView;

public interface PullScrollListener {
	public void onRefresh();

	public void onScroll(ScrollView view, int l, int t, int oldl, int oldt);

	public void onTop(int t);

	public void outTop(int t);

	public void onBottom(int t);

	public void outBottom(int t);

	public void onStop(int position);
}
