package czd.lib.view.scrollview.observable;

public abstract interface ObservableScrollListener {
	public abstract void onScroll(ObservableScrollView view, int l, int t, int oldl, int oldt);

	public abstract void onTop(int t);

	public abstract void outTop(int t);

	public abstract void onBottom(int t);

	public abstract void outBottom(int t);

	public abstract void onStop(int position);
}
