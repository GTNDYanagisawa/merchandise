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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Populate the product data with the product's gallery images
 */
public class ProductGalleryImagesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductImagePopulator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		// Collect the media containers on the product
		final List<MediaContainerModel> mediaContainers = new ArrayList<MediaContainerModel>();
		collectMediaContainers(productModel, mediaContainers);

		if (!mediaContainers.isEmpty())
		{
			final List<ImageData> imageList = new ArrayList<ImageData>();

			// fill our image list with the product's existing images
			if (productData.getImages() != null)
			{
				imageList.addAll(productData.getImages());
			}

			// Use all the images as gallery images
			int galleryIndex = 0;
			for (final MediaContainerModel mediaContainer : mediaContainers)
			{
				addImagesInFormats(mediaContainer, ImageDataType.GALLERY, galleryIndex++, imageList);
			}

			for (final ImageData imageData : imageList)
			{
				if (imageData.getAltText() == null)
				{
					imageData.setAltText(productModel.getName());
				}
			}

			// Overwrite the existing list of images
			productData.setImages(imageList);
		}
	}

	protected void collectMediaContainers(final ProductModel productModel, final List<MediaContainerModel> list)
	{
		final List<MediaContainerModel> galleryImages = (List<MediaContainerModel>) getProductAttribute(productModel,
				ProductModel.GALLERYIMAGES);
		if (galleryImages != null)
		{
			for (final MediaContainerModel galleryImage : galleryImages)
			{
				if (!list.contains(galleryImage))
				{
					list.add(galleryImage);
				}
			}

			if (galleryImages.isEmpty() && productModel instanceof VariantProductModel)
			{
				collectMediaContainers(((VariantProductModel) productModel).getBaseProduct(), list);
			}
		}
	}
}
