package de.hybris.platform.chinaaccelerator.alipay.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class AlipayServiceUtil {
	public static String printRequestParameters(HttpServletRequest request){
		Map<String, String> map = transRequestParam2Map(request.getParameterMap());
		StringBuilder fullStr = new StringBuilder();
		for(String key: map.keySet()){
			String value = "";
			try {
				value = java.net.URLEncoder.encode(map.get(key),"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			fullStr.append("\n").append(key).append(":").append(value).append("; ");
		}
		return fullStr.toString();
	}
	
	/**
	 * Convert the request parameter map into LinkedHashMap for string to string
	 * @param params
	 * @return
	 */
	public static Map<String, String> transRequestParam2Map(Map params) {
		final Map<String, String> map = new LinkedHashMap<String, String>();
		for(Object key: params.keySet()){
			//if(params.get(key) instanceof String[]){
				String[] strArray = (String[])params.get(key);
				StringBuilder builder = new StringBuilder();
				for(String s : strArray) {
				    builder.append(s);
				}
				String value = builder.toString();
				map.put((String)key, value);
			//}
		}
		return map;
	}

// REDDRA-39	public static Map<String, String> transBean2Map(final Object obj)
//	{
//		final Map<String, String> map = new LinkedHashMap<String, String>();
//		Field[] fields = obj.getClass().getDeclaredFields();
//		for (int i = 0; i < fields.length; i++)
//		{
//			try
//			{
//				fields[i].setAccessible(true);
//				final String key = fields[i].getName();
//				String val = "";
//				if (fields[i].get(obj) instanceof Float)
//				{
//					final Float fval = (Float) fields[i].get(obj);
//					if (fval.floatValue() > 0)
//					{
//						val = String.valueOf(fval);
//					}
//				}
//				else if (fields[i].get(obj) instanceof Integer)
//				{
//					final Integer ival = (Integer) fields[i].get(obj);
//					if (ival.intValue() > 0)
//					{
//						val = String.valueOf(ival);
//					}
//				}
//				else
//				{
//					val = (fields[i].get(obj) == null) ? "" : String.valueOf(fields[i].get(obj));
//				}
//				map.put(key, val);
//			}
//			catch (final IllegalArgumentException e)
//			{
//				e.printStackTrace();
//			}
//			catch (final IllegalAccessException e)
//			{
//				e.printStackTrace();
//			}
//		}
//		if (obj.getClass().getSuperclass() != null)
//		{
//			fields = obj.getClass().getSuperclass().getDeclaredFields();
//			for (int i = 0; i < fields.length; i++)
//			{
//				try
//				{
//					fields[i].setAccessible(true);
//					final String key = fields[i].getName();
//					String val = "";
//					if (fields[i].get(obj) instanceof Float)
//					{
//						final Float fval = (Float) fields[i].get(obj);
//						if (fval.floatValue() > 0)
//						{
//							val = String.valueOf(fval);
//						}
//					}
//					else if (fields[i].get(obj) instanceof Integer)
//					{
//						final Integer ival = (Integer) fields[i].get(obj);
//						if (ival.intValue() > 0)
//						{
//							val = String.valueOf(ival);
//						}
//					}
//					else
//					{
//						val = (fields[i].get(obj) == null) ? "" : String.valueOf(fields[i].get(obj));
//					}
//					map.put(key, val);
//				}
//				catch (final IllegalArgumentException e)
//				{
//					e.printStackTrace();
//				}
//				catch (final IllegalAccessException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return map;
//	}
}
