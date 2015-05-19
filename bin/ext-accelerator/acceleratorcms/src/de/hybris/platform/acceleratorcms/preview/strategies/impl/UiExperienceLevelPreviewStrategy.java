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
package de.hybris.platform.acceleratorcms.preview.strategies.impl;

import de.hybris.platform.acceleratorcms.preview.strategies.PreviewContextInformationLoaderStrategy;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;

import org.springframework.beans.factory.annotation.Required;


public class UiExperienceLevelPreviewStrategy implements PreviewContextInformationLoaderStrategy
{

	private UiExperienceService uiExperienceService;

	@Override
	public void initContextFromPreview(final PreviewDataModel preview)
	{
		getUiExperienceService().setDetectedUiExperienceLevel(preview.getUiExperience());
	}

	public UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	@Required
	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}
}
