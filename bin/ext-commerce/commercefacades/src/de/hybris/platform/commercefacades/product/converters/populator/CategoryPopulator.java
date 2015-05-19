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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Converter implementation for {@link de.hybris.platform.category.model.CategoryModel} as source and
 * {@link de.hybris.platform.commercefacades.product.data.CategoryData} as target type.
 */
public class CategoryPopulator extends CategoryUrlPopulator
{
	private Converter<MediaModel, ImageData> imageConverter;

	@Override
	public void populate(final CategoryModel source, final CategoryData target)
	{
		super.populate(source, target);
		final Collection<MediaModel> images = source.getLogo();
		if (CollectionUtils.isNotEmpty(images))
		{
			target.setImage(getImageConverter().convert(images.iterator().next()));
		}
	}

	public Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	@Required
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}
}
