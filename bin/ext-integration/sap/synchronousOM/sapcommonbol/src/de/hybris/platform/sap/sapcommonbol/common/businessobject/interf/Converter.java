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
package de.hybris.platform.sap.sapcommonbol.common.businessobject.interf;

/*****************************************************************************
 Class:        Converter.java
 Copyright (c) 2010, SAP AG, Germany, All rights reserved.
 Author:       SAP
 Created:      Aug 18, 2010
 Version:      1.0

 *****************************************************************************/


import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;

import java.math.BigDecimal;



/**
 * Converter BO functionality. <br>
 * Retrieves currency and unit scales and also is able to convert unit keys (language independent) into unit ID's.<br>
 * <br>
 * The language does not need to be provided, it is taken from the session {@link java.util.Locale locale}.
 *
 */
public interface Converter
{

	/**
	 * Identifier to access the Converter BO from the BOManager
	 */
	public static String BO_TYPE = "Converter";

	/**
	 * Converts the unit key (example <code> ST </code> ) into the language dependent ID (example <code> PC </code> for
	 * en_US)
	 *
	 * @param unitKey
	 *           language independent unit key
	 * @return language dependent unit ID
	 * @throws BusinessObjectException
	 */
	public String convertUnitKey2UnitID(String unitKey) throws BusinessObjectException;

	/**
	 * Converts the unit ID (example <code> PC </code> for en_US) into the unit key (example <code> ST </code>)
	 *
	 * @param unitID
	 *           language dependent unit ID
	 * @return unit key
	 * @throws BusinessObjectException
	 */
	public String convertUnitID2UnitKey(String unitID) throws BusinessObjectException;



	/**
	 * Retrieves the number of decimal places for a currency (which might be an ISO currency but doesn't need to be). <br>
	 * Basis is the ERP/CRM customizing in tables TCURX and TCURC. <br>
	 * Examples: USD typically has 2 decimal places, JPY has zero.
	 *
	 * @param sapCurrencyCode
	 *           currency code. Must be available in customizing tabke TCURC
	 * @return number of decimal places
	 * @throws BusinessObjectException
	 */
	public Integer getCurrencyScale(String sapCurrencyCode) throws BusinessObjectException;

	/**
	 * Retrieves the number of decimal places for units according to ERP/CRM customizing. This is relevant for UI display
	 * in case the unit amount is not an integer.
	 *
	 * @param unitKey
	 *           the language independet unit key. Must be available in customizing table T006
	 * @return the number of decimal places for UI display
	 * @throws BusinessObjectException
	 */
	public int getUnitScale(String unitKey) throws BusinessObjectException;


	/**
	 * @param unitKey
	 *           the language independet unit key. Must be available in customizing table T006
	 * @return BigDecimal representing the smallest value given a scale
	 * @throws BusinessObjectException
	 *
	 */
	public BigDecimal getMinimumScaleValue(String unitKey) throws BusinessObjectException;
}
