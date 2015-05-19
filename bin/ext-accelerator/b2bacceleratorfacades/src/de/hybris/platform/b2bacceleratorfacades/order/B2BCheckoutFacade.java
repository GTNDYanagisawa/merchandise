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
package de.hybris.platform.b2bacceleratorfacades.order;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BDaysOfWeekData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

import java.util.List;


/**
 * A B2B specific facade for check out process.
 */
public interface B2BCheckoutFacade extends CheckoutFacade
{
	/**
	 * Gets all visible cost centers for the currently logged-in {@link de.hybris.platform.b2b.model.B2BCustomerModel}
	 * based on his parent B2Unit
	 * 
	 * @return A collection of {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData}
	 */
	List<? extends B2BCostCenterData> getVisibleCostCenters();

	/**
	 * Gets all visible active cost centers for the currently logged-in
	 * {@link de.hybris.platform.b2b.model.B2BCustomerModel} based on his parent B2Unit
	 * 
	 * @return A collection of {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData}
	 */
	List<B2BCostCenterData> getActiveVisibleCostCenters();

	/**
	 * Sets the cost center on all the entries of the order
	 * 
	 * @param costCenterCode
	 *           A unique identifier of a cost center, If null all entries of the order will be set with a null cost
	 *           center
	 * @param orderCode
	 *           A unique identifier of an Order or Cart.
	 * @return Order data
	 */
	<T extends AbstractOrderData> T setCostCenterForCart(String costCenterCode, String orderCode);

	/**
	 * Gets the list of possible PaymentTypes for user selection in checkout summary
	 * 
	 * @return B2BPaymentTypeEnum
	 */

	List<B2BPaymentTypeData> getPaymentTypesForCheckoutSummary();

	/**
	 * Update the cartModel with PaymentTypeSelected by the user
	 * 
	 * @param paymentType
	 * @return boolean
	 */
	boolean setPaymentTypeSelectedForCheckout(String paymentType);

	/**
	 * Sets the purchase order number to the cartModel
	 * 
	 * @param purchaseOrderNumber
	 *           The PO number of the order.
	 * @return boolean
	 */
	boolean setPurchaseOrderNumber(String purchaseOrderNumber);

	/**
	 * Adds a quote request text to the {@link de.hybris.platform.core.model.order.CartModel}
	 * 
	 * @param quoteRequestDescription
	 *           The text describing reasons for requesting a quote on this order.
	 * @return True if the data got added to the cart successfully else false.
	 */
	boolean setQuoteRequestDescription(String quoteRequestDescription);

	/**
	 * Call the Enum service to fetch the list of days in a week using DayOfWeek enum
	 * 
	 * @return List of days in a week
	 */
	List<B2BDaysOfWeekData> getDaysOfWeekForReplenishmentCheckoutSummary();

	/**
	 * Places the cart that's in the session as a scheduled order scheduled by the Trigger parameter
	 * 
	 * @param trigger
	 * @return ScheduledCartData created
	 */
	ScheduledCartData scheduleOrder(TriggerData trigger);

	/**
	 * Creates {@link de.hybris.platform.core.model.order.CartModel} based on an order removes the current session carts
	 * and sets the new cart into the session.
	 * 
	 * @param orderCode
	 *           The unique identifier for an order
	 */
	void createCartFromOrder(final String orderCode);

	/**
	 * Checks the cart for stock availability Note: will remove entries which are no longer available or are completely
	 * out of stock, for partial stack availability quantity of the entry will be updated.
	 * 
	 * @return A list of cart modifications
	 */
	List<? extends CommerceCartModification> validateSessionCart() throws CommerceCartModificationException;

	/**
	 * Sets the default payment type as Account for B2B accelerator store. Gets the
	 * {@link de.hybris.platform.core.model.order.CartModel} object and sets the payment type to ACCOUNT
	 */
	void setDefaultPaymentTypeForCheckout();
}
