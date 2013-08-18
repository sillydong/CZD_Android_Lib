package czd.lib.io;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import czd.lib.application.ApplicationUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;

/**
 * 
 * @author Chen.Zhidong
 *         2011-12-26
 * 
 */
public class FileUtil {

	public static final long EXPIRE_DATES = 3;
	public static final int MB = 1024;

	/**
	 * return available space in SD card
	 * 
	 * @return int (MB)
	 */
	public static int getSDcardSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		double sdfree = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
		return (int) sdfree;
	}

	public static boolean sdcardWritable() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && Environment.getExternalStorageDirectory().canWrite()) {
			return true;
		}
		return false;
	}

	public static long getSize(File path) {
		long size = 0L;
		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for (File file : files) {
				size += getSize(file);
			}
		}
		else {
			try {
				FileInputStream fis = new FileInputStream(path);
				size += fis.available();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return size;
	}

	/**
	 * change the file's last modify time
	 * 
	 * @param filepath
	 */
	public static void setFileLastModifyTime(File file) {
		if (file.exists() && file.canWrite()) {
			long newtime = System.currentTimeMillis();
			file.setLastModified(newtime);
		}
	}

	/**
	 * remove expired files in a directory
	 * 
	 * @param filedir
	 *            directory to clean
	 */
	public static void removeExpiredFile(File filedir) {

		File[] files = filedir.listFiles();
		if (files == null) {
			return;
		}
		else {
			for (File file : files) {
				if (file.isDirectory()) {
					removeExpiredFile(file);
				}
				else if (file.isFile()) {
					if ((System.currentTimeMillis() - file.lastModified()) / (24 * 60 * 60 * 1000) > EXPIRE_DATES) {
						file.delete();
					}
				}
			}
		}
	}

	public static File getCacheDirectory(boolean forceinternal) {
		File cachedir;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1 && !forceinternal && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cachedir = ApplicationUtil.application_context.getExternalCacheDir();
		}
		else {
			cachedir = ApplicationUtil.application_context.getCacheDir();
		}
		if (!cachedir.exists() && cachedir.getParentFile().canWrite()) {
			cachedir.mkdirs();
		}
		return cachedir;
	}

	public static boolean copyFile(File source, File target) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel fc2 = null;
		FileChannel fc = null;
		MappedByteBuffer mbb = null;
		try {
			if (source.isFile() && source.canRead()) {
				if (target.exists() && target.length() == source.length() && target.hashCode() == source.hashCode()) {
					return true;
				}
				else {
					synchronized (target) {
						target.createNewFile();
						File parent = target.getParentFile();
						if (parent.canWrite()) {
							fis = new FileInputStream(source);
							fos = new FileOutputStream(target);
							fc = fis.getChannel();
							mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, source.length());
							fc2 = fos.getChannel();
							fc2.write(mbb);
							return true;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
				if (fc != null)
					fc.close();
				if (fc2 != null)
					fc2.close();
				if (mbb != null)
					mbb.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void copyFile(String source, String target) {
		copyFile(new File(source), new File(target));
	}

	public static void deleteFile(String file) {
		deleteFile(new File(file));
	}

	public static void deleteFile(File file) {
		if (file != null) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files == null) {
					file.delete();
				}
				else {
					for (File tmp_file : files) {
						deleteFile(tmp_file);
					}
				}
			}
			file.delete();
		}
	}

	public static boolean createDirectory(File path) {
		return path.mkdirs() || path.isDirectory();
	}

	public static void writeFile(File target, byte[] data) {
		if (target.exists()) {
			deleteFile(target);
		}
		try {
			FileOutputStream fos = new FileOutputStream(target);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] readFile(File source) {
		if (source.exists()) {
			try {
				FileInputStream fis = new FileInputStream(source);
				byte[] data = new byte[fis.available()];
				fis.read(data);
				fis.close();
				return data;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getMIMEType(File file) {
		String type = "";
		String fileName = file.getName();
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(Locale.SIMPLIFIED_CHINESE);
		if (ext.equals("m4a") || ext.equals("mp3") || ext.equals("mid") || ext.equals("xmf") || ext.equals("ogg") || ext.equals("wav")) {
			type = "audio";
		}
		else if (ext.equals("3gp") || ext.equals("mp4")) {
			type = "video";
		}
		else if (ext.equals("jpg") || ext.equals("gif") || ext.equals("png") || ext.equals("jpeg") || ext.equals("bmp")) {
			type = "image";
		}
		else if (ext.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		}
		else {
			type = "*";
		}
		return type;
	}
}
