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
package de.hybris.platform.yb2bacceleratorstorefront.history.impl;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.yb2bacceleratorstorefront.history.BrowseHistory;
import de.hybris.platform.yb2bacceleratorstorefront.history.BrowseHistoryEntry;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of history browsing service.
 */
public class DefaultBrowseHistory implements BrowseHistory
{
	private static final String SESSION_USER_BROWSE_HISTORY_KEY = "sessionUserBrowseHistory";

	private SessionService sessionService;
	private int capacity = 10;


	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected int getCapacity()
	{
		return capacity;
	}

	@Required
	public void setCapacity(final int capacity)
	{
		this.capacity = capacity;
	}


	@Override
	public void addBrowseHistoryEntry(final BrowseHistoryEntry browseHistoryEntry)
	{
		final List<BrowseHistoryEntry> browseHistoryEntries = getBrowseHistoryEntries();
		browseHistoryEntries.add(0, browseHistoryEntry);
		trimHistory(browseHistoryEntries);
		saveHistory(browseHistoryEntries);
	}

	protected List<BrowseHistoryEntry> getBrowseHistoryEntries()
	{
		final List<BrowseHistoryEntry> browseHistoryEntries = new LinkedList<BrowseHistoryEntry>();
		final Object history = getSessionService().getAttribute(SESSION_USER_BROWSE_HISTORY_KEY);
		if ((history instanceof List))
		{
			browseHistoryEntries.addAll((List) history);
		}
		return browseHistoryEntries;
	}

	protected void trimHistory(final List<BrowseHistoryEntry> browseHistoryEntries)
	{
		while (browseHistoryEntries.size() > getCapacity())
		{
			((LinkedList) browseHistoryEntries).removeLast();
		}
	}

	protected void saveHistory(final List<BrowseHistoryEntry> browseHistoryEntries)
	{
		getSessionService().setAttribute(SESSION_USER_BROWSE_HISTORY_KEY, browseHistoryEntries);
	}

	@Override
	public BrowseHistoryEntry findUrlInHistory(final String url)
	{
		for (final BrowseHistoryEntry entry : getBrowseHistoryEntries())
		{
			final String[] parts = entry.getUrl().split("/");
			if (parts.length > 0 && url.compareTo(parts[parts.length - 1]) == 0)
			{
				return entry;
			}
		}
		return null;
	}
}
