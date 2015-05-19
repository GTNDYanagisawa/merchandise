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
import de.hybris.platform.b2b.enums.WorkflowTemplateType;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BWorkflowDaoTest extends B2BIntegrationTransactionalTest
{

	private static final Logger LOG = Logger.getLogger(B2BWorkflowDaoTest.class);
	@Resource
	public B2BWorkflowIntegrationService b2bWorkflowIntegrationService;

	@Resource
	protected DefaultB2BWorkflowActionDao b2bWorkflowActionDao;

	@Before
	public void beforeTest() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
	}

	@Test
	public void approverGroupHasActionsOfSpecifiedTemplateCode() throws Exception
	{
		final B2BUnitModel unit = b2bUnitService.getUnitForUid("IC");

		Assert.assertNotNull(unit);
		Assert.assertNotNull(unit.getApprovers());
		Assert.assertTrue(unit.getApprovers().size() > 0);
		LOG.debug("Approvers are " + unit.getApprovers().size());

		final B2BCustomerModel approver = unit.getApprovers().iterator().next();
		final String workflowTemplateCode = b2bWorkflowIntegrationService.generateWorkflowTemplateCode("B2B_APPROVAL_WORKFLOW",
				Collections.singletonList(approver));
		final WorkflowTemplateModel workflowTemplate = b2bWorkflowIntegrationService.createWorkflowTemplate(
				Collections.singletonList(approver), workflowTemplateCode, "Generated B2B Order Approval Workflow",
				WorkflowTemplateType.ORDER_APPROVAL);

		Assert.assertNotNull(workflowTemplate);
		final WorkflowModel workflow = this.newestWorkflowService.createWorkflow(workflowTemplate.getName(), workflowTemplate,
				Collections.EMPTY_LIST, workflowTemplate.getOwner());
		workflowProcessingService.startWorkflow(workflow);
		this.modelService.saveAll(); // workaround for PLA-10938
		Collection<WorkflowActionModel> actions = b2bWorkflowActionDao.findWorkflowActionsByUserActionCodeAndStatus(
				WorkflowActionStatus.IN_PROGRESS, B2BWorkflowIntegrationService.ACTIONCODES.APPROVAL.name(), approver);
		Assert.assertEquals(1, actions.size());
		actions = b2bWorkflowActionDao.findWorkflowActionsByUserActionCodeAndStatus(WorkflowActionStatus.IN_PROGRESS,
				"qualifierThatDoesNotExist", approver);
		Assert.assertEquals(0, actions.size());

	}
}
