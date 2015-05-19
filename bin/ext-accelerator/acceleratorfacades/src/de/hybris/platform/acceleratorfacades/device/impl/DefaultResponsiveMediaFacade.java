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
package de.hybris.platform.acceleratorfacades.device.impl;


import de.hybris.platform.acceleratorfacades.device.ResponsiveMediaFacade;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.List;
import org.springframework.beans.factory.annotation.Required;


public class DefaultResponsiveMediaFacade implements ResponsiveMediaFacade
{
		private Converter<MediaContainerModel, List<ImageData>> mediaContainerConverter;

		@Override
		public List<ImageData> getImagesFromMediaContainer(final MediaContainerModel mediaContainerModel)
		{
			return getMediaContainerConverter().convert(mediaContainerModel);
		}

		public Converter<MediaContainerModel, List<ImageData>> getMediaContainerConverter()
		{
			return mediaContainerConverter;
		}

		@Required
		public void setMediaContainerConverter(Converter<MediaContainerModel, List<ImageData>> mediaContainerConverter)
		{
			this.mediaContainerConverter = mediaContainerConverter;
		}
}
