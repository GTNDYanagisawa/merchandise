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

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class IndexAndSearchFromMultipleCatalogVersionsTest extends AbstractSolrTest
{

	@Resource
	private FacetSearchService facetSearchService;

	private static final String PRODUCT_1 = "product1";
	private static final String PRODUCT_2 = "product2";

	//testCatalogA:Online
	private CatalogVersionModel catalogVersionA;
	//testCatalogB:Staged
	private CatalogVersionModel catalogVersionB;

	//testCatalogA:Staged
	private CatalogVersionModel catalogVersionAStaged;
	//testCatalogB:Online
	private CatalogVersionModel catalogVersionBOnline;

	private ProductModel productA1;
	private ProductModel productA2;

	private ProductModel productB1;
	private ProductModel productB2;
	private SearchQuery query;


	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		dropIndex();
		indexerService.performFullIndex(facetSearchConfig);
		query = new SearchQuery(facetSearchConfig, indexedType);
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		final CatalogModel cataloga = modelService.create(CatalogModel.class);
		cataloga.setId("testCatalogA");

		catalogVersionA = modelService.create(CatalogVersionModel.class);
		catalogVersionA.setCatalog(cataloga);
		catalogVersionA.setVersion("Online");

		catalogVersionAStaged = modelService.create(CatalogVersionModel.class);
		catalogVersionAStaged.setCatalog(cataloga);
		catalogVersionAStaged.setVersion("Staged");

		final CatalogModel catalogb = modelService.create(CatalogModel.class);
		catalogb.setId("testCatalogB");

		catalogVersionB = modelService.create(CatalogVersionModel.class);
		catalogVersionB.setCatalog(catalogb);
		catalogVersionB.setVersion("Staged");

		catalogVersionBOnline = modelService.create(CatalogVersionModel.class);
		catalogVersionBOnline.setCatalog(catalogb);
		catalogVersionBOnline.setVersion("Online");

		modelService.saveAll(catalogVersionA, catalogVersionB, catalogVersionAStaged, catalogVersionBOnline);

		productA1 = prepareProduct(catalogVersionA, PRODUCT_1);
		productA2 = prepareProduct(catalogVersionA, PRODUCT_2);
		productB1 = prepareProduct(catalogVersionB, PRODUCT_1);
		productB2 = prepareProduct(catalogVersionB, PRODUCT_2);

		modelService.saveAll(productA1, productA2, productB1, productB2);
	}

	private ProductModel prepareProduct(final CatalogVersionModel catalogVersion, final String productCode)
	{
		final ProductModel product = modelService.create(ProductModel.class);
		product.setCatalogVersion(catalogVersion);
		product.setCode(productCode);
		return product;
	}

	/**
	 * Override to define {@link CatalogVersionModel}s for your {@link SolrFacetSearchConfigModel} instance
	 */
	@Override
	protected List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{
		return Arrays.asList(catalogVersionA, catalogVersionB);
	}

	@Test
	public void testIndexTwoCatalogVersionsSearchInTwoCatalogVersions() throws FacetSearchException
	{
		query.setCatalogVersions(Arrays.asList(catalogVersionA, catalogVersionB));
		query.searchInField("code", PRODUCT_1);
		final List<ProductModel> foundProducts = (List<ProductModel>) facetSearchService.search(query).getResults();
		assertThat(foundProducts).containsExactly(productA1, productB1);
	}

	@Test
	public void testIndexTwoCatalogVersionsSearchInOneCatalogVersion() throws FacetSearchException
	{
		query.setCatalogVersions(Arrays.asList(catalogVersionA));
		query.searchInField("code", PRODUCT_2);
		final List<ProductModel> foundProducts = (List<ProductModel>) facetSearchService.search(query).getResults();
		assertThat(foundProducts).containsOnly(productA2);
	}

	@Test
	public void testIndexTwoCatalogVersionsSearchInTwoSessionCatalogVersions() throws FacetSearchException
	{
		assertThat(query.getCatalogVersions()).isNull();
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(catalogVersionA, catalogVersionB));
		query.searchInField("code", PRODUCT_1);
		final List<ProductModel> foundProducts = (List<ProductModel>) facetSearchService.search(query).getResults();
		assertThat(foundProducts).containsExactly(productA1, productB1);
	}

	@Test
	public void testIndexTwoCatalogVersionsSearchInOneSessionCatalogVersion() throws FacetSearchException
	{
		assertThat(query.getCatalogVersions()).isNull();
		catalogVersionService.setSessionCatalogVersion(catalogVersionB.getCatalog().getId(), catalogVersionB.getVersion());
		query.searchInField("code", PRODUCT_2);
		final List<ProductModel> foundProducts = (List<ProductModel>) facetSearchService.search(query).getResults();
		assertThat(foundProducts).containsOnly(productB2);
	}

	@Test
	public void testIndexTwoCatalogVersionsSearchInThird() throws FacetSearchException
	{
		query.setCatalogVersions(Arrays.asList(catalogVersionAStaged));
		query.searchInField("code", PRODUCT_1);
		final List<ProductModel> foundProducts = (List<ProductModel>) facetSearchService.search(query).getResults();
		assertThat(foundProducts).isEmpty();
	}

	@Test
	public void testIndexTwoCatalogVersionsSearchInTwoOthers() throws FacetSearchException
	{
		query.setCatalogVersions(Arrays.asList(catalogVersionAStaged, catalogVersionBOnline));
		query.searchInField("code", PRODUCT_1);
		final List<ProductModel> foundProducts = (List<ProductModel>) facetSearchService.search(query).getResults();
		assertThat(foundProducts).isEmpty();
	}
}
