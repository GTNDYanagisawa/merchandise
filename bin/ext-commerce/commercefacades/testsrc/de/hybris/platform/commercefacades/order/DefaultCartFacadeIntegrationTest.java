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
package de.hybris.platform.commercefacades.order;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


@IntegrationTest
public class DefaultCartFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_BASESTORE_UID = "testStore";

	@Resource
	private CartFacade cartFacade;
	@Resource
	private CartService cartService;
	@Resource
	private ModelService modelService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;
	@Resource
	private GuidKeyGenerator guidKeyGenerator;
	@Resource
	private BaseStoreService baseStoreService;

	private CurrencyModel currency;


	@BeforeClass
	public static void tenantStuff()
	{
		Registry.setCurrentTenantByID("junit");
	}

	@Before
	public void setUp() throws ImpExException
	{

		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		currency = commonI18NService.getCurrency("EUR");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);

		final UserModel user = createUser("user");

		setupEmptyCart(user);
		userService.setCurrentUser(user);
	}

	private void setupEmptyCart(final UserModel user)
	{
		final CartModel cartModel = modelService.create(CartModel.class);
		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(TEST_BASESTORE_UID);
		final BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();

		baseStore.setExternalTaxEnabled(Boolean.FALSE);
		baseStore.setUid(TEST_BASESTORE_UID);
		cartModel.setStore(baseStore);
		cartModel.setCurrency(currency);
		cartModel.setDate(new Date());
		cartModel.setNet(Boolean.TRUE);
		cartModel.setUser(user);
		cartModel.setGuid(guidKeyGenerator.generate().toString());
		cartModel.setSite(baseSite);
		modelService.save(cartModel);
		cartService.setSessionCart(cartModel);
		userService.setCurrentUser(user);
	}

	private UserModel createUser(final String uid)
	{
		final CustomerModel user = modelService.create(CustomerModel.class);
		user.setUid(uid);
		modelService.save(user);
		return user;
	}

	@Test
	public void testGetSessionCart()
	{
		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull(cart);
	}

	@Test
	public void testGetSessionCartNull()
	{
		cartService.removeSessionCart();
		commonI18NService.setCurrentCurrency(currency);
		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull(cart);
	}

	@Test
	public void testHasSessionCartTrue()
	{
		final boolean hasCart = cartFacade.hasSessionCart();
		Assert.assertTrue(hasCart);
	}

	@Test
	public void testHasSessionCartFalse()
	{
		cartService.removeSessionCart();
		final boolean hasCart = cartFacade.hasSessionCart();
		Assert.assertTrue(!hasCart);
	}

	@Test
	public void testGetMiniCart()
	{
		final CartData cart = cartFacade.getMiniCart();
		Assert.assertNotNull(cart);
	}

	@Test
	public void testGetMiniCartEmpty()
	{
		cartService.removeSessionCart();
		commonI18NService.setCurrentCurrency(currency);
		final CartData cart = cartFacade.getMiniCart();
		Assert.assertNotNull(cart);
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{
		cartFacade.addToCart("HW1210-3423", 1);
		final CartModel cart = cartService.getSessionCart();
		Assert.assertEquals("HW1210-3423", cart.getEntries().iterator().next().getProduct().getCode());
	}

	@Test
	public void testAddToCartLowerStock() throws CommerceCartModificationException
	{
		final long actualAdded = cartFacade.addToCart("HW1210-3423", 12).getQuantityAdded();
		final CartModel cart = cartService.getSessionCart();
		Assert.assertEquals(10, actualAdded);
		Assert.assertEquals("HW1210-3423", cart.getEntries().iterator().next().getProduct().getCode());
	}

	@Test
	public void testAnonymousCartRestoration() throws CommerceCartModificationException, CommerceCartRestorationException
	{
		final CustomerModel anonymous = userService.getAnonymousUser();


		//set current user as built in anonymous user
		setupEmptyCart(anonymous);

		final String productCode = "HW1210-3423";
		final long actualAdded = cartFacade.addToCart(productCode, 10).getQuantityAdded();
		final String anonymousCartGuid = cartService.getSessionCart().getGuid();

		Assert.assertEquals(10, actualAdded);
		Assert.assertEquals(anonymous, cartService.getSessionCart().getUser());

		final UserModel regularUser = createUser("regularUser");

		//set current user as new regular user
		//first setup a new cart, otherwise calling setCurrentUser would replace user in current cart
		setupEmptyCart(regularUser);

		final String regularUsersCartGuid = cartService.getSessionCart().getGuid();
		Assert.assertNotSame(anonymousCartGuid, regularUsersCartGuid);

		//try to restore previously created cart and set its user as currently operating regular user
		cartFacade.restoreAnonymousCartAndTakeOwnership(anonymousCartGuid);
		Assert.assertEquals(regularUser, cartService.getSessionCart().getUser());

		final AbstractOrderEntryModel aoem = cartService.getSessionCart().getEntries().get(0);
		Assert.assertEquals(10, aoem.getQuantity().longValue());
		Assert.assertEquals(productCode, aoem.getProduct().getCode());
	}

	@Test
	public void testAnonymousCartRestorationOfNonAnonymousCartShouldFail() throws CommerceCartModificationException,
			CommerceCartRestorationException
	{
		final UserModel regularUser1 = createUser("regularUser1");
		final UserModel regularUser2 = createUser("regularUser2");

		//set current user as built in anonymous user
		setupEmptyCart(regularUser1);
		userService.setCurrentUser(regularUser1);

		final long actualAdded = cartFacade.addToCart("HW1210-3423", 10).getQuantityAdded();
		final String regularUser1CartGuid = cartService.getSessionCart().getGuid();

		Assert.assertEquals(10, actualAdded);
		Assert.assertEquals(regularUser1, cartService.getSessionCart().getUser());

		//set current user as new regular user
		//first setup a new cart, otherwise calling setCurrentUser would replace user in current cart
		setupEmptyCart(regularUser2);
		userService.setCurrentUser(regularUser2);

		//try to restore previously created cart, should throw an exception
		try
		{
			cartFacade.restoreAnonymousCartAndTakeOwnership(regularUser1CartGuid);
			Assert.fail();
		}
		catch (final CommerceCartRestorationException ccre)
		{
			Assert.assertTrue(ccre.getMessage().contains("not found"));
		}
	}
}
