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

import de.hybris.platform.catalog.jalo.classification.util.Feature;
import de.hybris.platform.catalog.jalo.classification.util.FeatureContainer;
import de.hybris.platform.catalog.jalo.classification.util.FeatureValue;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider.FieldType;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;


/**
 * Test if the documents in Solr contain all indexed values in fields with the defined naming convention, the proper ID
 * and value.
 */

public class IndexFullProductTest extends AbstractSolrIntegrationTest
{
	/**
	 * Kinds of recognized text fileds
	 */
	enum KindOfTextAttribute
	{
		NAME, DESCRIPTION
	}

	private final static Logger LOG = Logger.getLogger(IndexFullProductTest.class.getName());

	public static final String CATEGORIES_QUALIFIER = "supercategories";

	private SearchResult<ProductModel> searchResult;
	private SolrServer solrServer;
	private Locale enLocale;
	private Locale deLocale;

	@Resource(name = "solrFieldNameProvider")
	private FieldNameProvider fieldNameProvider;


	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private PriceService priceService;

	@Before
	public void before() throws Exception
	{

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {PK} from {Product}");
		addIndexedCatalogVersionToQuery(flexibleSearchQuery);
		searchResult = flexibleSearchService.search(flexibleSearchQuery);

		solrServer = getSolrService().getSolrServer(facetSearchConfig.getSolrConfig(), indexedType);

		enLocale = Locale.ENGLISH;
		deLocale = Locale.GERMAN;
	}

	@Test
	public void verifySolrId() throws SolrServerException
	{
		final List<ProductModel> productModels = searchResult.getResult();
		for (final ProductModel productModel : productModels)
		{
			final String solrDocId = prepareProductSolrId(productModel);
			final QueryResponse solrResponse = solrServer.query(new SolrQuery("id:\"" + solrDocId + "\""));
			final SolrDocumentList solrDocumentList = solrResponse.getResults();
			assertNotNull(solrDocumentList);
			assertEquals("Document with ID " + solrDocId + " is not found!", 1, solrDocumentList.size());
		}
	}

	@Test
	public void verifyPK() throws SolrServerException
	{
		final List<ProductModel> productModels = searchResult.getResult();
		for (final ProductModel productModel : productModels)
		{
			final long pk = productModel.getPk().getLongValue();
			final QueryResponse solrResponse = solrServer.query(new SolrQuery("pk:\"" + pk + "\""));
			final SolrDocumentList solrDocumentList = solrResponse.getResults();
			assertNotNull(solrDocumentList);
			assertEquals("Document with PK " + pk + " is not found!", 1, solrDocumentList.size());
		}
	}


	@Test
	public void verifyManufacturername() throws SolrServerException
	{
		final List<ProductModel> productModels = searchResult.getResult();
		for (final ProductModel productModel : productModels)
		{
			final String manufacturerName = productModel.getManufacturerName();
			final String solrDocId = prepareProductSolrId(productModel);
			final QueryResponse solrResponse = solrServer.query(new SolrQuery("manufacturerName_string:\"" + manufacturerName
					+ "\" AND id:" + "\"" + solrDocId + "\""));
			final SolrDocumentList solrDocumentList = solrResponse.getResults();
			assertNotNull(solrDocumentList);
			assertEquals("Manufacturer name  for document with ID " + solrDocId + " is not found!", 1, solrDocumentList.size());
		}
	}

	@Test
	public void verifyName() throws SolrServerException
	{
		final String solrEnKey = "name_text_en";
		checkTextAttributeForLanguage(enLocale, solrEnKey, KindOfTextAttribute.NAME);

		final String solrDeKey = "name_text_de";
		checkTextAttributeForLanguage(deLocale, solrDeKey, KindOfTextAttribute.NAME);

	}

	@Test
	public void verifyDescription() throws SolrServerException
	{
		final String solrEnKey = "description_text_en";
		checkTextAttributeForLanguage(enLocale, solrEnKey, KindOfTextAttribute.DESCRIPTION);

		final String solrDeKey = "description_text_de";
		checkTextAttributeForLanguage(deLocale, solrDeKey, KindOfTextAttribute.DESCRIPTION);
	}

	@Test
	public void verifyProcessorVariantAttribute() throws SolrServerException
	{
		final List<ProductModel> productModels = searchResult.getResult();
		for (final ProductModel productModel : productModels)
		{
			final FeatureContainer cont = FeatureContainer.load((Product) modelService.getSource(productModel));
			if (!cont.hasFeature("processor"))
			{
				continue;
			}
			final Feature feature = cont.getFeature("processor");
			if (feature == null || feature.isEmpty())
			{
				continue;
			}
			final List<FeatureValue> fieldValues = feature.getValues();
			for (final FeatureValue fieldValue : fieldValues)
			{
				final String solrDocId = prepareProductSolrId(productModel);
				final String solrQuery = "processor_string_mv:\"" + fieldValue.getValue() + "\" AND id:\"" + solrDocId + "\"";
				final QueryResponse solrResponse = solrServer.query(new SolrQuery(solrQuery));
				final SolrDocumentList solrDocumentList = solrResponse.getResults();
				assertNotNull(solrDocumentList);
				assertEquals("Processor classification property " + solrQuery + " for document with ID " + solrDocId
						+ " is not found!", 1, solrDocumentList.size());
			}
		}
	}

