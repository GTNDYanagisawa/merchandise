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
package de.hybris.platform.commercefacades.order.converters.populator;


import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.DeliveryOrderEntryGroupData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;


/**
 * Updates {@link AbstractOrderData} with order group data. Order entry data must be already populated in
 * {@link AbstractOrderData}.
 * 
 */
public class DeliveryOrderEntryGroupPopulator extends OrderEntryGroupPopulator
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException
	{
		super.populate(source, target);

		if (target.getDeliveryOrderGroups() == null)
		{
			target.setDeliveryOrderGroups(new ArrayList<DeliveryOrderEntryGroupData>());
		}


		for (final AbstractOrderEntryModel entryModel : source.getEntries())
		{
			createUpdateShipGroupData(entryModel, target);
		}
		target.setDeliveryItemsQuantity(Long.valueOf(sumDeliveryItemsQuantity(source)));
	}



	protected void createUpdateShipGroupData(final AbstractOrderEntryModel entryModel, final AbstractOrderData target)
	{
		if (entryModel.getDeliveryPointOfService() == null)
		{
			DeliveryOrderEntryGroupData groupData = null;
			if (CollectionUtils.isNotEmpty(target.getDeliveryOrderGroups()))
			{
				groupData = target.getDeliveryOrderGroups().iterator().next();
			}
			if (groupData == null)
			{
				groupData = new DeliveryOrderEntryGroupData();
				groupData.setEntries(new ArrayList<OrderEntryData>());
				target.getDeliveryOrderGroups().add(groupData);
			}

			updateGroupTotalPriceWithTax(entryModel, groupData);
			groupData.getEntries().add(getOrderEntryData(target, entryModel.getEntryNumber()));
		}
	}

}
