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
package de.hybris.platform.b2b.occ.v2.controllers;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductCategorySearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.mapping.DataMapper;
import de.hybris.platform.commercewebservicescommons.mapping.FieldSetBuilder;
import de.hybris.platform.yb2bacceleratorfacades.search.B2BProductSearchFacade;
import de.hybris.platform.ycommercewebservices.v2.controller.BaseController;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
@RequestMapping(value = "/{baseSiteId}/categories")
@ApiVersion("v2")
public class B2BCategoriesController extends BaseController
{

	private static final String DEFAULT_PAGE_VALUE = "0";

	@Resource(name = "b2bSolrProductSearchFacade")
	private B2BProductSearchFacade<ProductData> solrProductSearchFacade;
	@Resource
	private FieldSetBuilder fieldSetBuilder;
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;



	/**
	 * Gets a list of products under a specific category.
	 *
	 * @param query
	 *           serialized query in format: freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2
	 * @param currentPage
	 *           the current result page requested
	 * @param pageSize
	 *           the number of results returned per page
	 * @param sort
	 *           sorting method applied to the display search results
	 * @return a representation of
	 *         {@link de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO}
	 */
	@RequestMapping(value = "/{categoryId}/products", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ProductSearchPageWsDTO searchProducts(@PathVariable final String categoryId,
			@RequestParam(required = false) final String query,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = "20") final int pageSize,
			@RequestParam(required = false) final String sort, @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{

		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(query);

		final SearchStateData searchState = new SearchStateData();
		searchState.setQuery(searchQueryData);

		final PageableData pageable = new PageableData();
		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		pageable.setSort(sort);

		final ProductSearchPageData<SearchStateData, ProductData> sourceResult = solrProductSearchFacade.categorySearch(categoryId,
				searchState, pageable);

		if (sourceResult instanceof ProductCategorySearchPageData)
		{
			return dataMapper.map(sourceResult, ProductCategorySearchPageWsDTO.class, fields);
		}

		return dataMapper.map(sourceResult, ProductSearchPageWsDTO.class, fields);
	}

}
