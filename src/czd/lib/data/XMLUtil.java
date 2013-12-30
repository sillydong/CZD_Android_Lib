package czd.lib.data;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class XMLUtil {
	public static ArrayList<Object> parse(InputStream is, Class<?> cls, List<String> fields, List<String> elements, String itemelement) {
		ArrayList<Object> result = new ArrayList<Object>();
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, "UTF-8");
			int event_type = parser.getEventType();
			boolean is_done = false;

			Object obj = null;
			String name = "";
			while ((event_type != XmlPullParser.END_DOCUMENT) && !is_done)
			{
				switch (event_type)
				{
					case XmlPullParser.START_DOCUMENT:
						result.clear();
						break;
					case XmlPullParser.START_TAG:
						name = parser.getName();
						if (itemelement.equals(name))
						{
							obj = cls.newInstance();
						}
						if (obj != null && elements.contains(name))
						{
							setFieldValue(obj, fields.get(elements.indexOf(name)), parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						name = parser.getName();
						if (itemelement.equals(name))
						{
							result.add(obj);
							obj = null;
						}
						break;
					default:
						break;
				}
				event_type = parser.next();
			}
		} catch (XmlPullParserException e)
		{
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				is.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	private static void setFieldValue(Object obj, String name, Object value) {
		Field field;
		try
		{
			field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}

	}
}
