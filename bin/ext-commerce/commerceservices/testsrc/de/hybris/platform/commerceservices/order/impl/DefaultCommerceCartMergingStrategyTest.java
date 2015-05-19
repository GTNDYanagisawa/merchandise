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
package de.hybris.platform.commerceservices.order.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCommerceCartMergingStrategyTest
{
	private final DefaultCommerceCartMergingStrategy mergingStrategy = new DefaultCommerceCartMergingStrategy();

	@Mock
	private ProductModel commonProductMock;
	@Mock
	private ProductModel productMock;
	@Mock
	private UserService userService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ModelService modelService;
	@Mock
	private DefaultCommerceAddToCartStrategy addToCartStrategy;
	@Mock
	private CartModel toCart;

	private CartModel fromCart;
	private CommerceCartModification commonCartModificationSuccess;
	private CommerceCartModification commonCartModificationPartialSuccess;
	private CommerceCartModification commonCartModificationNoSuccess;
	private CommerceCartModification notCommonCartModification;
	private final PK pk = PK.BIG_PK;
	private CartEntryModel cartEntryModel1;
	private CartEntryModel cartEntryModel3;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		mergingStrategy.setBaseSiteService(baseSiteService);
		mergingStrategy.setCommerceAddToCartStrategy(addToCartStrategy);
		mergingStrategy.setModelService(modelService);
		mergingStrategy.setUserService(userService);

		final UserModel userModel = new UserModel();
		given(userService.getCurrentUser()).willReturn(userModel);
		given(Boolean.valueOf(userService.isAnonymousUser(userModel))).willReturn(Boolean.FALSE);

		fromCart = new CartModel();
		fromCart.setGuid("guidFromCart");

		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		fromCart.setSite(baseSiteModel);

		cartEntryModel1 = spy(new CartEntryModel());
		cartEntryModel1.setEntryNumber(Integer.valueOf(0));
		cartEntryModel1.setQuantity(Long.valueOf(6));
		cartEntryModel1.setProduct(commonProductMock);

		final CartEntryModel cartEntryModel2 = new CartEntryModel();
		cartEntryModel2.setEntryNumber(Integer.valueOf(1));
		cartEntryModel2.setQuantity(Long.valueOf(9));
		cartEntryModel2.setProduct(productMock);

		final List<AbstractOrderEntryModel> entryList = new ArrayList<>();
		entryList.add(cartEntryModel1);
		entryList.add(cartEntryModel2);
		fromCart.setEntries(entryList);

		when(toCart.getSite()).thenReturn(baseSiteModel);

		cartEntryModel3 = spy(new CartEntryModel());
		cartEntryModel3.setEntryNumber(Integer.valueOf(0));
		cartEntryModel3.setQuantity(Long.valueOf(6));
		cartEntryModel3.setProduct(commonProductMock);

		final List<AbstractOrderEntryModel> entryList2 = new ArrayList<>();
		entryList2.add(cartEntryModel3);

		when(toCart.getEntries()).thenReturn(entryList2);
		given(baseSiteService.getCurrentBaseSite()).willReturn(fromCart.getSite());

		commonCartModificationSuccess = new CommerceCartModification();
		commonCartModificationSuccess.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		commonCartModificationSuccess.setQuantityAdded(2);
		commonCartModificationSuccess.setQuantity(2);
		commonCartModificationSuccess.setEntry(fromCart.getEntries().get(0));

		commonCartModificationPartialSuccess = new CommerceCartModification();
		commonCartModificationPartialSuccess.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
		commonCartModificationPartialSuccess.setQuantityAdded(1);
		commonCartModificationPartialSuccess.setQuantity(2);
		commonCartModificationPartialSuccess.setEntry(fromCart.getEntries().get(0));

		commonCartModificationNoSuccess = new CommerceCartModification();
		commonCartModificationNoSuccess.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
		commonCartModificationNoSuccess.setQuantityAdded(0);
		commonCartModificationNoSuccess.setQuantity(2);
		commonCartModificationNoSuccess.setEntry(fromCart.getEntries().get(0));

		notCommonCartModification = new CommerceCartModification();
		notCommonCartModification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		notCommonCartModification.setQuantityAdded(3);
		notCommonCartModification.setQuantity(3);
		notCommonCartModification.setEntry(fromCart.getEntries().get(1));

	}

	@Test
	public void testMergeInStockModifications() throws CommerceCartMergingException
	{
		when(cartEntryModel1.getPk()).thenReturn(pk);
		when(cartEntryModel3.getPk()).thenReturn(pk);
		final CommerceCartModification cartModification = new CommerceCartModification();
		cartModification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		cartModification.setQuantityAdded(6);
		cartModification.setQuantity(6);
		cartModification.setEntry(toCart.getEntries().get(0));

		final List<CommerceCartModification> list = new ArrayList<>();
		list.add(cartModification);

		try
		{
			when(addToCartStrategy.addToCart(any(CommerceCartParameter.class))).thenReturn(commonCartModificationSuccess)
					.thenReturn(notCommonCartModification);
		}
		catch (final CommerceCartModificationException e)
		{
			e.printStackTrace();
		}

		mergingStrategy.mergeCarts(fromCart, toCart, list);
		Assert.assertEquals(list.get(0).getQuantity(), 8);
		Assert.assertEquals(list.get(0).getQuantityAdded(), 8);
		Assert.assertEquals(list.get(0).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
		Assert.assertEquals(list.get(1).getQuantity(), 3);
		Assert.assertEquals(list.get(1).getQuantityAdded(), 3);
		Assert.assertEquals(list.get(1).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
	}

	@Test
	public void testMergePartiallyOutOfStockModifications() throws CommerceCartMergingException
	{
		when(cartEntryModel1.getPk()).thenReturn(pk);
		when(cartEntryModel3.getPk()).thenReturn(pk);
		final CommerceCartModification cartModification = new CommerceCartModification();
		cartModification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		cartModification.setQuantityAdded(6);
		cartModification.setQuantity(6);
		cartModification.setEntry(toCart.getEntries().get(0));

		final List<CommerceCartModification> list = new ArrayList<>();
		list.add(cartModification);

		try
		{
			when(addToCartStrategy.addToCart(any(CommerceCartParameter.class))).thenReturn(commonCartModificationPartialSuccess)
					.thenReturn(notCommonCartModification);
		}
		catch (final CommerceCartModificationException e)
		{
			e.printStackTrace();
		}

		mergingStrategy.mergeCarts(fromCart, toCart, list);
		Assert.assertEquals(list.get(0).getQuantity(), 8);
		Assert.assertEquals(list.get(0).getQuantityAdded(), 7);
		Assert.assertEquals(list.get(0).getStatusCode(), CommerceCartModificationStatus.NO_STOCK);
		Assert.assertEquals(list.get(1).getQuantity(), 3);
		Assert.assertEquals(list.get(1).getQuantityAdded(), 3);
		Assert.assertEquals(list.get(1).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
	}

	@Test
	public void testMergeOutOfStockModifications() throws CommerceCartMergingException
	{
		when(cartEntryModel1.getPk()).thenReturn(pk);
		when(cartEntryModel3.getPk()).thenReturn(pk);
		final CommerceCartModification cartModification = new CommerceCartModification();
		cartModification.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
		cartModification.setQuantityAdded(0);
		cartModification.setQuantity(6);
		cartModification.setEntry(toCart.getEntries().get(0));

		final List<CommerceCartModification> list = new ArrayList<>();
		list.add(cartModification);

		try
		{
			when(addToCartStrategy.addToCart(any(CommerceCartParameter.class))).thenReturn(commonCartModificationNoSuccess)
					.thenReturn(notCommonCartModification);
		}
		catch (final CommerceCartModificationException e)
		{
			e.printStackTrace();
		}

		mergingStrategy.mergeCarts(fromCart, toCart, list);
		Assert.assertEquals(list.get(0).getQuantity(), 8);
		Assert.assertEquals(list.get(0).getQuantityAdded(), 0);
		Assert.assertEquals(list.get(0).getStatusCode(), CommerceCartModificationStatus.NO_STOCK);
		Assert.assertEquals(list.get(1).getQuantity(), 3);
		Assert.assertEquals(list.get(1).getQuantityAdded(), 3);
		Assert.assertEquals(list.get(1).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
	}


	@Test
	public void testMergeInStockModificationsSeparateEntries() throws CommerceCartMergingException
	{
		final CommerceCartModification cartModification = new CommerceCartModification();
		cartModification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		cartModification.setQuantityAdded(6);
		cartModification.setQuantity(6);
		cartModification.setEntry(toCart.getEntries().get(0));

		final List<CommerceCartModification> list = new ArrayList<>();
		list.add(cartModification);

		try
		{
			when(addToCartStrategy.addToCart(any(CommerceCartParameter.class))).thenReturn(commonCartModificationSuccess)
					.thenReturn(notCommonCartModification);
		}
		catch (final CommerceCartModificationException e)
		{
			e.printStackTrace();
		}

		mergingStrategy.mergeCarts(fromCart, toCart, list);
		Assert.assertEquals(list.get(0).getQuantity(), 6);
		Assert.assertEquals(list.get(0).getQuantityAdded(), 6);
		Assert.assertEquals(list.get(0).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
		Assert.assertEquals(list.get(1).getQuantity(), 2);
		Assert.assertEquals(list.get(1).getQuantityAdded(), 2);
		Assert.assertEquals(list.get(1).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
		Assert.assertEquals(list.get(2).getQuantity(), 3);
		Assert.assertEquals(list.get(2).getQuantityAdded(), 3);
		Assert.assertEquals(list.get(2).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
	}

	@Test
	public void testMergePartiallyOutOfStockModificationsSeparateEntries() throws CommerceCartMergingException
	{
		final CommerceCartModification cartModification = new CommerceCartModification();
		cartModification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		cartModification.setQuantityAdded(6);
		cartModification.setQuantity(6);
		cartModification.setEntry(toCart.getEntries().get(0));

		final List<CommerceCartModification> list = new ArrayList<>();
		list.add(cartModification);

		try
		{
			when(addToCartStrategy.addToCart(any(CommerceCartParameter.class))).thenReturn(commonCartModificationPartialSuccess)
					.thenReturn(notCommonCartModification);
		}
		catch (final CommerceCartModificationException e)
		{
			e.printStackTrace();
		}

		mergingStrategy.mergeCarts(fromCart, toCart, list);
		Assert.assertEquals(list.get(0).getQuantity(), 6);
		Assert.assertEquals(list.get(0).getQuantityAdded(), 6);
		Assert.assertEquals(list.get(0).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
		Assert.assertEquals(list.get(1).getQuantity(), 2);
		Assert.assertEquals(list.get(1).getQuantityAdded(), 1);
		Assert.assertEquals(list.get(1).getStatusCode(), CommerceCartModificationStatus.NO_STOCK);
		Assert.assertEquals(list.get(2).getQuantity(), 3);
		Assert.assertEquals(list.get(2).getQuantityAdded(), 3);
		Assert.assertEquals(list.get(2).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
	}

	@Test
	public void testMergeOutOfStockModificationsSeparateEntries() throws CommerceCartMergingException
	{
		final CommerceCartModification cartModification = new CommerceCartModification();
		cartModification.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
		cartModification.setQuantityAdded(0);
		cartModification.setQuantity(6);
		cartModification.setEntry(toCart.getEntries().get(0));

		final List<CommerceCartModification> list = new ArrayList<>();
		list.add(cartModification);

		try
		{
			when(addToCartStrategy.addToCart(any(CommerceCartParameter.class))).thenReturn(commonCartModificationNoSuccess)
					.thenReturn(notCommonCartModification);
		}
		catch (final CommerceCartModificationException e)
		{
			e.printStackTrace();
		}

		mergingStrategy.mergeCarts(fromCart, toCart, list);
		Assert.assertEquals(list.get(0).getQuantity(), 6);
		Assert.assertEquals(list.get(0).getQuantityAdded(), 0);
		Assert.assertEquals(list.get(0).getStatusCode(), CommerceCartModificationStatus.NO_STOCK);
		Assert.assertEquals(list.get(1).getQuantity(), 2);
		Assert.assertEquals(list.get(1).getQuantityAdded(), 0);
		Assert.assertEquals(list.get(1).getStatusCode(), CommerceCartModificationStatus.NO_STOCK);
		Assert.assertEquals(list.get(2).getQuantity(), 3);
		Assert.assertEquals(list.get(2).getQuantityAdded(), 3);
		Assert.assertEquals(list.get(2).getStatusCode(), CommerceCartModificationStatus.SUCCESS);
	}
}
