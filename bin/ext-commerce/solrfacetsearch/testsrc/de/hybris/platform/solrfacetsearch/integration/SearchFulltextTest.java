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
package de.hybris.platform.solrfacetsearch.integration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.testframework.TestUtils;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * Tests the full text search function in the given catalog version.
 */
public class SearchFulltextTest extends AbstractSolrIntegrationTest
{

	@Resource
	private I18NService i18nService;

	@Override
	protected void prepareIndexForTest() throws Exception
	{
		dropIndex();
	}

	/**
	 * Creates the index for the hwcatalog_online, sets the language to German, and searches for "canon" in it. There
	 * should be 4 products that can be found, and each contains the keyword "canon", either in the name field or in the
	 * description field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFulltextSearch() throws Exception
	{
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		indexerService.performFullIndex(facetSearchConfig);
		query.setCatalogVersion(hwOnline);
		final String keyword = "canon";
		query.search(keyword);
		query.setLanguage("de");
		final SearchResult result = facetSearchService.search(query);
		assertEquals(4, result.getTotalNumberOfResults());
		final Collection<ProductModel> products = checkProductResults(result.getResults());
		for (final ProductModel p : products)
		{
			final boolean nameFound = (p.getName().toLowerCase().indexOf(keyword) != -1);
			final boolean descFound = (p.getDescription().toLowerCase().indexOf(keyword) != -1);
			assertTrue("text [canon] not found for product [" + p.getName() + "]", nameFound || descFound);
		}
	}

	@Test
	public void testFulltextSearchUsingFallbackLanguage() throws Exception
	{
		final boolean enabledBefore = i18nService.isLocalizationFallbackEnabled();

		try
		{
			final LanguageModel de_DE = modelService.create(LanguageModel.class);
			de_DE.setIsocode("de_DE");
			de_DE.setFallbackLanguages(Collections.singletonList(commonI18NService.getLanguage("de")));
			modelService.save(de_DE);

			i18nService.setLocalizationFallbackEnabled(true);

			catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
			indexerService.performFullIndex(facetSearchConfig);

			query.setCatalogVersion(hwOnline);
			final String keyword = "canon";
			query.search(keyword);
			query.setLanguage(de_DE.getIsocode());

			TestUtils.disableFileAnalyzer("exception expected from SOLR, because we are searching for a non existing field");
			final SearchResult result = facetSearchService.search(query);
			TestUtils.enableFileAnalyzer();

			assertEquals(4, result.getTotalNumberOfResults());
			final Collection<ProductModel> products = checkProductResults(result.getResults());
			for (final ProductModel p : products)
			{
				final boolean nameFound = (p.getName().toLowerCase().indexOf(keyword) != -1);
				final boolean descFound = (p.getDescription().toLowerCase().indexOf(keyword) != -1);
				assertTrue("text [canon] not found for product [" + p.getName() + "]", nameFound || descFound);
			}
		}
		finally
		{
			i18nService.setLocalizationFallbackEnabled(enabledBefore);
		}
	}

	private Collection<ProductModel> checkProductResults(final Collection<? extends ItemModel> items)
	{
		assertNotNull("Items collection must not be null", items);
		assertFalse("Items collection must not be empty", items.isEmpty());
		for (final ItemModel item : items)
		{
			assertTrue("Result item must be of type " + ProductModel.class, item instanceof ProductModel);
		}
		return (Collection<ProductModel>) items;
	}

}
