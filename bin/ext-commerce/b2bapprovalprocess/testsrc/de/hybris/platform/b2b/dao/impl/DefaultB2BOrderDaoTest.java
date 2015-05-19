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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTransactionalTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import java.util.Date;
import javax.annotation.Resource;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class DefaultB2BOrderDaoTest extends B2BIntegrationTransactionalTest
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BOrderDaoTest.class);
	@Resource
	public DefaultB2BOrderDao b2bOrderDao;

	@Before
	public void setup() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
	}

	@Test
	public void shouldFindOrdersApprovedForDateRange() throws Exception
	{
		final String userId = "IC CEO";
		final B2BCustomerModel user = login(userId);
		final Date currentDate = new Date(System.currentTimeMillis());
		LOG.debug("current date: " + currentDate);
		for (int i = 1; i < 11; i++)
		{
			//TODO: refactor to use  this.createOrder(user,1,OrderStatus.CREATED );
			final OrderModel order = (OrderModel) modelService.create(OrderModel.class);
			order.setCode(orderCodeGenerator.generate().toString());
			order.setUser(user);
			order.setUnit(b2bUnitService.getParent(user));
			order.setNet(Boolean.TRUE);
			order.setCurrency(commonI18NService.getCurrency("USD"));
			order.setStatus(OrderStatus.APPROVED);
			order.setDate(DateUtils.addDays(currentDate, i));
			modelService.save(order);
			modelService.refresh(order);
			LOG.debug("Created order for date: " + order.getDate());
		}


		Assert.assertEquals(10, b2bOrderDao.findOrdersApprovedByDateRange(user, currentDate, DateUtils.addDays(currentDate, 10))
				.size());
		Assert.assertEquals(5, b2bOrderDao.findOrdersApprovedByDateRange(user, currentDate, DateUtils.addDays(currentDate, 5))
				.size());

	}

	@Test
	public void shouldFindOrderByStatus() throws Exception
	{
		final B2BCustomerModel user = login("IC CEO");
		final OrderModel order = this.createOrder(user, 1, OrderStatus.CREATED, productService.getProductForCode("testProduct0"));
		Assert.assertEquals(
				String.format("Users of order and session did not match %s!=%s", user.getUid(), order.getUser().getUid()), user,
				order.getUser());
		Assert.assertTrue(String.format("Expected to find order by code %s and status %s", order.getCode(), OrderStatus.APPROVED),
				b2bOrderDao.findOrdersByStatus(user, OrderStatus.CREATED).iterator().hasNext());
		Assert.assertEquals(order.getCode(), b2bOrderDao.findOrdersByStatus(user, OrderStatus.CREATED).iterator().next().getCode());
	}

}
