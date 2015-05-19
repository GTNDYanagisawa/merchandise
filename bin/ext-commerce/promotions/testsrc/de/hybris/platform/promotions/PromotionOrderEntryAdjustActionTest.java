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
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class PromotionOrderEntryAdjustActionTest extends AbstractPromotionServiceTest
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
	private CartModel cart;
	private UserModel user;
	private PromotionGroupModel promotionGroup;
	private ProductModel product1;
	private ProductModel product2;

	@Override
	@Before
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
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));
		product1 = productService.getProductForCode(catVersion, "HW1210-3411");
		product2 = productService.getProductForCode(catVersion, "HW1230-0001");
	}

	/**
	 * Tests PromotionOrderEntryAdjustAction with the same product in different cart entries.
	 * <ul>
	 * <li>adds one product1 in entry0, and one product2 in entry1,</li>
	 * <li>adds another product1 in entry2, and product2 in entry3,</li>
	 * <li>product1 has fixed price promotion(10 Euro), and product2 has percent promotion(10%).</li>
	 * <li>tests each entry should have the discount, instead of the whole discount in the first entry.</li>
	 * </ul>
	 */
	@Test
	public void testPromotionOrderEntryAdjustAction() throws CalculationException
	{
		//PRO-75
		final List<PromotionGroupModel> groups = preparePromotionGroups();
		prepareCart();

		promotionsService.updatePromotions(groups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new java.util.Date());
		modelService.refresh(cart);
		testPromotionEntries(cart, preparePriceData());
	}

	private List<PromotionGroupModel> preparePromotionGroups()
	{
		promotionGroup = promotionsService.getPromotionGroup("prGroup1");
		final List<PromotionGroupModel> groups = new ArrayList<PromotionGroupModel>();
		groups.add(promotionGroup);
		return groups;
	}

	private void prepareCart()
	{
		cartService.addNewEntry(cart, product1, 1, product1.getUnit(), -1, true);
		cartService.addNewEntry(cart, product2, 1, product2.getUnit(), -1, true);
		cartService.addNewEntry(cart, product1, 1, product1.getUnit(), -1, false);
		cartService.addNewEntry(cart, product2, 1, product2.getUnit(), -1, false);
		modelService.save(cart);
	}

	private void testPromotionEntries(final AbstractOrderModel order, final Map<Integer, Double> expectedPrices)
	{
		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			modelService.refresh(entry);
			final Double price = expectedPrices.get(entry.getEntryNumber());
			assertEquals("should be the same price", price.doubleValue(), entry.getTotalPrice().doubleValue(), 0.01);
		}
	}

	private Map<Integer, Double> preparePriceData()
	{
		final Map<Integer, Double> prices = new HashMap<Integer, Double>();
		//fixed price, 10 Euro
		prices.put(Integer.valueOf(0), Double.valueOf(10.0));
		//10% percent, 769.0 * 0.9 = 692.1
		prices.put(Integer.valueOf(1), Double.valueOf(692.1));
		prices.put(Integer.valueOf(2), Double.valueOf(10.0));
		prices.put(Integer.valueOf(3), Double.valueOf(692.1));
		return prices;
	}

}
