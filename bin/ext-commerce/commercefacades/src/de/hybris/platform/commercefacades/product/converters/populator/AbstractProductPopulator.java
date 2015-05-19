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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

import org.springframework.beans.factory.annotation.Required;

/**
 * Abstract base class for product populators.
 */
public abstract class AbstractProductPopulator<SOURCE extends ProductModel, TARGET extends ProductData> implements Populator<SOURCE, TARGET>
{
	private ModelService modelService;

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Get an attribute value from a product.
	 * If the attribute value is null and the product is a variant then the same attribute will be
	 * requested from the base product.
	 *
	 * @param productModel the product
	 * @param attribute    the name of the attribute to lookup
	 * @return the value of the attribute
	 */
	protected Object getProductAttribute(final ProductModel productModel, final String attribute)
	{
		final Object value = getModelService().getAttributeValue(productModel, attribute);
		if (value == null && productModel instanceof VariantProductModel)
		{
			final ProductModel baseProduct = ((VariantProductModel) productModel).getBaseProduct();
			if (baseProduct != null)
			{
				return getProductAttribute(baseProduct, attribute);
			}
		}
		return value;
	}

	/**
	 * Convert the object value to a string. If the object is null it is converted to a blank string.
	 *
	 * @param value the value to convert
	 * @return the value string
	 */
	protected String safeToString(final Object value)
	{
		return (value == null) ? "" : value.toString();
	}
}
