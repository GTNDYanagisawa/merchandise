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


import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


public class CollectionNodeProcessor extends AbstractNodeProcessor
{
	private static final Logger log = Logger.getLogger(CollectionNodeProcessor.class);



	@Override
	public <T> T process(final NodeContextImpl nodeCtx, final Object source, T target)
	{
		// if we don't have a target to "inject" the source, create a default one
		if (target == null)
		{
			target = (T) this.getOrCreateTargetNode(nodeCtx, source);
			nodeCtx.setTargetNodeValue(target);
		}

		final PropertyContextImpl childPropCtx = nodeCtx.createChildPropertyContext(null);
		nodeCtx.getGraphContext().getGraphTransformer().propertyContextCreated(childPropCtx);


		// process each element of source collection separately
		for (Object sourceElement : (Collection<?>) source)
		{
			final AbstractNodeMapping nodeMapping = (AbstractNodeMapping) nodeCtx.getChildNodeLookup().get(sourceElement.getClass());

			// if so, start node processing
			if (nodeMapping != null)
			{
				final NodeContext childNodeCtx = childPropCtx.createChildNodeContext(nodeMapping, sourceElement);
				final NodeProcessor nodeProc = nodeMapping.getProcessor();
				sourceElement = nodeProc.process(childNodeCtx, sourceElement, null);
			}

			// add element (processed as node or not) to target collection
			((Collection) target).add(sourceElement);
		}
		return target;
	}

	private <T> T getOrCreateTargetNode(final NodeContextImpl nodeCtx, final Object srcNodeValue)
	{
		//final Object source = nodeCtx.getSourceNode();
		T result = null;

		// default: contract is type of source value
		Class contract = srcNodeValue.getClass();

		// if available: contract is write method parameter type
		if (nodeCtx.getParentContext() != null)
		{
			contract = nodeCtx.getParentContext().getPropertyMapping().getTargetConfig().getWriteMethod().getParameterTypes()[0];
		}

		// result value starts with contract type
		Class<?> targetClass = contract;

		// when contract is an interface ...
		if (targetClass.isInterface())
		{
			// and type of source value is compatible, take that as result type
			// otherwise clear result
			targetClass = contract.isAssignableFrom(srcNodeValue.getClass()) ? srcNodeValue.getClass() : null;
		}

		// check result for public constructor
		// if not available clear result
		if (targetClass != null)
		{
			try
			{
				targetClass.getConstructor();
			}
			catch (final Exception e)
			{
				// update contract
				// guarantees that it is no interface but a concrete
				// implementation based on the original contract
				contract = targetClass;
				targetClass = null;
			}
		}

		// still no result found then do some fallback
		if (targetClass == null)
		{
			if (List.class.isAssignableFrom(contract))
			{
				targetClass = ArrayList.class;
			}

			if (Set.class.isAssignableFrom(contract))
			{
				targetClass = HashSet.class;
			}

			log.debug("Fallback to " + ((targetClass != null) ? targetClass : "null"));

		}

		if (targetClass != null)
		{
			result = (T) this.createNode(targetClass);
			nodeCtx.getGraphContext().getGraphTransformer().performNodeCreated(nodeCtx, result);
		}

		return result;
	}





}
