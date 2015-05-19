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

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationProcessModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Automated action responsible for sending an email by launching a process
 */
public class SendEmailAutomatedWorkflowTemplateJob<PROCESS extends B2BRegistrationProcessModel> extends
		AbstractAutomatedWorkflowTemplateJob
{

	private static final Logger LOG = Logger.getLogger(SendEmailAutomatedWorkflowTemplateJob.class);

	private String processDefinitionName;

	/**
	 * @param processDefinitionName
	 *           The name of the process definition that is responsible for sending an email. Processes are configured
	 *           through XML files under the resource folder.
	 */
	@Required
	public void setProcessDefinitionName(final String processDefinitionName)
	{
		this.processDefinitionName = processDefinitionName;
	}

	/**
	 * @return the processDefinitionName
	 */
	public String getProcessDefinitionName()
	{
		return processDefinitionName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.workflow.jobs.AutomatedWorkflowTemplateJob#perform(de.hybris.platform.workflow.model.
	 * WorkflowActionModel)
	 */
	@Override
	public WorkflowDecisionModel perform(final WorkflowActionModel workflowActionModel)
	{

		final B2BRegistrationModel registrationModel = getRegistrationAttachment(workflowActionModel);
		Assert.notNull(registrationModel, "'B2BRegistrationModel' is a mandatory workflow attachment!");

		final CustomerModel customerModel = getCustomer(registrationModel);
		Assert.notNull(customerModel, "'CustomerModel' is a mandatory workflow attachment!");

		final B2BRegistrationProcessModel process = createProcessModel(customerModel, registrationModel);
		getModelService().save(process);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Starting process with code '%s'", process.getCode()));
		}

		getBusinessProcessService().startProcess(process);

		return defaultDecision(workflowActionModel);

	}

	/**
	 * Creates an instance of {@link B2BRegistrationProcessModel} using the process definition.
	 * 
	 * @param customerModel
	 *           Customer data
	 * @param registrationModel
	 *           Registration data
	 * @return An instance of {@link B2BRegistrationProcessModel}
	 */
	protected PROCESS createProcessModel(final CustomerModel customerModel, final B2BRegistrationModel registrationModel)
	{

		final String processCode = generateProcessCode(customerModel);
		final PROCESS process = getBusinessProcessService().createProcess(processCode, getProcessDefinitionName());

		process.setCustomer(customerModel);

		process.setRegistration(registrationModel);
		process.setSite(registrationModel.getCmsSite());
		process.setLanguage(registrationModel.getLanguage());
		process.setCurrency(registrationModel.getCurrency());
		process.setStore(registrationModel.getBaseStore());

		return process;

	}

	/**
	 * Generates a new 'unique' code for the process to be launched
	 * 
	 * @param customerModel
	 *           The customer associated to the workflow
	 * @return A code made of the process definition name, the customer's email and a timestamp
	 */
	protected String generateProcessCode(final CustomerModel customerModel)
	{
		return getProcessDefinitionName() + "-" + customerModel.getUid() + "-" + System.currentTimeMillis();
	}

}