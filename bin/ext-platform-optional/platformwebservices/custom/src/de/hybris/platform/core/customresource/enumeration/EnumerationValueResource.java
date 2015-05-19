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
import javax.ws.rs.core.Response;

import de.hybris.platform.core.dto.enumeration.EnumerationValueDTO;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.webservices.AbstractYResource;


/**
 * REST resource implementation for {@link EnumerationValueModel}
 * <p/>
 * Allowed methods: GET, PUT, DELETE
 */
public class EnumerationValueResource extends AbstractYResource<EnumerationValueModel>
{

	public EnumerationValueResource()
	{
		super("EnumerationValue");
	}

	/**
	 * HTTP GET
	 * 
	 * @return {@link Response}
	 */
	@GET
	public Response getEnumerationValue()
	{
		return createGetResponse().build();
	}

	/**
	 * HTTP PUT
	 * 
	 * @return {@link Response}
	 */
	@PUT
	public Response putEnumerationValue(final EnumerationValueDTO enumerationValueDto) throws ConsistencyCheckException
	{
		return createPutResponse(enumerationValueDto).build();
	}

	@Override
	public EnumerationValueModel createResource(final Object dto)
	{
		// EnumerationValues can't be handled in a generic way like other models
		// reason: 'itemtype' property must be set but actually property is read-only
		// therefore when creating a new enumvalue, manager has to be used
		final EnumerationValueDTO edto = ((EnumerationValueDTO) dto);
		final EnumerationValueModel result = serviceLocator.getWsUtilService().createEnumerationValue(
				this.enumerationTypeModel.getCode(), edto.getCode(), edto.getName());
		return result;
	}

	/**
	 * HTTP DELETE
	 * 
	 * @return {@link Response}
	 */
	@DELETE
	public Response deleteEnumerationValue()
	{
		return createDeleteResponse().build();
	}

	/**
	 */
	@Override
	protected EnumerationValueModel readResource(final String resourceId) throws RuntimeException
	{
		return serviceLocator.getTypeService().getEnumerationValue(enumerationTypeModel.getCode(), resourceId);
	}



	// CUSTOM STUFF
	private EnumerationMetaTypeModel enumerationTypeModel = null;


	/**
	 * @return the enumerationTypeModel
	 */
	public EnumerationMetaTypeModel getEnumerationType()
	{
		return enumerationTypeModel;
	}

	/**
	 * @param enumerationTypeModel
	 *           the enumerationTypeModel to set
	 */
	public void setEnumerationType(final EnumerationMetaTypeModel enumerationTypeModel)
	{
		this.enumerationTypeModel = enumerationTypeModel;
	}


}
