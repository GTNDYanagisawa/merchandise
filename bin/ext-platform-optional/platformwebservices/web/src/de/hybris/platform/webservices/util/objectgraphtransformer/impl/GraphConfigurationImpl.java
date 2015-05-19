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

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphConfiguration;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphException;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Manages runtime access to various {@link NodeMapping} instances. {@link GraphTransformer} already provides access to
 * {@link NodeMapping} instances but processing runtime adds dynamic behavior like different {@link NodeMapping}
 * instances for same node types based on depth of current processing (distance) or current processed property.
 */
public class GraphConfigurationImpl implements GraphConfiguration
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(GraphConfigurationImpl.class);

	// static node configuration
	// maps 'processing depth' -> 'custom node configuration map'
	private List<CachedClassLookupMap<NodeMapping>> staticNodeConfig = null;

	private CachedClassLookupMap<NodeMapping> baseConfig = null;


	/**
	 * Constructor.
	 * 
	 * @param baseConfiguration
	 */
	public GraphConfigurationImpl(final CachedClassLookupMap<NodeMapping> baseConfiguration)
	{
		this.baseConfig = baseConfiguration;
		this.staticNodeConfig = new ArrayList<CachedClassLookupMap<NodeMapping>>();
	}

	public CachedClassLookupMap<NodeMapping> getAllNodeMappings(final int distance)
	{
		if (distance < 0)
		{
			throw new GraphException("Can't manage a node configuration for a distance below zero (" + distance + ")");
		}

		if (this.staticNodeConfig.isEmpty())
		{
			this.staticNodeConfig.add(new CachedClassLookupMap<NodeMapping>(baseConfig));
		}

		while (this.staticNodeConfig.size() < distance + 1)
		{
			this.staticNodeConfig.add(new CachedClassLookupMap<NodeMapping>());
		}

		final CachedClassLookupMap<NodeMapping> result = this.staticNodeConfig.get(distance);
		return result;
	}



	@Override
	public void addNodeMapping(final int distance, final NodeMapping nodeMapping)
	{
		this.getAllNodeMappings(distance).put(nodeMapping.getSourceConfig().getType(), nodeMapping);
	}

	@Override
	public void addNodeMapping(final int distance, final Collection<NodeMapping> nodeMappingList)
	{
		final CachedClassLookupMap<NodeMapping> map = this.getAllNodeMappings(distance);
		for (final NodeMapping nodeMapping : nodeMappingList)
		{
			map.put(nodeMapping.getSourceConfig().getType(), nodeMapping);
		}
	}

	@Override
	public NodeMapping getNodeMapping(final Class<?> type)
	{
		return getNodeMapping(0, type);
	}

	@Override
	public NodeMapping getNodeMapping(final int distance, final Class<?> type)
	{
		final NodeMapping result = this.getAllNodeMappings(distance).get(type);
		return result;
	}



}
