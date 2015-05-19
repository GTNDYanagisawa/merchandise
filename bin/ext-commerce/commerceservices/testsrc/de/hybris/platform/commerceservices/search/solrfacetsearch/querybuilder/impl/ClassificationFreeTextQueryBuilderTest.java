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
package de.hybris.platform.commerceservices.search.solrfacetsearch.querybuilder.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


// Had to mark this as an IntegrationTest because the solrfacetsearch IndexedProperty uses the Config.getString
@IntegrationTest
public class ClassificationFreeTextQueryBuilderTest
{
	private ClassificationFreeTextQueryBuilder classificationFreeTextQueryBuilder;

	@Mock
	private FacetSearchConfig facetSearchConfig;
	@Mock
	private SearchConfig searchConfig;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classificationFreeTextQueryBuilder = new ClassificationFreeTextQueryBuilder();

		given(facetSearchConfig.getSearchConfig()).willReturn(searchConfig);
		given(searchConfig.getDefaultSortOrder()).willReturn(Collections.<String> emptyList());
	}

	@Test
	public void testConvert()
	{
		final IndexedType indexedType = mock(IndexedType.class);
		final SearchQuery searchQuery = spy(new SearchQuery(facetSearchConfig, indexedType));
		searchQuery.setLanguage("en");
		searchQuery.setCurrency("GBP");

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(classAttributeAssignmentModel.getSearchable()).willReturn(Boolean.TRUE);

		final IndexedProperty commerceIndexedProperty = new IndexedProperty();
		commerceIndexedProperty.setName("code");
		commerceIndexedProperty.setType("string");

		commerceIndexedProperty.setClassAttributeAssignment(classAttributeAssignmentModel);
		given(indexedType.getIndexedProperties()).willReturn(Collections.singletonMap("code", commerceIndexedProperty));

		//	given(indexedType.getIndexedProperties()).willReturn(Collections.singleton((IndexedProperty) commerceIndexedProperty));

		classificationFreeTextQueryBuilder.setBoost(10);
		classificationFreeTextQueryBuilder.addFreeTextQuery(searchQuery, "full text string", new String[]
		{ "full", "text", "string" });

		verify(searchQuery).searchInField("code", "full\\ text\\ string^20.0", SearchQuery.Operator.OR);
		verify(searchQuery).searchInField("code", "full\\ text\\ string*^10.0", SearchQuery.Operator.OR);
		verify(searchQuery).searchInField("code", "full^10.0", SearchQuery.Operator.OR);
		verify(searchQuery).searchInField("code", "full*^5.0", SearchQuery.Operator.OR);
		verify(searchQuery).searchInField("code", "text^10.0", SearchQuery.Operator.OR);
		verify(searchQuery).searchInField("code", "text*^5.0", SearchQuery.Operator.OR);
		verify(searchQuery).searchInField("code", "string^10.0", SearchQuery.Operator.OR);
		verify(searchQuery).searchInField("code", "string*^5.0", SearchQuery.Operator.OR);
	}
}
