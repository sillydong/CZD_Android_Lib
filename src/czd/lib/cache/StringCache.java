package czd.lib.cache;

import czd.lib.application.ApplicationUtil;
import czd.lib.data.FileUtil;
import czd.lib.data.PreferenceUtil;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-30
 * Time: 上午11:57
 */
public class StringCache extends AbsFileCache<String> implements CacheI<String> {
	protected static StringCache instance;

	public StringCache() {
		super();
		this.name = "string";
	}

	public static StringCache getInstance() {
		if (instance == null)
			instance = new StringCache();
		return instance;
	}

	@Override
	public boolean save(String key, final String value) {
		cleanOld();
		final File file = genFile(key);
		writer.execute(new Runnable() {
			@Override
			public void run() {
				if (file.exists() && file.isFile())
					file.delete();
				FileUtil.write(file, value.getBytes());
			}
		});
		PreferenceUtil.writeLongPreference(ApplicationUtil.application_context, this.name, genKey(key), System.currentTimeMillis());
		return true;
	}

	@Override
	public String get(String key) {
		return new String(FileUtil.read(genFile(key)));
	}
}
