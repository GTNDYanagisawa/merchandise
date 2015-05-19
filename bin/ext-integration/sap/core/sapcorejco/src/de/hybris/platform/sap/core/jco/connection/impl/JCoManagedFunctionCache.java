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
package de.hybris.platform.sap.core.jco.connection.impl;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import de.hybris.platform.sap.core.common.util.GenericFactoryProvider;
import de.hybris.platform.sap.core.jco.connection.impl.JCoFunctionLoader.JCoFunctionAttr;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;



/**
 * Cache for Managed Functions.
 */
public class JCoManagedFunctionCache
{

	
	private EHCacheRegion jcoCacheRegion;

	/**
	 * Parameter value for connection parameter cacheType which indicates that this function should be cached. Parameter
	 * is set in spring definition of JCoConnectionParameter.
	 */
	public static final String DEFAULT_CACHE_TYPE = "Default";


	/**
	 * Generic logger.
	 */
	private static final Logger logger = Logger.getLogger(JCoManagedFunctionCache.class.getName());

	/**
	 * Standard Constructor<br>
	 * .
	 */
	public JCoManagedFunctionCache()
	{
		jcoCacheRegion = GenericFactoryProvider.getInstance().getBean("sapCoreConnectionCacheRegion");
	}

	/**
	 * Check if given function should be cached.
	 * 
	 * @param function
	 *           function to be cached.
	 * @return true if function should be cached. False otherwise.
	 */
	public boolean isFunctionCached(final JCoManagedFunction function)
	{

		if (jcoCacheRegion == null)
		{
			return false;
		}

		if (function.getConnectionParameter() == null)
		{
			return false;
		}


		if (function.getConnectionParameter().getCacheType() == null)
		{
			return false;
		}

		if (!function.getConnectionParameter().getCacheType().equalsIgnoreCase(DEFAULT_CACHE_TYPE))
		{
			return false;
		}

		return true;

	}

	/**
	 * Execute function which is marked for caching. <br>
	 * First call gets the values from the backend and stores the result in the cache. Subsequent calls are served from
	 * the cache.
	 * 
	 * @param destination
	 *           destination.
	 * @param function
	 *           function.
	 * @throws BackendException
	 *            thrown in case of execution fails.
	 */
	@SuppressWarnings("deprecation")
	public void executeCachedFunction(final JCoDestination destination, final JCoManagedFunction function) throws BackendException
	{
		if (jcoCacheRegion == null)
		{
			throw new BackendException("cache access not available");
		}

		try
		{
			final CacheKey cacheKey = getJCoFunctionKey(destination, function);

			logger.debug("Serving Function Module \"" + function.getName() + "\" from cache");

			final JCoFunctionAttr jCoFunctionAttr = new JCoFunctionLoader.JCoFunctionAttr(destination, function);
			final JCoFunctionLoader jcoFuncLoader = new JCoFunctionLoader();
			jcoFuncLoader.setFuncAttr(jCoFunctionAttr);
			
			final JCoFunction cachedFunc = (JCoFunction) jcoCacheRegion.getWithLoader(cacheKey, jcoFuncLoader);

			Iterator<JCoField> iterator;
			final JCoParameterList expParam = cachedFunc.getExportParameterList();
			if (expParam != null)
			{
				iterator = expParam.iterator();
				int i = 0;
				while (iterator.hasNext())
				{
					final JCoField next = iterator.next();
					final Object obj = next.getValue();
					function.getExportParameterList().setValue(i, obj);
					i = i + 1;
				}
			}

			final JCoParameterList tabParam = cachedFunc.getTableParameterList();
			if (tabParam != null)
			{
				iterator = tabParam.iterator();
				int j = 0;
				while (iterator.hasNext())
				{
					final JCoField next = iterator.next();
					final Object obj = next.getValue();
					function.getTableParameterList().setValue(j, obj);
					j = j + 1;
				}
			}

		}
		catch (final JCoException ex)
		{
			final String errMsg = ex.toString();
			logger.log(Priority.ERROR, "Error during execution JCoFunction " + errMsg);
			final String msg = " Error during execution JCoFunction";
			throw new BackendException(msg, ex);
		}
	}


	/**
	 * This method returns a key for a given JCoFunction. <br>
	 * The key is constructed using the function name, the language and a concatenation of the import parameters and
	 * values.
	 * 
	 * @param destination
	 *           destination
	 * @param function
	 *           function for which a key has to be create
	 * @return function key
	 * @throws JCoException
	 *            Exception
	 */
	private CacheKey getJCoFunctionKey(final JCoDestination destination, final JCoManagedFunction function) throws JCoException
	{
		final StringBuffer key = new StringBuffer(destination.getAttributes().getClient());


		key.append(destination.getAttributes().getLanguage());

		key.append(function.getName());

		if (function.getConnectionParameter().getFunctionModuleToBeReplaced() != null)
		{
			key.append("[R]->").append(function.getConnectionParameter().getFunctionModule());
		}

		final JCoParameterList impParamList = function.getImportParameterList();

		if (impParamList == null)
		{
			return new JCoManagedFunctionCacheKey(key.toString());
		}

		final int numParams = impParamList.getFieldCount();


		for (int i = 0; i < numParams; i++)
		{
			if (impParamList.getListMetaData().isStructure(i))
			{
				key.append(getKey(impParamList.getStructure(i)));
			}
			else
			{
				key.append(impParamList.getString(i));
			}
		}

		return new JCoManagedFunctionCacheKey(key.toString());
	}

	/**
	 * Recursive function which creates a String representation of a (nested) JCo structure.
	 * 
	 * @param struct
	 *           JCoStructure object
	 * @return a String representation of the structure
	 */
	private String getKey(final JCoStructure struct)
	{

		final StringBuffer sb = new StringBuffer();

		final int numParams = struct.getFieldCount();

		for (int i = 0; i < numParams; i++)
		{

			// in case of nested structure
			if (struct.getRecordMetaData().isStructure(i))
			{
				sb.append(getKey(struct.getStructure(i)));
			}
			else
			{
				sb.append(struct.getString(i));
			}
		}
		return sb.toString();
	}
}
