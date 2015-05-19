/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.hybris.instore.widgets.solrsearch;

import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredInputs;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.instore.widgets.pageablelist.Pageable;
import com.hybris.instore.widgets.solrsearch.facade.SearchStateResolver;


/**
 * @author krzysztof.kwiatosz
 */
@NullSafeWidget(value = false)
@DeclaredInputs(
{ @DeclaredInput(socketType = String.class, value = "querystring"),
		@DeclaredInput(socketType = List.class, value = "refinements"),
		@DeclaredInput(socketType = CategoryHierarchyData.class, value = "categoryhierarchydata"),
		@DeclaredInput(socketType = ProductSearchPageData.class, value = "smallsearchresult"),
		@DeclaredInput(socketType = String.class, value = "sortoption"),
		@DeclaredInput(socketType = Pageable.class, value = SolrSearchController.SOCKET_IN_RESET) })
public class SolrSearchControllerTest extends AbstractWidgetUnitTest<SolrSearchController>
{
	private static final String CATEGORY_CODE = "testCategory";
	private static final String TEXT_SEARCH = "test";

	@InjectMocks
	private final SolrSearchController controller = new SolrSearchController();

	@Mock
	private ProductSearchFacade productSearchFacade;
	@Mock
	private SearchStateResolver<SearchStateData> searchStateResolver;



	@Before
	public void setUp()
	{
		widgetSettings.put("pageSize", Integer.valueOf(5));
		final SearchStateData categorySearchState = mockSearchStateData();
		final SearchStateData freeTextSearchState = mockSearchStateData();

		Mockito.when(
				searchStateResolver.resolveSearchStateData(Matchers.anyString(), Matchers.eq(CATEGORY_CODE), Matchers.anyList(),
						Matchers.anyString())).thenReturn(categorySearchState);

		Mockito.when(
				searchStateResolver.resolveSearchStateData(Matchers.eq(TEXT_SEARCH), Matchers.anyString(), Matchers.anyList(),
						Matchers.anyString())).thenReturn(freeTextSearchState);

		final ProductCategorySearchPageData categorySearchResultPage = new ProductCategorySearchPageData<SearchStateData, ProductData, CategoryHierarchyData>();
		categorySearchResultPage.setCategoryCode(CATEGORY_CODE);
		categorySearchResultPage.setBreadcrumbs(mockBreadCrumbs());
		categorySearchResultPage.setFacets(mockFacets());
		categorySearchResultPage.setPagination(mockPagination(5));
		categorySearchResultPage.setCurrentQuery(categorySearchState);
		categorySearchResultPage.setResults(mockProducts(5));
		categorySearchResultPage.setSorts(Collections.EMPTY_LIST);

		Mockito.when(
				productSearchFacade.categorySearch(Mockito.eq(CATEGORY_CODE), Mockito.eq(categorySearchState),
						Mockito.any(PageableData.class))).thenReturn(categorySearchResultPage);
		Mockito.when(productSearchFacade.textSearch(Mockito.eq(freeTextSearchState), Mockito.any(PageableData.class))).thenReturn(
				categorySearchResultPage);

	}

	private List<BreadcrumbData<SearchStateData>> mockBreadCrumbs()
	{
		final List<BreadcrumbData<SearchStateData>> breadcrumbs = new ArrayList<BreadcrumbData<SearchStateData>>();
		return breadcrumbs;
	}

	private List<FacetData<SearchStateData>> mockFacets()
	{
		final List<FacetData<SearchStateData>> facets = new ArrayList<FacetData<SearchStateData>>();
		return facets;
	}



	private PaginationData mockPagination(final int pageSize)
	{
		final PaginationData paginationData = new PaginationData();
		paginationData.setPageSize(pageSize);
		paginationData.setCurrentPage(0);
		paginationData.setNumberOfPages(10);
		return paginationData;
	}


	private SearchStateData mockSearchStateData()
	{
		final SearchStateData searchStateData = new SearchStateData();
		return searchStateData;
	}

	private List<ProductData> mockProducts(final int number)
	{
		final List<ProductData> products = new ArrayList<ProductData>();
		for (int i = 0; i < number; i++)
		{
			products.add(mockProduct(i));
		}
		return products;
	}

	private ProductData mockProduct(final int i)
	{
		final ProductData productMock = new ProductData();
		productMock.setCode("Product_" + i);
		return productMock;
	}

	@Override
	protected SolrSearchController getWidgetController()
	{
		return this.controller;
	}

	@Test
	public void testCategorySearchInput()
	{
		final CategoryHierarchyData categoryMock = new CategoryHierarchyData();
		categoryMock.setId(CATEGORY_CODE);

		executeInputSocketEvent(SolrSearchController.SOCKET_IN_CATEGORYHIERARCHYDATA, categoryMock);
		Mockito.verify(widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(SolrSearchController.SOCKET_OUT_PRODUCTSEARCHPAGE), Mockito.any(Pageable.class));
	}

	@Test
	public void testSearchTextInput()
	{
		executeInputSocketEvent(SolrSearchController.SOCKET_IN_SEARCHQUERY, TEXT_SEARCH);
		Mockito.verify(widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(SolrSearchController.SOCKET_OUT_PRODUCTSEARCHPAGE), Mockito.any(Pageable.class));
	}

}
