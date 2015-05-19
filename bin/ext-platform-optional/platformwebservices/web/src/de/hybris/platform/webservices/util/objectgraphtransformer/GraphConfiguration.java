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

import java.util.Collection;



/**
 * Graph specific configuration settings.
 */
public interface GraphConfiguration
{

	/**
	 * Adds a {@link NodeMapping} under a specific distance. Distance specifies the level of processing depth when this
	 * mapping shall become active.
	 * 
	 * @param distance
	 *           processing distance when {@link NodeMapping} shall become activated
	 * @param nodeMapping
	 *           {@link NodeMapping} to add
	 */
	public void addNodeMapping(int distance, NodeMapping nodeMapping);

	/**
	 * Adds a Collection of {@link NodeMapping} under a specific distance. Distance specifies the level of processing
	 * depth when this mapping shall become active.
	 * 
	 * @param distance
	 *           processing distance when {@link NodeMapping} shall become activated
	 * @param nodeMapping
	 *           {@link NodeMapping} to add
	 */
	public void addNodeMapping(int distance, Collection<NodeMapping> nodeMapping);


	/**
	 * Returns the {@link NodeMapping} which was configured on root-level base.
	 * <p/>
	 * This call equals {@link #getNodeMapping(int, Class)} with a distance level of zero.
	 * 
	 * @param type
	 *           type of node
	 * @return {@link NodeMapping}
	 */
	public NodeMapping getNodeMapping(Class<?> type);

	/**
	 * Returns the {@link NodeMapping} for a requested node type and requested distance when that mapping shall be used.
	 * A {@link NodeMapping} which was added under e.g. distance 2 is not returned here when requesting it under distance
	 * level 3 (this only works at runtime during graph processing)
	 * 
	 * @param distance
	 * @param type
	 * @return
	 */
	public NodeMapping getNodeMapping(int distance, Class<?> type);


}
