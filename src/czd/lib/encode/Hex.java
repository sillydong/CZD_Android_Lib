package czd.lib.encode;

public class Hex {

	public static String toString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (byte element : b)
		{
			int v = element & 0xFF;
			if (v < 16)
			{
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	public static byte[] fromString(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i++)
		{
			data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

}
