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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class ScheduledCartPopulator implements Populator<CartToOrderCronJobModel, ScheduledCartData>
{
	private Converter<CartModel, CartData> cartConverter;
	private Converter<TriggerModel, TriggerData> triggerConverter;

	@Override
	public void populate(final CartToOrderCronJobModel source, final ScheduledCartData prototype) throws ConversionException
	{
		addCartData(source, prototype);
		addTriggerData(source, prototype);

		prototype.setJobCode(source.getCode());
		prototype.setActive(Boolean.TRUE.equals(source.getActive()));
		prototype.setFirstOrderDate(getFirstOrderDate(source, prototype));
	}

	/**
	 * Returns the date of the first order placed, if placed, otherwise get the trigger's next ActivationDate
	 */
	protected Date getFirstOrderDate(final CartToOrderCronJobModel source, final ScheduledCartData prototype)
	{
		Date firstOrder = getFirstOrderDate(source.getOrders());
		if (firstOrder == null)
		{
			firstOrder = prototype.getTriggerData().getActivationTime();
		}
		return firstOrder;
	}

	/**
	 * Returns the date of the first order placed chronologically.
	 */
	protected Date getFirstOrderDate(final Collection<OrderModel> orders)
	{
		Date firstOrderDate = null;

		if (CollectionUtils.isNotEmpty(orders))
		{
			final OrderModel firstOrder = Collections.min(orders, new Comparator<OrderModel>()
			{
				@Override
				public int compare(final OrderModel orderModel1, final OrderModel orderModel2)
				{
					return orderModel1.getDate().compareTo(orderModel2.getDate());
				}
			});
			firstOrderDate = firstOrder.getDate();
		}

		return firstOrderDate;
	}

	protected void addCartData(final CartToOrderCronJobModel source, final ScheduledCartData prototype)
	{
		getCartConverter().convert(source.getCart(), prototype);
	}

	protected void addTriggerData(final CartToOrderCronJobModel source, final ScheduledCartData prototype)
	{
		if (CollectionUtils.isNotEmpty(source.getTriggers()))
		{

			prototype.setTriggerData(getTriggerConverter().convert(source.getTriggers().iterator().next()));
		}
	}

	protected Converter<CartModel, CartData> getCartConverter()
	{
		return cartConverter;
	}

	@Required
	public void setCartConverter(final Converter<CartModel, CartData> cartConverter)
	{
		this.cartConverter = cartConverter;
	}

	protected Converter<TriggerModel, TriggerData> getTriggerConverter()
	{
		return triggerConverter;
	}

	@Required
	public void setTriggerConverter(final Converter<TriggerModel, TriggerData> triggerConverter)
	{
		this.triggerConverter = triggerConverter;
	}
}
