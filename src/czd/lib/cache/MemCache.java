package czd.lib.cache;

import czd.lib.encode.MD5;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-30
 * Time: 上午11:57
 */
public class MemCache implements CacheI<Object> {
	protected ConcurrentHashMap<String,SoftReference<Object>> mem;

	@Override
	public boolean save(String key, Object value) {
		mem.put(genKey(key), new SoftReference<Object>(value));
		return true;
	}

	@Override
	public boolean exists(String key) {
		return mem.containsKey(genKey(key));
	}

	@Override
	public Object get(String key) {
		SoftReference<Object> softref = mem.get(genKey(key));
		if (softref != null)
			return softref.get();
		return null;
	}

	@Override
	public boolean delete(String key) {
		mem.remove(genKey(key));
		return true;
	}

	@Override
	public long size() {
		return mem.size();
	}

	@Override
	public String getRealName(String key) {
		return genKey(key);
	}

	@Override
	public void clean() {
		mem.clear();
	}

	@Override
	public String genKey(String key) {
		return MD5.encode(key.getBytes());
	}
}
