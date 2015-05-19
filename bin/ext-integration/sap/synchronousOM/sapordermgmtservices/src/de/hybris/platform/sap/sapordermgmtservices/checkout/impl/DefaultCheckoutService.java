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
package de.hybris.platform.sap.sapordermgmtservices.checkout.impl;

import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolOrderFacade;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartRestorationService;
import de.hybris.platform.sap.sapordermgmtservices.checkout.CheckoutService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Default service implementation for checkout with SAP Synchronous Order Management. <br>
 * The class synchronizes accesses to the BOL object representing the cart, as this is not thread safe. Multi-threaded
 * accesses can happen although we use request sequencing, since also filters might call cart facades.
 */
public class DefaultCheckoutService implements CheckoutService
{
	private BolCartFacade bolCartFacade;
	private BolOrderFacade bolOrderFacade;
	private Converter<Order, OrderData> orderConverter;
	private Converter<Entry<String, String>, DeliveryModeData> deliveryModeConverter;
	private CartRestorationService cartRestorationService;

	/**
	 * @return The converter for mapping the BOL representation of the delivery mode into the format needed for facade
	 *         layer
	 */
	public Converter<Entry<String, String>, DeliveryModeData> getDeliveryModeConverter()
	{
		return deliveryModeConverter;
	}

	/**
	 * @param deliveryModeConverter
	 *           The converter for mapping the BOL representation of the delivery mode into the format needed for the
	 *           facade layer
	 */
	public void setDeliveryModeConverter(final Converter<Entry<String, String>, DeliveryModeData> deliveryModeConverter)
	{
		this.deliveryModeConverter = deliveryModeConverter;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtservices.checkout.CheckoutService#placeOrder(de.hybris.platform.core.model.
	 * order.CartModel )
	 */
	@Override
	public OrderData placeOrder()
	{

		final Basket sapCart = getBolCartFacade().getCart();

		synchronized (sapCart)
		{

			final Order sapOrder = getBolCartFacade().placeOrderFromCart(sapCart);

			final OrderData orderData = getOrderConverter().convert(sapOrder);

			//reset search cache
			getBolOrderFacade().setSearchDirty();

			//reset cart
			getBolCartFacade().releaseCart();

			//reset hybris cart that is used for cross session cart restoration
			//initialize also hybris cart that is used for cross session cart restoration 
			if (cartRestorationService != null)
			{
				cartRestorationService.removeInternalSessionCart();
			}

			return orderData;
		}
	}

	/**
	 * @return The converter we need to map the BOL representation of an order into the format needed in the facade layer
	 */
	public Converter<Order, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	/**
	 * @param orderConverter
	 *           The converter we need to map the BOL representation of an order into the format needed in the facade
	 *           layer
	 */
	public void setOrderConverter(final Converter<Order, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}


	@Override
	public List<DeliveryModeData> getSupportedDeliveryModes()
	{
		final Map<String, String> allowedDeliveryTypes = getBolCartFacade().getAllowedDeliveryTypes();
		final List<DeliveryModeData> result = new ArrayList<>();

		//first add current one if existing
		final String currentCode = getCurrentDeliveryMode();
		if (currentCode != null)
		{
			result.add(getDeliveryModeConverter().convert(getEntry(currentCode, allowedDeliveryTypes)));
		}


		final Iterator<Entry<String, String>> iterator = allowedDeliveryTypes.entrySet().iterator();
		while (iterator.hasNext())
		{

			//now add the other ones
			final Entry<String, String> next = iterator.next();
			if (!next.getKey().equals(currentCode))
			{
				result.add(getDeliveryModeConverter().convert(next));
			}
		}

		return result;
	}


	@Override
	public boolean setDeliveryMode(final String deliveryModeCode)
	{
		final Basket cart = getBolCartFacade().getCart();
		synchronized (cart)
		{
			cart.getHeader().setShipCond(deliveryModeCode);
			getBolCartFacade().updateCart();
			return true;
		}
	}


	/**
	 * @return Current delivery mode from BOL header object
	 */
	public String getCurrentDeliveryMode()
	{
		final Basket cart = getBolCartFacade().getCart();
		return cart.getHeader().getShipCond();
	}

	Entry<String, String> getEntry(final String key, final Map<String, String> map)
	{
		Entry<String, String> result = null;
		final Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext())
		{
			final Entry<String, String> next = iterator.next();
			if (next.getKey().equals(key))
			{
				result = next;
				break;
			}
		}
		return result;
	}


	@Override
	public boolean setPurchaseOrderNumber(final String purchaseOrderNumber)
	{
		final Basket cart = getBolCartFacade().getCart();
		synchronized (cart)
		{
			cart.getHeader().setPurchaseOrderExt(purchaseOrderNumber);
			getBolCartFacade().updateCart();
			return true;
		}

	}

	/**
	 * @return the bolCartFacade
	 */
	public BolCartFacade getBolCartFacade()
	{
		return bolCartFacade;
	}

	/**
	 * @param bolCartFacade
	 *           the bolCartFacade to set
	 */
	public void setBolCartFacade(final BolCartFacade bolCartFacade)
	{
		this.bolCartFacade = bolCartFacade;
	}

	/**
	 * @return the bolOrderFacade
	 */
	public BolOrderFacade getBolOrderFacade()
	{
		return bolOrderFacade;
	}

	/**
	 * @param bolOrderFacade
	 *           the bolOrderFacade to set
	 */
	public void setBolOrderFacade(final BolOrderFacade bolOrderFacade)
	{
		this.bolOrderFacade = bolOrderFacade;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.checkout.CheckoutService#setDeliveryAddress(java.lang.String)
	 */
	@Override
	public boolean setDeliveryAddress(final String sapCustomerId)
	{
		final Basket cart = getBolCartFacade().getCart();
		synchronized (cart)
		{
			cart.getHeader().getShipTo().setId(sapCustomerId);
			getBolCartFacade().updateCart();
			return true;
		}
	}

	/**
	 * @param cartRestorationService
	 *           the cartRestorationService to set
	 */
	public void setCartRestorationService(final CartRestorationService cartRestorationService)
	{
		this.cartRestorationService = cartRestorationService;
	}

}
