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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.voucher.VoucherService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test the PromotionVoucher.
 */
public class PromotionVoucherTest extends ServicelayerTransactionalTest
{

	private ProductModel testProduct1;
	private ProductModel testProduct2;
	private ProductModel testProduct3;

	private final String promotionvoucherCode = "vo2";

	private UserModel user;

	@Resource
	private VoucherService voucherService;
	@Resource
	private CatalogService catalogService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;
	@Resource
	private I18NService i18nService;
	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private DeliveryModeService deliveryModeService;

	/**
	 * Creates necessary data.
	 */
	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();

		importCsv("/test/promotionVoucher.csv", "windows-1252");

		user = userService.getUserForUID("ariel");
		userService.setCurrentUser(user);
		i18nService.setCurrentCurrency(i18nService.getCurrency("EUR"));

		final CatalogVersionModel version = catalogService.getCatalogVersion("hwcatalog", "Online");
		catalogService.addSessionCatalogVersion("hwcatalog", "Online");
		testProduct1 = productService.getProductForCode(version, "HW2110-0012");
		testProduct2 = productService.getProductForCode(version, "HW2110-0027");
		testProduct3 = productService.getProductForCode(version, "HW2110-0029");
	}

	/**
	 * Creates 3 carts, and tries to redeem the same voucher
	 */
	@Test
	public void testPromotionVoucherRestriction() throws JaloPriceFactoryException, InvalidCartException, InvalidCartException
	{
		//the first cart, redemption must be successful
		placeVoucherOrder(testProduct1, true);

		//the second cart, redemption must be successful too
		placeVoucherOrder(testProduct2, true);

		//the third cart, redemption CANNOT be successful
		placeVoucherOrder(testProduct3, false);
	}

	private void placeVoucherOrder(final ProductModel product, final boolean successful) throws InvalidCartException,
			JaloPriceFactoryException
	{
		final CartModel cart = cartService.getSessionCart();
		assertEquals(0d, cart.getTotalPrice().doubleValue(), 0.001);
		cartService.addToCart(cart, product, 1, product.getUnit());
		cart.setDeliveryAddress(user.getDefaultShipmentAddress());
		final DeliveryModeModel deliveryModeDhl = deliveryModeService.getDeliveryModeForCode("dhl");
		cart.setDeliveryMode(deliveryModeDhl);
		final boolean redeemed = voucherService.redeemVoucher(promotionvoucherCode, cart);

		if (successful)
		{
			assertTrue(redeemed);
			final OrderModel order = orderService.placeOrder(cart, cart.getDeliveryAddress(), cart.getPaymentAddress(),
					cart.getPaymentInfo());
			voucherService.createVoucherInvalidation(promotionvoucherCode, order);
		}
		else
		{
			assertFalse(redeemed);
		}
	}

}
