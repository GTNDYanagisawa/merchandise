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



/**
 * Provides context (runtime) information about current processed node.
 */
public interface NodeContext
{
	/**
	 * Returns the real processed distance up to current node. This also includes virtual nodes.
	 * 
	 * @return distance
	 */
	public int getRealDistance();

	/**
	 * Returns the general distance for the node this context belongs too. Virtual nodes are not included in distance
	 * calculation. Every virtual node which is part of the processed node path has the same distance as it's ancestor
	 * {@link NodeContext}.
	 * 
	 * @return distance
	 */
	public int getDistance();

	/**
	 * Returns the {@link NodeMapping} which is used for current node processing.
	 * 
	 * @return {@link NodeMapping}
	 */
	public NodeMapping getNodeMapping();

	/**
	 * Returns the raw value from source graph which shall be used for node processing.
	 * 
	 * @return source value
	 */
	public Object getSourceNodeValue();

	/**
	 * Returns the raw value from target graph which gets "merged" with the transformed source value from
	 * {@link #getSourceNodeValue()}
	 * 
	 * @return target value
	 */
	public Object getTargetNodeValue();

	/**
	 * Returns the {@link PropertyContext} which was used to create this {@link NodeContext}.
	 * <p/>
	 * Returned {@link PropertyContext} is bound to a runtime value, which is a valid GraphProperty (a
	 * {@link PropertyConfig} is available). It is the parent element of current processed runtime value, which itself is
	 * of meta-type GraphNode and represented by this {@link NodeContext} instance.
	 * 
	 * @return {@link PropertyContext}
	 */
	public PropertyContext getParentContext();

	/**
	 * Returns a list of {@link NodeContext} instances starting from a root context up to parent context of current
	 * processed node.
	 * 
	 * @return List of {@link NodeContext}
	 */
	public List<NodeContext> getProcessingPath();

	/**
	 * Returns the {@link GraphContext}
	 * 
	 * @return {@link GraphContext}
	 */
	public GraphContext getGraphContext();

}
