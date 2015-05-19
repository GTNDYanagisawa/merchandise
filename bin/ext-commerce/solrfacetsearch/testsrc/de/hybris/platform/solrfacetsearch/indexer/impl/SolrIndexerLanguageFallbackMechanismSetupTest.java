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
package de.hybris.platform.solrfacetsearch.indexer.impl;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;


/**
 * Setup for test language fallback mechanism in solr. Set up language (add Japanese language and set English language
 * as fallback language for German language), set up index properties and add product
 */
@Ignore
public class SolrIndexerLanguageFallbackMechanismSetupTest extends AbstractSolrTest
{
	@Resource
	protected FacetSearchService facetSearchService;

	private LanguageModel japaneseLanguage;

	protected static final String PRODUCT_CODE = "testId";
	protected static final String NAME_IN_ENGLISH = "NameInEnglish";
	protected static final String DESCRIPTION_IN_ENGLISH = "DescriptionInEnglish";

	protected static final String FIELD_NAME = "name";
	protected static final String FIELD_DESCRIPTION = "description";

	protected boolean PRODUCT_SHOULD_BE_FOUND = true;
	protected boolean PRODUCT_SHOULD_NOT_BE_FOUND = false;

	@Override
	protected void setUpBasic() throws Exception
	{
		super.setUpBasic();
		addJapaneseLanguage();
		setFallbackLanguageForDE();
	}

	private void addJapaneseLanguage()
	{
		japaneseLanguage = modelService.get(getOrCreateLanguage("ja"));
		japaneseLanguage.setActive(Boolean.TRUE);
		japaneseLanguage.setFallbackLanguages(Collections.EMPTY_LIST);
		modelService.save(japaneseLanguage);
	}

	/**
	 * Languages for SolrFacetSearchConfigModel: EN, DE, JA
	 */
	@Override
	protected List<LanguageModel> setUpLanguages()
	{
		final List<LanguageModel> langForSolrFacetSearchConfig = super.setUpLanguages();
		langForSolrFacetSearchConfig.add(japaneseLanguage);
		return langForSolrFacetSearchConfig;
	}

	private void setFallbackLanguageForDE()
	{
		final LanguageModel languageDE = commonI18NService.getLanguage("de");
		final LanguageModel languageEN = commonI18NService.getLanguage("en");

		languageDE.setFallbackLanguages(Collections.singletonList(languageEN));
		modelService.save(languageDE);
	}

	/**
	 * add one product with set name and description in English (Japan and German not set)
	 */
	@Override
	protected void setUpProductData() throws Exception
	{
		super.setUpProductData();
		final ProductModel product = modelService.create(ProductModel.class);

		product.setCode(PRODUCT_CODE);
		product.setName(NAME_IN_ENGLISH, Locale.ENGLISH);
		product.setName(null, Locale.GERMAN);
		product.setName(null, Locale.JAPAN);

		product.setDescription(DESCRIPTION_IN_ENGLISH, Locale.ENGLISH);
		product.setDescription(null, Locale.GERMAN);
		product.setDescription(null, Locale.JAPAN);

		product.setCatalogVersion(cv);
		modelService.save(product);
	}

	/**
	 * set up indexed properties: code, name and description
	 */
	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{
		final SolrIndexedPropertyModel codeProperty = modelService.create(SolrIndexedPropertyModel.class);
		codeProperty.setName("code");
		codeProperty.setType(SolrPropertiesTypes.STRING);

		final SolrIndexedPropertyModel nameProperty = modelService.create(SolrIndexedPropertyModel.class);
		nameProperty.setName(FIELD_NAME);
		nameProperty.setType(SolrPropertiesTypes.TEXT);
		nameProperty.setLocalized(true);

		final SolrIndexedPropertyModel descriptionProperty = modelService.create(SolrIndexedPropertyModel.class);
		descriptionProperty.setName(FIELD_DESCRIPTION);
		descriptionProperty.setType(SolrPropertiesTypes.TEXT);
		descriptionProperty.setLocalized(true);

		return Arrays.asList(codeProperty, nameProperty, descriptionProperty);
	}

	/**
	 * set current language, search in field {@value #FIELD_NAME} and {@value #FIELD_DESCRIPTION}, and check if product
	 * can be found
	 * 
	 * @param productShouldBeFound
	 *           - if set to true, assert that search result contains 1 element, otherwise 0
	 * @param isoCode
	 *           - isocode for language which will be set as current
	 */
	protected void setLanguageAndCheckSearchResults(final String isoCode, final boolean productShouldBeFound) throws Exception
	{
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(isoCode));

		// if the fall back languages list is empty, we must be expecting no results - and the other way round.
		// this is supposed to prevent a situation were a fallback language has been added
		//Assume.assumeTrue(commonI18NService.getCurrentLanguage().getFallbackLanguages().isEmpty() == !productShouldBeFound);

		SearchQuery query = new SearchQuery(facetSearchConfig, indexedType);
		query.searchInField(FIELD_NAME, NAME_IN_ENGLISH);

		SearchResult search = facetSearchService.search(query);
		List<ProductModel> products = (List<ProductModel>) search.getResults();
		Assert.assertNotNull(products);
		Assert.assertTrue(products.size() == (productShouldBeFound ? 1 : 0));

		query = new SearchQuery(facetSearchConfig, indexedType);
		query.searchInField(FIELD_DESCRIPTION, DESCRIPTION_IN_ENGLISH);

		search = facetSearchService.search(query);
		products = (List<ProductModel>) search.getResults();
		Assert.assertNotNull(products);
		Assert.assertTrue(products.size() == (productShouldBeFound ? 1 : 0));
	}

}
