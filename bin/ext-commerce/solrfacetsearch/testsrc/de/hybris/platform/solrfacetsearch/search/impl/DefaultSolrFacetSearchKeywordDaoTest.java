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

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.enums.KeywordRedirectMatchType;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrFacetSearchKeywordRedirectModel;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrURIRedirectModel;

import java.util.Collections;

import javax.annotation.Resource;

import org.fest.assertions.Fail;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class DefaultSolrFacetSearchKeywordDaoTest extends AbstractSolrTest
{


	@Resource
	DefaultSolrFacetSearchKeywordDao defaultSolrFacetSearchKeywordDao;

	@Resource
	DefaultModelService modelService;

	@Resource
	de.hybris.platform.servicelayer.type.TypeService typeService;

	SolrFacetSearchKeywordRedirectModel solrFacetSearchKeywordRedirect;
	SolrFacetSearchConfigModel solrFacetSearchConfig;
	LanguageModel language;
	LanguageModel language2;
	private static final String KEYWORD_STRING_1 = "keyword1";

	/**
	 * 
	 */
	@Override
	@Before
	public void setUp() throws Exception
	{
		language = new LanguageModel();
		language.setIsocode("pl");
		modelService.save(language);

		language2 = new LanguageModel();
		language2.setIsocode("cz");
		modelService.save(language2);

		final SolrSearchConfigModel solrSearchConfigModel = new SolrSearchConfigModel();
		solrSearchConfigModel.setPageSize(Integer.valueOf(10));

		final SolrIndexerQueryModel solrIndexerQueryModel = new SolrIndexerQueryModel();
		solrIndexerQueryModel.setIdentifier("id");
		solrIndexerQueryModel.setType(IndexerOperationValues.FULL);
		solrIndexerQueryModel.setQuery("select * from {Product}");

		final SolrIndexConfigModel solrIndexConfigModel = new SolrIndexConfigModel();
		solrIndexConfigModel.setName("Name");

		final SolrIndexedTypeModel indexTypes = new SolrIndexedTypeModel();
		indexTypes.setIdentifier("id");
		indexTypes.setType(typeService.getComposedTypeForClass(ProductModel.class));
		indexTypes.setSolrIndexerQueries(Collections.singletonList(solrIndexerQueryModel));
		solrFacetSearchConfig = new SolrFacetSearchConfigModel();
		solrFacetSearchConfig.setSolrSearchConfig(solrSearchConfigModel);
		solrFacetSearchConfig.setSolrIndexedTypes(Collections.singletonList(indexTypes));
		solrFacetSearchConfig.setName("name");
		solrFacetSearchConfig.setSolrServerConfig(setUpSolrServerConfig());
		solrFacetSearchConfig.setSolrIndexConfig(solrIndexConfigModel);

		modelService.saveAll(solrFacetSearchConfig);
	}

	private SolrFacetSearchKeywordRedirectModel createKeyword(final String keyword, final LanguageModel language,
			final KeywordRedirectMatchType matchType)
	{
		final SolrFacetSearchKeywordRedirectModel result = modelService.create(SolrFacetSearchKeywordRedirectModel.class);
		result.setKeyword(keyword.trim());
		result.setFacetSearchConfig(solrFacetSearchConfig);
		result.setMatchType(matchType);
		result.setLanguage(language);

		final SolrURIRedirectModel redir = new SolrURIRedirectModel();
		redir.setUrl("www.hybris.com");
		result.setRedirect(redir);

		modelService.save(result);
		return result;
	}

	@Test
	public void createInvalidKeywordTest()
	{
		createKeyword(KEYWORD_STRING_1, language, KeywordRedirectMatchType.CONTAINS);
		try
		{
			createKeyword(KEYWORD_STRING_1, language, KeywordRedirectMatchType.CONTAINS);
			Fail.fail();
		}
		catch (final ModelSavingException e)
		{
			//ok

		}
		createKeyword(KEYWORD_STRING_1, language2, KeywordRedirectMatchType.CONTAINS);
	}
}
