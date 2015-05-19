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
package de.hybris.platform.sap.sapordermgmtservices.cart.impl;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.OrderMgmtMessage;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import sap.hybris.integration.models.services.SalesAreaService;


/**
 * Basic cart functions for SAP synchronous order management. In this case, the cart will be created in the back end
 * session, it does not touch the hybris persistence.<br>
 * The class synchronizes accesses to the BOL object representing the cart, as this is not thread safe. Multi-threaded
 * accesses can happen although we use request sequencing, since also filters might call cart facades.
 */
public class DefaultCartService implements CartService
{
	private static final Logger LOG = Logger.getLogger(DefaultCartService.class);

	private BolCartFacade bolCartFacade;

	private Converter<Basket, CartData> cartConverter;
	private Converter<Item, OrderEntryData> cartItemConverter;
	private Converter<Message, CartModificationData> messageConverter;

	SalesAreaService salesAreaService;




	@Override
	public CartData getSessionCart()
	{
		return compileSessionCart(false);
	}

	/**
	 * Creates the session cart from the BOL cart representation, and returns it in hybris representation. In case the
	 * cart is not initialized yet (i.e. if no back end call has taken place), an empty hybris cart is returned.
	 * 
	 * @param recentlyAddedFirst
	 * @return Cart in hybris format
	 */
	protected CartData compileSessionCart(final boolean recentlyAddedFirst)
	{


		final Basket currentCart = bolCartFacade.getCart();
		synchronized (currentCart)
		{

			CartData cartData = null;
			if (currentCart.isInitialized())
			{
				cartData = getCartConverter().convert(currentCart);
			}
			else
			{
				return createEmptyCart();
			}
			final B2BPaymentTypeData paymentType = new B2BPaymentTypeData();
			paymentType.setCode(CheckoutPaymentType.ACCOUNT.getCode());
			cartData.setPaymentType(paymentType);

			cartData.setQuoteAllowed(new Boolean(false));

			if (recentlyAddedFirst)
			{
				this.reverseCartSorting(cartData);
			}

			return cartData;
		}
	}


	/**
	 * Reverse the sorting of the ItemList of the Cart
	 * 
	 * @param cart
	 *           the Cart object
	 */
	protected void reverseCartSorting(final Basket cart)
	{
		Collections.reverse(cart.getItemList());
	}

	/**
	 * Reverse the sorting of the ItemList of the Cart
	 * 
	 * @param cart
	 *           the Cart object
	 */
	protected void reverseCartSorting(final CartData cart)
	{
		Collections.reverse(cart.getEntries());
	}

	/**
	 * Creates an empty cart, just initializing the entry list
	 * 
	 * @return Empty cart
	 */
	protected CartData createEmptyCart()
	{
		final CartData cart = new CartData();
		cart.setEntries(Collections.<OrderEntryData> emptyList());
		return cart;
	}


	@Override
	public boolean hasSessionCart()
	{
		final Basket currentCart = bolCartFacade.getCart();
		synchronized (currentCart)
		{
			return bolCartFacade.hasCart().booleanValue();
		}
	}


	@Override
	public CartModificationData addToCart(final String code, final long quantity)
	{
		final Basket currentCart = bolCartFacade.getCart();
		synchronized (currentCart)
		{

			final Item newItem = bolCartFacade.addToCart(code, quantity);


			final CartModificationData cartModificationData = new CartModificationData();
			cartModificationData.setQuantity(newItem.getQuantity().longValue());
			cartModificationData.setQuantityAdded(quantity);

			final OrderEntryData cartEntryModel = getCartItemConverter().convert(newItem);
			cartEntryModel.setQuantity(new Long(cartModificationData.getQuantityAdded()));
			cartModificationData.setEntry(cartEntryModel);

			//Now do validation and add first message, if existing, to the result
			final List<CartModificationData> cartMessages = validateCartData();

			if (cartMessages.size() > 0)
			{
				final CartModificationData message = cartMessages.get(0);
				cartModificationData.setStatusCode(message.getStatusCode());
				cartModificationData.setStatusMessage(message.getStatusMessage());
			}


			return cartModificationData;
		}

	}




