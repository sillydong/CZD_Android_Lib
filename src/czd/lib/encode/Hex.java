package czd.lib.encode;

public class Hex {

	private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	public static byte[] decodeHex(char[] data) {

		int len = data.length;

		if ((len & 0x01) != 0)
		{
			return null;
		}

		byte[] out = new byte[len >> 1];

		for (int i = 0, j = 0; j < len; i++)
		{
			int f = Character.digit(data[j], 16) << 4;
			j++;
			f = f | Character.digit(data[j], 16);
			j++;
			out[i] = (byte)(f & 0xFF);
		}

		return out;
	}

	public static char[] encodeHex(byte[] data, boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	private static char[] encodeHex(byte[] data, char[] toDigits) {
		int l = data.length;
		char[] out = new char[l << 1];
		for (int i = 0, j = 0; i < l; i++)
		{
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}

	public static String hexToString(String input, int groupLength) {
		StringBuilder sb = new StringBuilder(input.length());
		for (int i = 0; i < input.length(); i++)
		{
			String hex = input.substring(i, i + groupLength);
			sb.append((char)Integer.parseInt(hex, 16));
		}
		return sb.toString();
	}

}
