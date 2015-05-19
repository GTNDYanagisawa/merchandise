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
package de.hybris.platform.commercefacades.storelocator.impl;

import de.hybris.platform.commercefacades.storelocator.StoreLocatorFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storelocator.helpers.DistanceHelper;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.exception.LocationMapServiceException;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.exception.MapServiceException;
import de.hybris.platform.storelocator.location.DistanceAwareLocation;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.location.LocationMapService;
import de.hybris.platform.storelocator.location.LocationService;
import de.hybris.platform.storelocator.map.Map;
import de.hybris.platform.storelocator.map.MapService;
import de.hybris.platform.storelocator.map.markers.KmlDocument;
import de.hybris.platform.storelocator.map.utils.MapBounds;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import de.hybris.platform.storelocator.route.DistanceAndRoute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link StoreLocatorFacade}
 */
public class DefaultStoreLocatorFacade implements StoreLocatorFacade
{
	protected static final Logger LOG = Logger.getLogger(DefaultStoreLocatorFacade.class);

	private CommonI18NService commonI18NService;
	private BaseSiteService siteService;
	private PointOfServiceService pointOfServiceService;
	private LocationMapService locationMapService;
	private MapService mapService;
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
	private DistanceHelper distanceHelper;
	private LocationService<Location> locationService;

	private static double NULL_RADIUS = 0.0d;
	private static final Map NULL_MAP = new Map()
	{
		@Override
		public double getRadius()
		{
			return NULL_RADIUS;
		}

		@Override
		public List<Location> getPointsOfInterest()
		{
			return Collections.EMPTY_LIST;
		}

		@Override
		public String getTitle()
		{
			return null;
		}

		@Override
		public KmlDocument getKml()
		{
			return null;
		}

		@Override
		public GPS getGps()
		{
			return null;
		}

		@Override
		public DistanceAndRoute getDistanceAndRoute()
		{
			return null;
		}

		@Override
		public MapBounds getMapBounds()
		{
			return null;
		}

	};

	@Override
	public Map getLocationsForQuery(final String searchTerm, final int limit) throws MapServiceException
	{
		Map map = NULL_MAP;

		if (!StringUtils.isEmpty(searchTerm))
		{
			final LanguageModel currentLanguage = getCommonI18NService().getCurrentLanguage();
			final List<Map> allMaps = findAllMaps(currentLanguage.getIsocode(), searchTerm, limit);
			final GPS centerPosition = findCenterPosition(allMaps);

			if (centerPosition != null && CollectionUtils.isNotEmpty(allMaps))
			{
				final List<Location> locations = sortMapLocations(limit, allMaps);
				map = getMapService().getMap(centerPosition, limit, locations, "storeMap");
			}
		}
		return map == null ? NULL_MAP : map;
	}


	protected List<Location> sortMapLocations(final int limit, final List<Map> locations)
	{
		final List<Location> unorderedLocations = new ArrayList<Location>();
		for (final Map singleMap : locations)
		{
			if (CollectionUtils.isNotEmpty(singleMap.getPointsOfInterest()))
			{
				unorderedLocations.addAll(singleMap.getPointsOfInterest());
			}
		}

		// Limit locations and order by distance
		Collections.sort(unorderedLocations, new Comparator<Location>()
		{
			@Override
			public int compare(final Location location1, final Location location2)
			{
				if (location1 instanceof DistanceAwareLocation && location2 instanceof DistanceAwareLocation)
				{
					return ((DistanceAwareLocation) location1).compareTo((DistanceAwareLocation) location2);
				}
				else
				{
					return 0;
				}
			}
		});

		return unorderedLocations.subList(0, Math.min(limit, unorderedLocations.size()));
	}


	protected List<Map> findAllMaps(final String languageCode, final String searchTerm, final int limit)
	{
		final List<Map> allMaps = new ArrayList<Map>();
		for (final BaseStoreModel baseStoreModel : getBaseSiteService().getCurrentBaseSite().getStores())
		{
			try
			{
				final Map localMap = getLocationMapService().getMapOfLocations(searchTerm, languageCode, limit, baseStoreModel);
				allMaps.add(localMap);
			}
			catch (final LocationMapServiceException storeLookupException)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("No place was determined for search phrase " + searchTerm);
				}
			}
		}
		return allMaps;
	}

	protected GPS findCenterPosition(final List<Map> locations)
	{
		GPS centerPosition = null;
		for (final Map singleMap : locations)
		{

			if (CollectionUtils.isNotEmpty(singleMap.getPointsOfInterest()))
			{
				centerPosition = singleMap.getGps();
				break;
			}
		}
		return centerPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storelocator.StoreLocatorFacade#getPointOfServiceForName(java.lang.String)
	 */
	@Override
	public PointOfServiceData getPOSForName(final String posName) throws LocationServiceException
	{
		return getPOSForLocation(locationService.getLocationByName(posName));
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commercefacades.storelocator.StoreLocatorFacade#getPOSForLocation(de.hybris.platform.storelocator
	 * .location.Location)
	 */
	@Override
	public PointOfServiceData getPOSForLocation(final Location location)
	{
		final PointOfServiceModel pointOfServiceModel = getPointOfServiceService().getPointOfServiceForName(location.getName());
		final PointOfServiceData pointOfServiceData = getPointOfServiceConverter().convert(pointOfServiceModel);
		if (location instanceof DistanceAwareLocation)
		{
			pointOfServiceData.setFormattedDistance(getDistanceHelper().getDistanceStringForStore(
					pointOfServiceModel.getBaseStore(), ((DistanceAwareLocation) location).getDistance().doubleValue()));
		}
		return pointOfServiceData;
	}


	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return siteService;
	}

	public void setBaseSiteService(final BaseSiteService siteService)
	{
		this.siteService = siteService;
	}

	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

	protected MapService getMapService()
	{
		return mapService;
	}

	public void setMapService(final MapService mapService)
	{
		this.mapService = mapService;
	}

	protected Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	public void setPointOfServiceConverter(final Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}

	protected DistanceHelper getDistanceHelper()
	{
		return distanceHelper;
	}

	public void setDistanceHelper(final DistanceHelper distanceHelper)
	{
		this.distanceHelper = distanceHelper;
	}

	@Required
	public void setLocationService(final LocationService<Location> locationService)
	{
		this.locationService = locationService;
	}

	public LocationMapService getLocationMapService()
	{
		return locationMapService;
	}

	@Required
	public void setLocationMapService(final LocationMapService locationMapService)
	{
		this.locationMapService = locationMapService;
	}


	protected LocationService<Location> getLocationService()
	{
		return locationService;
	}


}
