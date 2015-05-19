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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;

import java.util.Collection;
import java.util.Locale;

import org.junit.Test;


/**
 * Test class concentrates on searching for particular indexed properties alone, or joined together with OR and AND
 * conjunctions
 */
public class SearchInFieldTest extends AbstractSolrIntegrationTest
{
	final static String HP = "Hewlett-Packard";
	final static String EIZO = "EIZO";

	@Override
	protected void prepareIndexForTest() throws Exception
	{
		dropIndex();
	}

	/**
	 * Given a product with code = HW2300-2356 in hwcatalog and with hwcatalog selected then a searchInField query must
	 * yield a result of size 1 containing the producht with HW2300-2356 for catalog hwcatalog
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchInFieldForCode() throws Exception
	{
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final ProductModel expectedProduct = productService.getProductForCode(hwOnline, "HW2300-2356");
		indexerService.performFullIndex(facetSearchConfig);

		query.searchInField("code", "HW2300-2356");
		query.setCatalogVersion(hwOnline);
		final SearchResult result = facetSearchService.search(query);
		final Collection<ProductModel> products = (Collection<ProductModel>) result.getResults();
		assertTrue("Only one product HW2300-2356 for hwcatalog/Online should be found", products.size() == 1);
		assertEquals("Product HW2300-2356 for hwcatalog/Online was not properly indexed. Could not be found", products.iterator()
				.next(), expectedProduct);

	}

	/**
	 * 
	 * Given products with name = 'Sony DSC-P200 CYBER-SHOT silber_online_en' for language en in hwcatalog and with
	 * hwcatalog selected and with lanugage en selected then a searchInField query must yield a result only containing
	 * products with the given name for langauge en and catalog hwcatalog
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchInFieldForName() throws Exception
	{
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final ProductModel expectedProduct = productService.getProduct(hwOnline, "HW1230-0200");
		indexerService.performFullIndex(facetSearchConfig);


		query.searchInField("name", "Sony DSC-P200 CYBER-SHOT silber_online_en");
		query.setCatalogVersion(hwOnline);
		query.setLanguage("en");
		final SearchResult result = facetSearchService.search(query);
		final Collection<ProductModel> products = (Collection<ProductModel>) result.getResults();
		assertTrue("At least one product should be matched by name", products.size() >= 1);
		boolean foundExpected = false;
		for (final ProductModel product : products)
		{
			foundExpected = product.equals(expectedProduct);
			if (foundExpected)
			{
				break;
			}
		}
		assertTrue("Expected product is not in the result set", foundExpected);

	}

	/**
	 * Given products with name = 'Sony DSC-P200 CYBER-SHOT silber_online_en' for language en in hwcatalog and no
	 * products with name 'Sony DSC-P200 CYBER-SHOT silber_online_en' for language fr in hwcatalog and with hwcatalog
	 * selected and with lanugage fr selected then a searchInField query should yield an empty result
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchForNameInAnotherLanguage() throws Exception
	{
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		indexerService.performFullIndex(facetSearchConfig);
		query.searchInField("name", "Sony DSC-P200 CYBER-SHOT silber_online_en");
		query.setCatalogVersion(hwOnline);
		query.setLanguage("fr");
		SearchResult result = facetSearchService.search(query);
		assertTrue("No products should be matched by french name", result.getResults().isEmpty());
		query.setLanguage("en");
		result = facetSearchService.search(query);
		assertFalse("At least one product should be matched by english name", result.getResults().isEmpty());
	}

	/**
	 * Given product 'HW1240-1732' with manufacturerName = 'Hewlett-Packard' in hwcatalog and another product
	 * 'HW1100-0024' with manufacturerName = 'EIZO' in hwcatalog and with hwcatalog selected then a searchInField for
	 * manufacturerName = 'Hewlett-Packard' OR manufacturerName = 'EIZO' should yield those two products and all products
	 * with manufacturerName = 'Hewlett-Packard' or 'EIZO'
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchInFieldWithOrConjunction() throws Exception
	{

		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final ProductModel expProd1 = productService.getProductForCode(hwOnline, "HW1240-1732");
		final ProductModel expProd2 = productService.getProductForCode(hwOnline, "HW1100-0024");


		indexerService.performFullIndex(facetSearchConfig);

		query.searchInField("manufacturerName", HP);
		query.searchInField("manufacturerName", EIZO, Operator.OR);
		query.setCatalogVersion(hwOnline);
		query.setLanguage("en");
		final SearchResult result = facetSearchService.search(query);
		final Collection<ProductModel> products = (Collection<ProductModel>) result.getResults();
		boolean found1 = false;
		boolean found2 = false;

		for (final ProductModel product : products)
		{

			assertTrue("Product's manufacturer is  neither '" + HP + "' nor '" + EIZO + "'", HP
					.equals(product.getManufacturerName())
					|| EIZO.equals(product.getManufacturerName()));

			if (product.equals(expProd1))
			{
				found1 = true;
				continue;
			}
			if (product.equals(expProd2))
			{
				found2 = true;
			}

		}
		assertTrue("Search must contain products HW2310-1005 and HW2310-1007", found1 && found2);

	}

	/**
	 * Given products with manufacturerName = 'XFX' in hwcatalog and the products with name like 'GeForce*online_en' for
	 * language en in hwcatalog and with hwcatalog selected and with language en selected then a searchInField for
	 * manufacturerName = 'XFX' AND name = 'GeForce' should yield a result with products with manufacturerName = 'XFX'
	 * AND name containing 'GeForce'
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchInFieldWithAndConjunction() throws Exception
	{
		final String expectedManufacturer = "XFX";
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		indexerService.performFullIndex(facetSearchConfig);
		query.searchInField("name", "GeForce", Operator.AND);
		query.addFacetValue("manufacturerName", "XFX");
		query.setCatalogVersion(hwOnline);
		query.setLanguage("en");
		final SearchResult result = facetSearchService.search(query);
		final Collection<? extends ItemModel> items = result.getResults();
		for (final ItemModel item : items)
		{
			assertTrue("Products only are expected in the search results", item instanceof ProductModel);
			final ProductModel product = (ProductModel) item;
			//it is either 'HW2310-1004' or contains 'Chaintech GeForce' in english name:
			assertEquals("Products manufactured by " + expectedManufacturer + " only are expected", expectedManufacturer, product
					.getManufacturerName());
			final String englishName = product.getName(Locale.ENGLISH);
			assertTrue("English name of product should contain 'GeForce'", englishName.contains("GeForce"));
			//special marker in the test data
			assertTrue(englishName + " is not a english name", englishName.contains("online_en"));
		}
	}



}
