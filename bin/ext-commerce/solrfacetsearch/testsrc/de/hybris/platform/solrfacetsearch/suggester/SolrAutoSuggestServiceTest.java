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
package de.hybris.platform.solrfacetsearch.suggester;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexUpdateException;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;
import de.hybris.platform.solrfacetsearch.suggester.exceptions.SolrAutoSuggestException;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SolrAutoSuggestServiceTest extends AbstractSolrTest
{

	private SolrServer solrServer;

	@Resource(name = "solrAutoSuggestService")
	private SolrAutoSuggestService solrAutoSuggestService;

	@Resource
	private CommonI18NService commonI18NService;

	private LanguageModel english;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		solrServer = getSolrService().getSolrServerMaster(solrConfig, indexedType);
		english = commonI18NService.getLanguage("en");

		solrServer.deleteByQuery("*:*");
		solrServer.commit();

		final String xmlFile = readXmlFile("/test/SolrSuggesterEmbeddedTest.xml");
		final DirectXmlRequest xmlRequest = new DirectXmlRequest("/update", xmlFile);
		solrServer.request(xmlRequest);
        solrServer.commit();

    }

	@Test
	@After
	public void testSuggestions() throws SolrAutoSuggestException, IndexUpdateException
	{
		SolrSuggestion suggestion = null;

		suggestion = solrAutoSuggestService.getAutoSuggestionsForQuery(english, indexedTypeModel, "anyWord");
		assertThat(suggestion.getSuggestions().containsKey("anyWord")).isFalse();

		suggestion = solrAutoSuggestService.getAutoSuggestionsForQuery(english, indexedTypeModel, "suggest"); //intentionally slightly mis-spelled
        assertThat(suggestion.getSuggestions().keySet().size()).isGreaterThan(0);
		assertThat(suggestion.getSuggestions().get("suggest")).isNotEmpty();

		suggestion = solrAutoSuggestService.getAutoSuggestionsForQuery(english, indexedTypeModel, "des");
		assertThat(suggestion.getSuggestions().get("des")).isNull();
	}
}
