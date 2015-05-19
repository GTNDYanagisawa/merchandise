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
package de.hybris.platform.sap.sapordermgmtservices.checkout;

import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;

import java.util.List;


/**
 * Service representation of checkout with SAP synchronous order management
 */
public interface CheckoutService
{
	/**
	 * Submits an order from the current session cart which is held in the SAP back end.
	 * 
	 * @return The order that has been persisted in the SAP back end
	 */
	public abstract OrderData placeOrder();

	/**
	 * Retrieving delivery modes from SAP back end. In SAP terminology, this is mostly referred to as 'Shipping
	 * Condition'
	 * 
	 * @return The list of available delivery modes. These are read from the SAP back end.
	 */
	List<DeliveryModeData> getSupportedDeliveryModes();

	/**
	 * Sets a delivery mode code into the current session cart which is held in SD, and updates the cart afterwards as
	 * prices can change.
	 * 
	 * @param deliveryModeCode
	 *           The new delivery mode code. Named 'Shipping condition' in SAP back end terms.
	 * @return Has this action been performed successfully?
	 */
	boolean setDeliveryMode(String deliveryModeCode);

	/**
	 * Sets the purchase order number into the current session cart which is held in SD, and updates the cart afterwards.
	 * 
	 * @param purchaseOrderNumber
	 *           Purchase order number
	 * @return Has this action been performed successfully?
	 */
	boolean setPurchaseOrderNumber(String purchaseOrderNumber);

	/**
	 * Sets current delivery address. It's not possible to set a document specific address, instead the ID of a valid
	 * ship-to party needs to be passed.
	 * 
	 * @param sapCustomerId
	 *           Technical key of an back end ship-to party, typically with length 10
	 * 
	 * @return Did it succeed?
	 * 
	 */
	public abstract boolean setDeliveryAddress(String sapCustomerId);
}
