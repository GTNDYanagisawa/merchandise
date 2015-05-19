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
package de.hybris.platform.sap.sapordermgmtb2bfacades.impl;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductUrlPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.apache.log4j.Logger;


/**
 * Default implementation for {@link ProductImageHelper}
 *
 */
public class DefaultProductImageHelper implements ProductImageHelper
{
	private static final Logger LOG = Logger.getLogger(DefaultProductImageHelper.class);

	private ProductService productService;
	private ProductPrimaryImagePopulator<ProductModel, ProductData> productPrimaryImagePopulator;
	private ProductUrlPopulator productUrlPopulator;


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.sapordermgmtfacades.ProductImageHelper#enrichWithProductImages(de.hybris.platform.
	 * commercefacades .order.data.AbstractOrderData)
	 */
	@Override
	public void enrichWithProductImages(final AbstractOrderData order)
	{
		for (final OrderEntryData entry : order.getEntries())
		{
			enrichWithProductImages(entry);
		}

	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper#enrichWithProductImages(de.hybris.platform.
	 * commercefacades.order.data.OrderEntryData)
	 */
	@Override
	public void enrichWithProductImages(final OrderEntryData entry)
	{
		final ProductData product = entry.getProduct();
		if (product != null)
		{
			try
			{
				final ProductModel productModel = productService.getProductForCode(product.getCode());
				productPrimaryImagePopulator.populate(productModel, product);
				productUrlPopulator.populate(productModel, product);
			}
			catch (final UnknownIdentifierException ex)
			{
				//The product is not part of hybris catalog -> no image will be attached
				LOG.info("Product with code: " + product.getCode() + " is not available as model (might not be part of catalog)");
			}
		}
	}


	@Override
	public ProductService getProductService()
	{
		return this.productService;
	}


	@Override
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}


	@Override
	public void setProductPrimaryImagePopulator(
			final ProductPrimaryImagePopulator<ProductModel, ProductData> productPrimaryImagePopulator)
	{
		this.productPrimaryImagePopulator = productPrimaryImagePopulator;
	}


	@Override
	public ProductPrimaryImagePopulator<ProductModel, ProductData> getProductPrimaryImagePopulator()
	{
		return productPrimaryImagePopulator;
	}


	/**
	 * @return the productUrlPopulator
	 */
	public ProductUrlPopulator getProductUrlPopulator()
	{
		return productUrlPopulator;
	}


	/**
	 * @param productUrlPopulator
	 *           the productUrlPopulator to set
	 */
	public void setProductUrlPopulator(final ProductUrlPopulator productUrlPopulator)
	{
		this.productUrlPopulator = productUrlPopulator;
	}

}
