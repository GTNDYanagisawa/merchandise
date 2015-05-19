/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.acceleratorservices.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


public class HtmlElementHelper
{
	private static final Logger LOG = Logger.getLogger(HtmlElementHelper.class);

	public void writeOpenElement(final PageContext pageContext, final String elementName, final Map<String, String> attributes)
	{
		validateParameterNotNull(pageContext, "Parameter pageContext must not be null");
		validateParameterNotNull(elementName, "Parameter elementName must not be null");
		checkArgument(StringUtils.isNotBlank(elementName), "Parameter elementName must not be blank");


		final JspWriter out = pageContext.getOut();

		try
		{
			out.write("<" + elementName);
			if (attributes != null && !attributes.isEmpty())
			{
				for (final Map.Entry<String, String> entry : attributes.entrySet())
				{
					// TODO: Correct escaping
					out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
				}
			}
			out.write(">");
			out.write("\n");
		}
		catch (final IOException e)
		{
			LOG.warn("Could not write open element: " + e.getMessage());
		}
	}

	public void writeEndElement(final PageContext pageContext, final String elementName)
	{
		validateParameterNotNull(pageContext, "Parameter pageContext must not be null");
		validateParameterNotNull(elementName, "Parameter elementName must not be null");
		checkArgument(StringUtils.isNotBlank(elementName), "Parameter elementName must not be blank");

		final JspWriter out = pageContext.getOut();

		try
		{
			out.write("</" + elementName + ">");
		}
		catch (final IOException e)
		{
			LOG.warn("Could not write end element: " + e.getMessage());
		}
	}

	public Map<String, String> mergeAttributeMaps(final Map<String, String>... maps)
	{
		final Map<String, String> result = new HashMap<>();

		if (maps != null && maps.length > 0)
		{
			for (final Map<String, String> map : maps)
			{
				if (map != null)
				{
					for (final Map.Entry<String, String> entry : map.entrySet())
					{
						if (!result.containsKey(entry.getKey()))
						{
							// Just add it to the string
							final String valueString = entry.getValue();
							if (valueString != null)
							{
								result.put(entry.getKey(), valueString);
							}
						}
						else
						{
							final String valueString = mergeAttributeValue(entry.getKey(), result.get(entry.getKey()), entry.getValue());
							if (valueString == null)
							{
								result.remove(entry.getKey());
							}
							else
							{
								result.put(entry.getKey(), valueString);
							}
						}
					}
				}
			}
		}

		return result;
	}

	protected String mergeAttributeValue(final String key, final String currentValue, final String newValue)
	{
		if ("class".equals(key))
		{
			return currentValue + " " + newValue;
		}
		return newValue;
	}
}
