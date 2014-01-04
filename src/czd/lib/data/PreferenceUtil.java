package czd.lib.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Map;

public class PreferenceUtil {
	/**
	 * write String into SharedPreference named by the given name
	 *
	 * @param context
	 * @param name
	 * @param towrite
	 */
	public static void writeStringPreference(Context context, String name, String tag, String towrite) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(tag, towrite);
		edit.commit();
	}

	/**
	 * write int into SharedPreference named by the given name
	 *
	 * @param context
	 * @param name
	 * @param tag
	 * @param towrite
	 */
	public static void writeIntPreference(Context context, String name, String tag, int towrite) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt(tag, towrite);
		edit.commit();
	}

	public static void writeLongPreference(Context context, String name, String tag, long towrite) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putLong(tag, towrite);
		edit.commit();
	}

	public static void writeFloatPreference(Context context, String name, String tag, float towrite) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putFloat(tag, towrite);
		edit.commit();
	}

	/**
	 * write int into SharedPreference named by the given name
	 *
	 * @param context
	 * @param name
	 * @param tag
	 * @param towrite
	 */
	public static void writeBooleanPreference(Context context, String name, String tag, boolean towrite) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean(tag, towrite);
		edit.commit();
	}

	/**
	 * write bundle data into SharedPreference named by the given name
	 *
	 * @param context
	 * @param name
	 * @param strtowrite
	 */
	public static void writeBundlePreference(Context context, String name, Bundle strtowrite) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		for (String key : strtowrite.keySet())
		{
			edit.putString(key, strtowrite.getString(key));
		}
		edit.commit();
	}

	public static void writeNameValuePairPreference(Context context, String name, ArrayList<NameValuePair> nameValuePairs) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		for (NameValuePair nameValuePair : nameValuePairs)
		{
			edit.putString(nameValuePair.getName(), nameValuePair.getValue());
		}
		edit.commit();
	}

	/**
	 * clean the named SharedPreference
	 *
	 * @param context
	 * @param name
	 */
	public static void cleanPreference(Context context, String name) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		if (sp != null)
		{
			Editor edit = sp.edit();
			edit.clear();
			edit.commit();
		}
	}

	public static void deletePreference(Context context, String name, String key) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		if (sp != null)
		{
			Editor editor = sp.edit();
			editor.remove(key);
			editor.commit();
		}
	}

	/**
	 * get SharedPreferences by name
	 *
	 * @param context
	 * @param name
	 *
	 * @return Map<String,?>
	 */
	public static Map<String,?> getMapPreference(Context context, String name) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return sp.getAll();
	}

	/**
	 * get String data from SharedPreferences
	 *
	 * @param context
	 * @param name
	 * @param tag
	 *
	 * @return
	 */
	public static String getStringPreference(Context context, String name, String tag) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return sp != null ? sp.getString(tag, "") : "";
	}

	/**
	 * @param context
	 * @param name
	 * @param tag
	 *
	 * @return
	 */
	public static int getIntPreference(Context context, String name, String tag) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return sp != null ? sp.getInt(tag, 0) : 0;
	}

	public static long getLongPreference(Context context, String name, String tag) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return sp != null ? sp.getLong(tag, 0L) : 0L;
	}

	/**
	 * @param context
	 * @param name
	 * @param tag
	 *
	 * @return
	 */
	public static boolean getBooleanPreference(Context context, String name, String tag, boolean defValue) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return sp != null && sp.getBoolean(tag, defValue);
	}

	public static float getFloatPreference(Context context, String name, String tag) {
		SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		return sp != null ? sp.getFloat(tag, 0F) : 0F;
	}
}
