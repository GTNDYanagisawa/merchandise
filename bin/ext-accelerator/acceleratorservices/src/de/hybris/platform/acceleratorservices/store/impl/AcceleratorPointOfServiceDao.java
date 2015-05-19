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
package de.hybris.platform.acceleratorservices.store.impl;

import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.PointOfServiceDaoException;
import de.hybris.platform.storelocator.impl.DefaultPointOfServiceDao;
import de.hybris.platform.storelocator.impl.GeometryUtils;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;


/**
 * This implementation is designed to cause only PointsOfService with type STORE to be visible on Storefronts.
 */
public class AcceleratorPointOfServiceDao extends DefaultPointOfServiceDao
{
	protected String buildQueryString(final BaseStoreModel baseStore)
	{
		final StringBuilder query = new StringBuilder(163);
		query.append("SELECT {" + PointOfServiceModel.PK + "} FROM {").append(PointOfServiceModel._TYPECODE).append("} WHERE {")
				.append(PointOfServiceModel.LATITUDE).append("} IS NOT null AND {").append(PointOfServiceModel.LONGITUDE)
				.append("} IS NOT null AND {").append(PointOfServiceModel.LATITUDE).append("} >= ?latMin AND {")
				.append(PointOfServiceModel.LATITUDE).append("} <= ?latMax AND {").append(PointOfServiceModel.LONGITUDE)
				.append("} >= ?lonMin AND {").append(PointOfServiceModel.LONGITUDE).append("} <= ?lonMax AND {")
				.append(PointOfServiceModel.TYPE).append("} = ?storeType");

		if (baseStore != null)
		{
			query.append(" AND {").append(PointOfServiceModel.BASESTORE).append("} = ?baseStore");
		}

		return query.toString();
	}

	@Override
	protected FlexibleSearchQuery buildQuery(final GPS center, final double radius, final BaseStoreModel baseStore)
			throws PointOfServiceDaoException
	{
		try
		{
			final List<GPS> corners = GeometryUtils.getSquareOfTolerance(center, radius);
			if (corners == null || corners.isEmpty() || corners.size() != 2)
			{
				throw new PointOfServiceDaoException("Could not fetch locations from database. Unexpected neighborhood");
			}

			final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(buildQueryString(baseStore));
			fQuery.addQueryParameter("latMax", Double.valueOf(corners.get(1).getDecimalLatitude()));
			fQuery.addQueryParameter("latMin", Double.valueOf(corners.get(0).getDecimalLatitude()));
			fQuery.addQueryParameter("lonMax", Double.valueOf(corners.get(1).getDecimalLongitude()));
			fQuery.addQueryParameter("lonMin", Double.valueOf(corners.get(0).getDecimalLongitude()));
			fQuery.addQueryParameter("storeType", PointOfServiceTypeEnum.STORE);
			if (baseStore != null)
			{
				fQuery.addQueryParameter("baseStore", baseStore);
			}

			return fQuery;
		}
		catch (final GeoLocatorException e)
		{
			throw new PointOfServiceDaoException("Could not fetch locations from database, due to :" + e.getMessage(), e);
		}
	}
}
