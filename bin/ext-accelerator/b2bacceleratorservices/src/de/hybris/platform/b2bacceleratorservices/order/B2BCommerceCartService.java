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
package de.hybris.platform.b2bacceleratorservices.order;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;


/**
 *
 */
public interface B2BCommerceCartService extends CommerceCartService
{
	/**
	 * Forcefully re-calulcate the order total after applying the promotions for payment type
	 * 
	 * @param cartModel
	 *           The cart whose total has to be re-calculated
	 */
	void calculateCartForPaymentTypeChange(CartModel cartModel);

	InvoicePaymentInfoModel createInvoicePaymentInfo(CartModel cartModel);
}
