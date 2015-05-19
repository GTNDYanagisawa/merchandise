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
 */
package de.hybris.platform.ycommercewebservices.queues.impl;

import de.hybris.platform.ycommercewebservices.queues.UpdateQueue;

import java.util.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;


/**
 * Abstract implementation of {@link de.hybris.platform.ycommercewebservices.queues.UpdateQueue} using {@link TreeMap}
 * for storing elements WARNING: this queue has limited capacity due to its in-memory nature
 */
public abstract class AbstractUpdateQueue<T> extends TreeMap<Long, T> implements UpdateQueue<T>
{
	private int maxCapacity = 1000;

	@Override
	public List<T> getItems()
	{
		return Lists.newArrayList(values());
	}

	@Override
	public List<T> getItems(final Date newerThan)
	{
		return Lists.newArrayList(tailMap(Long.valueOf(newerThan.getTime())).values());
	}

	@Override
	public void addItem(final T item)
	{
		if (size() < maxCapacity)
		{
			Long timeKey = getTimeKey(item);
			while (containsKey(timeKey))
			{
				timeKey = Long.valueOf(timeKey.longValue() + 1);
			}
			put(timeKey, item);
		}
	}

	@Override
	public void addItems(final List<T> items)
	{
		for (final T item : items)
		{
			addItem(item);
		}
	}

	@Override
	public void removeItems(final Date olderThan)
	{
		final SortedMap<Long, T> clone = (SortedMap<Long, T>) clone();
		final SortedMap<Long, T> newerThan = clone.tailMap(Long.valueOf(olderThan.getTime()));
		clear();
		putAll(newerThan);
	}

	@Override
	public void removeItems()
	{
		clear();
	}

	@Override
	public void removeItems(final Predicate<T> predicate)
	{
		final Iterator<T> it = values().iterator();
		while (it.hasNext())
		{
			if (predicate.apply(it.next()))
			{
				it.remove();
			}
		}
	}

	@Override
	public T getLastItem()
	{
		T ret = null;
		if (!isEmpty())
		{
			ret = lastEntry().getValue();
		}
		return ret;
	}

	public int getMaxCapacity()
	{
		return maxCapacity;
	}

	public void setMaxCapacity(final int maxCapacity)
	{
		this.maxCapacity = maxCapacity;
	}

	protected Long getTimeKey(final T item)
	{
		return Long.valueOf(System.currentTimeMillis());
	}

}
