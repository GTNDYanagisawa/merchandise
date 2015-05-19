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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util;

/**
 * Container object for the GetAll read Parameters. <br>
 * 
 * @version 1.0
 */
public class GetAllReadParameters {

    /**
     * Flag if incompletion log is requested.
     */
    public boolean isIncompletionLogRequested = false;
    
    /**
     * Freight condition type of the header.
     */
    public String headerCondTypeFreight = "";
    
    /**
     * Sub total including freight costs.
     */
    public String subTotalItemFreight = "";
    
    /**
     * Indicator if IPC price attributes have to be set.<br>
     */
    public boolean setIpcPriceAttributes = false;
    
    /**
     * The description of the shipping conditions.<br>
     */
    public boolean shippingConditionsAsText = false;
}