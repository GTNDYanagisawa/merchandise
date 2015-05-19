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
package de.hybris.platform.commerceservices.promotion;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.model.promotions.PromotionOrderRestrictionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.model.PromotionUserRestrictionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test the PromotionOrderRestriction.
 */
@IntegrationTest
public class PromotionOrderRestrictionTest extends ServicelayerTransactionalTest
{
	private static String PROMOTION_GROUP1 = "prGroup1";
	private static String PROMOTION_GROUP2 = "prGroup2";
	private static String PROMOTION_GROUP3 = "prGroup3";
	private static double BASE_PRICE = 253.0d;
	private static double PRICE_FOR_PRODUCT_PROMOTION = 200.0d;
	private static double PRICE_FOR_ORDER_PROMOTION = 243.0d;
	private static String PRODUCT_PROMOTION_WITH_SINGLE_ORDER_RESTRICTION = "productFixedPricePromotion1";
	private static String PRODUCT_PROMOTION_WITH_TWO_RESTRICTIONS = "productFixedPricePromotion2";
	private static String ORDER_PROMOTION_WITH_SINGLE_ORDER_RESTRICTION = "OrderThresholdDiscountPromotion1";

	private ProductModel product;
	private UserModel userDemo;
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
	@Resource
	private CommercePromotionService commercePromotionService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
		importCsv("/commerceservices/test/promotionOrderRestrictionTestData.csv", "windows-1252");

		prepareData();
	}

	private void prepareData()
	{
		final CatalogVersionModel version = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(version);
		product = productService.getProductForCode(version, "HW1210-3411");
		userDemo = userService.getUserForUID("demo");
		currency = commonI18NService.getCurrency("EUR");
		commonI18NService.setCurrentCurrency(currency);
	}

	private void createCart() throws CalculationException
	{
		userService.setCurrentUser(userDemo);
		cartService.removeSessionCart();
		cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product, 1, product.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);
	}

	private void addOrderToRestriction(final String promotionName)
	{
		final AbstractPromotionModel promotion = commercePromotionService.getPromotion(promotionName);
		if (promotion.getRestrictions().size() > 0)
		{
			for (final AbstractPromotionRestrictionModel restriction : promotion.getRestrictions())
			{
				if (restriction instanceof PromotionOrderRestrictionModel)
				{
					final Collection<AbstractOrderModel> orders = new HashSet<AbstractOrderModel>();
					orders.add(cart);
					((PromotionOrderRestrictionModel) restriction).setOrders(orders);
					modelService.save(restriction);
					break;
				}
			}
		}
	}

	private void addUserToRestriction(final String promotionName)
	{
		final AbstractPromotionModel promotion = commercePromotionService.getPromotion(promotionName);
		if (promotion.getRestrictions().size() > 0)
		{
			for (final AbstractPromotionRestrictionModel restriction : promotion.getRestrictions())
			{
				if (restriction instanceof PromotionUserRestrictionModel)
				{
					final Collection<PrincipalModel> users = new HashSet<PrincipalModel>();
					users.add(userDemo);
					((PromotionUserRestrictionModel) restriction).setUsers(users);
					modelService.save(restriction);
				}
			}
		}
	}

	private void removeOrderFromRestriction(final String promotionName)
	{
		final AbstractPromotionModel promotion = commercePromotionService.getPromotion(promotionName);
		if (promotion.getRestrictions().size() > 0)
		{
			for (final AbstractPromotionRestrictionModel restriction : promotion.getRestrictions())
			{
				if (restriction instanceof PromotionOrderRestrictionModel)
				{
					final Collection<AbstractOrderModel> orders = new HashSet<AbstractOrderModel>();
					((PromotionOrderRestrictionModel) restriction).setOrders(orders);
					modelService.save(restriction);
				}
			}
		}
	}

	private void updatePromotions(final String promotionGroupCode)
	{
		promotionGroup = promotionsService.getPromotionGroup(promotionGroupCode);
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionGroup);
		promotionsService.updatePromotions(promotionGroups, cart, true, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		modelService.refresh(cart);
	}

	@Test
	public void testSingleOrderRestrictionForProductPromotion() throws CalculationException
	{
		createCart();
		assertEquals("Cart total without promotions ", BASE_PRICE, cart.getTotalPrice().doubleValue(), 0.01);

		addOrderToRestriction(PRODUCT_PROMOTION_WITH_SINGLE_ORDER_RESTRICTION);
		updatePromotions(PROMOTION_GROUP1);
		assertEquals("Cart total after adding order to restriction ", PRICE_FOR_PRODUCT_PROMOTION, cart.getTotalPrice()
				.doubleValue(), 0.01);

		removeOrderFromRestriction(PRODUCT_PROMOTION_WITH_SINGLE_ORDER_RESTRICTION);
		updatePromotions(PROMOTION_GROUP1);
		assertEquals("Cart total after removing order from restriction ", BASE_PRICE, cart.getTotalPrice().doubleValue(), 0.01);

	}

	@Test
	public void testSingleOrderRestrictionForOrderPromotion() throws CalculationException
	{
		createCart();
		assertEquals("Cart total without promotions ", BASE_PRICE, cart.getTotalPrice().doubleValue(), 0.01);

		addOrderToRestriction(ORDER_PROMOTION_WITH_SINGLE_ORDER_RESTRICTION);
		updatePromotions(PROMOTION_GROUP2);
		assertEquals("Cart total after adding order to restriction ", PRICE_FOR_ORDER_PROMOTION,
				cart.getTotalPrice().doubleValue(), 0.01);

		removeOrderFromRestriction(ORDER_PROMOTION_WITH_SINGLE_ORDER_RESTRICTION);
		updatePromotions(PROMOTION_GROUP2);
		assertEquals("Cart total after removing order from restriction ", BASE_PRICE, cart.getTotalPrice().doubleValue(), 0.01);
	}

	@Test
	public void testMultipleRestrictionsForProductPromotion() throws CalculationException
	{
		createCart();
		assertEquals("Cart total without promotions ", BASE_PRICE, cart.getTotalPrice().doubleValue(), 0.01);

		addOrderToRestriction(PRODUCT_PROMOTION_WITH_TWO_RESTRICTIONS);
		updatePromotions(PROMOTION_GROUP3);
		assertEquals("Cart total after adding order to restriction ", BASE_PRICE, cart.getTotalPrice().doubleValue(), 0.01);

		addUserToRestriction(PRODUCT_PROMOTION_WITH_TWO_RESTRICTIONS);
		updatePromotions(PROMOTION_GROUP3);
		assertEquals("Cart total after adding user to restriction ", PRICE_FOR_PRODUCT_PROMOTION, cart.getTotalPrice()
				.doubleValue(), 0.01);

		removeOrderFromRestriction(PRODUCT_PROMOTION_WITH_TWO_RESTRICTIONS);
		updatePromotions(PROMOTION_GROUP3);
		assertEquals("Cart total after removing order from restriction ", BASE_PRICE, cart.getTotalPrice().doubleValue(), 0.01);
	}

}
