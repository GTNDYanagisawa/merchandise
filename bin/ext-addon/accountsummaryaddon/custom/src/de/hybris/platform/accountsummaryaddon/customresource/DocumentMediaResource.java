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
package de.hybris.platform.accountsummaryaddon.resource;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Date;
import java.util.UUID;

import de.hybris.platform.accountsummaryaddon.dto.DocumentMediaDTO;
import de.hybris.platform.accountsummaryaddon.model.DocumentMediaModel;
import de.hybris.platform.core.resource.link.LinkResource;
import de.hybris.platform.hmc.resource.HMCHistoryEntryResource;
import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.AbstractYResource;
import de.hybris.platform.webservices.resources.MediaContentResource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.http.MediaType;

/**
 * Generated resource class for type DocumentMedia first defined at extension accountsummary
 */
@SuppressWarnings("all")
public class DocumentMediaResource extends AbstractYResource<DocumentMediaModel>
{
	private String ACCOUNT_SUMMARY_FOLDER = "accountsummary";
	
	/**
	 * <i>Generated constructor</i> - for generic creation.
	 */
	public DocumentMediaResource()
	{
		super("DocumentMedia");
	}
	
	
	/**
	 * Generated HTTP method for covering DELETE requests.
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@DELETE
	public Response deleteDocumentMedia()
	{
		return createDeleteResponse().build();
	}
	
	/**
	 * Generated HTTP method for covering GET requests.
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@GET
	public Response getDocumentMedia()
	{
		return createGetResponse().build();
	}
	
	/**
	 * Convenience method which just delegates to {@link #getResourceValue()}
	 */
	public DocumentMediaModel getDocumentMediaModel()
	{
		return super.getResourceValue();
	}
	
	/**
	 *  Generated getter for sub resource of type {@link HMCHistoryEntryResource} for current root resource 
	 */
	@Path("/hmchistoryentries/{hmchistoryentry}")
	public AbstractYResource getHMCHistoryEntryResource(@PathParam("hmchistoryentry")  final String resourceKey)
	{
		final HMCHistoryEntryResource  resource  = resourceCtx.getResource(HMCHistoryEntryResource.class);
		resource.setResourceId(resourceKey );
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}
	
	/**
	 *  Generated getter for sub resource of type {@link LinkResource} for current root resource 
	 */
	@Path("/links/{link}")
	public AbstractYResource getLinkResource(@PathParam("link")  final String resourceKey)
	{
		final LinkResource  resource  = resourceCtx.getResource(LinkResource.class);
		resource.setResourceId(resourceKey );
		resource.setParentResource(this);
		passUniqueMember(resource);
		return resource;
	}


	@PUT
	@Path("data/")
	public Response putDocumentMedia(final InputStream inputStream, @Context HttpServletRequest servletRequest)
	{

		DocumentMediaModel documentMedia = getDocumentMediaModel();
		if(documentMedia == null){
			documentMedia = serviceLocator.getModelService().create(DocumentMediaModel.class);
			documentMedia.setCode(getResourceId());
			documentMedia.setFolder(serviceLocator.getMediaService().getFolder(ACCOUNT_SUMMARY_FOLDER));
		}
		// Persist document media before set media stream
		serviceLocator.getModelService().save(documentMedia);
 		serviceLocator.getMediaService().setStreamForMedia(documentMedia, new DataInputStream(inputStream)); 
	
 		// Update mime
		documentMedia.setMime(servletRequest.getContentType());
		serviceLocator.getModelService().save(documentMedia);
 		
		return getResponse().build();
	}
 
	
	@POST
	@Path("data/")
	public Response postDocumentMedia(final InputStream inputStream, @Context HttpServletRequest servletRequest){
		return putDocumentMedia(inputStream, servletRequest);
	}
	
	
	/**
	 * Generated HTTP method for covering PUT requests.
	 * @return {@link javax.ws.rs.core.Response}
	 */
	@PUT
	public Response putDocumentMedia(final DocumentMediaDTO dto)
	{
		return createPutResponse(dto).build();
	}
	
	/**
	 * Gets the {@link DocumentMediaModel} resource which is addressed by current resource request.
	 * @see de.hybris.platform.webservices.AbstractYResource#readResource(String)
	 */
	@Override
	protected DocumentMediaModel readResource(final String resourceId) throws Exception
	{
		DocumentMediaModel model = new DocumentMediaModel();
		model.setCode(resourceId);
		return (DocumentMediaModel) readResourceInternal(model);
	}
	
	/**
	 * Convenience method which just delegates to {@link #setResourceValue(DocumentMediaModel)}
	 */
	public void setDocumentMediaModel(final DocumentMediaModel value)
	{
		super.setResourceValue(value);
	}
	
}
