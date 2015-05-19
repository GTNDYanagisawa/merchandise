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
package de.hybris.platform.commercefacades.storefinder;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;


/**
 * Store finder facade. It is used for retrieving data for store finder related data.
 */
public interface StoreFinderFacade
{

	/**
	 * Gets the search page data object parametrized with {@link PointOfServiceData} and contains location search results
	 * for the given location text.
	 * 
	 * @param locationText
	 *           the text that location search is performed for
	 * @param pageableData
	 *           {@link PageableData} object that contains basing information for search result set
	 * @return locations found for the given parameters
	 */
	StoreFinderSearchPageData<PointOfServiceData> locationSearch(String locationText, PageableData pageableData);

	/**
	 * Gets the search page data object parametrized with {@link PointOfServiceData} and contains location search results
	 * for the given location text.
	 * 
	 * @param locationText
	 *           the text that location search is performed for
	 * @param pageableData
	 *           {@link PageableData} object that contains basing information for search result set
	 * @param maxRadius
	 *           the maximum radius from the location that results should be returned for.
	 * @return locations found for the given parameters
	 */
	StoreFinderSearchPageData<PointOfServiceData> locationSearch(String locationText, PageableData pageableData, double maxRadius);


	/**
	 * Gets the search page data object parametrized with {@link PointOfServiceData} and contains location search results
	 * for the given coordinates
	 * 
	 * @param geoPoint
	 *           geographical point that search is performed for
	 * @param pageableData
	 *           {@link PageableData} object that contains basing information for search result set
	 * @return locations found for the given parameters
	 */
	StoreFinderSearchPageData<PointOfServiceData> positionSearch(GeoPoint geoPoint, PageableData pageableData);

	/**
	 * Gets the search page data object parametrized with {@link PointOfServiceData} and contains location search results
	 * for the given coordinates
	 *
	 * @param geoPoint
	 *           geographical point that search is performed for
	 * @param pageableData
	 *           {@link PageableData} object that contains basing information for search result set
	 * @param maxRadius
	 *           the maximum radius from the location that results should be returned for.
	 * @return locations found for the given parameters
	 */
	StoreFinderSearchPageData<PointOfServiceData> positionSearch(GeoPoint geoPoint, PageableData pageableData,
			double maxRadius);


	/**
	 * Gets the {@link PointOfServiceData} for the given name that should be unique
	 * 
	 * @param name
	 *           the point of service name
	 * @return {@link PointOfServiceData} for the given parameter
	 */
	PointOfServiceData getPointOfServiceForName(String name);


	/**
	 * Gets the {@link PointOfServiceData} for the given name that should be unique. Additionally the distance between
	 * the {@link PointOfServiceData} and given coordinates is calculated.
	 * 
	 * @param name
	 *           - name of the requested point of service
	 * @param geoPoint
	 *           - geographical location of the origin point
	 * @return {@link PointOfServiceData} for the given parameter
	 */
	PointOfServiceData getPointOfServiceForNameAndPosition(String name, GeoPoint geoPoint);

	/**
	 * Gets the search page data object parametrized with {@link PointOfServiceData} and contains all stores for current
	 * base store
	 * 
	 * @param pageableData
	 *           {@link PageableData} object that contains basing information for search result set
	 * 
	 * @return locations found for the given parameters
	 */
	StoreFinderSearchPageData<PointOfServiceData> getAllPointOfServices(PageableData pageableData);

}
