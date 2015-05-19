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
 */

/**
 * 
 */
package de.hybris.platform.ycommercewebservices.oauth2;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;


/**
 * Votes to grant access if current OAuth Client ID matches with any config attribute
 * 
 * Only config attributes starting with 'prefix' will be took into consideration. Default prefix is 'CLIENT_'. Prefix
 * can be customized, in particular when null all config attributes will be analyzed.
 * 
 * If none of the configuration attributes starts with 'prefix', the voter abstains from voting.
 * 
 * If there are some configuration attributes starting with prefix but none matches, the voter denies access.
 * 
 * Comparisions are case insensitive.
 * 
 * Example: if config attribute = 'CLIENT_MOBILE_ANDROID' and client id is 'mobile_android' access by this voter is
 * granted
 * 
 */
public class OAuthClientVoter implements AccessDecisionVoter<Object>
{
	private String clientPrefix = "CLIENT_";

	public String getClientPrefix()
	{
		return clientPrefix;
	}

	public void setClientPrefix(final String clientPrefix)
	{
		this.clientPrefix = clientPrefix;
	}

	@Override
	public boolean supports(final ConfigAttribute attribute)
	{
		if ((attribute.getAttribute() != null) && attribute.getAttribute().startsWith(getClientPrefix()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * This implementation supports any type of class, because it does not query the presented secure object.
	 * 
	 * @param clazz
	 *           the secure object
	 * 
	 * @return always <code>true</code>
	 */
	@Override
	public boolean supports(final Class<?> clazz)
	{
		return true;
	}

	@Override
	public int vote(final Authentication authentication, final Object object, final Collection<ConfigAttribute> attributes)
	{
		int result = ACCESS_ABSTAIN;

		if (!(authentication instanceof OAuth2Authentication))
		{
			return ACCESS_ABSTAIN;
		}

		final OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
		final String clientId = oAuth2Authentication.getAuthorizationRequest().getClientId();
		final String clientIdPattern = getClientPrefix() + clientId.toUpperCase();

		for (final ConfigAttribute attribute : attributes)
		{
			if (this.supports(attribute))
			{
				result = ACCESS_DENIED;

				if (attribute.getAttribute().equalsIgnoreCase(clientIdPattern))
				{
					return ACCESS_GRANTED;
				}
			}
		}

		return result;
	}
}
