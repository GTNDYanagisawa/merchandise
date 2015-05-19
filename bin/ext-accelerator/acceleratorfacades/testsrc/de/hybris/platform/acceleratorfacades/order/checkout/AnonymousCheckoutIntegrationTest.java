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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;


@ContextConfiguration(locations =
{ "classpath:/payment-spring-test.xml" })
@IntegrationTest
public class AnonymousCheckoutIntegrationTest extends BaseCommerceBaseTest
{

	private static final Logger LOG = Logger.getLogger(AnonymousCheckoutIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";
	private static final double EPS = 0.001;

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
	private UserFacade userFacade;
	@Resource(name = "acceleratorCheckoutFacade")
	private DefaultAcceleratorCheckoutFacade checkoutFacade;
	@Resource
	private CustomerAccountService customerAccountService; //NOPMD
	@Resource
	private DefaultCheckoutCustomerStrategy checkoutCustomerStrategy;
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

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception
	{
		//Registry.activateMasterTenant();
		LOG.info("Creating data for Anonymous Checkout Integration Test ..");
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/acceleratorfacades/test/testAnonymousCheckout.impex", "utf-8");
		final BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
		baseSiteService.setCurrentBaseSite(baseSiteForUID, false);

		LOG.info("Finished creating data for Anonymous Checkout Integration Test in " + (System.currentTimeMillis() - startTime)
				+ "ms");

		cardPaymentService.setCommandFactoryRegistry(mockupCommandFactoryRegistry);
		paymentService.setCardPaymentService(cardPaymentService);
		commerceCheckoutService.setPaymentService(paymentService);

		LOG.info("Add items to cart...");
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		assertNotNull(catalogVersionModel);
		catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));

