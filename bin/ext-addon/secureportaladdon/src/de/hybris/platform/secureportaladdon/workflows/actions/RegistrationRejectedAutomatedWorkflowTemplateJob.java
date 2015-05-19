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

import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import org.apache.log4j.Logger;




/**
 * Action called when a registration request has been rejected
 */
public class RegistrationRejectedAutomatedWorkflowTemplateJob extends AbstractAutomatedWorkflowTemplateJob
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(RegistrationRejectedAutomatedWorkflowTemplateJob.class);

	/*
	 * In this workflow step, we do nothing to reject registration, the registered customer will be removed in the next
	 * send email workflow step
	 * 
	 * @see de.hybris.platform.workflow.jobs.AutomatedWorkflowTemplateJob#perform(de.hybris.platform.workflow.model.
	 * WorkflowActionModel)
	 */
	@Override
	public WorkflowDecisionModel perform(final WorkflowActionModel workflowAction)
	{
		return defaultDecision(workflowAction);
	}

}