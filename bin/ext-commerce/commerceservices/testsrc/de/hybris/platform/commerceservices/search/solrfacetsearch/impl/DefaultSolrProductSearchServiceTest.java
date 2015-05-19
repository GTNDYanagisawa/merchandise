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
package de.hybris.platform.commerceservices.search.solrfacetsearch.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchFiltersPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchPagePopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseBreadcrumbsPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseCategoryCodePopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseFacetsPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseFreeTextSearchPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseKeywordRedirectPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponsePaginationPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseQueryPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseResultsPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseSortsPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseSpellingSuggestionPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseSubCategoriesPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchSolrQueryPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchSortPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchTextPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SolrSearchRequestResponsePopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.querybuilder.FreeTextQueryBuilder;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.SolrKeywordRedirectService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


// Had to mark this as an IntegrationTest because the solrfacetsearch IndexedProperty uses the Config.getString
@IntegrationTest
public class DefaultSolrProductSearchServiceTest
{
	private DefaultSolrProductSearchService<SearchResultValueData> productSearchService;

	@Mock
	private BaseSiteService siteService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private FacetSearchService solrFacetSearchService;
	@Mock
	private FacetSearchConfigService facetSearchConfigService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private SearchConfig searchConfig;
	@Mock
	private FacetSearchConfig facetSearchConfig;
	@Mock
	private IndexedType commerceIndexedType;
	@Mock
	private SolrKeywordRedirectService keywordRedirectService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;
	@Mock
	private final de.hybris.platform.commerceservices.converter.impl.AbstractPopulatingConverter<SolrFacetSearchConfigModel, FacetSearchConfig> facetSearchConfigConverter = new de.hybris.platform.commerceservices.converter.impl.AbstractPopulatingConverter<SolrFacetSearchConfigModel, FacetSearchConfig>();



