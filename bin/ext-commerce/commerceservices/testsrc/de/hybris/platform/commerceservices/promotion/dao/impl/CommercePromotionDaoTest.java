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
package de.hybris.platform.commerceservices.promotion.dao.impl;

import de.hybris.platform.commerceservices.promotion.dao.CommercePromotionDao;
import de.hybris.platform.promotions.AbstractPromotionServiceTest;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.OrderPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class CommercePromotionDaoTest extends AbstractPromotionServiceTest
{
	protected static String FIXED_PRICE_PROMOTION_CODE = "FixedPriceGr1";
	protected static String PROMOTION_GROUP_1 = "prGroup1";
	protected static String PROMOTION_GROUP_2 = "prGroup2";
	protected static String PROMOTION_GROUP_5 = "prGroup5";

	@Resource
	protected CommercePromotionDao commercePromotionDao;

	@Resource
	protected PromotionsService promotionsService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
	}

	@Test
	public void findPromotionByCodeTest()
	{
		final List<AbstractPromotionModel> promotionList = commercePromotionDao.findPromotionForCode(FIXED_PRICE_PROMOTION_CODE);
		Assert.assertFalse(promotionList.isEmpty());
		Assert.assertTrue(FIXED_PRICE_PROMOTION_CODE.equals(promotionList.get(0).getCode()));
	}

	@Test
	public void findNotExistPromotionByCodeTest()
	{
		final List<AbstractPromotionModel> promotionList = commercePromotionDao.findPromotionForCode("NotExistPromotion");
		Assert.assertTrue(promotionList.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPromotionByCodeWithNullParameterTest()
	{
		commercePromotionDao.findPromotionForCode(null);
	}

	@Test
	public void findProductPromotionTest()
	{
		final List<ProductPromotionModel> promotionList = commercePromotionDao.findProductPromotions();
		Assert.assertTrue(promotionList.size() == 8);
	}

	@Test
	public void findOrderPromotionTest()
	{
		final List<OrderPromotionModel> promotionList = commercePromotionDao.findOrderPromotions();
		Assert.assertTrue(promotionList.size() == 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findProductPromotionForGroupWithNullParameterTest()
	{
		commercePromotionDao.findProductPromotions(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findProductPromotionForGroupWithEmptyListTest()
	{
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		commercePromotionDao.findProductPromotions(promotionGroups);
	}

	@Test
	public void findProductPromotionForGroupTest()
	{
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionsService.getPromotionGroup(PROMOTION_GROUP_1));
		List<ProductPromotionModel> promotionList = commercePromotionDao.findProductPromotions(promotionGroups);
		Assert.assertTrue(promotionList.size() == 2);

		promotionGroups.add(promotionsService.getPromotionGroup(PROMOTION_GROUP_2));
		promotionList = commercePromotionDao.findProductPromotions(promotionGroups);
		Assert.assertTrue(promotionList.size() == 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findOrderPromotionForGroupWithNullParameterTest()
	{
		commercePromotionDao.findOrderPromotions(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findOrderPromotionForGroupWithEmptyListTest()
	{
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		commercePromotionDao.findOrderPromotions(promotionGroups);
	}

	@Test
	public void findOrderPromotionForGroupTest()
	{
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionsService.getPromotionGroup(PROMOTION_GROUP_1));
		List<OrderPromotionModel> promotionList = commercePromotionDao.findOrderPromotions(promotionGroups);
		Assert.assertTrue(promotionList.isEmpty());

		promotionGroups.add(promotionsService.getPromotionGroup(PROMOTION_GROUP_5));
		promotionList = commercePromotionDao.findOrderPromotions(promotionGroups);
		Assert.assertTrue(promotionList.size() == 1);
	}
}
