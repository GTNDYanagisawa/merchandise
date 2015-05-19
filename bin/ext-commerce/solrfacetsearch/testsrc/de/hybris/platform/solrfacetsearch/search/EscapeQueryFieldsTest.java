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
package de.hybris.platform.solrfacetsearch.search;

import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.solrfacetsearch.config.ClusterConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigs;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfigs;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.config.SearchConfigs;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.constants.SolrfacetsearchConstants;
import de.hybris.platform.solrfacetsearch.indexer.impl.SolrServerExporter;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Tests escaping of special characters when building a search query.
 */
public class EscapeQueryFieldsTest extends ServicelayerTransactionalTest
{

	private final static String TARGET_FIELD_NAME = "Colour of product, 1766_string";
	@Resource(name = "solr.exporter.embedded")
	private SolrServerExporter solrServerExporter;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource(name = "solrFacetSearchService")
	private FacetSearchService facetSearchService;


	private IndexConfig indexConfig;
	private SolrConfig solrConfig;
	private SearchConfig searchConfig;
	private SearchQuery searchQuery;
	private FacetSearchConfig facetSearchConfig;

	@Mock
	private IndexedType indexedType;

	private CatalogVersionModel testCatalog;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		when(Boolean.valueOf(indexedType.isStaged())).thenReturn(Boolean.FALSE);
		when(indexedType.getIndexName()).thenReturn("testName");

		searchConfig = SearchConfigs.createSearchConfig(Collections.EMPTY_LIST, 10);

		createCoreData();
		createDefaultCatalog();

		testCatalog = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		indexConfig = IndexConfigs.createIndexConfig(Collections.singletonList(indexedType),
				Collections.singletonList(testCatalog), Collections.EMPTY_LIST, Collections.EMPTY_LIST, "", 100, false);


		final ClusterConfig clusterConfig = new ClusterConfig();
		clusterConfig.setAliveCheckInterval(Integer.valueOf(100));
		clusterConfig.setConnectionTimeout(Integer.valueOf(100));
		clusterConfig.setReadTimeout(Integer.valueOf(100));
		clusterConfig.setEmbeddedMaster(true);

		solrConfig = new SolrConfig();
		solrConfig.setClusterConfig(clusterConfig);
		solrConfig.setMode(SolrServerMode.EMBEDDED);


		facetSearchConfig = FacetSearchConfigs.createFacetSearchConfig("test", "test", indexConfig, searchConfig, solrConfig);

		final SolrInputDocument doc1 = new SolrInputDocument();
		doc1.addField("id", "1");
		doc1.addField(TARGET_FIELD_NAME, "test");
		doc1.addField("catalogId", "testCatalog");
		doc1.addField("catalogVersion", "Online");

		final SolrInputDocument doc2 = new SolrInputDocument();
		doc2.addField("id", "2");
		doc2.addField(TARGET_FIELD_NAME, "test");
		doc2.addField("catalogId", "testCatalog");
		doc2.addField("catalogVersion", "Online");

		// immediate hard commit is needed for the test
		// the current setting is remember, so it can be set back to the value
		final boolean hardCommitsExplicit = Config.getBoolean(SolrfacetsearchConstants.INDEXER_HARDCOMMITS_EXPLICIT_PROPERTY_KEY,
				false);
		Config.setParameter(SolrfacetsearchConstants.INDEXER_HARDCOMMITS_EXPLICIT_PROPERTY_KEY, "true");
		solrServerExporter.exportToUpdateIndex(Arrays.asList(doc1, doc2), indexConfig, solrConfig, indexedType);
		Config.setParameter(SolrfacetsearchConstants.INDEXER_HARDCOMMITS_EXPLICIT_PROPERTY_KEY,
				Boolean.toString(hardCommitsExplicit));
	}

	@Test
	public void testQueryEscapedField()
	{
		searchQuery = new SearchQuery(facetSearchConfig, indexedType);
		searchQuery.searchInField(TARGET_FIELD_NAME, "test");

		try
		{
			final SearchResult result = facetSearchService.search(searchQuery);
			Assert.assertEquals(2, result.getTotalNumberOfResults());
		}
		catch (final FacetSearchException e)
		{
			Assert.fail("No expection expected , but was : " + e.getMessage());
		}
	}
}
