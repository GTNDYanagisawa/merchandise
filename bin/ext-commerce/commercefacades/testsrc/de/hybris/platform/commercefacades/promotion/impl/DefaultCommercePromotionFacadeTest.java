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
package de.hybris.platform.commercefacades.promotion.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.promotion.PromotionOption;
import de.hybris.platform.commerceservices.promotion.CommercePromotionService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.OrderPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


@UnitTest
public class DefaultCommercePromotionFacadeTest
{
	protected static String GROUP_CODE = "groupCode";
	protected static String PROMOTION_CODE = "promotionCode";
	protected DefaultCommercePromotionFacade commercePromotionFacade;
	@Mock
	protected Converter<AbstractPromotionModel, PromotionData> promotionsConverter;
	@Mock
	protected ConfigurablePopulator<AbstractPromotionModel, PromotionData, PromotionOption> promotionConfiguredPopulator;
	@Mock
	protected CommercePromotionService commercePromotionService;
	@Mock
	protected PromotionsService promotionsService;
	protected ProductPromotionModel productPromotion;
	protected ProductPromotionModel productPromotionForGroup;
	protected OrderPromotionModel orderPromotion;
	protected OrderPromotionModel orderPromotionForGroup;
	protected List<ProductPromotionModel> productPromotionList;
	protected List<OrderPromotionModel> orderPromotionList;
	protected List<ProductPromotionModel> productPromotionForGroupList;
	protected List<OrderPromotionModel> orderPromotionForGroupList;
	protected PromotionData productPromotionData;
	protected PromotionData productPromotionForGroupData;
	protected PromotionData orderPromotionData;
	protected PromotionData orderPromotionForGroupData;
	protected List<String> groupCodes;
	protected PromotionGroupModel promotionGroup;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		commercePromotionFacade = new DefaultCommercePromotionFacade();
		commercePromotionFacade.setPromotionsConverter(promotionsConverter);
		commercePromotionFacade.setCommercePromotionService(commercePromotionService);
		commercePromotionFacade.setPromotionsService(promotionsService);
		commercePromotionFacade.setPromotionConfiguredPopulator(promotionConfiguredPopulator);

		productPromotion = new ProductPromotionModel();
		orderPromotion = new OrderPromotionModel();
		productPromotionForGroup = new ProductPromotionModel();
		orderPromotionForGroup = new OrderPromotionModel();

		productPromotionData = new PromotionData();
		productPromotionForGroupData = new PromotionData();
		orderPromotionData = new PromotionData();
		orderPromotionForGroupData = new PromotionData();

		productPromotionList = new ArrayList<ProductPromotionModel>();
		productPromotionList.add(productPromotion);
		productPromotionList.add(productPromotionForGroup);

		orderPromotionList = new ArrayList<OrderPromotionModel>();
		orderPromotionList.add(orderPromotion);
		orderPromotionList.add(orderPromotionForGroup);

		productPromotionForGroupList = new ArrayList<ProductPromotionModel>();
		productPromotionForGroupList.add(productPromotionForGroup);

		orderPromotionForGroupList = new ArrayList<OrderPromotionModel>();
		orderPromotionForGroupList.add(orderPromotionForGroup);

		groupCodes = new ArrayList<String>();
		groupCodes.add(GROUP_CODE);

		promotionGroup = new PromotionGroupModel();

		given(promotionsConverter.convert(productPromotion)).willReturn(productPromotionData);
		given(promotionsConverter.convert(orderPromotion)).willReturn(orderPromotionData);
		given(promotionsConverter.convert(productPromotionForGroup)).willReturn(productPromotionForGroupData);
		given(promotionsConverter.convert(orderPromotionForGroup)).willReturn(orderPromotionForGroupData);

		given(commercePromotionService.getProductPromotions()).willReturn(productPromotionList);
		given(commercePromotionService.getOrderPromotions()).willReturn(orderPromotionList);
		given(commercePromotionService.getProductPromotions(Mockito.any(Collection.class)))
				.willReturn(productPromotionForGroupList);
		given(commercePromotionService.getOrderPromotions(Mockito.any(Collection.class))).willReturn(orderPromotionForGroupList);
		given(commercePromotionService.getPromotion(PROMOTION_CODE)).willReturn(productPromotion);

		given(promotionsService.getPromotionGroup(GROUP_CODE)).willReturn(promotionGroup);

	}

	@Test
	public void testGetProductPromotions()
	{
		final List<PromotionData> result = commercePromotionFacade.getProductPromotions();
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertSame(productPromotionData, result.get(0));
		Assert.assertSame(productPromotionForGroupData, result.get(1));
	}

	@Test
	public void testGetOrderPromotions()
	{
		final List<PromotionData> result = commercePromotionFacade.getOrderPromotions();
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertSame(orderPromotionData, result.get(0));
		Assert.assertSame(orderPromotionForGroupData, result.get(1));
	}

	@Test
	public void testGetProductPromotionsForGroup()
	{
		final List<PromotionData> result = commercePromotionFacade.getProductPromotions(GROUP_CODE);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertSame(productPromotionForGroupData, result.get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetProductPromotionsForGroupWithNull()
	{
		commercePromotionFacade.getProductPromotions((String) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetProductPromotionsForNotExistingGroup()
	{
		commercePromotionFacade.getProductPromotions("notExistCode");
	}

	@Test
	public void testGetProductPromotionsForGroupCollection()
	{
		final List<PromotionData> result = commercePromotionFacade.getProductPromotions(groupCodes);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertSame(productPromotionForGroupData, result.get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetProductPromotionsForEmptyGroupCollection()
	{
		commercePromotionFacade.getProductPromotions(new ArrayList());
	}

	@Test
	public void testGetOrderPromotionsForGroup()
	{
		final List<PromotionData> result = commercePromotionFacade.getOrderPromotions(GROUP_CODE);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertSame(orderPromotionForGroupData, result.get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetOrderPromotionsForGroupWithNull()
	{
		commercePromotionFacade.getOrderPromotions((String) null);
	}

	@Test
	public void testGetOrderPromotionsForGroupCollection()
	{
		final List<PromotionData> result = commercePromotionFacade.getOrderPromotions(groupCodes);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertSame(orderPromotionForGroupData, result.get(0));
	}

	@Test
	public void testGetPromotion()
	{
		final PromotionData result = commercePromotionFacade.getPromotion(PROMOTION_CODE);
		Assert.assertSame(productPromotionData, result);
	}

	@Test
	public void testGetPromotionForOptions()
	{
		final Collection<PromotionOption> options = Lists.newArrayList(PromotionOption.EXTENDED);
		final PromotionData result = commercePromotionFacade.getPromotion(PROMOTION_CODE, options);
		verify(promotionConfiguredPopulator, times(1)).populate(productPromotion, productPromotionData, options);
		Assert.assertSame(productPromotionData, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPromotionWithNullParameter()
	{
		commercePromotionFacade.getPromotion(null);
	}

}
