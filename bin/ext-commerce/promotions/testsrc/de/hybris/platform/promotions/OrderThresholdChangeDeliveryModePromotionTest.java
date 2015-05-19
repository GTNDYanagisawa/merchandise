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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test for the OrderThresholdChangeDeliveryModePromotion.
 */
public class OrderThresholdChangeDeliveryModePromotionTest extends AbstractPromotionServiceTest
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
	private CalculationService calculationService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ProductService productService;
	@Resource
	private DeliveryModeService deliveryModeService;
	@Resource
	private PromotionsService promotionsService;

	private CatalogVersionModel catVersion;
	private CartModel cart;
	private UserModel user;
	private PromotionGroupModel promotionGroup;
	private PromotionGroupModel emptyPromotionGroup;
	private ProductModel product1;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importCsv("/test/orderThresholdChangeDeliverModeData.csv", "windows-1252");

		prepareData();
	}

	private void prepareData()
	{
		catVersion = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(catVersion);

		user = userService.getUserForUID("ariel");
		userService.setCurrentUser(user);
		cart = cartService.getSessionCart();
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));
		product1 = productService.getProductForCode(catVersion, "HW1230-0001");
		emptyPromotionGroup = promotionsService.getPromotionGroup("emptyGroup");
	}

	/**
	 * Puts product HW1230-0001 in the cart, sets the delivery mode to "ups" and updates the promotions in "prGroup4"
	 * which has an OrderThresholdChangeDeliveryModePromotion. Information for delivery cost in region europe: dhl costs
	 * 8.0 Euro, and ups costs 5.0 Euro.
	 * <ul>
	 * <li>without promotion, the delivery mode is "ups" which costs 5.0 Euro,</li>
	 * <li>updates promotion, and the delivery mode is "dhl" and still costs 5.0 Euro,</li>
	 * <li>updates promotion again, and the delivery mode remains the same as "dhl" and still costs 5.0 Euro,</li>
	 * <li>enables the update delivery cost mode, and the delivery mode is still "dhl", but costs 8.0 Euro.</li>
	 * </ul>
	 */
	@Test
	public void testUpgradeDeliveryMode() throws CalculationException
	{
		//PRO-78
		final String originalExpectedDeliveryMode = "ups";
		double deliveryCost = 5.0;
		promotionGroup = promotionsService.getPromotionGroup("prGroup4");
		final List<PromotionGroupModel> groups = new ArrayList<PromotionGroupModel>();
		groups.add(promotionGroup);
		cartService.addNewEntry(cart, product1, 1, product1.getUnit());
		final DeliveryModeModel deliveryModeUps = deliveryModeService.getDeliveryModeForCode("ups");
		cart.setDeliveryMode(deliveryModeUps);
		cart.setDeliveryAddress(user.getDefaultShipmentAddress());
		modelService.save(cart);

		//without promotion
		calculationService.calculate(cart);
		testDeliveryCost(cart, deliveryCost, originalExpectedDeliveryMode);

		//the delivery mode is upgraded after the OrderThresholdChangeDeliveryModePromotion is applied
		final String expectedDeliveryMode = "dhl";

		//the first round with OrderThresholdChangeDeliveryModePromotion
		promotionsService.updatePromotions(groups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new java.util.Date());
		testDeliveryCost(cart, deliveryCost, expectedDeliveryMode);

		//the second round with OrderThresholdChangeDeliveryModePromotion
		promotionsService.updatePromotions(groups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new java.util.Date());
		testDeliveryCost(cart, deliveryCost, expectedDeliveryMode);

		//the last round which requires to update the delivery cost
		Config.setParameter("orderThresholdChangeDeliveryMode.updateDeliveryCost", "true");
		deliveryCost = 8.0;
		promotionsService.updatePromotions(groups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new java.util.Date());
		testDeliveryCost(cart, deliveryCost, expectedDeliveryMode);

		//PRO-128, remove the OrderThresholdChangeDeliveryModePromotion
		groups.clear();
		groups.add(emptyPromotionGroup);
		promotionsService.updatePromotions(groups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new java.util.Date());
		deliveryCost = 5.0;
		testDeliveryCost(cart, deliveryCost, originalExpectedDeliveryMode);
	}

	private void testDeliveryCost(final CartModel cart, final double expectedDeliveryCost, final String expectedDeliveryMode)
	{
		modelService.refresh(cart);
		assertEquals(expectedDeliveryCost, cart.getDeliveryCost().doubleValue(), 0.01);
		assertEquals(expectedDeliveryMode, cart.getDeliveryMode().getCode());
	}

}
