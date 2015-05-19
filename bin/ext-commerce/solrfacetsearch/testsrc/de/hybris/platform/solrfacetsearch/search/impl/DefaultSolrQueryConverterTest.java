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

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.CoupledQueryField;
import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class DefaultSolrQueryConverterTest
{
	private static final String TEST_EN_LANG_CODE = "en";
	private static final String TEST_EN_CURRENCY_CODE = "GBP";

	//Categories

	private static final String TEST_FACET_CATEGORY_NAME = "manufacturer";
	private static final String TEST_FACET_CATEGORY_TRANSLATED_NAME = "manufacturer_string_mv";
	private static final String TEST_FACET_CATEGORY_VALUE1 = "Dell";
	private static final String TEST_FACET_CATEGORY_VALUE2 = "Maxtor";

	//Catalog Version
	private static final String TEST_CATALOG_VERSION_VER = "Online";
	private static final String TEST_CATALOG_VERSION_ID = "apparelProductCatalog";

	private static final int TEST_QUERY_PG_SIZE = 5;
	private static final int TEST_QUERY_OFFSET = 0;

	@Mock
	private SearchQuery solrSearchQuery;
	@Mock
	private FieldNameProvider solrFieldNameProvider;
	@Mock
	private IndexedType indexedType;
	@Mock
	private CatalogVersionModel catalogVersion;

	private DefaultSolrQueryConverter defaultSolrQueryConverter;
	private List<QueryField> queryFields;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	protected void configureCatalogVersion()
	{
		given(catalogVersion.getVersion()).willReturn(TEST_CATALOG_VERSION_VER);
		final CatalogModel catalogModel = mock(CatalogModel.class);
		given(catalogModel.getId()).willReturn(TEST_CATALOG_VERSION_ID);
		given(catalogVersion.getCatalog()).willReturn(catalogModel);
	}

	protected void configureIndexTypeAndProperty(final FacetType facetType)
	{
		//Category Facet
		final IndexedProperty facetCategoryIndexedProperty = mock(IndexedProperty.class);
		given(Boolean.valueOf(facetCategoryIndexedProperty.isLocalized())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(facetCategoryIndexedProperty.isCurrency())).willReturn(Boolean.FALSE);
		given((facetCategoryIndexedProperty).getFacetType()).willReturn(facetType);
		given(indexedType.getIndexedProperties()).willReturn(
				Collections.singletonMap(TEST_FACET_CATEGORY_NAME, facetCategoryIndexedProperty));
		given(solrFieldNameProvider.getFieldName(facetCategoryIndexedProperty, null, FieldNameProvider.FieldType.INDEX))
				.willReturn(TEST_FACET_CATEGORY_TRANSLATED_NAME);
		//Indextype Facets
		final Set<String> facets = new HashSet<String>(3);
		facets.add(TEST_FACET_CATEGORY_NAME);
		given(indexedType.getTypeFacets()).willReturn(facets);
	}

	protected void configureQuery()
	{
		given(solrSearchQuery.getLanguage()).willReturn(TEST_EN_LANG_CODE);
		given(solrSearchQuery.getCurrency()).willReturn(TEST_EN_CURRENCY_CODE);
		given(solrSearchQuery.getCatalogVersions()).willReturn(Collections.singletonList(catalogVersion));
		given(solrSearchQuery.getIndexedType()).willReturn(indexedType);
		given(solrSearchQuery.getRawQuery()).willReturn(null);
		given(solrSearchQuery.getSolrParams()).willReturn(Collections.<String, String[]> emptyMap()); //MMMX: Will need to change for coverage

		given(Integer.valueOf(solrSearchQuery.getPageSize())).willReturn(Integer.valueOf(TEST_QUERY_PG_SIZE));
		given(Integer.valueOf(solrSearchQuery.getOffset())).willReturn(Integer.valueOf(TEST_QUERY_OFFSET));

		//queryFields
		queryFields = new ArrayList<QueryField>();
		final QueryField queryField = new QueryField(TEST_FACET_CATEGORY_NAME, TEST_FACET_CATEGORY_VALUE1, SearchQuery.Operator.AND);
		queryField.getValues().add(TEST_FACET_CATEGORY_VALUE2);
		queryFields.add(queryField);
		given(solrSearchQuery.getAllFields()).willReturn(queryFields);
	}

	@Test
	public void testConvertQueryForFacetTypeMultiSelectOr() throws Exception
	{
		configureCatalogVersion();
		configureIndexTypeAndProperty(FacetType.MULTISELECTOR);
		configureQuery();

		defaultSolrQueryConverter = new DefaultSolrQueryConverter();
		defaultSolrQueryConverter.setSolrFieldNameProvider(solrFieldNameProvider);
		String result = defaultSolrQueryConverter.convertSolrQuery(solrSearchQuery).toString();
		result = URLDecoder.decode(result, "UTF-8");

		assertTrue("fq not as expected", result.contains("fq={!tag=fk0}(manufacturer_string_mv:(Maxtor OR Dell))"));
		assertTrue("facet.field not as expected", result.contains("facet.field={!ex=fk0}manufacturer_string_mv"));
	}

	@Test
	public void testConvertQueryForFacetTypeMultiSelectAnd() throws Exception
	{
		configureCatalogVersion();
		configureIndexTypeAndProperty(FacetType.MULTISELECTAND);
		configureQuery();

		defaultSolrQueryConverter = new DefaultSolrQueryConverter();
		defaultSolrQueryConverter.setSolrFieldNameProvider(solrFieldNameProvider);
		String result = defaultSolrQueryConverter.convertSolrQuery(solrSearchQuery).toString();
		result = URLDecoder.decode(result, "UTF-8");

		assertTrue("fq not as expected", result.contains("fq={!tag=fk0}(manufacturer_string_mv:(Maxtor AND Dell))"));
		assertTrue("facet.field not as expected", result.contains("facet.field={!ex=fk0}manufacturer_string_mv"));
	}

	@Test
	public void testConvertQueryForFacetTypeRefine() throws Exception
	{
		configureCatalogVersion();
		configureIndexTypeAndProperty(FacetType.REFINE);
		configureQuery();

		defaultSolrQueryConverter = new DefaultSolrQueryConverter();
		defaultSolrQueryConverter.setSolrFieldNameProvider(solrFieldNameProvider);
		String result = defaultSolrQueryConverter.convertSolrQuery(solrSearchQuery).toString();
		result = URLDecoder.decode(result, "UTF-8");

		assertTrue("fq not as expected", result.contains("fq=(manufacturer_string_mv:(Maxtor AND Dell))"));
		assertTrue("facet.field not as expected", result.contains("facet.field=manufacturer_string_mv"));
	}

	@Test
	public void testConvertQueryWithSimpleCoupledFields() throws Exception
	{
		final QueryField queryField1 = new QueryField("catalogId", "TestCatalog1");
		final QueryField queryField2 = new QueryField("version", "Online");

		final QueryField queryField3 = new QueryField("catalogId", "TestCatalog2");
		final QueryField queryField4 = new QueryField("version", "Staged");

		final CoupledQueryField couple1 = new CoupledQueryField("testCouple", queryField1, queryField2, Operator.AND, Operator.OR);
		final CoupledQueryField couple2 = new CoupledQueryField("testCouple", queryField3, queryField4, Operator.AND, Operator.OR);
		configureCatalogVersion();
		configureIndexTypeAndProperty(FacetType.REFINE);
		configureQuery();
		final List<CoupledQueryField> couples = Arrays.asList(couple1, couple2);

		given(solrSearchQuery.getCoupledFields()).willReturn(couples);
		defaultSolrQueryConverter = new DefaultSolrQueryConverter();
		defaultSolrQueryConverter.setSolrFieldNameProvider(solrFieldNameProvider);
		String result = defaultSolrQueryConverter.convertSolrQuery(solrSearchQuery).toString();
		result = URLDecoder.decode(result, "UTF-8");

		assertTrue(
				"fq not as expected",
				result.contains("q=(((catalogId:TestCatalog1) AND (version:Online)) OR ((catalogId:TestCatalog2) AND (version:Staged)))"));
	}

	@Test
	public void testConvertQueryWithComplicatedCoupledFields() throws Exception
	{
		final Set<String> catalog1Values = new HashSet<String>(3);
		catalog1Values.add("TestCatalog1A");
		catalog1Values.add("TestCatalog1B");
		catalog1Values.add("TestCatalog1C");
		final QueryField queryField1 = new QueryField("catalogId", catalog1Values, Operator.AND);

		final Set<String> versions1 = new HashSet<String>(3);
		versions1.add("version1");
		versions1.add("version2");
		versions1.add("version3");
		final QueryField queryField2 = new QueryField("version", versions1, Operator.OR);

		final Set<String> catalog2Values = new HashSet<String>(3);
		catalog2Values.add("TestCatalog2A");
		catalog2Values.add("TestCatalog2B");
		catalog2Values.add("TestCatalog2C");
		final QueryField queryField3 = new QueryField("catalogId", catalog2Values, Operator.OR);

		final Set<String> versions2 = new HashSet<String>(3);
		versions2.add("versionA");
		versions2.add("versionB");
		versions2.add("versionC");
		final QueryField queryField4 = new QueryField("version", versions2, Operator.AND);

		final CoupledQueryField couple1 = new CoupledQueryField("testCouple", queryField1, queryField2, Operator.AND, Operator.AND);
		final CoupledQueryField couple2 = new CoupledQueryField("testCouple", queryField3, queryField4, Operator.OR, Operator.AND);
		configureCatalogVersion();
		configureIndexTypeAndProperty(FacetType.REFINE);
		configureQuery();
		final List<CoupledQueryField> couples = Arrays.asList(couple1, couple2);

		given(solrSearchQuery.getCoupledFields()).willReturn(couples);
		defaultSolrQueryConverter = new DefaultSolrQueryConverter();
		defaultSolrQueryConverter.setSolrFieldNameProvider(solrFieldNameProvider);
		String result = defaultSolrQueryConverter.convertSolrQuery(solrSearchQuery).toString();
		result = URLDecoder.decode(result, "UTF-8");

		assertTrue(
				"q not as expected",
				result.contains("q=(((catalogId:(TestCatalog1C AND TestCatalog1B AND TestCatalog1A)) AND (version:(version3 OR version2 OR version1))) AND ((catalogId:(TestCatalog2A OR TestCatalog2B OR TestCatalog2C)) OR (version:(versionA AND versionB AND versionC))))"));
	}

}
