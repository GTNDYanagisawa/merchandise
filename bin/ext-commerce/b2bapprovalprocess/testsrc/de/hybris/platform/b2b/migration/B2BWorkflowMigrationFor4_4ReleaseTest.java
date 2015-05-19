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
package de.hybris.platform.b2b.migration;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTransactionalTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public class B2BWorkflowMigrationFor4_4ReleaseTest extends B2BIntegrationTransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(B2BWorkflowMigrationFor4_4ReleaseTest.class);
	@Resource
	private B2BWorkflowMigrationFor4_4Release b2bWorkflowMigrationFor4_4Release;

	@Before
	public void beforeTest() throws Exception
	{
		createWorkflow();
	}

	private void createWorkflow()
	{
		userService.setCurrentUser(userService.getAdminUser());
		final WorkflowTemplateModel workflowTemplate = modelService.create(WorkflowTemplateModel.class);
		//create action
		for (int i = 0; i < 10; i++)
		{
			final WorkflowActionTemplateModel workflowActTmpl = modelService.create(WorkflowActionTemplateModel.class);
			workflowActTmpl.setWorkflow(workflowTemplate);
			workflowActTmpl.setOwner(userService.getAdminUser());
			workflowActTmpl.setCode("action tmpl");
			workflowActTmpl.setActionType(WorkflowActionType.START);
			workflowActTmpl.setWorkflow(workflowTemplate);
			workflowActTmpl.setName(String.valueOf(i), Locale.ENGLISH);
			workflowActTmpl.setPrincipalAssigned(userService.getAdminUser());
			workflowActTmpl.setQualifier(null);
			workflowActTmpl.setCode(String.valueOf(System.currentTimeMillis()));
			final Collection<WorkflowDecisionTemplateModel> decisions = new ArrayList<WorkflowDecisionTemplateModel>();

			for (int j = 1; j < 2; j++)
			{
				final WorkflowDecisionTemplateModel decisionModel = modelService.create(WorkflowDecisionTemplateModel.class);
				decisionModel.setDescription(String.valueOf(j));
				decisionModel.setToTemplateActions(Collections.singletonList(workflowActTmpl));
				decisionModel.setName(String.valueOf(j));
				decisionModel.setQualifier(null);
				decisionModel.setCode(String.valueOf(j));
				decisions.add(decisionModel);
			}
			workflowActTmpl.setDecisionTemplates(decisions);
		}
		modelService.saveAll();
		newestWorkflowService.createWorkflow(workflowTemplate.getName(), workflowTemplate, Collections.<ItemModel> emptyList(),
				userService.getAdminUser());

	}

	@Test
	public void testWorkflowMigration() throws Exception
	{
		b2bWorkflowMigrationFor4_4Release.migrateWorkflowActionModels();
		Assert.assertTrue(b2bWorkflowMigrationFor4_4Release.findWorkflowActionsToMigrate().isEmpty());
	}
}
