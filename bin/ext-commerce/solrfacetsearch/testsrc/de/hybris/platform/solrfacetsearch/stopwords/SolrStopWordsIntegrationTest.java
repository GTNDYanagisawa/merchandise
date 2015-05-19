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
package de.hybris.platform.solrfacetsearch.stopwords;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexUpdateException;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrStopWordModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;
import de.hybris.platform.solrfacetsearch.stopwords.services.SolrStopWordsService;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrDocumentList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * Test for update StopWords feature
 */
@IntegrationTest
public class SolrStopWordsIntegrationTest extends AbstractSolrTest
{
	private static final String DE = "de";
	private static final String EN = "en";

	@Resource
	private SolrStopWordsService solrStopWordsService;

	private SolrServer solrServer;

	private static final String StopWord = "abcdef";

	@Resource
	private CommonI18NService commonI18NService;


	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		removeAllDocumentsFromMasterServer();
		addToIndexItemsFromTestDocument();
	}

	@After
	public void afterTest() throws IndexUpdateException
	{
		removeStopWordsFromServerForLanguages(EN, DE);
	}

	@Test
	public void testSearchForStopWordBeforeUpdate() throws SolrServerException, IOException, FacetConfigServiceException,
			FacetSearchException, IndexUpdateException
	{
		final SearchQuery query = getQueryForStopWordsWithLanguage(EN);

		//when
		final SolrDocumentList result = solrServer.query(converter.convertSolrQuery(query)).getResults();

		//then
		assertThat(result).hasSize(2);
	}

	@Test
	public void testSearchForStopWordAfterUpdate() throws SolrServerException, IOException, FacetConfigServiceException,
			FacetSearchException, IndexUpdateException
	{
		//given
		final LanguageModel language = commonI18NService.getLanguage(EN);
		solrStopWordsService.updateStopWords(solrServer, language, Sets.newHashSet(StopWord));

		final SearchQuery query = getQueryForStopWordsWithLanguage(EN);

		//when
		final SolrDocumentList result = solrServer.query(converter.convertSolrQuery(query)).getResults();

		//then
		assertThat(result).isEmpty();
	}

	@Test
	public void testSearchForStopWordInOtherLanguage() throws SolrServerException, IOException, FacetConfigServiceException,
			FacetSearchException, IndexUpdateException
	{
		//given
		final LanguageModel language = commonI18NService.getLanguage(EN);
		solrStopWordsService.updateStopWords(solrServer, language, Sets.newHashSet(StopWord));

		final SearchQuery query = getQueryForStopWordsWithLanguage(DE);

		//when
		final SolrDocumentList result = solrServer.query(converter.convertSolrQuery(query)).getResults();

		//then
		assertThat(result).hasSize(1);
	}

	@Test
	public void testScenarioUsingModels() throws SolrServerException, IOException, FacetConfigServiceException,
			FacetSearchException, IndexUpdateException
	{
		//search before update
		final SearchQuery query = getQueryForStopWordsWithLanguage(EN);
		SolrDocumentList result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertThat(result).hasSize(2);

		final SearchQuery query2 = new SearchQuery(facetSearchConfig, indexedType);
		query2.setCurrency("eur");
		query2.setLanguage(EN);
		query2.search("piece");

		result = solrServer.query(converter.convertSolrQuery(query2)).getResults();
		assertThat(result).hasSize(1);

		//update
		final LanguageModel language = commonI18NService.getLanguage(EN);

		final SolrStopWordModel StopWord1 = createStopWord(StopWord, language);
		final SolrStopWordModel StopWord2 = createStopWord("piece", language);
		localConfig.setStopWords(Lists.newArrayList(StopWord1, StopWord2));
		modelService.save(localConfig);

		solrStopWordsService.updateStopWords(language, localConfig);

		//then query
		result = solrServer.query(converter.convertSolrQuery(query)).getResults();
		assertThat(result).isEmpty();

		result = solrServer.query(converter.convertSolrQuery(query2)).getResults();
		assertThat(result).isEmpty();

	}

	private SolrStopWordModel createStopWord(final String word, final LanguageModel language)
	{
		final SolrStopWordModel StopWord1 = modelService.create(SolrStopWordModel.class);
		StopWord1.setStopWord(word);
		StopWord1.setLanguage(language);
		StopWord1.setFacetSearchConfig(localConfig);
		modelService.save(StopWord1);
		return StopWord1;
	}

	private void addToIndexItemsFromTestDocument() throws SolrServerException, IOException
	{
		final String xmlFile = readXmlFile("/test/SolrConfigurationServiceEmbeddedTest.xml");
		final DirectXmlRequest xmlRequest = new DirectXmlRequest("/update", xmlFile);
		solrServer.request(xmlRequest);
		solrServer.commit();
	}

	private void removeAllDocumentsFromMasterServer() throws SolrServiceException, SolrServerException, IOException
	{
		solrServer = getSolrService().getSolrServerMaster(solrConfig, indexedType);
		solrServer.deleteByQuery("*:*");
		solrServer.commit();
	}

	private SearchQuery getQueryForStopWordsWithLanguage(final String lang)
	{
		final SearchQuery query = new SearchQuery(facetSearchConfig, indexedType);
		query.setLanguage(lang);
		query.setCurrency("eur");
		query.search(StopWord);
		return query;
	}

	private void removeStopWordsFromServerForLanguages(final String... languages) throws IndexUpdateException
	{
		for (final String lang : languages)
		{
			final LanguageModel languageEn = commonI18NService.getLanguage(lang);
			solrStopWordsService.updateStopWords(solrServer, languageEn, Sets.newHashSet(""));
		}
	}
}
