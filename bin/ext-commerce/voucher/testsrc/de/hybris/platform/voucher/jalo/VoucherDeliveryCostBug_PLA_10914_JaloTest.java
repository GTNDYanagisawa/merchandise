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
package de.hybris.platform.voucher.jalo;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.price.Discount;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.util.DiscountValue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;


/**
 * Tests proper jalo order calculation using for free shipping voucher and delivery cost.
 */
public class VoucherDeliveryCostBug_PLA_10914_JaloTest extends AbstractVoucherTest
{


	@Test
	public void testJaloBehaviour() throws JaloPriceFactoryException
	{
		final Cart cart = jaloSession.getCart();

		//Add new cart entry with test product
		cart.addNewEntry((Product) modelService.getSource(product), 1l, (Unit) modelService.getSource(unit));

		//Attach the discount - promotional voucher with free shipping
		cart.setDiscounts(Arrays.asList((Discount) modelService.getSource(promotionVoucher)));

		//Setup a delivery mode that will return 5.55 Eur.
		final TestDeliveryMode dm = modelService.getSource(deliveryMode);
		cart.setDeliveryMode(dm);

		//Calculate cart
		cart.recalculate();

		//check delivery cost 
		assertEquals(cart.getDeliveryCosts(), deliveryCost, 0.0000001);

		//check cart's discounts
		List<DiscountValue> globalDiscountValues = cart.getGlobalDiscountValues();
		assertEquals(1, globalDiscountValues.size());
		DiscountValue discountValue = globalDiscountValues.get(0);

		final double expected = discountAmount + deliveryCost;
		assertEquals(expected, discountValue.getAppliedValue(), 0.000001);
		//check total : 15 + 2.5 - (10 off + free shipping) = 5
		assertEquals(5, cart.getTotal(), 0.000001);

		//now reset deliveryMode to null
		cart.setDeliveryMode(null);
		cart.recalculate();

		assertEquals(0.0, cart.getDeliveryCosts(), 0.0000001);
		globalDiscountValues = cart.getGlobalDiscountValues();
		assertEquals(1, globalDiscountValues.size());
		discountValue = globalDiscountValues.get(0);
		final double expectedAfter = discountAmount;

		assertEquals(expectedAfter, discountValue.getAppliedValue(), 0.000001);

	}



}
