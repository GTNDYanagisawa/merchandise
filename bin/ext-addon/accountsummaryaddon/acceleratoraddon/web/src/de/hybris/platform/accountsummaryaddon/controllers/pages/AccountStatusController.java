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
package de.hybris.platform.accountsummaryaddon.controllers.pages;

import de.hybris.platform.accountsummaryaddon.breadcrumb.impl.AccountSummaryMyCompanyBreadcrumbBuilder;
import de.hybris.platform.accountsummaryaddon.constants.AccountsummaryaddonConstants;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.accountsummaryaddon.facade.B2BAccountSummaryFacade;
import de.hybris.platform.b2bacceleratorfacades.company.CompanyB2BCommerceFacade;
import de.hybris.platform.b2bacceleratorfacades.query.QueryParameters;
import de.hybris.platform.b2bacceleratorservices.company.CompanyB2BCommerceService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.yb2bacceleratorstorefront.annotations.RequireHardLogIn;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.AbstractSearchPageController;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for organization management.
 */
@Controller
@Scope("tenant")
public class AccountStatusController extends AbstractSearchPageController
{
	protected static final Logger LOG = Logger.getLogger(AccountStatusController.class);

	@Resource(name = "accountSummaryMyCompanyBreadcrumbBuilder")
	protected AccountSummaryMyCompanyBreadcrumbBuilder myCompanyBreadcrumbBuilder;

	@Resource(name = "b2bCommerceFacade")
	protected CompanyB2BCommerceFacade companyB2BCommerceFacade;

	@Resource(name = "b2bCommerceUnitService")
	protected CompanyB2BCommerceService companyB2BCommerceService;

	@Resource(name = "b2bCustomerFacade")
	protected CustomerFacade customerFacade;

	@Resource(name = "b2bAccountSummaryFacade")
	protected B2BAccountSummaryFacade b2bAccountSummaryFacade;


	@RequestMapping(value = "/my-company/organization-management/accountstatus", method = RequestMethod.GET)
	@RequireHardLogIn
	public String accountStatus(final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(AccountsummaryaddonConstants.ACCOUNT_STATUS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(AccountsummaryaddonConstants.ACCOUNT_STATUS_CMS_PAGE));
		model.addAttribute("breadcrumbs", myCompanyBreadcrumbBuilder.createAccountSummaryBreadcrumbs());
		model.addAttribute("metaRobots", "no-index,no-follow");
        return getViewForPage(model);
	}

	@RequestMapping(value = "/my-company/organization-management/accountstatus/details", method = RequestMethod.GET)
	@RequireHardLogIn
	public String accountStatus(@RequestParam(value = "unit") final String unit, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(AccountsummaryaddonConstants.ACCOUNT_STATUS_DETAIL_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(AccountsummaryaddonConstants.ACCOUNT_STATUS_DETAIL_CMS_PAGE));
		model.addAttribute("breadcrumbs", myCompanyBreadcrumbBuilder.createAccountSummaryDetailsBreadcrumbs(unit));
		model.addAttribute("metaRobots", "no-index,no-follow");

        return getViewForPage(model);
	}

	@RequestMapping(value = "/my-company/organization-management/accountstatus/details/documents", method = RequestMethod.GET, produces = "application/json")
	@RequireHardLogIn
	public String search(final Model model, @RequestParam(value = "unit") final String unit,
			@RequestParam(value = "page", defaultValue = AccountsummaryaddonConstants.DEFAULT_PAGE) final Integer currentPage,
			@RequestParam(value = "pageSize", defaultValue = AccountsummaryaddonConstants.DEFAULT_PAGE_SIZE) final Integer pageSize,
			@RequestParam(value = "searchBy") final String searchBy, @RequestParam(value = "searchValue") final String searchValue,
			@RequestParam(value = "searchRangeMin") final String searchRangeMin,
			@RequestParam(value = "searchRangeMax") final String searchRangeMax,
			@RequestParam(value = "status", defaultValue = AccountsummaryaddonConstants.SEARCH_STATUS_OPEN) final String status,
			@RequestParam(value = "sort", defaultValue = AccountsummaryaddonConstants.DEFAULT_SORT) final String sort)
			throws CMSItemNotFoundException
	{
		final SearchPageData<B2BDocumentData> searchResult = b2bAccountSummaryFacade.findDocuments(QueryParameters
				.with("searchBy", searchBy).and("searchValue", searchValue).and("searchRangeMin", searchRangeMin)
				.and("searchRangeMax", searchRangeMax).and("unit", unit).and("status", status).and("sort", sort)
				.and("pageSize", pageSize.toString()).and("currentPage", currentPage.toString()).buildMap());

		populateModel(model, searchResult, ShowMode.Page);

		model.addAttribute("searchBy", searchBy);
		return AccountsummaryaddonConstants.ACCOUNT_STATUS_DOCUMENTLIST;
	}

}
