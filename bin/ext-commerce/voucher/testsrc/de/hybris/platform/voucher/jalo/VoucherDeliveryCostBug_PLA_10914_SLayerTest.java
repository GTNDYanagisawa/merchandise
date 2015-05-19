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

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.util.DiscountValue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * Tests proper order calculation using {@link CalculationService} for free shipping voucher and delivery cost.
 */
public class VoucherDeliveryCostBug_PLA_10914_SLayerTest extends AbstractVoucherTest
{

	@Resource
	private CalculationService calculationService;

	@Resource
	private CartService cartService;

	@Test
	public void testServiceLayerBehaviour() throws CalculationException
	{
		final CartModel cart = cartService.getSessionCart();
		cart.setDeliveryMode(deliveryMode);
		cart.setDiscounts(Arrays.asList((DiscountModel) promotionVoucher));
		cartService.addNewEntry(cart, product, 1, unit); // only in 4.4 !!!
		modelService.saveAll(cart);
		calculationService.recalculate(cart);

		assertEquals(cart.getDeliveryCost().doubleValue(), deliveryCost, 0.0000001);
		List<DiscountValue> globalDiscountValues = cart.getGlobalDiscountValues();
		assertEquals(1, globalDiscountValues.size());
		DiscountValue discountValue = globalDiscountValues.get(0);
		final double expected = discountAmount + deliveryCost;
		assertEquals(expected, discountValue.getAppliedValue(), 0.000001);
		//check total : 15 + 2.5 - (10 off + free shipping) = 5
		assertEquals(5, cart.getTotalPrice().doubleValue(), 0.000001);

		cart.setDeliveryMode(null);
		modelService.save(cart);
		calculationService.recalculate(cart);

		assertEquals(0.0, cart.getDeliveryCost().doubleValue(), 0.0000001);
		globalDiscountValues = cart.getGlobalDiscountValues();
		assertEquals(1, globalDiscountValues.size());
		discountValue = globalDiscountValues.get(0);
		final double expectedAfter = discountAmount;
		assertEquals(expectedAfter, discountValue.getAppliedValue(), 0.000001); // <---- this should show the error
	}

}
