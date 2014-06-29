package czd.lib.cache;

import android.graphics.Bitmap;
import czd.lib.data.ImageUtil;
import czd.lib.encode.MD5;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-30
 * Time: 上午11:55
 */
public class BitmapCache extends AbsFileCache<Bitmap> implements CacheI<Bitmap> {
	protected static BitmapCache instance;
	protected ConcurrentHashMap<String,SoftReference<Bitmap>> mem;

	public BitmapCache() {
		super();
		mem = new ConcurrentHashMap<String,SoftReference<Bitmap>>();
		this.name = "bitmap";
	}

	public static BitmapCache getInstance() {
		if (instance == null)
			instance = new BitmapCache();
		return instance;
	}

	@Override
	public boolean save(String key, final Bitmap value) {
		mem.put(this.name + genKey(key), new SoftReference<Bitmap>((Bitmap)value));
		cleanOld();
		final File file = genFile(key);
		writer.execute(new Runnable() {
			@Override
			public void run() {
				if (file.exists() && file.isFile())
					file.delete();
				ImageUtil.saveBitmapToFile(file, value);
			}
		});

		return true;
	}

	@Override
	public long gettime(String key) {
		return 0;
	}

	@Override
	public boolean exists(String key) {
		if (mem.containsKey(this.name + genKey(key)))
			return true;
		return super.exists(key);
	}

	@Override
	public Bitmap get(String key) {
		SoftReference<Bitmap> softbit = mem.get(this.name + genKey(key));
		if (softbit != null)
			return softbit.get();
		File file = genFile(key);
		if (file.exists() && file.isFile() && file.canRead())
		{
			try
			{
				return ImageUtil.getBitmapFromFile(file,2);
			} catch (OutOfMemoryError e)
			{
				return null;
			}

		}
		return null;
	}

	@Override
	public boolean delete(String key) {
		mem.remove(this.name + genKey(key));
		return super.delete(key);
	}

	@Override
	public void clean() {
		mem.clear();
	}

	public void cleanall() {
		clean();
		super.clean();
	}

	public Bitmap get(String key, int width, int height) {
		SoftReference<Bitmap> softbit = mem.get(this.name + genKey(key));
		if (softbit != null)
			return softbit.get();
		File file = genFile(key);
		if (file.exists() && file.isFile() && file.canRead())
		{
			try
			{
				return ImageUtil.getBitmapFromFile(file, width, height);
			} catch (OutOfMemoryError e)
			{
				return null;
			}
		}
		return null;
	}

	@Override
	public String genKey(String key) {
		return MD5.encode(key.getBytes(), true);
	}

	protected File genFile(String key) {
		String name = genKey(key);
		File file = new File(this.path + this.name + "/" + name.substring(0, 1) + "/" + name);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		return file;
	}
}
