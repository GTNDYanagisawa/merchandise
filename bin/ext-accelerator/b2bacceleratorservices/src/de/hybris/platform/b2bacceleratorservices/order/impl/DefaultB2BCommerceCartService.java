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
package de.hybris.platform.b2bacceleratorservices.order.impl;

import de.hybris.platform.b2bacceleratorservices.order.B2BCommerceCartService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultB2BCommerceCartService extends DefaultCommerceCartService implements B2BCommerceCartService
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BCommerceCartService.class);
	private KeyGenerator guidKeyGenerator;
	private CalculationService calculationService;

	@Override
	public void calculateCartForPaymentTypeChange(final CartModel cartModel)
	{
		try
		{
			super.calculateCart(cartModel);
			getCalculationService().calculateTotals(cartModel, true);
		}
		catch (final CalculationException ex)
		{
			LOG.error("Failed to calculate order [" + cartModel + "]", ex);
		}
	}

	@Override
	public InvoicePaymentInfoModel createInvoicePaymentInfo(final CartModel cartModel)
	{
		final InvoicePaymentInfoModel invoicePaymentInfoModel = getModelService().create(InvoicePaymentInfoModel.class);
		invoicePaymentInfoModel.setUser(cartModel.getUser());
		invoicePaymentInfoModel.setCode(getGuidKeyGenerator().generate().toString());
		return invoicePaymentInfoModel;
	}


	protected KeyGenerator getGuidKeyGenerator()
	{
		return guidKeyGenerator;
	}


	@Required
	public void setGuidKeyGenerator(final KeyGenerator guidKeyGenerator)
	{
		this.guidKeyGenerator = guidKeyGenerator;
	}

	@Required
	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}
}
