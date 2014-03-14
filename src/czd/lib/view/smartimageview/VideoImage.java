package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 14-3-10
 * Time: 下午9:47
 */
public class VideoImage implements SmartImage {
	private File file;

	public VideoImage(File file) {
		this.file = file;
	}

	@Override
	public void getBitmap(Context context, AbsSmartView.ViewHandler handler) {
		handler.sendEmptyMessage(AbsSmartView.MSG_START);
		try
		{
			Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
			if (bitmap != null && !bitmap.isRecycled())
				handler.sendMessage(handler.obtainMessage(AbsSmartView.MSG_SUCCESS, bitmap));
			else
				handler.sendEmptyMessage(AbsSmartView.MSG_FAILURE);
		} catch (Exception e)
		{
			handler.sendEmptyMessage(AbsSmartView.MSG_FAILURE);
		}
		handler.sendEmptyMessage(AbsSmartView.MSG_FINISH);
	}

	@Override
	public void recycle() {

	}
}
