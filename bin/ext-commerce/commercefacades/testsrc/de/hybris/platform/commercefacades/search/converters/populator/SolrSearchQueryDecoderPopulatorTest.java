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
import de.hybris.platform.commercefacades.search.solrfacetsearch.converters.populator.SolrSearchQueryDecoderPopulator;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;


@UnitTest
public class SolrSearchQueryDecoderPopulatorTest
{
	protected static final String FREE_TEXT_SEARCH = "free text search";
	protected static final String SORT = "sort";

	private AbstractPopulatingConverter<SearchQueryData, SolrSearchQueryData> solrSearchQueryDecoder =
				new ConverterFactory<SearchQueryData, SolrSearchQueryData, SolrSearchQueryDecoderPopulator>().create(SolrSearchQueryData.class, new SolrSearchQueryDecoderPopulator());

	@Before
	public void setUp()
	{
		//Do Nothing
	}

	@Test
	public void testConvertNull()
	{
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(null);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertNull(result.getFreeTextSearch());
		Assert.assertNull(result.getSort());
		Assert.assertNull(result.getFilterTerms());
	}

	@Test
	public void testConvertEmpty()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue("");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertNull(result.getFreeTextSearch());
		Assert.assertNull(result.getSort());
		Assert.assertNull(result.getFilterTerms());
	}

	@Test
	public void testConvertWord()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH);
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals(FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertNull(result.getSort());
		Assert.assertTrue(result.getFilterTerms().isEmpty());
	}

	@Test
	public void testConvertWord2()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH + ":");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals(FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertNull(result.getSort());
		Assert.assertTrue(result.getFilterTerms().isEmpty());
	}

	@Test
	public void testConvertSort()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(":" + SORT);
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals("", result.getFreeTextSearch());
		Assert.assertEquals(SORT, result.getSort());
		Assert.assertTrue(result.getFilterTerms().isEmpty());
	}

	@Test
	public void testConvertWordSort()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH + ":" + SORT);
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals(FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertEquals(SORT, result.getSort());
		Assert.assertTrue(result.getFilterTerms().isEmpty());
	}

	@Test
	public void testConvertFilterTerm()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue("::key1:value1");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals("", result.getFreeTextSearch());
		Assert.assertEquals("", result.getSort());
		Assert.assertNotNull(result.getFilterTerms());
		Assert.assertEquals(1, result.getFilterTerms().size());
		Assert.assertEquals("key1", result.getFilterTerms().get(0).getKey());
		Assert.assertEquals("value1", result.getFilterTerms().get(0).getValue());
	}

	@Test
	public void testConvertFilterTerm2()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue("::key1:value1:key2:value2");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals("", result.getFreeTextSearch());
		Assert.assertEquals("", result.getSort());
		Assert.assertNotNull(result.getFilterTerms());
		Assert.assertEquals(2, result.getFilterTerms().size());
		Assert.assertEquals("key1", result.getFilterTerms().get(0).getKey());
		Assert.assertEquals("value1", result.getFilterTerms().get(0).getValue());
		Assert.assertEquals("key2", result.getFilterTerms().get(1).getKey());
		Assert.assertEquals("value2", result.getFilterTerms().get(1).getValue());
	}

	@Test
	public void testConvertAll()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH + ":" + SORT + ":key1:value1:key2:value2");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals(FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertEquals(SORT, result.getSort());
		Assert.assertNotNull(result.getFilterTerms());
		Assert.assertEquals(2, result.getFilterTerms().size());
		Assert.assertEquals("key1", result.getFilterTerms().get(0).getKey());
		Assert.assertEquals("value1", result.getFilterTerms().get(0).getValue());
		Assert.assertEquals("key2", result.getFilterTerms().get(1).getKey());
		Assert.assertEquals("value2", result.getFilterTerms().get(1).getValue());
	}
}
