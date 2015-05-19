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

import org.apache.log4j.Logger;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;


/**
 * See specification of {@link PropertyContext}
 */
public class PropertyContextImpl implements PropertyContext
{
	private static final Logger log = Logger.getLogger(PropertyContextImpl.class);

	private PropertyMapping propertyMapping = null;
	private GraphContextImpl graphCtx = null;
	private NodeContextImpl parentNodeCtx = null;
	private CachedClassLookupMap<NodeMapping> childNodeLookup = null;


	protected PropertyContextImpl(final GraphContextImpl graphCtx, final NodeContextImpl nodeCtx,
			final PropertyMapping propertyMapping, final CachedClassLookupMap<NodeMapping> nodeLookup)
	{
		this.propertyMapping = propertyMapping;
		this.graphCtx = graphCtx;
		this.parentNodeCtx = nodeCtx;
		this.childNodeLookup = nodeLookup;
	}


	@Override
	public GraphContext getGraphContext()
	{
		return this.graphCtx;
	}



	@Override
	public PropertyMapping getPropertyMapping()
	{
		return this.propertyMapping;
	}

	@Override
	public NodeContext getParentContext()
	{
		return this.parentNodeCtx;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext#getNodeMappingsMap()
	 */
	protected CachedClassLookupMap<NodeMapping> getChildNodeLookup()
	{
		return this.childNodeLookup;
	}


	protected NodeContextImpl createChildNodeContext(final AbstractNodeMapping nodeMapping, final Object source)
	{
		final int distance = this.parentNodeCtx.getRealDistance() + 1;
		final int virtualDist = nodeMapping.isVirtual() ? this.parentNodeCtx.getDistance() : this.parentNodeCtx.getDistance() + 1;

		// if needed: update highest found transformation distance
		if (this.graphCtx.getMaxDistance() <= virtualDist)
		{
			this.graphCtx.setMaxDistance(distance);
		}

		// TODO: use childNode
		CachedClassLookupMap<NodeMapping> nodeLookup = this.graphCtx.getRuntimeNodeMappings(distance);

		if (nodeLookup == null)
		{
			// ...nodeLookup from this property
			final CachedClassLookupMap<NodeMapping> base = this.getChildNodeLookup();
			final CachedClassLookupMap<NodeMapping> merge = this.graphCtx.graphConfigImpl.getAllNodeMappings(distance);

			// ...build
			nodeLookup = this.graphCtx.buildChildNodeLookup(base, merge);

			this.graphCtx.setRuntimeNodeMappings(distance, nodeLookup);

			log.debug("Added distance based runtime node lookup: " + distance + ":" + nodeLookup.hashCode());
		}


		// create result context
		final NodeContextImpl result = new NodeContextImpl(this.graphCtx, this, nodeMapping, nodeLookup, distance, virtualDist,
				source);

		this.graphCtx.getGraphTransformer().nodeContextCreated(result);

		return result;
	}

	protected String createSourcePathString()
	{
		String result = this.parentNodeCtx.createSourcePathString();
		if (getPropertyMapping() != null)
		{
			result = result + ">" + getPropertyMapping().getId();
		}
		return result;
	}

	protected String createTargetPathString()
	{
		String result = this.parentNodeCtx.createTargetPathString();
		if (getPropertyMapping() != null)
		{
			result = result + ">" + getPropertyMapping().getId();
		}
		return result;
	}

}
