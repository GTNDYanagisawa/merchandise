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
package de.hybris.platform.sap.core.jco.runtime;

import com.sap.conn.jco.ext.JCoSessionReference;


/**
 * SAPJCoSessionReference is a simple reference to a SAPHyprisSession ID.
 */
public class SAPJCoSessionReference implements JCoSessionReference
{

	private final String id;


	/**
	 * Constructor.
	 * 
	 * @param id
	 *           session reference id
	 */
	public SAPJCoSessionReference(final String id)
	{
		super();
		this.id = id;
	}

	@Override
	public void contextFinished()
	{
		// Not required in SAP hybris integration project

	}

	@Override
	public void contextStarted()
	{
		// Not required in SAP hybris integration project

	}

	@Override
	public String getID()
	{
		return id;
	}
}
