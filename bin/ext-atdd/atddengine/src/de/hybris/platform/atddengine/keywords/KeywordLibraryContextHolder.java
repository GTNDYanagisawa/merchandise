/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.atddengine.keywords;


public final class KeywordLibraryContextHolder
{
	private static ThreadLocal<KeywordLibraryContext> threadLocal = new ThreadLocal<KeywordLibraryContext>();

	public static KeywordLibraryContext getKeywordLibraryContext()
	{
		return threadLocal.get();
	}

	public static void setKeywordLibraryContext(final KeywordLibraryContext keywordLibraryContext)
	{
		threadLocal.set(keywordLibraryContext);
	}

	private KeywordLibraryContextHolder()
	{

	}

}
