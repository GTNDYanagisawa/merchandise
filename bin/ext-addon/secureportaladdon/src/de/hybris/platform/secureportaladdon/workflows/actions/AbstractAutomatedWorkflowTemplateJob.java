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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.jobs.AutomatedWorkflowTemplateJob;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract class with basic functionalities to support automated workflow actions
 */
public abstract class AbstractAutomatedWorkflowTemplateJob implements AutomatedWorkflowTemplateJob
{

	private BusinessProcessService businessProcessService;

	private ModelService modelService;

	private WorkflowAttachmentService workflowAttachmentService;

	private UserService userService;

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
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

	/**
	 * @return the workflowAttachmentService
	 */
	public WorkflowAttachmentService getWorkflowAttachmentService()
	{
		return workflowAttachmentService;
	}

	/**
	 * @param workflowAttachmentService
	 *           the workflowAttachmentService to set
	 */
	@Required
	public void setWorkflowAttachmentService(final WorkflowAttachmentService workflowAttachmentService)
	{
		this.workflowAttachmentService = workflowAttachmentService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
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
	 * Use this when there is only ONE decision from a given action!
	 * 
	 * Gets the next decision and resumes the execution of the workflow UNLESS there are no more decisions in which case
	 * we return null to indicate the end of the workflow
	 * 
	 * @param workflowActionModel
	 *           The current workflow action
	 * @return A {@link WorkflowDecisionModel} that indicates the default decision
	 */
	protected WorkflowDecisionModel defaultDecision(final WorkflowActionModel workflowActionModel)
	{

		for (final WorkflowDecisionModel decision : workflowActionModel.getDecisions())
		{
			return decision;
		}

		return null;

	}

	/**
	 * Gets the {@link B2BRegistrationModel} attached to the workflow
	 * 
	 * @param workflowActionModel
	 *           The workflow action to extract the attachments from
	 * @return The {@link B2BRegistrationModel} attached to workflow, or null if not found
	 */
	protected B2BRegistrationModel getRegistrationAttachment(final WorkflowActionModel workflowActionModel)
	{
		return getModelOfType(workflowActionModel, B2BRegistrationModel.class);
	}

	/**
	 * Gets the {@link CustomerModel} attached to the workflow
	 * 
	 * @param b2bRegistrationModel
	 *           The registration model that holds the uid of the associated customer
	 * @return The {@link CustomerModel} attached to workflow, or null if not found
	 */
	protected CustomerModel getCustomer(final B2BRegistrationModel b2bRegistrationModel)
	{
		return getUserService().getUserForUID(b2bRegistrationModel.getEmail(), CustomerModel.class);
	}

	/**
	 * Gets a model of the provided class from the list
	 * 
	 * @param workflowActionModel
	 *           The workflow action being executed
	 * @param clazz
	 *           The item to be returned needs to be of this type
	 * @return The first instance of the provided type, or null if not found
	 */
	protected <T extends ItemModel> T getModelOfType(final WorkflowActionModel workflowActionModel, final Class<T> clazz)
	{
		final List<ItemModel> models = workflowAttachmentService.getAttachmentsForAction(workflowActionModel, clazz.getName());

		if (CollectionUtils.isNotEmpty(models))
		{
			return (T) models.iterator().next();
		}

		return null;

	}

}