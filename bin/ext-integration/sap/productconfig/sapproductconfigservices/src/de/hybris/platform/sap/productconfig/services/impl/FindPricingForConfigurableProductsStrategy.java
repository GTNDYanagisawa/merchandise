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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.impl.FindPricingWithCurrentPriceFactoryStrategy;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantProductModel;

import org.apache.log4j.Logger;


public class FindPricingForConfigurableProductsStrategy extends FindPricingWithCurrentPriceFactoryStrategy
{

	private static final Logger LOG = Logger.getLogger(FindPricingForConfigurableProductsStrategy.class);

	private ProductConfigurationService prodConfService;

	private String configurableSource;

	public void setConfigurableSource(final String configurableSource)
	{
		this.configurableSource = configurableSource;
	}


	public ProductConfigurationService getProdConfService()
	{
		return prodConfService;
	}


	public void setProdConfService(final ProductConfigurationService prodConfService)
	{
		this.prodConfService = prodConfService;
	}


	@Override
	public PriceValue findBasePrice(final AbstractOrderEntryModel entry) throws CalculationException
	{
		final PriceValue basePrice;
		final Boolean isConfigurable = (Boolean) getProductAttribute(entry.getProduct(), configurableSource);
		if (isConfigurable != null && isConfigurable.booleanValue())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Keeping old base price for configurable product " + entry.getProduct().getCode());
			}

			final AbstractOrderModel order = entry.getOrder();
			basePrice = new PriceValue(order.getCurrency().getIsocode(), entry.getBasePrice().doubleValue(), order.getNet()
					.booleanValue());

		}
		else
		{
			basePrice = super.findBasePrice(entry);
		}
		return basePrice;
	}


	protected Object getProductAttribute(final ProductModel productModel, final String configurableSource)
	{
		final Boolean value = getModelService().getAttributeValue(productModel, configurableSource);
		if (value == null && productModel instanceof VariantProductModel)
		{
			final ProductModel baseProduct = ((VariantProductModel) productModel).getBaseProduct();
			if (baseProduct != null)
			{
				return getProductAttribute(baseProduct, configurableSource);
			}
		}
		return value;
	}


	protected String getConfigurableSource()
	{
		return configurableSource;
	}
}
