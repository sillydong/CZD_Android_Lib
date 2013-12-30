package czd.lib.data;

import java.util.ArrayList;

public class StringUtil {
	public static String[] explode(String original, String split) {
		if (original == null || original.length() == 0 || split == null || split.length() == 0)
		{
			return new String[]{original};
		}
		ArrayList<String> strs = new ArrayList<String>();
		int index = 0;
		int len = split.length();
		while ((index = original.indexOf(split)) != -1)
		{
			strs.add(original.substring(0, index));
			original = original.substring(index + len);
		}
		strs.add(original);
		return strs.toArray(new String[0]);
	}

	public static String implode(String[] array) {
		if (array != null && array.length > 0)
		{
			StringBuilder sb = new StringBuilder();
			for (String string : array)
			{
				sb.append(string);
			}
			return sb.toString();
		}
		return "";
	}

	public static String htmlencode(String str) {
		if (str == null)
		{
			return null;
		}
		return replace("\"", "&quot;", replace("<", "&lt;", str));
	}

	public static String htmldecode(String str) {
		if (str == null)
		{
			return null;
		}
		return replace("&quot;", "\"", replace("&lt;", "<", str));
	}

	public static String replace(String from, String to, String source) {
		if (source == null || source.length() == 0 || from == null || from.length() == 0 || to == null)
		{
			return source;
		}
		StringBuffer str = new StringBuffer("");
		int index = -1;
		int len = from.length();
		while ((index = source.indexOf(from)) != -1)
		{
			str.append(source.substring(0, index) + to);
			source = source.substring(index + len);
		}
		str.append(source);
		return str.toString();
	}
}
