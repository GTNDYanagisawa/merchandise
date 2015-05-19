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

package de.hybris.platform.sap.core.requestsequencer.configurer;

import java.util.List;


/**
 * Interface for maintaining URL Patterns which are used by the RequestSequencerFilter.
 */
public interface UrlPattern
{

	/**
	 * @param urlRegEx
	 *           hold the url pattern as a regular expression
	 */
	public void setIncludeUrlRegExList(final List<String> urlRegEx);

	/**
	 * @return the URL Pattern
	 */
	public List<String> getIncludeUrlRegExList();


	/**
	 * @param urlRegEx
	 *           hold the url pattern as a regular expression
	 */
	public void setExcludeUrlRegExList(final List<String> urlRegEx);

	/**
	 * @return the URL Pattern
	 */
	public List<String> getExcludeUrlRegExList();

}
