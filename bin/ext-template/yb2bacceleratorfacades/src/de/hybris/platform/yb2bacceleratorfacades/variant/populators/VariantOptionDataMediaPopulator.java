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
package de.hybris.platform.yb2bacceleratorfacades.variant.populators;

import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class VariantOptionDataMediaPopulator<SOURCE extends VariantProductModel, TARGET extends VariantOptionData> implements
		Populator<SOURCE, TARGET>
{

	private ImageFormatMapping acceleratorImageFormatMapping;

	private List<String> imageFormats;



	@Override
	public void populate(final VariantProductModel variantProductModel, final VariantOptionData variantOptionData)
			throws ConversionException
	{
		if (variantProductModel.getOthers() != null)
		{
			final Collection<VariantOptionQualifierData> qualifierDataList = new ArrayList<VariantOptionQualifierData>();


			for (final Iterator<MediaModel> mediaModelIter = variantProductModel.getOthers().iterator(); mediaModelIter.hasNext();)
			{
				final MediaModel mediaModel = mediaModelIter.next();

				final ImageData imageData = new ImageData();
				imageData.setUrl(mediaModel.getURL());
				imageData.setFormat(getMediaFormat(mediaModel.getMediaFormat().getName()));

				final VariantOptionQualifierData qualifierData = new VariantOptionQualifierData();
				qualifierData.setImage(imageData);

				qualifierDataList.add(qualifierData);

				variantOptionData.setVariantOptionQualifiers(qualifierDataList);

			}
		}
	}

	protected String getMediaFormat(final String format)
	{
		for (final String imageFormat : imageFormats)
		{
			if (format.equals(getAcceleratorImageFormatMapping().getMediaFormatQualifierForImageFormat(imageFormat)))
			{
				return imageFormat;
			}
		}
		return null;
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

	protected ImageFormatMapping getAcceleratorImageFormatMapping()
	{
		return acceleratorImageFormatMapping;
	}

	@Required
	public void setAcceleratorImageFormatMapping(final ImageFormatMapping acceleratorImageFormatMapping)
	{
		this.acceleratorImageFormatMapping = acceleratorImageFormatMapping;
	}

}