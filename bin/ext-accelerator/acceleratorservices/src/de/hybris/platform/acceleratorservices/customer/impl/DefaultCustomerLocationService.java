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
package de.hybris.platform.acceleratorservices.customer.impl;

import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.impl.GeometryUtils;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultCustomerLocationService implements CustomerLocationService
{
	private static final Logger LOG = Logger.getLogger(DefaultCustomerLocationService.class);

	public final static String SESSION_USER_LOCATION_KEY = "userLocation";
	public final static String SESSION_LOCATIONS_CACHE_KEY = "localCoordinatesCache";
	public final static String SESSION_NEAREST_STORES_KEY = "userNearestStores";

	private BaseStoreService baseStoreService;
	private GeoWebServiceWrapper geoWebServiceWrapper;
	private SessionService sessionService;

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected GeoWebServiceWrapper getGeoWebServiceWrapper()
	{
		return geoWebServiceWrapper;
	}

	@Required
	public void setGeoWebServiceWrapper(final GeoWebServiceWrapper geoWebServiceWrapper)
	{
		this.geoWebServiceWrapper = geoWebServiceWrapper;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.store.LocalStorePreferencesService#getUserStoredLocation()
	 */
	@Override
	public UserLocationData getUserLocation()
	{
		return (UserLocationData) getSessionService().getAttribute(SESSION_USER_LOCATION_KEY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.acceleratorservices.store.LocalStorePreferencesService#setUserLocationForSearchTerm(java.lang
	 * .String, de.hybris.platform.storelocator.GPS)
	 */
	@Override
	public void setUserLocation(final UserLocationData userLocationData)
	{
		final GeoPoint geoPoint = userLocationData.getPoint();
		if (geoPoint == null)
		{
			userLocationData.setPoint(getLocationForSearchTerm(userLocationData.getSearchTerm()));
		}
		getSessionService().setAttribute(SESSION_NEAREST_STORES_KEY, null);
		getSessionService().setAttribute(SESSION_USER_LOCATION_KEY, userLocationData);
	}



	@Override
	public double calculateDistance(final GeoPoint origin, final PointOfServiceModel pointOfServiceModel)
	{
		if (origin != null && pointOfServiceModel.getLatitude() != null && pointOfServiceModel.getLongitude() != null)
		{
			final GPS positionGPS = new DefaultGPS(pointOfServiceModel.getLatitude().doubleValue(), pointOfServiceModel
					.getLongitude().doubleValue());
			final GPS referenceGps = new DefaultGPS(origin.getLatitude(), origin.getLongitude());
			return GeometryUtils.getElipticalDistanceKM(referenceGps, positionGPS);
		}
		throw new LocationServiceException("Unable to calculate a distance for PointOfService(" + pointOfServiceModel
				+ ") due to missing  latitude, longitude value");
	}



	protected GeoPoint getLocationForSearchTerm(final String searchTerm)
	{
		GeoPoint geoPoint = getCachedGPS(searchTerm);
		if (geoPoint == null)
		{
			final GPS gps = getGeoWebServiceWrapper().geocodeAddress(
					generateGeoAddressForSearchQuery(getBaseStoreService().getCurrentBaseStore(), searchTerm));
			geoPoint = new GeoPoint();
			geoPoint.setLatitude(gps.getDecimalLatitude());
			geoPoint.setLongitude(gps.getDecimalLongitude());
			saveGPSToCache(geoPoint, searchTerm);
		}
		return geoPoint;
	}

	protected GeoPoint getCachedGPS(final String locationName)
	{
		final Map<String, GeoPoint> locationsCache = (Map<String, GeoPoint>) getSessionService().getAttribute(
				SESSION_LOCATIONS_CACHE_KEY);
		if (MapUtils.isNotEmpty(locationsCache) && locationsCache.containsKey(locationName))
		{
			return locationsCache.get(locationName);
		}
		return null;
	}

	protected void saveGPSToCache(final GeoPoint gps, final String locationName)
	{
		final Map<String, GeoPoint> sessionLocationsCache = (Map<String, GeoPoint>) getSessionService().getAttribute(
				SESSION_LOCATIONS_CACHE_KEY);

		final Map<String, GeoPoint> locationsCache = new HashMap<String, GeoPoint>();

		if (!MapUtils.isEmpty(sessionLocationsCache))
		{
			locationsCache.putAll(sessionLocationsCache);
		}

		locationsCache.put(locationName, gps);

		getSessionService().setAttribute(SESSION_LOCATIONS_CACHE_KEY, locationsCache);
	}

	protected AddressData generateGeoAddressForSearchQuery(final BaseStoreModel baseStore, final String locationText)
	{
		String country = null; //Only null check is done in GoogleMapTools so default to this.

		if (locationText != null && !locationText.contains(","))
		{
			final Collection<PointOfServiceModel> allPos = baseStore.getPointsOfService();
			if (allPos != null && allPos.iterator().hasNext())
			{
				final PointOfServiceModel pos = allPos.iterator().next();
				if (pos != null && pos.getAddress() != null && pos.getAddress().getCountry() != null)
				{
					country = pos.getAddress().getCountry().getName();
				}
			}
		}
		// Create an address data
		final AddressData addressData = new AddressData();
		addressData.setCity(locationText);
		addressData.setCountryCode(country);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Generated Geo Address Data: City[" + locationText + "] - Country[" + country + "]");
		}

		return addressData;
	}
}
