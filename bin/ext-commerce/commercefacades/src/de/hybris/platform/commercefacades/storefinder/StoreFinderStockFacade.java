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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceStockData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;


/**
 * Store finder stock facade. It is used for retrieving store specific stock data.
 * 
 */
public interface StoreFinderStockFacade<ITEM extends PointOfServiceStockData, RESULT extends StoreFinderStockSearchPageData<ITEM>>
{
	/**
	 * Returns result set with point of services with stock levels for the given location free text search term
	 * 
	 * @param location
	 * @param productData
	 * @param pageableData
	 * @return {@link StoreFinderStockSearchPageData} with {@link PointOfServiceStockData}
	 */
	RESULT productSearch(String location, ProductData productData, PageableData pageableData);

	/**
	 * Returns result set with point of services with stock levels for the given point of service name
	 * 
	 * @param posName
	 * @param productData
	 * @param pageableData
	 * @return {@link StoreFinderStockSearchPageData} with {@link PointOfServiceStockData}
	 */
	RESULT productPOSSearch(String posName, ProductData productData, PageableData pageableData);

	/**
	 * Returns result set with point of services with stock levels for the given GPS coordinates
	 * 
	 * @param geoPoint
	 * @param productData
	 * @param pageableData
	 * @return {@link StoreFinderStockSearchPageData} with {@link PointOfServiceStockData}
	 */
	RESULT productSearch(GeoPoint geoPoint, ProductData productData, PageableData pageableData);
}
