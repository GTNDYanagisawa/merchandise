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
package de.hybris.platform.commercefacades.storefinder.impl;

import de.hybris.platform.commercefacades.storefinder.StoreFinderFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link StoreFinderFacade}
 */
public class DefaultStoreFinderFacade implements StoreFinderFacade
{
	private StoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> storeFinderService;
	private BaseStoreService baseStoreService;
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
	private Converter<StoreFinderSearchPageData<PointOfServiceDistanceData>, StoreFinderSearchPageData<PointOfServiceData>> searchPagePointOfServiceDistanceConverter;
	private Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceConverter;

	protected StoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> getStoreFinderService()
	{
		return storeFinderService;
	}

	@Required
	public void setStoreFinderService(
			final StoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> storeFinderService)
	{
		this.storeFinderService = storeFinderService;
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

	protected Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	@Required
	public void setPointOfServiceConverter(final Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}

	protected Converter<StoreFinderSearchPageData<PointOfServiceDistanceData>, StoreFinderSearchPageData<PointOfServiceData>> getSearchPagePointOfServiceDistanceConverter()
	{
		return searchPagePointOfServiceDistanceConverter;
	}

	@Required
	public void setSearchPagePointOfServiceDistanceConverter(
			final Converter<StoreFinderSearchPageData<PointOfServiceDistanceData>, StoreFinderSearchPageData<PointOfServiceData>> searchPagePointOfServiceDistanceConverter)
	{
		this.searchPagePointOfServiceDistanceConverter = searchPagePointOfServiceDistanceConverter;
	}

	protected Converter<PointOfServiceDistanceData, PointOfServiceData> getPointOfServiceDistanceConverter()
	{
		return pointOfServiceDistanceConverter;
	}

	@Required
	public void setPointOfServiceDistanceConverter(
			final Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceConverter)
	{
		this.pointOfServiceDistanceConverter = pointOfServiceDistanceConverter;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.yacceleratorfacades.storefinder.StoreFinderFacade#locationSearch(java.lang.String,
	 * de.hybris.platform.commerceservices.search.pagedata.PageableData)
	 */
	@Override
	public StoreFinderSearchPageData<PointOfServiceData> locationSearch(final String locationText, final PageableData pageableData)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = getStoreFinderService().locationSearch(
				currentBaseStore, locationText, pageableData);
		return getSearchPagePointOfServiceDistanceConverter().convert(searchPageData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.yacceleratorfacades.storefinder.StoreFinderFacade#locationSearch(java.lang.String,
	 * de.hybris.platform.commerceservices.search.pagedata.PageableData, int)
	 */
	@Override
	public StoreFinderSearchPageData<PointOfServiceData> locationSearch(final String locationText,
			final PageableData pageableData, final double maxRadius)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = getStoreFinderService().locationSearch(
				currentBaseStore, locationText, pageableData, maxRadius);
		return getSearchPagePointOfServiceDistanceConverter().convert(searchPageData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.yacceleratorfacades.storefinder.StoreFinderFacade#positionSearch(double, double,
	 * de.hybris.platform.commerceservices.search.pagedata.PageableData)
	 */
	@Override
	public StoreFinderSearchPageData<PointOfServiceData> positionSearch(final GeoPoint geoPoint,
			final PageableData pageableData)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = getStoreFinderService().positionSearch(
				currentBaseStore, geoPoint, pageableData);
		return getSearchPagePointOfServiceDistanceConverter().convert(searchPageData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.yacceleratorfacades.storefinder.StoreFinderFacade#positionSearch(double, double,
	 * de.hybris.platform.commerceservices.search.pagedata.PageableData, double)
	 */
	@Override
	public StoreFinderSearchPageData<PointOfServiceData> positionSearch(final GeoPoint geoPoint,
			final PageableData pageableData, final double maxRadius)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = getStoreFinderService().positionSearch(
				currentBaseStore, geoPoint, pageableData, maxRadius);
		return getSearchPagePointOfServiceDistanceConverter().convert(searchPageData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.yacceleratorfacades.storefinder.StoreFinderFacade#getPointOfServiceForName(java.lang.String)
	 */
	@Override
	public PointOfServiceData getPointOfServiceForName(final String name)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final PointOfServiceModel pointOfService = getStoreFinderService().getPointOfServiceForName(currentBaseStore, name);
		return getPointOfServiceConverter().convert(pointOfService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.yacceleratorfacades.storefinder.StoreFinderFacade#getPointOfServiceForNameAndPosition(java.
	 * lang.String, double, double)
	 */
	@Override
	public PointOfServiceData getPointOfServiceForNameAndPosition(final String name, final GeoPoint geoPoint)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final PointOfServiceDistanceData pointOfServiceDistance = getStoreFinderService().getPointOfServiceDistanceForName(
				currentBaseStore, name, geoPoint);
		return getPointOfServiceDistanceConverter().convert(pointOfServiceDistance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storefinder.StoreFinderFacade#getAllPointOfServices(de.hybris.platform.
	 * commerceservices.search.pagedata.PageableData)
	 */
	@Override
	public StoreFinderSearchPageData<PointOfServiceData> getAllPointOfServices(final PageableData pageableData)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = getStoreFinderService().getAllPos(
				currentBaseStore, pageableData);
		return getSearchPagePointOfServiceDistanceConverter().convert(searchPageData);
	}

}
