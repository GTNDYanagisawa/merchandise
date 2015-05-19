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
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * See specification of {@link NodeContext}.
 */
public class NodeContextImpl implements NodeContext
{
	private AbstractNodeMapping nodeMapping = null;
	private Object sourceNodeValue = null;
	private Object targetNodeValue = null;

	private PropertyContextImpl parentPropertyCtx = null;
	private GraphContextImpl graphCtx = null;

	private int distance = 0;
	private int virtualDistance = 0;
	private CachedClassLookupMap<NodeMapping> childNodeLookup = null;

	protected NodeContextImpl(final GraphContextImpl graphCtx, final PropertyContextImpl propertyCtx,
			final AbstractNodeMapping nodeMapping, final CachedClassLookupMap<NodeMapping> nodeMappingsMap, final int distance,
			final int virtualDistance, final Object source)
	{
		super();
		this.graphCtx = graphCtx;
		this.parentPropertyCtx = propertyCtx;
		if (this.parentPropertyCtx != null)
		{
			if (propertyCtx.getGraphContext() != this.graphCtx)
			{
				throw new GraphException(GraphContext.class.getSimpleName() + " of passed property is not same as of this node");
			}
		}
		this.nodeMapping = nodeMapping;

		this.sourceNodeValue = source;
		this.childNodeLookup = nodeMappingsMap;
		this.distance = distance;
		this.virtualDistance = virtualDistance;
	}

	@Override
	public int getRealDistance()
	{
		return distance;
	}

	@Override
	public int getDistance()
	{
		return virtualDistance;
	}

	@Override
	public AbstractNodeMapping getNodeMapping()
	{
		return nodeMapping;
	}

	@Override
	public Object getSourceNodeValue()
	{
		return sourceNodeValue;
	}

	protected void setSourceNodeValue(final Object sourceNode)
	{
		this.sourceNodeValue = sourceNode;
	}

	@Override
	public Object getTargetNodeValue()
	{
		return targetNodeValue;
	}

	protected void setTargetNodeValue(final Object targetNode)
	{
		this.targetNodeValue = targetNode;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * This implementation of {@link NodeContext} returns always a {@link PropertyContextImpl} type.
	 */
	@Override
	public PropertyContextImpl getParentContext()
	{
		return this.parentPropertyCtx;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * This implementation of {@link NodeContext} returns always a {@link GraphContextImpl} type.
	 */
	@Override
	public GraphContextImpl getGraphContext()
	{
		return this.graphCtx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext#getPath()
	 */
	@Override
	public List<NodeContext> getProcessingPath()
	{
		final List<NodeContext> path = new ArrayList<NodeContext>();

		NodeContext nodeCtx = this;

		while (nodeCtx != null)
		{
			path.add(nodeCtx);
			if (nodeCtx.getParentContext() != null)
			{
				nodeCtx = nodeCtx.getParentContext().getParentContext();
			}
			else
			{
				nodeCtx = null;
			}
		}

		Collections.reverse(path);
		return path;
	}

	protected CachedClassLookupMap<NodeMapping> getChildNodeLookup()
	{
		return this.childNodeLookup;
	}


	protected PropertyContextImpl createChildPropertyContext(final PropertyMapping propertyMapping)
	{
		CachedClassLookupMap<NodeMapping> nodeLookup = this.childNodeLookup;
		if (propertyMapping != null)
		{
			// create node lookup for property childs based on:
			final List<NodeMapping> merge = propertyMapping.getNewNodeMappings();
			nodeLookup = this.graphCtx.buildChildNodeLookup(nodeLookup, merge);
		}

		final PropertyContextImpl result = new PropertyContextImpl(this.graphCtx, this, propertyMapping, nodeLookup);

		this.graphCtx.getGraphTransformer().propertyContextCreated(result);

		return result;
	}

	protected String createTargetPathString()
	{
		String result = "[" + (getTargetNodeValue() != null ? getTargetNodeValue().getClass().getSimpleName() : "null") + "]";
		if (this.parentPropertyCtx != null)
		{
			result = this.parentPropertyCtx.createTargetPathString() + ">" + result;
		}
		return result;
	}

	protected String createSourcePathString()
	{
		String result = "[" + (getSourceNodeValue() != null ? getSourceNodeValue().getClass().getSimpleName() : "null") + "]";
		if (this.parentPropertyCtx != null)
		{
			result = this.parentPropertyCtx.createSourcePathString() + ">" + result;
		}
		return result;
	}

}
