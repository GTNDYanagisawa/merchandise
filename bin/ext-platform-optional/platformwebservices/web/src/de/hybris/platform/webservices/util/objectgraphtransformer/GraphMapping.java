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

public interface GraphMapping
{
	/**
	 * Returns the source configuration for this mapping.
	 * 
	 * @return {@link GraphConfig}
	 */
	GraphConfig getSourceConfig();

	/**
	 * Returns the target configuration for this mapping.
	 * 
	 * @return {@link GraphConfig}
	 */
	GraphConfig getTargetConfig();

	/**
	 * Returns the Processor which is used to transfer data from source to target for this mapping.
	 */
	GraphProcessor getProcessor();


	/**
	 * Returns the {@link NodeMapping} for passed node type.
	 * 
	 * @param nodeType
	 *           type of node
	 * @return {@link NodeMapping} or null
	 */
	NodeMapping getNodeMapping(final Class nodeType);


	/**
	 * Adds a node to this graph.
	 * 
	 * @param cfg
	 *           {@link NodeMapping} to add
	 */
	void addNodeMapping(final NodeMapping cfg);

	/**
	 * Adds passed node and each available child node of that node to this Mapping.
	 * <p/>
	 * Passed class must be annotated with {@link GraphNode}. Child nodes are detected by introspecting each node
	 * property. When a node property type is annotated with {@link GraphNode} or is a Collection who's generic type is
	 * annotated with {@link GraphNode} then this property is recognized as child node. Additionally child nodes can be
	 * given directly as value of {@link GraphNode#addNodes()}.
	 * 
	 * @param graph
	 *           a {@link GraphNode} annotated class
	 */
	void addNodes(Class graph);



}
