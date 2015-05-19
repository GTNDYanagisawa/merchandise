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

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.B2BPermissionTypeEnum;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.yb2bacceleratorstorefront.annotations.RequireHardLogIn;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.Breadcrumb;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.platform.yb2bacceleratorstorefront.forms.B2BPermissionForm;
import de.hybris.platform.yb2bacceleratorstorefront.forms.B2BPermissionTypeSelectionForm;

import java.text.ParseException;
import java.util.List;

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
 * Controller defines routes to manage Permissions and B2B User groups within My Company section.
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-company/organization-management/manage-permissions")
public class PermissionManagementPageController extends MyCompanyPageController
{
	private static final Logger LOG = Logger.getLogger(PermissionManagementPageController.class);

	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	public String managePermissions(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = B2BPermissionModel.CODE) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManagePermissionsBreadcrumb();
		model.addAttribute("breadcrumbs", breadcrumbs);

		// Handle paged search results
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<B2BPermissionData> searchPageData = b2bCommercePermissionFacade.getPagedPermissions(pageableData);
		populateModel(model, searchPageData, showMode);
		model.addAttribute("action", "managePermissions");
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManagePermissionsPage;
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	public String viewPermissionDetails(@RequestParam("permissionCode") final String permissionCode, final Model model)
			throws CMSItemNotFoundException
	{
		model.addAttribute("permissionData", b2bCommercePermissionFacade.getPermissionDetails(permissionCode));
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManagePermissionsBreadcrumb();
		model.addAttribute("breadcrumbs", breadcrumbs);
		breadcrumbs.add(new Breadcrumb(String.format(MANAGE_PERMISSIONS_VIEW_URL, permissionCode), getMessageSource().getMessage(
				"text.company.managePermissions.view.page", new Object[]
				{ permissionCode }, "View Permission {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManagePermissionsViewPage;
	}

	@Override
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editPermission(@RequestParam("permissionCode") final String permissionCode, final Model model)
			throws CMSItemNotFoundException
	{
		return super.editPermission(permissionCode, model);
	}

	@Override
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@RequireHardLogIn
	public String editPermission(@Valid final B2BPermissionForm b2BPermissionForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException, ParseException
	{
		return super.editPermission(b2BPermissionForm, bindingResult, model, redirectModel);
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getSelectPermissionTypePage(final Model model) throws CMSItemNotFoundException
	{
		if (!model.containsAttribute("b2BPermissionTypeSelectionForm"))
		{
			model.addAttribute(new B2BPermissionTypeSelectionForm());
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManagePermissionsBreadcrumb();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-permissions/add", getMessageSource().getMessage(
				"text.company.managePermissions.create.step1.page", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManagePermissionTypeSelectPage;

	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addNewPermission(@Valid final B2BPermissionTypeSelectionForm b2BPermissionTypeSelectionForm,
			final BindingResult bindingResult, final Model model) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			model.addAttribute(b2BPermissionTypeSelectionForm);
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return getSelectPermissionTypePage(model);
		}

		if (!model.containsAttribute("b2BPermissionForm"))
		{
			final B2BPermissionForm b2BPermissionForm = new B2BPermissionForm();
			final B2BPermissionTypeEnum permissionTypeEnum = B2BPermissionTypeEnum.valueOf(b2BPermissionTypeSelectionForm
					.getB2BPermissionType());
			final B2BPermissionTypeData b2BPermissionTypeData = b2bCommercePermissionFacade
					.getB2BPermissionTypeDataForPermission(permissionTypeEnum);
			b2BPermissionForm.setB2BPermissionTypeData(b2BPermissionTypeData);
			b2BPermissionForm.setParentUnitName(companyB2BCommerceFacade.getParentUnit().getUid());
			b2BPermissionForm.setPermissionType(b2BPermissionTypeData.getName());
			model.addAttribute(b2BPermissionForm);
		}
		return getAddPermissionPage(model);
	}

	@RequestMapping(value = "/addPermission", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getAddPermissionPage(final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManagePermissionsBreadcrumb();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-permissions/add", getMessageSource().getMessage(
				"text.company.managePermissions.create.step1.page", null, getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-permissions/addPermission", getMessageSource()
				.getMessage("text.company.managePermissions.create.step2.page", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManagePermissionAddPage;
	}

	@RequestMapping(value = "/add/save", method = RequestMethod.POST)
	@RequireHardLogIn
	public String saveNewPermissionDetails(@Valid final B2BPermissionForm b2BPermissionForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException, ParseException
	{
		b2BPermissionFormValidator.validate(b2BPermissionForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			model.addAttribute(b2BPermissionForm);
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return getAddPermissionPage(model);
		}

		final B2BPermissionData b2BPermissionData = populateB2BPermissionDataFromForm(b2BPermissionForm);
		try
		{
			b2bCommercePermissionFacade.addPermission(b2BPermissionData);
		}
		catch (final Exception e)
		{
			LOG.warn("Exception while saving the permission details " + e);
			if (e instanceof DuplicateUidException)
			{
				model.addAttribute(b2BPermissionForm);
				bindingResult.rejectValue("code", "text.company.managePermissions.code.exists.error.title");
				GlobalMessages.addErrorMessage(model, "form.global.error");
				return getAddPermissionPage(model);
			}
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManagePermissionsBreadcrumb();
		model.addAttribute("breadcrumbs", breadcrumbs);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.permission.created");
		return String.format(REDIRECT_TO_PERMISSION_DETAILS, b2BPermissionData.getCode());
	}

	@RequestMapping(value = "/enable", method = RequestMethod.GET)
	@RequireHardLogIn
	public String enablePermission(@RequestParam("permissionCode") final String permissionCode, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			b2bCommercePermissionFacade.enableDisablePermission(permissionCode, true);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return viewPermissionDetails(permissionCode, model);
		}

		return String.format(REDIRECT_TO_PERMISSION_DETAILS, urlEncode(permissionCode));
	}

	@RequestMapping(value = "/disable", method = RequestMethod.GET)
	@RequireHardLogIn
	public String confirmDisablePermission(@RequestParam("permissionCode") final String permissionCode, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManagePermissionsBreadcrumb();
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-permissions/%s/disable",
				permissionCode), getMessageSource().getMessage("text.company.managePermissions.disable.breadcrumb", new Object[]
		{ permissionCode }, "Disable {0} ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("permissionCode", permissionCode);
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MyCompany.MyCompanyManagePermissionDisablePage;
	}

	@RequestMapping(value = "/disable", method = RequestMethod.POST)
	@RequireHardLogIn
	public String disablePermission(@RequestParam("permissionCode") final String permissionCode, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			b2bCommercePermissionFacade.enableDisablePermission(permissionCode, false);
		}
		catch (final Exception e)
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return viewPermissionDetails(permissionCode, model);
		}

		return String.format(REDIRECT_TO_PERMISSION_DETAILS, urlEncode(permissionCode));
	}
}
