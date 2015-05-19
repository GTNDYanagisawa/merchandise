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
package de.hybris.platform.solrfacetsearch.config.impl;

import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

import java.util.Comparator;

import javax.annotation.Resource;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class DefaultFacetSortProviderTest extends ServicelayerTest
{

	@Resource(name = "facetCountComparator")
	private Comparator<FacetValue> scoreComparator;

	@Resource(name = "facetDisplayNameComparator")
	private Comparator<FacetValue> displayNameComparator;

	private DefaultFacetSortProvider sortProvider;

	@Mock
	private IndexedType indexedType;

	@Mock
	private IndexedProperty indexedProperty;

	private FacetValue facetValueA;
	private FacetValue facetValueB;

	@Before
	public void setUp()
	{
		sortProvider = new DefaultFacetSortProvider();
		MockitoAnnotations.initMocks(this);
		facetValueA = new FacetValue("Z", "theA", 30, false);
		facetValueB = new FacetValue("W", "theB", 20, false);
	}

	@Test
	public void testSortByScore()
	{
		sortProvider.setComparator(scoreComparator);

		sortProvider.setDescending(false);
		Assertions.assertThat(
				sortProvider.getComparatorForTypeAndProperty(indexedType, indexedProperty).compare(facetValueA, facetValueB))
				.isPositive();

		sortProvider.setDescending(true);
		Assertions.assertThat(
				sortProvider.getComparatorForTypeAndProperty(indexedType, indexedProperty).compare(facetValueA, facetValueB))
				.isNegative();
	}

	@Test
	public void testSortByDisplayName()
	{
		sortProvider.setComparator(displayNameComparator);

		sortProvider.setDescending(false);
		Assertions.assertThat(
				sortProvider.getComparatorForTypeAndProperty(indexedType, indexedProperty).compare(facetValueA, facetValueB))
				.isNegative();

		sortProvider.setDescending(true);
		Assertions.assertThat(
				sortProvider.getComparatorForTypeAndProperty(indexedType, indexedProperty).compare(facetValueA, facetValueB))
				.isPositive();
	}

	@Test
	public void testCustomSorting()
	{
		final Comparator<FacetValue> customComparator = new Comparator<FacetValue>()
		{

			@Override
			public int compare(final FacetValue o1, final FacetValue o2)
			{
				return o1.getName().compareTo(o2.getName());
			}

		};
		sortProvider.setComparator(customComparator);

		sortProvider.setDescending(false);
		Assertions.assertThat(
				sortProvider.getComparatorForTypeAndProperty(indexedType, indexedProperty).compare(facetValueA, facetValueB))
				.isPositive();

		sortProvider.setDescending(true);
		Assertions.assertThat(
				sortProvider.getComparatorForTypeAndProperty(indexedType, indexedProperty).compare(facetValueA, facetValueB))
				.isNegative();
	}



}
