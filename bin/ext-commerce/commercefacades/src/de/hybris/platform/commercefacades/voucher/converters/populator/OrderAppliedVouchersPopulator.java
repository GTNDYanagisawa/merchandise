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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherModel;


/**
 * Populate the {@link de.hybris.platform.commercefacades.order.data.AbstractOrderData} with the vouchers applied to
 * {@link de.hybris.platform.core.model.order.AbstractOrderModel}
 */
public class OrderAppliedVouchersPopulator implements Populator<AbstractOrderModel, AbstractOrderData>
{
	private VoucherService voucherService;
	private Converter<VoucherModel, VoucherData> voucherConverter;
	private Populator<AbstractOrderModel, VoucherData> appliedVoucherPopulatorList;

	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final List<VoucherData> vouchers = new ArrayList<VoucherData>();
		final Collection<DiscountModel> discounts = voucherService.getAppliedVouchers(source);
		for (final DiscountModel discount : discounts)
		{
			if (discount instanceof VoucherModel)
			{
				final VoucherData voucher = getVoucherConverter().convert((VoucherModel) discount);
				getAppliedVoucherPopulatorList().populate(source, voucher);
				vouchers.add(voucher);
			}
		}
		target.setAppliedVouchers(vouchers);
	}

	public VoucherService getVoucherService()
	{
		return voucherService;
	}

	@Required
	public void setVoucherService(final VoucherService voucherService)
	{
		this.voucherService = voucherService;
	}

	public Converter<VoucherModel, VoucherData> getVoucherConverter()
	{
		return voucherConverter;
	}

	@Required
	public void setVoucherConverter(final Converter<VoucherModel, VoucherData> voucherConverter)
	{
		this.voucherConverter = voucherConverter;
	}

	public Populator<AbstractOrderModel, VoucherData> getAppliedVoucherPopulatorList()
	{
		return appliedVoucherPopulatorList;
	}

	@Required
	public void setAppliedVoucherPopulatorList(final Populator<AbstractOrderModel, VoucherData> appliedVoucherPopulatorList)
	{
		this.appliedVoucherPopulatorList = appliedVoucherPopulatorList;
	}
}
