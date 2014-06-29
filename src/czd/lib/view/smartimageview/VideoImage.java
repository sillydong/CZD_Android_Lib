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
	public Bitmap getBitmap(Context context, SmartImageTask.OnCompleteHandler handler) {
		return ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
	}

	@Override
	public void cancel() {
	}
}
