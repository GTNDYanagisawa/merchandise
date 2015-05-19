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
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel;
import de.hybris.platform.promotions.model.OrderThresholdFreeGiftPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.model.PromotionPriceRowModel;
import de.hybris.platform.promotions.model.PromotionProductRestrictionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests whether order promotion should be applied with product restriction under different circumstances.
 */
public class OrderPromotionWithProductRestrictionTest extends AbstractPromotionServiceTest
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
	private CommonI18NService commonI18NService;
	@Resource
	private ProductService productService;
	@Resource
	private PromotionsService promotionsService;

	private CatalogVersionModel catVersion;
	private CurrencyModel euroCurrency;
	private CartModel cart;
	private UserModel user;
	private PromotionGroupModel promotionGroup;
	private List<PromotionGroupModel> groups;
	private OrderThresholdFreeGiftPromotionModel orderThresholdFreeGiftPromotion;
	private ProductModel product1;//also restriction product
	private ProductModel product2;
	private ProductModel freeGiftProduct;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		prepareData();
	}

	private void prepareData()
	{
		catVersion = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(catVersion);

		user = userService.getUserForUID("demo");
		userService.setCurrentUser(user);
		cart = cartService.getSessionCart();
		euroCurrency = commonI18NService.getCurrency("EUR");
		commonI18NService.setCurrentCurrency(euroCurrency);

		product1 = productService.getProductForCode(catVersion, "HW1210-3411");
		product2 = productService.getProductForCode(catVersion, "HW1230-0001");
		freeGiftProduct = productService.getProductForCode(catVersion, "HW2200-0561");
	}

	/**
	 * prGroup4 contains no promotions before the orderThresholdFreeGiftPromotion is added.
	 */
	private List<PromotionGroupModel> preparePromotionGroups(final boolean withRestriction)
	{
		promotionGroup = promotionsService.getPromotionGroup("prGroup4");
		final List<PromotionGroupModel> groups = new ArrayList<PromotionGroupModel>();
		prepareOrderThresholdFreeGiftPromotion(withRestriction);
		final List<AbstractPromotionModel> promotions = new ArrayList<AbstractPromotionModel>();
		promotions.add(orderThresholdFreeGiftPromotion);
		promotionGroup.setPromotions(promotions);
		groups.add(promotionGroup);
		return groups;
	}

	/**
	 * This promotion has to be created here because it needs to be used with product restrictions.
	 */
	private void prepareOrderThresholdFreeGiftPromotion(final boolean withRestriction)
	{
		orderThresholdFreeGiftPromotion = modelService.create(OrderThresholdFreeGiftPromotionModel.class);
		orderThresholdFreeGiftPromotion.setPromotionGroup(promotionGroup);
		orderThresholdFreeGiftPromotion.setCode("orderThresholdFreeGiftPromotion");
		orderThresholdFreeGiftPromotion.setTitle("orderThresholdFreeGiftPromotion title");
		orderThresholdFreeGiftPromotion.setEnabled(Boolean.TRUE);
		orderThresholdFreeGiftPromotion.setGiftProduct(freeGiftProduct);
		orderThresholdFreeGiftPromotion.setPriority(Integer.valueOf(2000));
		final PromotionPriceRowModel priceRow = preparePriceRow();
		orderThresholdFreeGiftPromotion.setThresholdTotals(Collections.singleton(priceRow));
		if (withRestriction)
		{
			addProductRestriction();
		}
		modelService.save(orderThresholdFreeGiftPromotion);
	}

	/**
	 * Creates threshold of 100 Euro.
	 */
	private PromotionPriceRowModel preparePriceRow()
	{
		final PromotionPriceRowModel priceRow = modelService.create(PromotionPriceRowModel.class);
		priceRow.setCurrency(euroCurrency);
		priceRow.setPrice(Double.valueOf(100.0));
		modelService.save(priceRow);
		return priceRow;
	}

	/**
	 * Sets product1 as the restricted product.
	 */
	private void addProductRestriction()
	{
		final PromotionProductRestrictionModel productRestriction = modelService.create(PromotionProductRestrictionModel.class);
		productRestriction.setProducts(Collections.singletonList(product1));
		modelService.save(productRestriction);
		final List<AbstractPromotionRestrictionModel> restrictions = new ArrayList<AbstractPromotionRestrictionModel>();
		restrictions.add(productRestriction);
		orderThresholdFreeGiftPromotion.setRestrictions(restrictions);
	}

	/**
	 * Creates an orderThresholdFreeGiftPromotion without product restriction, and the cart contains only product1 which
	 * is restricted. The free gift product should still be added to the cart.
	 */
	@Test
	public void testOrderPromotionWithoutRestriction() throws CalculationException
	{
		groups = preparePromotionGroups(false);
		testOrderPromotion(false, 2);
	}

	/**
	 * Creates an orderThresholdFreeGiftPromotion WITH product restriction, and the cart contains only product1 which is
	 * restricted. This time, the free gift product should NOT be added to the cart.
	 */
	@Test
	public void testOrderPromotionWithRestriction() throws CalculationException
	{
		groups = preparePromotionGroups(true);
		testOrderPromotion(false, 1);
	}

	/**
	 * Creates an orderThresholdFreeGiftPromotion without product restriction, and the cart contains proudct2 which is
	 * not restricted. The free gift product should be added to the cart.
	 */
	@Test
	public void testMoreProductsOrderPromotionWithoutRestriction() throws CalculationException
	{
		groups = preparePromotionGroups(false);
		testOrderPromotion(true, 3);
	}

	/**
	 * Creates an orderThresholdFreeGiftPromotion WITH product restriction, and the cart contains proudct2 which is not
	 * restricted, so the free gift product should be added to the cart.
	 */
	@Test
	public void testMoreProductsOrderPromotionWithRestriction() throws CalculationException
	{
		groups = preparePromotionGroups(true);
		testOrderPromotion(true, 3);
	}

	private void testOrderPromotion(final boolean moreProducts, final int cartSize) throws CalculationException
	{
		cartService.addNewEntry(cart, product1, 1, product1.getUnit());
		if (moreProducts)
		{
			cartService.addNewEntry(cart, product2, 1, product2.getUnit());
		}
		modelService.save(cart);
		promotionsService.updatePromotions(groups, cart, true, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new java.util.Date());
		modelService.refresh(cart);
		assertEquals("cart size", cartSize, cart.getEntries().size());
	}

}
