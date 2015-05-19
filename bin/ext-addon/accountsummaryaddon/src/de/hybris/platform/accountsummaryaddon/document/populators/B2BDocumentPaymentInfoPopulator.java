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
package de.hybris.platform.accountsummaryaddon.document.populators;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.util.localization.Localization;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentPaymentInfoData;
import de.hybris.platform.accountsummaryaddon.formatters.AmountFormatter;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentPaymentInfoModel;


public class B2BDocumentPaymentInfoPopulator implements Populator<B2BDocumentPaymentInfoModel, B2BDocumentPaymentInfoData>
{
	public static final String CC_PAYMENT = "accountsummary.ccpayment";

	private CommerceCommonI18NService commerceCommonI18NService;
	private CommonI18NService commonI18NService;
	private I18NService i18NService;
	private AmountFormatter amountFormatter;

	@Override
	public void populate(final B2BDocumentPaymentInfoModel source, final B2BDocumentPaymentInfoData target)
			throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final boolean usingDocument = StringUtils.isBlank(source.getCcTransactionNumber());
		final B2BDocumentModel document = source.getUseDocument();

		target.setExternal(source.getExternal());
		target.setDate(source.getDate());
		target.setAmount(source.getAmount());
		target.setReferenceNumber(usingDocument ? document.getDocumentNumber() : source.getCcTransactionNumber());
		target.setFormattedAmount(getFormattedAmount(source));
		target.setPaymentMethod(getPaymentMethod(usingDocument, document));
	}
	
	protected String getPaymentMethod(final boolean usingDocument, final B2BDocumentModel document)
	{
		String paymentMethod = null;
		if (usingDocument)
		{
			paymentMethod = document.getDocumentType().getName();
		}
		else
		{
			paymentMethod = Localization.getLocalizedString(CC_PAYMENT);
		}
		return paymentMethod;
	}
	
	protected String getFormattedAmount(final B2BDocumentPaymentInfoModel source)
	{
		// get local information
		final LanguageModel currentLanguage = getCommonI18NService().getCurrentLanguage();
		Locale locale = getCommerceCommonI18NService().getLocaleForLanguage(currentLanguage);
		if (locale == null)
		{
			locale = getI18NService().getCurrentLocale();
		}
		
		final CurrencyModel currency = source.getPayDocument().getCurrency();
		return amountFormatter.formatAmount(source.getAmount(), currency, locale);
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
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
	
	@Required
	public void setAmountFormatter(final AmountFormatter amountFormatter)
	{
		this.amountFormatter = amountFormatter;
	}

}
