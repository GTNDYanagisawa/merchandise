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

import static junit.framework.Assert.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerService;
import de.hybris.platform.solrfacetsearch.solr.SolrService;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class SearchByPropertiesWithExportIdTest extends ServicelayerTest
{
	private final static String SOLR_CONFIG_DATA_IMPEX = "/test/testSolrConfigWithExportID.csv";

	@Resource
	private FacetSearchConfigService facetSearchConfigService;
	@Resource(name = "solrFacetSearchService")
	private FacetSearchService facetSearchService;
	@Resource
	private IndexerService indexerService;
	@Resource
	private CommonI18NService commonI18NService;

	private FacetSearchConfig config;
	private IndexedType indexedType;
	private SearchQuery query;

	@Resource
	private SolrService solrService;

	/**
	 * Test solr configuration builds index for products from test catalog for three index properties:
	 * 
	 * 1.) propertyName : code , exportId : code_id <br/>
	 * 1.) propertyName : name, exportId : null
	 */
	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv(SOLR_CONFIG_DATA_IMPEX, "utf-8");

		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("de"));
		config = facetSearchConfigService.getConfiguration("ConfigWithExportIds");
		indexedType = config.getIndexConfig().getIndexedTypes().get("Product");
		clearIndex();
		indexerService.performFullIndex(config);
		query = new SearchQuery(config, indexedType);
		query.setLanguage("de");
	}

	private void clearIndex() throws Exception
	{
		final SolrServer solrServer = solrService.getSolrServerMaster(config.getSolrConfig(), indexedType);
		assertNotNull(solrServer);
		solrServer.deleteByQuery("*:*");
		solrServer.commit();
	}

	@Test
	public void testSearchingByFirstExportId() throws FacetSearchException
	{
		query.searchInField("code_id", "testProduct0");
		final SearchResult result = facetSearchService.search(query);
		assertThat(result.getResultCodes()).containsOnly("testProduct0");
	}


	@Test
	public void testSearchingByName() throws FacetSearchException
	{
		query.searchInField("name", "testProduct0de");
		final SearchResult result = facetSearchService.search(query);
		//We must change this assertion, because we applied word delimiter filter for filed of type text. Now it's simply matching 'testProduct0', '1testProduct0' beacuse of 'splitOnNumbers' option.
		assertThat(result.getResultCodes()).contains("testProduct0", "1testProduct0");
	}

}
