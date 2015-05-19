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
package de.hybris.platform.sap.core.extensibility.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


/**
 * This container can be used to pass data between layers of the SAP hybris integration framework.<br>
 * <b>NOTE:</b><br/>
 * This context MUST NOT be used in standard development. It is a tool for extending the functionality of the standard
 * in CUSTOMER projects.
 * <p/>
 * The scope of the container is an request (usually one HTTP request). After the request is finished the content of the
 * context is cleared by the framework. If you have to store data you should do it in the appropriate layer (HTTP
 * session context, Business Object, Backend Object/Backend Context) <br/>
 * You can access the request context as follows:<br/>
 * <b>Interaction Layer:</b></br> Within an action: getRequestContext()<br/>
 * <b>Business Object Layer:</b><br/>
 * Within an Business Object: getRequestContext()<br/>
 * <b>Business Logic Service Layer:</b><br/>
 * Within a backend object: getBackendObjectSupport().getRequestContext()<br/>
 * <b>Connection Event Listener:</b> ConnectionEvent.getRequestContext()<br/>
 * 
 */

public class RequestContext
{

	private static final Logger log = Logger.getLogger(RequestContext.class.getName());

	/**
	 * Constant can be used when storing request context in an other context.
	 */
	public static final String REQUEST_CONTEXT = "de.hybris.platform.sap.core.request.context";

	/**
	 * Map which keeps the request data.
	 */
	protected Map<Object, Object> mData = new HashMap<Object, Object>(); // NOPMD

	/**
	 * This method stores arbitrary data within this context.
	 * 
	 * @param name
	 *           key with which the specified value is to be associated.
	 * @param value
	 *           value to be associated with the specified key.
	 */
	public void addData(final Object name, final Object value)
	{
		if (log.isDebugEnabled())
		{
			log.debug("addData() [name]='" + name + "' [objid]='" + this + "'");
		}
		mData.put(name, value);
	}

	/**
	 * This method retrieves data associated with name from the context.
	 * 
	 * @param name
	 *           key with which the specified value is associated.
	 * @return value which is associated with the specified key. If object is not found <code>null</code> is returned.
	 */
	public Object getData(final Object name)
	{
		if (log.isDebugEnabled())
		{
			log.debug("getData() [name]='" + name + "' [objid]='" + this + "'");
		}
		return mData.get(name);
	}

	/**
	 * This method retrieves a Set of stored data.
	 * 
	 * @return set of stored data
	 */
	@SuppressWarnings("rawtypes")
	public Set getDataValues()
	{
		return mData.entrySet();
	}

	/**
	 * This method removes extension data from the context.
	 * 
	 * @param name
	 *           key with which the specified value is associated.
	 */
	public void removeData(final Object name)
	{
		if (log.isDebugEnabled())
		{
			log.debug("removeData() [name]='" + name + "' [objid]='" + this + "'");
		}
		mData.remove(name);
	}

	/**
	 * This method removes all extensions data from the context.
	 */
	public void removeDataValues()
	{
		if (log.isDebugEnabled())
		{
			log.debug("removeDataValues() [objid]='" + this + "'");
		}
		mData.clear();
	}

	/**
	 * Sets the data map to the given map.
	 * 
	 * @param dataMap
	 *           the new data Map
	 */
	public void setDataMap(final Map<Object, Object> dataMap)
	{
		mData = dataMap;
	}

	/**
	 * Returns the data Map.
	 * 
	 * @return am Map containing the stored data
	 */
	public Map<Object, Object> getDataMap()
	{
		return mData;
	}

	/**
	 * Returns a reqest context stored as request attribute. This method is especially usefull when getting the
	 * RequestContext within a JSP
	 * 
	 * @param request
	 *           HTTP request it available in request or <code>null</code>
	 * @return the request context
	 */
	public static RequestContext getRequestContext(final HttpServletRequest request)
	{
		if (request == null)
		{
			return null;
		}
		else
		{
			return (RequestContext) request.getAttribute(RequestContext.REQUEST_CONTEXT);
		}
	}

}
