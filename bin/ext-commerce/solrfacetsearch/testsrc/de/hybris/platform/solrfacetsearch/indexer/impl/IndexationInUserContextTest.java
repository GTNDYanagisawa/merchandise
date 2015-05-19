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
package de.hybris.platform.solrfacetsearch.indexer.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;



/**
 *
 */
@IntegrationTest
public class IndexationInUserContextTest extends AbstractSolrTest
{

	@Resource
	private UserService userService;
	@Resource
	private FacetSearchService facetSearchService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private UnitService unitService;


	private UserModel testUSer;
	private ProductModel indexedProduct1;
	private ProductModel indexedProduct2;
	private SearchQuery query;
	private UserModel admin;



	@Override
	public void setUp() throws Exception
	{

		super.setUp();
		admin = userService.getAdminUser();
		userService.setCurrentUser(admin);
		dropIndex();
		indexerService.performFullIndex(facetSearchConfig);
		query = new SearchQuery(facetSearchConfig, indexedType);
	}

	@Override
	protected void setUpBasic() throws Exception
	{
		super.setUpBasic();

		testUSer = modelService.create(EmployeeModel.class);
		testUSer.setUid("test");
		modelService.save(testUSer);
		testUSer = userService.getUserForUID("test");
	}


	@Override
	protected List<SolrIndexerQueryModel> setUpIndexerQueries()
	{
		final SolrIndexerQueryModel fullQueryModel = modelService.create(SolrIndexerQueryModel.class);
		fullQueryModel.setType(IndexerOperationValues.FULL);
		fullQueryModel.setQuery("select {pk} from {Product}");
		fullQueryModel.setIdentifier("fullQuery");
		fullQueryModel.setUser(testUSer);

		return Arrays.asList(fullQueryModel);
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		super.setUpProductData();
		indexedProduct1 = modelService.create(ProductModel.class);
		indexedProduct1.setCode("product1");
		indexedProduct1.setCatalogVersion(cv);
		indexedProduct1.setDescription("Description_product_1");

		indexedProduct2 = modelService.create(ProductModel.class);
		indexedProduct2.setCode("product2");
		indexedProduct2.setCatalogVersion(cv);
		indexedProduct2.setDescription("Description_product_2");

		final PriceRowModel price1Admin = prepareUserPriceRow(indexedProduct1, admin, 5);
		final PriceRowModel price1TestUser = prepareUserPriceRow(indexedProduct1, testUSer, 1);
		final PriceRowModel price2Admin = prepareUserPriceRow(indexedProduct2, admin, 10);
		final PriceRowModel price2TestUser = prepareUserPriceRow(indexedProduct2, testUSer, 2);

		modelService.saveAll(indexedProduct1, indexedProduct2);
		modelService.saveAll(Arrays.asList(price1Admin, price1TestUser, price2Admin, price2TestUser));
	}


	private PriceRowModel prepareUserPriceRow(final ProductModel product, final UserModel user, final double price)
	{
		final PriceRowModel prmodel1_admin = modelService.create(PriceRowModel.class);
		prmodel1_admin.setCurrency(commonI18NService.getCurrency("EUR"));
		prmodel1_admin.setMinqtd(Long.valueOf(1));
		prmodel1_admin.setNet(Boolean.TRUE);
		prmodel1_admin.setPrice(Double.valueOf(price));
		prmodel1_admin.setUnit(unitService.getUnitForCode("piece"));
		prmodel1_admin.setProduct(product);
		prmodel1_admin.setCatalogVersion(cv);
		prmodel1_admin.setUser(user);
		return prmodel1_admin;
	}

	@Test
	public void testQueryNotPermittedAttribute() throws FacetSearchException
	{
		Assert.assertEquals(admin, userService.getCurrentUser());
		query.setCurrency("EUR");
		query.searchInField("price", "1");
		SearchResult searchResult = facetSearchService.search(query);
		org.fest.assertions.Assertions.assertThat(searchResult.getResults()).containsOnly(indexedProduct1);


		query.searchInField("price", "10");
		searchResult = facetSearchService.search(query);
		org.fest.assertions.Assertions.assertThat(searchResult.getResults()).isEmpty();

		Assert.assertEquals(admin, userService.getCurrentUser());
	}

	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{
		final SolrIndexedPropertyModel price = modelService.create(SolrIndexedPropertyModel.class);
		price.setName("price");
		price.setType(SolrPropertiesTypes.DOUBLE);
		price.setFieldValueProvider("productPriceValueProvider");
		price.setCurrency(true);
		final List<SolrIndexedPropertyModel> result = new ArrayList(super.setUpIndexProperties());
		result.add(price);
		return result;
	}

}
