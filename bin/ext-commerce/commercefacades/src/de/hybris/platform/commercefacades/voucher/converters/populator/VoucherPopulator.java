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
package de.hybris.platform.commercefacades.voucher.converters.populator;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.voucher.model.PromotionVoucherModel;
import de.hybris.platform.voucher.model.VoucherModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populator implementation for {@link de.hybris.platform.voucher.model.VoucherModel} as source and
 * {@link de.hybris.platform.commercefacades.voucher.data.VoucherData} as target type.
 */
public class VoucherPopulator implements Populator<VoucherModel, VoucherData>
{
	private AbstractConverter<CurrencyModel, CurrencyData> currencyConverter;

	@Override
	public void populate(final VoucherModel source, final VoucherData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setValue(source.getValue());
		target.setValueFormatted(source.getValueString());
		target.setDescription(source.getDescription());
		target.setValueString(source.getValueString());
		target.setFreeShipping(Boolean.TRUE.equals(source.getFreeShipping()));
		if (source.getCurrency() != null)
		{
			target.setCurrency(getCurrencyConverter().convert(source.getCurrency()));
		}
		if (source instanceof PromotionVoucherModel)
		{
			target.setVoucherCode(((PromotionVoucherModel) source).getVoucherCode());
		}
	}

	public AbstractConverter<CurrencyModel, CurrencyData> getCurrencyConverter()
	{
		return currencyConverter;
	}

	@Required
	public void setCurrencyConverter(final AbstractConverter<CurrencyModel, CurrencyData> currencyConverter)
	{
		this.currencyConverter = currencyConverter;
	}
}
