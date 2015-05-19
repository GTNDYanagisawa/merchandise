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

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.yb2bacceleratorstorefront.annotations.RequireHardLogIn;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.Breadcrumb;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.platform.yb2bacceleratorstorefront.forms.B2BCostCenterForm;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller defines routes to mange Budgets and Cost Centers within My Company section.
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-company/organization-management/manage-costcenters")
public class CostCenterManagementPageController extends MyCompanyPageController
{
	private static final Logger LOG = Logger.getLogger(CostCenterManagementPageController.class);

	@RequestMapping(value = "/unitDetails", method = RequestMethod.GET)
	@RequireHardLogIn
	public String viewCostCenterUnitDetails(@RequestParam("costCenterCode") final String costCenterCode,
			@RequestParam("unit") final String unit, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		final String viewCostCenterUnitUrl = unitDetails(unit, model, request);
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageCostCenterBreadCrumbs();
		myCompanyBreadcrumbBuilder.addViewCostCenterBreadCrumbs(breadcrumbs, costCenterCode);
		breadcrumbs.add(new Breadcrumb(String.format(
				"/organization-management/manage-costcenters/unitDetails/?costCenterCode=%s&unit=%s", urlEncode(costCenterCode),
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.details", new Object[]
		{ unit }, "View {0} Business Unit ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return viewCostCenterUnitUrl;
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	@RequireHardLogIn
	public String manageCostCenters(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = B2BCostCenterModel.CODE) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<B2BCostCenterData> searchPageData = b2bCommerceCostCenterFacade.getPagedCostCenters(pageableData);
		populateModel(model, searchPageData, showMode);
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageCostCenterBreadCrumbs();
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageCostCentersPage;
	}

	@Override
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	public String viewCostCenterDetails(@RequestParam("costCenterCode") final String costCenterCode, final Model model)
			throws CMSItemNotFoundException
	{
		return super.viewCostCenterDetails(costCenterCode, model);
	}

	@RequestMapping(value = "/selectBudget", method = RequestMethod.GET)
	@RequireHardLogIn
	public String assignBudgetsForCostCenter(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = B2BBudgetModel.CODE) final String sortCode,
			@RequestParam("costCenterCode") final String costCenterCode, final Model model) throws CMSItemNotFoundException
	{
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<B2BBudgetData> searchPageData = b2bCommerceCostCenterFacade.getPagedBudgetsForCostCenters(
				pageableData, costCenterCode);

		populateModel(model, searchPageData, showMode);
		model.addAttribute("action", "budgets");
		model.addAttribute("baseUrl", MANAGE_COST_CENTER_BASE_URL);
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageCostCenterBreadCrumbs();
		myCompanyBreadcrumbBuilder.addViewCostCenterBreadCrumbs(breadcrumbs, costCenterCode);
		breadcrumbs.add(new Breadcrumb(String.format(MANAGE_COSTCENTERS_SELECTBUDGET_URL, costCenterCode), getMessageSource()
				.getMessage("text.company.costCenter.select.budget", null, "Edit Related Budgets",
						getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanySelectBudgetPage;
	}

	@ResponseBody
	@RequestMapping(value = "/budgets/select", method =
	{ RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData selectBudgetForCostCenter(@RequestParam("costCenterCode") final String costCenterCode,
			@RequestParam("budgetCode") final String budgetCode) throws CMSItemNotFoundException
	{
		B2BSelectionData b2BSelectionData = null;
		try
		{
			b2BSelectionData = b2bCommerceCostCenterFacade.selectBudgetForCostCenter(costCenterCode, budgetCode);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

		return b2BSelectionData;
	}

	@ResponseBody
	@RequestMapping(value = "/budgets/deselect", method =
	{ RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData deselectBudgetForCostCenter(@RequestParam("costCenterCode") final String costCenterCode,
			@RequestParam("budgetCode") final String budgetCode) throws CMSItemNotFoundException
	{
		try
		{
			b2bCommerceCostCenterFacade.deSelectBudgetForCostCenter(costCenterCode, budgetCode);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		final B2BBudgetData b2BudgetData = companyB2BCommerceFacade.getBudgetDataForCode(budgetCode);
		final B2BSelectionData b2BSelectionData = new B2BSelectionData();
		if (b2BudgetData != null)
		{
			b2BSelectionData.setId(b2BudgetData.getCode());
			b2BSelectionData
					.setNormalizedCode(b2BudgetData.getCode() == null ? null : b2BudgetData.getCode().replaceAll("\\W", "_"));
			b2BSelectionData.setSelected(false);
			b2BSelectionData.setActive(b2BudgetData.isActive());
		}

		return b2BSelectionData;
	}

	@RequestMapping(value = "/enable", method = RequestMethod.GET)
	@RequireHardLogIn
	public String enableCostCenter(@RequestParam("costCenterCode") final String costCenterCode, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			b2bCommerceCostCenterFacade.enableDisableCostCenter(costCenterCode, true);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			GlobalMessages.addErrorMessage(model, "form.global.error");
		}

		final B2BCostCenterData b2bCostCenterData = b2bCommerceCostCenterFacade.getCostCenterDataForCode(costCenterCode);

		if (b2bCostCenterData.getUnit() != null && !b2bCostCenterData.getUnit().isActive())
		{
			GlobalMessages.addErrorMessage(model, "text.costcenter.parentunit.disabled.error");
		}

		if (GlobalMessages.hasErrorMessage(model))
		{
			return viewCostCenterDetails(costCenterCode, model);
		}

		return String.format(REDIRECT_TO_COSTCENTER_DETAILS, urlEncode(costCenterCode));
	}

	@RequestMapping(value = "/disable", method = RequestMethod.GET)
	@RequireHardLogIn
	public String confirmDisableCostCenter(@RequestParam("costCenterCode") final String costCenterCode, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageCostCenterBreadCrumbs();
		myCompanyBreadcrumbBuilder.addViewCostCenterBreadCrumbs(breadcrumbs, costCenterCode);
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("costCenterCode", costCenterCode);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyCostCenterDisableConfirm;
	}

	@RequestMapping(value = "/disable", method = RequestMethod.POST)
	@RequireHardLogIn
	public String disableCostCenter(@RequestParam("costCenterCode") final String costCenterCode, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			b2bCommerceCostCenterFacade.enableDisableCostCenter(costCenterCode, false);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return viewCostCenterDetails(costCenterCode, model);
		}

		return String.format(REDIRECT_TO_COSTCENTER_DETAILS, urlEncode(costCenterCode));
	}

	@Override
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editCostCenterDetails(@RequestParam("costCenterCode") final String costCenterCode, final Model model)
			throws CMSItemNotFoundException
	{
		return super.editCostCenterDetails(costCenterCode, model);
	}

	@Override
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@RequireHardLogIn
	public String updateCostCenterDetails(@Valid final B2BCostCenterForm b2BCostCenterForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		return super.updateCostCenterDetails(b2BCostCenterForm, bindingResult, model, redirectModel);
	}

	@Override
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	public String addCostCenter(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(
				"title",
				getMessageSource().getMessage("text.company.costCenter.create.label", null, "Create Cost Center",
						getI18nService().getCurrentLocale()));
		return super.addCostCenter(model);
	}

	@Override
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequireHardLogIn
	public String saveCostCenter(@Valid final B2BCostCenterForm b2BCostCenterForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		return super.saveCostCenter(b2BCostCenterForm, bindingResult, model, redirectModel);
	}
}
