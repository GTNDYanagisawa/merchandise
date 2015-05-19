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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 */
public class CreditCardPaymentInfoPopulator implements Populator<CreditCardPaymentInfoModel, CCPaymentInfoData>
{
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<CreditCardType, CardTypeData> cardTypeConverter;

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Converter<CreditCardType, CardTypeData> getCardTypeConverter()
	{
		return cardTypeConverter;
	}

	@Required
	public void setCardTypeConverter(final Converter<CreditCardType, CardTypeData> cardTypeConverter)
	{
		this.cardTypeConverter = cardTypeConverter;
	}

	@Override
	public void populate(final CreditCardPaymentInfoModel source, final CCPaymentInfoData target)
	{
		target.setId(source.getPk().toString());
		target.setCardNumber(source.getNumber());

		if (source.getType() != null)
		{
			final CardTypeData cardTypeData = getCardTypeConverter().convert(source.getType());
			target.setCardType(cardTypeData.getCode());
			target.setCardTypeData(cardTypeData);
		}

		target.setAccountHolderName(source.getCcOwner());
		target.setExpiryMonth(source.getValidToMonth());
		target.setExpiryYear(source.getValidToYear());
		target.setStartMonth(source.getValidFromMonth());
		target.setStartYear(source.getValidFromYear());

		target.setSubscriptionId(source.getSubscriptionId());
		target.setSaved(source.isSaved());

		if (source.getBillingAddress() != null)
		{
			target.setBillingAddress(getAddressConverter().convert(source.getBillingAddress()));
		}
		if (source.getIssueNumber() != null)
		{
			target.setIssueNumber(source.getIssueNumber().toString());
		}
	}
}