	@Test
	public void verifyCategoryAttribute() throws SolrServerException
	{
		final List<ProductModel> productModels = searchResult.getResult();
		for (final ProductModel productModel : productModels)
		{
			final Collection<CategoryModel> categories = modelService.getAttributeValue(productModel, CATEGORIES_QUALIFIER);
			assertNotNull(categories);

			for (final CategoryModel categoryModel : categories)
			{
				final String categoryNameEn = categoryModel.getName(enLocale);
				checkCategoryForProductInSolr("categoryName_en_string_mv", productModel, categoryNameEn);
				final String categoryNameDe = categoryModel.getName(deLocale);
				checkCategoryForProductInSolr("categoryName_de_string_mv", productModel, categoryNameDe);
			}
		}
	}

	private void checkCategoryForProductInSolr(final String solrAttributeName, final ProductModel productModel,
			final String categoryName) throws SolrServerException
	{
		final String solrDocId = prepareProductSolrId(productModel);
		final QueryResponse solrResponse = solrServer.query(new SolrQuery(solrAttributeName + ":\"" + categoryName + "\" AND id:\""
				+ solrDocId + "\""));
		final SolrDocumentList solrDocumentList = solrResponse.getResults();
		assertNotNull(solrDocumentList);
		assertTrue("Category name " + categoryName + " is not found for " + solrDocId + "!", (1 == solrDocumentList.size()));
	}


	private void checkTextAttributeForLanguage(final Locale enLocale, final String solrPropertyName,
			final KindOfTextAttribute kindOfTextAttribute) throws SolrServerException
	{
		final List<ProductModel> productModels = searchResult.getResult();
		for (final ProductModel productModel : productModels)
		{
			String localisedProductvalue = null;
			switch (kindOfTextAttribute)
			{
				case NAME:
					localisedProductvalue = productModel.getName(enLocale);
					break;
				case DESCRIPTION:
					localisedProductvalue = productModel.getDescription(enLocale);
					break;
				default:
					throw new UnsupportedOperationException("Uknown name of indexed text attribute.");
			}
			if (StringUtils.isEmpty(localisedProductvalue))
			{
				continue;
			}
			final String solrDocumentId = prepareProductSolrId(productModel);
			localisedProductvalue = localisedProductvalue.replaceAll("\"", "\\\\\"");
			final String queryStr = solrPropertyName + ":\"" + localisedProductvalue + "\" AND id:" + "\"" + solrDocumentId + "\"";
			final QueryResponse solrResponse = solrServer.query(new SolrQuery(queryStr));
			final SolrDocumentList solrDocumentList = solrResponse.getResults();
			assertNotNull(solrDocumentList);
			assertEquals("Document with ID " + solrDocumentId + " is not found or umbigous result!", 1, solrDocumentList.size());
		}
	}

	@Test
	public void testVerifyNumberOfIndexedRecords() throws Exception
	{
		final int numberOfProductsToIndex = searchResult.getTotalCount();

		LOG.info("Number of products to index = " + numberOfProductsToIndex);

		final QueryResponse solrResponse = solrServer.query(new SolrQuery("*:*"));
		final SolrDocumentList solrDocumentList = solrResponse.getResults();
		assertNotNull(solrDocumentList);
		LOG.info("Number of indexed documents = " + solrDocumentList.getNumFound());
		assertEquals(numberOfProductsToIndex, solrDocumentList.getNumFound());
	}

	@Test
	public void testPriceRangeAttribute() throws SolrServerException
	{
		String currency = "EUR";
		checkPriceForAllProducts(currency);
		currency = "USD";
		checkPriceForAllProducts(currency);
	}

	private void checkPriceForAllProducts(final String currency) throws SolrServerException
	{
		final List<ProductModel> productModels = searchResult.getResult();
		final CurrencyModel currencyModel = commonI18NService.getCurrency(currency);
		commonI18NService.setCurrentCurrency(currencyModel);
		for (final ProductModel productModel : productModels)
		{
			final String solrDocId = prepareProductSolrId(productModel);
			final List<PriceInformation> prices = priceService.getPriceInformationsForProduct(productModel);
			assertNotNull(prices);
			assertFalse(prices.isEmpty());
			final Double value = Double.valueOf(prices.get(0).getPriceValue().getValue());
			if (value != null && value.doubleValue() > 0)
			{
				final String rangeStr = getPriceRangeForValue(value.doubleValue());
				final IndexedProperty priceProperty = indexedType.getIndexedProperties().get("price");
				final String priceField = fieldNameProvider.getFieldName(priceProperty, currency, FieldType.INDEX);
				final String solrQuery = priceField + ":\"" + rangeStr + "\" AND id:\"" + solrDocId + "\"";
				final QueryResponse solrResponse = solrServer.query(new SolrQuery(solrQuery));
				final SolrDocumentList solrDocumentList = solrResponse.getResults();
				assertNotNull(solrDocumentList);
				assertEquals("Price range " + rangeStr + " was not find for currency " + currency + ", solrDocId: " + solrDocId, 1,
						solrDocumentList.getNumFound());
			}
		}
	}

	private String getPriceRangeForValue(final double value)
	{
		if (value > 1000)
		{
			return "1001-INF";
		}
		if (value <= 0)
		{
			return null;
		}
		final int hundredNum = ((int) value / 100) * 100;
		final int endRange = hundredNum + 100;
		final int startRange = hundredNum + 1;
		return startRange + "-" + endRange;
	}

	protected void addIndexedCatalogVersionToQuery(final FlexibleSearchQuery flexibleSearchQuery) throws Exception
	{
		assertNotNull(facetSearchConfig);
		final IndexConfig indexConifg = facetSearchConfig.getIndexConfig();
		assertNotNull(indexConifg);
		final Collection<CatalogVersionModel> catalogVersions = indexConifg.getCatalogVersions();
		assertNotNull(catalogVersions);
		assertFalse("Catalog version list can not be empty in configuration!", catalogVersions.isEmpty());
		flexibleSearchQuery.setCatalogVersions(catalogVersions);
	}
}
