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
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
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
 * Test the PromotionUserRestriction.
 */
public class PromotionUserRestrictionTest extends ServicelayerTransactionalTest
{

	private ProductModel product1;

	private UserModel userDemo;
	private UserModel employee01;
	private UserModel employee02;
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

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
		importCsv("/test/promotionUserRestrictionTestData.csv", "windows-1252");

		prepareData();
	}

	private void prepareData()
	{
		final CatalogVersionModel version = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(version);
		product1 = productService.getProductForCode(version, "HW1210-3411");
		userDemo = userService.getUserForUID("demo");//customergroup
		employee01 = userService.getUserForUID("employee01");//NOT customergroup
		employee02 = userService.getUserForUID("employee02");//NOT customergroup
		currency = commonI18NService.getCurrency("EUR");
		commonI18NService.setCurrentCurrency(currency);
	}

	/**
	 * Tests the user restriction under different circumstances.
	 * <ul>
	 * <li>background:</li>
	 * <li>demo is in customergroup,</li>
	 * <li>employee01 is in group11 that belongs to group01,</li>
	 * <li>besides, employee01 is in group12 that belongs to group05,</li>
	 * <li>employee02 is in group02,</li>
	 * <li>tests start:</li>
	 * <li>case 1</li>
	 * <li>promotion is fired for demo because he is in customergroup,</li>
	 * <li>case 2</li>
	 * <li>promotion is fired for employee01 because of the group hierarchy(group11-->group01),</li>
	 * <li>promotion is NOT fired for employee02 because he is not in group01,</li>
	 * <li>case 3</li>
	 * <li>promotion is fired for demo because he is exactly the user,</li>
	 * <li>case 4</li>
	 * <li>promotion is NOT fired for employee01 because he is not in group02,</li>
	 * <li>promotion is fired for employee02 because he is in group02,</li>
	 * <li>case 5</li>
	 * <li>promotion is fired for employee01 because of the group hierarchy(group12-->group05),</li>
	 * <li>promotion is NOT fired for employee02 because he is not in group05.</li>
	 * </ul>
	 */
	@Test
	public void testPromotionUserRestriction() throws CalculationException
	{
		testCart(userDemo, "prGroup1", 253.0d, 200.0d);

		testCart(employee01, "prGroup2", 253.0d, 200.0d);
		testCart(employee02, "prGroup2", 253.0d, 253.0d);

		testCart(userDemo, "prGroup3", 253.0d, 200.0d);

		testCart(employee01, "prGroup4", 253.0d, 253.0d);
		testCart(employee02, "prGroup4", 253.0d, 200.0d);

		testCart(employee01, "prGroup5", 253.0d, 200.0d);
		testCart(employee02, "prGroup5", 253.0d, 253.0d);
	}

	private void testCart(final UserModel cartUser, final String promotionGroupCode, final double beforeTotal,
			final double afterTotal) throws CalculationException
	{
		userService.setCurrentUser(cartUser);
		cartService.removeSessionCart();
		cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product1, 1, product1.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);
		assertEquals("cart total before updatePromotions for customer", beforeTotal, cart.getTotalPrice().doubleValue(), 0.01);

		promotionGroup = promotionsService.getPromotionGroup(promotionGroupCode);
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionGroup);
		promotionsService.updatePromotions(promotionGroups, cart, true, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		modelService.refresh(cart);
		assertEquals("cart total before updatePromotions for customer", afterTotal, cart.getTotalPrice().doubleValue(), 0.01);
	}

}
