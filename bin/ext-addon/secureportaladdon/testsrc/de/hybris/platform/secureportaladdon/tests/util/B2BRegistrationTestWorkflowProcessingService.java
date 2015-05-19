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
package de.hybris.platform.secureportaladdon.tests.util;

import de.hybris.platform.workflow.impl.DefaultWorkflowProcessingService;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;


/**
 * Test Service to override default automated worflow action activation. It allows individual activations of automated
 * workflow actions.
 */
public class B2BRegistrationTestWorkflowProcessingService extends DefaultWorkflowProcessingService
{
	/**
	 * The method below calls activates all subsequent automated actions. It is kept empty for use in a Workflow
	 * Integration test in which it is desired to activate an individual automated workflow action.
	 */
	@Override
	protected void chosen(final WorkflowDecisionModel selDec)
	{
		//Read Java - Doc Comments. This method must be kept empty
	}

}
