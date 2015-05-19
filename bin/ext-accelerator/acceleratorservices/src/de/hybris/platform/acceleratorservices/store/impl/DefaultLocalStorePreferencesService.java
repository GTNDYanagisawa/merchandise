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

import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorservices.store.LocalStorePreferencesService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link LocalStorePreferencesService}
 * 
 */
public class DefaultLocalStorePreferencesService implements LocalStorePreferencesService
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultLocalStorePreferencesService.class);

	public final static String SESSION_FAVOURITES_STORES_KEY = "userFavouritesStores";
	public final static String SESSION_NEAREST_STORES_KEY = "userNearestStores";

	private SessionService sessionService;
	private BaseStoreService baseStoreService;
	private StoreFinderService storeFinderService;
	private CustomerLocationService customerLocationService;



	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.store.LocalStorePreferencesService#getNearestStores()
	 */
	@Override
	public List<PointOfServiceDistanceData> getAllPointsOfService()
	{
		List<PointOfServiceDistanceData> result = (List<PointOfServiceDistanceData>) getSessionService().getAttribute(
				SESSION_NEAREST_STORES_KEY);
		if (CollectionUtils.isEmpty(result) && getCustomerLocationService().getUserLocation() != null
				&& getCustomerLocationService().getUserLocation().getPoint() != null)
		{
			final StoreFinderSearchPageData<PointOfServiceDistanceData> posData = getStoreFinderService().positionSearch(
					getBaseStoreService().getCurrentBaseStore(), getCustomerLocationService().getUserLocation().getPoint(),
					getPageableData());

			result = new ArrayList<PointOfServiceDistanceData>(posData.getResults().size());
			result.addAll(posData.getResults());
			getSessionService().setAttribute(SESSION_NEAREST_STORES_KEY, result);
		}
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.store.LocalStorePreferencesService#getFavouriteStores()
	 */
	@Override
	public List<PointOfServiceModel> getFavoritePointOfServices()
	{
		List<PointOfServiceModel> favorites = (List<PointOfServiceModel>) getSessionService().getAttribute(
				SESSION_FAVOURITES_STORES_KEY);
		if (favorites == null)
		{
			favorites = new ArrayList<PointOfServiceModel>();
			getSessionService().setAttribute(SESSION_FAVOURITES_STORES_KEY, favorites);
		}
		return favorites;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.store.LocalStorePreferencesService#setFavouritesStores(java.util.List)
	 */
	@Override
	public void setFavoritesPointOfServices(final List<PointOfServiceModel> storesList)
	{
		getSessionService().setAttribute(SESSION_FAVOURITES_STORES_KEY, storesList);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.acceleratorservices.store.LocalStorePreferencesService#addToFavouritePointOfServices(de.hybris
	 * .platform.storelocator.model.PointOfServiceModel)
	 */
	@Override
	public void addToFavouritePointOfServices(final PointOfServiceModel favorite)
	{
		getFavoritePointOfServices().add(favorite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.acceleratorservices.store.LocalStorePreferencesService#removeFromFavoritePointOfServices(de
	 * .hybris.platform.storelocator.model.PointOfServiceModel)
	 */
	@Override
	public void removeFromFavoritePointOfServices(final PointOfServiceModel pointOfServiceModel)
	{
		getFavoritePointOfServices().remove(pointOfServiceModel);
	}

	protected PageableData getPageableData()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(getBaseStoreService().getCurrentBaseStore().getPointsOfService().size());
		pageableData.setSort(null);
		return pageableData;
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

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected StoreFinderService getStoreFinderService()
	{
		return storeFinderService;
	}

	@Required
	public void setStoreFinderService(final StoreFinderService storeFinderService)
	{
		this.storeFinderService = storeFinderService;
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

}
