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
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class ResponsiveImagePopulator implements Populator<MediaModel, ImageData>
{
	private Map<String, Integer> responsiveImageFormats;
	private final Pattern fileNameExtractorPattern = Pattern.compile("[_][0-9]+[xX][0-9]+[_]");
	private final Pattern widthExtractorPattern = Pattern.compile("[0-9]+[xX]");

	@Override
	public void populate(final MediaModel mediaModel, final ImageData imageData) throws ConversionException
	{
		final Matcher filenameMatcher = fileNameExtractorPattern.matcher(mediaModel.getCode());
		if (filenameMatcher.find())
		{
			final Matcher widthMatcher = widthExtractorPattern.matcher(filenameMatcher.group());
			if (widthMatcher.find())
			{
				final String matchingString = StringUtils.uncapitalize(widthMatcher.group());
				imageData.setWidth(Integer.valueOf(StringUtils.remove(matchingString, 'x')));
			}
		}
		else
		{
			if (getResponsiveImageFormats().containsKey(mediaModel.getMediaFormat().getQualifier()))
			{
				imageData.setWidth(getResponsiveImageFormats().get(mediaModel.getMediaFormat().getQualifier()));
			}
		}
	}

	public Map<String, Integer> getResponsiveImageFormats()
	{
		return responsiveImageFormats;
	}

	@Required
	public void setResponsiveImageFormats(final Map<String, Integer> responsiveImageFormats)
	{
		this.responsiveImageFormats = responsiveImageFormats;
	}
}
