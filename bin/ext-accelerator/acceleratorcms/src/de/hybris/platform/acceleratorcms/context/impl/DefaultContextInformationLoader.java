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
package de.hybris.platform.acceleratorcms.context.impl;

import de.hybris.platform.acceleratorcms.context.ContextInformationLoader;
import de.hybris.platform.acceleratorcms.preview.strategies.PreviewContextInformationLoaderStrategy;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.misc.CMSFilter;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default context information loader
 */
public class DefaultContextInformationLoader implements ContextInformationLoader
{
	private static final Logger LOG = Logger.getLogger(ContextInformationLoader.class);

	private CMSSiteService cmsSiteService;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private I18NService i18NService;
	private ModelService modelService;
	private TimeService timeService;
	private List<PreviewContextInformationLoaderStrategy> strategies;
	private List<PreviewContextInformationLoaderStrategy> previewRequestStrategies;



	public CMSSiteModel getCurrentSite()
	{
		return getCMSSiteService().getCurrentSite();
	}

	@Override
	public void setCatalogVersions()
	{
		try
		{
			final CMSSiteModel currentSiteModel = getCurrentSite();
			if (currentSiteModel != null)
			{
				getCMSSiteService().setCurrentSiteAndCatalogVersions(currentSiteModel, true);
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Catalog has no active catalog version!", e);
			}
		}
	}

	@Override
	public CMSSiteModel initializeSiteFromRequest(final String absoluteURL)
	{
		try
		{
			final URL currentURL = new URL(absoluteURL);
			final CMSSiteService cmsSiteService = getCMSSiteService();
			final CMSSiteModel cmsSiteModel = cmsSiteService.getSiteForURL(currentURL);
			if (cmsSiteModel != null)
			{
				getBaseSiteService().setCurrentBaseSite(cmsSiteModel, true);
				return cmsSiteModel;
			}
		}
		catch (final MalformedURLException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Cannot find CMSSite associated with current URL ( " + absoluteURL
						+ " - check whether this is correct URL) !");
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.warn("Cannot find CMSSite associated with current URL (" + absoluteURL + ")!");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
		}
		return null;
	}


	@Override
	public void initializePreviewRequest(final PreviewDataModel previewDataModel)
	{
		initializePreviewContextStrategies(getPreviewRequestStrategies(), previewDataModel);
	}

	protected void loadFakeUser(final UserModel fakeUser)
	{
		final UserService userService = getUserService();
		final UserModel currentUser = userService.getCurrentUser();
		if (fakeUser != null && !fakeUser.equals(currentUser))
		{
			userService.setCurrentUser(fakeUser);
		}
	}

	protected void loadFakeUserGroup(final PreviewDataModel previewDataModel)
	{
		if (previewDataModel.getUser() == null && previewDataModel.getUserGroup() != null)
		{
			UserModel userWithinDesiredGroup = null;
			final UserGroupModel fakeUserGroup = previewDataModel.getUserGroup();
			for (final PrincipalModel principalModel : fakeUserGroup.getMembers())
			{
				if (principalModel instanceof UserModel)
				{
					userWithinDesiredGroup = (UserModel) principalModel;
					break;
				}
			}
			if (userWithinDesiredGroup != null)
			{
				loadFakeUser(userWithinDesiredGroup);
			}
		}
	}

	protected void loadFakeLanguage(final LanguageModel languageModel)
	{
		if (languageModel != null)
		{
			getI18NService().setCurrentLocale(new Locale(languageModel.getIsocode()));
		}
	}

	protected void storePreviewTicketIDWithinSession(final HttpServletRequest httpRequest)
	{
		final String ticketId = httpRequest.getParameter(CMSFilter.PREVIEW_TICKET_ID_PARAM);
		if (StringUtils.isNotBlank(ticketId))
		{
			JaloSession.getCurrentSession().setAttribute(CMSFilter.PREVIEW_TICKET_ID_PARAM, ticketId);
		}
	}

	protected void loadFakeDate(final Date fakeDate)
	{
		if (fakeDate != null)
		{
			getTimeService().setCurrentTime(fakeDate);
			JaloSession.getCurrentSession().setAttribute(Cms2Constants.PREVIEW_TIME, fakeDate);
		}
	}

	@Override
	public void loadFakeContextInformation(final HttpServletRequest httpRequest, final PreviewDataModel previewData)
	{
		initializePreviewContextStrategies(getStrategies(), previewData);

		storePreviewTicketIDWithinSession(httpRequest);
	}

	@Override
	public void storePreviewData(final PreviewDataModel previewData)
	{
		final ModelService modelService = getModelService();
		if (previewData == null)
		{
			LOG.warn("Could not store preview data. Reason: Preview data was null.");
		}
		else
		{
			if (modelService == null)
			{
				LOG.warn("Could not store preview data. Reason: Model service was null.");
			}
			else
			{
				modelService.save(previewData);
			}
		}
	}

	private void initializePreviewContextStrategies(final List<PreviewContextInformationLoaderStrategy> strategyList,
			final PreviewDataModel previewData)
	{
		for (final PreviewContextInformationLoaderStrategy strategy : strategyList)
		{
			strategy.initContextFromPreview(previewData);
		}
	}


	public static class LoadUserStrategy extends DefaultContextInformationLoader implements
			PreviewContextInformationLoaderStrategy
	{
		@Override
		public void initContextFromPreview(final PreviewDataModel preview)
		{
			super.loadFakeUser(preview.getUser());

		}
	}

	public static class LoadUserGroupStrategy extends DefaultContextInformationLoader implements
			PreviewContextInformationLoaderStrategy
	{

		@Override
		public void initContextFromPreview(final PreviewDataModel preview)
		{

			super.loadFakeUserGroup(preview);

		}

	}

	public static class LoadLanguageStrategy extends DefaultContextInformationLoader implements
			PreviewContextInformationLoaderStrategy
	{

		@Override
		public void initContextFromPreview(final PreviewDataModel preview)
		{
			if (preview.getLanguage() != null)
			{
				super.loadFakeLanguage(preview.getLanguage());
			}

		}

	}

	public static class LoadDateStrategy extends DefaultContextInformationLoader implements
			PreviewContextInformationLoaderStrategy
	{

		@Override
		public void initContextFromPreview(final PreviewDataModel preview)
		{
			super.loadFakeDate(preview.getTime());
		}

	}



	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	@Required
	public void setPreviewRequestStrategies(final List<PreviewContextInformationLoaderStrategy> previewRequestStrategies)
	{
		this.previewRequestStrategies = previewRequestStrategies;

	}

	@Required
	public void setStrategies(final List<PreviewContextInformationLoaderStrategy> strategies)
	{
		this.strategies = strategies;
	}

	protected CMSSiteService getCMSSiteService()
	{
		return cmsSiteService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}


	protected UserService getUserService()
	{
		return userService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	protected List<PreviewContextInformationLoaderStrategy> getPreviewRequestStrategies()
	{
		return previewRequestStrategies;
	}

	protected List<PreviewContextInformationLoaderStrategy> getStrategies()
	{
		return strategies;
	}
}
