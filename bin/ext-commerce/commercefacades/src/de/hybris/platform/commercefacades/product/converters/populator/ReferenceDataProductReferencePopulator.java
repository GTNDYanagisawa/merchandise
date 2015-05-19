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

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commerceservices.product.data.ReferenceData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 */
public class ReferenceDataProductReferencePopulator extends AbstractPopulatingConverter<ReferenceData<ProductReferenceTypeEnum, ProductModel>, ProductReferenceData>
{
	private Converter<ProductModel, ProductData> productConverter;

	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	@Override
	protected ProductReferenceData createTarget()
	{
		return new ProductReferenceData();
	}

	@Override
	public void populate(final ReferenceData<ProductReferenceTypeEnum, ProductModel> source, final ProductReferenceData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setDescription(source.getDescription());
		target.setQuantity(source.getQuantity());
		target.setReferenceType(source.getReferenceType());
		target.setTarget(getProductConverter().convert(source.getTarget()));

		super.populate(source, target);
	}
}
