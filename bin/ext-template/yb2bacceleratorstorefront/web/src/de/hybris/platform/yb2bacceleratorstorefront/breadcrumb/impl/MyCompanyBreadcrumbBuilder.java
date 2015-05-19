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
package de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.impl;

import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.Breadcrumb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;


/**
 * MyCompanyBreadcrumbBuilder implementation for account related pages
 */
public class MyCompanyBreadcrumbBuilder
{
	private static final String MY_COMPANY_URL = "/my-company";
	private static final String MY_COMPANY_MESSAGE_KEY = "header.link.company";
	protected static final String MANAGE_COSTCENTERS_VIEW_URL = "/my-company/organization-management/manage-costcenters/view/?costCenterCode=%s";

	private MessageSource messageSource;
	private I18NService i18nService;

	public List<Breadcrumb> getBreadcrumbs(final String resourceKey) throws IllegalArgumentException
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(new Breadcrumb(MY_COMPANY_URL, getMessageSource().getMessage(MY_COMPANY_MESSAGE_KEY, null,
				getI18nService().getCurrentLocale()), null));

		if (resourceKey != null)
		{
			if (StringUtils.isNotBlank(resourceKey))
			{
				breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage(resourceKey, null,
						getI18nService().getCurrentLocale()), null));
			}
		}

		return breadcrumbs;
	}


	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected MessageSource getMessageSource()
	{
		return messageSource;
	}

	@Required
	public void setMessageSource(final MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	public List<Breadcrumb> createManageUnitsBreadcrumbs()
	{
		final List<Breadcrumb> breadcrumbs = createOrganizationManagementBreadcrumbs();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-units/", messageSource.getMessage(
				"text.company.manage.units", null, i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	protected List<Breadcrumb> createOrganizationManagementBreadcrumbs()
	{
		final List<Breadcrumb> breadcrumbs = this.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/", messageSource.getMessage(
				"text.company.organizationManagement", null, i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createManageUnitsDetailsBreadcrumbs(final String uid)
	{
		final List<Breadcrumb> breadcrumbs = this.createManageUnitsBreadcrumbs();
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/details/?unit=%s", uid),
				messageSource.getMessage("text.company.manage.units.details", new Object[]
				{ uid }, "View Unit: {0} ", i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createManageCostCenterBreadCrumbs()
	{
		final List<Breadcrumb> breadcrumbs = createOrganizationManagementBreadcrumbs();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-costcenters", messageSource.getMessage(
				"text.company.manageCostCenters", null, i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createManageUserBreadcrumb()
	{
		final List<Breadcrumb> breadcrumbs = createOrganizationManagementBreadcrumbs();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-users", messageSource.getMessage(
				"text.company.manageUsers", null, i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}


	public List<Breadcrumb> createManageUserDetailsBreadcrumb(final String uid)
	{
		final List<Breadcrumb> breadcrumbs = createManageUserBreadcrumb();
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-users/details?user=%s", uid),
				messageSource.getMessage("text.company.manageUsers.details.breadcrumb", new Object[]
				{ uid }, "Manage {0} Customer", i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createManageBudgetsBreadCrumbs()
	{
		final List<Breadcrumb> breadcrumbs = this.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/", messageSource.getMessage(
				"text.company.organizationManagement", null, i18nService.getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-budgets", messageSource.getMessage(
				"text.company.manageBudgets", null, i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createManagePermissionsBreadcrumb()
	{
		final List<Breadcrumb> breadcrumbs = createOrganizationManagementBreadcrumbs();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-permissions", messageSource.getMessage(
				"text.company.managePermissions", null, i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createManageUserGroupBreadCrumbs()
	{
		final List<Breadcrumb> breadcrumbs = createOrganizationManagementBreadcrumbs();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-usergroups", messageSource.getMessage(
				"text.company.manageUsergroups.breadcrumb", null, i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createManageUserGroupDetailsBreadCrumbs(final String uid)
	{
		final List<Breadcrumb> breadcrumbs = createManageUserGroupBreadCrumbs();
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-usergroups/details?usergroup=%s",
				uid), messageSource.getMessage("text.company.manageUsergroups.details", new Object[]
		{ uid }, "Manage {0} Usergroup details", i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public void addViewCostCenterBreadCrumbs(final List<Breadcrumb> breadcrumbs, final String costCenterCode)
	{
		breadcrumbs.add(new Breadcrumb(String.format(MANAGE_COSTCENTERS_VIEW_URL, urlEncode(costCenterCode)), messageSource
				.getMessage("text.company.costCenter.viewPage", new Object[]
				{ costCenterCode }, "View Cost Center: {0}", i18nService.getCurrentLocale()), null));
	}

	public void addViewBudgetBreadCrumbs(final List<Breadcrumb> breadcrumbs, final String budgetCode)
	{
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-budgets/view/?budgetCode=%s",
				urlEncode(budgetCode)), messageSource.getMessage("text.company.budget.viewPage", new Object[]
		{ budgetCode }, "View Budget: {0}", i18nService.getCurrentLocale()), null));
	}


	protected String urlEncode(final String url)
	{
		try
		{
			return URLEncoder.encode(url, "UTF-8");
		}
		catch (final UnsupportedEncodingException e)
		{
			return url;
		}
	}
}
