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
package de.hybris.platform.acceleratorservices.uiexperience.impl;


import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the UiExperienceService
 */
public class DefaultUiExperienceService implements UiExperienceService
{
	protected static final String DETECTED_UI_EXPERIENCE_LEVEL = "UiExperienceService-Detected-Level";
	protected static final String OVERRIDE_UI_EXPERIENCE_LEVEL = "UiExperienceService-Override-Level";

	private SessionService sessionService;

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Override
	public UiExperienceLevel getDetectedUiExperienceLevel()
	{
		return getSessionService().getAttribute(DETECTED_UI_EXPERIENCE_LEVEL);
	}

	@Override
	public void setDetectedUiExperienceLevel(final UiExperienceLevel uiExperienceLevel)
	{
		getSessionService().setAttribute(DETECTED_UI_EXPERIENCE_LEVEL, uiExperienceLevel);
	}

	@Override
	public UiExperienceLevel getOverrideUiExperienceLevel()
	{
		return getSessionService().getAttribute(OVERRIDE_UI_EXPERIENCE_LEVEL);
	}

	@Override
	public void setOverrideUiExperienceLevel(final UiExperienceLevel uiExperienceLevel)
	{
		if (uiExperienceLevel != null && !uiExperienceLevel.equals(getDetectedUiExperienceLevel()))
		{
			getSessionService().setAttribute(OVERRIDE_UI_EXPERIENCE_LEVEL, uiExperienceLevel);
		}
		else
		{
			getSessionService().removeAttribute(OVERRIDE_UI_EXPERIENCE_LEVEL);
		}
	}


	@Override
	public UiExperienceLevel getUiExperienceLevel()
	{
		// Lookup the UiExperienceLevel override specified in the current session
		final UiExperienceLevel sessionOverrideUiExperienceLevel = getOverrideUiExperienceLevel();
		if (sessionOverrideUiExperienceLevel != null)
		{
			return sessionOverrideUiExperienceLevel;
		}

		return getDetectedUiExperienceLevel();
	}
}
