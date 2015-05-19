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
 */
package de.hybris.platform.ycommercewebservices.queues.channel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.ycommercewebservices.queues.UpdateQueue;
import de.hybris.platform.ycommercewebservices.queues.data.ProductExpressUpdateElementData;
import de.hybris.platform.ycommercewebservices.queues.util.ProductExpressUpdateElementPredicate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Predicate;


public class ProductExpressUpdateChannelListener
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ProductExpressUpdateChannelListener.class);
	private final ProductExpressUpdateElementPredicate productExpressUpdateElementPredicate = new ProductExpressUpdateElementPredicate();
	private UpdateQueue<ProductExpressUpdateElementData> productExpressUpdateQueue;
	private Converter<ProductModel, ProductExpressUpdateElementData> productExpressUpdateElementConverter;

	public void onMessage(final ProductModel product)
	{
		LOG.debug("ProductExpressUpdateChannelListener got product with code " + product.getCode());
		final ProductExpressUpdateElementData productExpressUpdateElementData = getProductExpressUpdateElementConverter().convert(
				product);
		getProductExpressUpdateQueue().removeItems(getPredicate(productExpressUpdateElementData));
		getProductExpressUpdateQueue().addItem(productExpressUpdateElementData);
	}

	/**
	 * Method return object which will be used to determine if element is equal to productExpressUpdateElementData
	 * parameter.
	 * 
	 * @param productExpressUpdateElementData
	 *           - element data for comparison
	 * @return object implementing Predicate interface which should return true from apply method if element is equal to
	 *         productExpressUpdateElementData parameter
	 */
	protected Predicate<ProductExpressUpdateElementData> getPredicate(
			final ProductExpressUpdateElementData productExpressUpdateElementData)
	{
		productExpressUpdateElementPredicate.setProductExpressUpdateElementData(productExpressUpdateElementData);
		return productExpressUpdateElementPredicate;
	}

	public UpdateQueue<ProductExpressUpdateElementData> getProductExpressUpdateQueue()
	{
		return productExpressUpdateQueue;
	}

	@Required
	public void setProductExpressUpdateQueue(final UpdateQueue<ProductExpressUpdateElementData> productExpressUpdateQueue)
	{
		this.productExpressUpdateQueue = productExpressUpdateQueue;
	}

	public Converter<ProductModel, ProductExpressUpdateElementData> getProductExpressUpdateElementConverter()
	{
		return productExpressUpdateElementConverter;
	}

	@Required
	public void setProductExpressUpdateElementConverter(
			final Converter<ProductModel, ProductExpressUpdateElementData> productExpressUpdateElementConverter)
	{
		this.productExpressUpdateElementConverter = productExpressUpdateElementConverter;
	}

}
