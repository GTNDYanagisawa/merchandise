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
package de.hybris.platform.promotions;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.voucher.VoucherService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class PromotionWithVoucherTest extends AbstractPromotionServiceTest
{

	@Resource
	private ModelService modelService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	private CalculationService calculationService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ProductService productService;
	@Resource
	private DeliveryModeService deliveryModeService;
	@Resource
	private PromotionsService promotionsService;
	@Resource
	private VoucherService voucherService;

	private CatalogVersionModel catVersion;
	private CartModel cart;
	private UserModel user;
	private PromotionGroupModel promotionGroup;
	private ProductModel product1;

	//5% without free shipping
	private static final String voucherCode = "vo1";
	//20% with free shipping
	private static final String freeShippingVoucherCode = "vo2";
	private DeliveryModeModel deliveryModeDhl;
	//10% with product restriction "HW1230-0001"
	private static final String restrictionProductVoucher = "B01";

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importCsv("/test/promotionWithVoucher.csv", "windows-1252");

		prepareData();
	}

	private void prepareData()
	{
		catVersion = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(catVersion);

		user = userService.getUserForUID("ariel");
		userService.setCurrentUser(user);
		cart = cartService.getSessionCart();
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));
		product1 = productService.getProductForCode(catVersion, "HW2110-0012");
	}

	/**
	 * Puts two products in the cart, applies one 10% product promotion, and redeems
	 * <ul>
	 * <li>1. one voucher without free shipping or</li>
	 * <li>2. one voucher with free shipping or</li>
	 * <li>3. two vouchers, one with free shipping and the other one without.</li>
	 * </ul>
	 */
	@Test
	public void testPromotionWithVoucher() throws JaloPriceFactoryException
	{
		//PRO-64
		promotionGroup = promotionsService.getPromotionGroup("prGroup10");
		final List<PromotionGroupModel> groups = new ArrayList<PromotionGroupModel>();
		groups.add(promotionGroup);

		cartService.addNewEntry(cart, product1, 2, product1.getUnit());
		deliveryModeDhl = deliveryModeService.getDeliveryModeForCode("dhl");
		cart.setDeliveryMode(deliveryModeDhl);
		cart.setDeliveryAddress(user.getDefaultShipmentAddress());

		/**
		 * General information:
		 * <ul>
		 * <li>product(HW2110-0012) price = 81.08 --> 2x = 162.16, delivery cost 8.0, totally 170.16</li>
		 * <li>applied 10% ProductPercentageDiscountPromotion, 162.16 * 0.9 = 145.94</li>
		 * </ul>
		 * Three different situations with vouchers:
		 * <ul>
		 * <li>1. 5% voucher without free shipping, 145.94 * 0.95 = 138.64, delivery cost 8.0, totally 146.64</li>
		 * <li>2. 20% voucher with free shipping, 145.94 * 0.8 = 116.75, free shipping, totally 116.75</li>
		 * <li>3. both the vouchers above, 145.94 * (1 - 0.05 - 0.20) = 109.45, free shipping, totally 109.45</li>
		 * </ul>
		 */

		applyVouchersAndPromotions(new String[]
		{ voucherCode }, groups, 146.64);

		applyVouchersAndPromotions(new String[]
		{ freeShippingVoucherCode }, groups, 116.75);

		applyVouchersAndPromotions(new String[]
		{ voucherCode, freeShippingVoucherCode }, groups, 109.45);
	}

	private void applyVouchersAndPromotions(final String[] voucherCodes, final List<PromotionGroupModel> groups,
			final double expectedTotal) throws JaloPriceFactoryException
	{
		for (final String voucherCode : voucherCodes)
		{
			voucherService.redeemVoucher(voucherCode, cart);
		}
		modelService.save(cart);

		promotionsService.updatePromotions(groups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new java.util.Date());
		modelService.refresh(cart);

		assertEquals(expectedTotal, cart.getTotalPrice().doubleValue(), 0.001);

		for (final String voucherCode : voucherCodes)
		{
			voucherService.releaseVoucher(voucherCode, cart);
		}
	}

	/**
	 * Tests promotion with voucher that has product restriction. (currency is Euro)
	 * <ul>
	 * <li>product(HW1230-0001) price = 769.00 --> 2x = 1538.00</li>
	 * <li>product(HW2110-0012) price = 81.08 --> 2x = 162.16</li>
	 * <li>so the cart contains products that cost 1700.16 Euro without promotion/voucher.</li>
	 * <li>redeems the voucher that is 10% discount with product restriction: only valid for [HW1230-0001]</li>
	 * <li>applies the fixed price promotion that is only valid for [HW1230-0001], and the fixed price is 500</li>
	 * <li>update promotions, and the total discount should be (500 * 2) * 10% = 100</li>
	 * <li>total price of the cart: (500 * 2 + 81.08 * 2) - 100 = 1062.16</li>
	 * </ul>
	 */
	@Test
	public void testPromotionWithProductRestrictionVoucher() throws CalculationException, JaloPriceFactoryException
	{
		//PRO-70
		promotionGroup = promotionsService.getPromotionGroup("prGroup20");
		final ProductModel productSony = productService.getProductForCode(catVersion, "HW1230-0001");

		cartService.addNewEntry(cart, productSony, 2, productSony.getUnit());
		cartService.addNewEntry(cart, product1, 2, product1.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);
		modelService.refresh(cart);
		assertEquals("should be 1700.16 euro without promotion/voucher", 1700.16, cart.getTotalPrice().doubleValue(), 0.01);

		voucherService.redeemVoucher(restrictionProductVoucher, cart);
		modelService.save(cart);

		final List<PromotionGroupModel> groups = new ArrayList<PromotionGroupModel>();
		groups.add(promotionGroup);
		promotionsService.updatePromotions(groups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new java.util.Date());
		modelService.refresh(cart);
		assertEquals("should be 100 euro for fixed price discount", 100.0, cart.getTotalDiscounts().doubleValue(), 0.01);
		assertEquals("should be 1062.16 euro for total", 1062.16, cart.getTotalPrice().doubleValue(), 0.01);
	}

}
