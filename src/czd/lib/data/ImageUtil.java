package czd.lib.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import czd.lib.application.ApplicationUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtil {
	public static final int CLIP_ALL = 0;
	public static final int CLIP_LEFT = 1;
	public static final int CLIP_RIGHT = 2;
	public static final int CLIP_TOP = 3;
	public static final int CLIP_BOTTOM = 4;

	public static Bitmap getBitmapFromUrl(String url) {
		Bitmap bitmap = null;

		try
		{
			HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
			conn.setConnectTimeout(2000);
			conn.setUseCaches(true);
			conn.setInstanceFollowRedirects(true);
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inInputShareable = true;
			o2.inPurgeable = true;
			bitmap = BitmapFactory.decodeStream(new BufferedInputStream((InputStream)conn.getContent()), new Rect(-1, -1, -1, -1), o2);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap getBitmapFromFile(File file) {
		final BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inPurgeable = true;
		o2.inInputShareable = true;
		BufferedInputStream bis = null;
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			return BitmapFactory.decodeStream(bis, new Rect(-1, -1, -1, -1), o2);
		} catch (OutOfMemoryError e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (fis != null)
					fis.close();
				if (bis != null)
					bis.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap getBitmapFromFile(File file, int width, int height) {
		final BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inJustDecodeBounds = true;
		o2.inPurgeable = true;
		o2.inInputShareable = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), o2);
		o2.inSampleSize = ImageUtil.calculateInSampleSize(o2.outWidth, o2.outHeight, width, height);
		o2.inJustDecodeBounds = false;
		BufferedInputStream bis = null;
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			return BitmapFactory.decodeStream(bis, new Rect(-1, -1, -1, -1), o2);
		} catch (OutOfMemoryError e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (fis != null)
					fis.close();
				if (bis != null)
					bis.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap getBitmapFromAsset(String filename) {
		InputStream is = null;
		try
		{
			BitmapFactory.Options bo = new BitmapFactory.Options();
			bo.inScaled = false;
			bo.inDensity = 0;
			bo.inPurgeable = true;
			bo.inInputShareable = true;
			is = ApplicationUtil.application_context.getAssets().open(filename);
			return BitmapFactory.decodeStream(is, null, bo);
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				is.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean saveBitmapToFile(File file, Bitmap bitmap) {
		BufferedOutputStream ostream = null;
		try
		{
			ostream = new BufferedOutputStream(new FileOutputStream(file), 2 * 1024);
			return bitmap.compress(CompressFormat.PNG, 100, ostream);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (ostream != null)
				{
					ostream.flush();
					ostream.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	@SuppressWarnings("deprecation")
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	public static String getMediaPath(Context context, Uri uri) {
		String[] projection = {MediaColumns.DATA};
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static Bitmap clipBitmap(int type, Bitmap bitmap, int roundPx) {
		try
		{
			final int width = bitmap.getWidth();
			final int height = bitmap.getHeight();

			Bitmap paintingBoard = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(paintingBoard);
			canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

			final Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);

			switch (type)
			{
				case CLIP_ALL:
					clipAll(canvas, paint, roundPx, width, height);
					break;
				case CLIP_LEFT:
					clipLeft(canvas, paint, roundPx, width, height);
					break;
				case CLIP_RIGHT:
					clipRight(canvas, paint, roundPx, width, height);
					break;
				case CLIP_TOP:
					clipTop(canvas, paint, roundPx, width, height);
					break;
				case CLIP_BOTTOM:
					clipBottom(canvas, paint, roundPx, width, height);
					break;
				default:
					break;
			}
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			final Rect src = new Rect(0, 0, width, height);
			final Rect dst = src;
			canvas.drawBitmap(bitmap, src, dst, paint);
			return paintingBoard;
		} catch (Exception exp)
		{
			return bitmap;
		}
	}

	private static void clipLeft(final Canvas canvas, final Paint paint, int offset, int width, int height) {
		final Rect block = new Rect(offset, 0, width, height);
		canvas.drawRect(block, paint);
		final RectF rectF = new RectF(0, 0, offset * 2, height);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}

	private static void clipRight(final Canvas canvas, final Paint paint, int offset, int width, int height) {
		final Rect block = new Rect(0, 0, width - offset, height);
		canvas.drawRect(block, paint);
		final RectF rectF = new RectF(width - offset * 2, 0, width, height);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}

	private static void clipTop(final Canvas canvas, final Paint paint, int offset, int width, int height) {
		final Rect block = new Rect(0, offset, width, height);
		canvas.drawRect(block, paint);
		final RectF rectF = new RectF(0, 0, width, offset * 2);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}

	private static void clipBottom(final Canvas canvas, final Paint paint, int offset, int width, int height) {
		final Rect block = new Rect(0, 0, width, height - offset);
		canvas.drawRect(block, paint);
		final RectF rectF = new RectF(0, height - offset * 2, width, height);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}

	private static void clipAll(final Canvas canvas, final Paint paint, int offset, int width, int height) {
		final RectF rectF = new RectF(0, 0, width, height);
		canvas.drawRoundRect(rectF, offset, offset, paint);
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height, int degree, boolean cut) {
		if (width <= 0 && height <= 0)
		{
			//只旋转
			if (degree > 0)
			{
				return rotate(bitmap, degree);
			}
			else
			{
				return bitmap;
			}
		}
		int src_w = bitmap.getWidth();
		int src_h = bitmap.getHeight();

		if (src_w <= width || src_h <= height)
		{
			//放大
			if (degree > 0)
			{
				return rotate(bitmap, degree);
			}
			else
			{
				return bitmap;
			}
		}

		int start_x = 0;
		int start_y = 0;
		float scale = 0f;

		if (width > 0 && height <= 0)
		{
			//限宽，不考虑scale和cut
			scale = (float)width / (float)src_w;
		}
		else if (width <= 0 && height > 0)
		{
			//限高，不考虑scale和cut
			scale = (float)height / (float)src_h;
		}
		else
		{
			//双限，考虑scale和cut
			float scale_w = (float)width / (float)src_w;
			float scale_h = (float)height / (float)src_h;
			if (cut)
			{
				scale = Math.max(scale_w, scale_h);
				start_x = (int)Math.round(Math.abs(src_w - width / scale) / 2);
				start_y = (int)Math.round(Math.abs(src_h - height / scale) / 2);
				start_x = Math.max(0, start_x);
				start_y = Math.max(0, start_y);
			}
			else
			{
				scale = Math.min(scale_w, scale_h);
			}
		}
		if (degree == 0 && start_x == 0 && start_y == 0)
		{
			Bitmap b2 = Bitmap.createScaledBitmap(bitmap, (int)(scale * src_w), (int)(scale * src_h), true);
			if (bitmap != b2)
			{
				bitmap.recycle();
			}
			return b2;
		}
		else
		{
			Matrix matrix = new Matrix();
			matrix.setScale(scale, scale);

			matrix.postRotate(degree, scale * src_w / 2, scale * src_h / 2);
			Bitmap b2 = Bitmap.createBitmap(bitmap, start_x, start_y, src_w, src_h, matrix, true);
			if (bitmap != b2)
			{
				bitmap.recycle();
			}
			return b2;
		}
	}

	public static final Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		int width = maxSize;
		int height = maxSize;
		boolean needsResize = false;
		if (srcWidth > srcHeight)
		{
			if (srcWidth > maxSize)
			{
				needsResize = true;
				height = ((maxSize * srcHeight) / srcWidth);
			}
		}
		else
		{
			if (srcHeight > maxSize)
			{
				needsResize = true;
				width = ((maxSize * srcWidth) / srcHeight);
			}
		}
		if (needsResize)
		{
			Bitmap retVal = Bitmap.createScaledBitmap(bitmap, width, height, true);
			return retVal;
		}
		else
		{
			return bitmap;
		}
	}

	public static Bitmap rotate(Bitmap bitmap, float degree) {
		if (degree != 0 && bitmap != null)
		{
			Matrix m = new Matrix();
			m.setRotate(degree, (float)bitmap.getWidth() / 2, (float)bitmap.getHeight() / 2);
			try
			{
				Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != b2)
				{
					bitmap.recycle();
					bitmap = b2;
				}
			} catch (OutOfMemoryError ex)
			{
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return bitmap;
	}

	public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, boolean scaleUp) {
		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0))
		{
			/*
			 * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, source.getWidth()), deltaYHalf + Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
			c.drawBitmap(source, src, dst, null);
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float)targetWidth / targetHeight;

		if (bitmapAspect > viewAspect)
		{
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F)
			{
				scaler.setScale(scale, scale);
			}
			else
			{
				scaler = null;
			}
		}
		else
		{
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F)
			{
				scaler.setScale(scale, scale);
			}
			else
			{
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null)
		{
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
		}
		else
		{
			b1 = source;
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

		if (b1 != source)
		{
			b1.recycle();
		}

		return b2;
	}

	public static Bitmap extractMiniThumb(Bitmap source, int width, int height, boolean recycle) {
		if (source == null)
		{
			return null;
		}

		float scale;
		if (source.getWidth() < source.getHeight())
		{
			scale = width / (float)source.getWidth();
		}
		else
		{
			scale = height / (float)source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap miniThumbnail = transform(matrix, source, width, height, false);

		if (recycle && miniThumbnail != source)
		{
			source.recycle();
		}
		return miniThumbnail;
	}

	public static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth)
		{
			if (width > height)
			{
				inSampleSize = Math.round((float)height / (float)reqHeight);
			}
			else
			{
				inSampleSize = Math.round((float)width / (float)reqWidth);
			}

			final float totalPixels = width * height;

			final float totalReqPixelsCap = reqWidth * reqHeight;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap)
			{
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

}
