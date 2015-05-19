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

import java.util.Map;


/**
 * Describes a single node from a graph.
 */
public interface NodeConfig
{

	/**
	 * Returns the type of this node.
	 * 
	 * @return type of node
	 */
	Class<?> getType();

	/**
	 * Returns all Properties which shall be taken to form an UID value for a concrete node value. For more information
	 * see {@link GraphNode#uidProperties()}
	 * 
	 * @return List of {@link PropertyConfig}
	 */
	PropertyConfig[] getUidProperties();

	/**
	 * Returns a mapping of all {@link PropertyConfig} for this node.
	 * 
	 * @return {@link PropertyConfig} mapping
	 */
	Map<String, PropertyConfig> getProperties();

}
