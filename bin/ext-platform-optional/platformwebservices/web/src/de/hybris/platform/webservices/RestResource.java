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

import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;


public interface RestResource<RESOURCE>
{
	/**
	 * Returns a {@link ServiceLocator} for all available services.
	 * 
	 * @return {@link ServiceLocator}
	 */
	ServiceLocator getServiceLocator();

	CommandHandler getPutCommandHandler();

	/**
	 * Returns the {@link UriInfo} for current http-request.
	 * 
	 * @return {@link UriInfo}
	 */
	UriInfo getUriInfo();

	SecurityContext getSecurityContext();

	Request getRequest();

	RESOURCE getResourceValue();

	//
	//
	//	public void beforeDelete(final RESOURCE entity);
	//
	//	public void deleteResource(final RESOURCE entity);
	//
	//	public void afterDelete(final RESOURCE entity);
	//
	//
	//	public void beforePut(final Object reqEntity, final RESOURCE resourceEntity);
	//
	//	public Object createResource(final Object reqEntity);
	//
	//	public void createOrUpdateResource(final Object reqEntity, final RESOURCE respEntity, final boolean mustBeCreated) throws Exception;
	//
	//	public void afterPut(final Object reqEntity, final RESOURCE respEntity, final boolean isNewlyCreated);
	//
	//
	//	/**
	//	 * HTTP-POST-hook method for reading the POST resource value.
	//	 * <p/>
	//	 * Gets invoked by of {@link HttpPostResponseBuilder#readResource(Object)}.
	//	 * 
	//	 * @param <POST>
	//	 *           type of response entity
	//	 * @param reqEntity
	//	 *           the passed request entity
	//	 * @return response entity
	//	 */
	//	public <POST> POST readPostResource(final Object reqEntity);
	//
	//	/**
	//	 * HTTP-POST-listener which gets invoked before POST processing starts.
	//	 * <p/>
	//	 * Invoked by {@link HttpPostResponseBuilder#beforeProcessing(Object, Object)}
	//	 * 
	//	 * @param reqEntity
	//	 *           request entity (dto)
	//	 * @param respEntity
	//	 *           response entity (model), this is not null, when POST resource already exists
	//	 */
	//	public void beforePost(final Object reqEntity, final Object respEntity);
	//
	//
	//	/**
	//	 * HTTP-POST-hook method for creating the POST resource value.
	//	 * <p/>
	//	 * Invoked by {@link HttpPostResponseBuilder#createResource(Object)}
	//	 * 
	//	 * @param <POST>
	//	 *           type of response entity
	//	 * @param reqEntity
	//	 *           request entity (dto)
	//	 * @return created response entity (model)
	//	 */
	//	public <POST> POST createPostResource(final Object reqEntity);
	//
	//
	//
	//	/**
	//	 * HTTP-POST-hook method for updating the POST resource value.
	//	 * 
	//	 * @param reqEntity
	//	 *           request entity (dto)
	//	 * @param model
	//	 * @param mustBeCreated
	//	 * @throws Exception
	//	 */
	//	public void createOrUpdatePostResource(final Object reqEntity, final Object model, final boolean mustBeCreated)
	//			throws Exception;
	//
	//
	//	/**
	//	 * HTTP-POST-listener which gets invoked after POST processing is finished.
	//	 * 
	//	 * @param reqEntity
	//	 *           request entity (dto)
	//	 * @param respEntity
	//	 *           response entity (model)
	//	 * @param isNewlyCreated
	//	 *           true when response entity was newly created (false when only updated)
	//	 */
	//	public void afterPost(final Object reqEntity, final Object respEntity, final boolean isNewlyCreated);

}
