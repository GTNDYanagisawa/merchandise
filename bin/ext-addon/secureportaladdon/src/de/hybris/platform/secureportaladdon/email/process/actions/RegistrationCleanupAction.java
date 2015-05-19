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
package de.hybris.platform.secureportaladdon.email.process.actions;

import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationProcessModel;

import org.springframework.util.Assert;


/**
 * A process action that cleans up the registration information. This should be the very LAST part of the
 * workflow/process execution!
 */
public class RegistrationCleanupAction extends AbstractProceduralAction
{

	private boolean deleteCustomer;

	/**
	 * @param deleteCustomer
	 *           the deleteCustomer to set
	 */
	public void setDeleteCustomer(final boolean deleteCustomer)
	{
		this.deleteCustomer = deleteCustomer;
	}

	@Override
	public void executeAction(final BusinessProcessModel businessProcessModel)
	{

		Assert.isInstanceOf(B2BRegistrationProcessModel.class, businessProcessModel,
				"BusinessProcessModel should be of type B2BRegistrationProcessModel");

		final B2BRegistrationProcessModel registrationProcess = (B2BRegistrationProcessModel) businessProcessModel;

		// TODO: We should delete this object as it is no longer required, but there is an issue in the cockpit where
		// when an attachment of a workflow gets deleted, multiple exceptions occured since the contextbrowsermodel kept
		// references to it. For now, we will leave the registration-attachment intact.
		// getModelService().remove(registrationProcess.getRegistration());

		// Only when a final demand is rejected final should we delete final the attached customer
		if (deleteCustomer)
		{
			getModelService().remove(registrationProcess.getCustomer());
		}

	}

}
