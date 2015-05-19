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
package de.hybris.platform.yb2bacceleratorstorefront.history;

/**
 * Service for manipulating browsing history.
 */
public interface BrowseHistory
{
	/**
	 * Adds the url to the browsing history.
	 * 
	 * @param browseHistoryEntry
	 *           the {@link BrowseHistoryEntry} that will be added to the history
	 */
	void addBrowseHistoryEntry(BrowseHistoryEntry browseHistoryEntry);

	/**
	 * Browses history for given url.
	 * 
	 * @param url
	 *           to search for
	 * @return history entry or null if not found
	 */
	BrowseHistoryEntry findUrlInHistory(String url);
}