	@Before
	public void setUp() throws FacetConfigServiceException, NoValidSolrConfigException
	{
		MockitoAnnotations.initMocks(this);

		final SearchSolrQueryPopulator searchSolrQueryPopulator = new SearchSolrQueryPopulator();
		searchSolrQueryPopulator.setBaseSiteService(siteService);
		searchSolrQueryPopulator.setCatalogVersionService(catalogVersionService);
		searchSolrQueryPopulator.setCommonI18NService(commonI18NService);
		searchSolrQueryPopulator.setBaseStoreService(baseStoreService);
		searchSolrQueryPopulator.setSolrFacetSearchConfigSelectionStrategy(solrFacetSearchConfigSelectionStrategy);
		searchSolrQueryPopulator.setFacetSearchConfigConverter(facetSearchConfigConverter);


		final SearchPagePopulator searchPagePopulator = new SearchPagePopulator();
		final SearchSortPopulator searchSortPopulator = new SearchSortPopulator();
		final SearchTextPopulator searchTextPopulator = new SearchTextPopulator();
		searchTextPopulator.setFreeTextQueryBuilders(Collections.<FreeTextQueryBuilder> emptyList());

		final SearchFiltersPopulator searchFiltersPopulator = new SearchFiltersPopulator();

		final SearchQueryPageableConverter searchQueryPageableConverter = new SearchQueryPageableConverter();
		final ArrayList<Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest>> searchQueryPageableConverterPopulators = new ArrayList<Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest>>();
		searchQueryPageableConverterPopulators.add(searchSolrQueryPopulator);
		searchQueryPageableConverterPopulators.add(searchPagePopulator);
		searchQueryPageableConverterPopulators.add(searchSortPopulator);
		searchQueryPageableConverterPopulators.add(searchTextPopulator);
		searchQueryPageableConverterPopulators.add(searchFiltersPopulator);
		searchQueryPageableConverter.setPopulators(searchQueryPageableConverterPopulators);

		final SolrSearchRequestResponsePopulator solrSearchRequestResponsePopulator = new SolrSearchRequestResponsePopulator();
		solrSearchRequestResponsePopulator.setSolrFacetSearchService(solrFacetSearchService);
		solrSearchRequestResponsePopulator.setSolrKeywordRedirectService(keywordRedirectService);

		final SolrSearchRequestConverter solrSearchRequestConverter = new SolrSearchRequestConverter();
		final ArrayList<Populator<SolrSearchRequest, SolrSearchResponse>> solrSearchRequestConverterPopulators = new ArrayList<Populator<SolrSearchRequest, SolrSearchResponse>>();
		solrSearchRequestConverterPopulators.add(solrSearchRequestResponsePopulator);
		solrSearchRequestConverter.setPopulators(solrSearchRequestConverterPopulators);

		final SearchResponseQueryPopulator searchResponseQueryPopulator = new SearchResponseQueryPopulator();
		final SearchResponsePaginationPopulator searchResponsePaginationPopulator = new SearchResponsePaginationPopulator();
		final SearchResponseResultsPopulator searchResponseResultsPopulator = new SearchResponseResultsPopulator();
		final SearchResponseFacetsPopulator searchResponseFacetsPopulator = new SearchResponseFacetsPopulator();
		final SearchResponseBreadcrumbsPopulator searchResponseBreadcrumbsPopulator = new SearchResponseBreadcrumbsPopulator();
		final SearchResponseSortsPopulator searchResponseSortsPopulator = new SearchResponseSortsPopulator();
		final SearchResponseFreeTextSearchPopulator searchResponseFreeTextSearchPopulator = new SearchResponseFreeTextSearchPopulator();
		final SearchResponseCategoryCodePopulator searchResponseCategoryCodePopulator = new SearchResponseCategoryCodePopulator();
		final SearchResponseSubCategoriesPopulator searchResponseSubCategoriesPopulator = new SearchResponseSubCategoriesPopulator();
		final SearchResponseKeywordRedirectPopulator searchResponseKeywordRedirectPopulator = new SearchResponseKeywordRedirectPopulator();
		final SearchResponseSpellingSuggestionPopulator searchResponseSpellingSuggestionPopulator = new SearchResponseSpellingSuggestionPopulator();

		final SolrSearchResponseConverter solrSearchResponseConverter = new SolrSearchResponseConverter();
		final List solrSearchResponseConverterPopulators = Arrays.asList(searchResponseQueryPopulator,
				searchResponsePaginationPopulator, searchResponseResultsPopulator, searchResponseFacetsPopulator,
				searchResponseBreadcrumbsPopulator, searchResponseSortsPopulator, searchResponseFreeTextSearchPopulator,
				searchResponseCategoryCodePopulator, searchResponseSubCategoriesPopulator, searchResponseKeywordRedirectPopulator,
				searchResponseSpellingSuggestionPopulator);
		solrSearchResponseConverter.setPopulators(solrSearchResponseConverterPopulators);

		productSearchService = new DefaultSolrProductSearchService<SearchResultValueData>();
		productSearchService.setSearchQueryPageableConverter(searchQueryPageableConverter);
		productSearchService.setSearchRequestConverter(solrSearchRequestConverter);
		productSearchService.setSearchResponseConverter(solrSearchResponseConverter);

		final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
		final CatalogModel catalogModel = mock(CatalogModel.class);
		final CatalogVersionModel catalogVersionModel = mock(CatalogVersionModel.class);
		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = mock(SolrFacetSearchConfigModel.class);
		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);

		given(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig()).willReturn(solrFacetSearchConfigModel);
		given(solrFacetSearchConfigModel.getName()).willReturn("testConfig");
		given(facetSearchConfigService.getConfiguration("testConfig")).willReturn(facetSearchConfig);
		given(baseStoreModel.getSolrFacetSearchConfiguration()).willReturn(null);
		given(baseSiteModel.getSolrFacetSearchConfiguration()).willReturn(null);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(siteService.getCurrentBaseSite()).willReturn(baseSiteModel);
		given(siteService.getProductCatalogs(baseSiteModel)).willReturn(Collections.singletonList(catalogModel));
		given(catalogVersionService.getSessionCatalogVersions()).willReturn(Collections.singleton(catalogVersionModel));
		given(catalogVersionModel.getCatalog()).willReturn(catalogModel);
		given(catalogVersionModel.getFacetSearchConfigs()).willReturn(Collections.singletonList(solrFacetSearchConfigModel));
		given(facetSearchConfigConverter.convert(solrFacetSearchConfigModel)).willReturn(facetSearchConfig);

		final IndexConfig indexConfig = mock(IndexConfig.class);

