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
package de.hybris.platform.webservices.cache;

import de.hybris.platform.webservices.RestResource;



/**
 *
 */
@Deprecated
public interface CachingStrategy<RESOURCE>
{
	/**
	 * UID returned by the getUID method should return String computed from RESOURCE's important values. getUID method is
	 * used to tell if any of RESOURCE's important values has changed
	 * 
	 * @return unique ID for given RESOURCE
	 */
	String getUID(RestResource resource, RESOURCE resourceValue);
}
