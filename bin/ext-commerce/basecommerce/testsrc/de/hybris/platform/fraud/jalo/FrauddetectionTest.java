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
package de.hybris.platform.fraud.jalo;

import static de.hybris.platform.fraud.strategy.AbstractOrderFraudSymptomDetection.DEFAULT_INCREMENT;

import de.hybris.platform.basecommerce.enums.IntervalResolution;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.fraud.FraudService;
import de.hybris.platform.fraud.FraudServiceProvider;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.fraud.model.ProductOrderLimitModel;
import de.hybris.platform.fraud.symptom.impl.BlackListSymptom;
import de.hybris.platform.fraud.symptom.impl.FirstTimeOrderSymptom;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * JUnit Tests for the Frauddetection extension
 */
public class FrauddetectionTest extends ServicelayerTransactionalTest
{

	@Resource
	private FraudService fraudService;

	@Resource
	private FraudServiceProvider internalFraudServiceProvider;

	@Resource
	private ModelService modelService;

	@Resource
	private OrderService orderService;

	@Resource
	private CartService cartService;

	@Resource
	private ProductService productService;

	@Resource
	private UserService userService;

	@Resource
	private BlackListSymptom blackListSymptom;

	@Resource
	private FirstTimeOrderSymptom firstTimeOrderSymptom;

	private ProductModel product;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		final ProductOrderLimitModel productOrderLimmit = modelService.create(ProductOrderLimitModel.class);
		productOrderLimmit.setCode("testLimit");
		//set product limitations: 
		//only one order containing entries of this product can be placed per one day
		productOrderLimmit.setIntervalResolution(IntervalResolution.DAY);
		productOrderLimmit.setIntervalValue(Integer.valueOf(1));
		productOrderLimmit.setIntervalMaxOrdersNumber(Integer.valueOf(1));
		//only 3 items of this product can be ordered per order
		productOrderLimmit.setMaxNumberPerOrder(Integer.valueOf(3));
		modelService.save(productOrderLimmit);

		product = productService.getProduct("testProduct1");
		product.setProductOrderLimit(productOrderLimmit);
		modelService.save(product);

		blackListSymptom.setBannedEmails(Collections.singleton("bad.guy@gmail.com"));
	}

	protected void setupUser(final String email)
	{
		final CustomerModel user = modelService.create(CustomerModel.class);
		user.setName("Test");
		user.setUid(email);
		modelService.save(user);
		userService.setCurrentUser(user);
	}

	@Test
	public void testBlackList() throws Exception
	{
		setupUser("bad.guy@gmail.com");
		final OrderModel order = placeOrder(userService.getCurrentUser(), "bad.guy@gmail.com");
		order.setTotalPrice(new Double(999));

		modelService.save(order);

		final FraudServiceResponse response = fraudService.recognizeOrderSymptoms(internalFraudServiceProvider.getProviderName(),
				order);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getSymptoms());
		final double expected = blackListSymptom.getIncrement() + firstTimeOrderSymptom.getIncrement();
		Assert.assertEquals(expected, response.getScore(), 0);
	}

	@Test
	public void testPriceyOrder() throws Exception
	{
		setupUser("just-a-guy@gmail.com");
		final OrderModel order = placeOrder(userService.getCurrentUser(), "just-a-guy@gmail.com");
		order.setTotalPrice(new Double(1001));

		modelService.save(order);

		final FraudServiceResponse response = fraudService.recognizeOrderSymptoms(internalFraudServiceProvider.getProviderName(),
				order);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getSymptoms());
		final double expected = DEFAULT_INCREMENT * 2;
		Assert.assertEquals(expected, response.getScore(), 0);
	}

	@Test
	public void testAddress() throws Exception
	{
		setupUser("test@gmail.com");
		final OrderModel order = placeOrder(userService.getCurrentUser(), "test@gmail.com", "DE", "DE");
		order.setTotalPrice(new Double(100));

		modelService.save(order);

		final FraudServiceResponse response = fraudService.recognizeOrderSymptoms(internalFraudServiceProvider.getProviderName(),
				order);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getSymptoms());
	}

	@Test
	public void testOrderEntries() throws Exception
	{
		setupUser("test@gmail.com");
		final OrderModel order = placeOrder(userService.getCurrentUser(), "test@gmail.com", null, null, product);
		final FraudServiceResponse response = fraudService.recognizeOrderSymptoms(internalFraudServiceProvider.getProviderName(),
				order);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getSymptoms());
	}

	protected OrderModel placeOrder(final UserModel user, final String email, final String country1, final String country2,
			final ProductModel product) throws Exception //NOPMD
	{
		final CartModel cart = cartService.getSessionCart();

		if (null != product)
		{
			cartService.addToCart(cart, product, 4, null);
		}

		final AddressModel deliveryAddress = modelService.create(AddressModel.class);
		deliveryAddress.setFirstname("Krzysztof");
		deliveryAddress.setLastname("Kwiatosz");
		deliveryAddress.setTown("Katowice");
		if (email != null)
		{
			deliveryAddress.setEmail(email);
		}
		deliveryAddress.setOwner(user);
		deliveryAddress.setStreetname("aaa");
		if (country1 != null)
		{
			deliveryAddress.setCountry((CountryModel) modelService.get(jaloSession.getC2LManager().getCountryByIsoCode(country1)));
		}

		final AddressModel paymentAddress = modelService.create(AddressModel.class);
		paymentAddress.setFirstname("Krzysztof");
		paymentAddress.setLastname("Kwiatosz");
		paymentAddress.setTown("Katowice");
		paymentAddress.setEmail(email);
		paymentAddress.setOwner(user);
		paymentAddress.setStreetname("bbb");
		if (country2 != null)
		{
			deliveryAddress.setCountry((CountryModel) modelService.get(jaloSession.getC2LManager().getCountryByIsoCode(country2)));
		}

		final DebitPaymentInfoModel paymentInfo = modelService.create(DebitPaymentInfoModel.class);
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("Bank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("I");
		return orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
	}

	protected OrderModel placeOrder(final UserModel user) throws Exception //NOPMD
	{
		return placeOrder(user, null, null, null, null);
	}

	protected OrderModel placeOrder(final UserModel user, final String email) throws Exception //NOPMD
	{
		return placeOrder(user, email, null, null, null);
	}

	protected OrderModel placeOrder(final UserModel user, final String email, final String country1, final String country2) //NOPMD
			throws Exception
	{
		return placeOrder(user, email, country1, country2, null);
	}
}
