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

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.solrfacetsearch.enums.SolrIndexedPropertyFacetType;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrIntegrationTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;


/**
 * 
 */
public class RangedIndexedPropertyTest extends AbstractSolrIntegrationTest
{
	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;

	private CatalogVersionModel summer;
	private CatalogVersionModel winter;


	@Override
	protected void setUpBasic() throws Exception
	{
		createCoreData();
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		importCsv("/test/SolrTestThinCatalogImport.csv", "utf-8");
		winter = catalogVersionService.getCatalogVersion("testCatalog1", "Winter");
		summer = catalogVersionService.getCatalogVersion("testCatalog1", "Summer");
	}

	@Override
	protected java.util.List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{
		return Arrays.asList(winter, summer);
	}

	@Override
	protected java.util.List<de.hybris.platform.core.model.c2l.CurrencyModel> setUpCurrencies()
	{
		final CurrencyModel eur = commonI18NService.getCurrency("EUR");
		final CurrencyModel usd = commonI18NService.getCurrency("USD");
		final CurrencyModel chf = commonI18NService.getCurrency("CHF");
		final CurrencyModel gbp = commonI18NService.getCurrency("GBP");

		return Arrays.asList(eur, usd, chf, gbp);
	}

	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{

		final SolrIndexedPropertyModel codeProperty = modelService.create(SolrIndexedPropertyModel.class);
		codeProperty.setName("code");
		codeProperty.setType(SolrPropertiesTypes.TEXT);
		codeProperty.setRangeSet(stringSetModel);

		final SolrIndexedPropertyModel nameProperty = modelService.create(SolrIndexedPropertyModel.class);
		nameProperty.setName("name");
		nameProperty.setType(SolrPropertiesTypes.TEXT);
		nameProperty.setLocalized(true);
		nameProperty.setFacetType(SolrIndexedPropertyFacetType.MULTISELECTOR);
		nameProperty.setRangeSet(stringSetModel);

		final SolrIndexedPropertyModel descriptionProperty = modelService.create(SolrIndexedPropertyModel.class);
		descriptionProperty.setName("description");
		descriptionProperty.setType(SolrPropertiesTypes.TEXT);
		descriptionProperty.setLocalized(true);

		final SolrIndexedPropertyModel priceProperty = modelService.create(SolrIndexedPropertyModel.class);
		priceProperty.setFacet(true);
		priceProperty.setName("price");
		priceProperty.setType(SolrPropertiesTypes.DOUBLE);
		priceProperty.setFieldValueProvider("productPriceValueProvider");
		priceProperty.setRangeSet(priceSetModel);
		priceProperty.setCurrency(true);

		final SolrIndexedPropertyModel onlineDateProperty = modelService.create(SolrIndexedPropertyModel.class);
		onlineDateProperty.setFacet(false);
		onlineDateProperty.setName("onlineDate");
		onlineDateProperty.setType(SolrPropertiesTypes.DATE);
		onlineDateProperty.setRangeSet(dateSetModel);

		return Arrays.asList(codeProperty, nameProperty, descriptionProperty, priceProperty, onlineDateProperty);
	}


	@Override
	protected String getSolrConfigName()
	{
		return "sample SOLR configuration";
	}

