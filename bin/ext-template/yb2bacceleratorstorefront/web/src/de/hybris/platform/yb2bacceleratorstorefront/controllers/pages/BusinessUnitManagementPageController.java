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

import de.hybris.platform.b2bacceleratorfacades.company.data.B2BUnitNodeData;
import de.hybris.platform.b2bacceleratorfacades.company.data.UserData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BSelectionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.yb2bacceleratorstorefront.annotations.RequireHardLogIn;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.Breadcrumb;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.platform.yb2bacceleratorstorefront.forms.AddressForm;
import de.hybris.platform.yb2bacceleratorstorefront.forms.B2BCostCenterForm;
import de.hybris.platform.yb2bacceleratorstorefront.forms.B2BCustomerForm;
import de.hybris.platform.yb2bacceleratorstorefront.forms.B2BUnitForm;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
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
 * Controller defines routes to manage Business Units within My Company section.
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-company/organization-management/manage-units")
public class BusinessUnitManagementPageController extends MyCompanyPageController
{
	private static final Logger LOG = Logger.getLogger(BusinessUnitManagementPageController.class);

	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	public String manageUnits(final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsBreadcrumbs();
		model.addAttribute("breadcrumbs", breadcrumbs);

		final B2BUnitNodeData rootNode = b2bCommerceUnitFacade.getParentUnitNode();
		model.addAttribute("rootNode", rootNode);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitsPage;
	}

	@Override
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	@RequireHardLogIn
	public String unitDetails(@RequestParam("unit") final String unit, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		return super.unitDetails(unit, model, request);
	}

