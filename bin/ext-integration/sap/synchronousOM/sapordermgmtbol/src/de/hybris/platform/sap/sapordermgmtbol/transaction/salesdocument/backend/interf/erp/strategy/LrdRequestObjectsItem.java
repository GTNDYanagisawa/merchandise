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
 * This enumeration represents the item aspects of LO-API we are interested in. Together with the aspects we specify
 * whether the rea-only text data is needed or not.
 * 
 * 
 */
public enum LrdRequestObjectsItem
{



	/**
	 * Main item data
	 */
	ITEM("X", "X", "X"),

	/**
	 * Item SD status
	 */
	IVSTAT("X", "", ""),

	/**
	 * Item partner data
	 */
	//PARTY("X", "X", "X"),

	/**
	 * Schedule lines
	 */
	SLINE("X", "", "X"),

	/**
	 * Texts
	 */
	TEXT("X", "", "X");



	private String comv_request = "";
	private String comr_request = "";
	private String def_request = "";

	LrdRequestObjectsItem(final String comv, final String comr, final String def)
	{
		comv_request = comv;
		comr_request = comr;
		def_request = def;
	}

	/**
	 * @return Do we need a comv segment (language independent data)
	 */
	public String getComv_request()
	{
		return comv_request;
	}

	/**
	 * @return Do we need the comr segment (language dependent read-only data)
	 */
	public String getComr_request()
	{
		return comr_request;
	}

	/**
	 * @return Do we need def request i.e. to we need to request this object
	 */
	public String getDef_request()
	{
		return def_request;
	}

}
