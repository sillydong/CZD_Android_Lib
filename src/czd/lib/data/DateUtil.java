package czd.lib.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * Format String like "yyyy-MM-dd" into Date
	 *
	 * @param date the date string
	 *
	 * @return Date
	 */
	public static Date str2date(String date, String format) {
		DateFormat f = new SimpleDateFormat(format);
		try
		{
			return f.parse(date);
		} catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Format Date into String
	 *
	 * @param date the date
	 *
	 * @return String
	 */
	public static String date2str(Date date) {
		if (date != null)
		{
			DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
			return format.format(date);
		}
		else
		{
			return "";
		}
	}

	/**
	 * check days between two dates
	 *
	 * @param begin define the beginning date
	 * @param end   define the ending date
	 *
	 * @return long
	 */
	public static long datediff(Date begin, Date end) {
		return (begin != null && end != null) ? (end.getTime() - begin.getTime()) / (24 * 60 * 60 * 1000) : 0;
	}

	/**
	 * check days between two string dates
	 *
	 * @param begin define the beginning date
	 * @param end   define the ending date
	 *
	 * @return long
	 */
	public static long datediff(String begin, String end, String format) {
		return DateUtil.datediff(DateUtil.str2date(begin, format), DateUtil.str2date(end, format));
	}

	/**
	 * plus days to a date
	 *
	 * @param begin to be plused
	 * @param day   to plus
	 *
	 * @return Date
	 */
	public static Date dateplusd(Date begin, int day) {
		if (begin != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(begin);
			cal.add(Calendar.DATE, day);
			return cal.getTime();
		}
		else
		{
			return null;
		}
	}

	/**
	 * plus days to a date
	 *
	 * @param begin to be plused
	 * @param day   to plus
	 *
	 * @return String
	 */
	public static String dateplusd(String begin, int day, String format) {
		return DateUtil.date2str(DateUtil.dateplusd(DateUtil.str2date(begin, format), day));
	}

	/**
	 * plus months to a date
	 *
	 * @param begin to be plused
	 * @param month day to plus
	 *
	 * @return Date
	 */
	public static Date dateplusm(Date begin, int month) {
		if (begin != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(begin);
			cal.add(Calendar.MONTH, month);
			return cal.getTime();
		}
		else
		{
			return null;
		}
	}

	/**
	 * plus months to a date
	 *
	 * @param begin to be plused
	 * @param month day to plus
	 *
	 * @return String
	 */
	public static String dateplusm(String begin, int day, String format) {
		return DateUtil.date2str(DateUtil.dateplusm(DateUtil.str2date(begin, format), day));
	}

	/**
	 * plus years to a date
	 *
	 * @param begin to be plused
	 * @param month day to plus
	 *
	 * @return Date
	 */
	public static Date dateplusy(Date begin, int year) {
		if (begin != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(begin);
			cal.add(Calendar.YEAR, year);
			return cal.getTime();
		}
		else
		{
			return null;
		}
	}

	/**
	 * plus years to a date
	 *
	 * @param begin to be plused
	 * @param month day to plus
	 *
	 * @return String
	 */
	public static String dateplusy(String begin, int day, String format) {
		return DateUtil.date2str(DateUtil.dateplusy(DateUtil.str2date(begin, format), day));
	}

	public static long strtotime(String date, String format) {
		try
		{
			return new SimpleDateFormat(format).parse(date).getTime();
		} catch (ParseException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	public static Date timetostr(long timestamp, String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		try
		{
			return f.parse(f.format(timestamp));
		} catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
