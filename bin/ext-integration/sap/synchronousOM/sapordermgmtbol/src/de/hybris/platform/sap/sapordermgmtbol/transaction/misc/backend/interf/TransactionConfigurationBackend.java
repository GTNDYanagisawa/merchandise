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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.interf;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import java.util.Map;


/**
 * Communication interface between a shop and a back end. <br>
 * 
 */
public interface TransactionConfigurationBackend extends BackendBusinessObject
{
	/** identifies the field 'Country' of the ResultData for the taxCom table */
	public final static String T_COUNTRY = "COUNTRY";
	/** identifies the field 'State' of the ResultData for the taxCom table */
	public final static String STATE = "STATE";
	/** identifies the field 'County' of the ResultData for the taxCom table */
	public final static String COUNTY = "COUNTY";
	/** identifies the field 'City' of the ResultData for the taxCom table */
	public final static String T_CITY = "CITY";
	/** identifies the field 'ZipCode' of the ResultData for the taxCom table */
	public final static String ZIPCODE = "ZIPCODE";
	/** identifies the field 'TXJCD_L1' of the ResultData for the taxCom table */
	public final static String TXJCD_L1 = "TXJCD_L1";
	/** identifies the field 'TXJCD_L2' of the ResultData for the taxCom table */
	public final static String TXJCD_L2 = "TXJCD_L2";
	/** identifies the field 'TXJCD_L3' of the ResultData for the taxCom table */
	public final static String TXJCD_L3 = "TXJCD_L3";
	/** identifies the field 'TXJCD_L4' of the ResultData for the taxCom table */
	public final static String TXJCD_L4 = "TXJCD_L4";
	/** identifies the field 'TXJCD' of the ResultData for the taxCom table */
	public final static String TXJCD = "TXJCD";
	/** identifies the field 'OUTOF_CITY' of the ResultData for the taxCom table */
	public final static String OUTOF_CITY = "OUTOF_CITY";

	/**
	 * Read delivery types from cache (or from back-end, if cache is not ready)
	 * <ul>
	 * <li>CRM table crmc_ship_cond, value help crm_ship_cond</li>
	 * <li>ERP table tvsb, value help h_tvsb</li>
	 * </ul>
	 * 
	 * @return Map with < value(char 3), description>
	 * @throws BackendException
	 *            in case of issues with the JCO-Connection or ABAP-errors in the back-end
	 */
	public Map<String, String> getAllowedDeliveryTypes() throws BackendException;

	/**
	 * Read delivery types from back-end.
	 * <ul>
	 * <li>CRM table crmc_ship_cond, value help crm_ship_cond</li>
	 * <li>ERP table tvsb, value help h_tvsb</li>
	 * </ul>
	 * 
	 * @return Map with < value(char 3), description>
	 * @throws BackendException
	 *            in case of issues with the JCO-Connection or ABAP-errors in the back-end
	 */
	public Map<String, String> getAllowedDeliveryTypesFromBackend() throws BackendException;


}
