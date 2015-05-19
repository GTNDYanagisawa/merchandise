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
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceStockData;
import de.hybris.platform.commercefacades.storelocator.data.StoreStockHolder;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populate the store stock data with store specific stock information
 * 
 */
public class StoreStockPopulator implements Populator<StoreStockHolder, PointOfServiceStockData>
{
	private CommerceStockService commerceStockService;
	private BaseStoreService baseStoreService;
	private Populator<PointOfServiceModel, PointOfServiceData> pointOfServicePopulator;


	protected Populator<PointOfServiceModel, PointOfServiceData> getPointOfServicePopulator()
	{
		return pointOfServicePopulator;
	}

	@Required
	public void setPointOfServicePopulator(final Populator<PointOfServiceModel, PointOfServiceData> pointOfServicePopulator)
	{
		this.pointOfServicePopulator = pointOfServicePopulator;
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

	protected CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	@Required
	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}



	@Override
	public void populate(final StoreStockHolder storeStockHolder, final PointOfServiceStockData pointOfServiceStockData)
			throws ConversionException
	{
		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		final boolean stockSystemEnabled = isStockSystemEnabled(baseStore);
		getPointOfServicePopulator().populate(storeStockHolder.getPointOfService(), pointOfServiceStockData);

		final StockData stockData = new StockData();
		if (stockSystemEnabled)
		{

			stockData.setStockLevel(getCommerceStockService().getStockLevelForProductAndPointOfService(
					storeStockHolder.getProduct(), storeStockHolder.getPointOfService()));
			stockData.setStockLevelStatus(getCommerceStockService().getStockLevelStatusForProductAndPointOfService(
					storeStockHolder.getProduct(), storeStockHolder.getPointOfService()));
		}
		else
		{
			stockData.setStockLevel(Long.valueOf(0));
			stockData.setStockLevelStatus(StockLevelStatus.INSTOCK);
		}
		pointOfServiceStockData.setStockData(stockData);
	}


	protected boolean isStockSystemEnabled()
	{
		return getCommerceStockService().isStockSystemEnabled(getBaseStoreService().getCurrentBaseStore());
	}

	protected boolean isStockSystemEnabled(final BaseStoreModel baseStore)
	{
		return getCommerceStockService().isStockSystemEnabled(baseStore);
	}

}
