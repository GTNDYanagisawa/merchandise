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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


/**
 * Parser operating on a configured regular expression to return a matching part or null.
 */
public class RegexParser implements InitializingBean
{
	private static final int DEFAULT_GROUP = 0;

	private String regex;
	private Pattern pattern;

	@Override
	public void afterPropertiesSet()
	{
		Assert.hasLength(regex);
		pattern = Pattern.compile(regex);
	}

	/**
	 * Parse the value and return the matched part or null
	 * 
	 * @param value
	 * @return matched part or null
	 */
	public String parse(final String value)
	{
		return parse(value, DEFAULT_GROUP);
	}


	/**
	 * Parse the value and return the matched group or null
	 * 
	 * @param value
	 * @param group
	 * @return matched group or null
	 */
	public String parse(final String value, final int group)
	{
		Assert.isTrue(group >= 0);
		String result = null;
		if (!StringUtils.isBlank(value))
		{
			final Matcher matcher = pattern.matcher(value);
			if (matcher.find())
			{
				final String groupExp = matcher.group(group);
				if (!StringUtils.isBlank(groupExp))
				{
					result = groupExp;
				}
			}
		}
		return result;
	}

	/**
	 * @param regex
	 *           the regex to set
	 */
	public void setRegex(final String regex)
	{
		Assert.hasLength(regex);
		this.regex = regex;
	}

	/**
	 * @return the regex
	 */
	protected String getRegex()
	{
		return regex;
	}
}
