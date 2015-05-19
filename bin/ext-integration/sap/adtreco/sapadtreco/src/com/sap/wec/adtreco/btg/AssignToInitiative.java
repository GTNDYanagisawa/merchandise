/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sap.wec.adtreco.btg;

import de.hybris.platform.btg.model.BTGAssignToGroupDefinitionModel;
import de.hybris.platform.btg.model.BTGOutputActionDefinitionModel;
import de.hybris.platform.btg.outputaction.OutputActionContext;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.action.impl.ActionPerformable;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.action.AbstractActionModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Represents BTG output action logic. Assigns user to user group. Implements {@link ActionPerformable} from platform's
 * action framework for {@link OutputActionContext} type. The class is a spring bean, registered as 'assignToGroup'. All
 * {@link BTGAssignToGroupDefinitionModel} instances have this bean as a default action logic executor (
 * {@link BTGOutputActionDefinitionModel#getTarget()}).
 */
public class AssignToInitiative implements ActionPerformable<OutputActionContext>
{
	private ModelService modelService;
	private UserService userService;

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
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Assigns user to a user group defined in the output action definition.
	 *
	 * @param argument
	 *           - {@link OutputActionContext} consists of {@link UserModel} and {@link BTGOutputActionDefinitionModel}
	 */
	@Override
	public void performAction(final AbstractActionModel action, final OutputActionContext argument)
	{

	}
}
