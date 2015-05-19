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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl;

import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.cache.CacheAccess;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.interf.TransactionConfigurationBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.CheckoutConfigurationBackendTools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;


/**
 * Base class for backend implementation of TransactionConfiguration
 */
public abstract class TransactionConfigurationBase extends BackendBusinessObjectBaseJCo implements
		TransactionConfigurationBackend
{

	/**
	 * Logging instance
	 */
	protected static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(TransactionConfigurationBase.class.getName());
	CheckoutConfigurationBackendTools tools = new CheckoutConfigurationBackendTools();

	private static final String CACHEKEY_DELIVERY_TYPES = "DELIVERY_TYPES";
	static final String RFC_NAME_LANGUAGE_SWITCH_DELIV_CUSTOMIZING = "ERP_WEC_CUST_SHIPCOND";
	/**
	 * Cache access for retrieving delivery type customizing
	 */
	@Resource(name = SapordermgmtbolConstants.BEAN_ID_CACHE_DELIVERY_TYPES)
	protected CacheAccess deliverTypeCacheAccess;

	/**
	 * Map of delivery types
	 */
	protected Map<String, String> deliveryTypes;

	@Override
	public Map<String, String> getAllowedDeliveryTypes() throws BackendException
	{
		if (isLanguageSwitchDelivCustomizingAvailable(null))
		{
			return getDeliveryTypes();
		}
		else
		{
			return deliveryTypes;
		}
	}

	/**
	 * Initializes delivery type storage
	 * 
	 * @return List of delivery types
	 * @throws BackendException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> initAllowedDeliveryTypes() throws BackendException
	{
		sapLogger.entering("initAllowedDeliveryTypes");

		// sync access to cache
		synchronized (TransactionConfigurationBase.class)
		{
			final String rfcCacheKey = CACHEKEY_DELIVERY_TYPES + LocaleUtil.getLocale().getLanguage();

			deliveryTypes = (Map<String, String>) deliverTypeCacheAccess.get(rfcCacheKey);
			if (deliveryTypes == null)
			{
				try
				{
					deliveryTypes = getAllowedDeliveryTypesFromBackend();
					deliverTypeCacheAccess.put(rfcCacheKey, deliveryTypes);
					if (sapLogger.isDebugEnabled())
					{
						sapLogger.debug("loaded from backend delivery types: {0}", new Object[]
						{ deliveryTypes });
					}
				}
				catch (final SAPHybrisCacheException e)
				{
					throw new BackendRuntimeException("Issue during cache access.");
				}
			}
		}

		sapLogger.exiting();
		return deliveryTypes;
	}

	/**
	 * @return Key of cache region for delivery type customizing
	 */
	protected abstract String getCacheRegionForCustomizing();

	@Override
	public void initBackendObject() throws BackendException
	{
		super.initBackendObject();
		if (!isLanguageSwitchDelivCustomizingAvailable(null))
		{
			initAllowedDeliveryTypes();
		}
	}

	/**
	 * @param connection
	 *           JCO connection to the back end
	 * @return Is FM ERP_WEC_CUST_SHIPCOND available?
	 */
	protected boolean isLanguageSwitchDelivCustomizingAvailable(final JCoConnection connection)
	{
		JCoConnection checkConnection = connection;
		if (checkConnection == null)
		{
			checkConnection = getDefaultJCoConnection();
		}
		try
		{
			return checkConnection.isFunctionAvailable(RFC_NAME_LANGUAGE_SWITCH_DELIV_CUSTOMIZING);
		}
		catch (final BackendException ex)
		{
			throw new ApplicationBaseRuntimeException("Could not determine FM availability", ex);
		}

	}

	/**
	 * Get cached delivery types via newly introduced FM ERP_WEC_CUST_SHIPCOND . If nothing is part of the cache yet,
	 * backend will be invoked.
	 * 
	 * @return Map of delivery types with code as key and language dependent description as value
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, String> getDeliveryTypes()
	{
		// sync access to cache
		synchronized (TransactionConfigurationBase.class)
		{
			final String rfcCacheKey = CACHEKEY_DELIVERY_TYPES + LocaleUtil.getLocale().getLanguage();

			deliveryTypes = (Map<String, String>) deliverTypeCacheAccess.get(rfcCacheKey);
			if (deliveryTypes == null)
			{
				try
				{
					deliveryTypes = getDeliveryTypesFromBackend();
					deliverTypeCacheAccess.put(rfcCacheKey, deliveryTypes);
					if (sapLogger.isDebugEnabled())
					{
						sapLogger.debug("loaded from backend delivery types: {0}", new Object[]
						{ deliveryTypes });
					}
				}
				catch (final SAPHybrisCacheException e)
				{
					throw new BackendRuntimeException("Issue during cache access.");
				}
			}
			return deliveryTypes;
		}

	}

	/**
	 * Get delivery types from backend via newly introduced FM ERP_WEC_CUST_SHIPCOND .
	 * 
	 * @return Map of delivery types with code as key and language dependent description as value
	 */
	protected Map<String, String> getDeliveryTypesFromBackend()
	{
		try
		{
			final JCoConnection connection = getDefaultJCoConnection();
			final JCoFunction jcoFunction = connection.getFunction(RFC_NAME_LANGUAGE_SWITCH_DELIV_CUSTOMIZING);
			jcoFunction.getImportParameterList().setValue("IV_LANGU",
					ConversionTools.getR3LanguageCode(LocaleUtil.getLocale().getLanguage()));
			connection.execute(jcoFunction);
			final JCoTable table = jcoFunction.getTableParameterList().getTable("TT_SHIPCONDS");
			if (table.getNumRows() > 0)
			{
				final Map<String, String> result = new HashMap<String, String>();
				table.firstRow();
				do
				{
					result.put(table.getString("VSBED"), table.getString("VTEXT"));
				}
				while (table.nextRow());

				return result;
			}
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Could not fetch shipping conditons from back end", e);
		}
		return Collections.emptyMap();
	}

}
