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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.jalo.security.AccessManager;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;


public abstract class AbstractSecurityStrategy implements SecurityStrategy
{
	private ModelService modelService;

	// contains equivalents between http methods and access rights for type-based security check
	private static final Map<String, String> ACCESS_RIGHTS_MAP = new HashMap<String, String>();
	static
	{
		// static initialization; maps HTTP-Methods to hmc-AccessManager rights
		ACCESS_RIGHTS_MAP.put("GET", AccessManager.READ);
		ACCESS_RIGHTS_MAP.put("PUT_CHANGE", AccessManager.CHANGE);
		ACCESS_RIGHTS_MAP.put("PUT_CREATE", AccessManager.CREATE);
		ACCESS_RIGHTS_MAP.put("POST_CHANGE", AccessManager.CHANGE);
		ACCESS_RIGHTS_MAP.put("POST_CREATE", AccessManager.CREATE);
		ACCESS_RIGHTS_MAP.put("DELETE", AccessManager.REMOVE);
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Method performs model read security operations on the attribute level on the list of elements.
	 * 
	 * @param collection
	 *           The collection of objects that needs read security checking.
	 * 
	 * @return Collection of allowed attributes.
	 */
	public Collection<?> performDtoReadOperationAllowed(final Collection<?> collection)
	{
		//attribute security | type based attribute security level
		final Collection<Object> adjustedCollection = (collection instanceof Set) ? new HashSet<Object>() : new ArrayList<Object>();
		for (final Object object : collection)
		{
			if (isDtoOperationAllowed(object.getClass(), AccessManager.READ))
			{
				adjustedCollection.add(object);
			}
		}
		return adjustedCollection;
	}

	/**
	 * Method returns selected access operation.
	 * 
	 * @param httpMethod
	 *           GET, DELETE, POST, PUT.
	 * 
	 * @param isNew
	 *           If httpMethod is a POST or PUT then this attribute will decide that this is create or change operation.
	 * 
	 * @return the selected operation.
	 */
	public String getOperation(final String httpMethod, final boolean isNew)
	{
		//GET and DELETE
		String operation = convertHttpMethodToOperation(httpMethod);
		//PUT and POST (differentiation between create and change operation)
		if (operation == null)
		{
			operation = (isNew) ? convertHttpMethodToOperation(httpMethod + "_CREATE") : convertHttpMethodToOperation(httpMethod
					+ "_CHANGE");
		}

		return operation;
	}

	/**
	 * Method returns ComposedType for dtoClasses that contain GraphNode annotation and 'target' attribute. For other
	 * classes method returns null.
	 * 
	 * @param objectClass
	 *           Input Class object.
	 * 
	 * @return returns ComposedType for dtoClasses and modelClasses. For others returns null.
	 */
	protected ComposedType getComposedType(final Class<?> objectClass)
	{
		final TypeManager typeManager = TypeManager.getInstance();

		if (objectClass != null)
		{
			//if dto with GraphNode annotation
			if (objectClass.isAnnotationPresent(GraphNode.class))
			{
				// get annotation with node configurations
				final GraphNode graphNode = objectClass.getAnnotation(GraphNode.class);
				if (graphNode.target() != null)
				{
					return typeManager.getComposedType(modelService.getModelType(graphNode.target()));
				}
			}
		}
		//the class object doesn't have equivalent ComposedType
		return null;
	}

	/**
	 * Method returns right access operation for specified(customized) HTTP method.
	 * 
	 * @param httpMethod
	 *           GET(read), DELETE(delete), POST_CHANGE(change), POST_CREATE(create), PUT_CHANGE(change),
	 *           PUT_CREATE(create).
	 * 
	 * @return operation or null.
	 */
	private String convertHttpMethodToOperation(final String httpMethod)
	{
		return ACCESS_RIGHTS_MAP.get(httpMethod);
	}

}
