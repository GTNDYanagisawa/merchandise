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

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphException;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeProcessor;

import org.apache.log4j.Logger;




/**
 * Abstract base implementation for a {@link NodeProcessor}.
 */
public abstract class AbstractNodeProcessor implements NodeProcessor
{
	private static final Logger log = Logger.getLogger(AbstractNodeProcessor.class);

	@Override
	public <T extends Object> T process(final NodeContext nodeCtx, final Object source, final T target)
	{
		// instance check
		if (!(nodeCtx instanceof NodeContextImpl))
		{
			throw new UnsupportedOperationException(this.getClass().getSimpleName() + " needs an instance of "
					+ NodeContextImpl.class.getName() + " to work properly");
		}

		// cast 
		final NodeContextImpl nodeCtxImpl = (NodeContextImpl) nodeCtx;

		return process(nodeCtxImpl, source, target);
	}

	public abstract <T> T process(final NodeContextImpl nodeCtx, final Object source, final T target);

	/**
	 * Creates a new node.
	 * 
	 * @param <T>
	 * @param clazz
	 *           requested type
	 * @return created instance or null
	 */
	protected <T> T createNode(final Class<T> clazz)
	{
		T result = null;
		try
		{
			result = clazz.newInstance();
		}
		catch (final Exception e)
		{
			log.error("Can't create node " + clazz, e);
			throw new GraphException(e);
		}
		return result;
	}



}
