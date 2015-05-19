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
package de.hybris.platform.ycommercewebservices.oauth2.token.provider;

import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;


public class HybrisOAuthTokenServices extends DefaultTokenServices
{
	private static final Logger LOG = Logger.getLogger(HybrisOAuthTokenServices.class);

	@Override
	public OAuth2AccessToken createAccessToken(final OAuth2Authentication authentication) throws AuthenticationException
	{
		try
		{
			return super.createAccessToken(authentication);
		}
		catch (final ModelSavingException e) //in case when other client was faster in saving access token - try to get token again
		{
			LOG.debug("HybrisOAuthTokenServices->createAccessToken : ModelSavingException : " + e.getMessage());
			return super.createAccessToken(authentication);
		}
		catch (final ModelRemovalException e) //in case when other client was faster in removing expired token - try to get token again
		{
			LOG.debug("HybrisOAuthTokenServices->createAccessToken : ModelRemovalException :" + e.getMessage());
			return super.createAccessToken(authentication);
		}
	}
}
