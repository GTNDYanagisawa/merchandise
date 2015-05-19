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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.sap.sapordermgmtservices.impl.DefaultBackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.prodconf.ProductConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultCartFacadeTest
{
	DefaultCartFacade classUnderTest = new DefaultCartFacade();
	CartFacade standardFacade = null;
	private final String code = "A";
	private final long quantity = 1;
	private final CartModificationData cartModificationData = new CartModificationData();
	private final CartModificationData cartModificationDataBackendDown = new CartModificationData();
	OrderEntryData newEntry = new OrderEntryData();
	private final String storeId = "Store";
	private CartService cartService;
	ConfigurationProviderFactory configurationProviderFactory;
	ConfigurationProvider configurationProvider;
	ProductConfigurationService productConfigurationService;
	UserService userService;
	private UserService userServiceLoggedInUser;
	private ProductImageHelper productImageHelper;
	private final UserModel userModel = new UserModel();
	private final CartData sessionCart = new CartData();
	private final CartData sessionCartBackendDown = new CartData();
	private final List<CartModificationData> validateCartData = new ArrayList<>();
	private final long entryNumber = 10;
	private final CartRestorationData cartRestorationData = new CartRestorationData();
	private final DefaultBackendAvailabilityService backendAvailable = new DefaultBackendAvailabilityService();
	long quantityOne = 1;
	long quantityZero = 0;
	private final String itemKey = "A";
	private final String configId = "S1";


	@SuppressWarnings("boxing")
	@Before
	public void init() throws CommerceCartModificationException, CommerceCartRestorationException
	{

		final PriceData totalPrice = new PriceData();
		sessionCartBackendDown.setTotalPrice(totalPrice);
		newEntry.setEntryNumber(new Integer((int) entryNumber));
		newEntry.setHandle(itemKey);
		newEntry.setQuantity(quantity);
		final List<OrderEntryData> entries = new ArrayList<OrderEntryData>();
		entries.add(newEntry);
		sessionCart.setEntries(entries);
		sessionCartBackendDown.setEntries(entries);
		cartModificationData.setEntry(newEntry);
		cartModificationDataBackendDown.setEntry(newEntry);

		configurationProviderFactory = EasyMock.createMock(ConfigurationProviderFactory.class);
		configurationProvider = EasyMock.createMock(ConfigurationProvider.class);
		productConfigurationService = EasyMock.createMock(ProductConfigurationService.class);
		EasyMock.expect(productConfigurationService.getGetConfigId(itemKey)).andReturn(configId);
		EasyMock.expect(configurationProviderFactory.getProvider()).andReturn(configurationProvider);
		configurationProvider.releaseSession(configId);

		cartService = EasyMock.createMock(CartService.class);
		classUnderTest.setCartService(cartService);
		EasyMock.expect(cartService.addToCart(code, quantity)).andReturn(cartModificationData);
		EasyMock.expect(cartService.getSessionCart()).andReturn(sessionCart);
		EasyMock.expect(cartService.getSessionCart(false)).andReturn(sessionCart);
		EasyMock.expect(cartService.hasSessionCart()).andReturn(true);
		EasyMock.expect(cartService.validateCartData()).andReturn(validateCartData);
		EasyMock.expect(cartService.updateCartEntry(entryNumber, quantity)).andReturn(cartModificationData);
		cartService.removeSessionCart();

		productImageHelper = EasyMock.createMock(ProductImageHelper.class);
		classUnderTest.setProductImageHelper(productImageHelper);
		productImageHelper.enrichWithProductImages(newEntry);
		productImageHelper.enrichWithProductImages(sessionCart);

		userService = EasyMock.createMock(UserService.class);
		EasyMock.expect(userService.getCurrentUser()).andReturn(userModel);
		EasyMock.expect(userService.isAnonymousUser(userModel)).andReturn(Boolean.FALSE);
		classUnderTest.setUserService(userService);

		userServiceLoggedInUser = EasyMock.createMock(UserService.class);
		EasyMock.expect(userServiceLoggedInUser.getCurrentUser()).andReturn(userModel);
		EasyMock.expect(userServiceLoggedInUser.isAnonymousUser(userModel)).andReturn(Boolean.FALSE);

		standardFacade = EasyMock.createMock(CartFacade.class);
		EasyMock.expect(standardFacade.addToCart(code, quantity)).andReturn(cartModificationDataBackendDown);
		EasyMock.expect(standardFacade.updateCartEntry(entryNumber, quantity)).andReturn(cartModificationDataBackendDown);
		EasyMock.expect(standardFacade.getSessionCart()).andReturn(sessionCartBackendDown);
		EasyMock.expect(standardFacade.hasSessionCart()).andReturn(false);
		EasyMock.expect(standardFacade.restoreSavedCart(code)).andReturn(cartRestorationData);
		classUnderTest.setStandardFacade(standardFacade);
		classUnderTest.setConfigurationProviderFactory(configurationProviderFactory);
		classUnderTest.setProductConfigurationService(productConfigurationService);

		EasyMock.replay(cartService, productImageHelper, userService, userServiceLoggedInUser, standardFacade,
				configurationProviderFactory, configurationProvider, productConfigurationService);

		classUnderTest.setBackendAvailabilityService(backendAvailable);
		setBackendDown(false);
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{

		assertEquals(cartModificationData, classUnderTest.addToCart(code, quantity));
	}

	@Test
	public void testAddToCartWithStore() throws CommerceCartModificationException
	{
		assertEquals(cartModificationData, classUnderTest.addToCart(code, quantity, storeId));
	}

	@Test
	public void testAttribs()
	{
		//assertEquals(cartService, classUnderTest.getCartService());
		assertEquals(productImageHelper, classUnderTest.getProductImageHelper());

	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testEstimateExternalTaxes()
	{
		final String deliveryZipCode = null;
		final String countryIsoCode = null;
		classUnderTest.estimateExternalTaxes(deliveryZipCode, countryIsoCode);
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testUpdateCartIdWithStore() throws CommerceCartModificationException
	{
		final long entryNumber = 1;
		classUnderTest.updateCartEntry(entryNumber, storeId);
	}



	@Test
	public void testGetSessionCart()
	{
		classUnderTest.setUserService(userServiceLoggedInUser);
		assertEquals(sessionCart, classUnderTest.getSessionCart());
	}

	@Test
	public void testGetSessionCartWOrdering()
	{
		classUnderTest.setUserService(userServiceLoggedInUser);
		assertEquals(sessionCart, classUnderTest.getSessionCartWithEntryOrdering(false));
	}

	@Test
	public void testGetMiniCart()
	{
		classUnderTest.setUserService(userServiceLoggedInUser);
		assertEquals(sessionCart, classUnderTest.getMiniCart());
	}

	@Test
	public void testGetCartsForCurrentUserNoLogin()
	{
		assertTrue(classUnderTest.getCartsForCurrentUser().size() == 1);
	}

	@Test
	public void testGetDeliveryCountries()
	{
		assertNull(classUnderTest.getDeliveryCountries());
	}

	@Test
	public void testHasSessionCart()
	{
		assertTrue(classUnderTest.hasSessionCart());
	}

	@Test
	public void testHasSessionCartWithBackendDown()
	{
		setBackendDown(true);
		assertFalse(classUnderTest.hasSessionCart());
	}

	private void setBackendDown(final boolean b)
	{
		this.backendAvailable.setBackendDown(b);
	}

	@Test
	public void testRemoveSessionCart()
	{
		classUnderTest.removeSessionCart();
	}

	@Test
	public void testRestoreAnonymousCartAndTakeOwnership() throws CommerceCartRestorationException
	{
		assertNull(classUnderTest.restoreAnonymousCartAndTakeOwnership(null));
	}

	@Test
	public void testRestoreSavedCart() throws CommerceCartRestorationException
	{
		assertNull(classUnderTest.restoreSavedCart(code));
	}

	@Test
	public void testRestoreSavedCartWithBackendDown() throws CommerceCartRestorationException
	{
		setBackendDown(true);
		assertNotNull(classUnderTest.restoreSavedCart(code));
	}

	@Test
	public void testValidate() throws CommerceCartModificationException
	{
		assertEquals(validateCartData, classUnderTest.validateCartData());
	}

	@Test
	public void testUpdate() throws CommerceCartModificationException
	{
		assertEquals(cartModificationData, classUnderTest.updateCartEntry(entryNumber, quantity));
	}

	@Test
	public void testStandardFacade()
	{
		assertEquals(standardFacade, classUnderTest.getStandardFacade());
	}

	@Test
	public void testIsBackendDown()
	{
		setBackendDown(true);

		assertTrue(classUnderTest.isBackendDown());
	}

	@Test
	public void testAddToCartWithBackendDown() throws CommerceCartModificationException
	{
		setBackendDown(true);

		assertEquals(cartModificationDataBackendDown, classUnderTest.addToCart(code, quantity));
	}

	@Test
	public void testUpdateWithBackendDown() throws CommerceCartModificationException
	{
		setBackendDown(true);

		assertEquals(cartModificationDataBackendDown, classUnderTest.updateCartEntry(entryNumber, quantity));
	}

	@Test
	public void testUpdateOrderEntry() throws EntityValidationException, CommerceCartModificationException
	{
		assertEquals(cartModificationData, classUnderTest.updateOrderEntry(newEntry));
	}

	@Test
	public void testUpdateOrderEntryWithBackendDown() throws EntityValidationException, CommerceCartModificationException
	{
		setBackendDown(true);

		assertEquals(cartModificationDataBackendDown, classUnderTest.updateOrderEntry(newEntry));
	}

	@Test
	public void testGetSessionCartWithBackendDown()
	{
		setBackendDown(true);

		classUnderTest.setUserService(userServiceLoggedInUser);
		assertEquals(sessionCartBackendDown, classUnderTest.getSessionCart());
	}

	@Test
	public void testConfigurationProvider()
	{
		assertNotNull(classUnderTest.getConfigurationProvider());
	}

	@Test
	public void testConfigurationProviderFactory()
	{
		assertNotNull(classUnderTest.getConfigurationProviderFactory());
	}

	@Test
	public void testConfigurationService()
	{
		assertNotNull(classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testCheckForConfigurationRelease()
	{
		classUnderTest.checkForConfigurationRelease(quantityOne, itemKey);
		classUnderTest.checkForConfigurationRelease(quantityZero, itemKey);
	}

	@Test
	public void testGetItemKey()
	{
		assertEquals(this.itemKey, classUnderTest.getItemKey(entryNumber));
	}
}
