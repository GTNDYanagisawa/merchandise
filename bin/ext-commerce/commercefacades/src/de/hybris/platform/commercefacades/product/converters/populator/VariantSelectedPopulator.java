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

import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class VariantSelectedPopulator<SOURCE extends ProductModel, TARGET extends ProductData> implements Populator<SOURCE, TARGET>
{
	private Converter<VariantProductModel, BaseOptionData> baseOptionDataConverter;

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		final List<BaseOptionData> baseOptions = new ArrayList<BaseOptionData>();

		ProductModel currentProduct = productModel;
		while (currentProduct instanceof VariantProductModel)
		{
			final VariantProductModel variantProduct = (VariantProductModel) currentProduct;

			baseOptions.add(getBaseOptionDataConverter().convert(variantProduct));

			// Step up to the parent product
			currentProduct = variantProduct.getBaseProduct();
		}
		productData.setBaseOptions(baseOptions);
	}


	protected Converter<VariantProductModel, BaseOptionData> getBaseOptionDataConverter()
	{
		return baseOptionDataConverter;
	}

	@Required
	public void setBaseOptionDataConverter(final Converter<VariantProductModel, BaseOptionData> baseOptionDataConverter)
	{
		this.baseOptionDataConverter = baseOptionDataConverter;
	}
}
