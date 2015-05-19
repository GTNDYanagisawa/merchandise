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
package de.hybris.platform.solrfacetsearch.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.SolrSynonymService;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexUpdateException;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrSynonymConfigModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrDocumentList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class SolrConfigurationServiceEmbeddedTest extends AbstractSolrTest
{

	@Resource(name = "solrConfigurationService")
	private SolrConfigurationService solrConfigurationService;

	@Resource(name = "solrSynonymService")
	private SolrSynonymService solrSynonymService;

	private SolrServer solrServer;

	@Resource
	private CommonI18NService commonI18NService;



	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		solrServer = getSolrService().getSolrServerMaster(solrConfig, indexedType);

		solrServer.deleteByQuery("*:*");
		solrServer.commit();

		final String xmlFile = readXmlFile("/test/SolrConfigurationServiceEmbeddedTest.xml");
		final DirectXmlRequest xmlRequest = new DirectXmlRequest("/update", xmlFile);
		solrServer.request(xmlRequest);
		solrServer.commit();
	}

	@After
	public void tearDownSolrConfig() throws IndexUpdateException
	{
		solrConfigurationService
				.updateSynonyms(solrServer, new LanguageSynonymMappings().addMapping("de", "").addMapping("en", ""));
	}

	@Test
	public void testUpdateSynonyms() throws SolrServerException, IOException, FacetConfigServiceException, FacetSearchException,
			IndexUpdateException
	{
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

		query.setLanguage("de");

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("Zero ueesbee exists for German", 0, result.size());

		solrConfigurationService.updateSynonyms(solrServer, new LanguageSynonymMappings().addMapping("de", "ueesbee, usb"));

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("One ueesbee exists for German", 1, result.size());
	}

	@Test
	public void testUpdateSynonymsFromModel() throws SolrServerException, IOException, FacetConfigServiceException,
			FacetSearchException, IndexUpdateException
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

		final SolrFacetSearchConfigModel configuration = new SolrFacetSearchConfigModel();
		configuration.setName(SOLR_CONFIG_NAME);

		final Locale loc = new Locale("de");
		final LanguageModel lang = commonI18NService.getLanguage(loc.getLanguage());

		final List<SolrSynonymConfigModel> value = new ArrayList<SolrSynonymConfigModel>();
		final SolrSynonymConfigModel synonym = new SolrSynonymConfigModel();
		synonym.setSynonymFrom("bank");
		synonym.setSynonymTo("sparkasse, bank");
		synonym.setLanguage(lang);
		value.add(synonym);
		configuration.setSynonyms(value);


		solrSynonymService.updateSynonyms(loc, configuration);

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

		query.setLanguage("de");

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("Zero ueesbee exists for German", 0, result.size());

		solrConfigurationService.updateSynonyms(solrServer, new LanguageSynonymMappings().addMapping("de", "ueesbee, usb"));

		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("One ueesbee exists for German", 1, result.size());

	}

}
