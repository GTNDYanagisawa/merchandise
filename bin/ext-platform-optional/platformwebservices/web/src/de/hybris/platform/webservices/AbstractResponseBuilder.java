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

import java.lang.reflect.Constructor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import de.hybris.platform.webservices.dto.UriDTO;


public abstract class AbstractResponseBuilder<RESOURCE, REQUEST, RESPONSE> implements YResponseBuilder
{

	enum Operation
	{
		CREATE, READ, UPDATE, DELETE, CREATE_OR_UPDATE
	}

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AbstractResponseBuilder.class);

	private AbstractResource resource = null;
	private REQUEST reqEntity = null;
	private RESPONSE respEntity = null;

	// wrapped jersey ResponseBuilder
	private ResponseBuilder response = null;

	// servicelocator: a locator for available services 
	private ServiceLocator services;

	protected Operation operation = null;


	protected AbstractResponseBuilder(final Operation operation)
	{
		// NOP
		// but setResource(...) must be invoked before any other operation is executed
		this.operation = operation;
	}

	protected AbstractResponseBuilder(final AbstractResource resource, final Operation operation)
	{
		this(operation);
		this.setResource(resource);
	}

	public AbstractResource getResource()
	{
		return resource;
	}

	public void setResource(final AbstractResource resource)
	{
		this.response = resource.getResponse();
		this.services = resource.getServiceLocator();
		this.resource = resource;
	}

	public ServiceLocator getServiceLocator()
	{
		return this.services;
	}


	public RESOURCE getResourceValue()
	{
		return (RESOURCE) this.resource.getResourceValue();
	}


	public void setRequestValue(final REQUEST requestEntity)
	{
		this.reqEntity = requestEntity;
	}

	public REQUEST getRequestValue()
	{
		return this.reqEntity;
	}


	public void processRequest()
	{
		this.processRequest(false);
	}

	public void processRequest(final boolean suppressNotFound)
	{
		// get addressed resource value (resource entity)
		final RESOURCE resourceEntity = this.getResourceValue();

		// if there is such one or response processing is forced ...
		if (resourceEntity != null || suppressNotFound)
		{
			// ... create response entity (does not mean that a resource entity gets created)
			// type of result entity can be completely different to that one of resource
			// (POST & COMMANDS ... have an influence )
			try
			{
				this.respEntity = this.createResponseEntity(resourceEntity, reqEntity);
			}
			catch (final Exception e)
			{
				this.processException("Error processing request", e);
			}
		}
		// otherwise return a "resource not found"
		else
		{
			//setResponseStatus(404);
			getResponse().status(404);
		}
	}



	protected abstract RESPONSE createResponseEntity(final RESOURCE resourceEntity, final REQUEST requestEntity) throws Exception;


	/**
	 * Returns the wrapped {@link ResponseBuilder} which is taken for building a {@link Response}
	 * 
	 * @return {@link ResponseBuilder}
	 */
	public ResponseBuilder getResponse()
	{
		return this.response;
	}


	/**
	 * Throws a {@link WebApplicationException} which interrupts any current processing and gives full control to jersey.
	 * But before passed {@link Exception} gets evaluated and used to produce nice client output and notify loggers.
	 * 
	 * @param msg
	 *           a message which shall be given to client as headline
	 * @param exception
	 *           the exception which is taken to produce a short client error message
	 * @throws WebApplicationException
	 */
	protected void processException(final String msg, final Exception exception) throws WebApplicationException
	{
		getResource().processException(msg, exception);
	}

	@Override
	public Response build()
	{
		// pass response entity to wrapped responsebuilder
		if (this.respEntity != null)
		{
			getResponse().entity(respEntity);
		}

		// build the Response
		//final Response result = getResponse().status(this.respStatus).build();
		final Response result = getResponse().build();
		return result;
	}

	@Override
	public Response build(final Class<?> wrapperType)
	{
		// the response entity is not used directly, but gets wrapped into this instance
		// generally this is used to wrap plain collections into collection decorator DTOs 
		Object wrappedEntity = null;
		if (this.respEntity != null)
		{
			// type of unwrapped response entity is used ...
			final Class<?> respEntityClass = this.respEntity.getClass();
			try
			{
				// ... to find appropriate constructor of 'wrapperType'
				for (final Constructor<?> c : wrapperType.getDeclaredConstructors())
				{
					if (c.getParameterTypes().length == 1 && c.getParameterTypes()[0].isAssignableFrom(respEntityClass))
					{
						wrappedEntity = c.newInstance(this.respEntity);

						// set URI property if supported ... 
						if (wrappedEntity instanceof UriDTO)
						{
							final UriDTO uriDto = (UriDTO) wrappedEntity;
							// ... but only when value was not set before
							if (uriDto.getUri() == null)
							{
								uriDto.setUri(resource.getUriInfo().getRequestUri().toString());
							}
						}
						break;
					}
				}

				// ... if no constructor is available, we can't do anything
				if (wrappedEntity == null)
				{
					throw new InternalServerErrorException(wrapperType
							+ " does not have a constructor which supports parameter of type " + respEntityClass);
				}
			}
			// stop processing if no constructor is found or some other kind of problem occurred
			catch (final Exception e)
			{
				processException("Error creating instance of type " + wrapperType + " with parameter of type " + respEntityClass, e);
			}

			// pass created wrapper entity to original response builder
			getResponse().entity(wrappedEntity);
		}

		// and build response
		//final Response result = getResponse().status(this.respStatus).build();
		final Response result = getResponse().build();
		return result;
	}

	public RESPONSE getRespEntity()
	{
		return respEntity;
	}


}
