package czd.lib.view.smartimageview;

import android.content.Context;

public interface SmartImage {
	public void getBitmap(Context context, AbsSmartView.ViewHandler handler);

	public void recycle();

	public String toString();
}
