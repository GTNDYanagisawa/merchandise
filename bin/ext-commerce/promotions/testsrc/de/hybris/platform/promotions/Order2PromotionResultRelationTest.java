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
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.jalo.CachingStrategy;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;


/**
 * Tests the relation between {@link AbstractOrderModel} and {@link PromotionResultModel}.
 */
public class Order2PromotionResultRelationTest extends AbstractPromotionServiceTest
{

	private final static String PROMOTIONS_CACHING_STRATEGY_BEAN_ID = "promotionsCachingStrategy";

	private ProductModel product;
	private UserModel user;
	private CurrencyModel currency;
	private CartModel cart;
	private OrderModel order;

	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;
	@Resource
	private CartFactory cartFactory;
	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private CalculationService calculationService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;
	@Resource
	private PromotionsService promotionsService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	private boolean isCacheActivated = false;
	private CachingStrategy cachingStrategy;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		final CatalogVersionModel version = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(version);
		product = productService.getProductForCode(version, "HW1210-3411");
		user = userService.getUserForUID("demo");
		userService.setCurrentUser(user);
		currency = commonI18NService.getCurrency("EUR");
		commonI18NService.setCurrentCurrency(currency);
		cart = cartFactory.createCart();

		try
		{
			cachingStrategy = (CachingStrategy) Registry.getApplicationContext().getBean(PROMOTIONS_CACHING_STRATEGY_BEAN_ID);
			isCacheActivated = true;
		}
		catch (final NoSuchBeanDefinitionException e)
		{
			isCacheActivated = false;
		}
	}

	/**
	 * Tests the relation between {@link AbstractOrderModel} and {@link PromotionResultModel}, and between
	 * {@link PromotionResultModel} and {@link AbstractPromotionActionModel}. See PRO-66 for more details. If
	 * promotionsCachingStrategy bean is activated then instead getting promotionResults from db, data is obtained from
	 * cache.
	 * <ul>
	 * <li>adds a product to cart, adds a product promotion, and update promotions for the cart,</li>
	 * <li>tests both table sizes before and after the updatePromotion method call,</li>
	 * <li>adds an order promotion, and update promotion for the cart again,</li>
	 * <li>tests both table sizes again,</li>
	 * <li>creates an order from cart without updatePromotion method call, and tests both table sizes,</li>
	 * <li>removes the cart, and tests both table sizes,</li>
	 * <li>updates promotion for the order, and tests its table size,</li>
	 * <li>removes the order, and tests both table sizes.</li>
	 * </ul>
	 */
	@Test
	public void testOrderPromotionResultRelationTest() throws CalculationException
	{
		cartService.addNewEntry(cart, product, 1, product.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);

		//there is only a cart, no promotion result or action
		testResultsSize(0);
		testActionsSize(0);

		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionsService.getPromotionGroup("prGroup3"));
		promotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());

		//one product promotion applied, one potential(higher priority), and one action for the applied one
		testResultsSize(2);
		testActionsSize(1);

		final PromotionGroupModel promotionGroup = promotionsService.getPromotionGroup("prGroup5");
		promotionGroups.add(promotionGroup);
		promotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		//one more order promotion applied, so one more action than last time 
		testResultsSize(3);
		testActionsSize(2);

		final PromotionGroupModel promotionGroup2 = promotionsService.getPromotionGroup("prGroup2");
		promotionGroups.add(promotionGroup2);
		promotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		//new situation: one product promotion and one order promotion applied, and each has an action
		//the potential promotion result disappears since its has a lower priority this time
		testResultsSize(2);
		testActionsSize(2);

		try
		{
			order = orderService.createOrderFromCart(cart);
			modelService.save(order);

			//if isCacheActivated true then order is copied, if false PromotionResults for cart is 0 and order.clone will not copy that.
			final int expectedSize = isCacheActivated ? 2 : 4;
			testResultsSize(expectedSize);
			testActionsSize(expectedSize);
		}
		catch (final InvalidCartException ice)
		{
			ice.printStackTrace();
		}

		modelService.remove(cart);

		//after cart is removed, those promotion results and actions should also be removed
		testResultsSize(2);
		testActionsSize(isCacheActivated ? 0 : 2);
		promotionsService.updatePromotions(promotionGroups, order, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		//update promotion for order, and this behavior should not affect the table size, since no more promotion group is added
		testResultsSize(2);
		testActionsSize(isCacheActivated ? 0 : 2);

		modelService.remove(order);
		//after order is removed, all promotion result and actions should be removed
		testResultsSize(isCacheActivated ? 2 : 0);
		testActionsSize(0);
	}

	private void testResultsSize(final int promotionResultSize)
	{
		int resultsSize = 0;
		if (isCacheActivated)
		{
			resultsSize = cachingStrategy.get(cart.getCode()) == null ? 0 : cachingStrategy.get(cart.getCode()).size();
		}
		else
		{
			resultsSize = getTableSize(PromotionResultModel._TYPECODE);
		}
		assertEquals("wrong promotion result size", promotionResultSize, resultsSize);
	}

	private void testActionsSize(final int promotionActionSize)
	{
		int actionsSize = 0;
		if (isCacheActivated)
		{
			if (cachingStrategy.get(cart.getCode()) != null)
			{
				for (final PromotionResult promotionResult : cachingStrategy.get(cart.getCode()))
				{
					if (promotionResult.getActions() != null)
					{
						actionsSize += promotionResult.getActions().size();
					}
				}
			}
		}
		else
		{
			actionsSize = getTableSize(AbstractPromotionActionModel._TYPECODE);
		}
		assertEquals("wrong promotion action size", promotionActionSize, actionsSize);
	}

	private int getTableSize(final String itemTypeCode)
	{
		final String query = "select count(*) from {" + itemTypeCode + "}";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		final List<Class> resultClassList = new ArrayList<Class>();
		resultClassList.add(Integer.class);
		searchQuery.setResultClassList(resultClassList);
		final Integer totalSize = (Integer) flexibleSearchService.search(searchQuery).getResult().iterator().next();
		return totalSize.intValue();
	}

}
