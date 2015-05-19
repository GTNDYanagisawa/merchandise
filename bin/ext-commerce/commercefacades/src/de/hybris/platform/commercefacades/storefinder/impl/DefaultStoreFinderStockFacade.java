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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storefinder.StoreFinderStockFacade;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceStockData;
import de.hybris.platform.commercefacades.storelocator.data.StoreStockHolder;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link StoreFinderStockFacade}
 * 
 */
public class DefaultStoreFinderStockFacade<ITEM extends PointOfServiceStockData> implements
		StoreFinderStockFacade<ITEM, StoreFinderStockSearchPageData<ITEM>>
{
	private Converter<StoreStockHolder, ITEM> storeStockConverter;
	private BaseStoreService baseStoreService;
	private StoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> storeFinderService;
	private PointOfServiceService pointOfServiceService;
	private ProductService productService;
	private Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceDataConverter;

	@Override
	public StoreFinderStockSearchPageData<ITEM> productSearch(final String location, final ProductData productData,
			final PageableData pageableData)
	{
		final StoreFinderSearchPageData<PointOfServiceDistanceData> storeFinderSearchPageData = getStoreFinderService()
				.locationSearch(getBaseStoreService().getCurrentBaseStore(), location, pageableData);
		return getResultForPOSData(storeFinderSearchPageData, productData);
	}

	@Override
	public StoreFinderStockSearchPageData<ITEM> productPOSSearch(final String posName, final ProductData productData,
			final PageableData pageableData)
	{
		final PointOfServiceModel pointOfService = getPointOfServiceService().getPointOfServiceForName(posName);
		if (pointOfService != null && pointOfService.getLatitude() != null && pointOfService.getLongitude() != null)
		{
			StoreFinderSearchPageData<PointOfServiceDistanceData> storeFinderSearchPageData;
			if (pointOfService.getNearbyStoreRadius() != null)
			{
				final GeoPoint geoPoint = new GeoPoint();
				geoPoint.setLatitude(pointOfService.getLatitude().doubleValue());
				geoPoint.setLongitude(pointOfService.getLongitude().doubleValue());

				storeFinderSearchPageData = getStoreFinderService().positionSearch(getBaseStoreService().getCurrentBaseStore(),
						geoPoint, pageableData, pointOfService.getNearbyStoreRadius().doubleValue());
			}
			else
			{
				final GeoPoint geoPoint = new GeoPoint();
				geoPoint.setLatitude(pointOfService.getLatitude().doubleValue());
				geoPoint.setLongitude(pointOfService.getLongitude().doubleValue());

				storeFinderSearchPageData = getStoreFinderService().positionSearch(getBaseStoreService().getCurrentBaseStore(),
						geoPoint, pageableData);

			}
			return getResultForPOSData(storeFinderSearchPageData, productData);
		}
		else
		{
			return createSearchResult(Collections.<ITEM> emptyList(), createPagination(pageableData, 0), productData);
		}

	}

	@Override
	public StoreFinderStockSearchPageData<ITEM> productSearch(final GeoPoint geoPoint,
			final ProductData productData, final PageableData pageableData)
	{
		final StoreFinderSearchPageData<PointOfServiceDistanceData> storeFinderSearchPageData = getStoreFinderService()
				.positionSearch(getBaseStoreService().getCurrentBaseStore(), geoPoint, pageableData);
		return getResultForPOSData(storeFinderSearchPageData, productData);
	}

	protected StoreFinderStockSearchPageData<ITEM> getResultForPOSData(
			final StoreFinderSearchPageData<PointOfServiceDistanceData> storeFinderSearchPageData, final ProductData productData)
	{
		final List<ITEM> result = new ArrayList<ITEM>(storeFinderSearchPageData.getResults().size());
		for (final PointOfServiceDistanceData distanceData : storeFinderSearchPageData.getResults())
		{
			final StoreStockHolder storeStockHolder = createStoreStockHolder();
			storeStockHolder.setPointOfService(distanceData.getPointOfService());
			storeStockHolder.setProduct(getProductService().getProductForCode(productData.getCode()));

			final ITEM posStockData = getStoreStockConverter().convert(storeStockHolder);
			posStockData.setFormattedDistance(getPointOfServiceDistanceDataConverter().convert(distanceData).getFormattedDistance());
			result.add(posStockData);
		}
		return createSearchResult(result, storeFinderSearchPageData.getPagination(), productData);
	}

	protected StoreFinderStockSearchPageData<ITEM> createSearchResult(final List<ITEM> results,
			final PaginationData paginationData, final ProductData productData)
	{
		final StoreFinderStockSearchPageData<ITEM> searchPageData = createStoreFinderStockSearchPageData();
		searchPageData.setResults(results);
		searchPageData.setPagination(paginationData);
		searchPageData.setProduct(productData);

		return searchPageData;
	}

	protected StoreStockHolder createStoreStockHolder()
	{
		return new StoreStockHolder();
	}

	protected StoreFinderStockSearchPageData<ITEM> createStoreFinderStockSearchPageData()
	{
		return new StoreFinderStockSearchPageData<ITEM>();
	}

	protected PaginationData createPagination(final PageableData pageableData, final long totalNumberOfResults)
	{
		final PaginationData paginationData = createPaginationData();

		// Set the page size and and don't allow it to be less than 1
		paginationData.setPageSize(Math.max(1, pageableData.getPageSize()));

		paginationData.setTotalNumberOfResults(totalNumberOfResults);

		// Calculate the number of pages
		paginationData.setNumberOfPages((int) Math.ceil(((double) paginationData.getTotalNumberOfResults())
				/ paginationData.getPageSize()));

		// Work out the current page, fixing any invalid page values
		paginationData.setCurrentPage(Math.max(0, Math.min(paginationData.getNumberOfPages(), pageableData.getCurrentPage())));

		return paginationData;
	}

	protected PaginationData createPaginationData()
	{
		return new PaginationData();
	}

	protected Converter<StoreStockHolder, ITEM> getStoreStockConverter()
	{
		return storeStockConverter;
	}

	@Required
	public void setStoreStockConverter(final Converter<StoreStockHolder, ITEM> storeStockConverter)
	{
		this.storeStockConverter = storeStockConverter;
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

	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected Converter<PointOfServiceDistanceData, PointOfServiceData> getPointOfServiceDistanceDataConverter()
	{
		return pointOfServiceDistanceDataConverter;
	}

	@Required
	public void setPointOfServiceDistanceDataConverter(
			final Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceDataConverter)
	{
		this.pointOfServiceDistanceDataConverter = pointOfServiceDistanceDataConverter;
	}

}
