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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.security.AccessManager;
import de.hybris.platform.jalo.security.UserRight;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;

import org.apache.log4j.Logger;


/**
 * WS security system is integrated with a type based (hmc managed) security configuration.
 */
public class AccessManagerSecurityStrategy extends AbstractSecurityStrategy
{
	private static final Logger LOG = Logger.getLogger(AccessManagerSecurityStrategy.class);

	@Override
	public boolean isResourceOperationAllowed(final RestResource resource, final String operation)
	{
		final AccessManager accessManager = AccessManager.getInstance();
		final TypeManager typeManager = TypeManager.getInstance();

		final Object model = resource instanceof AbstractCollectionResource ? null : resource.getResourceValue();
		String composedTypeName = null;
		// if we try to get subtype from supertype list (e.g. ../users/"customer") the subtype is checked.
		//single resource
		if (model != null && ItemModel.class.isAssignableFrom(model.getClass()) && operation.equals(AccessManager.READ))
		{
			composedTypeName = getModelService().getModelType(model);
		}
		//collection resource
		else
		{
			composedTypeName = ((AbstractYResource) resource).getResourceType(); // getComposedTypeName();
		}
		final ComposedType composedType = typeManager.getComposedType(composedTypeName);
		final UserRight userRight = accessManager.getUserRightByCode(operation);
		final boolean isAllowed = composedType.checkTypePermission(userRight);

		return isAllowed;
	}

	@Override
	public boolean isDtoOperationAllowed(final Class<?> objectClass, final String operation)
	{
		final ComposedType composedType = getComposedType(objectClass);
		if (composedType == null)
		{
			//all classes(except dtoClasses) are by default allowed.
			return true;
		}
		//dtoClass case
		final AccessManager accessManager = AccessManager.getInstance();
		final UserRight userRight = accessManager.getUserRightByCode(operation);
		final boolean isAllowed = composedType.checkTypePermission(userRight);

		return isAllowed;
	}

	@Override
	public boolean isAttributeOperationAllowed(final Class<?> objectClass, final String attrQualifier, final String operation)
	{
		final ComposedType composedType = getComposedType(objectClass);
		if (composedType == null)
		{
			//all classes(except dtoClasses) are by default allowed.
			return true;
		}
		//dtoClass case
		final AccessManager accessManager = AccessManager.getInstance();
		final UserRight userRight = accessManager.getUserRightByCode(operation);

		AttributeDescriptor attrDescriptor = null;
		if (composedType.hasAttribute(attrQualifier))
		{
			try
			{
				attrDescriptor = composedType.getAttributeDescriptor(attrQualifier);
			}
			catch (final JaloItemNotFoundException exception)
			{
				//fields that have modifier "private" set to true are denied.
				LOG.warn("Field: " + composedType.getCode() + "#" + attrQualifier
						+ " is marked as a private attribute(items.xml) and thus is not exposed within web service response.");
				return false;
			}
		}
		// attributes that don't originate from hybris type system are always available. For instance 'uri'.
		else
		{
			return true;
		}

		return attrDescriptor.checkTypePermission(userRight);
	}

	@Override
	public boolean isResourceCommandAllowed(final RestResource resource, final String command)
	{
		//TODO: currently not used.
		return true;
	}
}
