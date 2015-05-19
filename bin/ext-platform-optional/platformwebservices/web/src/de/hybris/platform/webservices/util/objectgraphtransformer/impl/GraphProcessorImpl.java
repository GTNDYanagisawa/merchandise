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
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;


public class GraphProcessorImpl implements GraphProcessor
{

	@Override
	public <T> T process(final GraphContext graphCtx, final Object source, final T target)
	{
		if (!(graphCtx instanceof GraphContextImpl))
		{
			throw new UnsupportedOperationException(this.getClass().getSimpleName() + " needs an instance of "
					+ GraphContextImpl.class.getName() + " to work properly");

		}

		final GraphContextImpl graphCtxImpl = (GraphContextImpl) graphCtx;

		return this.process(graphCtxImpl, source, target);
	}

	public <T> T process(final GraphContextImpl graphCtx, final Object source, final T target)
	{
		if (!graphCtx.getGraphTransformer().isInitialized())
		{
			graphCtx.getGraphTransformer().initialize();
		}

		if (graphCtx.isReleased())
		{
			throw new GraphException("Can't use an instance of " + GraphContext.class.getSimpleName() + " twice");
		}

		// create nodeLookup to lookup root node
		final CachedClassLookupMap<NodeMapping> nodeLookup = ((GraphConfigurationImpl) graphCtx.getConfiguration())
				.getAllNodeMappings(0);
		final NodeMapping nodeMapping = nodeLookup.get(source.getClass());

		if (nodeMapping == null)
		{
			throw new GraphException("Can't find a " + NodeMapping.class.getSimpleName() + " for " + source.getClass());
		}

		// create nodeLookup used for root nodes childs 
		final NodeContext nodeCtx = graphCtx.createRootNodeContext(nodeLookup, (AbstractNodeMapping) nodeMapping, source);

		final T result = nodeMapping.getProcessor().process(nodeCtx, source, target);
		graphCtx.setReleased(true);

		return result;


	}

}
