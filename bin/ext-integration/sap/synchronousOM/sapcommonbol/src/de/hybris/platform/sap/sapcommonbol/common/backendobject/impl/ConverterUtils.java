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
package de.hybris.platform.sap.sapcommonbol.common.backendobject.impl;

import com.sap.conn.jco.JCoRecord;

/**
 */
public class ConverterUtils {
    protected static final String EMPTY_STRING = "";

    // function group CNV_UNITS
    /**
     * Function module name that retrieves F4 data for quantity units
     */
    public static String CRM_WEC_CUST_F4_UNITS = "CRM_WEC_CUST_F4_UNITS";

    /**
     * Function module name that provides physical units
     */
    public static String ERP_WEC_CUST_F4_UNITS = "ERP_WEC_CUST_F4_UNITS ";

    /**
     * Import parameter for the ABAP language. Must have ABAP format (e.g. D for
     * Germany)
     */
    public static final String LANGUAGE_INPUT = "IV_LANGUAGE";

    // function group CNV_CURRENCY
    /**
     * Function module name that retrieves F4 data for currencies
     */
    public static String PRC_CNV_CURR_F4_CURRENCY_UNITS = "PRC_CNV_CURR_F4_CURRENCY_UNITS";
    /**
     * Function module name that provides physical units
     */
    public static String ERP_WEC_CUST_F4_CURRENCY = "ERP_WEC_CUST_F4_CURRENCY";

    /**
     * @param s String to be checked
     * @return true, if s is not null and length >0
     */
    public static boolean provided(String s) {
        return s != null && s.length() != 0;
    }

    /**
     * @param s String to be checked
     * @return true, if s is not null and length=1
     */
    public static boolean singleCharacter(String s) {
        return s != null && s.length() == 1;
    }

    /**
     * This methods avoids creation of independend copies of "" in order to save
     * memory.
     * 
     * @param coRecord
     * @param arg0
     * @return EMPTY_STRING or value from coRecord
     */
    public static String getString(JCoRecord coRecord, String arg0) {
        String result = coRecord.getString(arg0);
        if (result == null || result.isEmpty()) {
            return EMPTY_STRING;
        }
        else {
            return result;
        }
    }
}
