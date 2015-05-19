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

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;


/**
 * Populate the product data with the product's primary image
 */
public class ProductPrimaryImagePopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductImagePopulator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		final MediaContainerModel primaryImageMediaContainer = getPrimaryImageMediaContainer(productModel);
		if (primaryImageMediaContainer != null)
		{
			final List<ImageData> imageList = new ArrayList<ImageData>();

			// Use the first container as the primary image
			addImagesInFormats(primaryImageMediaContainer, ImageDataType.PRIMARY, 0, imageList);

			for (final ImageData imageData : imageList)
			{
				if (imageData.getAltText() == null)
				{
					imageData.setAltText(productModel.getName());
				}
			}
			productData.setImages(imageList);
		}
	}

	protected MediaContainerModel getPrimaryImageMediaContainer(final SOURCE productModel)
	{
		final MediaModel picture = (MediaModel) getProductAttribute(productModel, ProductModel.PICTURE);
		if (picture != null)
		{
			return picture.getMediaContainer();
		}
		return null;
	}
}
