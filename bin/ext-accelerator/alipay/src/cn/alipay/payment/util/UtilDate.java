package cn.alipay.payment.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;


public class UtilDate
{

	public static final String dtLong = "yyyyMMddHHmmss";

	public static final String simple = "yyyy-MM-dd HH:mm:ss";

	public static final String dtShort = "yyyyMMdd";

	public static final String dtTimezone = "yyyy-MM-dd HH:mm:ss z";
	
	public static String getOrderNum()
	{
		final Date date = new Date();
		final DateFormat df = new SimpleDateFormat(dtLong);
		return df.format(date);
	}

	public static String getDateFormatter(TimeZone timezone)
	{
		final Date date = new Date();
		final DateFormat df = new SimpleDateFormat(simple);
		if(timezone !=null){
			df.setTimeZone(timezone);
		}
		return df.format(date);
	}

	public static String getDate()
	{
		final Date date = new Date();
		final DateFormat df = new SimpleDateFormat(dtShort);
		return df.format(date);
	}

	public static String getDate(TimeZone timezone)
	{
		Calendar cal = Calendar.getInstance(); 
		final Date date = cal.getTime(); 
		final DateFormat df = new SimpleDateFormat(dtShort); 
		if(timezone !=null){
			df.setTimeZone(timezone);
		}
		return df.format(date);
	}
	
	public static String getFullDate(TimeZone timezone){
		Calendar cal = Calendar.getInstance(); 
		final Date date = cal.getTime(); 
		final DateFormat df = new SimpleDateFormat(dtTimezone); 
		 if(timezone !=null){
			 df.setTimeZone(timezone);
		 }
		 return df.format(date);
	}
}
