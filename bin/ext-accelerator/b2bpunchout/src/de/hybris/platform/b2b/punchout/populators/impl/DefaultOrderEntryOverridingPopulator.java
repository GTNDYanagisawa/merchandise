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

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.cxml.ItemDetail;
import org.cxml.ItemOut;
import org.cxml.Tax;
import org.springframework.beans.factory.annotation.Required;


/**
 * This populator overrides certain properties on the given {@link AbstractOrderEntryModel}.
 */
public class DefaultOrderEntryOverridingPopulator implements Populator<ItemOut, AbstractOrderEntryModel>
{

	private static final Logger LOG = Logger.getLogger(DefaultOrderEntryOverridingPopulator.class);

	private CommerceCartService commerceCartService;

	private CartFacade cartFacade;

	private Populator<Tax, Collection<TaxValue>> taxValuePopulator;

	@Override
	public void populate(final ItemOut itemOut, final AbstractOrderEntryModel orderEntryModel)
	{
		final ItemDetail itemDetail = (ItemDetail) itemOut.getItemDetailOrBlanketItemDetail().iterator().next();

		LOG.debug(String.format("Overriding price and tax details for entry number: %s with base price: %s and total price: %s",
				orderEntryModel.getEntryNumber(), orderEntryModel.getBasePrice(), orderEntryModel.getTotalPrice()));

		final Double basePrice = Double.valueOf(itemDetail.getUnitPrice().getMoney().getvalue());
		final Double quantity = Double.valueOf(itemOut.getQuantity());
		orderEntryModel.setBasePrice(basePrice);
		orderEntryModel.setTotalPrice(basePrice * quantity);
		taxValuePopulator.populate(itemOut.getTax(), orderEntryModel.getTaxValues());

		LOG.debug(String.format("Overriding complete for entry number: %s with new base price: %s and new total price: %s",
				orderEntryModel.getEntryNumber(), orderEntryModel.getBasePrice(), orderEntryModel.getTotalPrice()));
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

	public CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	public CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}
}
