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
package de.hybris.platform.commercefacades.product;


/**
 * Image format mapping to media format qualifier.
 */
public interface ImageFormatMapping
{
	/**
	 * Get the media format qualifier for an image format. The image format is a useful frontend qualifier, e.g.
	 * "thumbnail"
	 * 
	 * @param imageFormat
	 *           the image format
	 * @return the media format qualifier
	 */
	String getMediaFormatQualifierForImageFormat(String imageFormat);
}