		//given(facetSearchConfigService.getConfiguration(catalogVersionModel)).willReturn(facetSearchConfig);
		given(facetSearchConfig.getIndexConfig()).willReturn(indexConfig);
		given(indexConfig.getIndexedTypes()).willReturn(Collections.singletonMap("Product", commerceIndexedType));
		given(facetSearchConfig.getSearchConfig()).willReturn(searchConfig);
		given(searchConfig.getDefaultSortOrder()).willReturn(Collections.<String> emptyList());


		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class);

		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(currencyModel.getIsocode()).willReturn("GBP");
		given(languageModel.getIsocode()).willReturn("en");


	}

	@Test
	public void testNull() throws FacetSearchException
	{
		final SearchResult searchResult = mock(SearchResult.class);
		final QueryResponse queryResponse = mock(QueryResponse.class);
		given(solrFacetSearchService.search(Matchers.<SearchQuery> any())).willReturn(searchResult);
		given(searchResult.getSolrObject()).willReturn(queryResponse);
		given(queryResponse.getResults()).willReturn(new SolrDocumentList());

		productSearchService.textSearch(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNull3()
	{
		productSearchService.searchAgain(null, null);
	}

	@Test
	public void testEmpty() throws FacetSearchException
	{
		final SearchResult searchResult = mock(SearchResult.class);
		final QueryResponse queryResponse = mock(QueryResponse.class);
		given(solrFacetSearchService.search(Matchers.<SearchQuery> any())).willReturn(searchResult);
		given(searchResult.getSolrObject()).willReturn(queryResponse);
		given(queryResponse.getResults()).willReturn(new SolrDocumentList());

		//		final SearchPageData<SearchResultValueData> searchResultValueData = new SearchPageData<SearchResultValueData>();
		//		searchResultValueData.setBreadcrumbs(Collections.EMPTY_LIST);
		//		searchResultValueData.setFacets(Collections.EMPTY_LIST);
		//		searchResultValueData.setResults(Collections.EMPTY_LIST);
		//		searchResultValueData.setSorts(Collections.EMPTY_LIST);
		//		final SearchQueryData currentQuery = new SearchQueryData();
		//		currentQuery.setFreeTextSearch("");
		//		currentQuery.setFilterTerms(Collections.EMPTY_LIST);
		//		searchResultValueData.setCurrentQuery(currentQuery);
		//		final PaginationData paginationData = new PaginationData();
		//		paginationData.setTotalNumberOfResults(0);
		//		paginationData.setNumberOfPages(0);
		//		paginationData.setPageSize(0);
		//		paginationData.setCurrentPage(0);
		//		searchResultValueData.setPagination(paginationData);
		//		given(commerceSearchService.search("")).willReturn(searchResultValueData);

		final FacetSearchPageData<SolrSearchQueryData, SearchResultValueData> searchPageData = productSearchService.textSearch("",
				null);
		Assert.assertEquals("", searchPageData.getCurrentQuery().getFreeTextSearch());
		Assert.assertNull(searchPageData.getCurrentQuery().getCategoryCode());
		Assert.assertTrue(searchPageData.getCurrentQuery().getFilterTerms().isEmpty());

		Assert.assertTrue(searchPageData.getBreadcrumbs().isEmpty());
		Assert.assertTrue(searchPageData.getFacets().isEmpty());
		Assert.assertTrue(searchPageData.getResults().isEmpty());
		Assert.assertTrue(searchPageData.getSorts().isEmpty());

		Assert.assertEquals(0, searchPageData.getPagination().getTotalNumberOfResults());
		Assert.assertEquals(0, searchPageData.getPagination().getNumberOfPages());
		Assert.assertEquals(0, searchPageData.getPagination().getPageSize());
		Assert.assertEquals(0, searchPageData.getPagination().getCurrentPage());
		Assert.assertNull(searchPageData.getPagination().getSort());
	}

	@Test
	public void testFreeTextQuery() throws FacetSearchException
	{
		final SearchResult searchResult = mock(SearchResult.class);
		final QueryResponse queryResponse = mock(QueryResponse.class);
		given(solrFacetSearchService.search(Matchers.<SearchQuery> any())).willReturn(searchResult);
		given(searchResult.getSolrObject()).willReturn(queryResponse);
		given(queryResponse.getResults()).willReturn(new SolrDocumentList());

		final FacetSearchPageData<SolrSearchQueryData, SearchResultValueData> searchPageData = productSearchService.textSearch(
				"free text query", null);
		Assert.assertEquals("free text query", searchPageData.getCurrentQuery().getFreeTextSearch());
		Assert.assertNull(searchPageData.getCurrentQuery().getCategoryCode());
		Assert.assertTrue(searchPageData.getCurrentQuery().getFilterTerms().isEmpty());

		Assert.assertTrue(searchPageData.getBreadcrumbs().isEmpty());
		Assert.assertTrue(searchPageData.getFacets().isEmpty());
		Assert.assertTrue(searchPageData.getResults().isEmpty());
		Assert.assertTrue(searchPageData.getSorts().isEmpty());

		Assert.assertEquals(0, searchPageData.getPagination().getTotalNumberOfResults());
		Assert.assertEquals(0, searchPageData.getPagination().getNumberOfPages());
		Assert.assertEquals(0, searchPageData.getPagination().getPageSize());
		Assert.assertEquals(0, searchPageData.getPagination().getCurrentPage());
		Assert.assertNull(searchPageData.getPagination().getSort());
	}

	@Test
	public void testPageData() throws FacetSearchException
	{
		final SearchResult searchResult = mock(SearchResult.class);
		final QueryResponse queryResponse = mock(QueryResponse.class);
		given(solrFacetSearchService.search(Matchers.<SearchQuery> any())).willReturn(searchResult);
		given(searchResult.getSolrObject()).willReturn(queryResponse);
		given(queryResponse.getResults()).willReturn(new SolrDocumentList());

		given(Long.valueOf(searchResult.getTotalNumberOfResults())).willReturn(Long.valueOf(100));
		given(Integer.valueOf(searchResult.getPageSize())).willReturn(Integer.valueOf(20));
		given(Integer.valueOf(searchResult.getOffset())).willReturn(Integer.valueOf(0));
		given(Long.valueOf(searchResult.getNumberOfPages())).willReturn(Long.valueOf(5));

		final FacetSearchPageData<SolrSearchQueryData, SearchResultValueData> searchPageData = productSearchService.textSearch("",
				null);
		Assert.assertEquals("", searchPageData.getCurrentQuery().getFreeTextSearch());
		Assert.assertNull(searchPageData.getCurrentQuery().getCategoryCode());
		Assert.assertTrue(searchPageData.getCurrentQuery().getFilterTerms().isEmpty());

		Assert.assertTrue(searchPageData.getBreadcrumbs().isEmpty());
		Assert.assertTrue(searchPageData.getFacets().isEmpty());
		Assert.assertTrue(searchPageData.getResults().isEmpty());
		Assert.assertTrue(searchPageData.getSorts().isEmpty());

		Assert.assertEquals(100L, searchPageData.getPagination().getTotalNumberOfResults());
		Assert.assertEquals(5, searchPageData.getPagination().getNumberOfPages());
		Assert.assertEquals(20, searchPageData.getPagination().getPageSize());
		Assert.assertEquals(0, searchPageData.getPagination().getCurrentPage());
		Assert.assertNull(searchPageData.getPagination().getSort());
	}

	@Test
	public void testFacets() throws FacetSearchException
	{
		final SearchResult searchResult = mock(SearchResult.class);
		final QueryResponse queryResponse = mock(QueryResponse.class);
		given(solrFacetSearchService.search(Matchers.<SearchQuery> any())).willReturn(searchResult);
		given(searchResult.getSolrObject()).willReturn(queryResponse);
		given(queryResponse.getResults()).willReturn(new SolrDocumentList());

		given(Long.valueOf(searchResult.getTotalNumberOfResults())).willReturn(Long.valueOf(100));

		final FacetValue facetValue = new FacetValue("value", 7, false);
		final Facet facet = new Facet("facet", new ArrayList<FacetValue>(Collections.singleton(facetValue)));
		given(searchResult.getFacets()).willReturn(Collections.singletonList(facet));

		final IndexedProperty facetProperty = new IndexedProperty();
		facetProperty.setName("facet");
		facetProperty.setType("string");
		facetProperty.setFacet(true);
		given(commerceIndexedType.getIndexedProperties()).willReturn(Collections.singletonMap("facet", facetProperty));
		facetProperty.setPriority(100);
		//facetProperty.setFacetSort(CommerceIndexedProperty.FacetSort.Alpha);

		final FacetSearchPageData<SolrSearchQueryData, SearchResultValueData> searchPageData = productSearchService.textSearch("",
				null);
		Assert.assertEquals("", searchPageData.getCurrentQuery().getFreeTextSearch());
		Assert.assertNull(searchPageData.getCurrentQuery().getCategoryCode());
		Assert.assertTrue(searchPageData.getCurrentQuery().getFilterTerms().isEmpty());

		Assert.assertEquals(1, searchPageData.getFacets().size());
		Assert.assertEquals("facet", searchPageData.getFacets().get(0).getCode());
		Assert.assertEquals("facet", searchPageData.getFacets().get(0).getName());
		Assert.assertEquals(1, searchPageData.getFacets().get(0).getValues().size());
		Assert.assertNotNull(searchPageData.getFacets().get(0).getValues().get(0).getQuery());
		Assert.assertEquals("value", searchPageData.getFacets().get(0).getValues().get(0).getName());
		Assert.assertEquals(7, searchPageData.getFacets().get(0).getValues().get(0).getCount());
	}

	@Test
	public void testReQuery() throws FacetSearchException
	{
		final SearchResult searchResult = mock(SearchResult.class);
		final QueryResponse queryResponse = mock(QueryResponse.class);
		given(solrFacetSearchService.search(Matchers.<SearchQuery> any())).willReturn(searchResult);
		given(searchResult.getSolrObject()).willReturn(queryResponse);
		given(queryResponse.getResults()).willReturn(new SolrDocumentList());

		given(Long.valueOf(searchResult.getTotalNumberOfResults())).willReturn(Long.valueOf(100));

		final FacetValue facetValue = new FacetValue("value", 7, false);
		final Facet facet = new Facet("facet", new ArrayList<FacetValue>(Collections.singleton(facetValue)));
		given(searchResult.getFacets()).willReturn(Collections.singletonList(facet));

		final IndexedProperty facetProperty = new IndexedProperty();
		facetProperty.setName("facet");
		facetProperty.setType("string");
		facetProperty.setFacet(true);
		given(commerceIndexedType.getIndexedProperties()).willReturn(Collections.singletonMap("facet", facetProperty));
		facetProperty.setPriority(100);
		//facetProperty.setFacetSort(CommerceIndexedProperty.FacetSort.Alpha);

		final FacetSearchPageData<SolrSearchQueryData, SearchResultValueData> searchPageData = productSearchService.textSearch("",
				null);
		Assert.assertEquals("", searchPageData.getCurrentQuery().getFreeTextSearch());
		Assert.assertNull(searchPageData.getCurrentQuery().getCategoryCode());
		Assert.assertTrue(searchPageData.getCurrentQuery().getFilterTerms().isEmpty());

		Assert.assertEquals(1, searchPageData.getFacets().size());
		Assert.assertEquals("facet", searchPageData.getFacets().get(0).getCode());
		Assert.assertEquals("facet", searchPageData.getFacets().get(0).getName());
		Assert.assertEquals(1, searchPageData.getFacets().get(0).getValues().size());
		Assert.assertNotNull(searchPageData.getFacets().get(0).getValues().get(0).getQuery());
		Assert.assertEquals("value", searchPageData.getFacets().get(0).getValues().get(0).getName());
		Assert.assertEquals(7, searchPageData.getFacets().get(0).getValues().get(0).getCount());

		// Query Again, this time for the selected facet

		final SolrSearchQueryData facetQuery = searchPageData.getFacets().get(0).getValues().get(0).getQuery();
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(1);
		pageableData.setPageSize(2);
		pageableData.setSort("size");
		final FacetSearchPageData<SolrSearchQueryData, SearchResultValueData> secondSearchPageData = productSearchService
				.searchAgain(facetQuery, pageableData);

		Assert.assertEquals("", secondSearchPageData.getCurrentQuery().getFreeTextSearch());
		Assert.assertNull(secondSearchPageData.getCurrentQuery().getCategoryCode());
		Assert.assertEquals(1, secondSearchPageData.getBreadcrumbs().size());
		Assert.assertEquals("facet", secondSearchPageData.getBreadcrumbs().get(0).getFacetName());
		Assert.assertEquals("value", secondSearchPageData.getBreadcrumbs().get(0).getFacetValueName());
		Assert.assertNotNull(secondSearchPageData.getBreadcrumbs().get(0).getRemoveQuery());
		Assert.assertNull(secondSearchPageData.getBreadcrumbs().get(0).getTruncateQuery());
	}

	public static class SolrSearchResponseConverter
			extends
			AbstractPopulatingConverter<SolrSearchResponse, ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>>
	{
		@Override
		protected ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel> createTarget()
		{
			return new ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>();
		}
	}

	public static class SearchQueryPageableConverter extends
			AbstractPopulatingConverter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest>
	{
		@Override
		protected SolrSearchRequest createTarget()
		{
			return new SolrSearchRequest();
		}
	}

	public static class SolrSearchRequestConverter extends AbstractPopulatingConverter<SolrSearchRequest, SolrSearchResponse>
	{
		@Override
		protected SolrSearchResponse createTarget()
		{
			return new SolrSearchResponse();
		}
	}

}
