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
package de.hybris.platform.acceleratorcms.services.impl;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSRestrictionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;


/**
 * Subclass of the DefaultCMSRestrictionService that selects the Page that matches the most restrictions.
 * 
 * In the DefaultCMSRestrictionService where multiple pages are allowed to be matched due to satisfied restrictions the
 * first matching page is returned. In the RankingCMSRestrictionService this behaviour is changes so that the page that
 * has the most restrictions is returned.
 */
public class RankingCMSRestrictionService extends DefaultCMSRestrictionService
{
	private static final Logger LOG = Logger.getLogger(RankingCMSRestrictionService.class);

	@Override
	public Collection<AbstractPageModel> evaluatePages(final Collection<AbstractPageModel> pages, final RestrictionData data)
	{
		final NavigableMap<Integer, List<AbstractPageModel>> allowedPages = new TreeMap<>();

		final Collection<AbstractPageModel> defaultPages = getDefaultPages(pages);
		for (final AbstractPageModel page : pages)
		{
			if (defaultPages.contains(page))
			{
				continue;
			}

			final List<AbstractRestrictionModel> restrictions = page.getRestrictions();
			if (restrictions == null || restrictions.isEmpty())
			{
				LOG.debug("Page [" + page.getName() + "] is not default page and contains no restrictions. Skipping this page.");
			}
			else
			{
				LOG.debug("Evaluating restrictions for page [" + page.getName() + "].");
				final boolean onlyOneRestrictionMustApply = page.isOnlyOneRestrictionMustApply();
				final boolean allowed = evaluate(restrictions, data, onlyOneRestrictionMustApply);
				if (allowed)
				{
					LOG.debug("Adding page [" + page.getName() + "] to allowed pages");
					final Integer countOfMatchingRestrictions = Integer.valueOf(onlyOneRestrictionMustApply ? 1 : restrictions.size());

					if (allowedPages.containsKey(countOfMatchingRestrictions))
					{
						// Add to existing list
						allowedPages.get(countOfMatchingRestrictions).add(page);
					}
					else
					{
						// Add a new entry
						final List<AbstractPageModel> list = new ArrayList<>();
						list.add(page);
						allowedPages.put(countOfMatchingRestrictions, list);
					}
				}
			}
		}

		final List<AbstractPageModel> result = new ArrayList<>();

		if (MapUtils.isNotEmpty(allowedPages))
		{
			// Take the highest match count
			result.addAll(allowedPages.lastEntry().getValue());
		}
		else
		{
			if (defaultPages.size() > 1)
			{
				LOG.warn(createMoreThanOneDefaultPageWarning(defaultPages));
			}
			if (CollectionUtils.isNotEmpty(defaultPages))
			{
				LOG.debug("Returning default page");
				result.add(defaultPages.iterator().next());
			}
		}

		return result;
	}

	protected String createMoreThanOneDefaultPageWarning(final Collection<AbstractPageModel> defaultPages)
	{
		final StringBuilder warningMessage = new StringBuilder(78);
		if (defaultPages.isEmpty())
		{
			return warningMessage.toString();
		}
		warningMessage.append("More than one default page defined! (");
		for (final AbstractPageModel defaultPage : defaultPages)
		{
			warningMessage.append(' ').append(defaultPage.getName()).append(',');
		}
		warningMessage.replace(warningMessage.length() - 1, warningMessage.length(), "");
		warningMessage.append(" ).");
		warningMessage.append(" First one will be taken as default.");
		return warningMessage.toString();
	}
}
