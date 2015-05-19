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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.cxml.ItemIn;
import org.cxml.Money;
import org.cxml.PunchOutOrderMessage;
import org.cxml.PunchOutOrderMessageHeader;
import org.cxml.Total;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populate Converts a Cart into a CMXL PunchOutOrderMessage.
 */
public class DefaultPunchOutOrderMessagePopulator implements Populator<CartModel, PunchOutOrderMessage>
{

	private Converter<AbstractOrderEntryModel, ItemIn> defaultPunchOutOrderEntryPopulator;

	@Override
	public void populate(final CartModel cartModel, final PunchOutOrderMessage punchOutOrder) throws ConversionException
	{
		punchOutOrder.setPunchOutOrderMessageHeader(new PunchOutOrderMessageHeader());
		punchOutOrder.getPunchOutOrderMessageHeader().setTotal(new Total());
		punchOutOrder.getPunchOutOrderMessageHeader().getTotal().setMoney(new Money());
		punchOutOrder.getPunchOutOrderMessageHeader().getTotal().getMoney().setCurrency(cartModel.getCurrency().getIsocode());
		punchOutOrder.getPunchOutOrderMessageHeader().getTotal().getMoney().setvalue(String.valueOf(cartModel.getTotalPrice()));

		for (final AbstractOrderEntryModel orderEntry : cartModel.getEntries())
		{
			final ItemIn itemIn = getPunchOutOrderEntryConverter().convert(orderEntry);
			punchOutOrder.getItemIn().add(itemIn);
		}

	}

	protected Converter<AbstractOrderEntryModel, ItemIn> getPunchOutOrderEntryConverter()
	{
		return defaultPunchOutOrderEntryPopulator;
	}


	@Required
	public void setPunchOutOrderEntryConverter(final Converter<AbstractOrderEntryModel, ItemIn> PunchOutOrderEntryPopulator)
	{
		this.defaultPunchOutOrderEntryPopulator = PunchOutOrderEntryPopulator;
	}
}