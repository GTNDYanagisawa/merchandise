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

import java.util.List;
import java.util.Map;


/**
 * Provides runtime information about current graph transformation process.
 * <p/>
 * Additionally allows various customizations/modification which differ from globally {@link GraphTransformer}
 * configuration and are used only for the transformation process which this context gets passed too.
 */
public interface GraphContext
{
	/**
	 * Returns highest found distance which was detected for all processed nodes up to currently processed one.
	 * 
	 * @return highest distance
	 */
	public int getMaxDistance();

	/**
	 * Returns {@link GraphTransformer} which this context is used for.
	 * 
	 * @return {@link GraphTransformer}
	 */
	public GraphTransformer getGraphTransformer();


	/**
	 * Returns configuration options for this graph. Configuration settings are only valid for one transformation which
	 * this context is bound too.
	 * 
	 * @return {@link GraphConfiguration}
	 */
	public GraphConfiguration getConfiguration();


	/**
	 * Returns a modifiable List of {@link PropertyFilter} instances. This chain of filters is applied to every processed
	 * property (including these ones which are treated as node).
	 * 
	 * @return List of {@link PropertyFilter} which are getting applied to every property
	 */
	// TODO: move to GraphConfiguration
	public List<PropertyFilter> getPropertyFilterList();

	/**
	 * Returns a modifiable List of {@link PropertyFilter} instances. This chain of filters is applied to every processed
	 * property which is treated as GraphNode.
	 * 
	 * @return list of {@link PropertyFilter} which are getting applied to node-properties only
	 */
	// TODO: move to GraphConfiguration
	public List<PropertyFilter> getNodeFilterList();

	/**
	 * A map of attributes which is free to use.
	 * 
	 * @return Map
	 */
	public Map getAttributes();

	/**
	 * Returns true when graph transformation process which used that context instance is finished. After a context is
	 * released it can not be used a second time for another transformation. However, it can still be used to gather some
	 * statistical data about the transformation.
	 * 
	 * @return true when context is released
	 */
	public boolean isReleased();


}
