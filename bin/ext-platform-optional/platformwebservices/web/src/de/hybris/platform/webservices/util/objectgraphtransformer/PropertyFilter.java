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
package de.hybris.platform.webservices.util.objectgraphtransformer;



/**
 * General filter for a property
 */
public interface PropertyFilter
{
	/**
	 * Returns true when property value shall be filtered.
	 * 
	 * @param ctx
	 *           {@link GraphContext}
	 * @param value
	 *           property value
	 * @return true when value shall be filtered
	 */
	boolean isFiltered(PropertyContext ctx, Object value);
}
