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
package de.hybris.platform.commercefacades.voucher.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherModel;


public class DefaultVoucherFacade implements VoucherFacade
{
	private VoucherService voucherService;
	private VoucherModelService voucherModelService;
	private CartService cartService;
	private Converter<VoucherModel, VoucherData> voucherConverter;

	@Override
	public boolean checkVoucherCode(final String voucherCode)
	{
		if (StringUtils.isBlank(voucherCode))
		{
			return false;
		}
		final VoucherModel voucher = getVoucherService().getVoucher(voucherCode);
		if (voucher == null)
		{
			return false;
		}
		return checkVoucherCanBeRedeemed(voucher, voucherCode);
	}

	@Override
	public VoucherData getVoucher(final String voucherCode) throws VoucherOperationException
	{
		validateVoucherCodeParameter(voucherCode);
		return getVoucherConverter().convert(getVoucherModel(voucherCode));
	}

	protected void validateVoucherCodeParameter(final String voucherCode)
	{
		if (StringUtils.isBlank(voucherCode))
		{
			throw new IllegalArgumentException("Parameter voucherCode must not be empty");
		}
	}

	protected boolean isVoucherCodeValid(final String voucherCode)
	{
		final VoucherModel voucher = getVoucherService().getVoucher(voucherCode);
		if (voucher == null)
		{
			return false;
		}
		return true;
	}

	protected boolean checkVoucherCanBeRedeemed(final VoucherModel voucher, final String voucherCode)
	{
		return getVoucherModelService().isApplicable(voucher, getCartService().getSessionCart())
				&& getVoucherModelService().isReservable(voucher, voucherCode, getCartService().getSessionCart());
	}

	@Override
	public void applyVoucher(final String voucherCode) throws VoucherOperationException
	{
		validateVoucherCodeParameter(voucherCode);
		if (!isVoucherCodeValid(voucherCode))
		{
			throw new VoucherOperationException("Voucher not found: " + voucherCode);
		}

		final CartModel cartModel = getCartService().getSessionCart();
		final VoucherModel voucher = getVoucherModel(voucherCode);
		if (!checkVoucherCanBeRedeemed(voucher, voucherCode))
		{
			throw new VoucherOperationException("Voucher cannot be redeemed: " + voucherCode);
		}
		else
		{
			try
			{
				if (!getVoucherService().redeemVoucher(voucherCode, cartModel))
				{
					throw new VoucherOperationException("Error while applying voucher: " + voucherCode);
				}
				//Important! Checking cart, if total amount <0, release this voucher
				checkCartAfterApply(voucherCode, voucher);
				return;
			}
			catch (final JaloPriceFactoryException e)
			{
				throw new VoucherOperationException("Error while applying voucher: " + voucherCode);
			}
		}
	}

	@Override
	public void releaseVoucher(final String voucherCode) throws VoucherOperationException
	{
		validateVoucherCodeParameter(voucherCode);
		final CartModel cartModel = getCartService().getSessionCart();
		final VoucherModel voucher = getVoucherModel(voucherCode);
		if (voucher != null && cartModel != null)
		{
			try
			{
				getVoucherService().releaseVoucher(voucherCode, cartModel);
				return;
			}
			catch (final JaloPriceFactoryException e)
			{
				throw new VoucherOperationException("Couldn't release voucher: " + voucherCode);
			}
		}
	}

	@Override
	public List<VoucherData> getVouchersForCart()
	{
		final List<VoucherData> vouchersData = new ArrayList<VoucherData>();
		final CartModel cartModel = getCartService().getSessionCart();
		if (cartModel != null)
		{
			final Collection<String> voucherCodes = getVoucherService().getAppliedVoucherCodes(cartModel);
			for (final String code : voucherCodes)
			{
				try
				{
					vouchersData.add(getSingleVouchersByCode(code));
				}
				catch (VoucherOperationException e)
				{
					// nothing
				}
			}
			return vouchersData;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * Voucher cannot be redeemed Getting single voucher
	 * 
	 * @param voucherCode
	 * @return VoucherData
	 */

	protected VoucherData getSingleVouchersByCode(final String voucherCode) throws VoucherOperationException
	{
		final VoucherModel voucherModel = getVoucherModel(voucherCode);
		final VoucherData voucherData = getVoucherConverter().convert(voucherModel);
		if (voucherCode.length() > 3)
		{
			//Serial voucher
			voucherData.setVoucherCode(voucherCode);
		}
		return voucherData;
	}

	protected VoucherModel getVoucherModel(final String voucherCode) throws VoucherOperationException
	{
		final VoucherModel voucher = getVoucherService().getVoucher(voucherCode);
		if (voucher == null)
		{
			throw new VoucherOperationException("Voucher not found: " + voucherCode);
		}
		return voucher;
	}

	/**
	 * Checking state of cart after redeem last voucher
	 * 
	 * @param lastVoucherCode
	 */
	protected void checkCartAfterApply(final String lastVoucherCode, final VoucherModel lastVoucher)
			throws VoucherOperationException
	{
		final CartModel cartModel = getCartService().getSessionCart();
		//Total amount in cart updated with delay... Calculating value of voucher regarding to order
		final double cartTotal = cartModel.getTotalPrice().doubleValue();
		final double voucherValue = lastVoucher.getValue().doubleValue();
		final double voucherCalcValue = (lastVoucher.getAbsolute().equals(Boolean.TRUE)) ? voucherValue
				: (cartTotal * voucherValue) / 100;

		if (cartModel.getTotalPrice().doubleValue() - voucherCalcValue < 0)
		{
			releaseVoucher(lastVoucherCode);
			//Throw exception with specific information
			throw new VoucherOperationException("Voucher " + lastVoucherCode + " cannot be redeemed: total price exceeded");
		}
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

	public VoucherModelService getVoucherModelService()
	{
		return voucherModelService;
	}

	@Required
	public void setVoucherModelService(final VoucherModelService voucherModelService)
	{
		this.voucherModelService = voucherModelService;
	}

	public CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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
}
