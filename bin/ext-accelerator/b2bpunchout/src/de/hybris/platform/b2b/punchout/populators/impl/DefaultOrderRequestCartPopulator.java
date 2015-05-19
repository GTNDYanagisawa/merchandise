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
package de.hybris.platform.b2b.punchout.populators.impl;

import de.hybris.platform.b2b.punchout.util.CXmlDateUtil;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.TaxValue;

import java.text.ParseException;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.cxml.Address;
import org.cxml.OrderRequestHeader;
import org.cxml.Tax;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@link CartModel} based on an {@link OrderRequestHeader}.
 */
public class DefaultOrderRequestCartPopulator implements Populator<OrderRequestHeader, CartModel>
{
	private Populator<Tax, Collection<TaxValue>> taxValuePopulator;

	private Converter<Address, AddressModel> addressModelConverter;

	private CommonI18NService commonI18NService;

	private CXmlDateUtil cXmlDateUtil;

	@Override
	public void populate(final OrderRequestHeader source, final CartModel target) throws ConversionException
	{
		if (!StringUtils.equalsIgnoreCase(source.getType(), "new"))
		{
			throw new UnsupportedOperationException("Operation not supported yet: " + source.getType());
		}
		target.setPurchaseOrderNumber(source.getOrderID());

		final AddressModel deliveryAddress = addressModelConverter.convert(source.getShipTo().getAddress());
		deliveryAddress.setOwner(target);
		target.setDeliveryAddress(deliveryAddress);

		final AddressModel billToAddress = addressModelConverter.convert(source.getBillTo().getAddress());
		billToAddress.setOwner(target);
		target.setPaymentAddress(billToAddress);

		target.setDeliveryCost(getDeliveryCost(source));

		taxValuePopulator.populate(source.getTax(), target.getTotalTaxValues());
		target.setTotalTax(sumUpAllTaxes(target.getTotalTaxValues()));
		target.setTotalPrice(Double.valueOf(source.getTotal().getMoney().getvalue()));
		target.setCurrency(commonI18NService.getCurrency(source.getTotal().getMoney().getCurrency()));

		try
		{
			target.setDate(cXmlDateUtil.parseString(source.getOrderDate()));
		}
		catch (final ParseException e)
		{
			throw new ConversionException("Could not parse date string: " + source.getOrderDate());
		}
	}

	private Double getDeliveryCost(final OrderRequestHeader source)
	{
		if (source.getShipping() != null)
		{
			return Double.valueOf(source.getShipping().getMoney().getvalue());
		}
		return new Double(0D);
	}

	private Double sumUpAllTaxes(final Collection<TaxValue> totalTaxValues)
	{
		double result = 0D;
		for (final TaxValue taxValue : totalTaxValues)
		{
			result += taxValue.getValue();
		}
		return Double.valueOf(result);
	}

	public Populator<Tax, Collection<TaxValue>> getTaxValuePopulator()
	{
		return taxValuePopulator;
	}

	@Required
	public void setTaxValuePopulator(final Populator<Tax, Collection<TaxValue>> taxValuePopulator)
	{
		this.taxValuePopulator = taxValuePopulator;
	}

	public Converter<Address, AddressModel> getAddressModelConverter()
	{
		return addressModelConverter;
	}

	@Required
	public void setAddressModelConverter(final Converter<Address, AddressModel> addressModelConverter)
	{
		this.addressModelConverter = addressModelConverter;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public CXmlDateUtil getcXmlDateUtil()
	{
		return cXmlDateUtil;
	}

	@Required
	public void setcXmlDateUtil(final CXmlDateUtil cXmlDateUtil)
	{
		this.cXmlDateUtil = cXmlDateUtil;
	}
}
