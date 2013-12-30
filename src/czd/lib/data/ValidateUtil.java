package czd.lib.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.regex.Pattern;

public class ValidateUtil {
	/**
	 * 判断字符串是否为合法Email地址
	 *
	 * @param email
	 *
	 * @return boolean
	 */
	public static boolean isEmail(String email) {
		String strPatten = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
		return Pattern.compile(strPatten).matcher(email).matches();
	}

	/**
	 * 判断字符串是否为中文字符
	 *
	 * @param str
	 *
	 * @return boolean
	 */
	public static boolean isChinese(String str) {
		return Pattern.compile("[\u0391-\uFFE5]+$").matcher(str).matches();
	}

	/**
	 * 判断字符串是否为NULL或空
	 *
	 * @param str
	 *
	 * @return boolean
	 */
	public static boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 判断整数是否为质数
	 *
	 * @param x
	 *
	 * @return boolean
	 */
	public static boolean isPrime(int x) {
		if (x <= 7)
		{
			if (x == 2 || x == 3 || x == 5 || x == 7)
				return true;
		}
		int c = 7;
		if (x % 2 == 0)
			return false;
		if (x % 3 == 0)
			return false;
		if (x % 5 == 0)
			return false;
		int end = (int)Math.sqrt(x);
		while (c <= end)
		{
			if (x % c == 0)
			{
				return false;
			}
			c += 4;
			if (x % c == 0)
			{
				return false;
			}
			c += 2;
			if (x % c == 0)
			{
				return false;
			}
			c += 4;
			if (x % c == 0)
			{
				return false;
			}
			c += 2;
			if (x % c == 0)
			{
				return false;
			}
			c += 4;
			if (x % c == 0)
			{
				return false;
			}
			c += 6;
			if (x % c == 0)
			{
				return false;
			}
			c += 2;
			if (x % c == 0)
			{
				return false;
			}
			c += 6;
		}
		return true;
	}

	/**
	 * 判断字符串是否为包含".a-zA-Z_0-9-!@#$%^&*()"字符的5到18位密码
	 *
	 * @param password
	 *
	 * @return boolean
	 */
	public static boolean isPassword(String password) {
		String strPattern = "^[.a-zA-Z_0-9-!@#$%^&*()]{5,18}$";
		return Pattern.compile(strPattern).matcher(password).matches();
	}

	/**
	 * 判断字符串是否为整数
	 *
	 * @param str
	 *
	 * @return boolean
	 */
	public static boolean isNumber(String str) {
		return Pattern.compile("^[-\\+]?[\\d]+$").matcher(str).matches();
	}

	/**
	 * 判断字符串是否为浮点，包括double和float
	 *
	 * @param str
	 *
	 * @return boolean
	 */
	public static boolean isDouble(String str) {
		return Pattern.compile("^[-\\+]?\\d+\\.\\d+$").matcher(str).matches();
	}

	/**
	 * 判断是否为合法字符
	 *
	 * @param str
	 *
	 * @return boolean
	 */
	public static boolean isLetter(String str) {
		if (str == null || str.length() < 0)
		{
			return false;
		}
		return Pattern.compile("[\\w\\.-_]*").matcher(str).matches();
	}

	/**
	 * 判断字符串是否为32为MD5加密结果
	 *
	 * @param str
	 *
	 * @return
	 */
	public static boolean isMD5(String str) {
		return Pattern.compile("^[a-zA-Z0-9]{32}$").matcher(str).matches();
	}

	/**
	 * 判断字符串是否为SH1加密结果
	 *
	 * @param str
	 *
	 * @return boolean
	 */
	public static boolean isSH1(String str) {
		return Pattern.compile("^[a-zA-Z0-9]{40}$").matcher(str).matches();
	}

	/**
	 * @param str
	 *
	 * @return
	 */
	public static boolean isAddress(String str) {
		return Pattern.compile("^[^!<>?=+@{}_$%]*$").matcher(str).matches();
	}

	public static boolean isValidKeyword(String str) {
		return Pattern.compile("^[^<>;?=#{}]{1,64}$").matcher(str).matches();
	}

	/**
	 * test the string is validate phone number
	 *
	 * @param str
	 *
	 * @return
	 */
	public static boolean isPhoneNumber(String str) {
		return Pattern.compile("^1[\\d]{10}$").matcher(str).matches();
	}

	public static boolean isPostCode(String str) {
		return Pattern.compile("^[a-zA-Z 0-9-]+$").matcher(str).matches();
	}

	public static boolean isURL(String str) {
		return Pattern.compile("^[a-zA-Z]+://[^\\S]$").matcher(str).matches();
	}

	public static boolean isIP(String str) {
		String strPattern = "^(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5])$";
		return Pattern.compile(strPattern).matcher(str).matches();
	}

	public static boolean isEnglishName(String str) {
		return Pattern.compile("^[a-z A-Z]+").matcher(str).matches();
	}

	public static boolean isIMEI(String imei) {
		return Pattern.compile("^[0-9]{15}").matcher(imei).matches();
	}

	public static boolean isJSONString(String data) {
		if (data != null)
		{
			try
			{
				Object data_o = new JSONTokener(data).nextValue();
				if (data_o instanceof JSONObject || data_o instanceof JSONArray)
				{
					return true;
				}
			} catch (JSONException e)
			{
				return false;
			}
		}
		return false;
	}

	public static boolean isNormalChineseContent(String data) {
		return Pattern.compile("^[\\w \\p{P}]+$").matcher(data).matches();
	}
}
