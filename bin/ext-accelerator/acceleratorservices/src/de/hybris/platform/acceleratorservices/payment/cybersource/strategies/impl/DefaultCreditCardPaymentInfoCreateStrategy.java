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
package de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.cybersource.enums.CardTypeEnum;
import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.acceleratorservices.payment.strategies.CreditCardPaymentInfoCreateStrategy;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


public class DefaultCreditCardPaymentInfoCreateStrategy implements CreditCardPaymentInfoCreateStrategy
{
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private CustomerEmailResolutionService customerEmailResolutionService;

	@Override
	public CreditCardPaymentInfoModel createCreditCardPaymentInfo(final SubscriptionInfoData subscriptionInfo,
			final PaymentInfoData paymentInfo, final AddressModel billingAddress, final CustomerModel customerModel,
			final boolean saveInAccount)
	{
		validateParameterNotNull(subscriptionInfo, "subscriptionInfo cannot be null");
		validateParameterNotNull(paymentInfo, "paymentInfo cannot be null");
		validateParameterNotNull(billingAddress, "billingAddress cannot be null");
		validateParameterNotNull(customerModel, "customerModel cannot be null");

		final CreditCardPaymentInfoModel cardPaymentInfoModel = getModelService().create(CreditCardPaymentInfoModel.class);
		cardPaymentInfoModel.setBillingAddress(billingAddress);
		cardPaymentInfoModel.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
		cardPaymentInfoModel.setUser(customerModel);
		cardPaymentInfoModel.setSubscriptionId(subscriptionInfo.getSubscriptionID());

		cardPaymentInfoModel.setNumber(paymentInfo.getCardAccountNumber());
		cardPaymentInfoModel.setType(CreditCardType.valueOf(CardTypeEnum.get(paymentInfo.getCardCardType()).name().toUpperCase()));
		cardPaymentInfoModel.setCcOwner(getCCOwner(paymentInfo, billingAddress));
		cardPaymentInfoModel.setValidFromMonth(paymentInfo.getCardStartMonth());
		cardPaymentInfoModel.setValidFromYear(paymentInfo.getCardStartYear());
		if (paymentInfo.getCardExpirationMonth().intValue() > 0)
		{
			cardPaymentInfoModel.setValidToMonth(String.valueOf(paymentInfo.getCardExpirationMonth()));
		}
		if (paymentInfo.getCardExpirationYear().intValue() > 0)
		{
			cardPaymentInfoModel.setValidToYear(String.valueOf(paymentInfo.getCardExpirationYear()));
		}

		cardPaymentInfoModel.setSubscriptionId(subscriptionInfo.getSubscriptionID());
		cardPaymentInfoModel.setSaved(saveInAccount);
		if (StringUtils.isNotBlank(paymentInfo.getCardIssueNumber()))
		{
			cardPaymentInfoModel.setIssueNumber(Integer.valueOf(paymentInfo.getCardIssueNumber()));
		}

		return cardPaymentInfoModel;
	}

	@Override
	public CreditCardPaymentInfoModel saveSubscription(final CustomerModel customerModel, final CustomerInfoData customerInfoData,
	                                                   final SubscriptionInfoData subscriptionInfo, final PaymentInfoData paymentInfoData, final boolean saveInAccount)
	{
		validateParameterNotNull(customerInfoData, "customerInfoData cannot be null");
		validateParameterNotNull(subscriptionInfo, "subscriptionInfo cannot be null");
		validateParameterNotNull(paymentInfoData, "paymentInfoData cannot be null");

		final AddressModel billingAddress = getModelService().create(AddressModel.class);
		billingAddress.setFirstname(customerInfoData.getBillToFirstName());
		billingAddress.setLastname(customerInfoData.getBillToLastName());
		billingAddress.setLine1(customerInfoData.getBillToStreet1());
		billingAddress.setLine2(customerInfoData.getBillToStreet2());
		billingAddress.setTown(customerInfoData.getBillToCity());
		billingAddress.setPostalcode(customerInfoData.getBillToPostalCode());

		final CountryModel country = getCommonI18NService().getCountry(customerInfoData.getBillToCountry());
		billingAddress.setCountry(country);
		if (StringUtils.isNotEmpty(customerInfoData.getBillToState()))
		{
			billingAddress.setRegion(getCommonI18NService().getRegion(country,
					country.getIsocode() + "-" + customerInfoData.getBillToState()));
		}
		final String email = getCustomerEmailResolutionService().getEmailForCustomer(customerModel);
		billingAddress.setEmail(email);

		final CreditCardPaymentInfoModel cardPaymentInfoModel = this.createCreditCardPaymentInfo(subscriptionInfo, paymentInfoData,
				billingAddress, customerModel, saveInAccount);

		billingAddress.setOwner(cardPaymentInfoModel);

		if (CustomerType.GUEST.equals(customerModel.getType()))
		{
			final StringBuilder name = new StringBuilder();
			if (!StringUtils.isBlank(customerInfoData.getBillToFirstName()))
			{
				name.append(customerInfoData.getBillToFirstName());
				name.append(' ');
			}
			if (!StringUtils.isBlank(customerInfoData.getBillToLastName()))
			{
				name.append(customerInfoData.getBillToLastName());
			}
			customerModel.setName(name.toString());
			getModelService().save(customerModel);
		}

		getModelService().saveAll(cardPaymentInfoModel, billingAddress);
		getModelService().refresh(customerModel);

		final List<PaymentInfoModel> paymentInfoModels = new ArrayList<PaymentInfoModel>(customerModel.getPaymentInfos());
		if (!paymentInfoModels.contains(cardPaymentInfoModel))
		{
			paymentInfoModels.add(cardPaymentInfoModel);
			if (saveInAccount)
			{
				customerModel.setPaymentInfos(paymentInfoModels);
				getModelService().save(customerModel);
			}

			getModelService().save(cardPaymentInfoModel);
			getModelService().refresh(customerModel);
		}

		return cardPaymentInfoModel;
	}

	protected String getCCOwner(final PaymentInfoData paymentInfo, final AddressModel billingAddress)
	{
		if (paymentInfo.getCardAccountHolderName() != null && !paymentInfo.getCardAccountHolderName().isEmpty())
		{
			return paymentInfo.getCardAccountHolderName();
		}
		else
		{
			return billingAddress.getFirstname() + " " + billingAddress.getLastname();
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


	protected CustomerEmailResolutionService getCustomerEmailResolutionService()
	{
		return customerEmailResolutionService;
	}

	@Required
	public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService)
	{
		this.customerEmailResolutionService = customerEmailResolutionService;
	}
}
