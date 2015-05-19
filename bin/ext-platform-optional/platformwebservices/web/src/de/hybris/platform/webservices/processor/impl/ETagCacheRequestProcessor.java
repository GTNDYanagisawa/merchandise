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
package de.hybris.platform.webservices.processor.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.AbstractResponseBuilder;
import de.hybris.platform.webservices.AbstractYResource;
import de.hybris.platform.webservices.RestResource;
import de.hybris.platform.webservices.processchain.RequestProcessChain;
import de.hybris.platform.webservices.processor.RequestProcessor;
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

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;

import org.apache.log4j.Logger;


/**
 * 
 * Processing filter which implements {@link EntityTag} base caching. Processes etag only if called in GET method. If
 * the result of the {@link Request#evaluatePreconditions(EntityTag)} 'decides' the data is cached
 * {@link #doProcess(de.hybris.platform.webservices.processor.RequestProcessor.RequestType, Object, AbstractResponseBuilder, RequestProcessChain)}
 * , will return false, causing interruption of the chain and avoiding the default processing of the Get response.
 */
public class ETagCacheRequestProcessor<RESOURCE> implements RequestProcessor<RESOURCE>
{
	private static final Logger LOG = Logger.getLogger(ETagCacheRequestProcessor.class);

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
	public void doProcess(final RequestProcessor.RequestType type, final Object dto,
			final AbstractResponseBuilder<RESOURCE, ?, ?> responseBuilder, final RequestProcessChain chain)
	{

		EntityTag tag = null;
		if (type == RequestProcessor.RequestType.GET)
		{

			final AbstractResource<RESOURCE> resource = responseBuilder.getResource();
			if (resource != null)
			{
				final String uid = getUID(resource, responseBuilder.getResourceValue());
				if (uid != null)
				{
					tag = new EntityTag(uid);
				}


				// check whether an entity tag is available...
				if (tag != null)
				{
					// ... if so, check whether it is still valid
					if (responseBuilder.getResource().getRequest().evaluatePreconditions(tag) != null)
					{
						LOG.debug("The WS call for item " + this + " was received with etag " + tag.getValue()
								+ " therefore caching was used, and the etag returned is " + tag.getValue());
						// ETag from client is equal to ETag computed for given resource -> resource was cached on client side
						// so we set response status to 304 (not modified)
						//result.setResponseStatus(304);
						responseBuilder.getResponse().status(304);
						return;
					}
					else
					{
						LOG.debug("The WS call for item " + this + " was received with etag " + tag.getValue()
								+ " therefore caching was not used, and the etag returned is " + tag.getValue());
						// ETag from client was null or is different than ETag computed for given resource -> resource has been changed since client's last visit
						// so we generate regular response and add ETag for future use
						responseBuilder.getResponse().tag(tag);
					}
				}

			}
		}
		chain.doProcess();
	}




	/**
	 * Generates uid for a resource based on its static {@link NodeConfig}.
	 */
	private String getUID(final RestResource resource, final RESOURCE resourceValue)
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
										+ method.getName() + ",orig value :" + methodResult + " , hash :" + methodResult.hashCode());
							}
							if (resourceHashCode == null)
							{
								resourceHashCode = Integer.valueOf(0);
							}
							resourceHashCode = Integer.valueOf((31 * resourceHashCode.intValue()) + methodResult.hashCode());
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
