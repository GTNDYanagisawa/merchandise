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
package de.hybris.platform.webservices.util.objectgraphtransformer.impl;




import de.hybris.platform.webservices.util.objectgraphtransformer.GraphMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeFactory;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeProcessor;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


public abstract class AbstractNodeMapping implements NodeMapping
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AbstractNodeMapping.class);

	private NodeFactory nodeFactory = null;
	private NodeConfig sourceNodeConfig = null;
	private NodeConfig targetNodeConfig = null;
	protected Map<String, PropertyMapping> propertyMappings = null;
	private Map<String, PropertyMapping> propertyMappingsUnmodifiable = null;
	private NodeProcessor nodeProcessor = null;
	private boolean isVirtualNode = false;

	private boolean isNodeInitialized = false;
	private boolean isPropertiesInitialized = false;

	protected GraphMapping graph = null;


	protected AbstractNodeMapping(final GraphMapping graph)
	{
		this(graph, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param sourcetype
	 *           type of source node
	 * @param targetType
	 *           type of target node
	 */
	public AbstractNodeMapping(final GraphMapping graph, final NodeConfig sourcetype, final NodeConfig targetType)
	{
		super();

		this.graph = graph;
		this.propertyMappings = new LinkedHashMap<String, PropertyMapping>();
		this.propertyMappingsUnmodifiable = Collections.unmodifiableMap(propertyMappings);
		this.sourceNodeConfig = sourcetype;
		this.targetNodeConfig = targetType;

	}

	@Override
	public NodeFactory getNodeFactory()
	{
		return nodeFactory;
	}

	protected void setNodeFactory(final NodeFactory nodeFactory)
	{
		this.nodeFactory = nodeFactory;
	}

	@Override
	public NodeConfig getSourceConfig()
	{
		return sourceNodeConfig;
	}

	protected void setSourceConfig(final NodeConfig sourceNode)
	{
		this.sourceNodeConfig = sourceNode;
	}

	@Override
	public NodeConfig getTargetConfig()
	{
		return targetNodeConfig;
	}

	protected void setTargetConfig(final NodeConfig targetNode)
	{
		this.targetNodeConfig = targetNode;
	}

	@Override
	public Map<String, PropertyMapping> getPropertyMappings()
	{
		return this.propertyMappingsUnmodifiable;
	}

	/**
	 * Puts a new {@link PropertyMapping} to this node. If a property with same ID already exists it gets overwritten
	 * otherwise property simply gets added.
	 * 
	 * @param property
	 *           property to add
	 */
	public void putPropertyMapping(final PropertyMapping property)
	{
		this.propertyMappings.put(property.getId(), property);
		this.isPropertiesInitialized = false;
	}

	/**
	 * Removes a property from this node. Returns removed instance or null when no property with passed ID was available
	 * under this node.
	 * 
	 * @param propId
	 *           id of property
	 * @return {@link PropertyMapping}
	 */
	public PropertyMapping removePropertyMapping(final String propId)
	{
		return this.propertyMappings.remove(propId);
	}

	public void removeAllPropertyMappings()
	{
		this.removeAllPropertyMappings(null, null);
	}

	/**
	 * Removes a collection of properties. Accepts a list of affected properties as well as an exclusion list.
	 * 
	 * @param propertiesToRemove
	 *           list of properties which are affected, null or empty means 'all'
	 * @param propertiesToKeep
	 *           list of properties which are excluded from removal (whitelist)
	 */
	private void removeAllPropertyMappings(Collection<String> propertiesToRemove, final Collection<String> propertiesToKeep)
	{
		// no useful values just means 'all'
		if (propertiesToRemove == null || propertiesToRemove.isEmpty())
		{
			propertiesToRemove = this.propertyMappings.keySet();
		}

		// in case a whitelist is passed removal list has to be modified
		if (propertiesToKeep != null && !propertiesToKeep.isEmpty())
		{
			propertiesToRemove = new HashSet<String>(propertiesToRemove);
			propertiesToRemove.removeAll(propertiesToKeep);
		}

		// removes each single property
		for (final Iterator<String> iter = propertiesToRemove.iterator(); iter.hasNext();)
		{
			iter.next();
			iter.remove();
		}
	}

	/**
	 * Returns the {@link NodeProcessor} which is used for processing a concrete node value with this configuration
	 * 
	 * @return {@link AbstractNodeProcessor}
	 */
	@Override
	public NodeProcessor getProcessor()
	{
		return this.nodeProcessor;
	}

	public void setNodeProcessor(final NodeProcessor nodeProcessor)
	{
		this.nodeProcessor = nodeProcessor;
	}

	@Override
	public boolean isVirtual()
	{
		return this.isVirtualNode;
	}

	public void setVirtual(final boolean virtual)
	{
		this.isVirtualNode = virtual;
	}

	@Override
	public GraphMapping getParentMapping()
	{
		return this.graph;
	}

	@Override
	public String toString()
	{
		final String hash = Integer.toHexString(hashCode());
		final String result = this.getClass().getSimpleName() + "@" + hash + "(" + this.sourceNodeConfig.getType().getSimpleName()
				+ "->" + this.targetNodeConfig.getType().getSimpleName();
		return result;
	}


	/**
	 * Initializes this node.
	 * <p/>
	 * 
	 */
	public void initialize()
	{
		// step1: initialize node
		if (!this.isNodeInitialized)
		{
			this.isNodeInitialized = this.initializeNode();
		}

		if (!this.isPropertiesInitialized)
		{
			// step2: initialize properties; filter out invalid ones
			this.isPropertiesInitialized = initializeProperties();
		}
	}

	protected abstract boolean initializeNode();

	/**
	 * Initializes each {@link PropertyMapping} of this node.
	 * <p/>
	 * If {@link PropertyMapping} is already initialized, it gets skipped.<br/>
	 * If {@link PropertyMapping} is not initialized, their initializer method gets called. If initialization fails,
	 * property gets removed from that node.
	 * 
	 * @return true when initialization succeeds
	 */
	protected boolean initializeProperties()
	{
		// initialize/refresh properties (when not already done)
		for (final Iterator<PropertyMapping> iter = this.propertyMappings.values().iterator(); iter.hasNext();)
		{
			final PropertyMapping pMap = iter.next();
			if (pMap instanceof AbstractPropertyMapping)
			{
				final AbstractPropertyMapping aPropMap = (AbstractPropertyMapping) pMap;
				if (!aPropMap.isInitialized())
				{
					final boolean valid = aPropMap.initialize(DefaultPropertyMapping.COMPLIANCE_LEVEL_LOW);
					if (!valid)
					{
						iter.remove();
					}
				}
			}
		}
		return true;
	}

	public boolean isInitialized()
	{
		return this.isNodeInitialized && this.isPropertiesInitialized;
	}

	protected void setInitialized(final boolean initialized)
	{
		this.isNodeInitialized = initialized;
		this.isPropertiesInitialized = initialized;
	}

	protected boolean isDebugEnabled()
	{
		final Set<String> debugNodes = ((AbstractGraphTransformer) graph).getDebugNodes();
		final boolean enabled = debugNodes.isEmpty() || debugNodes.contains(this.sourceNodeConfig.getType().getSimpleName())
				|| debugNodes.contains(this.targetNodeConfig.getType().getSimpleName());
		return enabled;
	}

}
