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
package de.hybris.platform.commercefacades.search.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.solrfacetsearch.converters.populator.SolrSearchQueryEncoderPopulator;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;


@UnitTest
public class SolrSearchQueryEncoderPopulatorTest
{
	protected static final String FREE_TEXT_SEARCH = "free text search";
	protected static final String SORT = "sort";

	private AbstractPopulatingConverter<SolrSearchQueryData, SearchQueryData> solrSearchQueryEncoder =
				new ConverterFactory<SolrSearchQueryData, SearchQueryData, SolrSearchQueryEncoderPopulator>().create(SearchQueryData.class, new SolrSearchQueryEncoderPopulator());

	@Before
	public void setUp()
	{
		//Do Nothing
	}

	@Test
	public void testConvertNull()
	{
		final SearchQueryData result = solrSearchQueryEncoder.convert(null);
		Assert.assertEquals("", result.getValue());
	}

	@Test
	public void testConvertEmpty()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("", result.getValue());
	}

	@Test
	public void testConvertEmptyTerms()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setFilterTerms(Collections.<SolrSearchQueryTermData> emptyList());
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("", result.getValue());
	}

	@Test
	public void testConvertEmptyTerms2()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData searchQueryTermData = new SolrSearchQueryTermData();
		searchQueryData.setFilterTerms(Collections.singletonList(searchQueryTermData));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("", result.getValue());
	}

	@Test
	public void testConvertFreeText()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setFreeTextSearch(FREE_TEXT_SEARCH);
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals(FREE_TEXT_SEARCH + ":", result.getValue());
	}

	@Test
	public void testConvertSort()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setSort(SORT);
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals(":" + SORT, result.getValue());
	}

	@Test
	public void testConvertTerms1()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData searchQueryTermData = new SolrSearchQueryTermData();
		searchQueryTermData.setKey("key1");
		searchQueryTermData.setValue("value1");
		searchQueryData.setFilterTerms(Collections.singletonList(searchQueryTermData));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("::key1:value1", result.getValue());
	}

	@Test
	public void testConvertTerms2()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData searchQueryTermData1 = new SolrSearchQueryTermData();
		searchQueryTermData1.setKey("key1");
		searchQueryTermData1.setValue("value1");
		final SolrSearchQueryTermData searchQueryTermData2 = new SolrSearchQueryTermData();
		searchQueryTermData2.setKey("key2");
		searchQueryTermData2.setValue("value2");

		searchQueryData.setFilterTerms(Arrays.asList(searchQueryTermData1, searchQueryTermData2));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("::key1:value1:key2:value2", result.getValue());
	}

	@Test
	public void testConvertAll()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setFreeTextSearch(FREE_TEXT_SEARCH);
		searchQueryData.setSort(SORT);
		final SolrSearchQueryTermData searchQueryTermData1 = new SolrSearchQueryTermData();
		searchQueryTermData1.setKey("key1");
		searchQueryTermData1.setValue("value1");
		final SolrSearchQueryTermData searchQueryTermData2 = new SolrSearchQueryTermData();
		searchQueryTermData2.setKey("key2");
		searchQueryTermData2.setValue("value2");

		searchQueryData.setFilterTerms(Arrays.asList(searchQueryTermData1, searchQueryTermData2));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals(FREE_TEXT_SEARCH + ":" + SORT + ":key1:value1:key2:value2", result.getValue());
	}
}