	@Test
	public void testModelPropertyValueProvider() throws Exception
	{
		final FacetSearchConfig config = facetSearchConfig;

		final IndexedType productType = indexedType;
		final IndexedProperty name = productType.getIndexedProperties().get("name");

		//check value provider
		final Collection<FieldValue> nameValues = IndexedProperties.getFieldValueProvider(name).getFieldValues(
				config.getIndexConfig(), name, productService.getProductForCode("testProduct2"));
		//testProduct2 has two names: en-> English name and de-> Deutche Name
		Assert.assertEquals("There should be two values for localized property 'name'", 2, nameValues.size());
		for (final FieldValue fieldValue : nameValues)
		{
			if (fieldValue.getFieldName().contains("en"))
			{
				Assert.assertTrue("Values for property 'name' are not as expected", fieldValue.getFieldName()
						.equals("name_en_string"));
				Assert.assertTrue("Values for property 'name' are not as expected", fieldValue.getValue().equals("E-K"));
			}
			else if (fieldValue.getFieldName().contains("de"))
			{
				Assert.assertEquals("Values for property 'name'", "name_de_string", fieldValue.getFieldName());
				Assert.assertTrue("Values for property 'name' are not as expected", fieldValue.getValue().equals("A-D"));
			}
			else
			{
				Assert.fail("Values for property 'name' are not as expected");
			}
		}

		//check for facet type property
		assertEquals("facetType for property 'name' is not as expected", FacetType.MULTISELECTOR, name.getFacetType());

		//check of non-localized property: code
		final IndexedProperty code = productType.getIndexedProperties().get("code");
		final Collection<FieldValue> codeValues = IndexedProperties.getFieldValueProvider(code).getFieldValues(
				config.getIndexConfig(), code, productService.getProductForCode("testProduct2"));

		Assert.assertEquals("There should be one values for non-localized property 'code'", 1, codeValues.size());
		final FieldValue actual = codeValues.iterator().next();
		Assert.assertTrue("Value for property 'code' is not as expected", actual.getFieldName().equals("code_string"));
		Assert.assertTrue("Value for property 'code' is not as expected", actual.getValue().equals("S-Z"));

		//check for non-ranged, localized property
		final IndexedProperty description = productType.getIndexedProperties().get("description");
		final Collection<FieldValue> descriptionValues = IndexedProperties.getFieldValueProvider(description).getFieldValues(
				config.getIndexConfig(), description, productService.getProductForCode("testProduct2"));
		for (final FieldValue fieldValue : descriptionValues)
		{
			if (fieldValue.getFieldName().contains("en"))
			{
				Assert.assertTrue("Values for property 'description' are not as expected",
						fieldValue.getFieldName().equals("description_text_en"));
				Assert.assertTrue("Values for property 'description' are not as expected",
						fieldValue.getValue().equals("en-description"));
			}
			else if (fieldValue.getFieldName().contains("de"))
			{
				Assert.assertTrue("Values for property 'description' are not as expected",
						fieldValue.getFieldName().equals("description_text_de"));
				Assert.assertTrue("Values for property 'description' are not as expected",
						fieldValue.getValue().equals("de-description"));
			}
			else
			{
				Assert.fail("Values for property 'description' are not as expected");
			}
		}

		final Map<Integer, String> expectedResults = new HashMap<Integer, String>(3);
		expectedResults.put(Integer.valueOf(0), "2008_05");
		expectedResults.put(Integer.valueOf(1), "2009_02");
		expectedResults.put(Integer.valueOf(2), "2009_12");

		//check for ranged date property
		final IndexedProperty onlineDate = productType.getIndexedProperties().get("onlineDate");
		for (final Integer key : expectedResults.keySet())
		{
			final Collection<FieldValue> onlineDateValues = IndexedProperties.getFieldValueProvider(onlineDate).getFieldValues(
					config.getIndexConfig(), onlineDate, productService.getProductForCode("testProduct" + key.intValue()));
			Assert.assertEquals("There should be one value for ranged property 'onlineDate'", 1, onlineDateValues.size());
			final FieldValue dateValue = onlineDateValues.iterator().next();
			Assert.assertEquals(
					"The date range name [" + dateValue.getValue() + "] is not as expected : [" + expectedResults.get(key) + "]",
					expectedResults.get(key), dateValue.getValue());
			Assert.assertEquals("The FiledValue name for 'onlineDate' [" + dateValue.getFieldName()
					+ "] is not as expected[onlineDate_date]", "onlineDate_string", dateValue.getFieldName());

		}
	}

	@Test
	public void testPricePropertyValueProvider() throws Exception
	{
		final FacetSearchConfig config = facetSearchConfig;
		final IndexedType productType = indexedType;
		final IndexedProperty price = productType.getIndexedProperties().get("price");
		//Resolve price ranges for product that has two prices defined : 87,95 EUR, 145,99 USD
		final List<FieldValue> priceValues = (ArrayList<FieldValue>) IndexedProperties.getFieldValueProvider(price).getFieldValues(
				config.getIndexConfig(), price, productService.getProductForCode("testProduct1"));
		Assert.assertEquals("There should be as many price values as there are currencies related to the solr config", config
				.getIndexConfig().getCurrencies().size(), priceValues.size());

		final Comparator<? super FieldValue> priceValueComparator = new Comparator<FieldValue>()
		{

			@Override
			public int compare(final FieldValue o1, final FieldValue o2)
			{
				return o1.getFieldName().compareTo(o2.getFieldName());
			}
		};
		//sort by field name
		Collections.sort(priceValues, priceValueComparator);
		Assert.assertTrue("CHF price doesn't belong to the expected range",
				priceValues.get(0).getFieldName().equals("price_chf_string"));
		Assert.assertTrue("CHF price doesn't belong to the expected range", priceValues.get(0).getValue().equals("1001-INF"));

		Assert.assertTrue("EUR price doesn't belong to the expected range",
				priceValues.get(1).getFieldName().equals("price_eur_string"));
		Assert.assertTrue("EUR price doesn't belong to the expected range", priceValues.get(1).getValue().equals("1-100"));

		Assert.assertTrue("GBP price doesn't belong to the expected range",
				priceValues.get(2).getFieldName().equals("price_gbp_string"));
		Assert.assertTrue("GBP price doesn't belong to the expected range", priceValues.get(2).getValue().equals("1-100"));

		Assert.assertTrue("USD price doesn't belong to the expected range",
				priceValues.get(3).getFieldName().equals("price_usd_string"));
		Assert.assertTrue("USD price doesn't belong to the expected range", priceValues.get(3).getValue().equals("101-200"));




	}
}
