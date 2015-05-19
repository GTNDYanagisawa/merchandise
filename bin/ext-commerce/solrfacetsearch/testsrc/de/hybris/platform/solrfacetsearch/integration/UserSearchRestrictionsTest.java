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

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


/**
 * 
 */
public class UserSearchRestrictionsTest extends AbstractSolrIntegrationTest
{

	/**
	 * 
	 */
	private static final String SELECT_PK_FROM_PRODUCT = "SELECT {pk} FROM {Product}";
	private static final String CONFIG_NAME = "RestrictionTestIndexConfig";

	private static final String UNAPPROVED_CODE = "HW1100-0024";


	@Resource
	private EnumerationService enumerationService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private UserService userService;
	@Resource
	private CategoryService categoryService;


	@Override
	protected List<SolrIndexerQueryModel> setUpIndexerQueries()
	{
		final SolrIndexerQueryModel fullQueryModel = modelService.create(SolrIndexerQueryModel.class);
		fullQueryModel.setType(IndexerOperationValues.FULL);
		fullQueryModel.setQuery(SELECT_PK_FROM_PRODUCT);
		fullQueryModel.setIdentifier("fullQuery");
		fullQueryModel.setUser(userService.getUserForUID("demo"));
		return Collections.singletonList(fullQueryModel);
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		importCsv("/test/restrictionTestProducts.csv", "utf-8");
		hwOnline = catalogVersionService.getCatalogVersion("hwcatalog", VERSION_ONLINE);
		Assert.assertNotNull(hwOnline);
		final CategoryModel category = categoryService.getCategoryForCode(hwOnline, "HW1100");
		Assert.assertNotNull(category);
		Assert.assertEquals(3, productService.getProductsForCategory(category).size());
	}


	@Override
	protected String getSolrConfigName()
	{
		return CONFIG_NAME;
	}

	@Override
	protected List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{
		return Collections.singletonList(hwOnline);
	}

	@Override
	protected void prepareIndexForTest() throws Exception
	{
		//create restrictions before reindexing
		final EnumerationValueModel approved = typeService.getEnumerationValue(enumerationService.getEnumerationValue(
				ArticleApprovalStatus._TYPECODE, ArticleApprovalStatus.APPROVED.getCode()));

		final SearchRestrictionModel restriction = modelService.create(SearchRestrictionModel.class);
		restriction.setPrincipal(userService.getUserForUID("demo"));
		restriction.setRestrictedType(typeService.getComposedTypeForCode("Product"));
		restriction.setCode("anonymousRestriction");
		restriction.setName("anonymousRestriction");
		restriction.setGenerate(Boolean.TRUE);
		restriction.setQuery("{approvalStatus} = " + approved.getPk());

		modelService.save(restriction);
		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		super.prepareIndexForTest();
	}

	/**
	 * Test indexing of products, when indexer's queries have user defined. In such case, the indexer takes the items for
	 * indexing that follow this users flexible search restrictions.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIndexingWithUserRestriction() throws Exception
	{
		indexerService.performFullIndex(facetSearchConfig);
		final SearchResult result = facetSearchService.search(query);
		final List<String> resultCodes = result.getResultCodes();
		Assert.assertFalse(resultCodes.isEmpty());

		final FlexibleSearchQuery query = new FlexibleSearchQuery(SELECT_PK_FROM_PRODUCT);
		query.setUser(userService.getUserForUID("demo"));
		query.setCatalogVersions(hwOnline);
		final de.hybris.platform.servicelayer.search.SearchResult<ProductModel> searchedProducts = flexibleSearchService
				.search(query);
		final List<ProductModel> indexedProducts = (List<ProductModel>) result.getResults();
		Assert.assertTrue(indexedProducts.containsAll(searchedProducts.getResult()));
		Assert.assertTrue(!resultCodes.contains(UNAPPROVED_CODE));

	}
}
