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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import org.apache.log4j.Logger;


/**
 * Action called when a registration request has been approved
 */
public class RegistrationApprovedAutomatedWorkflowTemplateJob extends AbstractAutomatedWorkflowTemplateJob
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(RegistrationApprovedAutomatedWorkflowTemplateJob.class);


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.workflow.jobs.AutomatedWorkflowTemplateJob#perform(de.hybris.platform.workflow.model.
	 * WorkflowActionModel)
	 */
	@Override
	public WorkflowDecisionModel perform(final WorkflowActionModel workflowAction)
	{

		final B2BRegistrationModel registration = getRegistrationAttachment(workflowAction);
		final CustomerModel customer = getCustomer(registration);

		final B2BCustomerModel b2BCustomer = createB2BCustomerModel(customer, registration);

		//Delete temporary customer attached to workflow
		getModelService().remove(customer);

		//persist the newly created b2bCustomer
		getModelService().save(b2BCustomer);

		return defaultDecision(workflowAction);

	}

	/**
	 * Creates an instance of {@link B2BCustomerModel} out of {@link CustomerModel}.
	 * 
	 * @param customer
	 *           CustomerModel data
	 * @return An instance of {@link B2BCustomerModel}
	 */

	private B2BCustomerModel createB2BCustomerModel(final CustomerModel customer, final B2BRegistrationModel registration)
	{

		final B2BCustomerModel b2bCustomer = getModelService().create(B2BCustomerModel.class);

		b2bCustomer.setEmail(customer.getUid());
		b2bCustomer.setName(customer.getName());
		b2bCustomer.setTitle(customer.getTitle());
		b2bCustomer.setUid(customer.getUid());

		b2bCustomer.setDefaultB2BUnit(registration.getDefaultB2BUnit());

		return b2bCustomer;
	}

}