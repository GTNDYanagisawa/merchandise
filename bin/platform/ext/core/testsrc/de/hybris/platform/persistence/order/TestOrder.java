/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.persistence.order;

import de.hybris.platform.jalo.order.Order;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.util.PriceValue;

import java.util.Collections;
import java.util.List;


public class TestOrder extends Order
{

	@Override
	protected ComposedType getCustomEntryType()
	{
		return getSession().getTypeManager().getComposedType(TestOrderEntry.class);
	}

	@Override
	protected PriceValue findDeliveryCosts() throws JaloPriceFactoryException
	{
		return null;
	}

	@Override
	protected List findGlobalDiscounts() throws JaloPriceFactoryException
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	protected PriceValue findPaymentCosts() throws JaloPriceFactoryException
	{
		return null;
	}
}
