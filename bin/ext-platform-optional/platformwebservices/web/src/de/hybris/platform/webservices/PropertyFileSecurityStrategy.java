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

import de.hybris.platform.jalo.JaloInternalException;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.ResponseBuilder;


public class PropertyFileSecurityStrategy extends AbstractSecurityStrategy
{

	private static final String PREFIX = "webservices.security";


	/**
	 * Gets the {@link ResponseBuilder} for this resource request or initially creates a new one.
	 * <p>
	 * When a new {@link ResponseBuilder} has to be created and security checking is enabled the currents user access to
	 * this resource gets verified before.
	 * <p>
	 * Security checking is only done once per request.
	 */
	@Override
	//TODO: use operation attribute within this method.
	public boolean isResourceOperationAllowed(final RestResource resource, final String operation)
	{
		List<String> allowedGroups = null;

		boolean isAllowed = true;

		isAllowed = false;
		final SecurityContext securityCtx = resource.getSecurityContext();

		// fetch all groups which have access to current resource
		allowedGroups = getAllAllowedGroups(resource.getRequest().getMethod(), resource.getClass());

		// no resource access when no groups are available  
		if (allowedGroups != null)
		{
			// check whether user has authenticated itself (auth-basic)
			if (securityCtx.getUserPrincipal() != null)
			{
				// ... if so, iterate over each allowed usergroup
				for (final String ag : allowedGroups)
				{
					// ... and check authenticated user against that group 
					isAllowed = securityCtx.isUserInRole(ag);
					if (isAllowed)
					{
						// ...if user is allowed, set jalosession properties
						final String userID = securityCtx.getUserPrincipal().getName();
						resource.getServiceLocator().getWsUtilService().setRequestUserIntoJaloSession(userID);
						break;
					}
				}
			}
			// user has not authenticated itself (anonymous)
			else
			{
				try
				{
					final Customer customer = UserManager.getInstance().getAnonymousCustomer();
					for (final String ag : allowedGroups)
					{
						isAllowed = ("ROLE_" + customer.getUID()).compareTo(ag) == 0;
						if (isAllowed)
						{
							break;
						}
					}
				}
				catch (final JaloInternalException e)
				{
					//silent catch; anonymous user doesn't exist
				}
			}
		}

		return isAllowed;
	}

	/**
	 * Get all allowed groups for a given resource(and all super-packages) and requested http method.
	 * 
	 * @param httpMethod
	 *           - GET, PUT or DELETE
	 * @param resource
	 * @return list with allowed groups
	 */
	public List<String> getAllAllowedGroups(final String httpMethod, final Class resource)
	{
		List<String> result = new ArrayList<String>();
		String resourcePath = resource.getName();

		String propertyValue = Config.getString(PREFIX + "." + resourcePath, null);
		if (propertyValue != null)
		{
			result.addAll(createSecurityConfig(httpMethod, propertyValue));
		}
		boolean process = true;
		while (process)
		{
			final int lastDotIndex = resourcePath.lastIndexOf('.');
			if (lastDotIndex == -1)
			{
				process = false;
			}
			else
			{
				resourcePath = resourcePath.substring(0, lastDotIndex);
				propertyValue = Config.getString(PREFIX + "." + resourcePath, null);
				if (propertyValue != null)
				{
					result.addAll(createSecurityConfig(httpMethod, propertyValue));
				}
			}
		}
		if (result.isEmpty())
		{
			result = null;
		}
		return result;
	}

	private static final Pattern PATTERN = Pattern.compile("(.*?)\\s*\\[(.*?)]");

	/**
	 * Create security config list.
	 * 
	 * @param rawValue
	 * @return list of allowed groups for http method
	 */
	private List<String> createSecurityConfig(final String httpMethod, final String rawValue)
	{
		final List<String> result = new ArrayList<String>();

		final String[] groups = rawValue.split("\\s*;\\s*");
		for (final String group : groups)
		{
			final Matcher matcher = PATTERN.matcher(group);
			if (matcher.matches())
			{
				final String groupName = matcher.group(1);
				final String methods = matcher.group(2);
				final String[] methodList = methods.split("\\s*,\\s*");

				for (final String method : methodList)
				{
					if ((method.toLowerCase()).compareTo(httpMethod.toLowerCase()) == 0)
					{
						result.add("ROLE_" + groupName);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Currently this strategy doesn't support dto security.
	 * <p>
	 * All dto are allowed.
	 */
	@Override
	public boolean isDtoOperationAllowed(final Class<?> dtoClass, final String operation)
	{
		return true;
	}

	/**
	 * Currently this strategy doesn't support resource commands security.
	 * <p>
	 * All commands are allowed.
	 */
	@Override
	public boolean isResourceCommandAllowed(final RestResource resource, final String command)
	{
		return true;
	}

	/**
	 * Currently this strategy doesn't support attribute security.
	 * <p>
	 * All attributes are allowed.
	 */
	@Override
	public boolean isAttributeOperationAllowed(final Class<?> attrDtoClass, final String attrQualifier, final String attrOperation)
	{
		return true;
	}

}
