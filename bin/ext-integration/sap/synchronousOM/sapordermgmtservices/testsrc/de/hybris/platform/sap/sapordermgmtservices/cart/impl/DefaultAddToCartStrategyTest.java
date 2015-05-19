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
package de.hybris.platform.sap.sapordermgmtservices.cart.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class DefaultAddToCartStrategyTest
{
	DefaultAddToCartStrategy classUnderTest = new DefaultAddToCartStrategy();


	@Test
	public void testAllowedCartAdjustmentForProduct()
	{
		final CartModel cartModel = null;
		final ProductModel productModel = null;
		final long quantityToAdd = 10;
		final PointOfServiceModel pointOfServiceModel = null;
		assertEquals(quantityToAdd,
				classUnderTest.getAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd, pointOfServiceModel));
	}
}
