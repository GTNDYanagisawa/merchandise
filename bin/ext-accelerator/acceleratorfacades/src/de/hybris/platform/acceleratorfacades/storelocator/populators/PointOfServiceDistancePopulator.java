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
package de.hybris.platform.acceleratorfacades.storelocator.populators;

import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.storefinder.helpers.DistanceHelper;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populate distanceKm and formattedDistance attributes for
 * {@link de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData} base on
 * {@link de.hybris.platform.storelocator.model.PointOfServiceModel}
 */
public class PointOfServiceDistancePopulator implements Populator<PointOfServiceModel, PointOfServiceData>
{
	private CustomerLocationService customerLocationService;
	private DistanceHelper distanceHelper;


	@Override
	public void populate(final PointOfServiceModel source, final PointOfServiceData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (getCustomerLocationService().getUserLocation() != null)
		{
			target.setDistanceKm(Double.valueOf(getCustomerLocationService().calculateDistance(
					getCustomerLocationService().getUserLocation().getPoint(), source)));
			calculateFormattedDistance(source, target);
		}
	}

	protected void calculateFormattedDistance(final PointOfServiceModel pointOfServiceModel,
			final PointOfServiceData pointOfServiceData)
	{
		final String formattedDistance = getDistanceHelper().getDistanceStringForStore(pointOfServiceModel.getBaseStore(),
				pointOfServiceData.getDistanceKm().doubleValue());
		pointOfServiceData.setFormattedDistance(formattedDistance);
	}

	protected CustomerLocationService getCustomerLocationService()
	{
		return customerLocationService;
	}

	@Required
	public void setCustomerLocationService(final CustomerLocationService customerLocationService)
	{
		this.customerLocationService = customerLocationService;
	}

	protected DistanceHelper getDistanceHelper()
	{
		return distanceHelper;
	}

	@Required
	public void setDistanceHelper(final DistanceHelper distanceHelper)
	{
		this.distanceHelper = distanceHelper;
	}
}
