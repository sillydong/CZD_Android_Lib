package czd.lib.view.absinterface;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-28
 * Time: 上午11:25
 */
public abstract interface RecyclableItem {
	public abstract void recycle();

	public abstract void load();

	public abstract boolean isLoaded();

	public abstract Object getData();

	public abstract void setData();
}
