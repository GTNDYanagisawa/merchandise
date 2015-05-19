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
package de.hybris.platform.promotions.jalo;

import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.result.PromotionOrderResults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * PromotionsTest. Test uses products from hwcatalog version online.
 */
public class PromotionGroupTest extends AbstractPromotionTest
{

	private Cart cart;
	private Product testProduct1;
	List<PromotionGroup> promotionGroups;
	PromotionsManager proManager;

	@Before
	public void setUp() throws Exception
	{

		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
		createPromotionData();

		final CatalogVersion version = CatalogManager.getInstance().getCatalog("hwcatalog").getCatalogVersion("Online");
		jaloSession.getSessionContext().setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS, Collections.singletonList(version));

		jaloSession.getSessionContext().setUser(UserManager.getInstance().getAdminEmployee());
		cart = jaloSession.getCart();
		final C2LManager c2lman = C2LManager.getInstance();
		jaloSession.getSessionContext().setCurrency(c2lman.getCurrencyByIsoCode("EUR"));

		testProduct1 = CatalogManager.getInstance().getProductByCatalogVersion(version, "HW1210-3411");

		proManager = PromotionsManager.getInstance();
		promotionGroups = new ArrayList<PromotionGroup>();
	}


	@Test
	public void testFirstPromotionGroup() throws JaloPriceFactoryException
	{
		promotionGroups.add(proManager.getPromotionGroup("prGroup1"));
		cart.addNewEntry(testProduct1, 1, testProduct1.getUnit());

		try
		{
			cart.recalculate();
		}
		catch (final JaloPriceFactoryException e)
		{
			e.printStackTrace();
		}
		final double totalPrice = cart.getTotal();

		proManager.updatePromotions(jaloSession.getSessionContext(), promotionGroups, cart, false, AutoApplyMode.APPLY_ALL,
				AutoApplyMode.APPLY_ALL, new java.util.Date());

		assertTrue("price schould change it is " + totalPrice, (totalPrice != cart.getTotal()));
		assertTrue("price schould be 10.0 EUR it is " + cart.getTotal(), (10.0 == cart.getTotal()));
	}

	@Test
	public void testSecondPromotionGroup() throws JaloPriceFactoryException
	{
		promotionGroups.add(proManager.getPromotionGroup("prGroup2"));
		cart.addNewEntry(testProduct1, 1, testProduct1.getUnit());

		try
		{
			cart.recalculate();
		}
		catch (final JaloPriceFactoryException e)
		{
			e.printStackTrace();
		}
		final double totalPrice = cart.getTotal();

		proManager.updatePromotions(jaloSession.getSessionContext(), promotionGroups, cart, false, AutoApplyMode.APPLY_ALL,
				AutoApplyMode.APPLY_ALL, new java.util.Date());

		assertTrue("price schould change", (totalPrice != cart.getTotal()));
		assertTrue("price schould be 110.0 EUR", (110.0 == cart.getTotal()));
	}

	/**
	 * Tests ProductPercentageDiscountPromotion
	 */
	@Test
	public void testProductPercentageDiscountPromotion()
	{
		promotionGroups.add(proManager.getPromotionGroup("prGroup3"));
		cart.addNewEntry(testProduct1, 2, testProduct1.getUnit());

		try
		{
			cart.recalculate();
		}
		catch (final JaloPriceFactoryException e)
		{
			e.printStackTrace();
		}
		final double totalPrice = cart.getTotal();
		final double entryBasePrice = cart.getEntry(0).getBasePrice().doubleValue();

		proManager.updatePromotions(jaloSession.getSessionContext(), promotionGroups, cart, false, AutoApplyMode.APPLY_ALL,
				AutoApplyMode.APPLY_ALL, new java.util.Date());


		assertTrue("Order total price schould be changed" + totalPrice, (totalPrice != cart.getTotal()));
		Assert.assertEquals("Order total price is invalid", totalPrice * (1 - 0.19), cart.getTotal(), 0.00001);

		final PromotionOrderResults promotionResults = proManager.getPromotionResults(cart);
		for (final PromotionResult promotionResult : promotionResults.getFiredProductPromotions())
		{
			for (final PromotionOrderEntryConsumed c : promotionResult.getConsumedEntries())
			{
				Assert.assertEquals("original base price is invalid: ", Double.valueOf(entryBasePrice),
						Double.valueOf(c.getUnitPrice()));
				Assert.assertEquals("adjusted total price is invalid: ", Double.valueOf(cart.getTotal()),
						Double.valueOf(c.getAdjustedEntryPrice()));
			}
		}
	}


	/**
	 * Tests ProductSteppedMultiBuyPromotion
	 */
	@Test
	public void testProductSteppedMultiBuyPromotion()
	{
		promotionGroups.add(proManager.getPromotionGroup("prGroup3"));
		cart.addNewEntry(testProduct1, 4, testProduct1.getUnit());

		try
		{
			cart.recalculate();
		}
		catch (final JaloPriceFactoryException e)
		{
			e.printStackTrace();
		}
		final double totalPrice = cart.getTotal();
		final double entryBasePrice = cart.getEntry(0).getBasePrice().doubleValue();

		System.out.println(totalPrice);
		proManager.updatePromotions(jaloSession.getSessionContext(), promotionGroups, cart, false, AutoApplyMode.APPLY_ALL,
				AutoApplyMode.APPLY_ALL, new java.util.Date());


		assertTrue("Order total price schould be changed" + totalPrice, (totalPrice != cart.getTotal()));
		Assert.assertEquals("Order total price is invalid", 40.0, cart.getTotal(), 0.00001);

		final PromotionOrderResults promotionResults = proManager.getPromotionResults(cart);
		for (final PromotionResult promotionResult : promotionResults.getFiredProductPromotions())
		{
			for (final PromotionOrderEntryConsumed c : promotionResult.getConsumedEntries())
			{
				//displayPromotionEntryConsumed(c);
				Assert.assertEquals("original base price is invalid: ", Double.valueOf(entryBasePrice),
						Double.valueOf(c.getUnitPrice()));
				Assert.assertEquals("adjusted total price is invalid: ", 40.0, c.getAdjustedEntryPrice(), 0.00001);
			}
		}
	}

	/**
	 * Tests mixed promotion scenario: ProductSteppedMultiBuyPromotion + ProductPercentageDiscountPromotion.
	 * ProductSteppedMultiBuyPromotion has higher priority, ProductPercentageDiscountPromotion has lower priority. There
	 * are 5 items in the cart. First 4 are covered by ProductSteppedMultiBuyPromotion, because the definition of this
	 * promotion contains entries for 3 and 4 products - the latter one is taken. The remaining one product is covered by
	 * ProductPercentageDiscountPromotion. In the effect total price is composed of two parts: first part is the price
	 * for first 4 items, second part is the price for the last item. The first price is determined by the
	 * ProductSteppedMultiBuyPromotion promotion's steps definition (it does not depend on product price!), second price
	 * is determined by the unit price of the item and the promotion's defined percentage discount.
	 */
	@Test
	public void testMixedPromotions()
	{
		promotionGroups.add(proManager.getPromotionGroup("prGroup3"));
		cart.addNewEntry(testProduct1, 5, testProduct1.getUnit());

		try
		{
			cart.recalculate();
		}
		catch (final JaloPriceFactoryException e)
		{
			e.printStackTrace();
		}
		final double totalPrice = cart.getTotal();
		final double entryBasePrice = cart.getEntry(0).getBasePrice().doubleValue();

		System.out.println(totalPrice);
		proManager.updatePromotions(jaloSession.getSessionContext(), promotionGroups, cart, false, AutoApplyMode.APPLY_ALL,
				AutoApplyMode.APPLY_ALL, new java.util.Date());

		assertTrue("Order total price schould be changed" + totalPrice, (totalPrice != cart.getTotal()));
		Assert.assertEquals("Order total price is invalid", 40.0 + 253.0 * (1 - (0.19)), cart.getTotal(), 0.00001);

		final PromotionOrderResults promotionResults = proManager.getPromotionResults(cart);
		for (final PromotionResult promotionResult : promotionResults.getFiredProductPromotions())
		{
			for (final PromotionOrderEntryConsumed c : promotionResult.getConsumedEntries())
			{
				//displayPromotionEntryConsumed(c);
				Assert.assertEquals("original base price is invalid: ", Double.valueOf(entryBasePrice),
						Double.valueOf(c.getUnitPrice()));

				if ("Product_Stepped_Multi_Buy".equals(c.getPromotionResult().getPromotion().getCode()))
				{
					Assert.assertEquals("adjusted total price is invalid: ", 40.0, c.getAdjustedEntryPrice(), 0.00001);
				}
				else
				{
					//Percentage discount
					Assert.assertEquals("adjusted total price is invalid: ", 253.0 * (1 - 0.19), c.getAdjustedEntryPrice(), 0.00001);
				}
			}
		}
	}

	//	private void displayPromotionEntryConsumed(final PromotionOrderEntryConsumed c)
	//	{
	//		System.out.println("quantity: " + c.getQuantity() + " ");
	//		System.out.println("unit: " + c.getUnit().getName() + " ");
	//		System.out.println("code: " + c.getProduct().getCode() + " ");
	//		System.out.println("original unit price: " + c.getUnitPrice() + " ");
	//		System.out.println("original total price: " + c.getEntryPrice() + " ");
	//		System.out.println("adjusted unit price: " + c.getAdjustedUnitPrice() + " ");
	//		System.out.println("adjusted total price: " + c.getAdjustedEntryPrice() + " ");
	//	}

}
