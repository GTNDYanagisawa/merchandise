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
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Context which is used for and during the process of transforming one object graph into another one.
 */
public class GraphContextImpl implements GraphContext
{
	private static final Logger log = Logger.getLogger(GraphContextImpl.class);

	private List<PropertyFilter> filterList = Collections.EMPTY_LIST;

	private int maxFoundDistance = -1;


	protected GraphConfigurationImpl graphConfigImpl = null;

	private AbstractGraphTransformer graphTransformer = null;

	//TODO; currently used as unsafe node cache in (ID->node)
	protected Map attributes = null;

	protected Map srcNodeValueMap = null;
	protected Map srcNodeIdMap = null;

	private boolean released = false;

	private final List<PropertyFilter> propFilters = new ArrayList();
	private final List<PropertyFilter> nodeFilters = new ArrayList();


	/**
	 * Constructor.
	 * 
	 * @param objGraph
	 *           {@link GraphTransformer}
	 */
	public GraphContextImpl(final GraphTransformer objGraph)
	{

		this.graphTransformer = (AbstractGraphTransformer) objGraph;
		if (filterList != null)
		{
			this.filterList = new ArrayList<PropertyFilter>(filterList);
		}
		this.srcNodeValueMap = new HashMap();
		this.srcNodeIdMap = new HashMap();
		this.attributes = new HashMap();
		//this.graphConfigImpl = new GraphConfigurationImpl(((DefaultGraphTransformer) objGraph).getNodeConfigMap());
		this.graphConfigImpl = new GraphConfigurationImpl(graphTransformer.getNodeMappingsMap());

	}

	@Override
	public GraphConfiguration getConfiguration()
	{
		return this.graphConfigImpl;
	}

	@Override
	public List<PropertyFilter> getNodeFilterList()
	{
		return this.nodeFilters;
	}

	@Override
	public List<PropertyFilter> getPropertyFilterList()
	{
		return this.propFilters;
	}

	@Override
	public boolean isReleased()
	{
		return released;
	}

	protected void setReleased(final boolean isReleased)
	{
		this.released = isReleased;
	}

	@Override
	public Map getAttributes()
	{
		return this.attributes;
	}


	/**
	 * Returns the most far away distance which was detected from start node. If graph processing is finished, it is the
	 * highest distance for that graph at all.
	 * 
	 * @return highest distance (snapshot)
	 */
	@Override
	public int getMaxDistance()
	{
		return this.maxFoundDistance;
	}

	public void setMaxDistance(final int distance)
	{
		this.maxFoundDistance = distance;
	}

	/**
	 * Returns the {@link GraphTransformer} which this context belongs to.
	 * 
	 * @return {@link GraphTransformer}
	 */
	@Override
	public AbstractGraphTransformer getGraphTransformer()
	{
		return this.graphTransformer;
	}

	/**
	 * Returns a Map of already processed nodes.
	 * 
	 * @return
	 */
	public Map<Object, Object> getProcessedNodes()
	{
		return this.srcNodeValueMap;
	}

	public Map<Object, Object> getProcessedNodesId()
	{
		return this.srcNodeIdMap;
	}


	/**
	 * Creates an initial {@link NodeContext} (root node context)
	 * 
	 * @param rootNodeLookup
	 * @param nodeMapping
	 * @param source
	 * @return
	 */
	protected NodeContext createRootNodeContext(final CachedClassLookupMap<NodeMapping> rootNodeLookup,
			final AbstractNodeMapping nodeMapping, final Object source)
	{
		NodeContext result = null;

		// configured nodeLookup for distance 1
		//final CachedClassLookupMap<NodeMapping> add = this.graphConfigImpl.getAllNodeMappings(1);
		final CachedClassLookupMap<NodeMapping> add = this.graphConfigImpl.getAllNodeMappings(1);

		// child nodes lookup is a merged result of current used node lookup and configured node lookup for next processing distance 
		final CachedClassLookupMap<NodeMapping> childNodesLookup = this.buildChildNodeLookup(rootNodeLookup, add);

		this.setRuntimeNodeMappings(0, rootNodeLookup);
		this.setRuntimeNodeMappings(1, childNodesLookup);

		if (log.isDebugEnabled())
		{
			log.debug("Added distance based runtime node lookup: " + 0 + ":" + rootNodeLookup.hashCode());
			log.debug("Added distance based runtime node lookup: " + 1 + ":" + childNodesLookup.hashCode());
		}


		// create result context
		result = new NodeContextImpl(this, null, nodeMapping, childNodesLookup, 0, 0, source);

		this.graphTransformer.nodeContextCreated(result);

		return result;
	}



	private final Map<Integer, CachedClassLookupMap<NodeMapping>> runtimeNodeMappings = new HashMap<Integer, CachedClassLookupMap<NodeMapping>>();

	/**
	 * Returns an already calculated {@link CachedClassLookupMap}.
	 * 
	 * @param distance
	 * @return
	 */
	protected CachedClassLookupMap<NodeMapping> getRuntimeNodeMappings(final int distance)
	{
		return this.runtimeNodeMappings.get(Integer.valueOf(distance));
	}

	protected void setRuntimeNodeMappings(final int distance, final CachedClassLookupMap<NodeMapping> nodeLookup)
	{
		this.runtimeNodeMappings.put(Integer.valueOf(distance), nodeLookup);
	}




	protected CachedClassLookupMap<NodeMapping> buildChildNodeLookup(final CachedClassLookupMap<NodeMapping> base,
			final CachedClassLookupMap<NodeMapping> add)
	{
		// by default result is same instance as base
		CachedClassLookupMap<NodeMapping> result = base;

		// merge only when 'add' is not same as 'base'  
		if (base != add)
		{
			// take static configuration 
			final Collection<NodeMapping> _add = add.getStaticMap().values();
			// and merge with 'base'
			result = buildChildNodeLookup(base, _add);
		}
		return result;
	}



	protected CachedClassLookupMap<NodeMapping> buildChildNodeLookup(final CachedClassLookupMap<NodeMapping> base,
			final Collection<NodeMapping> add)
	{
		// by default result is same instance as base
		CachedClassLookupMap<NodeMapping> result = base;

		// merge only when collection is not empty... 
		if (add != null && !add.isEmpty())
		{
			// ... create a new result instance 
			result = new CachedClassLookupMap<NodeMapping>();
			// ... all static elements (no elements which were dynamically found) of base  
			result.putAll(base.getStaticMap());
			// ... all passed NodeConfig elements 
			for (final NodeMapping nodeCfg : add)
			{
				result.put(nodeCfg.getSourceConfig().getType(), nodeCfg);
			}
		}
		return result;
	}




}
