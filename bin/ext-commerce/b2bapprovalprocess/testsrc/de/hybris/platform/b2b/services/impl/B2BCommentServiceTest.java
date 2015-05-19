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
import de.hybris.platform.b2b.services.B2BCommentService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BCommentServiceTest extends B2BIntegrationTransactionalTest
{
	@Resource
	B2BCommentService<AbstractOrderModel> b2bCommentService;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(B2BCommentServiceTest.class);

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
	public void shouldGetCommentByUser() throws InvalidCartException, CalculationException
	{
		final OrderModel order = createOrder("GC CEO", 10, OrderStatus.CREATED);
		final UserModel user = userService.getUserForUID("GC CEO");
		Assert.assertNotNull(order);
		final B2BCommentModel comment = modelService.create(B2BCommentModel.class);
		comment.setCode("1");
		comment.setComment("Testing");
		comment.setOwner(user);
		order.setB2bcomments(Collections.singletonList(comment));
		this.modelService.saveAll();
		final List<B2BCommentModel> b2bCommentList = b2bCommentService.getB2BCommentsForUser(order, user);
		Assert.assertNotNull("Comment cannot be null", b2bCommentList.iterator().next().getComment());

	}

	@Test
	public void shouldGetAllB2BComments() throws InvalidCartException, CalculationException
	{
		final OrderModel order = createOrder("GC CEO", 10, OrderStatus.CREATED);
		final UserModel user = userService.getUserForUID("GC CEO");

		final B2BCommentModel comment1 = modelService.create(B2BCommentModel.class);
		comment1.setCode("1");
		comment1.setComment("Testing1");
		comment1.setOwner(user);

		final B2BCommentModel comment2 = modelService.create(B2BCommentModel.class);
		comment2.setCode("2");
		comment2.setComment("Testing2");
		comment2.setOwner(user);

		final List<B2BCommentModel> b2bComments = new ArrayList(order.getB2bcomments());
		b2bComments.add(comment1);
		b2bComments.add(comment2);
		order.setB2bcomments(b2bComments);
		this.modelService.saveAll();

		final List<B2BCommentModel> b2bCommentList = b2bCommentService.getComments(order);
		// the 3rd commend is created for a quote request
		Assert.assertEquals(3, b2bCommentList.size());
	}
}
