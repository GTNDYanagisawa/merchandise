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
package de.hybris.platform.secureportaladdon.facades.impl;

import de.hybris.platform.secureportaladdon.facades.B2BRegistrationWorkflowFacade;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BRegistrationWorkflowFacade}
 */
public class DefautB2BRegistrationWorkflowFacade implements B2BRegistrationWorkflowFacade
{

	private static final Logger LOG = Logger.getLogger(DefautB2BRegistrationWorkflowFacade.class);

	private WorkflowService workflowService;

	private WorkflowProcessingService workflowProcessingService;

	private UserService userService;

	private ModelService modelService;

	/**
	 * @param workflowService
	 *           the workflowService to set
	 */
	@Required
	public void setWorkflowService(final WorkflowService workflowService)
	{
		this.workflowService = workflowService;
	}


	/**
	 * @param workflowProcessingService
	 *           the workflowProcessingService to set
	 */
	@Required
	public void setWorkflowProcessingService(final WorkflowProcessingService workflowProcessingService)
	{
		this.workflowProcessingService = workflowProcessingService;
	}


	/**
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.secureportaladdon.facades.B2bRegistrationWorkflowFacade#launchWorkflow(de.hybris.platform.workflow
	 * .model.WorkflowTemplateModel, de.hybris.platform.secureportaladdon.model.B2BRegistrationModel,
	 * de.hybris.platform.core.model.user.CustomerModel)
	 */
	@Override
	public void launchWorkflow(final WorkflowTemplateModel workflowTemplateModel, final B2BRegistrationModel b2bRegistrationModel)
	{

		final WorkflowModel workflow = workflowService.createWorkflow(workflowTemplateModel, b2bRegistrationModel,
				userService.getAdminUser());

		modelService.save(workflow);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Starting workflow for customer with email '%s' and organization (company name) '%s'.",
					b2bRegistrationModel.getEmail(), b2bRegistrationModel.getCompanyName()));
		}

		workflowProcessingService.startWorkflow(workflow);

	}

}