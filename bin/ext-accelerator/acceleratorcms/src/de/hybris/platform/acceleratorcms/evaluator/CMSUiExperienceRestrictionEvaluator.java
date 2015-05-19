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

import de.hybris.platform.acceleratorcms.model.restrictions.CMSUiExperienceRestrictionModel;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 */
public class CMSUiExperienceRestrictionEvaluator implements CMSRestrictionEvaluator<CMSUiExperienceRestrictionModel>
{
	private static final Logger LOG = Logger.getLogger(CMSUiExperienceRestrictionEvaluator.class);

	private UiExperienceService uiExperienceService;

	protected UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	@Required
	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}

	@Override
	public boolean evaluate(final CMSUiExperienceRestrictionModel restriction, final RestrictionData context)
	{
		final UiExperienceLevel uiExperienceLevel = getUiExperienceService().getUiExperienceLevel();
		if (uiExperienceLevel != null)
		{
			return uiExperienceLevel.equals(restriction.getUiExperience());
		}

		LOG.warn("Could not evaluate CMSUiExperienceRestriction. No experience level set. Returning false.");
		return false;
	}
}
