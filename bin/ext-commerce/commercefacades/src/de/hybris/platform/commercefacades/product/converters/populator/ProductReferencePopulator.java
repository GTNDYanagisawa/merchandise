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

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.catalog.model.ProductReferenceModel} as source and
 * {@link de.hybris.platform.commercefacades.product.data.ProductReferenceData} as target type.
 */
public class ProductReferencePopulator implements Populator<ProductReferenceModel, ProductReferenceData>
{
	private Converter<ProductModel, ProductData> productConverter;

	@Override
	public void populate(final ProductReferenceModel source, final ProductReferenceData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setDescription(source.getDescription());
		target.setPreselected(source.getPreselected());
		target.setQuantity(source.getQuantity());
		target.setReferenceType(source.getReferenceType());
		final ProductModel product = source.getTarget();
		target.setTarget(product == null ? null : productConverter.convert(product));
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}
}
