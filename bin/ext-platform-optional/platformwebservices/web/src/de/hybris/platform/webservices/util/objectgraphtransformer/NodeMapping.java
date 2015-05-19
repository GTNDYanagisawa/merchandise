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
 * "Connects" two nodes, a source and a target node and provides any information needed by {@link GraphTransformer} to
 * process the transformation.
 */
public interface NodeMapping
{
	/**
	 * Returns the source configuration for this mapping.
	 * 
	 * @return {@link NodeConfig}
	 */
	NodeConfig getSourceConfig();

	/**
	 * Returns the target configuration for this mapping
	 * 
	 * @return {@link NodeConfig}
	 */
	NodeConfig getTargetConfig();

	/**
	 * Returns the parent mapping which this mapping belongs too.
	 * 
	 * @return {@link GraphMapping}
	 */
	GraphMapping getParentMapping();


	/**
	 * /** Returns the Processor which is used to transfer data from source to target for this mapping.
	 * <p/>
	 * For a {@link NodeMapping} this is always a {@link NodeProcessor}
	 * 
	 * @return {@link NodeProcessor}
	 */
	NodeProcessor getProcessor();



	/**
	 * Returns a {@link NodeFactory} or null.
	 * 
	 * @return {@link NodeFactory}
	 */
	NodeFactory getNodeFactory();

	/**
	 * Returns a view of all property mappings.
	 * 
	 * @return property mapping
	 */
	Map<String, PropertyMapping> getPropertyMappings();

	/**
	 * Virtual nodes are treated with their parent node as "one" final node. Collections are for instance virtual.
	 * 
	 * @return
	 */
	boolean isVirtual();


}
