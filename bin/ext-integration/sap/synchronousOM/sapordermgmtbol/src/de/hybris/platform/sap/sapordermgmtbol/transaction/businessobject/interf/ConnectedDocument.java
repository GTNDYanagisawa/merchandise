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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the ConnectedDocument object<br>
 * 
 */
public interface ConnectedDocument extends ConnectedObject {

    /**
     * Application Type Order.
     */
    public static final String ORDER = "";
    /**
     * Application Type Billing.
     */
    public static final String BILL = "BILL";
    /**
     * Application Type Delivery.
     */
    public static final String DLVY = "DLVY";

    /**
     * Returns the application type of the document(e.g. one order document,
     * billing document, etc.).<br>
     * 
     * @return Document application type
     */
    public String getAppTyp();

    /**
     * Sets the application type of the document(e.g. one order document,
     * billing document, etc.).<br>
     * 
     * @param appTyp Document application type
     */
    public void setAppTyp(String appTyp);

}
