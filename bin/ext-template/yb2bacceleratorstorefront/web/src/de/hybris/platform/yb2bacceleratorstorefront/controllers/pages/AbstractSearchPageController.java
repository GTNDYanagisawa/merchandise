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
package de.hybris.platform.yb2bacceleratorstorefront.controllers.pages;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

import org.springframework.ui.Model;


/**
 * Controller for search pages.
 */
public abstract class AbstractSearchPageController extends AbstractPageController
{
	public static final int MAX_PAGE_LIMIT = 100;//should be configured
	protected static final String PAGINATION_TYPE = "pagination.type";
	protected static final String PAGINATION_NUMBER_OF_RESULTS_COUNT = "pagination.number.results.count";
	protected static final String PAGINATION = "pagination";
	protected static final String IS_SHOW_ALLOWED = "isShowAllAllowed";

	public static enum ShowMode
	{
		Page, All
	}

	protected PageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode,
			final ShowMode showMode)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(pageNumber);
		pageableData.setSort(sortCode);

		if (ShowMode.All == showMode)
		{
			pageableData.setPageSize(MAX_PAGE_LIMIT);
		}
		else
		{
			pageableData.setPageSize(pageSize);
		}
		return pageableData;
	}

	protected boolean isShowAllAllowed(final SearchPageData<?> searchPageData)
	{
		return searchPageData.getPagination().getNumberOfPages() > 1
				&& searchPageData.getPagination().getTotalNumberOfResults() < MAX_PAGE_LIMIT;
	}

	protected void populateModel(final Model model, final SearchPageData<?> searchPageData, final ShowMode showMode)
	{
		final int numberPagesShown = getSiteConfigService().getInt(PAGINATION_NUMBER_OF_RESULTS_COUNT, 5);
		final String paginationType = getSiteConfigService().getString(PAGINATION_TYPE, "pagination");

		model.addAttribute("numberPagesShown", Integer.valueOf(numberPagesShown));
		model.addAttribute("paginationType", paginationType);
		model.addAttribute("searchPageData", searchPageData);
		model.addAttribute("isShowAllAllowed", calculateShowAll(searchPageData, showMode));
		model.addAttribute("isShowPageAllowed", calculateShowPaged(searchPageData, showMode));
	}



	protected Boolean calculateShowAll(final SearchPageData<?> searchPageData, final ShowMode showMode)
	{
		return Boolean
				.valueOf((showMode != ShowMode.All && searchPageData.getPagination().getTotalNumberOfResults() > searchPageData
						.getPagination().getPageSize()) && isShowAllAllowed(searchPageData));
	}

	protected Boolean calculateShowPaged(final SearchPageData<?> searchPageData, final ShowMode showMode)
	{
		return Boolean
				.valueOf(showMode == ShowMode.All
						&& (searchPageData.getPagination().getNumberOfPages() > 1 || searchPageData.getPagination().getPageSize() == getMaxSearchPageSize()));
	}

	/**
	 * Get the default search page size.
	 * 
	 * @return the number of results per page, <tt>0</tt> (zero) indicated 'default' size should be used
	 */
	protected int getSearchPageSize()
	{
		return getSiteConfigService().getInt("storefront.search.pageSize", 0);
	}

	protected int getMaxSearchPageSize()
	{
		return MAX_PAGE_LIMIT;
	}

	public static class SearchResultsData<RESULT>
	{
		private List<RESULT> results;
		private PaginationData pagination;

		public List<RESULT> getResults()
		{
			return results;
		}

		public void setResults(final List<RESULT> results)
		{
			this.results = results;
		}

		public PaginationData getPagination()
		{
			return pagination;
		}

		public void setPagination(final PaginationData pagination)
		{
			this.pagination = pagination;
		}
	}
}
