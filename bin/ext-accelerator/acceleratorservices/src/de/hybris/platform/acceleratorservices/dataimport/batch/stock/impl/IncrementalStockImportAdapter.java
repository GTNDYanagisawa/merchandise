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
package de.hybris.platform.acceleratorservices.dataimport.batch.stock.impl;

import de.hybris.platform.acceleratorservices.dataimport.batch.stock.StockImportAdapter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.StockService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link StockImportAdapter}.
 */
public class IncrementalStockImportAdapter implements StockImportAdapter
{
	private static final Logger LOG = Logger.getLogger(StockImportAdapter.class);
	private static final String DEFAULT_WAREHOUSE_CODE = "default";
	private String warehouseCode = DEFAULT_WAREHOUSE_CODE;
	private ModelService modelService;
	private WarehouseService warehouseService;
	private StockService stockService;

	@Override
	public void performImport(final String cellValue, final Item product)
	{
		Assert.hasText(cellValue);
		Assert.notNull(product);
		try
		{
			final String[] values = cellValue.split(":");
			int actualAmount = Integer.parseInt(values[0]);
			final WarehouseModel warehouseModel;
			if (values.length > 1 && values[1] != null && !values[1].isEmpty())
			{
				warehouseModel = warehouseService.getWarehouseForCode(values[1]);
			}
			else
			{
				warehouseModel = warehouseService.getWarehouseForCode(warehouseCode);
			}
			final ProductModel productModel = modelService.get(product);

			actualAmount += stockService.getStockLevelAmount(productModel, warehouseModel);

			if (actualAmount < 0)
			{
				actualAmount = 0;
			}

			stockService.updateActualStockLevel(productModel, warehouseModel, actualAmount, null);
		}
		catch (final RuntimeException e)
		{
			LOG.warn("Could not import stock for product " + product + ": " + e);
			throw e;
		}
		catch (final Exception e)
		{
			LOG.warn("Could not import stock for " + product + ": " + e);
			throw new SystemException("Could not import stock for " + product, e);
		}
	}

	/**
	 * @param warehouseCode
	 *           the warehouseCode to set
	 */
	public void setWarehouseCode(final String warehouseCode)
	{
		Assert.hasText(warehouseCode);
		this.warehouseCode = warehouseCode;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @param stockService
	 *           the stockService to set
	 */
	@Required
	public void setStockService(final StockService stockService)
	{
		this.stockService = stockService;
	}

	/**
	 * @param warehouseService
	 *           the warehouseService to set
	 */
	@Required
	public void setWarehouseService(final WarehouseService warehouseService)
	{
		this.warehouseService = warehouseService;
	}

	/**
	 * @return the warehouseCode
	 */
	protected String getWarehouseCode()
	{
		return warehouseCode;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @return the warehouseService
	 */
	protected WarehouseService getWarehouseService()
	{
		return warehouseService;
	}

	/**
	 * @return the stockService
	 */
	protected StockService getStockService()
	{
		return stockService;
	}
}
