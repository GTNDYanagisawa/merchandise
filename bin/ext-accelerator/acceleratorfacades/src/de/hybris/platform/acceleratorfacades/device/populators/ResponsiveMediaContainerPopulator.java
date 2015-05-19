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
package de.hybris.platform.acceleratorfacades.device.populators;


import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ResponsiveMediaContainerPopulator implements Populator<MediaContainerModel, List<ImageData>>
{
	private static final Logger LOG = Logger.getLogger(ResponsiveMediaContainerPopulator.class);
	private Converter<MediaModel, ImageData> imageConverter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final MediaContainerModel source, final List<ImageData> target) throws ConversionException
	{
		populateImages(source, target, ImageDataType.GALLERY); //GALLERY by default
	}

	/**
	 * Populating images for container
	 * 
	 * @param source
	 * @param target
	 */
	protected void populateImages(final MediaContainerModel source, final List<ImageData> target, final ImageDataType imageType)
	{
		final Collection<MediaModel> mediaModels = source.getMedias();
		if (CollectionUtils.isNotEmpty(mediaModels))
		{
			for (final MediaModel media : mediaModels)
			{
				if (media != null)
				{
					final ImageData imageData = getImageConverter().convert(media);
					imageData.setImageType(imageType);
					target.add(imageData);
				}
			}
			sortMediasBasedOnWidth(target);
		}
		else
		{
			LOG.info("No medias found for this media container.");
		}
	}

	protected List<ImageData> sortMediasBasedOnWidth(final List<ImageData> mediaDataList)
	{
		if (CollectionUtils.isNotEmpty(mediaDataList))
		{
			Collections.sort(mediaDataList, new Comparator<ImageData>()
			{
				@Override
				public int compare(final ImageData imageData1, final ImageData imageData2)
				{
					return (imageData1.getWidth()).compareTo(imageData2.getWidth());
				}
			});
		}
		return mediaDataList;
	}

	/**
	 * @return the imageConverter
	 */
	public Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	/**
	 * @param imageConverter
	 *           the imageConverter to set
	 */
	@Required
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

}
