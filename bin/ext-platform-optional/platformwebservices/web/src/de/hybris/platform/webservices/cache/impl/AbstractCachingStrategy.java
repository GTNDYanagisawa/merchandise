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
package de.hybris.platform.webservices.cache.impl;

import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.AbstractResponseBuilder;
import de.hybris.platform.webservices.cache.CachingStrategy;

import javax.ws.rs.core.EntityTag;


/**
 * Computes EntityTag based on RESOURCE's unique ID
 */
@Deprecated
public abstract class AbstractCachingStrategy<RESOURCE> implements CachingStrategy<RESOURCE>
{
	/**
	 * Computes EntityTag based on RESOURCE's unique ID
	 * 
	 * @return {@link EntityTag} EntityTag used to tell if resource has been modified since last visit or null to disable
	 *         caching
	 */
	public final EntityTag createETag(final AbstractResponseBuilder result)
	{
		final AbstractResource<RESOURCE> resource = result.getResource();
		if (resource == null)
		{
			return null;
		}
		final String uid = getUID(resource, (RESOURCE) result.getResourceValue());
		return uid == null ? null : new EntityTag(uid);
	}
}
