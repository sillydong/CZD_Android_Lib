package czd.lib.encode;

import java.util.Random;

/**
 * @author Chen.Zhidong
 *         2011-12-21
 */
public class Reversible {
	public static String encode(String toencode, String key) {
		String encrypt_key = MD5.encode(((int)(new Random().nextDouble() * 32000) + "").getBytes(), false);
		int ctr = 0;
		StringBuilder result = new StringBuilder();
		int len = toencode.length();
		for (int i = 0; i < len; i++)
		{
			ctr = (ctr == encrypt_key.length() ? 0 : ctr);
			result.append(encrypt_key.charAt(ctr));
			result.append((char)(toencode.charAt(i) ^ encrypt_key.charAt(ctr++)));
		}
		return Base64.encode(Reversible.passKey(result.toString(), key).getBytes());
	}

	public static String decode(String todecode, String key) {
		String txt = Reversible.passKey(new String(Base64.decode(todecode.getBytes())), key);
		StringBuilder result = new StringBuilder();
		int len = txt.length();
		for (int i = 0; i < len; i++)
		{
			result.append((char)(txt.charAt(i) ^ txt.charAt(++i)));
		}
		return result.toString();
	}

	private static String passKey(String text, String key) {
		String encrypt_key = MD5.encode(key.getBytes(), false);
		int ctr = 0;
		StringBuilder result = new StringBuilder();
		int len = text.length();
		for (int i = 0; i < len; i++)
		{
			ctr = (ctr == encrypt_key.length() ? 0 : ctr);
			result.append((char)(text.charAt(i) ^ encrypt_key.charAt(ctr++)));
		}
		return result.toString();
	}

}
