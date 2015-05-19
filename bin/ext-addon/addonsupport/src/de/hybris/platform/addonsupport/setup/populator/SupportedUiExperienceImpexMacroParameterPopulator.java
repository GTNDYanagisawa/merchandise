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
package de.hybris.platform.addonsupport.setup.populator;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.addonsupport.setup.impl.AddOnDataImportEventContext;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;


public class SupportedUiExperienceImpexMacroParameterPopulator implements
		Populator<AddOnDataImportEventContext, ImpexMacroParameterData>
{
	private SiteConfigService siteConfigService;
	private SessionService sessionService;
	private BaseSiteService baseSiteService;
	private UiExperienceService uiExperienceService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AddOnDataImportEventContext source, final ImpexMacroParameterData target)
			throws ConversionException
	{
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				getBaseSiteService().setCurrentBaseSite(source.getBaseSite(), false);
				target.setSupportedUiExperienceLevels(getSupportedUiExperienceLevels());
			}

		});
	}

	protected List<UiExperienceLevel> getSupportedUiExperienceLevels()
	{
		final String[] levelsAsString = StringUtils.split(
				getSiteConfigService().getString("storefront.supportedUiExperienceLevels", UiExperienceLevel.DESKTOP.getCode()), ",");

		final Set<UiExperienceLevel> levels = new LinkedHashSet<UiExperienceLevel>();
		for (int i = 0; i < levelsAsString.length; i++)
		{
			final UiExperienceLevel level = UiExperienceLevel.valueOf(levelsAsString[i]);
			levels.add(level);
		}
		return new ArrayList<UiExperienceLevel>(levels);
	}

	public SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
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
