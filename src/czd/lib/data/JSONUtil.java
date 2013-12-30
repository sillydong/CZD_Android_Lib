package czd.lib.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONUtil {
	public static JSONArray combineArrays(JSONArray jarray1, JSONArray jarray2) {
		if (jarray1 != null && jarray2 != null)
		{
			int i = 0;
			int j = jarray2.length();
			while (true)
			{
				if (i >= j)
					return jarray1;
				try
				{
					Object tmp = jarray2.get(i);
					if (!containsObject(jarray1, tmp))
						jarray1.put(tmp);
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
				i++;
			}
		}
		else if (jarray1 == null && jarray2 != null)
		{
			return jarray2;
		}
		else if (jarray1 != null && jarray2 == null)
		{
			return jarray1;
		}
		return null;
	}

	public static boolean containsObject(JSONArray jarray, Object search) {
		if (jarray != null)
		{
			int i = 0;
			int j = jarray.length();
			while (true)
			{
				if (i >= j)
					return false;
				try
				{
					if (jarray.get(i).equals(search))
					{
						return true;
					}
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
				i++;
			}
		}
		return false;
	}

	public static Integer indexOfObject(JSONArray jarray, Object search) {
		int i = 0;
		int j = jarray.length();
		while (true)
		{
			if (i >= j)
				return null;
			try
			{
				if (jarray.get(i).equals(search))
				{
					return i;
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			i++;
		}
	}

	public static JSONArray nullObject(JSONArray jarray, Object search) {
		if (jarray != null)
		{
			try
			{
				Integer index = indexOfObject(jarray, search);
				if (index != null)
					jarray.put(index, null);
				return jarray;
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static JSONArray nullObject(JSONArray jarray, int index) {
		if (jarray != null)
		{
			try
			{
				jarray.put(index, null);
				return jarray;
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static JSONArray removeAtIndex(JSONArray jarray, int index) {
		JSONArray result = new JSONArray();
		int i = 0;
		int j = jarray.length();
		while (true)
		{
			if (i >= j)
				return result;
			if (i != index)
			{
				try
				{
					result.put(jarray.get(i));
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
			i++;
		}
	}

	public static JSONArray safeAddToArray(JSONArray jarray, Object object) {
		jarray.put(object);
		return jarray;
	}

	public static JSONObject safeAddToObject(JSONObject jobject, String key, Object value) {
		try
		{
			jobject.put(key, value);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return jobject;
	}

	public static JSONArray safeGetJSONArray(JSONObject jobject, String name) {
		try
		{
			return jobject.getJSONArray(name);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String,String> jTOm(JSONObject obj) throws JSONException {
		if (obj != null && obj.length() >= 0)
		{
			Map<String,String> s = new HashMap<String,String>();
			for (Iterator<String> names = obj.keys(); names.hasNext(); )
			{
				String name = names.next();
				String value = obj.getString(name);
				s.put(name, value.equalsIgnoreCase("null") ? "" : value);
			}
			return s;
		}
		return null;
	}

	public static ArrayList<Object> jTOa(JSONArray ja) {
		ArrayList<Object> result = new ArrayList<Object>();
		if (ja != null && ja.length() > 0)
		{
			try
			{
				int len = ja.length();
				for (int i = 0; i < len; i++)
				{
					result.add(ja.get(i));
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

}
