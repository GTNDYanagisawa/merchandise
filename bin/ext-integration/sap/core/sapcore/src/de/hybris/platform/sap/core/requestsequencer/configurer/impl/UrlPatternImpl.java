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

package de.hybris.platform.sap.core.requestsequencer.configurer.impl;

import de.hybris.platform.sap.core.common.configurer.impl.ConfigurerEntitiesListImpl;
import de.hybris.platform.sap.core.requestsequencer.configurer.UrlPattern;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


/**
 * Default Implementation for {@link UrlPattern}.
 */
public class UrlPatternImpl implements UrlPattern
{
	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(UrlPatternImpl.class.getName());

	/**
	 * Parent parameter list.
	 */
	private ConfigurerEntitiesListImpl<Pattern> configurerListForUrlIncludePatterns = null;
	private ConfigurerEntitiesListImpl<Pattern> configurerListForUrlExcludePatterns = null;

	/**
	 *
	 */
	private List<String> includeUrlRegExList = null;
	private List<String> excludeUrlRegExList = null;

	/**
	 * @param regExMap
	 *           a list of include regular expressions
	 */
	public void setIncludeUrlPatternList(final ConfigurerEntitiesListImpl<Pattern> regExMap)
	{
		this.configurerListForUrlIncludePatterns = regExMap;
	}

	/**
	 * @return a list of include regular expressions
	 */
	protected ConfigurerEntitiesListImpl<Pattern> getIncludeUrlPatternList()
	{
		return this.configurerListForUrlIncludePatterns;
	}

	/**
	 * @param regExMap
	 *           a list of exclude regular expressions
	 */
	public void setExcludeUrlPatternList(final ConfigurerEntitiesListImpl<Pattern> regExMap)
	{
		this.configurerListForUrlExcludePatterns = regExMap;
	}

	/**
	 * @return a list of exclude regular expressions
	 */
	protected ConfigurerEntitiesListImpl<Pattern> getExcludeUrlPatternList()
	{
		return this.configurerListForUrlExcludePatterns;
	}


	@Override
	public List<String> getIncludeUrlRegExList()
	{
		return includeUrlRegExList;
	}

	@Override
	public void setIncludeUrlRegExList(final List<String> regExList)
	{
		this.includeUrlRegExList = regExList;
	}

	@Override
	public void setExcludeUrlRegExList(final List<String> urlRegEx)
	{
		this.excludeUrlRegExList = urlRegEx;

	}

	@Override
	public List<String> getExcludeUrlRegExList()
	{
		return this.excludeUrlRegExList;
	}


	/**
	 * Compiles a RegEx Pattern object for each element of the include URL RegEx pattern list and the exclude URL RegEx
	 * pattern list and adds the resulting Pattern object to the corresponding configurerListForUrlIncludePatterns list
	 * or configurerListForUrlExcludePatterns list.
	 *
	 *
	 */
	public void init()
	{
		if (includeUrlRegExList != null)
		{
			compileUrlPatterns(includeUrlRegExList, configurerListForUrlIncludePatterns);
		}

		if (excludeUrlRegExList != null)
		{
			compileUrlPatterns(excludeUrlRegExList, configurerListForUrlExcludePatterns);
		}

	}


	@Override
	public String toString()
	{
		return "Include RegEx URL patterns:" + this.getIncludeUrlRegExList().toString() + "Include RegEx Pattern objects:"
				+ this.getIncludeUrlPatternList().toString() + "Exclude RegEx URL patterns"
				+ this.getExcludeUrlRegExList().toString() + "Exclude RegEx Pattern objects"
				+ this.getExcludeUrlPatternList().toString();
	}


	/**
	 * Compiles the URL RegEx patterns to RegEx Pattern objects.
	 *
	 * @param urlPatterns
	 *           list of URL RegEx patterns
	 * @param compiledUrlPatterns
	 *           list of RegEx Pattern objects
	 */
	protected void compileUrlPatterns(final List<String> urlPatterns, final ConfigurerEntitiesListImpl<Pattern> compiledUrlPatterns)
	{

		if (urlPatterns != null && compiledUrlPatterns != null)
		{
			for (final String urlPattern : urlPatterns)
			{
				final Pattern compiledPattern = Pattern.compile(urlPattern);
				compiledUrlPatterns.addEntity(compiledPattern);

			}
		}
	}

}