		Config.setParameter("paymentinfo.creditcard.checknumber", "false");
	}

	@Test
	public void testAnonymousCheckout() throws Exception
	{
		addItemsToCartBeforeCheckout();

		final String guestEmail = UUID.randomUUID() + "test@test.com";

		LOG.info("Checkout as GUEST... ");
		try
		{
			customerFacade.createGuestUserForAnonymousCheckout(guestEmail, "Guest");
		}
		catch (final DuplicateUidException e)
		{
			fail(e.getMessage());
		}

		LOG.info("Verify current hybris user is anonymous...");

		assertEquals(userService.getAnonymousUser(), userService.getCurrentUser());
		assertTrue(checkoutCustomerStrategy.isAnonymousCheckout());
		assertTrue(CustomerType.GUEST.equals(checkoutCustomerStrategy.getCurrentUserForCheckout().getType()));
		assertTrue(checkoutCustomerStrategy.getCurrentUserForCheckout().getUid().contains(guestEmail));

		final OrderData order = completeCheckoutProcess();

		LOG.info("Guest enters password to create account...");
		customerFacade.changeGuestToCustomer("password", order.getGuid());

		LOG.info("Load customer and verify data...");
		final UserModel user = userService.getCurrentUser();
		LOG.info("Verify UID contains only provided email...");
		assertEquals(user.getUid(), guestEmail);

		LOG.info("Verify addresses. Note: only shipping address is saved...");
		final Collection<AddressModel> addresses = user.getAddresses();
		assertNotNull(addresses);
		assertEquals(1, addresses.size());
		final AddressModel shipAddress = addresses.iterator().next();
		verifyAddress(shipAddress, buildDeliveryAddress());

		final Collection<PaymentInfoModel> paymentInfoModels = user.getPaymentInfos();
		assertNotNull(paymentInfoModels);
		assertEquals(1, paymentInfoModels.size());
		final PaymentInfoModel paymentInfoModel = paymentInfoModels.iterator().next();
		verifyAddress(paymentInfoModel.getBillingAddress(), buildDeliveryAddress());

		LOG.info("Verify user has 1 order...");
		final Collection<OrderModel> orders = user.getOrders();
		assertEquals(1, orders.size());
		final OrderModel orderInUserProfile = orders.iterator().next();
		assertEquals(250.00, orderInUserProfile.getTotalPrice().doubleValue(), EPS);
	}


	protected void addItemsToCartBeforeCheckout()
	{
		final ProductModel product1 = productService.getProductForCode(catalogVersionService.getSessionCatalogVersions().iterator()
				.next(), "HW1210-3422");
		assertNotNull(product1);
		final ProductModel product2 = productService.getProductForCode(catalogVersionService.getSessionCatalogVersions().iterator()
				.next(), "HW1210-3423");
		assertNotNull(product2);

		LOG.info("Add 1 item of product 1, unit cost 50 EUR...");
		try
		{
			cartFacade.addToCart(product1.getCode(), 1);
		}
		catch (final CommerceCartModificationException e2)
		{
			fail();
		}

		LOG.info("Add 2 items of product 2, unit cost 100 EUR...");

		try
		{
			cartFacade.addToCart(product2.getCode(), 2);
		}
		catch (final CommerceCartModificationException e2)
		{
			fail();
		}

		LOG.info("Verify cart size...");
		assertEquals(2, cartFacade.getSessionCart().getEntries().size());

		LOG.info("Verify products in cart...");
		final Collection<String> productCodes = Collections2.transform(cartFacade.getSessionCart().getEntries(),
				new Function<OrderEntryData, String>()
				{
					@Override
					public String apply(final OrderEntryData entry)
					{
						return entry.getProduct().getCode();
					}
				});
		assertTrue(productCodes.contains("HW1210-3422"));
		assertTrue(productCodes.contains("HW1210-3423"));
		assertEquals(productCodes.size(), 2);

		LOG.info("Verify cart total price...");
		assertEquals(250, cartFacade.getSessionCart().getTotalPrice().getValue().doubleValue(), EPS);
		assertEquals("EUR", cartFacade.getSessionCart().getTotalPrice().getCurrencyIso());
	}

	protected OrderData completeCheckoutProcess() throws CommerceCartModificationException, InvalidCartException
	{

		LOG.info("Set delivery address...");
		final AddressData deliveryAddress = buildDeliveryAddress();
		checkoutFacade.setDeliveryAddress(deliveryAddress);
		userFacade.addAddress(deliveryAddress);

		LOG.info("Set payment info...");
		final CCPaymentInfoData paymentInfo = buildPaymentInfo(deliveryAddress);
		final CCPaymentInfoData newPaymentSubscription = checkoutFacade.createPaymentSubscription(paymentInfo);
		checkoutFacade.setPaymentDetails(newPaymentSubscription.getId());

		final OrderData order = checkoutFacade.placeOrder();

		LOG.info("Order submited successfully...");

		return order;

	}


	protected void verifyAddress(final AddressModel addressInUserProfile, final AddressData originalAddress)
	{
		assertEquals(addressInUserProfile.getFirstname(), originalAddress.getFirstName());
		assertEquals(addressInUserProfile.getLastname(), originalAddress.getLastName());
		assertEquals(addressInUserProfile.getLine1(), originalAddress.getLine1());
		assertEquals(addressInUserProfile.getPostalcode(), originalAddress.getPostalCode());
		assertEquals(addressInUserProfile.getTown(), originalAddress.getTown());
		assertEquals(addressInUserProfile.getCountry().getIsocode(), originalAddress.getCountry().getIsocode());
	}


	protected AddressData buildDeliveryAddress()
	{
		final AddressData address = new AddressData();
		address.setId("12345");
		address.setFirstName("First");
		address.setLastName("Last");
		address.setLine1("123 ABC St");
		address.setPostalCode("12345");
		address.setTown("New York");
		final CountryData countryData = new CountryData();
		countryData.setIsocode("US");
		address.setCountry(countryData);
		address.setBillingAddress(true);
		address.setShippingAddress(true);
		address.setDefaultAddress(true);

		return address;
	}

	protected CCPaymentInfoData buildPaymentInfo(final AddressData billingAddress)
	{
		final CCPaymentInfoData paymentInfo = new CCPaymentInfoData();
		paymentInfo.setAccountHolderName("First Last");
		paymentInfo.setBillingAddress(billingAddress);
		paymentInfo.setCardNumber("4111111111111111");
		paymentInfo.setCardType("visa");
		paymentInfo.setExpiryMonth("1");
		paymentInfo.setExpiryYear("2017");
		paymentInfo.setSubscriptionId("123");

		return paymentInfo;
	}
}
