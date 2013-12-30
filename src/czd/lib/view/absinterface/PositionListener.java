package czd.lib.view.absinterface;

import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-28
 * Time: 上午11:25
 */
public abstract interface PositionListener {
	public abstract void onScroll(ViewGroup view, int l, int t, int oldl, int oldt);

	public abstract void onTop(int t);

	public abstract void outTop(int t);

	public abstract void onBottom(int t);

	public abstract void outBottom(int t);

	public abstract void onStop(int position);
}
