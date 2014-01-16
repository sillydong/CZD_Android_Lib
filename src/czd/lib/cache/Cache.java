package czd.lib.cache;

import czd.lib.application.ApplicationUtil;
import czd.lib.data.FileUtil;
import czd.lib.data.PreferenceUtil;

import java.io.File;
import java.io.FilenameFilter;
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

	abstract public long gettime(String key);

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
		if (key.endsWith("*"))
		{
			final String start=key.substring(0,key.length()-1);
			boolean result = true;
			File parent = genFile(key).getParentFile();
			if (parent.isDirectory() && parent.canWrite())
			{
				File[] files = parent.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return filename.startsWith(start);
					}
				});
				if (files != null && files.length > 0)
				{
					for (File file : files){
						result &= !(file.exists() && file.isFile()) || file.delete();
						PreferenceUtil.deletePreference(ApplicationUtil.application_context,this.name,file.getName());
					}
				}
			}
			return result;
		}
		else
		{
			File file = genFile(key);
			PreferenceUtil.deletePreference(ApplicationUtil.application_context, this.name, genKey(key));
			return !(file.exists() && file.isFile()) || file.delete();
		}
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
		PreferenceUtil.cleanPreference(ApplicationUtil.application_context, this.name);
	}

	@Override
	public long gettime(String key) {
		return PreferenceUtil.getLongPreference(ApplicationUtil.application_context, this.name, genKey(key));
	}

	@Override
	public String genKey(String key) {
		return key;
	}

	protected File genFile(String key) {
		File file = new File(this.path + this.name + "/" + key);
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

