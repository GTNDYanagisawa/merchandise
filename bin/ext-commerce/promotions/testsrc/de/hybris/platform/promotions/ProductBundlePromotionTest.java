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
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test for ProductBundlePromotion.
 */
public class ProductBundlePromotionTest extends AbstractPromotionTest
{

	private ProductModel product1;
	private ProductModel product2;
	private ProductModel product3;
	private ProductModel product4;

	private UserModel user;
	private CurrencyModel currency;
	private CartModel cart;
	private PromotionGroupModel promotionGroup;

	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	private CalculationService calculationService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;
	@Resource
	private PromotionsService promotionsService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		final CatalogVersionModel version = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(version);
		product1 = productService.getProductForCode(version, "HW1210-3411");
		product2 = productService.getProductForCode(version, "HW1100-0023");
		product3 = productService.getProductForCode(version, "HW2310-1001");
		product4 = productService.getProductForCode(version, "HW2320-1009");
		user = userService.getUserForUID("demo");
		userService.setCurrentUser(user);
		currency = commonI18NService.getCurrency("EUR");
		commonI18NService.setCurrentCurrency(currency);
	}

	/**
	 * HW1210-3411: 253 Euro, HW1100-0023: 523.99 Euro, HW2310-1001: 29.90 Euro, and HW2320-1009: 99.80 Euro. Adds all
	 * these products to cart, and check the price of the cart before and after the updatePromoitions(). The bundle
	 * promotion defines the combination of three products: [HW1210-3411, HW1100-0023, and HW2310-1001], and the price is
	 * 700 Euro.
	 * <ul>
	 * <li>tests the total price, 2 * 253 + 2 * 523.99 + 3 * 29.90 + 2 * 99.80 = 1843.28,</li>
	 * <li>update promotion, and since only HW2310-1001 has the quantity of 3, the last one will not be considered in the
	 * promotion,</li>
	 * <li>tests the total price, should be: 2 * 700 + 1 * 29.90 + 2 * 99.80 = 1629.5.</li>
	 * </ul>
	 */
	@Test
	public void testProductBundlePromotion() throws CalculationException
	{
		cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product1, 2, product1.getUnit());
		cartService.addNewEntry(cart, product2, 2, product2.getUnit());
		cartService.addNewEntry(cart, product3, 3, product3.getUnit());
		cartService.addNewEntry(cart, product4, 2, product4.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);
		assertEquals("cart total before updatePromotions(ProductBundlePromotion)", 1843.28d, cart.getTotalPrice().doubleValue(),
				0.01);

		promotionGroup = promotionsService.getPromotionGroup("prGroup2");
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionGroup);
		promotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		modelService.refresh(cart);
		assertEquals("cart total after updatePromotions(ProductBundlePromotion)", 1629.5d, cart.getTotalPrice().doubleValue(), 0.01);
	}
}
