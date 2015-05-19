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
package de.hybris.platform.webservices.util.objectgraphtransformer.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyFilter;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.CachedClassLookupMap;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultGraphTransformer;


public class BasicNodeFilter implements PropertyFilter
{
	int maxDistance = Integer.MAX_VALUE;

	Map leafNodeProcessing = Collections.EMPTY_MAP;
	boolean whiteListLeafNodeProcessing = true;

	public BasicNodeFilter()
	{
		this(Integer.MAX_VALUE);
	}

	public BasicNodeFilter(final int maxDistance)
	{
		this.maxDistance = maxDistance;
	}



	public BasicNodeFilter(final int maxDistance, final Collection<Class> processLeafNodeTypes)
	{
		this.maxDistance = maxDistance;
		this.setLeafNodeProcessing(processLeafNodeTypes, false);
	}

	public void setLeafNodeProcessing(final Collection<Class> leafNodeTypes, final boolean isWhiteList)
	{
		this.leafNodeProcessing = new CachedClassLookupMap<Class>();
		for (final Class clazz : leafNodeTypes)
		{
			leafNodeProcessing.put(clazz, Boolean.TRUE);
		}
		this.whiteListLeafNodeProcessing = isWhiteList;
	}



	public void setDepth(final int depth)
	{
		this.maxDistance = depth;
	}


	@Override
	public boolean isFiltered(final PropertyContext ctx, final Object value)
	{
		boolean isFiltered = false;

		if (value != null)
		{
			// threshold for max allowed distance reached?
			isFiltered = ctx.getParentContext().getDistance() >= maxDistance;

			// special handling for node -> property conversions
			// in case a write-property converter converts from node to flat property: distance based check is disabled 
			if (isFiltered && ctx.getPropertyMapping().getTargetConfig().getWriteInterceptor() != null)
			{
				// unconverted type
				//final Class converterInputType = ctx.getPropertyMapping().getTargetPropertyConfig().getWriteType();
				final Class setterInputType = ctx.getPropertyMapping().getTargetConfig().getWriteMethod().getParameterTypes()[0];

				//isFiltered = ctx.getParentContext().getDistance() >= maxDistance;
				isFiltered = ((DefaultGraphTransformer) ctx.getGraphContext().getGraphTransformer()).getNodeMappingsMap()
						.getStaticMap().containsKey(setterInputType);
			}

			if (isFiltered && !leafNodeProcessing.isEmpty())
			{
				final boolean isElement = leafNodeProcessing.containsKey(value.getClass());
				isFiltered = isElement ^ whiteListLeafNodeProcessing;
			}
		}
		return isFiltered;
	}

}
