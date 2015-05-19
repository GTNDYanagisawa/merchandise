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
package de.hybris.platform.b2bacceleratorfacades.order.impl;


import static de.hybris.platform.util.localization.Localization.getLocalizedString;

import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.exception.DomainException;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.CartService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CartFacade}.
 */
public class DefaultB2BCartFacade extends DefaultCartFacade implements CartFacade
{
	protected static final Logger LOG = Logger.getLogger(DefaultB2BCartFacade.class);

	private ProductFacade b2bProductFacade;
	private static final Integer MINIMUM_SINGLE_SKU_ADD_CART = 0;
	private static final String BASKET_QUANTITY_ERROR_KEY = "basket.error.quantity.invalid";
	private static final String BASKET_QUANTITY_NOITEMADDED_ERROR_PREFIX_KEY = "basket.information.quantity.noItemsAdded.";
	private static final String BASKET_QUANTITY_REDUCED_NUMBER_PREFIX_KEY = "basket.information.quantity.reducedNumberOfItemsAdded.";
	private static final String CART_MODIFICATION_ERROR = "basket.error.occurred";
	public static final String BASKET_QUANTITY_REMOVE_SUCCESS = "basket.page.message.remove";

	private Populator<AbstractOrderModel, List<CartModificationData>> groupCartModificationListPopulator;

	@Override
	public CartModificationData addOrderEntry(final OrderEntryData cartEntry)
	{

		if (!isValidEntry(cartEntry))
		{
			throw new EntityValidationException(BASKET_QUANTITY_ERROR_KEY);
		}
		CartModificationData cartModification = null;
		try
		{
			cartModification = addToCart(cartEntry.getProduct().getCode(), cartEntry.getQuantity());
		}
		catch (final CommerceCartModificationException e)
		{
			throw new DomainException(getLocalizedString(CART_MODIFICATION_ERROR), e);
		}
		setAddStatusMessage(cartEntry, cartModification);
		return cartModification;
	}

	protected boolean isValidEntry(final OrderEntryData cartEntry)
	{
		return (cartEntry.getProduct() != null && cartEntry.getProduct().getCode() != null) && cartEntry.getQuantity() != null
				&& cartEntry.getQuantity() >= MINIMUM_SINGLE_SKU_ADD_CART;
	}

