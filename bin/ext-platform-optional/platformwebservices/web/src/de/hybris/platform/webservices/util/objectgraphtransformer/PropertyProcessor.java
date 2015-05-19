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
 * Processes a single property of a node. This includes copying ad well as conversion (when needed)
 */
public interface PropertyProcessor
{

	/**
	 * Processes a single property. Converts the property when needed and copies it from passed source node into passed
	 * target node.
	 * 
	 * @param pCtx
	 *           {@link PropertyContext}
	 * @param source
	 *           source node where property value is read from
	 * @param target
	 *           target node where property value gets written to
	 */
	void process(PropertyContext pCtx, Object source, Object target);

}
