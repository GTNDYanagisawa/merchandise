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
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import java.util.Collection;
import java.util.Locale;
import javax.annotation.Resource;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
@Ignore("BTOB-683")
public class B2BOrderAprovalProcessIntegrationTest extends WorkflowIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(B2BOrderAprovalProcessIntegrationTest.class);
	@Resource
	public B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	@Resource
	private BaseDao baseDao;

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/organizationdata.csv", "UTF-8");
		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}


	@Test
	@Ignore("[PerformMerchantCheck] Assigned user AcctMgrA has no read access to items of type WorkflowAction.\n"
			+ " [yunitint] de.hybris.platform.servicelayer.exceptions.ModelSavingException: Assigned user AcctMgrA has no read access to items of type WorkflowAction.")
	public void shouldStartApprovalProcessAndAssertAproval() throws Exception
	{
		//mark.rivers@rustic-hw.com has Permissions 12K USD ORDER, 15K USD MONTH
		final OrderModel order = createOrder("mark.rivers@rustic-hw.com", 1200, OrderStatus.CREATED);

		Assert.assertNotNull(order);
		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);
		LOG.info(ReflectionToStringBuilder.toString(b2bApprovalProcessModel.getCurrentTasks()));

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcess", 80000))
		{
			this.modelService.refresh(order);
			Assert.assertNotNull(order.getWorkflow());
			// approve workflow
			modelService.refresh(order);
			final Collection<WorkflowActionModel> actions = b2bWorkflowIntegrationService.getStartWorkflowActions(order
					.getWorkflow());
			Assert.assertEquals(1, actions.size());
			b2bWorkflowIntegrationService.approveWorkflowAction(actions.iterator().next());
		}

		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
		this.modelService.refresh(b2bApprovalProcessModel);
		this.modelService.refresh(order);
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
		Assert.assertEquals(OrderStatus.APPROVED, order.getStatus());
	}

	@Test
	public void shouldStartApprovalProcessAndAsserRejection() throws Exception
	{
		//mark.rivers@rustic-hw.com has Permissions 12K USD ORDER, 15K USD MONTH
		final OrderModel order = createOrder("mark.rivers@rustic-hw.com", 1200, OrderStatus.CREATED);
		Assert.assertNotNull(order);

		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcess", 60000))
		{
			this.modelService.refresh(order);
			Assert.assertNotNull(order.getWorkflow());

			// reject workflow
			modelService.refresh(order);
			final Collection<WorkflowActionModel> actions = b2bWorkflowIntegrationService.getStartWorkflowActions(order
					.getWorkflow());
			Assert.assertEquals(1, actions.size());
			b2bWorkflowIntegrationService.rejectWorkflowAction(actions.iterator().next());
		}

		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
		this.modelService.refresh(b2bApprovalProcessModel);
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
		this.modelService.refresh(order);
		Assert.assertEquals(OrderStatus.REJECTED, order.getStatus());
	}
}
