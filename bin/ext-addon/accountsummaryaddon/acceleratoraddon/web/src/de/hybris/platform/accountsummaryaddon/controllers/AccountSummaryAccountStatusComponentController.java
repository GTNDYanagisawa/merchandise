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
package de.hybris.platform.accountsummaryaddon.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.hybris.platform.accountsummaryaddon.constants.AccountsummaryaddonConstants;
import de.hybris.platform.accountsummaryaddon.document.data.B2BAmountBalanceData;
import de.hybris.platform.accountsummaryaddon.facade.B2BAccountSummaryFacade;
import de.hybris.platform.accountsummaryaddon.formatters.AmountFormatter;
import de.hybris.platform.accountsummaryaddon.jalo.B2BDocument;
import de.hybris.platform.accountsummaryaddon.model.AccountSummaryAccountStatusComponentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;
import de.hybris.platform.b2b.model.B2BCreditLimitModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.company.CompanyB2BCommerceFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.b2bacceleratorservices.company.CompanyB2BCommerceService;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.cms.AbstractCMSComponentController;


/**
 * Controller to display account status information.
 */
@Controller("AccountSummaryAccountStatusComponentController")
@Scope("tenant")
@RequestMapping(value = "/view/AccountSummaryAccountStatusComponentController")
public class AccountSummaryAccountStatusComponentController extends
		AbstractCMSComponentController<AccountSummaryAccountStatusComponentModel>
{
	private static final String COMMA_STR = ",";

	private static final String LIST_VIEW_PAGE_SIZE = "listViewPageSize";

	private static final String GRID_VIEW_PAGE_SIZE = "gridViewPageSize";
	
	private static final String DOCUMENT_TYPE_LIST = "documentTypeList";
	
	private static final String DOCUMENT_TYPE_CODE = "documentTypeCode";
	
	@Resource(name = "b2bCommerceFacade")
	private CompanyB2BCommerceFacade companyB2BCommerceFacade;

	@Resource(name = "b2bCommerceUnitService")
	private CompanyB2BCommerceService companyB2BCommerceService;

	@Resource(name = "b2bCustomerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "b2bAccountSummaryFacade")
	private B2BAccountSummaryFacade b2bAccountSummaryFacade;

	@Resource
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "i18nService")
	private I18NService i18nService;
	
	@Resource(name = "amountFormatter")
	private AmountFormatter amountFormatter;

	@Resource(name = "accountStatusSearchByList")
	private Map<String, Class> accountStatusSearchByList;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AccountSummaryAccountStatusComponentModel component)
	{
		final String unit = request.getParameter("unit");
		model.addAttribute("searchByList", buildSearchByList());

		prepareAccountInformation(unit, model);
		prepareDocumentTypeList(model);

		final Integer listViewPageSize = modelService.getAttributeValue(component, LIST_VIEW_PAGE_SIZE);
		final Integer gridViewPageSize = modelService.getAttributeValue(component, GRID_VIEW_PAGE_SIZE);

		model.addAttribute(LIST_VIEW_PAGE_SIZE, listViewPageSize);
		model.addAttribute(GRID_VIEW_PAGE_SIZE, gridViewPageSize);
		model.addAttribute(DOCUMENT_TYPE_CODE, B2BDocument.DOCUMENTTYPE);
	}

	@Override
	protected String getView(final AccountSummaryAccountStatusComponentModel component)
	{
		return AccountsummaryaddonConstants.ACCOUNT_STATUS_COMPONENTVIEW;
	}

	@SuppressWarnings("rawtypes")
	protected Map<String, Class> buildSearchByList()
	{
		return accountStatusSearchByList;
	}

	protected void prepareAccountInformation(final String unit, final Model model)
	{
		B2BUnitData b2bUnitData = companyB2BCommerceFacade.getUnitForUid(unit);
		B2BCreditLimitModel creditLimit = new B2BCreditLimitModel();
		B2BAmountBalanceData amountBalance = null;
		String accountManagerName = "";
		final StringBuffer emailBuffer = new StringBuffer();

		final B2BUnitModel b2bUnitModel = companyB2BCommerceService.getUnitForUid(unit);
		if (b2bUnitModel != null)
		{
			creditLimit = b2bUnitModel.getCreditLimit();

			EmployeeModel employeeModel = b2bUnitModel.getAccountManager();

			if (employeeModel != null)
			{
				final UserModel accountManager = userService.getUserForUID(employeeModel.getUid());

				if (accountManager != null)
				{
					fillEmailBuffer(emailBuffer, accountManager);
					accountManagerName = accountManager.getDisplayName();
				}
			}
			amountBalance = b2bAccountSummaryFacade.getAmountBalance(b2bUnitModel);
		}

		model.addAttribute("amountBalanceData", amountBalance);
		model.addAttribute("b2bUnitData", b2bUnitData);
		model.addAttribute("customerData", customerFacade.getCurrentCustomer());
		model.addAttribute("billingAddress", prepareDefaultAddress(b2bUnitData));
		model.addAttribute("creditLimit", getFormattedCreditLimit(creditLimit));
		model.addAttribute("accountManagerName", accountManagerName);
		model.addAttribute("email", emailBuffer.toString());
	}

	protected AddressData prepareDefaultAddress(B2BUnitData b2bUnitData) {
		AddressData billingAddress = new AddressData();
		
		if (b2bUnitData != null) {
			final List<AddressData> addresses = b2bUnitData.getAddresses();
			for (final AddressData addressData : addresses)
			{
				if (addressData.isShippingAddress())
				{
					billingAddress = addressData;
				}
	
				if (addressData.isBillingAddress())
				{
					billingAddress = addressData;
					break;
				}
			}
		}
		return billingAddress;
	}
	
	protected void fillEmailBuffer(final StringBuffer emailBuffer, final UserModel accountManager)
	{
		final Collection<AddressModel> userAddresses = accountManager.getAddresses();

		for (final AddressModel userAddress : userAddresses)
		{
			if (StringUtils.isEmpty(emailBuffer.toString()))
			{
				emailBuffer.append(userAddress.getEmail());
			}
			else
			{
				emailBuffer.append(COMMA_STR).append(userAddress.getEmail());
			}
		}
	}

	protected String getFormattedCreditLimit(final B2BCreditLimitModel creditLimit)
	{
		String formattedCreditLimit = StringUtils.EMPTY;
		if (creditLimit != null)
		{
			formattedCreditLimit = amountFormatter.formatAmount(creditLimit.getAmount(), creditLimit.getCurrency(), getI18nService()
					.getCurrentLocale());
		}
		return formattedCreditLimit;
	}

	protected void prepareDocumentTypeList(final Model model)
	{
		final List<B2BDocumentTypeModel> documentTypeList = b2bAccountSummaryFacade.getAllDocumentTypes().getResult();
		final StringBuffer strBuffer = new StringBuffer();
		for (final B2BDocumentTypeModel each : documentTypeList)
		{

			if (StringUtils.isEmpty(strBuffer.toString()))
			{
				strBuffer.append(each.getCode());
			}
			else
			{
				strBuffer.append(COMMA_STR).append(each.getCode());
			}
		}
		model.addAttribute(DOCUMENT_TYPE_LIST, documentTypeList);
	}

	public I18NService getI18nService()
	{
		return i18nService;
	}
}
