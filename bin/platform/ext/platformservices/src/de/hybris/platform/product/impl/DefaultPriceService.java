/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.product.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

import java.util.List;


/**
 * Default implementation of the {@link PriceService}.
 */
public class DefaultPriceService extends AbstractBusinessService implements PriceService
{
	@Override
	public List<PriceInformation> getPriceInformationsForProduct(final ProductModel product)
	{
		// TODO: Re-Implement with PriceFactoryService
		final Product productItem = (Product) getModelService().getSource(product);
		try
		{
			final PriceFactory pricefactory = OrderManager.getInstance().getPriceFactory();
			return productItem.getPriceInformations(pricefactory.isNetUser(JaloSession.getCurrentSession().getUser()));
		}
		catch (final JaloPriceFactoryException e)
		{
			throw new SystemException(e.getMessage(), e);
		}
	}

}
