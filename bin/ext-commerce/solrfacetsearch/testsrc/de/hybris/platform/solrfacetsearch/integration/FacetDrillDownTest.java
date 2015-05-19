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

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperties;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.ValueRange;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;


/**
 * Tests the drill down of facets, i.e. for each selected facet value the remaining facet count should be reduced. This
 * also should work
 */
public class FacetDrillDownTest extends AbstractSolrIntegrationTest
{

	private static final String MANUFACTURER_NAME = "Intel";
	private static final String PRICE_RANGE = "101-200";
	private static final String CATEGORY_NAME = "Motherboards_online_de";
	private static final String CATEGORY2_NAME = "Mainboards";
	private static final String FULL_TEXT_VALUE = "graphics";


	@Resource
	protected PriceService priceService;

	@Resource
	protected CategoryService categoryService;

	/**
	 * Given products with manufacturerName = "Intel" in hwcatalog and with a certain facet count<br/>
	 * and with hwcatalog selected<br/>
	 * and with facet value manufacturerName set to "Intel"<br/>
	 * then a search yields products only with manufacturerName = <manufacturerName> and the facet count reduced to
	 * reflect this
	 */
	@Test
	public void testFacetNonLocalized() throws Exception
	{
		query.setCatalogVersion(hwOnline);
		query.clearOrderFields();
		query.addOrderField("name", true);

		final SearchResult result1 = facetSearchService.search(query);
		assertTrue("query1 must return results", result1.getTotalNumberOfResults() > 0);
		final Collection<ProductModel> products1 = checkProductCollection(hwOnline, result1.getResults());
		checkOrderByName(products1);
		checkFacets(result1);

		query.addFacetValue("manufacturerName", MANUFACTURER_NAME);
		final SearchResult result2 = facetSearchService.search(query);
		assertNotNull("Result must not be null", result2);
		assertTrue("query2 must return results", result2.getTotalNumberOfResults() > 0);
		final Collection<ProductModel> products2 = checkProductCollection(hwOnline, result2.getResults());
		checkOrderByName(products2);
		for (final ProductModel product : products2)
		{
			assertEquals("Manufacturer name", MANUFACTURER_NAME, product.getManufacturerName());
		}
		checkFacets(result2);
		compareFacets(result1, result2);
	}

	private void checkOrderByName(final Collection<ProductModel> products)
	{
		String previousName = "";
		for (final ProductModel product : products)
		{
			assertTrue(
					String.format("Products not sorted by 'name'. '%s' should come before '%s' ", previousName, product.getName()),
					previousName.compareToIgnoreCase(product.getName()) <= 0);
			previousName = product.getName();
		}
	}

	private void checkOrderByManufacturerName(final Collection<ProductModel> products)
	{
		String previousName = "";
		for (final ProductModel product : products)
		{
			assertTrue(
					String.format("Products not sorted by 'manufacturerName'. '%s' should come before '%s' ", previousName,
							product.getManufacturerName()), previousName.compareToIgnoreCase(product.getManufacturerName()) <= 0);
			previousName = product.getManufacturerName();
		}
	}

	/**
	 * Given products with categoryName = "Motherboards_online_de" in hwcatalog and language de and with a certain facet
	 * count<br/>
	 * and with hwcatalog selected<br/>
	 * and with language de selected<br/>
	 * and with facet value categoryName set to "Motherboards_online_de"<br/>
	 * then a search yields products only with categoryName = "Motherboards_online_de" and the facet count reduced to
	 * reflect this
	 */
	@Test
	public void testFacetLocalized() throws Exception
	{

		query.setCatalogVersion(hwOnline);
		query.setLanguage("de");
		query.clearOrderFields();
		query.addOrderField("manufacturerName", true);

		final SearchResult result1 = facetSearchService.search(query);
		assertTrue("query1 must return results", result1.getTotalNumberOfResults() > 0);
		final Collection<ProductModel> products1 = checkProductCollection(hwOnline, result1.getResults());
		checkOrderByManufacturerName(products1);
		checkFacets(result1);

		query.addFacetValue("categoryName", CATEGORY_NAME);
		final SearchResult result2 = facetSearchService.search(query);
		assertTrue("query2 must return results", result2.getTotalNumberOfResults() > 0);
		final Collection<ProductModel> products2 = checkProductCollection(hwOnline, result2.getResults());
		checkOrderByManufacturerName(products2);
		for (final ProductModel product : products2)
		{
			final Collection<CategoryModel> categories = product.getSupercategories();
			boolean catFound = false;
			for (final CategoryModel category : categories)
			{
				if (CATEGORY_NAME.equals(category.getName()))
				{
					catFound = true;
					break;
				}
			}
			assertTrue("Category not found", catFound);
		}

		checkFacets(result2);
		compareFacets(result1, result2);
	}

