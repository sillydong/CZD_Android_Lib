package czd.lib.view.abslistview.pull;

import android.widget.AbsListView;

public interface AbsListPullListener {
	public void onRefresh();

	public void onScrollStop(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);

	public void onScrollStateChanged(AbsListView view, int scrollState);

	public void onTop(int firstVisibleItem, int visibleItemCount, int totalItemCount);
	
	public void outTop(int firstVisibleItem, int visibleItemCount, int totalItemCount);

	public void onBottom(int firstVisibleItem, int visibleItemCount, int totalItemCount);
	
	public void outBottom(int firstVisibleItem, int visibleItemCount, int totalItemCount);

}
