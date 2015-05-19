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
 * "Connects" a single node-property from a source to a target.
 * <p/>
 * Provides mapping information which is needed to transform and/or copy the value of a source node property into a
 * target node.
 */
public interface PropertyMapping
{
	/**
	 * Returns the mapping id.
	 * <p/>
	 * Generally this is the bean-property name of the source property.
	 * 
	 * @return id String
	 */
	String getId();

	/**
	 * Returns the source configuration for this mapping.
	 * <p/>
	 * This is the property which is taken for reading.
	 * 
	 * @return {@link PropertyConfig}
	 */
	PropertyConfig getSourceConfig();

	/**
	 * Returns the target configuration for this mapping.
	 * <p/>
	 * This is the property which is taken for writing.
	 * 
	 * @return {@link PropertyConfig}
	 */
	PropertyConfig getTargetConfig();

	/**
	 * Returns the parent mapping which this mapping belongs too.
	 * <p/>
	 * For a {@link PropertyMapping} the parent mapping is always a {@link NodeMapping}.
	 * 
	 * @return {@link NodeMapping}
	 */
	NodeMapping getParentMapping();


	/**
	 * Returns the Processor which is used to transfer data from source to target for this mapping.
	 * <p/>
	 * For a {@link PropertyMapping} this is always a {@link PropertyProcessor}.
	 * 
	 * @return {@link PropertyProcessor}
	 */
	PropertyProcessor getProcessor();


	List<NodeMapping> getNewNodeMappings();


	List<PropertyFilter> getPropertyFilters();

	/**
	 * True when type of read-property (source node property) represents a GraphNode which implies further processing
	 * with deeper graph processing . False when property returns a plain value which is not registered as GraphNode.
	 * <p/>
	 * Mapping is treated as node when source property is a node (regardless whether target property is a node or not)
	 * 
	 * @return true when property value is a node.
	 */
	boolean isNode();

	/**
	 * True when this mapping is treated "virtual". For virtual property mappings only the write property is invoked
	 * regardless whether a read property is available or not. Virtual makes sense in combinations with
	 * {@link PropertyInterceptor}. A {@link PropertyFilter} do not have any influence on virtual properties.
	 * 
	 * @return true when virtual
	 */
	// TODO: virtual should become a PropertyConfig flag for setters on target-nodes
	boolean isVirtual();



}