	/**
	 * Given product in a price range = "101-200" and "graphics" in description, residing in hwcatalog and with a certain
	 * facet count<br/>
	 * and with hwcatalog selected<br/>
	 * and with facet price set to "101-200"<br/>
	 * then when searching in description for the string "graphics" the the resulting products must be in the specified
	 * price range and and the facet count must be reduced to reflect this
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFacetWithSearch() throws Exception
	{
		query.setCatalogVersion(hwOnline);
		query.setLanguage("en");

		final SearchResult result1 = facetSearchService.search(query);
		assertTrue("query1 must return results", result1.getTotalNumberOfResults() > 0);
		checkProductCollection(hwOnline, result1.getResults());
		checkFacets(result1);

		query.addFacetValue("price", PRICE_RANGE);
		final SearchResult result2 = facetSearchService.search(query);
		assertNotNull("Result must not be null", result2);
		assertTrue("query2 must return results", result2.getTotalNumberOfResults() > 0);
		final Collection<ProductModel> products2 = checkProductCollection(hwOnline, result2.getResults());
		for (final ProductModel product : products2)
		{
			checkPrice(product, 101, 200);
		}
		checkFacets(result2);
		compareFacets(result1, result2);


		query.search(FULL_TEXT_VALUE);
		final SearchResult result3 = facetSearchService.search(query);
		assertNotNull("Result must not be null", result3);
		assertTrue("query3 must return results", result3.getTotalNumberOfResults() > 0);
		final Collection<ProductModel> products3 = checkProductCollection(hwOnline, result3.getResults());
		for (final ProductModel product : products3)
		{
			checkPrice(product, 101, 200);
			final String description = product.getDescription(Locale.ENGLISH);
			assertNotNull("Product must have a description", description);
			assertTrue("Description must contain string 'graphics'", description.toLowerCase().contains("graphics"));
		}
		checkFacets(result3);
		compareFacets(result2, result3);
	}

	private void checkPrice(final ProductModel product, final int min, final int max)
	{
		final List<PriceInformation> prices = priceService.getPriceInformationsForProduct(product);
		final PriceInformation price = prices.get(0);
		final double value = price.getPriceValue().getValue();
		assertTrue(String.format("Price must be between %d and %d", Integer.valueOf(min), Integer.valueOf(max)), value >= min
				&& value <= max);
	}

	/**
	 * Given product in a price range = "101-200" in hwcatalog and with a certain facet count<br/>
	 * and with hwcatalog selected<br/>
	 * and with facet price set to "101-200" then the the resulting products must be in the specified price range and the
	 * facet count mustbe reduced to reflect this
	 */
	@Test
	public void testRangedFacet() throws Exception
	{
		query.setCatalogVersion(hwOnline);

		final SearchResult result1 = facetSearchService.search(query);
		assertTrue("query1 must return results", result1.getTotalNumberOfResults() > 0);
		checkProductCollection(hwOnline, result1.getResults());
		checkFacets(result1);

		query.addFacetValue("price", PRICE_RANGE);
		final SearchResult result2 = facetSearchService.search(query);
		assertNotNull("Result must not be null", result2);
		assertTrue("query2 must return results", result2.getTotalNumberOfResults() > 0);
		final Collection<ProductModel> products2 = checkProductCollection(hwOnline, result2.getResults());
		for (final ProductModel product : products2)
		{
			checkPrice(product, 101, 200);
		}
		checkFacets(result2);
		compareFacets(result1, result2);

	}

