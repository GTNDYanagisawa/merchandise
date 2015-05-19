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

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.webservices.objectgraphtransformer.YNodeProcessor;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphException;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeFactory;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;


public class DefaultNodeMapping extends AbstractNodeMapping
{
	private static final Logger log = Logger.getLogger(DefaultNodeMapping.class);

	private static final AbstractNodeProcessor DEFAULT_NODE_PROCESSOR = new YNodeProcessor();

	private boolean autoDetectMappings = true;


	/**
	 * Reserved Constructor for Spring.
	 */
	public DefaultNodeMapping(final GraphMapping graph)
	{
		super(graph);
		setNodeProcessor(DEFAULT_NODE_PROCESSOR);
	}

	public DefaultNodeMapping(final GraphMapping graph, final Class srcNodeCfg)
	{
		this(graph, new DefaultNodeConfig(srcNodeCfg));
	}

	public DefaultNodeMapping(final GraphMapping graph, final Class srcNodeCfg, final Class targetNodeCfg)
	{
		this(graph, new DefaultNodeConfig(srcNodeCfg), new DefaultNodeConfig(targetNodeCfg));
	}


	/**
	 * Constructor.
	 * 
	 * @param srcNodeCfg
	 * @param targetNodeCfg
	 */
	public DefaultNodeMapping(final GraphMapping graph, final NodeConfig srcNodeCfg, final NodeConfig targetNodeCfg)
	{
		super(graph, srcNodeCfg, targetNodeCfg);
		setNodeProcessor(DEFAULT_NODE_PROCESSOR);
	}


