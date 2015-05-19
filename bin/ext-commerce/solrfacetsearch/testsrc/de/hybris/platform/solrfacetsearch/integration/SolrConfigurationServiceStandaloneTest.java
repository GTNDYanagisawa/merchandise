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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexUpdateException;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.solr.LanguageSynonymMappings;
import de.hybris.platform.solrfacetsearch.solr.SolrConfigurationService;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrDocumentList;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * This test will fail if there is no standalone SOLR running. To test the standalone option, please run standalone SOLR
 * server and review the 'solr.standalone.test.instance' property for the solr's URL.
 * 
 */
@Ignore
@IntegrationTest
public class SolrConfigurationServiceStandaloneTest extends AbstractSolrTest
{

	private SolrServer solrServer;

	@Resource
	private SolrConfigurationService solrConfigurationService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		solrServer = getSolrService().getSolrServerMaster(solrConfig, indexedType);

	}

	@Override
	protected SolrServerConfigModel setUpSolrServerConfig()
	{
		return setUpStandaloneSolrServerConfig();
	}


	@After
	public void tearDownSynonyms() throws IndexUpdateException
	{
		if (solrServer != null)
		{
			solrConfigurationService.updateSynonyms(solrServer,
					new LanguageSynonymMappings().addMapping("de", "").addMapping("en", ""));
		}
	}

	@Test
	public void testUpdateSynonyms() throws SolrServerException, IOException, FacetSearchException, IndexUpdateException
	{
		final String xmlFile = readXmlFile("/test/SolrConfigurationServiceEmbeddedTest.xml");
		final DirectXmlRequest xmlRequest = new DirectXmlRequest("/update", xmlFile);
		solrServer.request(xmlRequest);
		solrServer.commit();

		final SearchQuery query = new SearchQuery(facetSearchConfig, indexedType);

		query.setLanguage("en");
		query.setCurrency("eur");

		SolrDocumentList result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertTrue("Result set should not be empty", result.size() > 0);

		query.search("bank");
		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("One bank exists for English", 1, result.size());

		query.search("bank");
		query.setLanguage("de");

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("One hit expected for 'bank' in German", 1, result.size());

		solrConfigurationService.updateSynonyms(solrServer,
				new LanguageSynonymMappings().addMapping("de", "bank => sparkasse, bank"));

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("Two hits for 'bank' expected for German since we have defined synonyms", 2, result.size());

		query.clearAllFields();
		query.setLanguage("en");
		query.setCurrency("eur");
		query.search("usb");

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("One usb exists for English", 1, result.size());

		query.clearAllFields();
		query.setLanguage("en");
		query.setCurrency("eur");
		query.search("ueesbee");
		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("Zero ueesbee exists for English", 0, result.size());

		solrConfigurationService.updateSynonyms(solrServer, new LanguageSynonymMappings().addMapping("en", "ueesbee, usb"));

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("One ueesbee exists for English", 1, result.size());

		query.clearAllFields();
		query.setLanguage("de");
		query.setCurrency("eur");
		query.search("ueesbee");

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("Zero ueesbee exists for German", 0, result.size());

		solrConfigurationService.updateSynonyms(solrServer, new LanguageSynonymMappings().addMapping("de", "ueesbee, usb"));

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("One ueesbee exists for German", 1, result.size());
	}
}
