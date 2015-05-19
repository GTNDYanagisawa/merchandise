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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy;

/**
 * This enumeration represents the header aspects of LO-API we are interested in. Together with the aspects we specify
 * whether the rea-only text data is needed or not.
 * 
 * 
 */
public enum LrdRequestObjectsHead
{

	/**
	 * Condition data. Used for deriving freight
	 */
	COND("X", "X", "X"),

	/**
	 * Header data
	 */
	HEAD("X", "X", "X"),


	/**
	 * Header status information
	 */
	HVSTAT("X", "X", "X"),

	/**
	 * Incompletion log
	 */
	INCLOG("", "", ""),

	/**
	 * Partner data
	 */
	PARTY("X", "X", "X"),

	/**
	 * Text data
	 */
	TEXT("X", "", "X");

	private String comv_request = "";
	private String comr_request = "";
	private String def_request = "";

	LrdRequestObjectsHead(final String comv, final String comr, final String def)
	{
		comv_request = comv;
		comr_request = comr;
		def_request = def;
	}

	/**
	 * @return Do we need comv request (standard non-localized data)
	 */
	public String getComv_request()
	{
		return comv_request;
	}

	/**
	 * @return Do we need read-only localized data
	 */
	public String getComr_request()
	{
		return comr_request;
	}

	/**
	 * @return Do we need def request i.e. do we need to request this data
	 */
	public String getDef_request()
	{
		return def_request;
	}

}
