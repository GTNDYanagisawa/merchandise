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
package de.hybris.platform.webservices.objectgraphtransformer;

import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.ServiceLocator;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.GraphContextImpl;

import javax.ws.rs.core.UriInfo;


public class YObjectGraphContext extends GraphContextImpl
{
	private Boolean graphWasNewlyCreated = null;

	private ServiceLocator serviceLocator = null;
	private UriInfo uriInfo = null;

	private AbstractResource requestResource = null;
	private boolean _isModelToDtoTransformation = false;

	/**
	 * @param objGraph
	 */
	public YObjectGraphContext(final GraphTransformer objGraph)
	{
		super(objGraph);
	}

	/**
	 * @return the serviceLocator
	 */
	public ServiceLocator getServices()
	{
		return serviceLocator;
	}

	/**
	 * @param serviceLocator
	 *           the serviceLocator to set
	 */
	protected void setServices(final ServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}


	public AbstractResource getRequestResource()
	{
		return requestResource;
	}

	public void setRequestResource(final AbstractResource requestResource)
	{
		this.requestResource = requestResource;
		this.uriInfo = requestResource.getUriInfo();
		this.serviceLocator = requestResource.getServiceLocator();
	}

	public Boolean getGraphWasNewlyCreated()
	{
		return graphWasNewlyCreated;
	}

	public void setGraphWasNewlyCreated(final Boolean graphWasNewlyCreated)
	{
		this.graphWasNewlyCreated = graphWasNewlyCreated;
	}

	public UriInfo getUriInfo()
	{
		return uriInfo;
	}

	public void setUriInfo(final UriInfo uriInfo)
	{
		this.uriInfo = uriInfo;
	}

	public boolean isModelToDtoTransformation()
	{
		return this._isModelToDtoTransformation;
	}

	public boolean isDtoToModelTransformation()
	{
		return !this._isModelToDtoTransformation;
	}

	public void setModelToDtoTransformation(final boolean value)
	{
		this._isModelToDtoTransformation = value;
	}


}
