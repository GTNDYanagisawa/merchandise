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
package de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.impl.FacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultSolrFacetSearchConfigSelectionStrategy}.
 * 
 * @author krzysztof.kwiatosz
 */
@UnitTest
public class DefaultSolrFacetSearchConfigSelectionStrategyTest
{

	private DefaultSolrFacetSearchConfigSelectionStrategy strategy;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private BaseSiteModel baseSite;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private CatalogModel productCatalog1;
	@Mock
	private CatalogModel productCatalog2;
	@Mock
	private CatalogVersionModel productCatalogVersion2;
	@Mock
	private CatalogVersionModel productCatalogVersion1;
	@Mock
	private FacetSearchConfigDao facetSearchConfigDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		strategy = new DefaultSolrFacetSearchConfigSelectionStrategy();
		strategy.setBaseSiteService(baseSiteService);
		strategy.setBaseStoreService(baseStoreService);
		strategy.setCatalogVersionService(catalogVersionService);
		strategy.setFacetSearchConfigDao(facetSearchConfigDao);
		when(facetSearchConfigDao.findAllSolrFacetSearchConfigs()).thenReturn(Collections.<SolrFacetSearchConfigModel> emptyList());
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(
				Arrays.asList(productCatalogVersion1, productCatalogVersion2));
		when(productCatalogVersion1.getCatalog()).thenReturn(productCatalog1);
		when(productCatalogVersion2.getCatalog()).thenReturn(productCatalog2);
	}

	@Test(expected = NoValidSolrConfigException.class)
	public void testNoIndexConfigFound() throws NoValidSolrConfigException
	{
		when(baseSite.getSolrFacetSearchConfiguration()).thenReturn(null);
		when(baseStore.getSolrFacetSearchConfiguration()).thenReturn(null);
		when(baseSiteService.getProductCatalogs(baseSite)).thenReturn(null);
		strategy.getCurrentSolrFacetSearchConfig();
	}

	@Test
	public void testIndexConfigForBaseSiteConfigured() throws NoValidSolrConfigException
	{
		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = new SolrFacetSearchConfigModel();
		when(baseSite.getSolrFacetSearchConfiguration()).thenReturn(solrFacetSearchConfigModel);
		when(baseStore.getSolrFacetSearchConfiguration()).thenReturn(null);
		Assert.assertEquals(solrFacetSearchConfigModel, strategy.getCurrentSolrFacetSearchConfig());
	}

	@Test
	public void testIndexConfigForBaseStoreConfigured() throws NoValidSolrConfigException
	{
		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = new SolrFacetSearchConfigModel();
		when(baseSite.getSolrFacetSearchConfiguration()).thenReturn(null);
		when(baseStore.getSolrFacetSearchConfiguration()).thenReturn(solrFacetSearchConfigModel);
		Assert.assertEquals(solrFacetSearchConfigModel, strategy.getCurrentSolrFacetSearchConfig());
	}

	@Test
	public void testIndexConfigForBaseStoreAndBaseSiteConfigured() throws NoValidSolrConfigException
	{
		final SolrFacetSearchConfigModel solrFacetSearchConfigModel1 = new SolrFacetSearchConfigModel();
		final SolrFacetSearchConfigModel solrFacetSearchConfigModel2 = new SolrFacetSearchConfigModel();
		when(baseSite.getSolrFacetSearchConfiguration()).thenReturn(solrFacetSearchConfigModel1);
		when(baseStore.getSolrFacetSearchConfiguration()).thenReturn(solrFacetSearchConfigModel2);
		Assert.assertEquals(solrFacetSearchConfigModel1, strategy.getCurrentSolrFacetSearchConfig());
	}

	@Test
	public void testFallbackFromCatalogVersions() throws NoValidSolrConfigException
	{
		final SolrFacetSearchConfigModel solrConfigFromCatalogVersions = new SolrFacetSearchConfigModel();
		final SolrFacetSearchConfigModel solrConfigEmpty = new SolrFacetSearchConfigModel();
		final SolrFacetSearchConfigModel solrConfigWithOnlyOneCatalogVersion = new SolrFacetSearchConfigModel();
		when(facetSearchConfigDao.findAllSolrFacetSearchConfigs()).thenReturn(
				Arrays.asList(solrConfigEmpty, solrConfigWithOnlyOneCatalogVersion, solrConfigFromCatalogVersions));
		when(baseSite.getSolrFacetSearchConfiguration()).thenReturn(null);
		when(baseStore.getSolrFacetSearchConfiguration()).thenReturn(null);
		when(baseSiteService.getProductCatalogs(baseSite)).thenReturn(Arrays.asList(productCatalog1, productCatalog2));

		solrConfigFromCatalogVersions.setCatalogVersions(Arrays.asList(productCatalogVersion1, productCatalogVersion2));
		solrConfigWithOnlyOneCatalogVersion.setCatalogVersions(Collections.singletonList(productCatalogVersion1));
		productCatalogVersion1.setFacetSearchConfigs(Arrays.asList(solrConfigFromCatalogVersions,
				solrConfigWithOnlyOneCatalogVersion));
		productCatalogVersion2.setFacetSearchConfigs(Collections.singletonList(solrConfigFromCatalogVersions));

		Assert.assertEquals(solrConfigFromCatalogVersions, strategy.getCurrentSolrFacetSearchConfig());

	}

}
