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
package de.hybris.platform.b2b.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTransactionalTest;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.util.DiscountValue;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class DefaultB2BCartServiceTest extends B2BIntegrationTransactionalTest
{

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}

	@Test
	public void shouldCreateCartFromOrder() throws Exception
	{
		final OrderModel order = createOrder(1);
		order.setGlobalDiscountValues(Collections.singletonList(new DiscountValue("test", 1.0, true, "USD")));
		this.modelService.save(order);
		final CartModel cart = b2bCartService.createCartFromAbstractOrder(order);
		Assert.assertNotNull(cart);
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			Assert.assertNotNull("cost center is null", abstractOrderEntryModel.getCostCenter());
		}
		Assert.assertEquals(order.getEntries().size(), entries.size());
		Assert.assertEquals(order.getGlobalDiscountValues(), cart.getGlobalDiscountValues());

		// check that cost centers are set
	}

	@Test
	public void sholdGetSessionCart() throws Exception
	{
		final String userId = "IC CEO";
		login(userId);
		final CartModel sessionCart = b2bCartService.getSessionCart();
		Assert.assertNotNull(sessionCart.getUser());
		Assert.assertNotNull(sessionCart.getUnit());
	}


	@Test
	public void shouldCreateCartWithComment() throws Exception
	{
		final String userId = "IC CEO";
		final B2BCustomerModel user = login(userId);
		final CartModel cart = b2bCartService.getSessionCart();
		b2bCartService.addNewEntry(cart, productService.getProductForCode("b2bproduct"), 1, null);
		setDefaultCostCenterOnEntries(user, cart);
		final B2BCommentModel b2BCommentModel = modelService.create(B2BCommentModel.class);
		b2BCommentModel.setCode("QuoteRequest");
		b2BCommentModel.setComment("Requesting 5% discount.");
		b2BCommentModel.setModifiedDate(new Date());
		cart.setB2bcomments(Collections.singleton(b2BCommentModel));
		modelService.saveAll();
		Assert.assertEquals(cart.getB2bcomments().iterator().next(), Collections.singleton(b2BCommentModel).iterator().next());
		calculationService.calculate(cart);

		Assert.assertEquals(cart.getB2bcomments().iterator().next(), Collections.singleton(b2BCommentModel).iterator().next());
		final OrderModel order = b2bOrderService.createOrderFromCart(cart);
		this.modelService.save(order);
		Assert.assertFalse("Order.b2bcomments should have been cloned ", order.getB2bcomments().isEmpty());
	}

	@Test
	public void shouldCreateCartWithAddress() throws Exception
	{
		final String userId = "IC CEO";
		final B2BCustomerModel user = login(userId);
		final CartModel cart = b2bCartService.getSessionCart();
		b2bCartService.addNewEntry(cart, productService.getProductForCode("b2bproduct"), 1, null);
		setDefaultCostCenterOnEntries(user, cart);

		final AddressModel delivery = new AddressModel();
		delivery.setFirstname(user.getName());
		delivery.setLastname(user.getName());
		delivery.setStreetname("Broadway");
		delivery.setStreetnumber("53rd Street");
		delivery.setTown("NYC");
		delivery.setPostalcode("10019");
		delivery.setOwner(user);

		//Why we explicitly need to save the delivery address first?
		//https://jira.hybris.com/browse/PLA-11350
		modelService.save(delivery);

		//TODO: Find why Setting the Delivery address throwing exception while saving 
		cart.setDeliveryAddress(delivery);
		final String code = b2bOrderService.createOrderFromCart(cart).getCode();
		Assert.assertNotNull(code);

	}


	@Test
	public void shouldCreateCartWithAddress1() throws Exception
	{
		final String userId = "IC CEO";
		final B2BCustomerModel user = login(userId);
		final CartModel cart = b2bCartService.getSessionCart();
		b2bCartService.addNewEntry(cart, productService.getProductForCode("b2bproduct"), 1, null);
		setDefaultCostCenterOnEntries(user, cart);

		final AddressModel delivery = new AddressModel();
		delivery.setFirstname(user.getName());
		delivery.setLastname(user.getName());
		delivery.setStreetname("Broadway");
		delivery.setStreetnumber("53rd Street");
		delivery.setTown("NYC");
		delivery.setPostalcode("10019");
		delivery.setOwner(user);
		cart.setDeliveryAddress(delivery);
		//Using the depreciated API place Order works with the address
		final OrderModel order = b2bOrderService.placeOrder(cart, delivery, null, null);
		Assert.assertNotNull(order);

	}


}
