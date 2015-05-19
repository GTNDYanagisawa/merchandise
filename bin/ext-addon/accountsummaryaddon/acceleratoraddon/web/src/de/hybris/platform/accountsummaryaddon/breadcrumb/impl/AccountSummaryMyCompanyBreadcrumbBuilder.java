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
package de.hybris.platform.accountsummaryaddon.breadcrumb.impl;

import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.Breadcrumb;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.impl.MyCompanyBreadcrumbBuilder;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;


/**
 * Builds a breadcrumb for the account summary section.
 */
public class AccountSummaryMyCompanyBreadcrumbBuilder extends MyCompanyBreadcrumbBuilder
{


	private static final String ACCOUNT_STATUS_PATH = "/my-company/organization-management/accountstatus/";
	private static final String TEXT_COMPANY_ACCOUNTSUMMARY_DETAILS = "text.company.accountsummary.details";
	private static final String TEXT_COMPANY_ACCOUNTSUMMARY = "text.company.accountsummary";

	@Override
	@Required
	public void setI18nService(final I18NService i18nService)
	{
		super.setI18nService(i18nService);
	}

	@Override
	@Required
	public void setMessageSource(final MessageSource messageSource)
	{
		super.setMessageSource(messageSource);
	}

	public List<Breadcrumb> createAccountSummaryBreadcrumbs()
	{
		final List<Breadcrumb> breadcrumbs = createOrganizationManagementBreadcrumbs();
		breadcrumbs.add(new Breadcrumb(ACCOUNT_STATUS_PATH, super.getMessageSource().getMessage(
				TEXT_COMPANY_ACCOUNTSUMMARY, null, super.getI18nService().getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createAccountSummaryDetailsBreadcrumbs(final String uid)
	{
		final List<Breadcrumb> breadcrumbs = this.createAccountSummaryBreadcrumbs();
		breadcrumbs.add(new Breadcrumb(String.format(ACCOUNT_STATUS_PATH + "details/?unit=%s", uid),
				super.getMessageSource().getMessage(TEXT_COMPANY_ACCOUNTSUMMARY_DETAILS, new Object[]
				{ uid }, "View Unit: {0} ", super.getI18nService().getCurrentLocale()), null));
		return breadcrumbs;
	}
}
