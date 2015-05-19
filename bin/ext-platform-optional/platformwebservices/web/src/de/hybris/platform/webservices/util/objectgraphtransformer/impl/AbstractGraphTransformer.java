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

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphException;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProcessor;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


public abstract class AbstractGraphTransformer implements GraphTransformer, GraphMapping
{
	private static final Logger log = Logger.getLogger(AbstractGraphTransformer.class);

	private static final GraphProcessor DEFAULT_GRAPH_PROCESSOR = new GraphProcessorImpl();

	protected GraphProcessor graphProcessor = null;
	protected CachedClassLookupMap<NodeMapping> nodeMappings = null;

	private boolean _isInitialized = false;

	private GraphConfig sourceGraphCfg = null;
	private GraphConfig targetGraphCfg = null;

	// for debug only
	private Set<String> debugNodesSet = null;


	public AbstractGraphTransformer(final GraphConfig sourceGraphCfg, final GraphConfig targetGraphCfg)
	{
		this.graphProcessor = DEFAULT_GRAPH_PROCESSOR;
		this.nodeMappings = new CachedClassLookupMap<NodeMapping>();

		this.sourceGraphCfg = sourceGraphCfg;
		this.targetGraphCfg = targetGraphCfg;

		this.debugNodesSet = new HashSet<String>();
	}

	protected Set<String> getDebugNodes()
	{
		return this.debugNodesSet;
	}


	/**
	 * @return the isCompiled
	 */
	protected boolean isInitialized()
	{
		return _isInitialized;
	}

	/**
	 * @param initialized
	 *           the isCompiled to set
	 */
	protected void setInitialized(final boolean initialized)
	{
		this._isInitialized = initialized;
	}

