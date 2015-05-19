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
package de.hybris.platform.solrfacetsearch.search.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Tests {@link DefaultSolrQueryCatalogVersionsResolver}
 */
@UnitTest
public class DefaultSolrQueryCatalogVersionsResolverTest
{

	private DefaultSolrQueryCatalogVersionsResolver resolver;
	@Mock
	private FacetSearchConfig facetSearchConfig;
	@Mock
	private IndexConfig indexConfigWithNoCatalogVersions;
	@Mock
	private IndexConfig indexConfigWithEmptyCatalogVersions;
	@Mock
	private IndexConfig singleCatalogVersionIndexConfig;
	@Mock
	private IndexConfig doubleCatalogVersionIndexConfig;
	@Mock
	private IndexConfig secondCatalogVersionIndexConfig;

	@Mock
	private CatalogVersionModel catalogVersion1;
	@Mock
	private CatalogVersionModel catalogVersion2;
	@Mock
	private CatalogVersionService catalogVersionService;


	@Before
	public void setUp()
	{
		resolver = new DefaultSolrQueryCatalogVersionsResolver();
		MockitoAnnotations.initMocks(this);

		given(indexConfigWithNoCatalogVersions.getCatalogVersions()).willReturn(null);
		given(indexConfigWithEmptyCatalogVersions.getCatalogVersions()).willReturn(Collections.EMPTY_LIST);
		given(singleCatalogVersionIndexConfig.getCatalogVersions()).willReturn(Collections.singletonList(catalogVersion2));
		given(doubleCatalogVersionIndexConfig.getCatalogVersions()).willReturn(Arrays.asList(catalogVersion1, catalogVersion2));
		given(secondCatalogVersionIndexConfig.getCatalogVersions()).willReturn(Arrays.asList(catalogVersion2));

		resolver.setCatalogVersionService(catalogVersionService);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullSource()
	{
		resolver.resolveQueryCatalogVersions(null);
	}

	@Test
	public void testFacetConfigWithNoCatalogVersionsSource()
	{
		given(facetSearchConfig.getIndexConfig()).willReturn(indexConfigWithNoCatalogVersions);
		assertThat(resolver.resolveQueryCatalogVersions(facetSearchConfig)).isEmpty();
	}

	@Test
	public void testFacetConfigWithEmptyCatalogVersionsSource()
	{
		given(facetSearchConfig.getIndexConfig()).willReturn(indexConfigWithEmptyCatalogVersions);
		assertThat(resolver.resolveQueryCatalogVersions(facetSearchConfig)).isEmpty();
	}

	@Test
	public void testTwoSessionCatalogVersionButOnlyOneConfigured()
	{
		given(catalogVersionService.getSessionCatalogVersions()).willReturn(Arrays.asList(catalogVersion1, catalogVersion2));
		given(facetSearchConfig.getIndexConfig()).willReturn(singleCatalogVersionIndexConfig);
		assertThat(resolver.resolveQueryCatalogVersions(facetSearchConfig)).containsOnly(catalogVersion2);
	}

	@Test
	public void testOneSessionCatalogVersionButTwoConfigured()
	{
		given(catalogVersionService.getSessionCatalogVersions()).willReturn(Arrays.asList(catalogVersion1));
		given(facetSearchConfig.getIndexConfig()).willReturn(doubleCatalogVersionIndexConfig);
		assertThat(resolver.resolveQueryCatalogVersions(facetSearchConfig)).containsOnly(catalogVersion1);
	}

	@Test
	public void testExclusiveCase()
	{
		given(catalogVersionService.getSessionCatalogVersions()).willReturn(Arrays.asList(catalogVersion1));
		given(facetSearchConfig.getIndexConfig()).willReturn(secondCatalogVersionIndexConfig);
		assertThat(resolver.resolveQueryCatalogVersions(facetSearchConfig)).isEmpty();
	}

	@Test
	public void testBothCatalogVersionsMatch()
	{
		given(catalogVersionService.getSessionCatalogVersions()).willReturn(Arrays.asList(catalogVersion1, catalogVersion2));
		given(facetSearchConfig.getIndexConfig()).willReturn(doubleCatalogVersionIndexConfig);
		assertThat(resolver.resolveQueryCatalogVersions(facetSearchConfig)).containsExactly(catalogVersion1, catalogVersion2);
	}


}
