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
package de.hybris.platform.sap.sapordermgmtservices.cart.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.sap.sapordermgmtservices.prodconf.ProductConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;


@UnitTest
@SuppressWarnings(
{ "javadoc", "unchecked" })
public class DefaultCartRestorationServiceTest extends SapordermanagmentBolSpringJunitTest
{
	private final DefaultCartRestorationService classUnderTest = new DefaultCartRestorationService();
	private SessionService sessionServiceMock;
	private CartService cartServiceMock;

	private ProductConfigurationService productConfigurationServiceMock;

	@Test
	public void testRestoreCartForProductsWithoutConfiguration()
	{


		final CartModel fromCart = getCartModel();
		prepareMocksForRestoreCart(fromCart);

		final CartRestorationData restData = classUnderTest.restoreCart(fromCart);
		assertTrue(restData != null);
		EasyMock.verify(sessionServiceMock);
	}

	@Test
	public void testCreateItemsFromCart()
	{
		productConfigurationServiceMock = EasyMock.createMock(ProductConfigurationService.class);
		EasyMock.expect(productConfigurationServiceMock.getConfigModel("KAP", null)).andReturn(null);
		EasyMock.expect(productConfigurationServiceMock.getConfigModel("KAC", null)).andReturn(null);
		classUnderTest.setProductConfigurationService(productConfigurationServiceMock);

		EasyMock.replay(productConfigurationServiceMock);


		final CartModel fromCart = getCartModel();
		final List<Item> items = classUnderTest.createItemsFromCart(fromCart);
		assertTrue(items.size() == 2);
	}


	@Test
	public void testSetConfigurationToItem()
	{
		final String EXT_CONF = "ext_conf";

		final Item item = new ItemSalesDoc();

		final AbstractOrderEntryModel entry1 = prepareMocksForSetConfToItem(EXT_CONF, item);

		classUnderTest.setConfigurationToItem(entry1, item);
		assertTrue(item.isConfigurable());
	}

	/**
	 * @param EXT_CONF
	 * @param item
	 * @return
	 */
	private AbstractOrderEntryModel prepareMocksForSetConfToItem(final String EXT_CONF, final Item item)
	{
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		final ProductModel pModel = new ProductModel();
		pModel.setCode("KAP");
		entry1.setProduct(pModel);
		entry1.setQuantity(new Long(2));
		entry1.setExternalConfiguration(EXT_CONF);

		productConfigurationServiceMock = EasyMock.createMock(ProductConfigurationService.class);
		final ConfigModel confModel = new ConfigModelImpl();

		EasyMock.expect(
				productConfigurationServiceMock.getConfigModel(entry1.getProduct().getCode(), entry1.getExternalConfiguration()))
				.andReturn(confModel);
		productConfigurationServiceMock.setIntoSession(item.getHandle(), null);
		EasyMock.expectLastCall();
		classUnderTest.setProductConfigurationService(productConfigurationServiceMock);

		EasyMock.replay(productConfigurationServiceMock);
		return entry1;
	}



	private void prepareMocksForRestoreCart(final CartModel fromCart)
	{

		sessionServiceMock = EasyMock.createMock(SessionService.class);
		sessionServiceMock.setAttribute(DefaultCartRestorationService.SESSION_CART_PARAMETER_NAME, fromCart);
		classUnderTest.setSessionService(sessionServiceMock);

		cartServiceMock = EasyMock.createMock(CartService.class);

		cartServiceMock.addItemsToCart(EasyMock.isA(ArrayList.class));

		EasyMock.expectLastCall();

		classUnderTest.setCartService(cartServiceMock);

		productConfigurationServiceMock = EasyMock.createMock(ProductConfigurationService.class);
		EasyMock.expect(productConfigurationServiceMock.getConfigModel("KAP", null)).andReturn(null);
		EasyMock.expect(productConfigurationServiceMock.getConfigModel("KAC", null)).andReturn(null);
		classUnderTest.setProductConfigurationService(productConfigurationServiceMock);

		EasyMock.replay(sessionServiceMock, productConfigurationServiceMock);

	}

	/**
	 * @return
	 */
	private CartModel getCartModel()
	{
		final CartModel fromCart = new CartModel();
		final List<AbstractOrderEntryModel> listEntry = new ArrayList<AbstractOrderEntryModel>();

		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		ProductModel pModel = new ProductModel();
		pModel.setCode("KAP");
		entry1.setProduct(pModel);
		entry1.setQuantity(new Long(2));
		entry1.setExternalConfiguration(null);
		listEntry.add(entry1);



		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		pModel = new ProductModel();
		pModel.setCode("KAC");
		entry2.setProduct(pModel);
		entry2.setQuantity(new Long(3));
		entry2.setExternalConfiguration(null);
		listEntry.add(entry2);

		fromCart.setEntries(listEntry);
		return fromCart;
	}

}
