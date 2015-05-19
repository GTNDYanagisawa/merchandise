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

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.cxml.ItemOut;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;


/**
 * Converts an {@link ItemOut} into an {@link AbstractOrderEntryModel}.
 */
public class DefaultItemOutConverter implements Converter<ItemOut, AbstractOrderEntryModel>
{

	private CommerceCartService commerceCartService;

	private CartService cartService;

	private ProductService productService;


	@Override
	public AbstractOrderEntryModel convert(final ItemOut source) throws ConversionException
	{
		final String supplierPartID = source.getItemID().getSupplierPartID();
		final Long quantity = Double.valueOf(source.getQuantity()).longValue();

		try
		{
			final CartModel cartModel = cartService.getSessionCart();
			final ProductModel productModel = productService.getProductForCode(supplierPartID);
			final UnitModel unit = productModel.getUnit();
			final CommerceCartModification result = commerceCartService.addToCart(cartModel, productModel, quantity.longValue(),
					unit, true);
			return result.getEntry();
		}
		catch (final CommerceCartModificationException e)
		{
			throw new PunchOutException(PunchOutResponseCode.CONFLICT, String.format(
					"Cannot add product with code: %s and quantity: %s to cart", supplierPartID, quantity));
		}
	}


	public CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
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

	public ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

}
