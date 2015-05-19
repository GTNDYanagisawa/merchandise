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
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.orderscheduling.ScheduleOrderService;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.JobPerformable;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BReplenishmentTest extends B2BIntegrationTransactionalTest
{

	@Resource
	private ScheduleOrderService scheduleOrderService;

	@Resource
	private FlexibleSearchService flexibleSearchService;


	@Resource
	private CronJobService cronJobService;

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
		createJobPerformables();
	}

	@Before
	public void beforeTest() throws Exception
	{
		final Map<String, JobPerformable> beans = Registry.getApplicationContext().getBeansOfType(JobPerformable.class);
		System.out.println("beans beforeTest " + beans.size());
		for (final Map.Entry<String, JobPerformable> entry : beans.entrySet())
		{
			if (flexibleSearchService
					.search(
							"SELECT {" + ServicelayerJobModel.PK + "} FROM {" + ServicelayerJobModel._TYPECODE + "} WHERE {"
									+ ServicelayerJobModel.SPRINGID + "}=?springid", Collections.singletonMap("springid", entry.getKey()))
					.getResult().isEmpty())
			{
				final ServicelayerJobModel servicelayerJobModel = modelService.create(ServicelayerJobModel.class);
				servicelayerJobModel.setCode(entry.getKey());
				servicelayerJobModel.setSpringId(entry.getKey());
				modelService.save(servicelayerJobModel);
			}
		}

		//HACK for BTOBA-34
		if (flexibleSearchService
				.search(
						"SELECT {" + ServicelayerJobModel.PK + "} FROM {" + ServicelayerJobModel._TYPECODE + "} WHERE {"
								+ ServicelayerJobModel.SPRINGID + "}=?springid", Collections.singletonMap("springid", "cartToOrderJob"))
				.getResult().isEmpty())
		{
			final ServicelayerJobModel servicelayerJobModel = modelService.create(ServicelayerJobModel.class);
			servicelayerJobModel.setCode("cartToOrderJob");
			servicelayerJobModel.setSpringId("cartToOrderJob");
			modelService.save(servicelayerJobModel);
		}

	}

	@Test
	public void testReplenishment() throws InvalidCartException, InterruptedException
	{
		final String userId = "IC CEO";
		final UserModel user = login(userId);
		//1. Create the Cart
		final CartModel sessionCart = b2bCartService.getSessionCart();
		Assert.assertNotNull(sessionCart);
		final CartEntryModel cartEntryModel = b2bCartService.addNewEntry(sessionCart,
				productService.getProductForCode("b2bproduct"), 1, null);
		modelService.save(cartEntryModel);
		modelService.save(sessionCart);
		b2bCartService.setSessionCart(sessionCart);

		//2. Create the address
		final AddressModel address = this.modelService.create(AddressModel.class);
		address.setFirstname("Krish");
		address.setLastname("Dey");
		address.setStreetname("Broadway");
		address.setRegion(commonI18NService.getRegion(commonI18NService.getCountry("US"), "NY"));
		address.setStreetnumber("53");
		address.setOwner(user);

		//3. Create the Trigger Model
		final TriggerModel trigger = modelService.create(TriggerModel.class);
		trigger.setSecond(Integer.valueOf(1));
		trigger.setMinute(Integer.valueOf(-1));
		trigger.setHour(Integer.valueOf(-1));
		trigger.setDay(Integer.valueOf(-1));
		trigger.setMonth(Integer.valueOf(-1));
		trigger.setRelative(Boolean.TRUE);
		trigger.setActivationTime(new Date());

		//4.Schedule the currentCart that is in the session
		final CartToOrderCronJobModel cronJob = this.scheduleOrderFromCart(address, trigger);
		//5. find the scheduled job for the order scheduled above
		List<CartToOrderCronJobModel> scheduledOrders = b2bOrderService.getScheduledOrders(user);
		// test that the currentCart was scheduled
		Assert.assertTrue(scheduledOrders.iterator().hasNext());
		final CartModel scheduledCart = scheduledOrders.get(0).getCart();
		final String cartCode = scheduledCart.getCode();

		//replecate currentCart the currentCart from order by cartCode and set in current session
		this.createAndSetNewCartFromOrder(cartCode);
		// See if the order is still there, as in case of b2bstore it's getting removed after replicate order.
		Assert.assertNotNull("The order is expected to exist", b2bOrderService.getAbstractOrderForCode(cartCode));

		final CartModel currentCart = b2bCartService.getSessionCart();
		// test the the schedule cart is not equal to the new cart


		b2bOrderService.createOrderFromCart(currentCart);
		b2bCartService.removeSessionCart();

		//Nullify the reference here, we will not use this anymore further
		//rather the schedule order will be fetched fresh from storage
		scheduledOrders = null;


		//See if Cronjob is getting the currentCart as it was getting the currentCart null in b2bstore.
		final CronJobModel cornJobFromDB = cronJobService.getCronJob(cronJob.getCode());
		Assert.assertNotNull(((CartToOrderCronJobModel) cornJobFromDB).getCart());


		//Check if the schedule order is still there, as after replicating the order is getting deleted
		Assert.assertTrue(b2bOrderService.getScheduledOrders(user).iterator().hasNext());
		// make sure that the currentCart assigned to the cronjob are the same as return by the schedule method.
		Assert.assertEquals(cronJob.getCart().getCode(), b2bOrderService.getScheduledOrders(user).iterator().next().getCart()
				.getCode());
	}

	private void createAndSetNewCartFromOrder(final String orderCode) throws InvalidCartException
	{
		final AbstractOrderModel order = b2bOrderService.getAbstractOrderForCode(orderCode);
		Assert.assertNotNull("The order cannot be null", order);
		final CartModel cart = b2bCartService.createCartFromAbstractOrder(order);
		cart.setStatus(OrderStatus.CREATED);
		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			this.modelService.save(entry);
		}

		//cart.setCartToOrderCronJob(null);
		this.modelService.save(cart);
		b2bCartService.setSessionCart(cart);
	}


	private CartToOrderCronJobModel scheduleOrderFromCart(final AddressModel delivery, final TriggerModel trigger)
			throws InvalidCartException
	{
		// create a clone of the session cart because the original one gets removed and scheduling api is not able to
		// schedule it.
		final CartModel clone = b2bCartService.createCartFromAbstractOrder(b2bCartService.getSessionCart());
		// persist the clone before scheduling it.
		this.modelService.save(clone);

		// remove the current cart  before scheduling
		b2bCartService.removeSessionCart();

		// pass in the cloned cart to the cronjob because original cart is removed here
		return scheduleOrderService.createOrderFromCartCronJob(clone, delivery, null, null, Collections.singletonList(trigger));
	}
}
