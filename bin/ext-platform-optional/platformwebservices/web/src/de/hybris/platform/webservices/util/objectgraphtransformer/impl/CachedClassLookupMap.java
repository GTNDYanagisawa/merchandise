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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CachedClassLookupMap<V extends Object> implements Map<Class<?>, V>
{
	// list which holds dynamically created "soft" keys
	private List<Class<?>> softEntriesKeyList = new ArrayList<Class<?>>();

	// dynamic Map; 
	// contains all directly added entries and 'dynamic' entries.
	// dynamic entries are detected during runtime
	// the key of a dynamic entry is at least assignable to an existing key of a static entry
	private final Map<Class<?>, V> mergedMap = new HashMap<Class<?>, V>();

	// static map
	// contains all directly added entries
	private final Map<Class<?>, V> staticMap = new HashMap<Class<?>, V>();

	public CachedClassLookupMap()
	{
		super();
	}

	public CachedClassLookupMap(final CachedClassLookupMap base)
	{
		super();
		mergedMap.putAll(base.mergedMap);
		softEntriesKeyList.addAll(base.softEntriesKeyList);
		staticMap.putAll(base.getStaticMap());
	}

	/**
	 * @param key
	 *           the lookup key which must be of type {@link Class}
	 * 
	 * @return a value which is fetched directly or via internal class lookup mechanism
	 * 
	 */
	@Override
	public V get(final Object key)
	{
		if (key instanceof Class)
		{
			V result = null;

			// if there's already an entry take it
			if (mergedMap.containsKey(key))
			{
				result = mergedMap.get(key);
			}
			// otherwise start dynamic lookup based on class/interface hierarchy...  
			else
			{
				final Class clazz = (Class) key;

				//... first lookup: class hierarchy
				Class curClass = clazz;
				while (result == null && curClass.getSuperclass() != null)
				{
					result = mergedMap.get(curClass = curClass.getSuperclass());
				}

				//... second lookup: interface hierarchy
				curClass = clazz;
				while (result == null && curClass != null)
				{
					result = getByIntf(curClass.getInterfaces());
					curClass = curClass.getSuperclass();
				}


				// store result to prevent further lookups with same key
				// thats done "soft" which means that entry gets cleared after a put operation
				mergedMap.put(clazz, result);

				// remember all "soft" entries 
				this.softEntriesKeyList.add(clazz);
			}

			return result;
		}
		else
		{
			throw new IllegalArgumentException("Only keys of type class are supported (got " + key + ")");
		}
	}

	public Map<Class<?>, V> getStaticMap()
	{
		return this.staticMap;
	}



	private V getByIntf(final Class[] intf)
	{
		V result = null;
		for (int i = 0; i < intf.length && result == null; i++)
		{
			result = mergedMap.get(intf[i]);

			if (result == null)
			{
				result = getByIntf(intf[i].getInterfaces());
			}
		}
		return result;

	}

	@Override
	public void clear()
	{
		mergedMap.clear();
		this.softEntriesKeyList = new ArrayList<Class<?>>();
	}

	@Override
	public V put(final Class<?> key, final V value)
	{
		//slower puts but faster lookups therefore
		this.invalidateDynamicKeys();
		this.staticMap.put(key, value);
		return this.mergedMap.put(key, value);
	}

	@Override
	public boolean containsKey(final Object key)
	{
		return mergedMap.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value)
	{
		return mergedMap.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<Class<?>, V>> entrySet()
	{
		return mergedMap.entrySet();
	}

	@Override
	public boolean isEmpty()
	{
		return mergedMap.isEmpty();
	}

	@Override
	public Set<Class<?>> keySet()
	{
		return mergedMap.keySet();
	}

	@Override
	public void putAll(final Map<? extends Class<?>, ? extends V> map)
	{
		this.invalidateDynamicKeys();
		mergedMap.putAll(map);
		staticMap.putAll(map);
	}

	@Override
	public V remove(final Object key)
	{
		this.invalidateDynamicKeys();
		staticMap.remove(key);
		return mergedMap.remove(key);
	}

	@Override
	public int size()
	{
		return this.mergedMap.size();
	}

	@Override
	public Collection<V> values()
	{
		return mergedMap.values();
	}

	private void invalidateDynamicKeys()
	{
		for (final Class clazz : this.softEntriesKeyList)
		{
			// putting a new entry might mess up with previous cached super-class lookups
			// so remove all entries that have been created by super-class lookups 
			this.mergedMap.remove(clazz);
			this.softEntriesKeyList = new ArrayList<Class<?>>();
		}
	}


	@Override
	public int hashCode()
	{
		return staticMap.hashCode();
	}

	/**
	 * This instance equals another one of same type when the hard-configured internal map equals that one from passed
	 * object. Dynamic found values does not have an influence (except performance) on lookup behavior thats why these
	 * values are not included in equality check.
	 */
	@Override
	public boolean equals(final Object obj)
	{
		return (obj instanceof CachedClassLookupMap) && this.getStaticMap().equals(((CachedClassLookupMap) obj).getStaticMap());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return mergedMap.toString();
	}




}
