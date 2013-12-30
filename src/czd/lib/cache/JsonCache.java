package czd.lib.cache;

import czd.lib.data.FileUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-30
 * Time: 上午11:56
 */
public class JsonCache extends FileCache {
	protected static JsonCache instance;

	public JsonCache() {
		super();
		this.name = "json";
	}

	public static JsonCache getInstance() {
		if (instance == null)
			instance = new JsonCache();
		return instance;
	}

	@Override
	public boolean save(final String key, final Object value) {
		cleanOld();
		final File file = new File(this.path + this.name, genKey(key));
		if (file.exists() && file.isFile())
			file.delete();
		if (value instanceof JSONObject)
		{
			writer.execute(new Runnable() {
				@Override
				public void run() {
					FileUtil.writeFile(file, ((JSONObject)value).toString().getBytes());
				}
			});
		}

		else if (value instanceof JSONArray)
		{
			writer.execute(new Runnable() {
				@Override
				public void run() {
					FileUtil.writeFile(file, ((JSONArray)value).toString().getBytes());
				}
			});
		}
		else
			super.save(key, value);

		return true;
	}

	@Override
	public Object get(String key) {
		File file = new File(this.path + this.name, genKey(key));
		String data = new String(FileUtil.readFile(file));
		if (data.startsWith("{") || data.startsWith("["))
		{
			try
			{
				return new JSONTokener(data).nextValue();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
}
