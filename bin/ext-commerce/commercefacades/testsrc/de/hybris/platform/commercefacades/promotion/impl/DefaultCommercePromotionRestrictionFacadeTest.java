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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.commerceservices.model.promotions.PromotionOrderRestrictionModel;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionException;
import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionService;
import de.hybris.platform.commerceservices.promotion.CommercePromotionService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.OrderPromotionModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test for {@link DefaultCommercePromotionRestrictionFacade}
 */
public class DefaultCommercePromotionRestrictionFacadeTest
{
	final static String ORDER_PROMOTION_CODE = "orderPromotionCode";
	final static String ABSTRACT_PROMOTION_CODE = "abstractPromotionCode";
	final static String NOT_EXISTING_PROMOTION_CODE = "notExistingPromotion";
	@Mock
	protected CommercePromotionService commercePromotionService;
	@Mock
	protected CommercePromotionRestrictionService commercePromotionRestrictionService;
	@Mock
	protected CartService cartService;
	@Mock
	protected CommerceCartService commerceCartService;
	@Mock
	protected AbstractPromotionModel abstractPromotionModel;
	@Mock
	protected OrderPromotionModel orderPromotionModel;
	@Mock
	protected PromotionOrderRestrictionModel promotionOrderRestrictionModel;
	@Mock
	protected CartModel cartModel;
	protected DefaultCommercePromotionRestrictionFacade commercePromotionRestrictionFacade;
	@Mock
	protected CommerceCartParameter commerceCartParameter;
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		commercePromotionRestrictionFacade = new DefaultCommercePromotionRestrictionFacade();
		commercePromotionRestrictionFacade.setCommercePromotionService(commercePromotionService);
		commercePromotionRestrictionFacade.setCommercePromotionRestrictionService(commercePromotionRestrictionService);
		commercePromotionRestrictionFacade.setCartService(cartService);
		commercePromotionRestrictionFacade.setCommerceCartService(commerceCartService);
	}

	@Test
	public void testEnablePromotionForCurrentCart() throws CommercePromotionRestrictionException, CalculationException
	{
		given(commercePromotionService.getPromotion(ORDER_PROMOTION_CODE)).willReturn(orderPromotionModel);
		given(commercePromotionRestrictionService.getPromotionOrderRestriction(orderPromotionModel)).willReturn(
				promotionOrderRestrictionModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		commercePromotionRestrictionFacade.enablePromotionForCurrentCart(ORDER_PROMOTION_CODE);
		verify(commercePromotionRestrictionService, times(1)).addOrderToRestriction(promotionOrderRestrictionModel, cartModel);
		verify(commerceCartService, times(1)).recalculateCart(any(CommerceCartParameter.class));
	}
}
