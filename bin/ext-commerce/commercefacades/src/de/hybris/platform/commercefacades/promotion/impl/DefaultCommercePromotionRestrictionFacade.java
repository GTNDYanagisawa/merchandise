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
package de.hybris.platform.commercefacades.promotion.impl;


import de.hybris.platform.commercefacades.promotion.CommercePromotionRestrictionFacade;
import de.hybris.platform.commerceservices.model.promotions.PromotionOrderRestrictionModel;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionException;
import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionService;
import de.hybris.platform.commerceservices.promotion.CommercePromotionService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultCommercePromotionRestrictionFacade implements CommercePromotionRestrictionFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCommercePromotionRestrictionFacade.class);
	private CommercePromotionRestrictionService commercePromotionRestrictionService;
	private CommercePromotionService commercePromotionService;
	private CommerceCartService commerceCartService;
	private CartService cartService;

	@Override
	public void enablePromotionForCurrentCart(final String promotionCode) throws CommercePromotionRestrictionException
	{
		final AbstractPromotionModel promotion = getCommercePromotionService().getPromotion(promotionCode);
		final PromotionOrderRestrictionModel restriction;

		try
		{
			restriction = getCommercePromotionRestrictionService().getPromotionOrderRestriction(promotion);
		}
		catch (UnknownIdentifierException e)
		{
			throw new CommercePromotionRestrictionException(e.getMessage());
		}

		final CartModel sessionCart = getCartService().getSessionCart();
		getCommercePromotionRestrictionService().addOrderToRestriction(restriction, sessionCart);

		try
		{
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(sessionCart);
			getCommerceCartService().recalculateCart(parameter);
		}
		catch (final CalculationException ex)
		{
			LOG.error("Failed to recalculate order [" + sessionCart.getCode() + "]", ex);
		}
	}

	@Override
	public void disablePromotionForCurrentCart(final String promotionCode) throws CommercePromotionRestrictionException
	{
		final AbstractPromotionModel promotion = getCommercePromotionService().getPromotion(promotionCode);
		final PromotionOrderRestrictionModel restriction;

		try
		{
			restriction = getCommercePromotionRestrictionService().getPromotionOrderRestriction(promotion);
		}
		catch (UnknownIdentifierException e)
		{
			throw new CommercePromotionRestrictionException(e.getMessage());
		}

		final CartModel sessionCart = getCartService().getSessionCart();
		getCommercePromotionRestrictionService().removeOrderFromRestriction(restriction, sessionCart);

		try
		{
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(sessionCart);
			getCommerceCartService().recalculateCart(parameter);
		}
		catch (final CalculationException ex)
		{
			LOG.error("Failed to recalculate order [" + sessionCart.getCode() + "]", ex);
		}
	}

	protected CommercePromotionRestrictionService getCommercePromotionRestrictionService()
	{
		return commercePromotionRestrictionService;
	}

	@Required
	public void setCommercePromotionRestrictionService(
			final CommercePromotionRestrictionService commercePromotionRestrictionService)
	{
		this.commercePromotionRestrictionService = commercePromotionRestrictionService;
	}

	protected CommercePromotionService getCommercePromotionService()
	{
		return commercePromotionService;
	}

	@Required
	public void setCommercePromotionService(final CommercePromotionService commercePromotionService)
	{
		this.commercePromotionService = commercePromotionService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}
}
