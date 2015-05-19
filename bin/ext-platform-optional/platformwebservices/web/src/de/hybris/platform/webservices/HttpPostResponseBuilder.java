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


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import de.hybris.platform.core.PK.PKException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


/**
 * 
 * 
 * @param <RESOURCE>
 *           the type of the post resource (not that one of currently executed resource) which shall be exposed to
 *           client (model)
 */
public class HttpPostResponseBuilder<RESOURCE, REQ_RESP> extends AbstractYResponseBuilder<RESOURCE, REQ_RESP, REQ_RESP>
{
	private static final Logger LOG = Logger.getLogger(HttpPostResponseBuilder.class);

	private RESOURCE postResrcEntity = null;
	private boolean postResrcEntityResolved = false;


	public HttpPostResponseBuilder()
	{
		super(Operation.CREATE_OR_UPDATE);
	}

	public HttpPostResponseBuilder(final AbstractYResource resource)
	{
		super(resource, Operation.CREATE_OR_UPDATE);
	}

	// CODE ACTUALLY IS DOUBLED IN ABSTRACTRESOURCE!
	@Override
	public RESOURCE getResourceValue()
	{
		//return this.resourceValue;
		if (!this.postResrcEntityResolved)
		{
			this.postResrcEntityResolved = true;
			try
			{
				final REQ_RESP dto = getRequestValue();
				this.postResrcEntity = this.readResource(dto);
				this.postResrcEntityResolved = true;
			}
			catch (final Exception e)
			{
				final Set<Class> handled = new HashSet<Class>(Arrays.asList( //
						UnknownIdentifierException.class, //
						PKException.class, //
						ModelLoadingException.class, //
						JaloItemNotFoundException.class, //
						ClassCastException.class, //
						ModelNotFoundException.class, //for modelService.getByExample()
						IllegalArgumentException.class));

				if (handled.contains(e.getClass()))
				{
					LOG.debug(e.getMessage());
				}
				else
				{
					throw new InternalServerErrorException(e);
				}
			}
		}
		return postResrcEntity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.sandbox.AbstractResponseProcessor#createResponseEntity(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public final REQ_RESP createResponseEntity(RESOURCE postResrcEntity, final REQ_RESP reqEntity) throws Exception
	{
		// BEFORE invocation 
		// default implementation delegates to AbstractResource#beforePost
		this.beforeProcessing(reqEntity, postResrcEntity);

		if (postResrcEntity == null)
		{
			// CREATE invocation
			// default implementation delegates to AbstractResource#createPostResource
			postResrcEntity = this.createResource(reqEntity);

			// CREATED when a result is available 
			if (postResrcEntity != null)
			{
				super.operation = Operation.CREATE;
			}
		}

		// transform DTO (request entity) -> MODEL (resource entity) and persist
		// may update OPERATION flag
		postResrcEntity = this.dtoToModel(reqEntity, postResrcEntity);

		final boolean isCreated = operation.equals(Operation.CREATE);

		// create/update resource 
		try
		{
			// CREATE/UPDATE invocation
			// default implementation delegates to AbstractResource#createOrUpdatePostResource
			this.createOrUpdateResource(reqEntity, postResrcEntity, isCreated);
		}
		// some exceptions can be handled, others are just passed through 
		catch (final ModelSavingException e)
		{
			final String msg = "Error " + operation + " resource with POST";
			throw new BadRequestException(msg, e);
		}

		// set response status code
		getResponse().status(isCreated ? Status.CREATED : Status.OK);

		// AFTER invocation
		// default implementation delegates to AbstractResource#afterPostResource
		this.afterProcessing(reqEntity, postResrcEntity, isCreated);

		// transform MODEL (resource entity) -> DTO (response entity)
		// request DTO can't be used as it may represent only a part of the resource entity (MODEL)  
		final REQ_RESP result = (REQ_RESP) modelToDto(postResrcEntity, 1);

		return result;
	}

	/**
	 * Reads and returns the resource value which shall be modified/created by this POST request. Generally that value is
	 * not identical to that value returned by current addressed resource. POST generally is used for values which are
	 * only related in some kind to the addressed resource. (e.g. POST a Address to a /addresses)
	 * <p/>
	 * This implementation just delegates to {@link AbstractResource#readPostResource(Object)}
	 * 
	 */
	//@Override
	public RESOURCE readResource(final REQ_RESP dto) throws Exception
	{
		return (RESOURCE) this.getResource().readPostResource(dto);
	}

	/**
	 * Delegates to {@link AbstractResource#beforePost(Object, Object)}
	 * 
	 * @param resrcEntity
	 * @param dto
	 */
	public void beforeProcessing(final REQ_RESP dto, final RESOURCE resrcEntity)
	{
		this.getResource().beforePost(dto, resrcEntity);
	}

	/**
	 * Delegates to {@link AbstractResource#createPostResource(Object)}
	 * 
	 * @param reqEntity
	 * @return
	 * @throws Exception
	 */
	public RESOURCE createResource(final REQ_RESP reqEntity) throws Exception
	{
		final RESOURCE result = (RESOURCE) this.getResource().createPostResource(reqEntity);
		return result;
	}


	/**
	 * Delegates to {@link AbstractResource#createOrUpdatePostResource(Object, Object, boolean)}
	 * 
	 * @param reqEntity
	 *           request entity (dto)
	 * @param respEntity
	 *           response entity (model)
	 * @param mustBeCreated
	 *           whether response entity must be newly created (false when only an updated is needed)
	 * 
	 */
	public void createOrUpdateResource(final REQ_RESP reqEntity, final RESOURCE respEntity, final boolean mustBeCreated)
			throws Exception
	{
		getResource().createOrUpdatePostResource(reqEntity, respEntity, mustBeCreated);
	}

	/**
	 * Delegates to {@link AbstractResource#afterPost(Object, Object, boolean)}
	 * 
	 * @param reqEntity
	 *           request entity (dto)
	 * @param respEntity
	 *           response entity (model)
	 * @param wasNewlyCreated
	 *           true when response entity was newly created (otherwise updated only)
	 */
	public void afterProcessing(final REQ_RESP reqEntity, final RESOURCE respEntity, final boolean wasNewlyCreated)
	{
		this.getResource().afterPost(reqEntity, respEntity, wasNewlyCreated);
	}

	protected <T> T getParentResourceValue()
	{
		return (T) getResource().getResourceValue();
	}


}
