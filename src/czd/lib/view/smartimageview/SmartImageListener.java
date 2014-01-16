package czd.lib.view.smartimageview;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 14-1-5
 * Time: 下午9:29
 */
public abstract interface SmartImageListener {
	public abstract void onStart();

	public abstract void onSuccess();

	public abstract void onFailure();

	public abstract void onProgress(long current, long total);
}
