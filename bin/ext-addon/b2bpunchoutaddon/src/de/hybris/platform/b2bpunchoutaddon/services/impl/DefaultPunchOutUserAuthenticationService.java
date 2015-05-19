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
package de.hybris.platform.b2bpunchoutaddon.services.impl;

import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.constants.PunchOutSetupOperation;
import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;
import de.hybris.platform.b2bpunchoutaddon.security.PunchOutUserAuthenticationStrategy;
import de.hybris.platform.b2bpunchoutaddon.security.PunchOutUserGroupSelectionStrategy;
import de.hybris.platform.b2bpunchoutaddon.security.PunchOutUserSelectionStrategy;
import de.hybris.platform.b2bpunchoutaddon.services.PunchOutUserAuthenticationService;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * Default implementation of {@link PunchOutUserAuthenticationService}.
 */
public class DefaultPunchOutUserAuthenticationService implements PunchOutUserAuthenticationService
{

	private PunchOutUserGroupSelectionStrategy punchOutUserGroupSelectionStrategy;
	private PunchOutUserSelectionStrategy punchOutUserSelectionStrategy;
	private PunchOutUserAuthenticationStrategy punchOutUserAuthenticationStrategy;

	private ConfigurationService configurationService;

	@Override
	public void authenticate(final String userId, final boolean isSeamlessLogin, final PunchOutSession punchOutSession,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		final Collection<UserGroupModel> groups = getPunchOutUserGroupSelectionStrategy().select(userId);
		final UserModel user = getPunchOutUserSelectionStrategy().select(userId, groups, punchOutSession);
		if (isSeamlessLogin)
		{
			selectRedirectPage(punchOutSession, request);
		}
		getPunchOutUserAuthenticationStrategy().authenticate(user, request, response);
	}

	@Override
	public void logout()
	{
		getPunchOutUserAuthenticationStrategy().logout();
	}

	/**
	 * Select page that user will be redirected in seamless login, depending on the selected operation.
	 * 
	 * @param punchOutSession
	 * @param request
	 */
	private void selectRedirectPage(final PunchOutSession punchOutSession, final HttpServletRequest request)
	{
		final Configuration configuration = configurationService.getConfiguration();
		final String operation = punchOutSession.getOperation();

		if (StringUtils.equalsIgnoreCase(operation, PunchOutSetupOperation.CREATE.toString()))
		{
			request.setAttribute(B2bpunchoutaddonConstants.SEAMLESS_PAGE,
					configuration.getString("b2bpunchoutaddon.redirect.create"));
		}
		else if (StringUtils.equalsIgnoreCase(operation, PunchOutSetupOperation.EDIT.toString()))
		{
			request.setAttribute(B2bpunchoutaddonConstants.SEAMLESS_PAGE, configuration.getString("b2bpunchoutaddon.redirect.edit"));
		}
		else if (StringUtils.equalsIgnoreCase(operation, PunchOutSetupOperation.INSPECT.toString()))
		{
			request.setAttribute(B2bpunchoutaddonConstants.SEAMLESS_PAGE,
					configuration.getString("b2bpunchoutaddon.redirect.inspect"));
		}
	}

	public PunchOutUserGroupSelectionStrategy getPunchOutUserGroupSelectionStrategy()
	{
		return punchOutUserGroupSelectionStrategy;
	}

	@Required
	public void setPunchOutUserGroupSelectionStrategy(final PunchOutUserGroupSelectionStrategy punchOutUserGroupSelectionStrategy)
	{
		this.punchOutUserGroupSelectionStrategy = punchOutUserGroupSelectionStrategy;
	}

	public PunchOutUserSelectionStrategy getPunchOutUserSelectionStrategy()
	{
		return punchOutUserSelectionStrategy;
	}

	@Required
	public void setPunchOutUserSelectionStrategy(final PunchOutUserSelectionStrategy punchOutUserSelectionStrategy)
	{
		this.punchOutUserSelectionStrategy = punchOutUserSelectionStrategy;
	}

	public PunchOutUserAuthenticationStrategy getPunchOutUserAuthenticationStrategy()
	{
		return punchOutUserAuthenticationStrategy;
	}

	@Required
	public void setPunchOutUserAuthenticationStrategy(final PunchOutUserAuthenticationStrategy punchOutUserAuthenticationStrategy)
	{
		this.punchOutUserAuthenticationStrategy = punchOutUserAuthenticationStrategy;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