	@Override
	public CartData getSessionCart(final boolean recentlyAddedFirst)
	{
		return compileSessionCart(recentlyAddedFirst);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.cart.CartService#updateCartEntry(long, long)
	 */
	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final long quantityAsLong)
	{
		final Basket currentCart = bolCartFacade.getCart();
		synchronized (currentCart)
		{

			final int number = convertToPositiveInt(entryNumber);
			final BigDecimal quantity = convertQuantity(quantityAsLong);
			final Item itemToUpdate = bolCartFacade.getCartItem(number);
			if (itemToUpdate == null)
			{
				throw new ApplicationBaseRuntimeException("Could not find item for quantity update, number: " + entryNumber);
			}
			final long oldQuantity = itemToUpdate.getQuantity().longValue();
			itemToUpdate.setQuantity(quantity);
			checkForDeletion(quantity, itemToUpdate);

			bolCartFacade.updateCart();


			// Return the modification data
			final CartModificationData modification = new CartModificationData();
			final Item updatedItem = bolCartFacade.getCartItem(number);
			if (updatedItem != null)
			{

				final OrderEntryData cartEntryModel = getCartItemConverter().convert(updatedItem);

				modification.setEntry(cartEntryModel);
			}
			modification.setQuantity(quantity.longValue());
			modification.setQuantityAdded(quantity.longValue() - oldQuantity);

			modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);

			return modification;
		}

	}

	/**
	 * @param entryNumber
	 * @return Input converted into positive int
	 */
	protected int convertToPositiveInt(final long entryNumber)
	{
		final int number = new BigDecimal(entryNumber).intValueExact();
		if (number < 0)
		{
			throw new ApplicationBaseRuntimeException("quantity must not be negative");
		}
		return number;
	}

	/**
	 * Checks if cart item is meant to be deleted, which is indicated by a quantity zero. In this case, the BOL item will
	 * be marked to be deleted
	 * 
	 * @param quantity
	 *           New item quantity, if zero: Item will be deleted
	 * @param itemToUpdate
	 *           BOL item to be updated
	 */
	protected void checkForDeletion(final BigDecimal quantity, final Item itemToUpdate)
	{
		if (quantity == BigDecimal.ZERO)
		{
			itemToUpdate.setProductId("");
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Item will be deleted");
			}
		}
	}

	/**
	 * Converts a quantity into a BigDecimal so that BOL can consume it.
	 * 
	 * @param quantity
	 * @return Quantity as result from the input, if input is positive. Zero otherwise
	 */
	protected BigDecimal convertQuantity(final long quantity)
	{
		BigDecimal qty = BigDecimal.ZERO;
		if (quantity > 0)
		{
			qty = new BigDecimal(quantity);
		}
		return qty;
	}


	@Override
	public List<CartModificationData> validateCartData()
	{
		final List<CartModificationData> modifications = new ArrayList<>();
		final MessageList cartErrors = bolCartFacade.validateCart();
		for (final Message cartError : cartErrors)
		{
			//Don't add messages that are assigned explicitly to checkout
			if (cartError instanceof OrderMgmtMessage)
			{
				final OrderMgmtMessage orderMgmtMessage = (OrderMgmtMessage) cartError;
				if ("CH".equals(orderMgmtMessage.getProcessStep()))
				{
					continue;
				}
			}

			modifications.add(messageConverter.convert(cartError));
		}

		return modifications;
	}



	/**
	 * @return the cartConverter
	 */
	public Converter<Basket, CartData> getCartConverter()
	{
		return cartConverter;
	}

	/**
	 * @param cartConverter
	 *           the cartConverter to set
	 */
	public void setCartConverter(final Converter<Basket, CartData> cartConverter)
	{
		this.cartConverter = cartConverter;
	}

	/**
	 * @return the cartItemConverter
	 */
	public Converter<Item, OrderEntryData> getCartItemConverter()
	{
		return cartItemConverter;
	}

	/**
	 * @param cartItemConverter
	 *           the cartItemConverter to set
	 */
	public void setCartItemConverter(final Converter<Item, OrderEntryData> cartItemConverter)
	{
		this.cartItemConverter = cartItemConverter;
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
	 * @return the messageConverter
	 */
	public Converter<Message, CartModificationData> getMessageConverter()
	{
		return messageConverter;
	}

	/**
	 * @param messageConverter
	 *           the messageConverter to set
	 */
	public void setMessageConverter(final Converter<Message, CartModificationData> messageConverter)
	{
		this.messageConverter = messageConverter;
	}

	@Override
	public void removeSessionCart()
	{
		final Basket currentCart = bolCartFacade.getCart();
		synchronized (currentCart)
		{
			bolCartFacade.releaseCart();
		}

	}

	/**
	 * @return the salesAreaService
	 */
	public SalesAreaService getSalesAreaService()
	{
		return salesAreaService;
	}

	/**
	 * @param salesAreaService
	 *           the salesAreaService to set
	 */
	public void setSalesAreaService(final SalesAreaService salesAreaService)
	{
		this.salesAreaService = salesAreaService;
	}


	@Override
	public String addConfigurationToCart(final ConfigModel configModel)
	{
		final Basket currentCart = bolCartFacade.getCart();
		synchronized (currentCart)
		{
			return bolCartFacade.addConfigurationToCart(configModel);
		}
	}


	@Override
	public void updateConfigurationInCart(final String key, final ConfigModel configModel)
	{
		final Basket currentCart = bolCartFacade.getCart();
		synchronized (currentCart)
		{
			bolCartFacade.updateConfigurationInCart(key, configModel);
		}
	}



	@Override
	public boolean isItemAvailable(final String itemKey)
	{
		final Basket currentCart = bolCartFacade.getCart();
		synchronized (currentCart)
		{
			final Item item = currentCart.getItem(new TechKey(itemKey));
			return item != null;
		}
	}


	@Override
	public void addItemsToCart(final List<Item> items)
	{
		bolCartFacade.addItemsToCart(items);
	}








}
