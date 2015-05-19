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
 * Represents the Status object. <br>
 * 
 */
public interface StatusObject
{

	/**
	 * Set the shipping status for this object. <br>
	 * 
	 * @param state
	 *           Shipping Status
	 */
	public void setShippingStatus(ShippingStatus state);

	/**
	 * Set the billing status for this object. <br>
	 * 
	 * @param state
	 *           Billing Status
	 */
	public void setBillingStatus(BillingStatus state);

	/**
	 * Set the overall status for this object. <br>
	 * 
	 * @param state
	 *           Overall Status
	 */
	public void setOverallStatus(OverallStatus state);

}
