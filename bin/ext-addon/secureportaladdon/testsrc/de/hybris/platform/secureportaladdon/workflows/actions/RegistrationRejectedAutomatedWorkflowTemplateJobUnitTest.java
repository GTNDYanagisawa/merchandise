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
package de.hybris.platform.secureportaladdon.workflows.actions;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * This test file tests and demonstrates the behavior of the RegistrationRejectedAutomatedWorkflowTemplateJob's method
 * perform.
 */
@UnitTest
public class RegistrationRejectedAutomatedWorkflowTemplateJobUnitTest
{
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private RegistrationRejectedAutomatedWorkflowTemplateJob registrationRejectedAutomatedWorkflowTemplateJob;
	private WorkflowActionModel workflowActionModel;
	private WorkflowDecisionModel workflowDecisionModel;

	private final String decisionName = "Test decision name";

	@Before
	public void setUp()
	{
		// We will be testing RegistrationRejectedAutomatedWorkflowTemplateJob
		registrationRejectedAutomatedWorkflowTemplateJob = new RegistrationRejectedAutomatedWorkflowTemplateJob();
		// This instance of a WorkflowActionModel will be used by the tests
		workflowActionModel = new WorkflowActionModel();
		// This instance of a WorkflowDecisionModel will be used by the tests
		workflowDecisionModel = new WorkflowDecisionModel();
	}

	/**
	 * This test tests and demonstrates that the RegistrationRejectedAutomatedWorkflowTemplateJob's method perform. Cause
	 * we do nothing in the B2BRRegistrationRejected workflow step, just return the next decision of the
	 * workflowActionModel
	 */
	@Test
	public void testRejectedAutomatedWorkflowTemplateJob()
	{
		// We will create a test workflowDecisionModel
		final Locale locale = Locale.getDefault();
		workflowDecisionModel.setName(decisionName, locale);

		// Add workflowDecisionModel to collection
		final Collection<WorkflowDecisionModel> c = new ArrayList<>();
		c.add(workflowDecisionModel);

		// Save the workflowDecisionModel collection to the workflowActionModel
		workflowActionModel.setDecisions(c);

		// Now we test to perform the workflowActionModel
		final WorkflowDecisionModel result = registrationRejectedAutomatedWorkflowTemplateJob.perform(workflowActionModel);

		// We then verify the result from the RegistrationRejectedAutomatedWorkflowTemplateJob is equal to the one we just created above 
		assertEquals("the next workflow decision model is expected", workflowDecisionModel, result);
	}
}
