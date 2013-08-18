package czd.lib.data;

import czd.lib.encode.Blowfish;
import czd.lib.encode.Rijndael;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class CookieUtil {
	public static final int ENCODE_BLOWFISH = 1;
	public static final int ENCODE_RIJNDAEL = 2;

	public static Map<String, String> decode(String cookiedata, int encode, String key) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			cookiedata = URLDecoder.decode(cookiedata, "utf-8");

			String decoded = "";
			if (encode == ENCODE_BLOWFISH) {
				decoded = Blowfish.getInstance(key).decrypt(cookiedata);
			}
			else if (encode == ENCODE_RIJNDAEL) {
				decoded = Rijndael.decrypt(cookiedata, key.getBytes());
			}

			if (decoded != null && !decoded.equals("")) {
				String[] sdecoded = decoded.split("¤");
				for (String string : sdecoded) {
					String[] a = string.split("\\|");
					result.put(a[0], a[1]);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
}
