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
package de.hybris.platform.integration.cis.payment.strategies.impl;

import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;


public class CisGenerateMerchantTransactionCodeStrategy implements GenerateMerchantTransactionCodeStrategy
{
	@Override
	public String generateCode(final CartModel cartModel)
	{
		// AbstractOrder.guid is the same in Cart and Order while abstractOrder.code changes when the cart is cloned into the order.
		return cartModel.getGuid();
	}
}
