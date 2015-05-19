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
package de.hybris.platform.acceleratorfacades.order.checkout;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


@ContextConfiguration(locations =
{ "classpath:/payment-spring-test.xml" })
@IntegrationTest
public class DefaultExpressCheckoutIntegrationCheckoutTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultExpressCheckoutIntegrationCheckoutTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private CommerceCartService commerceCartService; //NOPMD
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService; //NOPMD
	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private BaseStoreService baseStoreService; //NOPMD
	@Resource
	private CartFacade cartFacade;
	@Resource
	private OrderFacade orderFacade; //NOPMD
	@Resource
	private CustomerFacade customerFacade;
	@Resource
	private UserFacade userFacade; //NOPMD
	@Resource
	private CustomerAccountService customerAccountService; //NOPMD
	@Resource
	private DefaultCheckoutCustomerStrategy checkoutCustomerStrategy;//NOPMD
	@Resource
	private ConfigurationService configurationService; //NOPMD
	@Resource
	private CommandFactoryRegistry mockupCommandFactoryRegistry;
	@Resource
	private DefaultCardPaymentServiceImpl cardPaymentService;
	@Resource
	private DefaultPaymentServiceImpl paymentService;
	@Resource
	private DefaultCommerceCheckoutService commerceCheckoutService;
	@Resource
	private DefaultAcceleratorCheckoutFacade acceleratorCheckoutFacade;
	@Resource
	private DeliveryService deliveryService;//NOPMD

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception
	{
		LOG.info("Creating data for Express Checkout Integration Test ..");
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/acceleratorfacades/test/testExpressCheckout.impex", "utf-8");
		final BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
		baseSiteService.setCurrentBaseSite(baseSiteForUID, false);

		LOG.info("Finished creating data for Express Checkout Integration Test in " + (System.currentTimeMillis() - startTime)
				+ "ms");

		cardPaymentService.setCommandFactoryRegistry(mockupCommandFactoryRegistry);
		paymentService.setCardPaymentService(cardPaymentService);
		commerceCheckoutService.setPaymentService(paymentService);

		LOG.info("Add items to cart...");
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));
		Config.setParameter("paymentinfo.creditcard.checknumber", "false");
	}


	@Test
	public void testExpressCheckoutForShippingCart() throws Exception
	{
		final CustomerModel customerModel = (CustomerModel) userService.getUserForUID("expressuser@expresscheckout.com");
		userService.setCurrentUser(customerModel);
		assertEquals(customerModel.getPaymentInfos().size(), 1);
		customerModel.setDefaultPaymentInfo(customerModel.getPaymentInfos().iterator().next());

		final CartModel cartModel = cartService.getSessionCart();
		final CustomerModel customer = (CustomerModel) cartModel.getUser();
		addItemsToCartBeforeCheckout();
		customerFacade.loginSuccess();
		assertEquals(cartModel.getUser().getUid(), "expressuser@expresscheckout.com");

		LOG.info("Start checking out as express customer... ");
		assertEquals(acceleratorCheckoutFacade.performExpressCheckout(), AcceleratorCheckoutFacade.ExpressCheckoutResult.SUCCESS);

		assertEquals(cartModel.getDeliveryMode().getCode(), "standard-gross");
		assertEquals(cartModel.getPaymentInfo(), customer.getDefaultPaymentInfo());
		assertEquals(cartModel.getDeliveryAddress(), customer.getDefaultShipmentAddress());
		assertNotNull(acceleratorCheckoutFacade.placeOrder());
	}

	@Test
	public void testExpressCheckoutForPickupOnlyCart() throws Exception
	{
		final CustomerModel customerModel = (CustomerModel) userService.getUserForUID("pickupuser@expresscheckout.com");
		userService.setCurrentUser(customerModel);
		customerModel.setDefaultPaymentInfo(customerModel.getPaymentInfos().iterator().next());
		assertEquals(customerModel.getPaymentInfos().size(), 1);
		assertEquals(customerModel.getDefaultShipmentAddress(), null);

		final CartModel cartModel = cartService.getSessionCart();
		final CustomerModel customer = (CustomerModel) cartModel.getUser();
		addPickupItemsToCartBeforeCheckout();
		customerFacade.loginSuccess();
		assertEquals(cartModel.getUser().getUid(), "pickupuser@expresscheckout.com");

		LOG.info("Start checking out as express customer... ");
		assertEquals(acceleratorCheckoutFacade.performExpressCheckout(), AcceleratorCheckoutFacade.ExpressCheckoutResult.SUCCESS);

		assertEquals(cartModel.getPaymentInfo(), customer.getDefaultPaymentInfo());
		assertNotNull(acceleratorCheckoutFacade.placeOrder());
	}

	protected void addItemsToCartBeforeCheckout()
	{
		final ProductModel product2 = productService.getProductForCode(catalogVersionService.getSessionCatalogVersions().iterator()
				.next(), "HW1210-3423");
		assertNotNull(product2);
		try
		{
			cartFacade.addToCart(product2.getCode(), 2);
		}
		catch (final CommerceCartModificationException e2)
		{
			fail();
		}
		LOG.info("Verify cart size...");
		assertEquals(1, cartFacade.getSessionCart().getEntries().size());
	}

	protected void addPickupItemsToCartBeforeCheckout()
	{

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartService.getSessionCart());

		commerceCartService.removeAllEntries(parameter);
		final ProductModel product = productService.getProductForCode(catalogVersionService.getSessionCatalogVersions().iterator()
				.next(), "HW1210-3424");
		assertNotNull(product);
		try
		{
			cartFacade.addToCart(product.getCode(), 2, "Yokosuka");
		}
		catch (final CommerceCartModificationException e2)
		{
			fail();
		}
		LOG.info("Verify cart size...");
		assertEquals(1, cartFacade.getSessionCart().getEntries().size());
	}


}
