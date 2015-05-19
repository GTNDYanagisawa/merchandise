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
package de.hybris.platform.commercefacades.storefinder.converters.populator;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.helpers.DistanceHelper;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;


/**
 */
public class PointOfServiceDistanceDataPopulator implements Populator<PointOfServiceDistanceData, PointOfServiceData>
{
	private DistanceHelper distanceHelper;

	protected DistanceHelper getDistanceHelper()
	{
		return distanceHelper;
	}

	@Required
	public void setDistanceHelper(final DistanceHelper distanceHelper)
	{
		this.distanceHelper = distanceHelper;
	}

	@Override
	public void populate(final PointOfServiceDistanceData source, final PointOfServiceData target) throws ConversionException
	{
		if (source != null)
		{
			final String formattedDistance = getDistanceHelper().getDistanceStringForStore(
					source.getPointOfService().getBaseStore(), source.getDistanceKm());
			target.setFormattedDistance(formattedDistance);
		}
	}
}
