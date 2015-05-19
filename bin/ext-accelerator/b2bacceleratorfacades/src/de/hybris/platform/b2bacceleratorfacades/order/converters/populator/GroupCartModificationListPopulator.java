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
package de.hybris.platform.b2bacceleratorfacades.order.converters.populator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Groups multiple {@link de.hybris.platform.commercefacades.order.data.CartModificationData} based on the way it's
 * entries should be grouped.
 */
public class GroupCartModificationListPopulator implements Populator<AbstractOrderModel, List<CartModificationData>>
{
	public static final String MESSAGE_SEPARATOR = " - ";
	private static final String SUCCESSFUL_MODIFICATION_CODE = "success";
	private Populator<AbstractOrderModel, AbstractOrderData> groupOrderEntryPopulator;

	@Override
	public void populate(final AbstractOrderModel abstractOrderModel, final List<CartModificationData> cartModificationDataList)
			throws ConversionException
	{
		final List<CartModificationData> groupedList = groupCartModificationDataList(cartModificationDataList);
		cartModificationDataList.clear();
        cartModificationDataList.addAll(groupedList);
	}

	protected List<CartModificationData> groupCartModificationDataList(final List<CartModificationData> modificationDataList)
	{

		List<CartModificationData> groupedModificationList = new ArrayList<>();
		final List<OrderEntryData> groupedEntries = getGroupedOrderEntries(modificationDataList);

		if (groupedEntries.size() < modificationDataList.size())
		{
			for (CartModificationData modification : modificationDataList)
			{
				final OrderEntryData groupedEntry = getGroupedEntry(groupedEntries, modification.getEntry());
				if (groupedEntry == null)
				{
					groupedModificationList.add(modification);
				}
				else
				{
					CartModificationData groupedModification = getGroupedCartModificationData(groupedModificationList, groupedEntry);
					updateGroupedModificationValues(groupedModification, modification);
				}
			}
		}
		else
		{
			//No grouped entries
			groupedModificationList = modificationDataList;
		}

		return groupedModificationList;

	}

	protected List<OrderEntryData> getGroupedOrderEntries(final List<CartModificationData> modificationDataList)
	{
		final AbstractOrderData holder = new AbstractOrderData();
		holder.setEntries(new ArrayList<OrderEntryData>());

		for (CartModificationData modification : modificationDataList)
		{
			holder.getEntries().add(modification.getEntry());
		}

		getGroupOrderEntryPopulator().populate(null, holder);
		return holder.getEntries();
	}

	protected void updateGroupedModificationValues(final CartModificationData existing, final CartModificationData modification)
	{
		final long quantity = existing.getQuantity() + modification.getQuantity();
		existing.setQuantity(quantity);
		final long quantityAdded = existing.getQuantityAdded() + modification.getQuantityAdded();
		existing.setQuantityAdded(quantityAdded);

		// The status will be different than success if one modification is not successful.
		if (!SUCCESSFUL_MODIFICATION_CODE.equals(modification.getStatusCode()) && SUCCESSFUL_MODIFICATION_CODE.equals(existing.getStatusCode()))
		{
			existing.setStatusCode(modification.getStatusCode());
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (existing.getStatusMessage() != null)
		{
			stringBuilder.append(existing.getStatusMessage());
        }

        if (modification.getStatusMessage() != null)
        {
            stringBuilder.append(MESSAGE_SEPARATOR).append(modification.getStatusMessage()).toString();
            existing.setStatusMessage(stringBuilder.toString());
        }

	}


	protected CartModificationData getGroupedCartModificationData(final List<CartModificationData> groupedModificationList,
			final OrderEntryData groupedEntry)
	{
		CartModificationData existing = null;

		for (CartModificationData groupedModification : groupedModificationList)
		{
			if (groupedModification.getEntry().equals(groupedEntry))
			{
				existing = groupedModification;
			}
		}

		if (existing == null)
		{
			existing = new CartModificationData();
			existing.setQuantity(0L);
			existing.setQuantityAdded(0L);
			existing.setEntry(groupedEntry);
			existing.setStatusCode(SUCCESSFUL_MODIFICATION_CODE);
			groupedModificationList.add(existing);
		}
		return existing;
	}

	private OrderEntryData getGroupedEntry(final List<OrderEntryData> groupedEntries, final OrderEntryData entryToFind)
	{
		for (OrderEntryData groupedEntry : groupedEntries)
		{
			if (groupedEntry.getEntries() != null)
			{
				for (OrderEntryData entry : groupedEntry.getEntries())
				{
					if (entry.equals(entryToFind))
					{
						return groupedEntry;
					}
				}
			}
		}
		return null;
	}


	protected Populator<AbstractOrderModel, AbstractOrderData> getGroupOrderEntryPopulator()
	{
		return groupOrderEntryPopulator;
	}

	@Required
	public void setGroupOrderEntryPopulator(final Populator<AbstractOrderModel, AbstractOrderData> groupOrderEntryPopulator)
	{
		this.groupOrderEntryPopulator = groupOrderEntryPopulator;
	}

}
