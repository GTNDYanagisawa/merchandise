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

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import org.cxml.CXML;
import org.cxml.ItemOut;
import org.cxml.OrderRequest;
import org.cxml.OrderRequestHeader;
import org.springframework.core.convert.converter.Converter;


/**
 * This implementation of {@link PunchOutProcessingAction} is meant to process the OrderRequest info from the cXML.
 */
public class PopulateCartPurchaseOrderProcessingAction implements PunchOutProcessingAction<CXML, CartModel>
{

	private Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter;

	private Populator<ItemOut, AbstractOrderEntryModel> orderEntryOverridingPopulator;

	private Populator<OrderRequestHeader, CartModel> orderRequestCartPopulator;

	@Override
	public void process(final CXML input, final CartModel output)
	{

		final OrderRequest orderRequest = getOrderRequest(input);
		final OrderRequestHeader orderRequestHeader = orderRequest.getOrderRequestHeader();

		for (final ItemOut itemOut : orderRequest.getItemOut())
		{
			final AbstractOrderEntryModel orderEntryModel = itemOutConverter.convert(itemOut);

			orderEntryOverridingPopulator.populate(itemOut, orderEntryModel);
		}

		// populating the cart at the end so that we are able to override the total price, taxes, etc.
		orderRequestCartPopulator.populate(orderRequestHeader, output);
	}

	/**
	 * Finds an {@link OrderRequest} from the input.
	 * 
	 * @param input
	 *           the {@link CXML} input
	 * @return the {@link OrderRequest} from the input
	 */
	protected OrderRequest getOrderRequest(final CXML input)
	{
		final OrderRequest result = new CXMLElementBrowser(input).findRequestByType(OrderRequest.class);
		if (result == null)
		{
			throw new PunchOutException(PunchOutResponseCode.CONFLICT, "No OrderRequest in the CXML request data");
		}
		return result;
	}

	public Converter<ItemOut, AbstractOrderEntryModel> getItemOutConverter()
	{
		return itemOutConverter;
	}

	public void setItemOutConverter(final Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter)
	{
		this.itemOutConverter = itemOutConverter;
	}

	public Populator<ItemOut, AbstractOrderEntryModel> getOrderEntryOverridingPopulator()
	{
		return orderEntryOverridingPopulator;
	}

	public void setOrderEntryOverridingPopulator(final Populator<ItemOut, AbstractOrderEntryModel> orderEntryOverridingPopulator)
	{
		this.orderEntryOverridingPopulator = orderEntryOverridingPopulator;
	}

	public Populator<OrderRequestHeader, CartModel> getOrderRequestCartPopulator()
	{
		return orderRequestCartPopulator;
	}

	public void setOrderRequestCartPopulator(final Populator<OrderRequestHeader, CartModel> orderRequestCartPopulator)
	{
		this.orderRequestCartPopulator = orderRequestCartPopulator;
	}

}
