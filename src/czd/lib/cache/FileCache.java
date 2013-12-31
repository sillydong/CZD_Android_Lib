package czd.lib.cache;

import czd.lib.data.FileUtil;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-30
 * Time: 上午11:56
 */
public class FileCache extends AbsFileCache<Object> implements CacheI<Object> {
	protected static FileCache instance;

	public FileCache() {
		super();
		this.name = "object";
	}

	public static FileCache getInstance() {
		if (instance == null)
			instance = new FileCache();
		return instance;
	}

	@Override
	public boolean save(String key, final Object value) {
		cleanOld();
		final File file = genFile(key);
		if (file.exists() && file.isFile())
			file.delete();
		writer.execute(new Runnable() {
			@Override
			public void run() {
				try
				{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(value);
					oos.flush();
					oos.close();
					FileUtil.write(file, baos.toByteArray());
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});

		return true;
	}

	@Override
	public Object get(String key) {
		File file = genFile(key);
		if (file.exists() && file.isFile() && file.canRead())
		{
			try
			{
				byte[] data = FileUtil.read(file);
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
				Object obj = ois.readObject();
				ois.close();
				return obj;
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}

		}
		return null;
	}
}
