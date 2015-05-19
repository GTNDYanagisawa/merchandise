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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populate the product data with the product's configurable attribute
 */
public class ProductConfigurableSearchPopulator implements Populator<SearchResultValueData, ProductData>
{

	private String configurableSource;

	public void setConfigurableSource(final String configurableSource)
	{
		this.configurableSource = configurableSource;
	}


	@Override
	public void populate(final SearchResultValueData source, final ProductData target) throws ConversionException
	{
		final Boolean isProductConfigurable = (Boolean) getValue(source, configurableSource);
		target.setConfigurable(isProductConfigurable);
	}

	protected <T> T getValue(final SearchResultValueData source, final String propertyName)
	{
		if (source.getValues() == null)
		{
			return null;
		}

		return (T) source.getValues().get(propertyName);
	}
}
