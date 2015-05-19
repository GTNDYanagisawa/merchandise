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

import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;

/**
 * Populate the product data with the products classification data
 */
public class ProductClassificationPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET>
{
	private ClassificationService classificationService;
	private Populator<FeatureList, ProductData> productFeatureListPopulator;
	
	protected ClassificationService getClassificationService()
	{
		return classificationService;
	}

	@Required
	public void setClassificationService(final ClassificationService classificationService)
	{
		this.classificationService = classificationService;
	}

	protected Populator<FeatureList, ProductData> getProductFeatureListPopulator()
	{
		return productFeatureListPopulator;
	}

	@Required
	public void setProductFeatureListPopulator(final Populator<FeatureList, ProductData> productFeatureListPopulator)
	{
		this.productFeatureListPopulator = productFeatureListPopulator;
	}

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		final FeatureList featureList = getClassificationService().getFeatures(productModel);
		if (featureList != null && !featureList.getFeatures().isEmpty())
		{
			getProductFeatureListPopulator().populate(featureList, productData);
		}
	}
}
