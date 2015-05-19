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
package de.hybris.platform.yb2bacceleratorfacades.product.populators;

import de.hybris.platform.b2b.model.VariantCategoryModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.VariantCategoryData;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.commercefacades.product.data.VariantValueCategoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;


public class VariantMatrixElementPopulator<SOURCE extends VariantValueCategoryModel, TARGET extends VariantMatrixElementData>
		implements Populator<SOURCE, TARGET>
{


	@Override
	public void populate(final VariantValueCategoryModel variantValueCategory,
			final VariantMatrixElementData variantMatrixElementData) throws ConversionException
	{
		final VariantCategoryData parent = new VariantCategoryData();

		final VariantCategoryModel variantCategoryModel = (VariantCategoryModel) variantValueCategory.getSupercategories().get(0);
		parent.setName(variantCategoryModel.getName());
		parent.setHasImage(variantCategoryModel.getHasImage());

		final VariantValueCategoryData data = new VariantValueCategoryData();
		data.setName(variantValueCategory.getName());
		data.setSequence(variantValueCategory.getSequence());

		variantMatrixElementData.setVariantValueCategory(data);
		variantMatrixElementData.setParentVariantCategory(parent);

		final VariantOptionData variantOptionData = new VariantOptionData();
		final VariantOptionQualifierData variantOptionQualifierData = new VariantOptionQualifierData();
		variantOptionQualifierData.setImage(new ImageData());
		variantOptionData.setVariantOptionQualifiers(Arrays.asList(new VariantOptionQualifierData[]
		{ variantOptionQualifierData }));

		variantMatrixElementData.setVariantOption(variantOptionData);
	}

}