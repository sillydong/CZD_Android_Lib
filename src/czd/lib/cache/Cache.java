package czd.lib.cache;

import czd.lib.data.FileUtil;
import czd.lib.encode.MD5;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-30
 * Time: 上午11:49
 */
interface CacheI<T> {
	abstract public boolean save(String key, T value);

	abstract public boolean exists(String key);

	abstract public T get(String key);

	abstract public boolean delete(String key);

	abstract public long size();

	abstract public String getRealName(String key);

	abstract public void clean();

	abstract String genKey(String key);

}

abstract class AbsFileCache<T> implements CacheI<T> {
	protected String path = "";
	protected String name = "";
	protected ExecutorService writer;

	public AbsFileCache() {
		this.path = FileUtil.getCacheDirectory(false).getAbsolutePath() + "/";
		writer = Executors.newCachedThreadPool();
	}

	@Override
	public boolean exists(String key) {
		File file = genFile(key);
		return file.exists() && file.isFile() && file.canRead();
	}

	@Override
	public boolean delete(String key) {
		File file = genFile(key);
		return !(file.exists() && file.isFile()) || file.delete();
	}

	@Override
	public long size() {
		return FileUtil.getSize(new File(this.path + this.name));
	}

	@Override
	public String getRealName(String key) {
		return genFile(key).getAbsolutePath();
	}

	@Override
	public void clean() {
		File file = new File(this.path + this.name);
		if (file.exists() && file.isDirectory())
			FileUtil.rm(file);
	}

	@Override
	public String genKey(String key) {
		return MD5.encode(key.getBytes());
	}

	protected File genFile(String key) {
		String name = genKey(key);
		File file = new File(this.path + this.name + "/" + name.substring(0, 1) + "/" + name);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		return file;
	}

	protected void cleanOld() {
		final File cachedir = new File(this.path + this.name);
		new Thread() {
			@Override
			public void run() {
				super.run();
				if (FileUtil.getSize(cachedir) > 50 * 1024 * 1024)
					FileUtil.removeExpiredFile(cachedir);
			}
		}.start();
	}
}

