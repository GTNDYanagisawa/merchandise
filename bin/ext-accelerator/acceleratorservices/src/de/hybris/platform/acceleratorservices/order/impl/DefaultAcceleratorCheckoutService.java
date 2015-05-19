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
package de.hybris.platform.acceleratorservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.order.AcceleratorCheckoutService;
import de.hybris.platform.acceleratorservices.store.pickup.PickupPointOfServiceConsolidationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Required;


/**
 * Accelerator specific implementation of {@link DefaultCommerceCheckoutService}
 */
public class DefaultAcceleratorCheckoutService implements AcceleratorCheckoutService
{
	private PickupPointOfServiceConsolidationStrategy pickupPointOfServiceConsolidationStrategy;
	private CartService cartService;
	private CommerceCartService commerceCartService;

	@Override
	public List<PointOfServiceDistanceData> getConsolidatedPickupOptions(final CartModel cartModel)
	{
		validateParameterNotNull(cartModel, "CartModel cannot be null");

		return getPickupPointOfServiceConsolidationStrategy().getConsolidationOptions(cartModel);
	}

	@Override
	public List<CommerceCartModification> consolidateCheckoutCart(final CartModel cartModel,
			final PointOfServiceModel consolidatedPickupPointModel) throws CommerceCartModificationException
	{
		validateParameterNotNull(cartModel, "CartModel cannot be null");
		validateParameterNotNull(consolidatedPickupPointModel, "PointOfServiceModel cannot be null");

		final Map<ProductModel, Long> consolidatedEntries = new HashMap<ProductModel, Long>();

		for (final AbstractOrderEntryModel entry : cartModel.getEntries())
		{
			if (entry.getDeliveryPointOfService() != null)
			{
				if (!consolidatedEntries.containsKey(entry.getProduct()))
				{
					consolidatedEntries.put(entry.getProduct(), Long.valueOf(calculateCartLevel(entry.getProduct(), cartModel)));
				}

				final CommerceCartParameter parameter = new CommerceCartParameter();
				parameter.setEnableHooks(true);
				parameter.setCart(cartModel);
				parameter.setEntryNumber(entry.getEntryNumber().longValue());
				parameter.setQuantity(0);

				getCommerceCartService().updateQuantityForCartEntry(parameter);
			}
		}

		final List<CommerceCartModification> unsuccessfulModifications = new ArrayList<CommerceCartModification>();
		for (final Entry<ProductModel, Long> entry : consolidatedEntries.entrySet())
		{

			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setProduct(entry.getKey());
			parameter.setPointOfService(consolidatedPickupPointModel);
			parameter.setQuantity(entry.getValue().longValue());
			parameter.setUnit(null);
			parameter.setCreateNewEntry(true);

			final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);
			if (!CommerceCartModificationStatus.SUCCESS.equals(modification.getStatusCode()))
			{
				unsuccessfulModifications.add(modification);
			}
		}

		return unsuccessfulModifications;
	}

	protected long calculateCartLevel(final ProductModel productModel, final CartModel cartModel)
	{
		long cartLevel = 0;
		for (final CartEntryModel entryModel : getCartService().getEntriesForProduct(cartModel, productModel))
		{
			if (entryModel.getDeliveryPointOfService() != null)
			{
				cartLevel += entryModel.getQuantity() != null ? entryModel.getQuantity().longValue() : 0;
			}
		}
		return cartLevel;
	}

	protected PickupPointOfServiceConsolidationStrategy getPickupPointOfServiceConsolidationStrategy()
	{
		return pickupPointOfServiceConsolidationStrategy;
	}

	@Required
	public void setPickupPointOfServiceConsolidationStrategy(
			final PickupPointOfServiceConsolidationStrategy pickupPointOfServiceConsolidationStrategy)
	{
		this.pickupPointOfServiceConsolidationStrategy = pickupPointOfServiceConsolidationStrategy;
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
