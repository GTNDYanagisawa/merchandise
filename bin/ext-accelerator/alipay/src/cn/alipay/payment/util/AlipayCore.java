package cn.alipay.payment.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AlipayCore
{

	public static String buildMysign(final Map<String, String> sArray, final String key)
	{
		String prestr = createLinkString(sArray);
		prestr = prestr + key;
		final String mysign = AlipayMd5Encrypt.md5(prestr);
		return mysign;
	}

	public static Map<String, String> paraFilter(final Map<String, String> sArray)
	{

		final Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0)
		{
			return result;
		}

		for (final String key : sArray.keySet())
		{
			final String value = sArray.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type"))
			{
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	public static String createLinkString(final Map<String, String> params)
	{

		final List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++)
		{
			final String key = keys.get(i);
			final String value = params.get(key);

			if (i == keys.size() - 1)
			{
				prestr = prestr + key + "=" + value;
			}
			else
			{
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

	public static void logResult(final String sWord)
	{
		FileWriter writer = null;
		try
		{
			writer = new FileWriter("D:/alipay.log");
			writer.write(sWord);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
