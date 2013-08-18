package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import czd.lib.data.ImageUtil;
import czd.lib.io.FileUtil;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebImageCache {
	private static final String DISK_CACHE_PATH = "/web_image_cache/";

	private ConcurrentHashMap<String, SoftReference<Bitmap>> memoryCache;
	private String diskCachePath;
	private boolean diskCacheEnabled = false;
	private ExecutorService writeThread;

	public WebImageCache(Context context) {
		// Set up in-memory cache store
		memoryCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

		// Set up disk cache store
		File diskCacheFile = new File(FileUtil.getCacheDirectory(false), DISK_CACHE_PATH);
		if (!diskCacheFile.exists() && diskCacheFile.getParentFile().canWrite()) {
			diskCacheFile.mkdirs();
		}
		diskCachePath = diskCacheFile.getAbsolutePath() + "/";
		diskCacheEnabled = (diskCacheFile.exists() && diskCacheFile.canWrite());
		if (diskCacheEnabled && getDiskCacheSize() > 512 * 1024 * 1024) {
			FileUtil.removeExpiredFile(diskCacheFile);
		}

		// Set up threadpool for image fetching tasks
		writeThread = Executors.newCachedThreadPool();
	}

	public Bitmap get(final String url) {
		Bitmap bitmap = null;

		// Check for image in memory
		bitmap = getBitmapFromMemory(url);

		// Check for image on disk cache
		if (bitmap == null || bitmap.isRecycled()) {
			bitmap = getBitmapFromDisk(url);

			// Write bitmap back into memory cache
			if (bitmap != null && !bitmap.isRecycled()) {
				cacheBitmapToMemory(url, bitmap);
			}
		}

		return bitmap;
	}

	public Bitmap get(final String url, int width, int height) {
		Bitmap bitmap = null;
		bitmap = getBitmapFromMemory(url);

		if (bitmap == null || bitmap.isRecycled()) {
			bitmap = getBitmapFromDisk(url, width, height);

			if (bitmap != null && !bitmap.isRecycled()) {
				cacheBitmapToMemory(url, bitmap);
			}
		}

		return bitmap;
	}

	public void put(String url, Bitmap bitmap) {
		cacheBitmapToMemory(url, bitmap);
		cacheBitmapToDisk(url, bitmap);
	}

	public void remove(String url) {
		if (url == null) {
			return;
		}

		// Remove from memory cache
		memoryCache.remove(getCacheKey(url));

		// Remove from file cache
		File f = new File(diskCachePath, getCacheKey(url));
		if (f.exists() && f.isFile()) {
			f.delete();
		}
	}

	public void clear(boolean both) {
		// Remove everything from memory cache
		memoryCache.clear();

		if (both) {
			// Remove everything from file cache
			File cachedFileDir = new File(diskCachePath);
			if (cachedFileDir.exists() && cachedFileDir.isDirectory()) {
				File[] cachedFiles = cachedFileDir.listFiles();
				for (File f : cachedFiles) {
					if (f.exists() && f.isFile()) {
						f.delete();
					}
				}
			}
		}
	}

	private void cacheBitmapToMemory(final String url, final Bitmap bitmap) {
		memoryCache.put(getCacheKey(url), new SoftReference<Bitmap>(bitmap));
	}

	private void cacheBitmapToDisk(final String url, final Bitmap bitmap) {
		writeThread.execute(new Runnable() {
			@Override
			public void run() {
				if (diskCacheEnabled) {
					BufferedOutputStream ostream = null;
					try {
						ostream = new BufferedOutputStream(new FileOutputStream(new File(diskCachePath, getCacheKey(url))), 2 * 1024);
						bitmap.compress(CompressFormat.PNG, 100, ostream);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							if (ostream != null) {
								ostream.flush();
								ostream.close();
							}
						} catch (IOException e) {
						}
					}
				}
			}
		});
	}

	private Bitmap getBitmapFromMemory(String url) {
		Bitmap bitmap = null;
		SoftReference<Bitmap> softRef = memoryCache.get(getCacheKey(url));
		if (softRef != null) {
			bitmap = softRef.get();
		}

		return bitmap;
	}

	private Bitmap getBitmapFromDisk(String url) {
		Bitmap bitmap = null;
		if (diskCacheEnabled) {
			String filePath = getFilePath(url);
			File file = new File(filePath);
			if (file.exists()) {
				final BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inPurgeable = true;
				o2.inInputShareable = true;
				BufferedInputStream bis = null;
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					bitmap = BitmapFactory.decodeStream(bis, new Rect(-1, -1, -1, -1), o2);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fis.close();
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return bitmap;
	}

	private Bitmap getBitmapFromDisk(String url, int width, int height) {
		Bitmap bitmap = null;
		if (diskCacheEnabled) {
			String filePath = getFilePath(url);
			File file = new File(filePath);
			if (file.exists()) {
				final BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inJustDecodeBounds = true;
				o2.inPurgeable = true;
				o2.inInputShareable = true;
				BitmapFactory.decodeFile(filePath, o2);
				o2.inSampleSize = ImageUtil.calculateInSampleSize(o2.outWidth, o2.outHeight, width, height);
				o2.inJustDecodeBounds = false;
				BufferedInputStream bis = null;
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					bitmap = BitmapFactory.decodeStream(bis, new Rect(-1, -1, -1, -1), o2);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fis.close();
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return bitmap;
	}

	public String getFilePath(String url) {
		return diskCachePath + getCacheKey(url);
	}

	private long getDiskCacheSize() {
		return FileUtil.getSize(new File(diskCachePath));
	}

	private String getCacheKey(String url) {
		if (url == null) {
			throw new RuntimeException("Null url passed in");
		}
		else {
			//return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
			//return MD5.encode(url.getBytes());
			return String.valueOf(url.hashCode());
		}
	}
}
