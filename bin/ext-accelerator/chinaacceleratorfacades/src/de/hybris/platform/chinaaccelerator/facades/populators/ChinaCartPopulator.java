/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.chinaaccelerator.facades.populators;



import de.hybris.platform.chinaaccelerator.facades.data.PaymentModeData;
import de.hybris.platform.chinaaccelerator.services.model.invoice.InvoiceModel;
import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.InvoiceData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * Accelerator specific variant option data converter implementation.
 */
public class ChinaCartPopulator extends CartPopulator
{

	private Converter<InvoiceModel, InvoiceData> invoiceConverter;
	private Converter<PaymentModeModel, PaymentModeData> paymentModeConverter;

	/**
	 * @return the invoiceConverter
	 */
	public Converter<InvoiceModel, InvoiceData> getInvoiceConverter()
	{
		return invoiceConverter;
	}

	/**
	 * @param invoiceConverter
	 *           the invoiceConverter to set
	 */
	public void setInvoiceConverter(final Converter<InvoiceModel, InvoiceData> invoiceConverter)
	{
		this.invoiceConverter = invoiceConverter;
	}

	/**
	 * @return the paymentModeConverter
	 */
	public Converter<PaymentModeModel, PaymentModeData> getPaymentModeConverter()
	{
		return paymentModeConverter;
	}

	/**
	 * @param paymentModeConverter
	 *           the paymentModeConverter to set
	 */
	public void setPaymentModeConverter(final Converter<PaymentModeModel, PaymentModeData> paymentModeConverter)
	{
		this.paymentModeConverter = paymentModeConverter;
	}

	@Override
	public void populate(final CartModel source, final CartData target)
	{
		super.populate(source, target);
		if (source.getInvoice() != null)
		{
			final InvoiceData invData = invoiceConverter.convert(source.getInvoice());
			target.setInvoice(invData);
		}
		else
		{
			target.setInvoice(null);
		}

		if (source.getPaymentMode() != null)
		{
			target.setPaymentMode(paymentModeConverter.convert(source.getPaymentMode()));
		}
		else
		{
			target.setPaymentMode(null);
		}

		target.setDeliveryTimeslot(source.getDeliveryTimeslot());

	}
}
