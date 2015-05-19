/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.mediaconversion.integrationtest;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.InputStream;


enum TestMedia
{
	TULIP("/mediaconversion/test/tulip.png", "image/png", 152, 444, false), //
	TART("/mediaconversion/test/y.jpg", "image/jpeg", 714, 282, true), //
	KING("/mediaconversion/test/y2.jpg", "image/jpeg", 340, 377, false);

	private final String path;
	private final String mime;
	private final int width;
	private final int height;
	private final boolean hasExifMetaData;

	private TestMedia(final String path, final String mime, final int width, final int height, final boolean hasExifMetaData)
	{
		this.path = path;
		this.mime = mime;
		this.width = width;
		this.height = height;
		this.hasExifMetaData = hasExifMetaData;
	}

	boolean hasExifMetaData()
	{
		return hasExifMetaData;
	}

	int getWidth()
	{
		return width;
	}

	int getHeight()
	{
		return height;
	}

	String getMime()
	{
		return mime;
	}

	String getPath()
	{
		return path;
	}

	InputStream openStream()
	{
		return TestMedia.class.getResourceAsStream(this.getPath());
	}

	String getFileFormat()
	{
		return this.getMime().substring(this.getMime().indexOf('/') + 1).toUpperCase();
	}

	MediaModel createMedia(final ModelService modelService, final MediaService mediaService, final MediaContainerModel container)
	{
		final MediaModel ret = modelService.create(MediaModel.class);
		ret.setCode(this.name());
		ret.setMediaContainer(container);
		ret.setCatalogVersion(container == null ? TestDataFactory.someCatalogVersion(modelService) : //
				container.getCatalogVersion());
		modelService.save(ret);
		mediaService.setStreamForMedia(ret, this.openStream(), this.getPath().substring(this.getPath().indexOf('/') + 1),
				this.getMime());
		modelService.refresh(ret);
		return ret;
	}
}