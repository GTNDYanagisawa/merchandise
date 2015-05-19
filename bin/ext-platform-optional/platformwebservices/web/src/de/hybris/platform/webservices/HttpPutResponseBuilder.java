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
package de.hybris.platform.webservices;

import javax.ws.rs.core.Response.Status;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;


public class HttpPutResponseBuilder<RESOURCE, REQ_RESP> extends AbstractYResponseBuilder<RESOURCE, REQ_RESP, REQ_RESP>
{
	public HttpPutResponseBuilder()
	{
		super(Operation.CREATE_OR_UPDATE);
	}

	public HttpPutResponseBuilder(final AbstractYResource resource)
	{
		super(resource, Operation.CREATE_OR_UPDATE);
	}


	@Override
	protected REQ_RESP createResponseEntity(RESOURCE resrcEntity, final REQ_RESP reqEntity) throws Exception
	{
		// BEFORE invocation 
		// default implementation delegates to AbstractResource#beforePut
		this.beforeProcessing(reqEntity, resrcEntity);

		if (resrcEntity == null)
		{
			// CREATE invocation
			// default implementation delegates to AbstractResource#createPutResource
			resrcEntity = this.createResource(reqEntity);

			// CREATED when a result is available 
			if (resrcEntity != null)
			{
				super.operation = Operation.CREATE;
			}
		}

		// merge request entity (DTO) into resource entity (MODEL) 
		resrcEntity = this.dtoToModel(reqEntity, resrcEntity);

		final boolean isCreated = super.operation.equals(Operation.CREATE);

		try
		{
			// CREATE/UPDATE invocation
			// default implementation delegates to AbstractResource#createOrUpdatePutResource
			this.createOrUpdateResource(reqEntity, resrcEntity, isCreated);
		}
		catch (final ModelSavingException e)
		{
			final String msg = "Error " + ((isCreated) ? "creating" : "updating") + " resource with PUT";
			throw new BadRequestException(msg, e);
		}

		// set response status code
		getResponse().status(isCreated ? Status.CREATED : Status.OK);

		// AFTER invocation
		// default implementation delegates to AbstractResource#afterPutResource
		this.afterProcessing(reqEntity, resrcEntity, isCreated);

		// a plain PUT returns no entity, only a status code
		return null;
	}

	public void beforeProcessing(final REQ_RESP dto, final RESOURCE model)
	{
		this.getResource().beforePut(dto, model);
	}


	/**
	 * @param reqEntity
	 * @return
	 * @throws Exception
	 */
	public RESOURCE createResource(final REQ_RESP reqEntity) throws Exception
	{
		final RESOURCE result = (RESOURCE) this.getResource().createResource(reqEntity);
		return result;
	}

	/**
	 * Persist the value of this resource which always is of type {@link ItemModel}.
	 * <p/>
	 * This is done by using the generic {@link ModelService#save(Object)} approach.
	 * 
	 * @param reqEntity
	 *           dto which was used to create/update the model
	 * @param respEntity
	 *           the model which has to be persisted
	 * @param mustBeCreated
	 *           true when model must be newly created
	 * 
	 * @throws Exception
	 *            any kind of exception
	 */
	public void createOrUpdateResource(final REQ_RESP reqEntity, final RESOURCE respEntity, final boolean mustBeCreated)
			throws Exception
	{
		this.getResource().createOrUpdateResource(reqEntity, respEntity, mustBeCreated);
	}


	public void afterProcessing(final REQ_RESP dto, final RESOURCE resource, final boolean wasCreated)
	{
		this.getResource().afterPut(dto, resource, wasCreated);
	}




}
