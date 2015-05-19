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
package de.hybris.platform.core.customresource.enumeration;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import de.hybris.platform.core.dto.enumeration.EnumerationMetaTypeDTO;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.resource.enumeration.EnumerationValueResource;
import de.hybris.platform.webservices.AbstractYResource;


/**
 * REST resource implementation for {@link EnumerationMetaTypeModel}
 * <p/>
 * Allowed methods: GET, PUT, DELETE
 */
public class EnumerationMetaTypeResource extends AbstractYResource<EnumerationMetaTypeModel>
{
	public EnumerationMetaTypeResource()
	{
		super("EnumerationMetaType");
	}

	/**
	 * HTTP GET
	 * 
	 * @return {@link Response}
	 */
	@GET
	public Response getEnumerationType()
	{
		return createGetResponse().build();
	}

	/**
	 * HTTP PUT
	 * 
	 * @return {@link Response}
	 */
	@PUT
	public Response putEnumerationType(final EnumerationMetaTypeDTO enumerationTypeDto)
	{
		return createPutResponse(enumerationTypeDto).build();
	}

	/**
	 * HTTP DELETE
	 * 
	 * @return {@link Response}
	 */
	@DELETE
	public Response deleteEnumerationType()
	{
		return createDeleteResponse().build();
	}

	@Path("{enumvalueid}")
	public EnumerationValueResource getEnumerationValueResource(@PathParam("enumvalueid") final String enumvalueid)
	{
		final EnumerationMetaTypeModel enumerationType = getResourceValue();
		EnumerationValueResource result = null;
		if (enumerationType != null)
		{
			result = resourceCtx.getResource(EnumerationValueResource.class);
			result.setResourceId(enumvalueid);
			result.setEnumerationType(enumerationType);
		}
		return result;
	}

	/**
	 */
	@Override
	protected EnumerationMetaTypeModel readResource(final String resourceId) throws RuntimeException
	{
		return serviceLocator.getTypeService().getEnumerationType(resourceId);
	}


}
