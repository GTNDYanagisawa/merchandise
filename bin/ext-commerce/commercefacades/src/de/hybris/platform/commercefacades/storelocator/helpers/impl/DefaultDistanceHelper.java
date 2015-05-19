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
package de.hybris.platform.commercefacades.storelocator.helpers.impl;

import de.hybris.platform.basecommerce.enums.DistanceUnit;
import de.hybris.platform.commercefacades.storelocator.helpers.DistanceHelper;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 */
public class DefaultDistanceHelper implements DistanceHelper
{
	private PointOfServiceService pointOfServiceService;
	private EnumerationService enumerationService;

	public static final double IMPERIAL_DISTANCE_RATIO = 0.62137;


	protected String getDistanceUnit(final DistanceUnit distanceUnit)
	{
		return getEnumerationService().getEnumerationName(distanceUnit);
	}


	protected Double getDistance(final DistanceUnit distanceUnit, final double distanceInKm)
	{
		Double distance = Double.valueOf(distanceInKm);
		if (DistanceUnit.KM.equals(distanceUnit))
		{
			distance = Double.valueOf(distanceInKm);
		}
		else if (DistanceUnit.MILES.equals(distanceUnit))
		{
			distance = Double.valueOf(distanceInKm * IMPERIAL_DISTANCE_RATIO);
		}
		return distance;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commercefacades.storelocator.helpers.DistanceHelper#getDistanceString(de.hybris.platform.store
	 * .BaseStoreModel, double)
	 */
	@Override
	public String getDistanceStringForStore(final BaseStoreModel baseStoreModel, final double distanceInKm)
	{
		String distanceString = "";
		DistanceUnit distanceUnit = DistanceUnit.KM;
		if (baseStoreModel.getStorelocatorDistanceUnit() != null)
		{
			distanceUnit = baseStoreModel.getStorelocatorDistanceUnit();
		}
		final DecimalFormat distanceFormat = new DecimalFormat("###,###.#");
		distanceString = distanceFormat.format(getDistance(distanceUnit, distanceInKm).doubleValue()) + " "
				+ getDistanceUnit(distanceUnit);
		return distanceString;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storelocator.helpers.DistanceHelper#getDistanceString(java.lang.String,
	 * double)
	 */
	@Override
	public String getDistanceStringForLocation(final String locationName, final double distanceInKm)
	{
		final PointOfServiceModel pointOfServiceModel = getPointOfServiceService().getPointOfServiceForName(locationName);
		return getDistanceStringForStore(pointOfServiceModel.getBaseStore(), distanceInKm);
	}



	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
