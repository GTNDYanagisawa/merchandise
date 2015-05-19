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
 * Test for ProductBOGOFPromotion.
 */
public class ProductBOGOFPromotionTest extends AbstractPromotionTest
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
	 * these products to cart, and check the price of the cart before and after the updatePromoitions(). Since the
	 * ProductBOGOFPromotion is enabled, the HW2310-1001(29.90 Euro) with the lowest price is sold for free.
	 */
	@Test
	public void testProductBOGOFPromotion() throws CalculationException
	{
		cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product1, 1, product1.getUnit());
		cartService.addNewEntry(cart, product2, 1, product2.getUnit());
		cartService.addNewEntry(cart, product3, 1, product3.getUnit());
		cartService.addNewEntry(cart, product4, 1, product4.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);
		assertEquals("cart total before updatePromotions", 906.69, cart.getTotalPrice().doubleValue(), 0.01);

		promotionGroup = promotionsService.getPromotionGroup("prGroup1");
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionGroup);
		promotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		modelService.refresh(cart);
		assertEquals("cart total after updatePromotions", 876.79, cart.getTotalPrice().doubleValue(), 0.01);
	}

}
