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
package de.hybris.platform.commerceservices.promotion.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.promotion.dao.CommercePromotionDao;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.OrderPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CommercePromotionServiceTest
{
	protected static String PRODUCT_PROMOTION_CODE = "productPromotionCode";
	protected static String PRODUCT_PROMOTION_FOR_GROUP_CODE = "productPromotionForGroupCode";
	protected static String ORDER_PROMOTION_CODE = "productPromotionCode";
	protected static String ORDER_PROMOTION_FOR_GROUP_CODE = "productPromotionForGroupCode";
	protected static String NOT_EXIST_PROMOTION_CODE = "notExistPromotionCode";

	protected List<OrderPromotionModel> orderPromotionList;
	protected List<ProductPromotionModel> productPromotionList;
	protected List<OrderPromotionModel> orderPromotionForGroupList;
	protected List<ProductPromotionModel> productPromotionForGroupList;
	protected ProductPromotionModel productPromotion;
	protected List<AbstractPromotionModel> promotionList;
	protected List<AbstractPromotionModel> duplicatePromotionList;

	protected DefaultCommercePromotionService commercePromotionService;

	@Mock
	CommercePromotionDao commercePromotionDao;

	@Mock
	Collection<PromotionGroupModel> promotionGroupCollection;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		commercePromotionService = new DefaultCommercePromotionService();
		commercePromotionService.setCommercePromotionDao(commercePromotionDao);

		orderPromotionList = new ArrayList<OrderPromotionModel>();
		OrderPromotionModel orderPromotion = new OrderPromotionModel();
		orderPromotion.setCode(ORDER_PROMOTION_CODE);
		orderPromotionList.add(orderPromotion);

		productPromotionList = new ArrayList<ProductPromotionModel>();
		productPromotion = new ProductPromotionModel();
		productPromotion.setCode(PRODUCT_PROMOTION_CODE);
		productPromotionList.add(productPromotion);

		promotionList = new ArrayList<AbstractPromotionModel>();
		promotionList.add(productPromotion);

		duplicatePromotionList = new ArrayList<AbstractPromotionModel>();
		duplicatePromotionList.add(productPromotion);
		duplicatePromotionList.add(productPromotion);

		orderPromotionForGroupList = new ArrayList<OrderPromotionModel>();
		orderPromotion = new OrderPromotionModel();
		orderPromotion.setCode(ORDER_PROMOTION_FOR_GROUP_CODE);
		orderPromotionForGroupList.add(orderPromotion);

		productPromotionForGroupList = new ArrayList<ProductPromotionModel>();
		final ProductPromotionModel productPromotionForGroup = new ProductPromotionModel();
		productPromotionForGroup.setCode(PRODUCT_PROMOTION_FOR_GROUP_CODE);
		productPromotionForGroupList.add(productPromotionForGroup);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getPromotionWithNullParameterTest()
	{
		commercePromotionService.getPromotion(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getNotExistPromotionTest()
	{
		given(commercePromotionDao.findPromotionForCode(NOT_EXIST_PROMOTION_CODE)).willReturn(
				new ArrayList<AbstractPromotionModel>());
		commercePromotionService.getPromotion(NOT_EXIST_PROMOTION_CODE);
	}

	@Test
	public void getPromotionTest()
	{
		given(commercePromotionDao.findPromotionForCode(PRODUCT_PROMOTION_CODE)).willReturn(promotionList);

		final AbstractPromotionModel result = commercePromotionService.getPromotion(PRODUCT_PROMOTION_CODE);
		Assert.assertEquals(productPromotion, result);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void getPromotionWithDuplicationTest()
	{
		given(commercePromotionDao.findPromotionForCode(PRODUCT_PROMOTION_CODE)).willReturn(duplicatePromotionList);
		commercePromotionService.getPromotion(PRODUCT_PROMOTION_CODE);
	}

	@Test
	public void getOrderPromotionsTest()
	{
		given(commercePromotionDao.findOrderPromotions()).willReturn(orderPromotionList);

		final List<OrderPromotionModel> result = commercePromotionService.getOrderPromotions();
		de.hybris.platform.testframework.Assert.assertCollection(orderPromotionList, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getOrderPromotionsForGroupWithNullParameterTest()
	{
		commercePromotionService.getOrderPromotions(null);
	}

	@Test
	public void getOrderPromotionsForGroupTest()
	{
		given(commercePromotionDao.findOrderPromotions(promotionGroupCollection)).willReturn(orderPromotionForGroupList);

		final List<OrderPromotionModel> result = commercePromotionService.getOrderPromotions(promotionGroupCollection);
		de.hybris.platform.testframework.Assert.assertCollection(orderPromotionForGroupList, result);
	}


	@Test
	public void getProductPromotionsTest()
	{
		given(commercePromotionDao.findProductPromotions()).willReturn(productPromotionList);

		final List<ProductPromotionModel> result = commercePromotionService.getProductPromotions();
		de.hybris.platform.testframework.Assert.assertCollection(productPromotionList, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getProductPromotionsForGroupWithNullParameterTest()
	{
		commercePromotionService.getProductPromotions(null);
	}

	@Test
	public void getProductPromotionsForGroupTest()
	{
		given(commercePromotionDao.findProductPromotions(promotionGroupCollection)).willReturn(productPromotionForGroupList);

		final List<ProductPromotionModel> result = commercePromotionService.getProductPromotions(promotionGroupCollection);
		de.hybris.platform.testframework.Assert.assertCollection(productPromotionForGroupList, result);
	}

}
