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
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.OrderThresholdDiscountPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.model.PromotionPriceRowModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class OrderThresholdDiscountPromotionTest extends ServicelayerTransactionalTest
{
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;
	@Resource
	private PromotionsService promotionsService;

	private ProductModel product;
	private PromotionGroupModel promotionGroup;
	private CurrencyModel currency;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();

		final CatalogVersionModel version = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(version);

		userService.setCurrentUser(userService.getUserForUID("demo"));
		currency = commonI18NService.getCurrency("EUR");

		promotionGroup = createPromotionGroup("orderThresholdDiscountPromotionTestGroup");
		//HW2310-1001 for 29.90 EUR
		product = productService.getProductForCode(version, "HW2310-1001");
	}

	private PromotionGroupModel createPromotionGroup(final String name)
	{
		final PromotionGroupModel promotionGroup = new PromotionGroupModel();
		promotionGroup.setIdentifier(name);
		modelService.save(promotionGroup);
		return promotionGroup;
	}

	private OrderThresholdDiscountPromotionModel preparePromotion(final String code, final double orderThreshold,
			final double discount, final PromotionGroupModel promotionGroup)
	{
		final OrderThresholdDiscountPromotionModel promo = new OrderThresholdDiscountPromotionModel();
		promo.setCode(code);
		promo.setEnabled(Boolean.TRUE);
		promo.setPriority(Integer.valueOf(1000));
		promo.setPromotionGroup(promotionGroup);

		promo.setDiscountPrices(createPriceRow(currency, discount));
		promo.setThresholdTotals(createPriceRow(currency, orderThreshold));

		modelService.save(promo);
		return promo;
	}

	private List<PromotionPriceRowModel> createPriceRow(final CurrencyModel currency, final double discount)
	{
		final PromotionPriceRowModel priceRow = new PromotionPriceRowModel();
		priceRow.setCurrency(currency);
		priceRow.setPrice(Double.valueOf(discount));
		modelService.save(priceRow);
		final List<PromotionPriceRowModel> rows = new ArrayList<PromotionPriceRowModel>();
		rows.add(priceRow);
		return rows;
	}

	@Test
	public void testHighDiscountPromotion()
	{
		final CartModel cart = cartService.getSessionCart();
		final List<AbstractPromotionModel> promotions = new ArrayList<AbstractPromotionModel>();
		//1000 EUR for discount
		promotions.add(preparePromotion("discount 1k", 10.00, 1000.00, promotionGroup));
		promotionGroup.setPromotions(promotions);

		cartService.addNewEntry(cart, product, 1, product.getUnit());
		modelService.save(cart);

		promotionsService.updatePromotions(Collections.singletonList(promotionGroup), cart, false, AutoApplyMode.APPLY_ALL,
				AutoApplyMode.APPLY_ALL, new java.util.Date());
		modelService.refresh(cart);

		assertTrue("Price must be positive: " + cart.getTotalPrice(), cart.getTotalPrice().doubleValue() >= 0);
	}

	@Test
	public void testLowDiscountPromotion()
	{
		cartService.removeSessionCart();
		final CartModel cart = cartService.getSessionCart();
		final List<AbstractPromotionModel> promotions = new ArrayList<AbstractPromotionModel>();
		//10 EUR for discount
		promotions.add(preparePromotion("discount 10", 10.00, 10.00, promotionGroup));
		promotionGroup.setPromotions(promotions);

		cartService.addNewEntry(cart, product, 1, product.getUnit());
		modelService.save(cart);

		promotionsService.updatePromotions(Collections.singletonList(promotionGroup), cart, false, AutoApplyMode.APPLY_ALL,
				AutoApplyMode.APPLY_ALL, new java.util.Date());
		modelService.refresh(cart);

		assertEquals("Applied discout promotion: ", 19.90d, cart.getTotalPrice().doubleValue(), 0.001d);
	}

}
