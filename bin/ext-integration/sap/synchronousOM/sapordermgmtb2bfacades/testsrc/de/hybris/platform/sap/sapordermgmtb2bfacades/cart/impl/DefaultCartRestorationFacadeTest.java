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
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartRestorationService;
import de.hybris.platform.sap.sapordermgmtservices.prodconf.ProductConfigurationService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Test;


@UnitTest
public class DefaultCartRestorationFacadeTest
{
	DefaultCartRestorationFacade classUnderTest = new DefaultCartRestorationFacade();


	private CartRestorationService cartRestorationServiceMock;
	private UserModel userModel;
	private CartRestorationData restoredDataByTheService;
	private CommerceCartService commerceCartServiceMock;
	private BaseSiteService baseSiteServiceMock;
	private ProductService productServiceMock;
	private ProductConfigurationService productConfigurationServiceMock;



	private void prepareMocksForRemoveSavedCart()
	{
		cartRestorationServiceMock = EasyMock.createMock(CartRestorationService.class);
		classUnderTest.setCartRestorationService(cartRestorationServiceMock);
		cartRestorationServiceMock.removeInternalSessionCart();
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(cartRestorationServiceMock);
	}


	@SuppressWarnings("boxing")
	private void prepareMocksForRestoreSavedCart(final boolean hasInternalSessionCart)
	{
		userModel = new UserModel();
		restoredDataByTheService = new CartRestorationData();

		cartRestorationServiceMock = EasyMock.createMock(CartRestorationService.class);
		classUnderTest.setCartRestorationService(cartRestorationServiceMock);
		EasyMock.expect(cartRestorationServiceMock.hasInternalSessionCart()).andReturn(hasInternalSessionCart);


		if (hasInternalSessionCart)
		{
			final CartModel cartModel1 = new CartModel();
			cartModel1.setEntries(new ArrayList<AbstractOrderEntryModel>());
			EasyMock.expect(cartRestorationServiceMock.getInternalSessionCart()).andReturn(cartModel1);
			cartRestorationServiceMock.setInternalSessionCart(null);
			EasyMock.expectLastCall().times(1);
		}

		final BaseSiteModel bSiteModel = new BaseSiteModel();
		baseSiteServiceMock = EasyMock.createMock(BaseSiteService.class);
		classUnderTest.setBaseSiteService(baseSiteServiceMock);
		EasyMock.expect(baseSiteServiceMock.getCurrentBaseSite()).andReturn(bSiteModel);

		final CartModel cartModel = new CartModel();
		commerceCartServiceMock = EasyMock.createMock(CommerceCartService.class);
		classUnderTest.setCommerceCartService(commerceCartServiceMock);
		EasyMock.expect(commerceCartServiceMock.getCartForGuidAndSiteAndUser(null, bSiteModel, userModel)).andReturn(cartModel);

		EasyMock.expect(cartRestorationServiceMock.restoreCart(cartModel)).andReturn(restoredDataByTheService);

		EasyMock.replay(baseSiteServiceMock, cartRestorationServiceMock, commerceCartServiceMock);
	}

	private void prepareMocksForSetSavedCart(final CartData newCart) throws CommerceCartModificationException
	{
		commerceCartServiceMock = EasyMock.createMock(CommerceCartService.class);
		classUnderTest.setCommerceCartService(commerceCartServiceMock);

		cartRestorationServiceMock = EasyMock.createMock(CartRestorationService.class);
		classUnderTest.setCartRestorationService(cartRestorationServiceMock);
		final CartModel cartModel1 = new CartModel();
		cartModel1.setEntries(new ArrayList<AbstractOrderEntryModel>());
		EasyMock.expect(cartRestorationServiceMock.getInternalSessionCart()).andReturn(cartModel1).atLeastOnce();
		commerceCartServiceMock.removeAllEntries(EasyMock.anyObject(CommerceCartParameter.class));
		EasyMock.expectLastCall().times(1);


		if (!newCart.getEntries().isEmpty())
		{
			productServiceMock = EasyMock.createMock(ProductService.class);
			classUnderTest.setProductService(productServiceMock);
			final ProductModel prdMod = new ProductModel();
			prdMod.setUnit(new UnitModel());
			EasyMock.expect(productServiceMock.getProductForCode("PR")).andReturn(prdMod);


			final CommerceCartModification modif = new CommerceCartModification();

			EasyMock.expect(commerceCartServiceMock.addToCart(EasyMock.anyObject(CommerceCartParameter.class))).andReturn(modif)
					.times(newCart.getEntries().size());
			EasyMock.replay(productServiceMock);

			productConfigurationServiceMock = EasyMock.createMock(ProductConfigurationService.class);
			EasyMock.expect(productConfigurationServiceMock.isInSession(EasyMock.isA(String.class))).andReturn(false);
			classUnderTest.setProductConfigurationService(productConfigurationServiceMock);
		}

		EasyMock.replay(commerceCartServiceMock, cartRestorationServiceMock);

	}

	@Test
	public void testRemoveSavedCart()
	{
		prepareMocksForRemoveSavedCart();
		classUnderTest.removeSavedCart();
		EasyMock.verify(cartRestorationServiceMock);
	}

	@Test
	public void testRestoreSavedCartNoDeleteOfEmptyCart() throws CommerceCartRestorationException
	{
		prepareMocksForRestoreSavedCart(false);
		final CartRestorationData restoredData = classUnderTest.restoreSavedCart(null, userModel);
		assertEquals(restoredDataByTheService, restoredData);
	}

	@Test
	public void testRestoreSavedCartDeleteOfEmptyCart() throws CommerceCartRestorationException
	{
		prepareMocksForRestoreSavedCart(true);
		final CartRestorationData restoredData = classUnderTest.restoreSavedCart(null, userModel);
		assertEquals(restoredDataByTheService, restoredData);

	}

	@Test
	public void testSetSavedEmptyCart() throws CommerceCartModificationException
	{

		final CartData newCart = new CartData();
		newCart.setEntries(new ArrayList<OrderEntryData>());
		prepareMocksForSetSavedCart(newCart);

		classUnderTest.setSavedCart(newCart);
		EasyMock.verify(commerceCartServiceMock);
	}

	@Test
	public void testSetSavedCart() throws CommerceCartModificationException
	{

		final CartData newCart = new CartData();
		final ArrayList<OrderEntryData> entries = new ArrayList<OrderEntryData>();
		final OrderEntryData entry1 = new OrderEntryData();
		entry1.setQuantity(new Long(2));
		final ProductData prodData = new ProductData();
		prodData.setCode("PR");
		entry1.setProduct(prodData);
		entries.add(entry1);
		newCart.setEntries(entries);

		prepareMocksForSetSavedCart(newCart);

		classUnderTest.setSavedCart(newCart);
		EasyMock.verify(commerceCartServiceMock);
	}

}
