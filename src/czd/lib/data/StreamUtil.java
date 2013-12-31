package czd.lib.data;

import java.io.*;

public class StreamUtil {
	public static String getStringFromSteam(InputStream is, String encode) {
		if (is != null)
		{
			try
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, (encode == null || encode.equals("") ? "utf-8" : encode)));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null)
				{
					sb.append(line).append("\n");
				}
				return sb.toString();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return "";
	}

	public static void copyStream(InputStream is, OutputStream os) {
		try
		{
			byte[] data = new byte[1024];
			int len = 0;
			while ((len = is.read(data, 0, 1024)) != -1)
			{
				os.write(data, 0, len);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
