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
package de.hybris.platform.commercefacades.storefinder.converters;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * PointOfServer converter that also adds the distance data
 */
public class PointOfServiceDistanceConverter extends AbstractPopulatingConverter<PointOfServiceDistanceData, PointOfServiceData>
{
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;

	protected Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	@Required
	public void setPointOfServiceConverter(final Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}

	// This is a special case where we want to delegate target creation to another converter rather than the createTarget method
	@Override
	public PointOfServiceData convert(final PointOfServiceDistanceData source) throws ConversionException
	{
		final PointOfServiceData prototype = getPointOfServiceConverter().convert(source.getPointOfService());
		populate(source, prototype);
		return prototype;
	}

	@Override
	protected PointOfServiceData createTarget()
	{
		throw new IllegalStateException("createTarget not supported by PointOfServiceDistanceConverter");
	}
}
