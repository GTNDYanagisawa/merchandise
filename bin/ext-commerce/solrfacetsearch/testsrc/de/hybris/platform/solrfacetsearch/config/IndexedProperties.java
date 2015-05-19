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
package de.hybris.platform.solrfacetsearch.config;

import de.hybris.platform.core.Registry;
import de.hybris.platform.solrfacetsearch.provider.FacetDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;


/**
 *
 */
public class IndexedProperties
{
	static private final String DEFAULT_RANGE_SET_QUALIFIER = "default";

	public static boolean isRanged(final IndexedProperty property)
	{
		return !CollectionUtils.isEmpty(property.getValueRangeSets());
	}

	public static FieldValueProvider getFieldValueProvider(final IndexedProperty property)
	{
		final String name = property.getFieldValueProvider();
		return name == null ? null : Registry.getGlobalApplicationContext().getBean(name, FieldValueProvider.class);
	}

	public static FacetDisplayNameProvider getFacetDisplayNameProvider(final IndexedProperty property)
	{
		final String name = property.getFacetDisplayNameProvider();
		return name == null ? null : Registry.getGlobalApplicationContext().getBean(name, FacetDisplayNameProvider.class);
	}

	public static FacetSortProvider getFacetSortProvider(final IndexedProperty property)
	{
		final String name = property.getFacetSortProvider();
		return name == null ? null : Registry.getGlobalApplicationContext().getBean(name, FacetSortProvider.class);
	}

	public static List<ValueRange> getValueRanges(final IndexedProperty property, final String qualifier)
	{
		ValueRangeSet valueRangeSet;
		if (qualifier == null)
		{
			valueRangeSet = property.getValueRangeSets().get(DEFAULT_RANGE_SET_QUALIFIER);
		}
		else
		{
			valueRangeSet = property.getValueRangeSets().get(qualifier);
			if (valueRangeSet == null)
			{
				valueRangeSet = property.getValueRangeSets().get(DEFAULT_RANGE_SET_QUALIFIER);
			}
		}
		if (valueRangeSet != null)
		{
			return valueRangeSet.getValueRanges();
		}
		else
		{
			return Collections.emptyList();
		}
	}
}
