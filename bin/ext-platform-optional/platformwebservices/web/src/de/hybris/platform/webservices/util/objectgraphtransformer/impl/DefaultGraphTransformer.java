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

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphException;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProcessor;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeValueCreatedListener;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;


/**
 * Base class for object graph transformations.
 */
public class DefaultGraphTransformer extends AbstractGraphTransformer
{

	private static final Logger LOG = Logger.getLogger(DefaultGraphTransformer.class);	//NOPMD

	private NodeValueCreatedListener nodeCreatedListener = null;


	private static GraphProcessor DEFAULT_GRAPH_PROCESSOR = new GraphProcessorImpl();

	private final CollectionNodeMapping COLLECTION_NODE_MAPPING;


	protected final GraphProcessor graphProcessor = DEFAULT_GRAPH_PROCESSOR;


	/**
	 * Constructor. Creates an empty object graph definition.
	 */
	public DefaultGraphTransformer()
	{
		this(null);
	}

	/**
	 * Creates an object graph definition starting with passed class as root node type.
	 * 
	 * @param graph
	 *           type of graph root node
	 */
	public DefaultGraphTransformer(final Class graph)
	{
		super(new DefaultGraphConfig(), new DefaultGraphConfig());
		COLLECTION_NODE_MAPPING = new CollectionNodeMapping(this);
		if (graph != null)
		{
			this.addNodes(graph);
			this.initialize();
		}
	}


	@Override
	public <T> T transform(GraphContext ctx, final Object source, final T target)
	{
		// a source graph must be specified
		if (source == null)
		{
			throw new GraphException("No source graph to transform [null]", new NullPointerException());
		}

		// if no context is passed, a default one gets created
		if (ctx == null)
		{
			ctx = createGraphContext();
		}

		final T result = graphProcessor.process(ctx, source, target);

		return result;
	}

	@Override
	public void setNodeValueCreatedListener(final NodeValueCreatedListener listener)
	{
		this.nodeCreatedListener = listener;
	}

	@Override
	protected void performNodeCreated(final NodeContext nodeCtx, final Object node)
	{
		if (nodeCreatedListener != null)
		{
			this.nodeCreatedListener.performCreated(nodeCtx, node);
		}
	}

	@Override
	public void initialize()
	{
		// always provide a default NodeMapping for Collection nodes
		// custom collection mappings can be added nevertheless 
		this.nodeMappings.put(Collection.class, COLLECTION_NODE_MAPPING);
		COLLECTION_NODE_MAPPING.graph = this;

		// create NodeMappings for available nodes
		final Collection<NodeConfig> sourceGraphNodes = getSourceConfig().getNodes().values();
		for (final NodeConfig nodeCfg : sourceGraphNodes)
		{
			if (!this.nodeMappings.containsKey(nodeCfg.getType()))
			{
				final DefaultNodeMapping nodeMapping = new DefaultNodeMapping(this, nodeCfg);
				nodeMapping.graph = this;

				//this.nodeMappings.put(nodeCfg.getType(), nodeMapping);
				this.addNodeMapping(nodeMapping);
			}
		}

		// a TreeSet which holds all NodeMappings which shall be compiled
		// a TreeSet is chosen to guarantees always same order for each equal graph (easier to unit-test) 
		final Set<NodeMapping> nodeMappingsSet = new TreeSet<NodeMapping>(new Comparator<NodeMapping>()
		{
			@Override
			public int compare(final NodeMapping object1, final NodeMapping object2)
			{
				return object1.getSourceConfig().getType().getName().compareTo(object2.getSourceConfig().getType().getName());
			}
		});

		// sort NodeMappings
		nodeMappingsSet.addAll(nodeMappings.values());


		// compile each NodeMapping
		for (final NodeMapping nodeMapping : nodeMappingsSet)
		{
			// AbstractNodeMapping types can be compiled
			// other types are ignored 
			if (nodeMapping instanceof AbstractNodeMapping)
			{
				((AbstractNodeMapping) nodeMapping).initialize();
			}
		}

		setInitialized(true);
	}


}
