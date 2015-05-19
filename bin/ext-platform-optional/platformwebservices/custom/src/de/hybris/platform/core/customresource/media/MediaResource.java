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
package de.hybris.platform.core.customresource.media;

import de.hybris.platform.core.dto.media.MediaDTO;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.resource.link.LinkResource;
import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.AbstractYResource;
import de.hybris.platform.webservices.resources.MediaContentResource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


/**
 * Generated resource class for type Media first defined at extension core
 */
@SuppressWarnings("all")
public class MediaResource extends AbstractYResource<MediaModel>
{
	/**
	 * <i>Generated constructor</i> - for generic creation.
	 */
	public MediaResource()
	{
		super("Media");
	}


	/**
	 * Generated HTTP method for covering DELETE requests.
	 * 
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@DELETE
	public Response deleteMedia()
	{
		return createDeleteResponse().build();
	}

	/*
	*//**
	 * Generated getter for sub resource of type {@link HMCHistoryEntryResource} for current root resource
	 */
	/*
	 * @Path("/hmchistoryentries/{hmchistoryentry}") public AbstractYResource
	 * getHMCHistoryEntryResource(@PathParam("hmchistoryentry") final String resourceKey) { final HMCHistoryEntryResource
	 * resource = resourceCtx.getResource(HMCHistoryEntryResource.class); resource.setResourceId(resourceKey);
	 * resource.setParentResource(this); passUniqueMember(resource); return resource; }
	 */
	/**
	 * Generated getter for sub resource of type {@link LinkResource} for current root resource
	 */
	@Path("/links/{link}")
	public AbstractYResource getLinkResource(@PathParam("link") final String resourceKey)
	{
		final LinkResource resource = resourceCtx.getResource(LinkResource.class);
		resource.setResourceId(resourceKey);
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}

	@Path("data/")
	public AbstractResource getMediaContentResource()
	{
		final MediaContentResource result = resourceCtx.getResource(MediaContentResource.class);
		result.setMediaModel(this.getResourceValue());

		return result;
	}

	/**
	 * Generated HTTP method for covering GET requests.
	 * 
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@GET
	public Response getMedia()
	{
		return createGetResponse().build();
	}

	/**
	 * Convenience method which just delegates to {@link #getResourceValue()}
	 */
	public MediaModel getMediaModel()
	{
		return super.getResourceValue();
	}

	/**
	 * Generated HTTP method for covering PUT requests.
	 * 
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@PUT
	public Response putMedia(final MediaDTO dto)
	{
		return createPutResponse(dto).build();
	}

	/**
	 * Gets the {@link MediaModel} resource which is addressed by current resource request.
	 * 
	 * @see de.hybris.platform.webservices.AbstractYResource#readResource(String)
	 */
	@Override
	protected MediaModel readResource(final String resourceId) throws Exception
	{
		final MediaModel model = new MediaModel();
		model.setCode(resourceId);
		return (MediaModel) readResourceInternal(model);
	}

	/**
	 * Convenience method which just delegates to {@link #setResourceValue(MediaModel)}
	 */
	public void setMediaModel(final MediaModel value)
	{
		super.setResourceValue(value);
	}

}
