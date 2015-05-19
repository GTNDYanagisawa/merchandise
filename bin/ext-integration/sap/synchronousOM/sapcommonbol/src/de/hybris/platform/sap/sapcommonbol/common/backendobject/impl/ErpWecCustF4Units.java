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

/**
 * Represents attributes of ERP function module ERP_WEC_CUST_F4_UNITS
 * 
 */
public class ErpWecCustF4Units {

    /**
     * SAP language code (non-ISO)
     */
    public static String iv_language = "IV_LANGUAGE";

    /**
     * Table of units
     */
    public static String et_physical_unit = "ET_PHYSICAL_UNIT";
    /**
     * SAP unit key (e.g. <code> ST </code> for piece)
     */
    public static String internal_name = "INTERNAL_NAME";
    /**
     * SAP unit id, language dependent (e.g. <code> PC </code> for piece in
     * English)
     */
    public static String external_name = "EXTERNAL_NAME";

    /**
     * Number of decimal places
     */
    public static String decimals = "DECIMALS";
    /**
     * Language dependent unit description
     */
    public static String description = "DESCRIPTION";
}
