package czd.lib.encode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Chen.Zhidong
 *         2012-8-25
 */
public class MD5 {
	public static String encode(byte[] toencode) {
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(toencode);
			return new String(Hex.encodeHex(toencode, false));
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