	@RequestMapping(value = "/addcostcenter", method = RequestMethod.GET)
	@RequireHardLogIn
	public String addCostCenterToUnit(@RequestParam("unit") final String unit, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		if (unitData == null)
		{
			GlobalMessages.addErrorMessage(model, "b2bunit.notfound");
		}
		else if (!model.containsAttribute("b2BCostCenterForm"))
		{
			final B2BCostCenterForm b2BCostCenterForm = new B2BCostCenterForm();
			b2BCostCenterForm.setParentB2BUnit(unitData.getUid());
			model.addAttribute(b2BCostCenterForm);
		}

		final String addCostCenterUrl = addCostCenter(model);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/addcostcenter?unit=%s",
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.addCostCenter.breadcrumb", new Object[]
		{ unit }, "Create Cost Center for Unit: {0} ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("title", getMessageSource().getMessage("text.company.manage.units.create.costCenter.title", new Object[]
		{ unitData.getName() }, "Create Cost Center for Unit: {0}", getI18nService().getCurrentLocale()));
		model.addAttribute("cancelUrl", String.format("%s/my-company/organization-management/manage-units/details?unit=%s",
				request.getContextPath(), urlEncode(unit)));
		model.addAttribute("saveUrl", String.format("%s/my-company/organization-management/manage-units/addcostcenter?unit=%s",
				request.getContextPath(), urlEncode(unit)));
		return addCostCenterUrl;
	}

	@RequestMapping(value = "/addcostcenter", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addCostCenterToUnit(@RequestParam("unit") final String unit, @Valid final B2BCostCenterForm b2BCostCenterForm,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		if (unitData == null)
		{
			GlobalMessages.addErrorMessage(model, "b2bunit.notfound");
		}

		final String url = saveCostCenter(b2BCostCenterForm, bindingResult, model, redirectModel);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String
				.format("/my-company/organization-management/manage-units/addcostcenter?unit=%s", unit), getMessageSource()
				.getMessage("text.company.manage.units.addCostCenter.breadcrumb", new Object[]
				{ unit }, "Add Cost Center to {0} Business Unit ", getI18nService().getCurrentLocale()), null));

		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("title", getMessageSource().getMessage("text.company.manage.units.create.costCenter.title", new Object[]
		{ unitData.getName() }, "Create Cost Center for Unit: {0}", getI18nService().getCurrentLocale()));
		model.addAttribute("cancelUrl", String.format("%s/my-company/organization-management/manage-units/details?unit=%s",
				request.getContextPath(), urlEncode(unit)));
		model.addAttribute("saveUrl", String.format("%s/my-company/organization-management/manage-units/addcostcenter?unit=%s",
				request.getContextPath(), urlEncode(unit)));
		if (bindingResult.hasErrors())
		{
			return url;
		}
		else
		{
			return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
		}
	}

	@RequestMapping(value = "/editcostcenter", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editCostCenterToUnit(@RequestParam("unit") final String unit,
			@RequestParam("costCenterCode") final String costCenterCode, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		if (unitData == null)
		{
			GlobalMessages.addErrorMessage(model, "b2bunit.notfound");
		}

		final String url = editCostCenterDetails(costCenterCode, model);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format(
				"/my-company/organization-management/manage-units/details/editcostcenter/?unit=%s&costCenterCode=%s",
				urlEncode(unit), urlEncode(costCenterCode)), getMessageSource().getMessage(
				"text.company.manage.units.editCostCenter.breadcrumb", new Object[]
				{ unit }, "Edit Cost Center for Unit: {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("cancelUrl", String.format("%s/my-company/organization-management/manage-units/details?unit=%s",
				request.getContextPath(), urlEncode(unit)));
		model.addAttribute(
				"saveUrl",
				String.format("%s/my-company/organization-management/manage-units/editcostcenter/?unit=%s&costCenterCode=%s",
						request.getContextPath(), urlEncode(unit), urlEncode(costCenterCode)));
		return url;
	}

	@RequestMapping(value = "/editcostcenter", method = RequestMethod.POST)
	@RequireHardLogIn
	public String editCostCenterToUnit(@RequestParam("unit") final String unit,
			@RequestParam("costCenterCode") final String costCenterCode, @Valid final B2BCostCenterForm b2BCostCenterForm,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		if (unitData == null)
		{
			GlobalMessages.addErrorMessage(model, "b2bunit.notfound");
		}

		final String url = updateCostCenterDetails(b2BCostCenterForm, bindingResult, model, redirectModel);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/?unit=%s&costCenterCode=%s",
				urlEncode(unit), urlEncode(costCenterCode)), getMessageSource().getMessage(
				"text.company.manage.units.editCostCenter.breadcrumb", new Object[]
				{ unit }, "Edit Cost Center for Unit: {0}", getI18nService().getCurrentLocale()), null));

		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("cancelUrl", String.format("%s/my-company/organization-management/manage-units/details?unit=%s",
				request.getContextPath(), urlEncode(unit)));
		model.addAttribute("saveUrl", String.format(
				"%s/my-company/organization-management/manage-units/details/editcostcenter/?unit=%s&costCenterCode=%s",
				request.getContextPath(), urlEncode(unit), urlEncode(costCenterCode)));
		if (bindingResult.hasErrors())
		{
			return url;
		}
		else
		{
			return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
		}
	}

	@RequestMapping(value = "/disable", method = RequestMethod.GET)
	@RequireHardLogIn
	public String disableUnitConfirmation(@RequestParam("unit") final String unit, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/disable?unit=%s",
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.disable.breadcrumb", new Object[]
		{ unit }, "Disable {0} Business Unit ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		model.addAttribute("unit", unitData);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitDisablePage;
	}

	@RequestMapping(value = "/disable", method = RequestMethod.POST)
	@RequireHardLogIn
	public String disableUnit(@RequestParam("unit") final String unit, final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/disable?unit=%s",
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.disable.breadcrumb", new Object[]
		{ unit }, "Disable {0} Business Unit ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		b2bCommerceUnitFacade.disableUnit(unit);

		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
	}

	@RequestMapping(value = "/enable", method = RequestMethod.GET)
	@RequireHardLogIn
	public String enableUnit(@RequestParam("unit") final String unit, final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/enable?unit=%s",
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.enable.breadcrumb", new Object[]
		{ unit }, "Enable {0} Business Unit ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		b2bCommerceUnitFacade.enableUnit(unit);
		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editUnit(@RequestParam("unit") final String unit, final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/edit?unit=%s",
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.editUnit.breadcrumb", new Object[]
		{ unit }, "Edit Unit: {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		if (!model.containsAttribute("b2BUnitForm"))
		{
			final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);

			final B2BUnitForm b2BUnitForm = new B2BUnitForm();
			b2BUnitForm.setOriginalUid(unitData.getUid());
			if (unitData.getUnit() != null)
			{
				b2BUnitForm.setParentUnit(unitData.getUnit().getUid());
			}
			b2BUnitForm.setApprovalProcessCode(unitData.getApprovalProcessCode());
			b2BUnitForm.setUid(unitData.getUid());
			b2BUnitForm.setName(unitData.getName());
			model.addAttribute(b2BUnitForm);
		}
		model.addAttribute("branchSelectOptions", getBranchSelectOptions(b2bCommerceUnitFacade.getAllowedParentUnits(unit)));
		model.addAttribute("action", "edit");
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitEditPage;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@RequireHardLogIn
	public String editUnit(@RequestParam("unit") final String unit, @Valid final B2BUnitForm unitForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/edit?unit=%s",
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.editUnit.breadcrumb", new Object[]
		{ unit }, "Edit {0} Business Unit ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			model.addAttribute(unitForm);
			return editUnit(unit, model);

		}
		if (!unitForm.getUid().equals(unit) && companyB2BCommerceFacade.getUnitForUid(unitForm.getUid()) != null)
		{
			// a unit uid is not unique
			GlobalMessages.addErrorMessage(model, "form.global.error");
			bindingResult.rejectValue("uid", "form.b2bunit.notunique");
			model.addAttribute(unitForm);
			return editUnit(unit, model);
		}

		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);

		if (unitData.getUnit() == null && StringUtils.isBlank(unitForm.getApprovalProcessCode()))
		{
			// approval process is required for root unit
			GlobalMessages.addErrorMessage(model, "form.global.error");
			bindingResult.rejectValue("approvalProcessCode", "form.b2bunit.approvalProcessCode.required");
			model.addAttribute(unitForm);
			return editUnit(unit, model);
		}

		unitData.setUid(unitForm.getUid());
		unitData.setName(unitForm.getName());
		unitData.setApprovalProcessCode(unitForm.getApprovalProcessCode());
		if (StringUtils.isNotBlank(unitForm.getParentUnit()))
		{
			unitData.setUnit(companyB2BCommerceFacade.getUnitForUid(unitForm.getParentUnit()));
		}

		try
		{
			b2bCommerceUnitFacade.updateOrCreateBusinessUnit(unit, unitData);
		}
		catch (final DuplicateUidException e)
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			bindingResult.rejectValue("uid", "form.b2bunit.notunique");
			return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitEditPage;
		}
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "form.b2bunit.success");

		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unitForm.getUid()));
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	@RequireHardLogIn
	public String createUnit(@RequestParam("unit") final String unit, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs;
		final B2BUnitData unitData;
		String parentUnit = "";
		if (StringUtils.isNotBlank(unit))
		{
			unitData = companyB2BCommerceFacade.getUnitForUid(unit);
			breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
			model.addAttribute("cancelUrl", String.format("%s/my-company/organization-management/manage-units/details/?unit=%s",
					request.getContextPath(), urlEncode(unit)));
		}
		else
		{
			breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsBreadcrumbs();
			unitData = companyB2BCommerceFacade.getParentUnit();
			parentUnit = unitData == null ? "" : unitData.getName();
		}
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-units/create?unit=", getMessageSource()
				.getMessage("text.company.manage.units.createUnit.breadcrumb", new Object[]
				{ StringUtils.isEmpty(parentUnit) ? unit : parentUnit }, "Create Child Unit for Unit: {0}",
						getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("unitName", (unitData == null ? "" : unitData.getName()));

		if (!model.containsAttribute("b2BUnitForm"))
		{
			final B2BUnitForm b2BUnitForm = new B2BUnitForm();
			b2BUnitForm.setParentUnit((unitData == null ? "" : unitData.getUid()));
			model.addAttribute(b2BUnitForm);
		}
		model.addAttribute("branchSelectOptions", getBranchSelectOptions(this.companyB2BCommerceFacade.getBranchNodes()));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitCreatePage;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@RequireHardLogIn
	public String createUnit(@Valid final B2BUnitForm unitForm, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unitForm.getUid());
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-units/create?unit=", getMessageSource()
				.getMessage("text.company.manage.units.createUnit.breadcrumb", new Object[]
				{ unitForm.getUid() }, "Create Child Unit for Unit: {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			model.addAttribute(unitForm);
			return createUnit(unitForm.getParentUnit(), model, request);
		}
		if (companyB2BCommerceFacade.getUnitForUid(unitForm.getUid()) != null)
		{
			// a unit uid is not unique
			GlobalMessages.addErrorMessage(model, "form.global.error");
			bindingResult.rejectValue("uid", "form.b2bunit.notunique");
			model.addAttribute(unitForm);
			return createUnit(unitForm.getUid(), model, request);
		}

		final B2BUnitData unitData = new B2BUnitData();
		unitData.setUid(unitForm.getUid());
		unitData.setName(unitForm.getName());
		unitData.setApprovalProcessCode(unitForm.getApprovalProcessCode());
		if (StringUtils.isNotBlank(unitForm.getParentUnit()))
		{
			unitData.setUnit(companyB2BCommerceFacade.getUnitForUid(unitForm.getParentUnit()));
		}

		try
		{
			b2bCommerceUnitFacade.updateOrCreateBusinessUnit(unitData.getUid(), unitData);
		}
		catch (final DuplicateUidException e)
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			bindingResult.rejectValue("uid", "form.b2bunit.notunique");
			return createUnit(unitForm.getUid(), model, request);
		}

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "form.b2bunit.success");
		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unitForm.getUid()));
	}

	@RequestMapping(value = "/costcenter", method = RequestMethod.GET)
	@RequireHardLogIn
	public String viewCostCenterForUnit(@RequestParam("unit") final String unit,
			@RequestParam("costCenterCode") final String costCenterCode, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		final String url = viewCostCenterDetails(costCenterCode, model);
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format(
				"/my-company/organization-management/manage-units/costcenter/?unit=%s&costCenterCode=%s", urlEncode(unit),
				urlEncode(costCenterCode)), getMessageSource().getMessage("text.company.manage.units.viewcostcenter.breadcrumb",
				new Object[]
				{ costCenterCode }, "View Cost Center {0} ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("editUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/editcostcenter/?unit=%s&costCenterCode=%s", urlEncode(unit),
				urlEncode(costCenterCode)));
		return url;
	}

	@RequestMapping(value = "/createuser", method = RequestMethod.GET)
	@RequireHardLogIn
	public String createCustomerOfUnit(@RequestParam("unit") final String unit, @RequestParam("role") final String role,
			final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		final String url = createUser(model);
		final B2BCustomerForm b2bCustomerForm = (B2BCustomerForm) model.asMap().get("b2BCustomerForm");
		b2bCustomerForm.setParentB2BUnit(unit);
		b2bCustomerForm.setRoles(Collections.singleton(role));

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/createuser/?unit=%s&role=%s",
				urlEncode(unit), urlEncode(role)), getMessageSource().getMessage("text.company.organizationManagement", null,
				getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("action", "manage.units");
		model.addAttribute("saveUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/createuser?unit=%s&role=%s", urlEncode(unit), urlEncode(role)));
		model.addAttribute("cancelUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/details?unit=%s", urlEncode(unit)));
		return url;
	}

	@RequestMapping(value = "/createuser", method = RequestMethod.POST)
	@RequireHardLogIn
	public String createCustomerOfUnit(@RequestParam("unit") final String unit, @RequestParam("role") final String role,
			@Valid final B2BCustomerForm b2bCustomerForm, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		model.addAttribute("saveUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/createuser?unit=%s&role=%s", urlEncode(unit), urlEncode(role)));
		model.addAttribute("cancelUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/details?unit=%s&role=%s", urlEncode(unit), urlEncode(role)));
		b2bCustomerForm.setParentB2BUnit(unit);
		b2bCustomerForm.setRoles(Collections.singleton(role));
		final String url = createUser(b2bCustomerForm, bindingResult, model, redirectModel);
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/createuser?unit=%s&role=%s",
				urlEncode(unit), urlEncode(role)), getMessageSource().getMessage("text.company.manage.units.createuser.breadcrumb", new Object[]
		{ b2bCustomerForm.getUid() }, "Create Customer {0} ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("action", "manage.units");
		if (bindingResult.hasErrors())
		{
			return url;
		}
		else
		{
			selectCustomer(b2bCustomerForm.getEmail(), role, model);
			return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
		}
	}

	@RequestMapping(value = "/edituser", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editCustomerOfUnit(@RequestParam("unit") final String unit, @RequestParam("user") final String user,
			final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		final String url = editUser(user, model);
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/edituser/?unit=%s&user=%s",
				urlEncode(unit), urlEncode(user)), getMessageSource().getMessage("text.company.manage.units.editcustomer.breadcrumb",
				new Object[]
				{ user }, "Edit Customer {0} ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("action", "manage.units");
		model.addAttribute("saveUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/edituser/?unit=%s&user=%s", urlEncode(unit), urlEncode(user)));
		model.addAttribute("cancelUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/details?unit=%s", urlEncode(unit)));
		return url;
	}

	@RequestMapping(value = "/edituser", method = RequestMethod.POST)
	@RequireHardLogIn
	public String editCustomerOfUnit(@RequestParam("unit") final String unit, @RequestParam("user") final String user,
			@Valid final B2BCustomerForm b2bCustomerForm, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		final String url = editUser(user, b2bCustomerForm, bindingResult, model, redirectModel);
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/edituser/?unit=%s&user=%s",
				urlEncode(unit), urlEncode(user)), getMessageSource().getMessage("text.company.manage.units.editcustomer.breadcrumb",
				new Object[]
				{ user }, "Edit Customer {0} ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		if (bindingResult.hasErrors() || GlobalMessages.hasErrorMessage(model))
		{
			model.addAttribute("action", "manage.units");
			model.addAttribute("saveUrl", String.format(request.getContextPath()
					+ "/my-company/organization-management/manage-units/edituser/?unit=%s&user=%s", urlEncode(unit), urlEncode(user)));
			model.addAttribute("cancelUrl", String.format(request.getContextPath()
					+ "/my-company/organization-management/manage-units/details?unit=%s", urlEncode(unit)));
			return url;
		}
		else
		{
			return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
		}
	}

	@RequestMapping(value = "/approvers", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getPagedApproversForUnit(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = UserModel.NAME) final String sortCode,
			@RequestParam("unit") final String unit, final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/approvers/?unit=%s",
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.approvers", new Object[]
		{ unit }, "Business Unit {0} Approvers", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		model.addAttribute("unit", unitData);

		// Handle paged search results
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<UserData> searchPageData = b2bCommerceUnitFacade.getPagedApproversForUnit(pageableData, unit);
		populateModel(model, searchPageData, showMode);
		model.addAttribute("action", "approvers");
		model.addAttribute("baseUrl", MANAGE_UNITS_BASE_URL);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitApproverListPage;
	}

	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getPagedCustomersForUnit(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = UserModel.NAME) final String sortCode,
			@RequestParam("unit") final String unit, @RequestParam("role") final String role, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/customers?unit=%s&role=%s",
				urlEncode(unit), urlEncode(role)), getMessageSource().getMessage("text.company.manage.units.customers", new Object[]
		{ unit }, "Business Unit {0} Customers", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		model.addAttribute("unit", unitData);

		// Handle paged search results
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<UserData> searchPageData = b2bCommerceUnitFacade.getPagedCustomersForUnit(pageableData, unit);
		populateModel(model, searchPageData, showMode);
		model.addAttribute("action", "customers");
		model.addAttribute("baseUrl", MANAGE_UNITS_BASE_URL);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitUserListPage;
	}

	@RequestMapping(value = "/administrators", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getPagedAdministratorsForUnit(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = UserModel.NAME) final String sortCode,
			@RequestParam("unit") final String unit, @RequestParam("role") final String role, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format(
				"/my-company/organization-management/manage-units/administrators?unit=%s&role=%s", urlEncode(unit), urlEncode(role)),
				getMessageSource().getMessage("text.company.manage.units.administrators", new Object[]
				{ unit }, "Business Unit {0} Administrators", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		model.addAttribute("unit", unitData);

		// Handle paged search results
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<UserData> searchPageData = b2bCommerceUnitFacade.getPagedAdministratorsForUnit(pageableData, unit);
		populateModel(model, searchPageData, showMode);
		model.addAttribute("action", "administrators");
		model.addAttribute("baseUrl", MANAGE_UNITS_BASE_URL);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitUserListPage;
	}

	@RequestMapping(value = "/managers", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getPagedManagersForUnit(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = UserModel.NAME) final String sortCode,
			@RequestParam("unit") final String unit, @RequestParam("role") final String role, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/managers?unit=%s&role=%s",
				urlEncode(unit), urlEncode(role)), getMessageSource().getMessage("text.company.manage.units.managers", new Object[]
		{ unit }, "Business Unit {0} Managers", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		model.addAttribute("unit", unitData);

		// Handle paged search results
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<UserData> searchPageData = b2bCommerceUnitFacade.getPagedManagersForUnit(pageableData, unit);
		populateModel(model, searchPageData, showMode);
		model.addAttribute("action", "managers");
		model.addAttribute("baseUrl", MANAGE_UNITS_BASE_URL);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitUserListPage;
	}

	@ResponseBody
	@RequestMapping(value = "/approvers/select", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData selectApprover(@RequestParam("unit") final String unit, @RequestParam("user") final String user,
			final Model model) throws CMSItemNotFoundException
	{
		return populateDisplayNamesForRoles(b2bCommerceUnitFacade.addApproverToUnit(unit, user));
	}

	@ResponseBody
	@RequestMapping(value = "/approvers/deselect", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData deselectApprover(@RequestParam("unit") final String unit, @RequestParam("user") final String user,
			final Model model) throws CMSItemNotFoundException
	{
		return populateDisplayNamesForRoles(b2bCommerceUnitFacade.removeApproverFromUnit(unit, user));
	}

	@RequestMapping(value = "/approvers/remove", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeApproverFromUnit(@RequestParam("unit") final String unit, @RequestParam("user") final String user,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		b2bCommerceUnitFacade.removeApproverFromUnit(unit, user);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "success.remove.user.from.unit",
				new Object[]
				{ user, unit });
		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
	}

	@RequestMapping(value = "/members/remove", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeMemberFromUnit(@RequestParam("unit") final String unit, @RequestParam("user") final String user,
			@RequestParam("role") final String role, final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		b2bCommerceUserFacade.removeUserRole(user, role);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "success.remove.user.from.unit",
				new Object[]
				{ user, unit });
		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
	}

	@RequestMapping(value = "/members/confirm/remove", method =
	{ RequestMethod.GET })
	@RequireHardLogIn
	public String confirmRemoveMemberFromUnit(@RequestParam("unit") final String unit, @RequestParam("user") final String user,
			@RequestParam("role") final String role, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{

		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage(
				String.format("text.company.units.remove.%s.confirmation", role), new Object[]
				{ user, unit }, "Remove {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		model.addAttribute("arguments", String.format("%s, %s", user, unit));
		model.addAttribute("page", "units");
		model.addAttribute("role", role);
		model.addAttribute("disableUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/members/remove/?unit=%s&user=%s&role=%s", urlEncode(unit),
				urlEncode(user), urlEncode(role)));
		model.addAttribute("cancelUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/details?unit=%s", urlEncode(unit)));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyRemoveDisableConfirmationPage;

	}

	@RequestMapping(value = "/approvers/confirm/remove", method =
	{ RequestMethod.GET })
	@RequireHardLogIn
	public String confirmRemoveApproverFromUnit(@RequestParam("unit") final String unit, @RequestParam("user") final String user,
			@RequestParam("role") final String role, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage(
				String.format("text.company.units.remove.%s.confirmation", role), new Object[]
				{ user, unit }, "Remove {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		model.addAttribute("arguments", String.format("%s, %s", user, unit));
		model.addAttribute("page", "units");
		model.addAttribute("role", role);
		model.addAttribute("disableUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/approvers/remove/?unit=%s&user=%s", urlEncode(unit),
				urlEncode(user)));
		model.addAttribute("cancelUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/details?unit=%s", urlEncode(unit)));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyRemoveDisableConfirmationPage;
	}

	@ResponseBody
	@RequestMapping(value = "/members/select", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData selectCustomer(@RequestParam("user") final String user, @RequestParam("role") final String role,
			final Model model) throws CMSItemNotFoundException
	{
		return populateDisplayNamesForRoles(b2bCommerceUserFacade.addUserRole(user, role));
	}

	@ResponseBody
	@RequestMapping(value = "/members/deselect", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData deselectCustomer(@RequestParam("user") final String user, @RequestParam("role") final String role,
			final Model model) throws CMSItemNotFoundException
	{
		return populateDisplayNamesForRoles(b2bCommerceUserFacade.removeUserRole(user, role));
	}

	@RequestMapping(value = "/add-address", method = RequestMethod.GET)
	@RequireHardLogIn
	public String addAddress(@RequestParam("unit") final String unit, final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
		model.addAttribute("titleData", getUserFacade().getTitles());
		model.addAttribute("addressForm", new AddressForm());

		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/add-address/?unit=%s",
				urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.addAddress", new Object[]
		{ unit }, "Add Address for {0} Business Unit ", getI18nService().getCurrentLocale()), null));
		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		if (unitData != null)
		{
			model.addAttribute("unitName", unitData.getName());
		}
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("uid", unit);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitAddAddressPage;
	}

	@RequestMapping(value = "/add-address", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addAddress(@RequestParam("unit") final String unit, @Valid final AddressForm addressForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
			breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/add-address/?unit=%s",
					urlEncode(unit)), getMessageSource().getMessage("text.company.manage.units.addAddress", new Object[]
			{ unit }, "Add Address to {0} Business Unit ", getI18nService().getCurrentLocale()), null));
			model.addAttribute("breadcrumbs", breadcrumbs);
			GlobalMessages.addErrorMessage(model, "form.global.error");
			storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
			model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
			model.addAttribute("titleData", getUserFacade().getTitles());
			model.addAttribute("uid", unit);
			final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
			if (unitData != null)
			{
				model.addAttribute("unitName", unitData.getName());
			}
			return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitAddAddressPage;
		}

		final AddressData newAddress = new AddressData();
		newAddress.setTitleCode(addressForm.getTitleCode());
		newAddress.setFirstName(addressForm.getFirstName());
		newAddress.setLastName(addressForm.getLastName());
		newAddress.setLine1(addressForm.getLine1());
		newAddress.setLine2(addressForm.getLine2());
		newAddress.setTown(addressForm.getTownCity());
		newAddress.setPostalCode(addressForm.getPostcode());
		newAddress.setBillingAddress(false);
		newAddress.setShippingAddress(true);

		final CountryData countryData = new CountryData();

		countryData.setIsocode(addressForm.getCountryIso());
		newAddress.setCountry(countryData);

		try
		{
			b2bCommerceUnitFacade.addAddressToUnit(newAddress, unit);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.added");
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "form.global.error");
		}

		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
	}

	@RequestMapping(value = "/remove-address", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeAddress(@RequestParam("unit") final String unit, @RequestParam("addressId") final String addressId,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		try
		{
			b2bCommerceUnitFacade.removeAddressFromUnit(unit, addressId);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"account.confirmation.address.removed");
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "form.global.error");
		}

		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
	}

	@RequestMapping(value = "/edit-address", method =
	{ RequestMethod.GET })
	@RequireHardLogIn
	public String editAddress(@RequestParam("unit") final String unit, @RequestParam("addressId") final String addressId,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{

		final AddressForm addressForm = new AddressForm();
		model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
		model.addAttribute("titleData", getUserFacade().getTitles());
		model.addAttribute("addressForm", addressForm);
		final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
		if (unit != null)
		{
			for (final AddressData addressData : unitData.getAddresses())
			{
				if (addressData.getId() != null && addressData.getId().equals(addressId))
				{
					model.addAttribute("addressData", addressData);
					addressForm.setAddressId(addressData.getId());
					addressForm.setTitleCode(addressData.getTitleCode());
					addressForm.setFirstName(addressData.getFirstName());
					addressForm.setLastName(addressData.getLastName());
					addressForm.setLine1(addressData.getLine1());
					addressForm.setLine2(addressData.getLine2());
					addressForm.setTownCity(addressData.getTown());
					addressForm.setPostcode(addressData.getPostalCode());
					addressForm.setCountryIso(addressData.getCountry().getIsocode());
					break;
				}
			}
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "b2bunit.notfound");
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format(
				"/my-company/organization-management/manage-units/edit-address/?unit=%s&addressId=%s", urlEncode(unit),
				urlEncode(addressId)), getMessageSource().getMessage("text.company.manage.units.editAddress", new Object[]
		{ unit }, "Edit Address for {0} Business Unit ", getI18nService().getCurrentLocale()), null));

		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("uid", unit);
		model.addAttribute("unitName", unitData.getName());
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUnitAddAddressPage;
	}

	@RequestMapping(value = "/edit-address", method =
	{ RequestMethod.POST })
	@RequireHardLogIn
	public String editAddress(@RequestParam("unit") final String unit, @RequestParam("addressId") final String addressId,
			@Valid final AddressForm addressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{

		if (bindingResult.hasErrors())
		{
			final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
			breadcrumbs.add(new Breadcrumb(String.format(
					"/my-company/organization-management/manage-units/edit-address/?unit=%s&addressId=%s", urlEncode(unit),
					urlEncode(addressId)), getMessageSource().getMessage("text.company.manage.units.editAddress.breadcrumb",
					new Object[]
					{ unit }, "Edit Address of {0} Business Unit ", getI18nService().getCurrentLocale()), null));

			GlobalMessages.addErrorMessage(model, "form.global.error");
			storeCmsPageInModel(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MANAGE_UNITS_CMS_PAGE));
			model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
			model.addAttribute("titleData", getUserFacade().getTitles());
			model.addAttribute("uid", unit);
			final B2BUnitData unitData = companyB2BCommerceFacade.getUnitForUid(unit);
			if (unitData != null)
			{
				model.addAttribute("unitName", unitData.getName());
			}
			return ControllerConstants.Views.Pages.Account.AccountEditAddressPage;
		}

		final AddressData newAddress = new AddressData();
		newAddress.setId(addressForm.getAddressId());
		newAddress.setTitleCode(addressForm.getTitleCode());
		newAddress.setFirstName(addressForm.getFirstName());
		newAddress.setLastName(addressForm.getLastName());
		newAddress.setLine1(addressForm.getLine1());
		newAddress.setLine2(addressForm.getLine2());
		newAddress.setTown(addressForm.getTownCity());
		newAddress.setPostalCode(addressForm.getPostcode());
		newAddress.setBillingAddress(false);
		newAddress.setShippingAddress(true);

		final CountryData countryData = new CountryData();

		countryData.setIsocode(addressForm.getCountryIso());
		newAddress.setCountry(countryData);

		try
		{
			b2bCommerceUnitFacade.editAddressOfUnit(newAddress, unit);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"account.confirmation.address.updated");
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "form.global.error");
		}

		return String.format(REDIRECT_TO_UNIT_DETAILS, urlEncode(unit));
	}

	@RequestMapping(value = "/viewuser", method = RequestMethod.GET)
	@RequireHardLogIn
	public String viewCustomerOfUnit(@RequestParam("unit") final String unit, @RequestParam("user") final String user,
			final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		final String url = manageUserDetail(user, model, request);
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(unit);
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/viewuser?unit=%s&user=%s",
				urlEncode(unit), urlEncode(user)), getMessageSource().getMessage("text.company.manage.units.viewcustomer.breadcrumb",
				new Object[]
				{ user }, "View Customer {0} ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("saveUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/edituser?unit=%s&user=%s", urlEncode(unit), urlEncode(user)));
		model.addAttribute("cancelUrl", String.format(request.getContextPath()
				+ "/my-company/organization-management/manage-units/details?unit=%s", urlEncode(unit)));
		model.addAttribute("action", "manage.units");
		model.addAttribute("metaRobots", "no-index,no-follow");
		return url;
	}

}
