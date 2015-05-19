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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.log4j.Logger;


public class ProductConfigurationPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{
	private static final Logger LOG = Logger.getLogger(ProductConfigurationPopulator.class);

	private String configurableSource;

	public void setConfigurableSource(final String configurableSource)
	{
		this.configurableSource = configurableSource;
	}

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		final Boolean isProductConfigurable = (Boolean) getProductAttribute(productModel, configurableSource);
		productData.setConfigurable(isProductConfigurable);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(productModel.getName() + " (" + productModel.getCode() + "): configurable=" + isProductConfigurable);
		}
	}


}
