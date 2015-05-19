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
package de.hybris.platform.acceleratorservices.urldecoder.impl;

import de.hybris.platform.acceleratorservices.urldecoder.FrontendUrlDecoder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.PathMatcher;


/**
 * Match url pattern using a path Matcher pattern.
 * 
 * @param <T>
 */
public abstract class BaseFrontendPathMatcherUrlDecoder<T> implements FrontendUrlDecoder<T>
{

	private static final Logger LOG = Logger.getLogger(BaseFrontendPathMatcherUrlDecoder.class);

	private PathMatcher pathMatcher;
	private String pathMatchPattern;


	@Override
	public T decode(final String urlIn)
	{
		final URL url;
		try
		{
			url = new URL(urlIn);
		}
		catch (final MalformedURLException e)
		{
			LOG.warn("unable to parse url [" + urlIn + "] as it was malformed");
			return null;
		}
		final int paramsIndex = StringUtils.indexOfAny(url.getPath(), ";?&");
		final String cleanedUrl = paramsIndex > -1 ? url.getPath().substring(0, paramsIndex) : url.getPath();
		if (getPathMatcher().match(getPathMatchPattern(), cleanedUrl))
		{
			final Map<String, String> pathParams = getPathMatcher().extractUriTemplateVariables(getPathMatchPattern(), cleanedUrl);

			if (pathParams == null || pathParams.size() > 1)
			{
				LOG.warn("unable to extract id from path " + url.getPath() + " and pattern " + getPathMatchPattern());
				return null;
			}
			return translateId(pathParams.get(pathParams.keySet().iterator().next()));
		}
		return null;
	}

	protected abstract T translateId(String id);


	/**
	 * @param pathMatcher
	 *           the pathMatcher to set
	 */
	@Required
	public void setPathMatcher(final PathMatcher pathMatcher)
	{
		this.pathMatcher = pathMatcher;
	}

	/**
	 * @return the pathMatcher
	 */
	public PathMatcher getPathMatcher()
	{
		return pathMatcher;
	}

	/**
	 * @param pathMatchPattern
	 *           the pathMatchPattern to set
	 */
	@Required
	public void setPathMatchPattern(final String pathMatchPattern)
	{
		this.pathMatchPattern = pathMatchPattern;
	}

	/**
	 * @return the pathMatchPattern
	 */
	public String getPathMatchPattern()
	{
		return pathMatchPattern;
	}
}
