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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.cybersource.enums.CardTypeEnum;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


public class PaymentInfoDataPopulator extends AbstractResultPopulator<CreditCardPaymentInfoModel, PaymentInfoData>
{
	@Override
	public void populate(final CreditCardPaymentInfoModel source, final PaymentInfoData target) throws ConversionException
	{
		//We may not have any existing credit card details
		if (source == null)
		{
			return;
		}

		validateParameterNotNull(target, "Parameter [PaymentInfoData] target cannot be null");

		target.setCardAccountNumber(source.getNumber());
		final CardTypeEnum cardType = CardTypeEnum.valueOf(source.getType().name().toLowerCase());
		if (cardType != null)
		{
			target.setCardCardType(cardType.getStringValue());
		}
		target.setCardExpirationMonth(getIntegerForString(source.getValidToMonth()));
		target.setCardExpirationYear(getIntegerForString(source.getValidToYear()));
		target.setCardIssueNumber(String.valueOf(source.getIssueNumber()));
		target.setCardStartMonth(source.getValidFromMonth());
		target.setCardStartYear(source.getValidFromYear());
		target.setCardAccountHolderName(source.getCcOwner());
	}
}
