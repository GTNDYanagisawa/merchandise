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
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * @author krzysztof.kwiatosz
 * 
 */
public class CCPaymentInfoReversePopulator implements Populator<CCPaymentInfoData, CreditCardPaymentInfoModel>
{

	private AddressReversePopulator addressReversePopulator;

	private EnumerationService enumerationService;


	@Override
	public void populate(final CCPaymentInfoData ccPaymentInfoData, final CreditCardPaymentInfoModel cardPaymentInfoModel)
			throws ConversionException
	{
		Assert.notNull(ccPaymentInfoData, "Parameter ccPaymentInfoData cannot be null.");
		Assert.notNull(cardPaymentInfoModel, "Parameter creditCardPaymentInfoModel cannot be null.");

		cardPaymentInfoModel.setCcOwner(ccPaymentInfoData.getAccountHolderName());

		cardPaymentInfoModel.setSubscriptionId(ccPaymentInfoData.getSubscriptionId());
		cardPaymentInfoModel.setNumber(getMaskedCardNumber(ccPaymentInfoData.getCardNumber()));

		final CreditCardType cardType = (CreditCardType) getEnumerationService().getEnumerationValue(
				CreditCardType.class.getSimpleName(), ccPaymentInfoData.getCardType());

		cardPaymentInfoModel.setType(cardType);
		cardPaymentInfoModel.setValidToMonth(String.valueOf(ccPaymentInfoData.getExpiryMonth()));
		cardPaymentInfoModel.setValidToYear(String.valueOf(ccPaymentInfoData.getExpiryYear()));
		/*
		 * if (ccPaymentInfoData.getStartMonth() != null) {
		 */
		cardPaymentInfoModel.setValidFromMonth(/* String.valueOf( */ccPaymentInfoData.getStartMonth()/* ) */);
		/*
		 * } if (ccPaymentInfoData.getStartYear() != null) {
		 */
		cardPaymentInfoModel.setValidFromYear(/* String.valueOf( */ccPaymentInfoData.getStartYear()/* ) */);
		//}

		cardPaymentInfoModel.setSaved(ccPaymentInfoData.isSaved());
		if (!StringUtils.isEmpty(ccPaymentInfoData.getIssueNumber()))
		{
			cardPaymentInfoModel.setIssueNumber(Integer.valueOf(ccPaymentInfoData.getIssueNumber()));
		}

		final AddressData billingAddressData = ccPaymentInfoData.getBillingAddress();
		final AddressModel billingAddressModel = cardPaymentInfoModel.getBillingAddress();
		if (billingAddressData != null && billingAddressModel != null)
		{
			getAddressReversePopulator().populate(billingAddressData, billingAddressModel);
		}
	}


	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setAddressReversePopulator(final AddressReversePopulator addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	protected AddressReversePopulator getAddressReversePopulator()
	{
		return addressReversePopulator;
	}

	protected String getMaskedCardNumber(final String cardNumber)
	{
		if (cardNumber != null && cardNumber.trim().length() > 4)
		{
			final String endPortion = cardNumber.trim().substring(cardNumber.length() - 4);
			return "************" + endPortion;
		}
		return null;
	}
}
