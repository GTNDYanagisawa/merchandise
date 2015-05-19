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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf;


/**
 * Enumeration of DocumentTypes supported by Sales transaction module.
 * 
 */
public enum SalesDocumentType
{

	/**
	 * A document type that can be displayed on the Order Confirmation Page. Typically an Order BO is used.
	 */
	ORDERCONFIRMATION("orderconfirmation"),
	/**
	 * A document type that can be displayed/changed on the Basket Page. Typically an Basket BO is used.
	 */
	BASKET("basket"),
	/**
	 * A document type that can be displayed on the Order Page. Typically an Order BO is used.
	 */
	ORDER("order"),
	/**
	 * A document type that can be displayed on the Quotation Page. Typically an Quotation BO is used.
	 */
	QUOTATION("quotation"),
	/**
	 * A document type that can be displayed on the RfQ/Quotation Page. Typically an RfQ BO is used.
	 */
	RFQ("rfq"),
	/**
	 * If the document type is not yes clear, e.g: Bean without BO assigned
	 */
	UNKNOWN("unknown");

	private final String authorityCheckName;


	private SalesDocumentType(final String authorityCheckName)
	{
		this.authorityCheckName = authorityCheckName;
	}

	/**
	 * Returns the name to be used for authority checks.<br>
	 * 
	 * @return name for authority checks
	 */
	public String getAuthorityCheckName()
	{
		return authorityCheckName;
	}
}
