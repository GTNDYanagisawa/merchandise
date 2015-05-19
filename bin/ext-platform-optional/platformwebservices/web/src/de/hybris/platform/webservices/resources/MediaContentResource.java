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
package de.hybris.platform.webservices.resources;

import java.io.DataInputStream;
import java.io.InputStream;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.webservices.AbstractResource;


/**
 * REST resource implementation for {@link MediaModel} binary content
 * <p/>
 * Allowed methods: POST, PUT
 */
public class MediaContentResource extends AbstractResource<MediaModel>
{
	protected MediaModel mediaModel = null;

	public void setMediaModel(final MediaModel mediaModel)
	{
		this.mediaModel = mediaModel;
	}

	/**
	 * HTTP POST
	 * 
	 * @return {@link Response}
	 */
	@POST
	public Response postMediaContent(final InputStream inputStream)
	{
		if (mediaModel == null)
		{
			getResponse().status(Response.Status.NOT_FOUND);
		}
		else
		{
			serviceLocator.getMediaService().setDataStreamForMedia(mediaModel, new DataInputStream(inputStream));
		}
		return getResponse().build();
	}

	/**
	 * HTTP PUT
	 * 
	 * @return {@link Response}
	 */
	@PUT
	public Response putMediaContent(final InputStream inputStream)
	{
		if (mediaModel == null)
		{
			getResponse().status(Response.Status.NOT_FOUND);
		}
		else
		{
			serviceLocator.getMediaService().setDataStreamForMedia(mediaModel, new DataInputStream(inputStream));
		}
		return getResponse().build();
	}

	@Override
	protected MediaModel readResource(final String resourceId)
	{
		// YTODO Auto-generated method stub
		return null;
	}
}