	/**
	 * Constructor. Expects a node type which is annotated with {@link GraphNode}
	 * 
	 * @param srcNodeCfg
	 */
	public DefaultNodeMapping(final GraphMapping graph, final NodeConfig srcNodeCfg) throws GraphException
	{
		super(graph);
		final Class<?> nodeType = srcNodeCfg.getType();
		if (nodeType.isAnnotationPresent(GraphNode.class))
		{
			final GraphNode nodeAnno = nodeType.getAnnotation(GraphNode.class);

			// create initial node configuration
			setSourceConfig(srcNodeCfg);

			if (nodeAnno.target() != null)
			{
				setTargetConfig(new DefaultNodeConfig(nodeAnno.target()));
			}
			setNodeProcessor(DEFAULT_NODE_PROCESSOR);

			// add factory when configured
			if (nodeAnno.factory() != NodeFactory.class)
			{
				try
				{
					final NodeFactory fac = nodeAnno.factory().newInstance();
					setNodeFactory(fac);
				}
				catch (final Exception e)
				{
					log.error(e.getMessage());
					throw new GraphException("Can't create Factory " + nodeAnno.factory(), e);
				}
			}
		}
		else
		{
			throw new GraphException(nodeType + " is not annotated with " + GraphNode.class.getSimpleName());
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param template
	 */
	public DefaultNodeMapping(final AbstractGraphTransformer graph, final NodeMapping template)
	{
		this(graph, template, true);
		setInitialized(true);
	}

	/**
	 * Constructor.
	 * <p/>
	 * Creates a new Configuration based on passed template configuration.
	 * 
	 * @param template
	 *           template configuration
	 * @param suppressProperties
	 *           if true {@link PropertyMapping} elements are not copied from template
	 */
	public DefaultNodeMapping(final AbstractGraphTransformer graph, final NodeMapping template, final boolean suppressProperties)
	{
		super(graph);
		setSourceConfig(new DefaultNodeConfig(template.getSourceConfig()));
		setTargetConfig(new DefaultNodeConfig(template.getTargetConfig()));
		setNodeFactory(template.getNodeFactory());
		if (template instanceof AbstractNodeMapping)
		{
			super.setNodeProcessor(((AbstractNodeMapping) template).getProcessor());
		}
		else
		{
			log.error("Expected a " + NodeMapping.class.getSimpleName() + " template of type "
					+ AbstractNodeMapping.class.getSimpleName() + " but got " + template.getClass().getName());
			super.setNodeProcessor(DEFAULT_NODE_PROCESSOR);
		}

		if (!suppressProperties)
		{
			for (final Map.Entry<String, PropertyMapping> entry : template.getPropertyMappings().entrySet())
			{
				putPropertyMapping(entry.getValue());
			}
		}
	}

	public void setAutoDetectMappingEnabled(final boolean enabled)
	{
		this.autoDetectMappings = enabled;
	}


	/**
	 * @param nodeFactory
	 *           the nodeFactory to set
	 */
	@Override
	public void setNodeFactory(final NodeFactory nodeFactory)
	{
		super.setNodeFactory(nodeFactory);
	}


	@Override
	public void setSourceConfig(final NodeConfig sourceNode)
	{
		if (getSourceConfig() != null)
		{
			throw new GraphException("Can't set a source node type twice");
		}
		super.setSourceConfig(sourceNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.restjersey.objectgraphtransformer.impl.AbstractNodeConfig#setTargetType(java.lang.Class)
	 */
	@Override
	public void setTargetConfig(final NodeConfig targetNode)
	{
		if (getTargetConfig() != null)
		{
			throw new GraphException("Can't set a target node type twice");
		}
		super.setTargetConfig(targetNode);
	}


	@Override
	protected boolean initializeNode()
	{
		// step1: property auto-detection (if enabled)
		// this step creates a source/target PropertyMapping for every source node property and adds it to 
		// the collection of PropertyMappings which are getting processed during transformation by a NodeProcessor
		// Already existing PropertyMappings are overruling auto-detected ones  
		if (this.autoDetectMappings)
		{
			// iterate over each property which is provided by the source graph node... 
			final Collection<PropertyConfig> pcfgList = this.getSourceConfig().getProperties().values();
			for (final PropertyConfig propCfg : pcfgList)
			{
				// property id is bean-properties name
				final String propId = propCfg.getName();

				// ... if property was not already configured (e.g. via an external custom API call) 
				if (!this.getPropertyMappings().containsKey(propId))
				{
					// ... clone that PropertyConfig (that allows later modifications without changing the initial values) 
					final DefaultPropertyConfig readPropertyCfg = new DefaultPropertyConfig((DefaultPropertyConfig) propCfg);

					// ... and ask for a PropertyConfig with same id in target graph node 
					DefaultPropertyConfig writePropertyCfg = (DefaultPropertyConfig) getTargetConfig().getProperties().get(propId);
					// ... if a target property exists
					if (writePropertyCfg != null)
					{
						// ... clone it
						writePropertyCfg = new DefaultPropertyConfig(writePropertyCfg);
					}
					else
					{
						// ...otherwise create a dummy
						writePropertyCfg = new DefaultPropertyConfig(propId, null, null);
					}

					// finally create and add the PropertyMapping between source and target
					final DefaultPropertyMapping property = new DefaultPropertyMapping(this, readPropertyCfg, writePropertyCfg);
					putPropertyMapping(property);
				}
			}

			// iterate over each property which is provided by target graph node 
			// (find the 'virtual' properties which are allowed to have no read-property)
			final Collection<PropertyConfig> writePropCfg = this.getTargetConfig().getProperties().values();
			for (final PropertyConfig propCfg : writePropCfg)
			{
				// GraphProperty annotation is actually misused here to pass a propertymapping configuration 
				// in that case: whether mapping is virtual (means: has no read source)
				if (propCfg.getWriteMethod() != null && propCfg.getWriteMethod().isAnnotationPresent(GraphProperty.class))
				{
					final boolean virtualMapping = (propCfg.getWriteMethod().getAnnotation(GraphProperty.class)).virtual();

					if (virtualMapping)
					{
						// property id is bean-properties name
						final String propId = propCfg.getName();

						// ... if property was not already configured (e.g. via an external custom API call) 
						if (!this.getPropertyMappings().containsKey(propId))
						{
							// finally create and add the PropertyMapping between source and target
							final DefaultPropertyConfig readProp = new DefaultPropertyConfig(propId, null, null);
							final DefaultPropertyConfig writeProp = new DefaultPropertyConfig((DefaultPropertyConfig) propCfg);
							final DefaultPropertyMapping property = new DefaultPropertyMapping(this, readProp, writeProp);
							putPropertyMapping(property);
						}
					}
				}
			}
		}
		return true;
	}

}
