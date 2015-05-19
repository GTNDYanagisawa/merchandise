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
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperties;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.ValueRange;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeSetModel;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;


/**
 * 
 */
public class AllAsItemsTest extends AbstractSolrIntegrationTest
{

	private SolrValueRangeSetModel priceRangesEUR;
	private SolrValueRangeSetModel priceRangesUSD;

	/**
	 * This Test performs similar actions as FacetDrillDown.testFacetLocalized, however it uses configuration based upon
	 * items - not xml configuration file.
	 */
	@Test
	public void testFacetLocalized() throws Exception
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		query.setCatalogVersions(Collections.singletonList(catalogVersion));
		query.setLanguage("de");
		query.clearOrderFields();
		query.addFacetValue("manufacturerName", "Intel");

		final SearchResult result1 = facetSearchService.search(query);
		final Collection<ProductModel> products1 = checkProductCollection(catalogVersion, result1.getResults());
		checkProductsManufacturer(products1, "Intel");

		query.addFacetValue("categoryName", "Motherboards_online_de");
		final SearchResult result2 = facetSearchService.search(query);
		final Collection<ProductModel> products2 = checkProductCollection(catalogVersion, result2.getResults());
		checkProductsCategory(products2, "Motherboards_online_de");


	}

	@Test
	public void testFacetSearchConfigQualifiedRanges() throws Exception
	{
		final FacetSearchConfig config = this.facetSearchConfig;
		assertNotNull("Config must not be null", config);
		assertNotNull("IndexedType must not be null", indexedType);
		final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get("priceWithCurrency");
		assertNotNull("Indexed Property must not be null", indexedProperty);
		final List<ValueRange> eurValueRanges = IndexedProperties.getValueRanges(indexedProperty, "EUR");
		assertNotNull("EUR value ranges must not be null", eurValueRanges);
		assertEquals("Number of ranges for EUR", 2, eurValueRanges.size());
		final ValueRange eurValueRange1 = eurValueRanges.get(0);
		assertEquals("Name of EUR range", "1-2000", eurValueRange1.getName());
		assertEquals("Start of EUR range", Double.valueOf(1.0), eurValueRange1.getFrom());
		assertEquals("End of EUR range", Double.valueOf(2000), eurValueRange1.getTo());
		final ValueRange eurValueRange2 = eurValueRanges.get(1);
		assertEquals("Name of EUR range", "2001-INF", eurValueRange2.getName());
		assertEquals("Start of EUR range", Double.valueOf(2001), eurValueRange2.getFrom());
		assertEquals("End of EUR range", null, eurValueRange2.getTo());
		final List<ValueRange> usdValueRanges = IndexedProperties.getValueRanges(indexedProperty, "USD");
		assertNotNull("USD value ranges must not be null", usdValueRanges);
		assertEquals("Number of ranges for USD", 2, usdValueRanges.size());
		final ValueRange usdValueRange1 = usdValueRanges.get(0);
		assertEquals("Name of USD range", "1-3000", usdValueRange1.getName());
		assertEquals("Start of USD range", Double.valueOf(1.0), usdValueRange1.getFrom());
		assertEquals("End of USD range", Double.valueOf(3000.0), usdValueRange1.getTo());
		final ValueRange usdValueRange2 = usdValueRanges.get(1);
		assertEquals("Name of USD range", "3001-INF", usdValueRange2.getName());
		assertEquals("Start of USD range", Double.valueOf(3001), usdValueRange2.getFrom());
		assertEquals("End of USD range", null, usdValueRange2.getTo());

	}

	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{
		final List<SolrIndexedPropertyModel> superProperties = new ArrayList<SolrIndexedPropertyModel>(super.setUpIndexProperties());

		final SolrIndexedPropertyModel priceWithCurrencyProperty = modelService.create(SolrIndexedPropertyModel.class);
		priceWithCurrencyProperty.setFacet(true);
		priceWithCurrencyProperty.setName("priceWithCurrency");
		priceWithCurrencyProperty.setType(SolrPropertiesTypes.DOUBLE);
		priceWithCurrencyProperty.setFieldValueProvider("productPriceValueProvider");
		priceWithCurrencyProperty.setRangeSets(Arrays.asList(priceRangesEUR, priceRangesUSD));
		priceWithCurrencyProperty.setCurrency(true);

		superProperties.add(priceWithCurrencyProperty);

		return superProperties;
	}

	@Override
	protected List<SolrValueRangeSetModel> setUpValueRanges()
	{

		priceRangesEUR = modelService.create(SolrValueRangeSetModel.class);
		priceRangesEUR.setName("priceRangesEUR");
		priceRangesEUR.setType("double");
		priceRangesEUR.setQualifier("EUR");

		final List<SolrValueRangeModel> priceEURValueRanges = new ArrayList<SolrValueRangeModel>();
		priceEURValueRanges.add(setUpSingleRangeForRangeSet(priceRangesEUR, "1-2000", "1", "2000"));
		priceEURValueRanges.add(setUpSingleRangeForRangeSet(priceRangesEUR, "2001-INF", "2001", null));
		priceRangesEUR.setSolrValueRanges(priceEURValueRanges);


		priceRangesUSD = modelService.create(SolrValueRangeSetModel.class);
		priceRangesUSD.setName("priceRangesUSD");
		priceRangesUSD.setType("double");
		priceRangesUSD.setQualifier("USD");


		final List<SolrValueRangeModel> priceUSDValueRanges = new ArrayList<SolrValueRangeModel>();
		priceUSDValueRanges.add(setUpSingleRangeForRangeSet(priceRangesUSD, "1-3000", "1", "3000"));
		priceUSDValueRanges.add(setUpSingleRangeForRangeSet(priceRangesUSD, "3001-INF", "3001", null));
		priceRangesUSD.setSolrValueRanges(priceUSDValueRanges);
		final List<SolrValueRangeSetModel> rangeSets = new ArrayList<SolrValueRangeSetModel>(super.setUpValueRanges());
		rangeSets.add(priceRangesEUR);
		rangeSets.add(priceRangesUSD);

		return rangeSets;
	}



}
