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
package de.hybris.platform.webservices.cache.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.webservices.AbstractYResource;
import de.hybris.platform.webservices.RestResource;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;




/**
 * Computes unique ID based on RESOURCE's hashCode
 */
@SuppressWarnings("deprecation")
@Deprecated
public class DefaultCachingStrategy<RESOURCE> extends AbstractCachingStrategy<RESOURCE>
{
	private final static Logger LOG = Logger.getLogger(DefaultCachingStrategy.class);

	private static Set<Class> basicTypes = null;

	static
	{
		basicTypes = new HashSet<Class>();
		basicTypes.add(Byte.class);
		basicTypes.add(Short.class);
		basicTypes.add(Integer.class);
		basicTypes.add(Long.class);
		basicTypes.add(Float.class);
		basicTypes.add(Double.class);
		basicTypes.add(Boolean.class);
		basicTypes.add(Character.class);
		basicTypes.add(String.class);
		basicTypes.add(Date.class);
	}

	@Override
	public String getUID(final RestResource resource, final RESOURCE resourceValue)
	{

		Integer resourceHashCode = null;
		if (resourceValue instanceof AbstractItemModel)
		{
			final NodeConfig nodeCfg = ((AbstractYResource) resource).getObjectGraph().getNodeMapping(resourceValue.getClass())
					.getSourceConfig();
			final Collection<PropertyConfig> propCfgList = nodeCfg.getProperties().values();

			for (final PropertyConfig propCfg : propCfgList)
			{
				final Method method = propCfg.getReadMethod();
				final Type methodReturnType = method.getGenericReturnType();
				if (basicTypes.contains(methodReturnType))
				{
					Object methodResult;
					try
					{
						methodResult = method.invoke(resourceValue);

						if (methodResult != null)
						{
							if (LOG.isDebugEnabled())
							{
								LOG.debug("Hash code for " + resourceValue + "'s (" + ((ItemModel) resourceValue).getPk() + ") "
										+ method.getName() + ",orig value :" + methodResult + " , hash :"
										+ methodResult.hashCode());
							}
							if (resourceHashCode == null)
							{
								resourceHashCode = Integer.valueOf(0);
							}
							resourceHashCode = Integer.valueOf(resourceHashCode.intValue() + methodResult.hashCode());
						}
					}
					catch (final IllegalArgumentException e)
					{
						LOG.error("error when computing hash code");
					}
					catch (final IllegalAccessException e)
					{
						LOG.error("error when computing hash code");
					}
					catch (final InvocationTargetException e)
					{
						LOG.error("error when computing hash code");
					}
				}
			}
		}
		else if (resourceValue instanceof List)
		{
			resourceHashCode = Integer.valueOf(resourceValue.hashCode());
		}
		return resourceHashCode == null ? null : String.valueOf(resourceHashCode.hashCode());
	}
}
