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
package de.hybris.platform.b2bacceleratorservices.btg.outputaction.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorservices.model.btg.BTGAssignOrganizationToGroupDefinitionModel;
import de.hybris.platform.btg.outputaction.OutputActionContext;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.action.impl.ActionPerformable;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.action.AbstractActionModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


public class AssignOrganizationToGroup implements ActionPerformable<OutputActionContext>
{
	private ModelService modelService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService)
	{
		this.b2BUnitService = b2BUnitService;
	}

	/**
	 * Assigns user to a user group defined in the output action definition.
	 * 
	 * @param argument
	 *           - {@link de.hybris.platform.btg.outputaction.OutputActionContext} consists of
	 *           {@link de.hybris.platform.core.model.user.UserModel} and
	 *           {@link de.hybris.platform.btg.model.BTGOutputActionDefinitionModel}
	 */
	@Override
	public void performAction(final AbstractActionModel action, final OutputActionContext argument)
	{
		final Collection<UserGroupModel> userGroups = ((BTGAssignOrganizationToGroupDefinitionModel) argument.getActionDefinition())
				.getUserGroups();
		final UserModel user = argument.getUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BUnitModel parentUnit = b2BUnitService.getParent((B2BCustomerModel) user);
			final B2BUnitModel rootUnit = b2BUnitService.getRootUnit(parentUnit);

			final Set<PrincipalGroupModel> groups = new HashSet(rootUnit.getGroups());
			groups.addAll(userGroups);
			rootUnit.setGroups(groups);
			modelService.save(rootUnit);
		}
	}

}
