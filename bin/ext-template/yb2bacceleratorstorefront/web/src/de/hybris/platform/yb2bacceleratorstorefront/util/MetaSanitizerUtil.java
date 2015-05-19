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
package de.hybris.platform.yb2bacceleratorstorefront.util;

import de.hybris.platform.catalog.model.KeywordModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;


/**
 * 
 * Utility class for sanitizing up content that will appear in HTML meta tags.
 * 
 */
public class MetaSanitizerUtil
{
	/**
	 * Takes a List of KeywordModels and returns a comma separated list of keywords as String.
	 * 
	 * @param keywords
	 *           List of KeywordModel objects
	 * @return String of comma separated keywords
	 */
	public static String sanitizeKeywords(final List<KeywordModel> keywords)
	{
		// Remove duplicates
		final List<String> keywordList = new ArrayList<String>();
		for (final KeywordModel kw : keywords)
		{
			keywordList.add(kw.getKeyword());
		}
		final Set<String> keywordSet = new HashSet<String>(keywordList);
		keywordList.clear();
		keywordList.addAll(keywordSet);

		// Format keywords
		final StringBuilder stringBuilder = new StringBuilder();
		String formattedKeywords = null;
		for (final String kw : keywordList)
		{
			stringBuilder.append(kw);
			stringBuilder.append(',');
		}
		if (stringBuilder.length() > 0)
		{
			formattedKeywords = stringBuilder.substring(0, stringBuilder.length() - 1);
		}
		return formattedKeywords;
	}

	/**
	 * Takes a string of words, removes duplicates and returns a comma separated list of keywords as a String
	 * 
	 * @param keywords
	 *           Keywords to be sanitized
	 * @return String of comma separated keywords
	 */
	public static String sanitizeKeywords(final String keywords)
	{
		final String clean = (StringUtils.isNotEmpty(keywords) ? Jsoup.parse(keywords).text() : ""); // Clean html
		final String[] words = StringUtils.split(clean.replace("\"", "")); // Clean quotes

		// Remove duplicates
		final StringBuffer noDupes = new StringBuffer();
		for (final String word : words)
		{
			if (noDupes.indexOf(word) == -1)
			{
				noDupes.append(word).append(',');
			}
		}
		if (noDupes.length() > 0)
		{
			noDupes.deleteCharAt(noDupes.length() - 1);
		}
		return noDupes.toString();
	}

	/**
	 * Removes all HTML tags and double quotes and returns a String
	 * 
	 * @param description
	 *           Description to be sanitized
	 * @return String object
	 */
	public static String sanitizeDescription(final String description)
	{
		if (StringUtils.isNotEmpty(description))
		{
			final String clean = Jsoup.parse(description).text();
			return clean.replace("\"", "");
		}
		else
		{
			return "";
		}
	}
}
