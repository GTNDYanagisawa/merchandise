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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import java.util.HashMap;
import java.util.Map;

import com.sap.conn.jco.JCoTable;


/**
 * Handles the object instance references for the LORD API. The object instances are stored in table parameter
 * TT_OBJINST.
 */
public class ObjectInstances
{

	private final Map<String, SingleObjectInstance> objInstMap;

	private final static String EMPTY_STRING = "";

	/**
	 * Creates a map for <code>TT_OBJINST</code> parameter of the LORD API. This map contains the parent relations for
	 * the objects provided in the different tables of the ERP_LORD_GET_ALL function module. Key of the map is the
	 * <code>HANDLE</code>.
	 * 
	 * @param ttObjInst
	 *           jCo Table containing the T<code>TT_OBJINST</code> instances
	 */
	public ObjectInstances(final JCoTable ttObjInst)
	{

		// Create a map with object instances
		final int numObjInst = ttObjInst.getNumRows();
		objInstMap = new HashMap<String, SingleObjectInstance>(numObjInst);

		// store all provided rows in the map
		for (int i = 0; i < numObjInst; i++)
		{

			ttObjInst.setRow(i);

			final SingleObjectInstance objInst = new SingleObjectInstance();
			objInst.setHandle(ttObjInst.getString("HANDLE"));
			objInst.setHandleParent(ttObjInst.getString("HANDLE_PARENT"));
			objInst.setObjectId(ttObjInst.getString("OBJECT_ID"));

			objInstMap.put(objInst.getHandle(), objInst);
		}
	}

	/**
	 * Returns the parent handle for a given handle.
	 * 
	 * @param handle
	 *           child handle
	 * @return handle of the parent null, when there is no object instance for the handle EMPTY_STRING, when the handle
	 *         has no parent
	 */
	public String getParent(final String handle)
	{

		if (handle == null)
		{
			return null;
		}

		final SingleObjectInstance objInst = objInstMap.get(handle);
		if (objInst == null)
		{
			return null;
		}

		String retVal = objInst.getHandleParent();
		if (retVal == null)
		{
			retVal = EMPTY_STRING;
		}

		return retVal;
	}

	/**
	 * Returns the object type for a given handle. This is the value of the field <code>OBJECT_ID</code>.
	 * 
	 * @param handle
	 *           object handle
	 * @return the object type of the handle; null, if object type cannot be retrieved.
	 */
	public String getObjectType(final String handle)
	{

		// invalid call
		if (handle == null)
		{
			return null;
		}

		// find the entry with the handle
		final SingleObjectInstance objInst = objInstMap.get(handle);
		if (objInst == null)
		{
			return null;
		}

		// read the object id of the entry
		final String retVal = objInst.getObjectId();


		return retVal;
	}

	/**
	 * Bean to store a single data record.
	 */
	private static class SingleObjectInstance
	{

		private String handle;
		private String handleParent;
		private String objectId;

		public String getHandle()
		{
			return handle;
		}

		public String getHandleParent()
		{
			return handleParent;
		}

		public String getObjectId()
		{
			return objectId;
		}

		public void setHandle(final String string)
		{
			handle = string;
		}

		public void setHandleParent(final String string)
		{
			handleParent = string;
		}

		public void setObjectId(final String string)
		{
			objectId = string;
		}
	}

}
