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
package de.hybris.platform.sap.orderexchange.constants;

/**
 * 
 * Partner roles for sales order processing as defined in SAP ERP. Corresponds to SAP defined entries of table TPAR
 * 
 */
public enum PartnerRoles
{

	@SuppressWarnings("javadoc")
	SOLD_TO("AG"), @SuppressWarnings("javadoc")
	CONTACT("AP"), @SuppressWarnings("javadoc")
	SHIP_TO("WE"), @SuppressWarnings("javadoc")
	BILL_TO("RE");

	private String code;

	private PartnerRoles(final String code)
	{
		this.code = code;
	}

	@SuppressWarnings("javadoc")
	public String getCode()
	{
		return code;
	}
}
