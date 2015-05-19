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
package de.hybris.platform.acceleratorcms.evaluator;

import de.hybris.platform.acceleratorcms.model.CMSActionTypeModel;
import de.hybris.platform.acceleratorcms.model.restrictions.CMSActionRestrictionModel;
import de.hybris.platform.cms2.model.CMSComponentTypeModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class CMSActionRestrictionEvaluator implements CMSRestrictionEvaluator<CMSActionRestrictionModel>
{
	private static final Logger LOG = Logger.getLogger(CMSActionRestrictionEvaluator.class);
	private TypeService typeService;

	@Override
	public boolean evaluate(final CMSActionRestrictionModel restriction, final RestrictionData context)
	{
		final Object parentComponent = context.getValue("parentComponent");
		final Object component = context.getValue("component");
		if (parentComponent != null && component != null)
		{
			final String parentComponentType = ((AbstractCMSComponentModel) parentComponent).getItemtype();
			final String actionType = ((AbstractCMSComponentModel) component).getItemtype();
			final CMSComponentTypeModel componentTypeModel = (CMSComponentTypeModel) getTypeService().getComposedTypeForCode(parentComponentType);
			final Collection<CMSActionTypeModel> applicableActionTypes = componentTypeModel.getActionTypes();

			for (CMSActionTypeModel applicableAction : applicableActionTypes)
			{
				if (applicableAction.getCode().equals(actionType))
				{
					return true;
				}
			}
			return false;
		}
		LOG.warn("parentComponent attribute was not passed in the cms:component tag restriction evaluation skipped for " + restriction.getItemtype());
		return true;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}
