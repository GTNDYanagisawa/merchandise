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

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.CipherService;
import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cxml.Credential;
import org.cxml.Identity;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PunchOutConfigurationService}.
 */
public class DefaultPunchOutConfigurationService implements PunchOutConfigurationService
{

	private BaseSiteService baseSiteService;

	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	private ConfigurationService configurationService;

	private PunchOutSessionService punchoutSessionService;

	private PunchOutCredentialService punchOutCredentialService;

	private CipherService cipherService;

	private String punchOutSessionUrlPath;

	@Override
	public String getPunchOutLoginUrl()
	{
		final String sessionId = punchoutSessionService.getCurrentPunchOutSessionId();
		if (StringUtils.isNotBlank(sessionId))
		{
			final PunchOutSession punchoutSession = punchoutSessionService.load(sessionId);
			return siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSiteService.getCurrentBaseSite(), "", true,
					getPunchOutSessionUrlPath(), "key=" + getPunchOutLoginAuthToken(punchoutSession) + "&sid=" + sessionId);
		}
		return null;
	}

	private String getPunchOutLoginAuthToken(final PunchOutSession punchoutSession)
	{
		String userId = null;
		final List<Organization> organizationList = punchoutSession.getInitiatedBy();
		final B2BCustomerModel customer = getCustomerFromOrganizations(organizationList);

		if (customer != null)
		{
			userId = customer.getUid();
			return cipherService.encrypt(userId, punchoutSession);
		}
		else
		{
			throw new PunchOutException(PunchOutResponseCode.INTERNAL_SERVER_ERROR, getInexistenCustomerMessage(organizationList));
		}
	}

	private B2BCustomerModel getCustomerFromOrganizations(final List<Organization> organizationList)
	{
		B2BCustomerModel customer = null;

		for (int i = 0; i < organizationList.size(); i++)
		{
			final Credential credential = new Credential();
			credential.setDomain(organizationList.get(i).getDomain());
			final Identity identity = new Identity();
			identity.getContent().add(organizationList.get(i).getIdentity());
			credential.setIdentity(identity);

			customer = punchOutCredentialService.getCustomerForCredentialNoAuth(credential);

			if (customer != null)
			{
				break;
			}
		}
		return customer;
	}

	private String getInexistenCustomerMessage(final List<Organization> organizationList)
	{
		final StringBuilder sb = new StringBuilder("Unable to find customer for given credentials [");
		for (int i = 0; i < organizationList.size(); i++)
		{
			sb.append("[" + organizationList.get(i).getDomain() + "," + organizationList.get(i).getIdentity() + "]");
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String getDefaultCostCenter()
	{
		return getConfigurationService().getConfiguration().getString("b2bpunchoutaddon.checkout.costcenter.default");
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

	public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	@Required
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
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

	public PunchOutSessionService getPunchOutSessionService()
	{
		return punchoutSessionService;
	}

	@Required
	public void setPunchOutSessionService(final PunchOutSessionService punchoutSessionService)
	{
		this.punchoutSessionService = punchoutSessionService;
	}

	public PunchOutCredentialService getPunchOutCredentialService()
	{
		return punchOutCredentialService;
	}

	@Required
	public void setPunchOutCredentialService(final PunchOutCredentialService punchOutCredentialService)
	{
		this.punchOutCredentialService = punchOutCredentialService;
	}

	public CipherService getCipherService()
	{
		return cipherService;
	}

	@Required
	public void setCipherService(final CipherService cipherService)
	{
		this.cipherService = cipherService;
	}

	public String getPunchOutSessionUrlPath()
	{
		return punchOutSessionUrlPath;
	}

	@Required
	public void setPunchOutSessionUrlPath(final String punchOutSessionUrlPath)
	{
		this.punchOutSessionUrlPath = punchOutSessionUrlPath;
	}

}
