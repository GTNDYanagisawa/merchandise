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
package de.hybris.platform.sap.sapordermgmtb2bfacades;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;


/**
 * Used for enriching orders with images corresponding to the products contained
 *
 */
public interface ProductImageHelper
{

	/**
	 * Attaches images to the product corresponding to the entry parameter
	 *
	 * @param entry
	 */
	void enrichWithProductImages(OrderEntryData entry);

	/**
	 * Attaches images to the products corresponding to each order entry
	 *
	 * @param order
	 */
	void enrichWithProductImages(AbstractOrderData order);

	/**
	 * @return productService
	 */
	ProductService getProductService();

	/**
	 * @param productService
	 */
	void setProductService(ProductService productService);

	/**
	 * @return productImagePopulator
	 */
	ProductPrimaryImagePopulator<ProductModel, ProductData> getProductPrimaryImagePopulator();

	/**
	 * @param productPrimaryImagePopulator
	 */
	void setProductPrimaryImagePopulator(ProductPrimaryImagePopulator<ProductModel, ProductData> productPrimaryImagePopulator);
}
