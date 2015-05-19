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
package de.hybris.platform.b2b.punchout.actions;

import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.cxml.CXML;
import org.cxml.ItemOut;
import org.cxml.PunchOutSetupRequest;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;


/**
 * Handles operation="edit" onto the {@link PunchOutSetupRequest} by populating the session shopping cart.
 */
public class EditOperationPunchOutSetupProcessingAction implements PunchOutProcessingAction<CXML, CXML>
{
	private static final Logger LOG = Logger.getLogger(EditOperationPunchOutSetupProcessingAction.class);

	private Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter;

	private CartService cartService;

	@Override
	public void process(final CXML input, final CXML output)
	{
		final CXMLElementBrowser cxmlElementBrowser = new CXMLElementBrowser(input);
		final PunchOutSetupRequest punchOutSetUpRequest = cxmlElementBrowser.findRequestByType(PunchOutSetupRequest.class);
		// clean old cart and create a new one
		getCartService().removeSessionCart();
		final CartModel cartModel = getCartService().getSessionCart();
		populateShoppingCart(punchOutSetUpRequest.getItemOut(), cartModel);
	}

	/**
	 * Populates a shopping cart using the provided list of {@link ItemOut} elements.
	 * 
	 * @param itemsOut
	 *           all the items out
	 * @param cartModel
	 *           the cart model
	 */
	protected void populateShoppingCart(final List<ItemOut> itemsOut, final CartModel cartModel)
	{
		if (CollectionUtils.isEmpty(itemsOut))
		{
			LOG.debug("No items to fill in cart");
			return;
		}
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<AbstractOrderEntryModel>();

		for (final ItemOut itemOut : itemsOut)
		{
			final AbstractOrderEntryModel orderEntryModel = getItemOutConverter().convert(itemOut);
			orderEntries.add(orderEntryModel);
		}

		cartModel.setEntries(orderEntries);

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

	public Converter<ItemOut, AbstractOrderEntryModel> getItemOutConverter()
	{
		return itemOutConverter;
	}

	@Required
	public void setItemOutConverter(final Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter)
	{
		this.itemOutConverter = itemOutConverter;
	}

}
