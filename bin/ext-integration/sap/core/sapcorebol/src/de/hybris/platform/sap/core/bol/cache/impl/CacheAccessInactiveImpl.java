/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 * 
 */
package de.hybris.platform.sap.core.bol.cache.impl;

import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.sap.core.bol.cache.CacheAccess;
import de.hybris.platform.sap.core.bol.cache.GenericCacheKey;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Cache implementation class that deactivates the cache implementation such that nothing will be cached.<br/>
 * The cache will always be empty, even when a put-method is called.
 * 
 */
public class CacheAccessInactiveImpl implements CacheAccess
{

	private Map<Object, Object> map = null;

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *           name of the cache region
	 * @param maxEntries
	 *           maximum number of entries
	 */
	public CacheAccessInactiveImpl(final String name, final int maxEntries)
	{
		this();
	}

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *           name of the cache region
	 * @param maxEntries
	 *           maximum number of entries
	 * @param evictionPolicy
	 *           eviction policy which is used
	 */
	public CacheAccessInactiveImpl(final String name, final int maxEntries, final String evictionPolicy)
	{
		this();
	}

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *           name of the cache region
	 * @param maxEntries
	 *           maximum number of entries
	 * @param evictionPolicy
	 *           eviction policy which is used
	 * @param exclusiveComputation
	 *           flag indicating if exclusive computation is used
	 * @param statsEnabled
	 *           flag indicating if statistics are stored or not
	 */
	public CacheAccessInactiveImpl(final String name, final int maxEntries, final String evictionPolicy,
			final boolean exclusiveComputation, final boolean statsEnabled)
	{
		this();
	}

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *           name of the cache region
	 * @param maxEntries
	 *           maximum number of entries
	 * @param evictionPolicy
	 *           eviction policy which is used
	 * @param exclusiveComputation
	 *           flag indicating if exclusive computation is used
	 * @param statsEnabled
	 *           flag indicating if statistics are stored or not
	 * @param ttlSeconds
	 *           time to live of the stored objects in seconds
	 */
	public CacheAccessInactiveImpl(final String name, final int maxEntries, final String evictionPolicy,
			final boolean exclusiveComputation, final boolean statsEnabled, final Long ttlSeconds)
	{
		this();
	}

	/**
	 * Standard constructor.
	 */
	public CacheAccessInactiveImpl()
	{
		map = new HashMap<Object, Object>();
	}

	/**
	 * Convenience method to get access to the empty "Cache".
	 * 
	 * @return cache map
	 * 
	 */
	public Map<Object, Object> getMap()
	{
		return map;
	}

	/**
	 * Convenience method to set the empty "Cache".
	 * 
	 * @param map
	 *           cache map
	 */
	public void setMap(final Map<Object, Object> map)
	{
		this.map = map;
	}

	@Override
	public Object get(final Object key)
	{
		return null;
	}

	@Override
	public Set<Object> getKeys()
	{
		return map.keySet();
	}


	@Override
	public int getNumObjects()
	{
		return map.size();
	}

	@Override
	public void put(final Object name, final Object object) throws SAPHybrisCacheException
	{
		//nothing to do
	}

	@Override
	public void remove(final Object key) throws SAPHybrisCacheException
	{
		if (!map.containsKey(key))
		{
			return;
		}
		map.remove(key);
	}

	@Override
	public void putIfAbsent(final Object name, final Object object) throws SAPHybrisCacheException
	{
		//nothing to do
	}

	@Override
	public Object getWithLoader(final Object key, final CacheValueLoader<?> loader)
	{
		final GenericCacheKey genericCacheKey = generateGenericCacheKey(key);
		Object co = map.get(genericCacheKey);
		if (co == null && loader != null)
		{
			final Object object = loader.load(genericCacheKey);
			map.put(key, object);
			co = object;
		}
		return co;
	}

	/**
	 * Generates a {@link GenericCacheKey} object from key.
	 * 
	 * @param key
	 *           cache key
	 * @return {@link GenericCacheKey}
	 */
	private GenericCacheKey generateGenericCacheKey(final Object key)
	{
		if (key instanceof GenericCacheKey)
		{
			return (GenericCacheKey) key;
		}
		else
		{
			return new GenericCacheKey(key, "SAPObjects");
		}
	}

	@Override
	public void clearCache()
	{
		map.clear();
	}

}
