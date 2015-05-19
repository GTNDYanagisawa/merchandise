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
package de.hybris.platform.acceleratorservices.search.comparators;

import de.hybris.platform.acceleratorservices.store.LocalStorePreferencesService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class PointOfServiceDistanceComparator extends AbstractComparator<String>
{
	private PointOfServiceService pointOfServiceService;
	private LocalStorePreferencesService localStorePreferencesService;

	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

	protected LocalStorePreferencesService getLocalStorePreferencesService()
	{
		return localStorePreferencesService;
	}

	@Required
	public void setLocalStorePreferencesService(final LocalStorePreferencesService localStorePreferencesService)
	{
		this.localStorePreferencesService = localStorePreferencesService;
	}

	@Override
	protected int compareInstances(final String value1, final String value2)
	{
		if (getLocalStorePreferencesService() != null)
		{
			final List<PointOfServiceDistanceData> locations = getLocalStorePreferencesService().getAllPointsOfService();
			if (locations != null && !locations.isEmpty())
			{
				PointOfServiceDistanceData result1 = null;
				PointOfServiceDistanceData result2 = null;
				for (final PointOfServiceDistanceData location : locations)
				{
					if (location != null && location.getPointOfService() != null)
					{
						if (value1.equals(location.getPointOfService().getName()))
						{
							result1 = location;
						}
						if (value2.equals(location.getPointOfService().getName()))
						{
							result2 = location;
						}
					}
				}
				if (result1 != null && result2 != null)
				{
					int result = compareValues(result1.getDistanceKm(), result2.getDistanceKm());
					if (EQUAL == result)
					{
						result = compareValues(value1, value2, false);
					}
					return result;
				}
			}
		}
		return compareValues(value1, value2, false);
	}
}
