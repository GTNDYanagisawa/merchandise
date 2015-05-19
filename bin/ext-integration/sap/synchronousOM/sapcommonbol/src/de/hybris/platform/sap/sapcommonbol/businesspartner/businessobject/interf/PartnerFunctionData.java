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
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf;

/**
 * Defines the partner functions.
 * 
 * The partner functions could used in the sales document
 * to integrate the business partner in their different functions.
 * 
 */
public interface PartnerFunctionData {

	/** 
	 * constant for the partner function soldto
	 */
    public static final String SOLDTO    = "SOLDTO";

    /** 
     * constant for the partner function payer
     */
    public static final String PAYER    = "PAYER";

    /** 
     * constant for the partner function billto
     */
    public static final String BILLTO    = "BILLTO";

    /** 
     * constant for the partner function shipto.<br>
     * <b>Note that the ship to isn't support as partner function in Java Layer </b>
     */
    public static final String SHIPTO    = "SHIPTO";


	/** 
	 * constant for the partner function soldfrom
	 */
    public static final String SOLDFROM  = "SOLDFROM";

	/** 
	 * constant for the partner function contact
	 */
    public static final String CONTACT   = "CONTACT";

	/** 
	 * constant for the partner function reseller
	 */
    public static final String RESELLER  = "RESELLER";

	/** 
	 * constant for the partner function sales prospect
	 */
    public static final String SALES_PROSPECT  = "SALES_PROSPECT";
    
	/** 
	 * Constant for the partner function agent. An agent is an employee
	 * that is not related to a business partner. Used for BOB scenarios.
	 */
    public static final String AGENT  = "AGENT";

	/** 
	 * Constant for the partner function responsible at partner. 
	 */
    public static final String RESP_AT_PARTNER  = "RESP_AT_PARTNER";

	/** 
	 * Constant for the partner function end customer. 
	 */
    public static final String END_CUSTOMER  = "END_CUSTOMER";

    
    /** 
     * constant for the partner function ship from. <br>
     */
    public static final String SHIPFROM    = "SHIPFROM";


	/** 
	 * constant for the partner function vendor
	 */
	public static final String VENDOR    = "VENDOR";

	/**
	 * 
	 * constant for the partner function responsible employee
	 */
	public static final String RESP_EMPLOYEE = "RESP_EMPLOYEE";
	
    /**
     * Returns the name of the partner function
     * @return name of partner function
     */
    public String getName();

}