	protected void setAddStatusMessage(final OrderEntryData orderEntry, final CartModificationData cartModification)
	{
		if (cartModification.getQuantityAdded() <= MINIMUM_SINGLE_SKU_ADD_CART)
		{
			cartModification.setStatusMessage(getLocalizedString(
					BASKET_QUANTITY_NOITEMADDED_ERROR_PREFIX_KEY + cartModification.getStatusCode(), new Object[]
					{ cartModification.getEntry().getProduct().getName() }));
		}
		else if (cartModification.getQuantityAdded() < orderEntry.getQuantity())
		{
			cartModification.setStatusMessage(getLocalizedString(
					BASKET_QUANTITY_REDUCED_NUMBER_PREFIX_KEY + cartModification.getStatusCode(), new Object[]
					{ cartModification.getEntry().getProduct().getName() }));
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public CartModificationData updateOrderEntry(final OrderEntryData orderEntry)
	{
		CartModificationData cartModification = null;
		orderEntry.setEntryNumber(getOrderEntryNumber(orderEntry));

		try
		{
			if (orderEntry.getEntryNumber() != null)
			{
				// grouped items
				if (CollectionUtils.isNotEmpty(orderEntry.getEntries()))
				{
					if (orderEntry.getQuantity().intValue() == 0)
					{
						cartModification = deleteGroupedOrderEntries(orderEntry);
					}
				}
				else
				{
					cartModification = super.updateCartEntry(orderEntry.getEntryNumber(), orderEntry.getQuantity());
				}
			}
			else
			{
				cartModification = addOrderEntry(orderEntry);
			}

			setUpdateStatusMessage(orderEntry, cartModification);
		}
		catch (final CommerceCartModificationException e)
		{
			throw new DomainException(getLocalizedString(CART_MODIFICATION_ERROR), e);
		}

		return cartModification;

	}

	protected CartModificationData deleteGroupedOrderEntries(final OrderEntryData orderEntry)
	{
		final List<CartModificationData> modificationDataList = new ArrayList<CartModificationData>();

		for (final OrderEntryData subEntry : orderEntry.getEntries())
		{
			subEntry.setEntryNumber(null);
			subEntry.setQuantity(0L);
			subEntry.setEntryNumber(getOrderEntryNumber(subEntry));

			final CartModificationData cartModificationData = updateOrderEntry(subEntry);
			modificationDataList.add(cartModificationData);
		}

		final List<CartModificationData> listCartModifications = groupCartModificationDataList(modificationDataList);

		if (CollectionUtils.isNotEmpty(listCartModifications))
		{
			return listCartModifications.get(0);
		}

		return null;
	}

	protected void setUpdateStatusMessage(final OrderEntryData orderEntry, final CartModificationData cartModification)
	{
		if (cartModification.getQuantity() == 0)
		{
			cartModification.setStatusMessage(getLocalizedString(BASKET_QUANTITY_REMOVE_SUCCESS));
		}
		else if (cartModification.getQuantity() < orderEntry.getQuantity())
		{
			cartModification.setStatusMessage(getLocalizedString(
					BASKET_QUANTITY_REDUCED_NUMBER_PREFIX_KEY + cartModification.getStatusCode(), new Object[]
					{ cartModification.getEntry().getProduct().getName() }));
		}
	}

	@Override
	public List<CartModificationData> addOrderEntryList(final List<OrderEntryData> cartEntries)
	{

		final List<CartModificationData> modificationDataList = new ArrayList<CartModificationData>();

		for (final OrderEntryData orderEntry : cartEntries)
		{
			if (isValidEntry(orderEntry))
			{
				try
				{
					final CartModificationData cartModificationData = addOrderEntry(orderEntry);
					if (cartModificationData != null)
					{
						modificationDataList.add(cartModificationData);
					}
				}
				catch (final DomainException d)
				{
					LOG.error("Error processing entry", d);
				}
			}
		}

		if (modificationDataList.isEmpty())
		{
			throw new DomainException(getLocalizedString(CART_MODIFICATION_ERROR));
		}


		return groupCartModificationDataList(modificationDataList);
	}

	@Override
	public List<CartModificationData> updateOrderEntryList(final List<OrderEntryData> cartEntries)
	{

		final List<CartModificationData> modificationDataList = new ArrayList<CartModificationData>();

		for (final OrderEntryData orderEntry : cartEntries)
		{
			if (isValidEntry(orderEntry))
			{
				try
				{
					final CartModificationData cartModificationData = updateOrderEntry(orderEntry);
					if (cartModificationData != null)
					{
						modificationDataList.add(cartModificationData);
					}
				}
				catch (final DomainException d)
				{
					LOG.error("Error processing entry", d);
				}
			}
		}

		return groupCartModificationDataList(modificationDataList);
	}

	private List<CartModificationData> groupCartModificationDataList(final List<CartModificationData> ungroupedList)
	{
		groupCartModificationListPopulator.populate(null, ungroupedList);

		return ungroupedList;
	}


	protected Integer getOrderEntryNumber(final OrderEntryData findEntry)
	{

		if (findEntry.getEntryNumber() != null && findEntry.getEntryNumber().intValue() >= 0)
		{
			return findEntry.getEntryNumber();
		}
		else if (findEntry.getProduct() != null && findEntry.getProduct().getCode() != null)
		{
			for (final OrderEntryData orderEntry : getSessionCart().getEntries())
			{
				// find the entry
				if (orderEntry.getProduct().getCode().equals(findEntry.getProduct().getCode()))
				{
					if (CollectionUtils.isNotEmpty(orderEntry.getEntries()))
					{
						findEntry.setEntries(orderEntry.getEntries());
					}
					return orderEntry.getEntryNumber();
				}
				// check sub entries
				else if (orderEntry.getEntries() != null && !orderEntry.getEntries().isEmpty())
				{
					for (final OrderEntryData subEntry : orderEntry.getEntries())
					{
						// find the entry
						if (subEntry.getProduct().getCode().equals(findEntry.getProduct().getCode()))
						{
							return subEntry.getEntryNumber();
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	@Required
	public void setCartService(final CartService cartService)
	{
		if (cartService instanceof B2BCartService)
		{
			super.setCartService(cartService);
		}
		else
		{
			final String msg = "CartService for DefaultB2BCartFacade should be an instance of B2BCartService.";
			LOG.error(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	protected B2BCartService getCartService()
	{
		return (B2BCartService) super.getCartService();
	}

	public ProductFacade getB2bProductFacade()
	{
		return b2bProductFacade;
	}

	@Required
	public void setB2bProductFacade(final ProductFacade b2bProductFacade)
	{
		this.b2bProductFacade = b2bProductFacade;
	}

	protected Populator<AbstractOrderModel, List<CartModificationData>> getGroupCartModificationListPopulator()
	{
		return groupCartModificationListPopulator;
	}

	@Required
	public void setGroupCartModificationListPopulator(
			final Populator<AbstractOrderModel, List<CartModificationData>> groupCartModificationListPopulator)
	{
		this.groupCartModificationListPopulator = groupCartModificationListPopulator;
	}
}
