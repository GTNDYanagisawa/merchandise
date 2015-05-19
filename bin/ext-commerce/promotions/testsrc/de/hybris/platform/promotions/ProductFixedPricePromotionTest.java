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
 * Test for ProductFixedPricePromotion.
 */
public class ProductFixedPricePromotionTest extends AbstractPromotionTest
{

	private ProductModel product1;

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
		user = userService.getUserForUID("demo");
		userService.setCurrentUser(user);
		currency = commonI18NService.getCurrency("EUR");
		commonI18NService.setCurrentCurrency(currency);
	}

	/**
	 * Quite straightforward test. Adds one product to cart, which costs 253 Euro, and update promotion which fires the
	 * ProductFixedPricePromotion, and afterwards the customer only needs to pay 200 Euro.
	 */
	@Test
	public void testProductFixedPricePromotion() throws CalculationException
	{
		cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product1, 1, product1.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);
		assertEquals("cart total before updatePromotions(ProductFixedPricePromotion)", 253.0d, cart.getTotalPrice().doubleValue(),
				0.01);

		promotionGroup = promotionsService.getPromotionGroup("prGroup3");
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionGroup);
		promotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		modelService.refresh(cart);
		assertEquals("cart total after updatePromotions(ProductFixedPricePromotion)", 200.0d, cart.getTotalPrice().doubleValue(),
				0.01);
	}

}
