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
package de.hybris.platform.secureportaladdon.facades;

import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;


/**
 * Facade that handles workflow specific actions for B2B registrations
 */
public interface B2BRegistrationWorkflowFacade
{
	/**
	 * Launches a new workflow instance
	 * 
	 * @param workflowTemplateModel
	 *           The workflow template definition to use
	 * @param b2bRegistrationModel
	 *           All registration specific information
	 */
	public void launchWorkflow(WorkflowTemplateModel workflowTemplateModel, B2BRegistrationModel b2bRegistrationModel);

}
