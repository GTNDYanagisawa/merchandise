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
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class VariantFullPopulator<SOURCE extends ProductModel, TARGET extends ProductData> implements Populator<SOURCE, TARGET>
{
	private Converter<VariantProductModel, VariantOptionData> variantOptionDataConverter;
	private Converter<VariantProductModel, BaseOptionData> baseOptionDataConverter;

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		// Populate the list of available child variant options
		if (productModel.getVariantType() != null && CollectionUtils.isNotEmpty(productModel.getVariants()))
		{
			final List<VariantOptionData> variantOptions = new ArrayList<VariantOptionData>();
			for (final VariantProductModel variantProductModel : productModel.getVariants())
			{
				variantOptions.add(getVariantOptionDataConverter().convert(variantProductModel));
			}
			productData.setVariantOptions(variantOptions);
		}

		// Populate the list of base options
		final List<BaseOptionData> baseOptions = new ArrayList<BaseOptionData>();
		ProductModel currentProduct = productModel;

		while (currentProduct instanceof VariantProductModel)
		{
			final ProductModel baseProduct = ((VariantProductModel) currentProduct).getBaseProduct();

			final BaseOptionData baseOptionData = getBaseOptionDataConverter().convert((VariantProductModel) currentProduct);

			// Fill out the list of available product options
			baseOptionData.setOptions(Converters.convertAll(baseProduct.getVariants(), getVariantOptionDataConverter()));

			baseOptions.add(baseOptionData);
			currentProduct = baseProduct;
		}
		productData.setBaseOptions(baseOptions);
	}


	protected Converter<VariantProductModel, VariantOptionData> getVariantOptionDataConverter()
	{
		return variantOptionDataConverter;
	}

	@Required
	public void setVariantOptionDataConverter(final Converter<VariantProductModel, VariantOptionData> variantOptionDataConverter)
	{
		this.variantOptionDataConverter = variantOptionDataConverter;
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