	protected abstract void initialize();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer#transform(de.hybris.platform.restjersey
	 * . objectgraphtransformer.GraphContext, java.lang.Object)
	 */
	@Override
	public <T> T transform(final GraphContext ctx, final Object source)
	{
		return (T) transform(ctx, source, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer#transform(java.lang.Object)
	 */
	@Override
	public <T extends Object> T transform(final Object source)
	{
		return (T) transform(null, source, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer#transform(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public <T extends Object> T transform(final Object source, final T target)
	{
		return transform(null, source, target);
	}


	/**
	 * Implementation as specified in {@link GraphTransformer#transform(GraphContext, Object, Object)}. Adds dynamic
	 * (when not already done) graph compilation before transformation starts
	 */
	@Override
	public abstract <T> T transform(GraphContext ctx, final Object source, final T target);

	public void nodeContextCreated(@SuppressWarnings("unused") final NodeContext nodeCtx)
	{
		// NOP
	}

	public void propertyContextCreated(@SuppressWarnings("unused") final PropertyContext propCtx)
	{
		// NOP
	}

	@Override
	public GraphConfig getSourceConfig()
	{
		return sourceGraphCfg;
	}

	@Override
	public GraphConfig getTargetConfig()
	{
		return targetGraphCfg;
	}

	@Override
	public GraphProcessor getProcessor()
	{
		return this.graphProcessor;
	}

	protected void setProcessor(final GraphProcessor processor)
	{
		this.graphProcessor = processor;
	}

	@Override
	public GraphContext createGraphContext()
	{
		return new GraphContextImpl(this);
	}

	protected abstract void performNodeCreated(final NodeContext nodeCtx, Object node);


	@Override
	public void addNodeMapping(final NodeMapping cfg)
	{
		this.nodeMappings.put(cfg.getSourceConfig().getType(), cfg);
		((DefaultGraphConfig) this.sourceGraphCfg).addNode(cfg.getSourceConfig());
		((DefaultGraphConfig) this.targetGraphCfg).addNode(cfg.getTargetConfig());
		setInitialized(false);
	}

	public void addNodes(final Collection<Class> nodes)
	{
		for (final Class clazz : nodes)
		{
			addNodes(clazz);
		}
	}

	@Override
	public void addNodes(final Class graph)
	{
		final Set<Class> ignore = new HashSet<Class>();
		ignore.addAll(sourceGraphCfg.getNodes().keySet());

		final Map<Class, NodeConfig> sourceNodes = this.findAllChildNodeConfig(Collections.singletonList(graph), ignore);

		if (!sourceNodes.isEmpty())
		{
			for (final Map.Entry<Class, NodeConfig> entrySet : sourceNodes.entrySet())
			{
				final NodeMapping mapping = new DefaultNodeMapping(this, entrySet.getValue());
				this.addNodeMapping(mapping);
			}
			setInitialized(false);
		}
	}



	public void addNodeMappings(final Collection<NodeMapping> nodeMappings)
	{
		for (final NodeMapping cfg : nodeMappings)
		{
			this.addNodeMapping(cfg);
		}

		// XXX: maybe move compilation lazily to getNode() ... 
		// adding nodes needs a refresh
		//this.compile();
	}

	@Override
	public NodeMapping getNodeMapping(final Class node)
	{
		return getNodeMappingsMap().get(node);
	}

	public CachedClassLookupMap<NodeMapping> getNodeMappingsMap()
	{
		return this.nodeMappings;
	}


	private Map<Class, NodeConfig> findAllChildNodeConfig(final Collection<Class> nodes, final Set<Class> ignore)
	{
		final Map<Class, NodeConfig> result = new LinkedHashMap<Class, NodeConfig>();

		for (final Class nodeClass : nodes)
		{
			// which is annotated as graph 
			if (!nodeClass.isAnnotationPresent(GraphNode.class))
			{
				throw new GraphException(nodeClass.getName() + " is not annotated with " + GraphNode.class.getSimpleName());
			}

			log.debug("Start introspecting (sub)graph " + nodeClass + "...");

			final int size = result.size();
			this.findAllChildNodeConfig(nodeClass, result, ignore);

			if (log.isDebugEnabled())
			{
				final int newNodes = result.size() - size;
				log.debug("... finished introspecting (sub)graph " + nodeClass + "; detected " + newNodes + " new nodes");
			}
		}

		return result;
	}


	/**
	 * @param node
	 *           start node
	 * @param nodeCfgMap
	 *           mapping
	 * @param ignore
	 *           all processed types
	 */
	private void findAllChildNodeConfig(final Class node, final Map<Class, NodeConfig> nodeCfgMap, final Set<Class> ignore)
	{
		// only process node type if not already done
		if (!ignore.contains(node))
		{
			// add node type to 'ignore' to prevent it from being processed multiple times
			ignore.add(node);

			// lookup for graph annotation
			if (node.isAnnotationPresent(GraphNode.class))
			{
				// get annotation with node configurations
				final GraphNode graphNode = (GraphNode) node.getAnnotation(GraphNode.class);

				// create a NodeConfig
				final DefaultNodeConfig cfg = new DefaultNodeConfig(node);
				nodeCfgMap.put(node, cfg);

				// Recursive processing of child nodes
				// child nodes are detected in different strategies ... 

				// strategy 1: child nodes are taken from annotation GraphNnode#addNodes 
				// default value is "void" which means: nothing set, no child nodes
				if (!graphNode.addNodes()[0].equals(void.class))
				{
					for (final Class<?> childNode : graphNode.addNodes())
					{
						// recursive call with current child node candidate
						this.findAllChildNodeConfig(childNode, nodeCfgMap, ignore);
					}
				}

				// strategy 2: child nodes are detected  from available property types (getters)
				// (with support for typed collections)
				final Collection<PropertyConfig> pCfgList = cfg.getProperties().values();
				for (final PropertyConfig pCfg : pCfgList)
				{
					Class<?> childNodeCandidate = pCfg.getReadType();

					// if type is collection:
					// try to find out the collection type and update child node candiate
					if (Collection.class.isAssignableFrom(childNodeCandidate))
					{
						// works only if collection is typed
						final Type type = pCfg.getReadMethod().getGenericReturnType();
						if (type instanceof ParameterizedType)
						{
							final Type[] types = ((ParameterizedType) type).getActualTypeArguments();
							// don't know whether any other value than '1' can ever occur
							if (types.length == 1)
							{
								Type _type = types[0];

								// extract class-type from wildcard-type
								if (types[0] instanceof WildcardType)
								{
									final WildcardType wType = ((WildcardType) types[0]);

									// currently only upper-bounds are supported (? extends type)
									if (wType.getUpperBounds().length == 1)
									{
										_type = wType.getUpperBounds()[0];
									}
								}

								// any other type than 'class' gets skipped
								if (_type instanceof Class)
								{
									childNodeCandidate = (Class) _type;
								}
							}
						}
					}

					// recursive call with current child node candidate
					findAllChildNodeConfig(childNodeCandidate, nodeCfgMap, ignore);
				}
			}
		}
	}

}
