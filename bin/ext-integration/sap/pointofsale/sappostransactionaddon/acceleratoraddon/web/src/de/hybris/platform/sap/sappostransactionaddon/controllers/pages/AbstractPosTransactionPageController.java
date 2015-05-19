/**
 * 
 */
package de.hybris.platform.sap.sappostransactionaddon.controllers.pages;

import de.hybris.platform.addonsupport.controllers.page.AbstractAddOnPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import org.springframework.ui.Model;
import org.springframework.util.StringUtils;


/**
 * 
 */
public abstract class AbstractPosTransactionPageController extends AbstractAddOnPageController
{

	public static enum ShowMode
	{
		Page, All
	}

	public static final int MAX_PAGE_LIMIT = 100;
	private static final String PAGINATION_NUMBER_OF_RESULTS_COUNT = "pagination.number.results.count";


	@Override
	protected ContentPageModel getContentPageForLabelOrId(final String labelOrId) throws CMSItemNotFoundException
	{

		String key = labelOrId;
		if (StringUtils.isEmpty(labelOrId))
		{ // Fallback to site home page 
			final ContentPageModel homePage = this.getCmsPageService().getHomepage();
			if (homePage != null)
			{
				key = this.getCmsPageService().getLabelOrId(homePage);
			}
			else
			{ // Fallback to site start page label 
				final CMSSiteModel site = this.getCmsSiteService().getCurrentSite();
				if (site != null)
				{
					key = this.getCmsSiteService().getStartPageLabelOrId(site);
				}
			}
		}

		// Actually resolve the label or id - running cms restrictions 
		return this.getCmsPageService().getPageForLabelOrId(key);
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

	protected void populateModel(final Model model, final SearchPageData<?> searchPageData, final ShowMode showMode)
	{
		final int numberPagesShown = getSiteConfigService().getInt(PAGINATION_NUMBER_OF_RESULTS_COUNT, 5);

		model.addAttribute("numberPagesShown", Integer.valueOf(numberPagesShown));
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
						&& (searchPageData.getPagination().getNumberOfPages() > 1 || searchPageData.getPagination().getPageSize() == MAX_PAGE_LIMIT));
	}

	protected boolean isShowAllAllowed(final SearchPageData<?> searchPageData)
	{
		return searchPageData.getPagination().getNumberOfPages() > 1
				&& searchPageData.getPagination().getTotalNumberOfResults() < MAX_PAGE_LIMIT;
	}

}
