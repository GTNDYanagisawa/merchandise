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
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;


public class BidiGraphTransformer extends DefaultGraphTransformer
{
	public BidiGraphTransformer()
	{
		this(null);
	}

	public BidiGraphTransformer(final Class graph)
	{
		super(graph);
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
			final NodeMapping mapping = getNodeMapping(source.getClass());
			final Class nodeType = mapping.getSourceConfig().getType();

			if (getSourceConfig().getNodes().containsKey(nodeType))
			{
				ctx = this.createGraphContext();
			}
			else
			{
				if (getTargetConfig().getNodes().containsKey(nodeType))
				{
					ctx = this.createSecondGraphContext();
				}
				else
				{
					throw new GraphException("Can't create a GraphContext");

				}
			}
		}

		final T result = graphProcessor.process(ctx, source, target);

		return result;
	}


	@Override
	public void addNodeMapping(final NodeMapping cfg)
	{
		super.addNodeMapping(cfg);

		// add bi-directional mapping
		final Class<?> src = cfg.getTargetConfig().getType();
		final Class<?> dest = cfg.getSourceConfig().getType();
		final NodeMapping targetNode = new DefaultNodeMapping(this, src, dest);

		this.nodeMappings.put(targetNode.getSourceConfig().getType(), targetNode);

	}

	public GraphContext createSecondGraphContext()
	{
		return super.createGraphContext();
	}




}
