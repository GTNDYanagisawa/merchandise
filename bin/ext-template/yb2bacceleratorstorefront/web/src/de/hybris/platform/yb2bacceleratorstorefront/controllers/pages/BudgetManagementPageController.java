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
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.yb2bacceleratorstorefront.annotations.RequireHardLogIn;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.Breadcrumb;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.platform.yb2bacceleratorstorefront.forms.B2BBudgetForm;

import java.text.ParseException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller defines routes to manage Budgets within My Company section.
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-company/organization-management/manage-budgets")
public class BudgetManagementPageController extends MyCompanyPageController
{
	private static final Logger LOG = Logger.getLogger(BudgetManagementPageController.class);

	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	public String manageBudgets(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = B2BBudgetModel.CODE) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<B2BBudgetData> searchPageData = companyB2BCommerceFacade.getPagedBudgets(pageableData);
		populateModel(model, searchPageData, showMode);
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageBudgetsBreadCrumbs();
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageBudgetsPage;
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	public String viewBudgetsDetails(@RequestParam("budgetCode") final String budgetCode, final Model model)
			throws CMSItemNotFoundException
	{
		final B2BBudgetData budgetData = companyB2BCommerceFacade.getBudgetDataForCode(budgetCode);
		if (budgetData == null)
		{
			GlobalMessages.addErrorMessage(model, "error.bugetcode.notfound");
		}
		model.addAttribute("budget", budgetData);
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageBudgetsBreadCrumbs();
		model.addAttribute("breadcrumbs", breadcrumbs);
		myCompanyBreadcrumbBuilder.addViewBudgetBreadCrumbs(breadcrumbs, budgetCode);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageBudgetsViewPage;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editBudgetsDetails(@RequestParam("budgetCode") final String budgetCode, final Model model)
			throws CMSItemNotFoundException
	{
		if (!model.containsAttribute("b2BBudgetForm"))
		{
			final B2BBudgetData b2BBudgetData = companyB2BCommerceFacade.getBudgetDataForCode(budgetCode);
			final B2BBudgetForm b2BBudgetform = new B2BBudgetForm();
			b2BBudgetform.setOriginalCode(budgetCode);
			b2BBudgetform.setCode(b2BBudgetData.getCode());
			b2BBudgetform.setCurrency(b2BBudgetData.getCurrency().getIsocode());
			b2BBudgetform.setName(b2BBudgetData.getName());
			b2BBudgetform.setParentB2BUnit(b2BBudgetData.getUnit().getUid());
			b2BBudgetform.setStartDate(b2BBudgetData.getStartDate());
			b2BBudgetform.setEndDate(b2BBudgetData.getEndDate());
			b2BBudgetform.setBudget(formatFactory.createNumberFormat().format(b2BBudgetData.getBudget().doubleValue()));
			model.addAttribute(b2BBudgetform);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageBudgetsBreadCrumbs();
		myCompanyBreadcrumbBuilder.addViewBudgetBreadCrumbs(breadcrumbs, budgetCode);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-budgets/edit?budgetCode=%s",
				urlEncode(budgetCode)), getMessageSource().getMessage("text.company.budget.editPage", new Object[]
		{ budgetCode }, "Edit Budget: {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageBudgetsEditPage;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@RequireHardLogIn
	public String updateBudgetsDetails(@RequestParam("budgetCode") final String budgetCode,
			@Valid final B2BBudgetForm b2BBudgetForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException, ParseException
	{
		b2BBudgetFormValidator.validate(b2BBudgetForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			model.addAttribute(b2BBudgetForm);
			return editBudgetsDetails(b2BBudgetForm.getOriginalCode(), model);
		}
		if (checkEndDateIsBeforeStartDateForBudget(b2BBudgetForm))
		{
			model.addAttribute(b2BBudgetForm);
			bindingResult.rejectValue("endDate", "text.company.budget.endDateLesser.error.title");
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return editBudgetsDetails(b2BBudgetForm.getOriginalCode(), model);
		}
		final B2BBudgetData b2BBudgetData = populateB2BBudgetDataFromForm(b2BBudgetForm);
		try
		{
			b2bCommerceBudgetFacade.updateBudgetDetails(b2BBudgetData);

		}
		catch (final Exception e)
		{
			LOG.warn("Exception while saving the budget details " + e);
			model.addAttribute(b2BBudgetForm);
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return editBudgetsDetails(b2BBudgetForm.getOriginalCode(), model);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageBudgetsBreadCrumbs();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-budgets/update", getMessageSource().getMessage(
				"text.company.budget.editPage", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.budget.updated");
		return String.format(REDIRECT_TO_BUDGET_DETAILS, urlEncode(b2BBudgetData.getCode()));
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getAddBudgetPage(final Model model) throws CMSItemNotFoundException
	{
		if (!model.containsAttribute("b2BBudgetForm"))
		{
			final B2BBudgetForm b2BBudgetForm = new B2BBudgetForm();
			final B2BUnitData b2BUnitData = companyB2BCommerceFacade.getParentUnit();
			b2BBudgetForm.setParentB2BUnit(b2BUnitData.getUid());
			model.addAttribute(b2BBudgetForm);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageBudgetsBreadCrumbs();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-budgets/update", getMessageSource().getMessage(
				"text.company.budget.addPage", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageBudgetsAddPage;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addNewBudget(@Valid final B2BBudgetForm b2BBudgetForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException, ParseException
	{
		b2BBudgetFormValidator.validate(b2BBudgetForm, bindingResult);
		if (bindingResult.hasFieldErrors())
		{
			model.addAttribute(b2BBudgetForm);
			return getAddBudgetPage(model);
		}
		if (checkEndDateIsBeforeStartDateForBudget(b2BBudgetForm))
		{
			model.addAttribute(b2BBudgetForm);
			bindingResult.rejectValue("endDate", "text.company.budget.endDateLesser.error.title");
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return getAddBudgetPage(model);
		}
		final B2BBudgetData b2BBudgetData = populateB2BBudgetDataFromForm(b2BBudgetForm);
		try
		{
			b2bCommerceBudgetFacade.addBudget(b2BBudgetData);
		}
		catch (final Exception e)
		{
			LOG.warn("Exception while saving the budget details " + e);
			model.addAttribute(b2BBudgetForm);
			bindingResult.rejectValue("code", "text.company.budget.code.exists.error.title");
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return getAddBudgetPage(model);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageBudgetsBreadCrumbs();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-budgets/update", getMessageSource().getMessage(
				"text.company.budget.editPage", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.budget.created");
		return String.format(REDIRECT_TO_BUDGET_DETAILS, urlEncode(b2BBudgetData.getCode()));
	}

	@RequestMapping(value = "/enable", method = RequestMethod.GET)
	@RequireHardLogIn
	public String enableBudget(@RequestParam("budgetCode") final String budgetCode, final Model model)
			throws CMSItemNotFoundException
	{

		final B2BBudgetData b2bBudgetData = companyB2BCommerceFacade.getBudgetDataForCode(budgetCode);

		if (b2bBudgetData.getUnit() != null && !b2bBudgetData.getUnit().isActive())
		{
			GlobalMessages.addErrorMessage(model, "error.budget.parentunit.disabled");
		}
		else
		{
			try
			{
				b2bCommerceBudgetFacade.enableDisableBudget(budgetCode, true);
			}
			catch (final Exception e)
			{
				LOG.error(e.getMessage(), e);
				GlobalMessages.addErrorMessage(model, "form.global.error");
			}

			if (GlobalMessages.hasErrorMessage(model))
			{
				return viewBudgetsDetails(budgetCode, model);
			}

		}

		return String.format(REDIRECT_TO_BUDGET_DETAILS, urlEncode(budgetCode));
	}

	@RequestMapping(value = "/disable", method = RequestMethod.GET)
	@RequireHardLogIn
	public String confirmDisableBudgets(@RequestParam("budgetCode") final String budgetCode, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageBudgetsBreadCrumbs();
		myCompanyBreadcrumbBuilder.addViewBudgetBreadCrumbs(breadcrumbs, budgetCode);
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("budgetCode", budgetCode);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyBudgetDisableConfirm;
	}

	@RequestMapping(value = "/disable", method = RequestMethod.POST)
	@RequireHardLogIn
	public String disableBudget(@RequestParam("budgetCode") final String budgetCode, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			b2bCommerceBudgetFacade.enableDisableBudget(budgetCode, false);
		}
		catch (final Exception e)
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return viewBudgetsDetails(budgetCode, model);
		}
		return String.format(REDIRECT_TO_BUDGET_DETAILS, urlEncode(budgetCode));
	}

	@RequestMapping(value = "/unitDetails", method = RequestMethod.GET)
	@RequireHardLogIn
	public String viewBudgetUnitDetails(@RequestParam("budgetCode") final String budgetCode,
			@RequestParam("unit") final String unit, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		final String viewBudgetUniUrl = unitDetails(unit, model, request);

		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageBudgetsBreadCrumbs();
		myCompanyBreadcrumbBuilder.addViewBudgetBreadCrumbs(breadcrumbs, budgetCode);
		breadcrumbs.add(new Breadcrumb(String.format(
				"/my-company/organization-management/manage-budgets/unitDetails?budgetCode=%s&unit=%s", urlEncode(budgetCode),
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.details", new Object[]
		{ unit }, "View {0} Business Unit ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return viewBudgetUniUrl;
	}
}
