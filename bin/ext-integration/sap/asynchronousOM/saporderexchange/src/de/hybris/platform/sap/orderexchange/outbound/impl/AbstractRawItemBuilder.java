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
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.sap.orderexchange.outbound.RawItemBuilder;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.servicelayer.model.AbstractItemModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;



/**
 * Default raw item builder delegating the creation of the individual lines of the raw item to the registered instances
 * of {@link RawItemContributor}. The results are merged into one list. Fields not provided by all contributors are
 * defaulted to ""
 * 
 * @param <T>
 *           The item model for which the raw item shall be assembled
 */
public abstract class AbstractRawItemBuilder<T extends AbstractItemModel> implements RawItemBuilder<T>
{
	private List<RawItemContributor<T>> contributors = Collections.emptyList();
	private final Set<String> columns = new TreeSet<>();

	@Override
	public List<RawItemContributor<T>> getContributors()
	{
		return contributors;
	}

	@Override
	public Set<String> getColumns()
	{
		if (columns.isEmpty())
		{
			for (final RawItemContributor<T> contributor : getContributors())
			{
				columns.addAll(contributor.getColumns());
			}
		}
		return columns;
	}

	@Override
	public void setContributors(final List<RawItemContributor<T>> contributors)
	{
		this.contributors = contributors;
	}

	@Override
	public List<Map<String, Object>> rowsAsNameValuePairs(final T model)
	{
		final List<Map<String, Object>> allRowsAsNameValue = new ArrayList<>();
		final Set<String> allColumns = getColumns();
		for (final RawItemContributor<T> contributor : contributors)
		{
			final List<Map<String, Object>> rows = contributor.createRows(model);
			for (final Map<String, Object> row : rows)
			{
				for (final String column : allColumns)
				{
					if (row.get(column) == null)
					{
						row.put(column, "");
					}
				}
				allRowsAsNameValue.add(row);
			}
		}
		if (isDebug())
		{
			dumpRowsToLogger(allRowsAsNameValue);
		}
		return allRowsAsNameValue;
	}

	private void dumpRowsToLogger(final List<Map<String, Object>> allRowsAsNameValue)
	{
		final Logger logger = getLogger();
		logger.debug("Created name/value pairs from " + contributors.size() + " contributors");
		for (final Map<String, Object> row : allRowsAsNameValue)
		{
			logger.debug("Row:");
			for (final Entry<String, Object> entry : row.entrySet())
			{
				final Object value = entry.getValue();
				if (value == null || value instanceof String && ((String) value).isEmpty())
				{
					continue;
				}
				logger.debug(entry.getKey() + "=" + value);
			}
		}
	}

	protected boolean isDebug()
	{
		return getLogger().isDebugEnabled();
	}

	protected abstract Logger getLogger();

}
