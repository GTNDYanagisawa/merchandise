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
 * Test for ProductMultiBuyPromotion.
 */
public class ProductMultiBuyPromotionTest extends AbstractPromotionTest
{

	private ProductModel product1;
	private ProductModel product2;
	private ProductModel product3;

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
		user = userService.getUserForUID("demo");
		userService.setCurrentUser(user);
		currency = commonI18NService.getCurrency("EUR");
		commonI18NService.setCurrentCurrency(currency);
	}

	/**
	 * HW1210-3411: 253.0 Euro, HW1100-0023: 523.99 Euro, and HW2310-1001: 29.90 Euro. The ProductMultiBuyPromotion will
	 * be fired if at least 3 pieces of HW1210-3411 and/or HW1100-0023 are in the cart for 900 Euro.
	 * <ul>
	 * <li>adds one piece of all those products in cart, and tests the total price,</li>
	 * <li>adds one more HW1210-3411 and HW1100-0023 in cart, and tests the total price again,</li>
	 * <li>updates with the ProductMultiBuyPromotion, and checks the total price,</li>
	 * <li>in ProductMultiBuyPromotion, the product with lowest price will be first consumed,</li>
	 * <li>so two HW1210-3411 and one HW1100-0023 are valid for the ProductMultiBuyPromotion under this situation,</li>
	 * <li>checks the total price: 900 + 523.99 + 29.90 = 1453.89.</li>
	 * </ul>
	 */
	@Test
	public void testProductMultiBuyPromotion() throws CalculationException
	{
		cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product1, 1, product1.getUnit());
		cartService.addNewEntry(cart, product2, 1, product2.getUnit());
		cartService.addNewEntry(cart, product3, 1, product3.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);
		assertEquals("cart total before updatePromotions(ProductMultiBuyPromotion)", 806.89d, cart.getTotalPrice().doubleValue(),
				0.01);

		cartService.addNewEntry(cart, product1, 1, product1.getUnit());
		cartService.addNewEntry(cart, product2, 1, product2.getUnit());
		modelService.saveAll();
		calculationService.calculate(cart);
		//2 * 253 + 2* 523.99 + 29.90 = 1583.88
		assertEquals("another 2 products in cart", 1583.88d, cart.getTotalPrice().doubleValue(), 0.01);

		promotionGroup = promotionsService.getPromotionGroup("prGroup4");
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionGroup);
		promotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		modelService.refresh(cart);
		assertEquals("cart total after updatePromotions(ProductMultiBuyPromotion)", 1453.89d, cart.getTotalPrice().doubleValue(),
				0.01);
	}

}
