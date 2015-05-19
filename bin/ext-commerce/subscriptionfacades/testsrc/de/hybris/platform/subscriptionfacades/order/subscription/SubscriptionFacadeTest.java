/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.subscriptionfacades.order.subscription;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionfacades.SubscriptionFacade;
import de.hybris.platform.subscriptionfacades.data.BillingPlanData;
import de.hybris.platform.subscriptionfacades.data.BillingTimeData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionTermData;
import de.hybris.platform.subscriptionfacades.data.TermOfServiceFrequencyData;
import de.hybris.platform.subscriptionfacades.data.TermOfServiceRenewalData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceFrequency;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceRenewal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class SubscriptionFacadeTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(SubscriptionFacadeTest.class);

	@Resource
	private SubscriptionFacade subscriptionFacade;

	@Resource
	private ModelService modelService;

	OrderData order;

	@Before
	public void setUp() throws Exception
	{
		final CustomerModel customer = modelService.create(CustomerModel.class);

		final String customerId = UUID.randomUUID().toString().split("-")[0];

		customer.setUid(customerId);
		customer.setCustomerID(customerId);
		customer.setName("junit_customer_" + customerId);
		modelService.save(customer);

		jaloSession.getSessionContext().setUser((User) modelService.getSource(customer));

		order = new OrderData();
		order.setCode("order");
		order.setCreated(new Date(System.currentTimeMillis()));
		order.setGuid("21061966");

		final ProductData productData = new ProductData();
		productData.setSubscriptionTerm(this.createSubscriptionTerm());
		productData.setDescription("Description1");
		productData.setName("SubscriptionName");
		productData.setCode("subscriptionProductCode");

		productData.setPrice(new SubscriptionPricePlanData());

		final List<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();
		final OrderEntryData orderEntry = new OrderEntryData();
		orderEntry.setProduct(productData);
		orderEntries.add(orderEntry);

		order.setEntries(orderEntries);
	}

	@Test
	public void createSubscriptionTest()
	{
		try
		{
			subscriptionFacade.createSubscriptions(order, null);
			final Collection<SubscriptionData> subscriptions = subscriptionFacade.getSubscriptions();
			assertTrue(subscriptions.size() > 0);
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.error(e);
		}
	}

	private SubscriptionTermData createSubscriptionTerm()
	{
		final BillingTimeData billingTime = new BillingTimeData();
		billingTime.setCode(TermOfServiceFrequency.MONTHLY.getCode());

		final BillingPlanData billingPlan = new BillingPlanData();
		billingPlan.setBillingTime(billingTime);

		final TermOfServiceRenewalData termOfServiceRenewal = new TermOfServiceRenewalData();
		termOfServiceRenewal.setCode(TermOfServiceRenewal.AUTO_RENEWING.getCode());

		final TermOfServiceFrequencyData termOfServiceFrequency = new TermOfServiceFrequencyData();
		termOfServiceFrequency.setCode(TermOfServiceFrequency.MONTHLY.getCode());

		final SubscriptionTermData subscriptionTermData = new SubscriptionTermData();
		subscriptionTermData.setCancellable(false);
		subscriptionTermData.setBillingPlan(billingPlan);
		subscriptionTermData.setTermOfServiceNumber(12);
		subscriptionTermData.setTermOfServiceRenewal(termOfServiceRenewal);
		subscriptionTermData.setTermOfServiceFrequency(termOfServiceFrequency);

		return subscriptionTermData;
	}
}
