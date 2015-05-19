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
import de.hybris.platform.b2b.enums.WorkflowTemplateType;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import java.util.Collections;
import java.util.List;
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
public class DefaultB2BWorkflowIntegrationServiceTest extends WorkflowIntegrationTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(B2BOrderAprovalProcessIntegrationTest.class);
	@Resource
	public B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
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
	public void shouldStartWorkflowAndSaveAttachmentsForActions() throws Exception
	{

		final B2BCustomerModel user = userService.getUserForUID("IC CEO", B2BCustomerModel.class);
		final OrderModel order = createOrder("IC CEO", 1, OrderStatus.CREATED);
		Assert.assertNotNull(order);
		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);

		final List<? extends UserModel> userModels = Collections.singletonList(user);
		final String workflowTemplateCode = b2bWorkflowIntegrationService.generateWorkflowTemplateCode("B2B_APPROVAL_WORKFLOW",
				userModels);
		final WorkflowTemplateModel workflowTemplate = b2bWorkflowIntegrationService.createWorkflowTemplate(userModels,
				workflowTemplateCode, "Generated B2B Order Approval Workflow", WorkflowTemplateType.ORDER_APPROVAL);

		final WorkflowModel workflow = newestWorkflowService.createWorkflow(workflowTemplate.getName(), workflowTemplate,
				Collections.<ItemModel> singletonList(b2bApprovalProcessModel), workflowTemplate.getOwner());
		Assert.assertTrue(workflowProcessingService.startWorkflow(workflow));
		this.modelService.saveAll();
		for (final WorkflowActionModel action : workflow.getActions())
		{
			// Attachments did saved on all the actions of the actions of the workflow except the last one.
			Assert.assertFalse("Attachment should have been saved on the workflowActionModel", action.getAttachmentItems().isEmpty());
		}

		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
	}
}
