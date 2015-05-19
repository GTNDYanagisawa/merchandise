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


public abstract class AbstractKeywordLibrary
{
	protected AbstractKeywordLibrary()
	{
		final KeywordLibraryContext keywordLibraryContext = KeywordLibraryContextHolder.getKeywordLibraryContext();

		if (keywordLibraryContext == null)
		{
			final String message = String.format("Failed to initialize %s. No KeywordLibraryContext has been created yet.",
					getClass().getName());
			throw new IllegalStateException(message);
		}
		else
		{
			keywordLibraryContext.initializeKeywordLibrary(this);
		}
	}
}
