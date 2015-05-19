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
package de.hybris.platform.acceleratorservices.store;

import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;


/**
 * Service relating to selecting a favourite local store
 * 
 */
public interface LocalStorePreferencesService
{
	/**
	 * Returns all the points of service with the distance from the session's stored search location for the current base
	 * store.
	 * 
	 * @see StoreFinderService#positionSearch(de.hybris.platform.store.BaseStoreModel,
	 *      de.hybris.platform.commerceservices.store.data.GeoPoint,
	 *      de.hybris.platform.commerceservices.search.pagedata.PageableData)
	 * 
	 * @return session cached list of points of service.
	 */
	List<PointOfServiceDistanceData> getAllPointsOfService();


	/**
	 * Returns user's favourite stores list
	 * 
	 * @return stores list
	 */
	List<PointOfServiceModel> getFavoritePointOfServices();


	/**
	 * Stores user's favourite stores list
	 * 
	 * @param storesList
	 *           - list of favourite stores
	 */
	void setFavoritesPointOfServices(List<PointOfServiceModel> storesList);


	/**
	 * Adds given point of service to favorite
	 * 
	 * @param favorite
	 */
	void addToFavouritePointOfServices(PointOfServiceModel favorite);


	/**
	 * Removes given point of service from favorite
	 * 
	 * @param pointOfServiceModel
	 */
	void removeFromFavoritePointOfServices(PointOfServiceModel pointOfServiceModel);
}
