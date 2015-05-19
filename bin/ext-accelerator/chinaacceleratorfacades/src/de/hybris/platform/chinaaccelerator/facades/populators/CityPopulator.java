// CHINAACC_NEWFILE

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
package de.hybris.platform.chinaaccelerator.facades.populators;



import de.hybris.platform.chinaaccelerator.facades.data.CityData;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;


/**
 *
 */
public class CityPopulator implements Populator<CityModel, CityData>
{
	@Override
	public void populate(final CityModel source, final CityData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		//target.setName(source.getName(Locale.ENGLISH)); // 
		target.setName(source.getName());
		target.setCode(source.getCode());

		// from ChinaStoreLocatorFacadeImpl
		target.setCityName(source.getName());
		target.setCityPK(source.getPk().getLong());
		final GeoPoint point = new GeoPoint();
		point.setLatitude(source.getLatitude() != null ? source.getLatitude().doubleValue() : 0d);
		point.setLongitude(source.getLongitude() != null ? source.getLongitude().doubleValue() : 0d);
		target.setGeoPoint(point);
	}
}
