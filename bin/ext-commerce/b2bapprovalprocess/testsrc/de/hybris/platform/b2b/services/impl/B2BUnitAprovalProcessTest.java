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
import de.hybris.platform.b2b.WorkflowIntegrationTest;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.enums.ProcessState;
import java.util.Locale;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BUnitAprovalProcessTest extends WorkflowIntegrationTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(B2BUnitAprovalProcessTest.class);
	@Resource
	private BaseDao baseDao;

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
	public void shouldCheckProcessSteps() throws Exception
	{
		final B2BCustomerModel loginUser = login("GC Sales Boss");
		final B2BUnitModel parentUnit = b2bUnitService.getParent(loginUser);
		parentUnit.setApprovalProcessCode("simpleapproval");
		modelService.save(parentUnit);
		final OrderModel order = this.createOrder(loginUser, 1, OrderStatus.CREATED,
				productService.getProductForCode("testProduct0"));
		Assert.assertNotNull(order);
		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);
		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 20000);
		this.modelService.refresh(b2bApprovalProcessModel);
		this.modelService.refresh(order);
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
		Assert.assertEquals(OrderStatus.CREATED, order.getStatus());
	}
}