	/**
	 * Given products with manufacturerName = "Intel", categoryName = "Motherboards_online_de" in language de, in
	 * hwcatalog and with a certain facet count and with hwcatalog selected<br/>
	 * and with facet value manufacturerName set "Intel"<br/>
	 * and with facet value categoryName set to "Motherboards_online_de"<br/>
	 * then a search yields products only with manufacturerName = "Intel" and the facet count reduced to reflect this,
	 * with the facet count lower when the second facet is selected
	 */
	@Test
	public void testFacetDrillDown() throws Exception
	{
		query.setCatalogVersion(hwOnline);
		query.setLanguage("de");

		final SearchResult result1 = facetSearchService.search(query);
		assertTrue("query1 must return results", result1.getTotalNumberOfResults() > 0);
		checkProductCollection(hwOnline, result1.getResults());
		checkFacets(result1);

		query.addFacetValue("manufacturerName", MANUFACTURER_NAME);
		final SearchResult result2 = facetSearchService.search(query);
		assertNotNull("Result must not be null", result2);
		assertTrue("query2 must return results", result2.getTotalNumberOfResults() > 0);
		final Collection<ProductModel> products2 = checkProductCollection(hwOnline, result2.getResults());
		for (final ProductModel product : products2)
		{
			assertEquals("Manufacturer name", MANUFACTURER_NAME, product.getManufacturerName());
		}
		checkFacets(result2);
		compareFacets(result1, result2);

		query.addFacetValue("categoryName", CATEGORY2_NAME);
		final SearchResult result3 = facetSearchService.search(query);
		assertTrue("query3 must return results", result3.getTotalNumberOfResults() > 0);
		final Collection<ProductModel> products3 = checkProductCollection(hwOnline, result3.getResults());
		for (final ProductModel product : products3)
		{
			assertEquals("Manufacturer name", MANUFACTURER_NAME, product.getManufacturerName());
			assertTrue("Category not found", isSuperCategory(product, CATEGORY2_NAME));
		}

		checkFacets(result3);
		compareFacets(result2, result3);
	}

	protected boolean isSuperCategory(final ProductModel product, final String categoryName)
	{
		for (final CategoryModel category : product.getSupercategories())
		{
			if (isSuperCategory(category, categoryName))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isSuperCategory(final CategoryModel category, final String categoryName)
	{
		if (categoryName.equals(category.getName()))
		{
			return true;
		}

		for (final CategoryModel superCategory : category.getSupercategories())
		{
			if (isSuperCategory(superCategory, categoryName))
			{
				return true;
			}
		}
		return false;
	}

	private void checkFacets(final SearchResult result)
	{
		final List<Facet> facets3 = result.getFacets();
		assertFalse("Facets collection must not be empty", facets3.isEmpty());
		assertTrue("Result not contain facet price", result.containsFacet("price"));
		final Facet facet = result.getFacet("price");
		final List<String> facetValues = (List<String>) CollectionUtils.collect(facet.getFacetValues(),
				new BeanToPropertyValueTransformer("name"), new ArrayList<String>());
		final IndexedProperty priceProperty = indexedType.getIndexedProperties().get("price");
		assertNotNull("No such indexed property: 'price'", priceProperty);
		final List<ValueRange> priceRanges = IndexedProperties.getValueRanges(priceProperty, null);
		assertNotNull("No priceRanges found", priceRanges);
		assertFalse("Price ranges must not be empty", priceRanges.isEmpty());
		final List<String> ranngeValues = (List<String>) CollectionUtils.collect(priceRanges, new BeanToPropertyValueTransformer(
				"name"), new ArrayList<String>());
		final Iterator<String> facetValueItr = facetValues.iterator();
		final Iterator<String> rangeValueItr = ranngeValues.iterator();
		while (rangeValueItr.hasNext())
		{
			final String rangeValue = rangeValueItr.next();
			if (facetValues.contains(rangeValue))
			{
				assertTrue("No more facet values", facetValueItr.hasNext());
				final String facetValue = facetValueItr.next();
				assertEquals("Facet value", rangeValue, facetValue);
			}
		}
		assertTrue("Result not contain facet manufacturerName", result.containsFacet("manufacturerName"));
		//assertTrue("Result not contain facet processor", result.containsFacet("processor"));
		assertTrue("Result not contain facet categoryName", result.containsFacet("categoryName"));
	}

	private void compareFacets(final SearchResult result1, final SearchResult result2)
	{
		for (final Facet facet1 : result1.getFacets())
		{
			final Facet facet2 = result2.getFacet(facet1.getName());
			if (facet2 != null)
			{
				final Collection<FacetValue> facetValues1 = facet1.getFacetValues();
				for (final FacetValue facetValue1 : facetValues1)
				{
					long facetValue2Count = 0;
					final FacetValue facetValue2 = facet2.getFacetValue(facetValue1.getName());
					if (facetValue2 != null)
					{
						facetValue2Count = facetValue2.getCount();
					}
					final long facetValue1Count = facetValue1.getCount();
					assertTrue("Facet value count of " + facet1.getName() + "/" + facet1.getName()
							+ " is expected to be less than or equal to " + facetValue1Count, facetValue2Count <= facetValue1Count);
				}
			}
		}
	}

}
