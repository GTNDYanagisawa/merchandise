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
package de.hybris.platform.commercefacades.storelocator.converters.populator;

import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.storelocator.data.OpeningScheduleData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.model.storelocator.StoreLocatorFeatureModel;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.storelocator.model.OpeningScheduleModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.storelocator.model.PointOfServiceModel} as source and
 * {@link de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData} as target type.
 */
public class PointOfServicePopulator implements Populator<PointOfServiceModel, PointOfServiceData>
{
	private static final Logger LOG = Logger.getLogger(PointOfServicePopulator.class);

	private Converter<MediaModel, ImageData> imageConverter;
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<OpeningScheduleModel, OpeningScheduleData> openingScheduleConverter;
	private MediaService mediaService;
	private MediaContainerService mediaContainerService;
	private ImageFormatMapping imageFormatMapping;
	private List<String> imageFormats;


	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	@Required
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

	protected Converter<OpeningScheduleModel, OpeningScheduleData> getOpeningScheduleConverter()
	{
		return openingScheduleConverter;
	}

	@Required
	public void setOpeningScheduleConverter(final Converter<OpeningScheduleModel, OpeningScheduleData> openingScheduleConverter)
	{
		this.openingScheduleConverter = openingScheduleConverter;
	}

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected MediaContainerService getMediaContainerService()
	{
		return mediaContainerService;
	}

	@Required
	public void setMediaContainerService(final MediaContainerService mediaContainerService)
	{
		this.mediaContainerService = mediaContainerService;
	}

	protected ImageFormatMapping getImageFormatMapping()
	{
		return imageFormatMapping;
	}

	@Required
	public void setImageFormatMapping(final ImageFormatMapping imageFormatMapping)
	{
		this.imageFormatMapping = imageFormatMapping;
	}

	protected List<String> getImageFormats()
	{
		return imageFormats;
	}

	@Required
	public void setImageFormats(final List<String> imageFormats)
	{
		this.imageFormats = imageFormats;
	}

	@Override
	public void populate(final PointOfServiceModel source, final PointOfServiceData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setName(source.getName());
		target.setDisplayName(source.getDisplayName());
		if (source.getAddress() != null)
		{
			target.setAddress(getAddressConverter().convert(source.getAddress()));
		}
		target.setDescription(source.getDescription());
		setGeoPoint(source, target);
		if (source.getMapIcon() != null)
		{
			target.setMapIcon(getImageConverter().convert(source.getMapIcon()));
		}

		if (source.getOpeningSchedule() != null)
		{
			target.setOpeningHours(openingScheduleConverter.convert(source.getOpeningSchedule()));
		}
		target.setStoreContent(source.getStoreContent());

		// Build up the list of images in the requested imageFormats
		final List<ImageData> storeImages = new ArrayList<ImageData>();
		final MediaContainerModel storeImageContainer = source.getStoreImage();
		if (storeImageContainer != null)
		{

			for (final String imageFormat : getImageFormats())
			{
				try
				{
					final String mediaFormatCode = getImageFormatMapping().getMediaFormatQualifierForImageFormat(imageFormat);
					if (mediaFormatCode != null)
					{
						final MediaFormatModel mediaFormat = getMediaService().getFormat(mediaFormatCode);
						if (mediaFormat != null)
						{
							try
							{
								final MediaModel media = getMediaContainerService().getMediaForFormat(storeImageContainer, mediaFormat);
								if (media != null)
								{
									final ImageData imageData = getImageConverter().convert(media);
									imageData.setFormat(imageFormat);
									storeImages.add(imageData);
								}
							}
							catch (final ModelNotFoundException mnfe)
							{
								LOG.info("Fetching store image media failed. Reason: '" + mnfe.getMessage() + "'.");
								continue;
							}
						}
					}
				}
				catch (final ModelNotFoundException mnfe)
				{
					LOG.info("Fetching store image media failed. Reason: '" + mnfe.getMessage() + "'.");
					continue;
				}
			}
		}
		target.setStoreImages(storeImages);



		// Add the store features
		final Map<String, String> features = new HashMap<String, String>();
		if (CollectionUtils.isNotEmpty(source.getFeatures()))
		{
			for (final StoreLocatorFeatureModel feature : source.getFeatures())
			{
				features.put(feature.getCode(), feature.getName());
			}
		}
		target.setFeatures(features);
	}

	protected void setGeoPoint(final PointOfServiceModel source, final PointOfServiceData target)
	{
		if (target.getGeoPoint() == null)
		{
			target.setGeoPoint(new GeoPoint());
		}
		if (source.getLatitude() != null)
		{
			target.getGeoPoint().setLatitude(source.getLatitude().doubleValue());
		}
		if (source.getLongitude() != null)
		{
			target.getGeoPoint().setLongitude(source.getLongitude().doubleValue());
		}
	}
}
