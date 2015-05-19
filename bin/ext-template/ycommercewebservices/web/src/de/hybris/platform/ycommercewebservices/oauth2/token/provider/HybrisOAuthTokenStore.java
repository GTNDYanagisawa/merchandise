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

import de.hybris.platform.commercewebservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.commercewebservicescommons.model.OAuthRefreshTokenModel;
import de.hybris.platform.commercewebservicescommons.oauth2.token.OAuthTokenService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;


public class HybrisOAuthTokenStore implements TokenStore
{
	private static final Logger LOG = Logger.getLogger(HybrisOAuthTokenStore.class);
	private final AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
	private OAuthTokenService oauthTokenService;

	@Override
	public OAuth2AccessToken getAccessToken(final OAuth2Authentication authentication)
	{
		OAuth2AccessToken accessToken = null;
		OAuthAccessTokenModel accessTokenModel = null;
		final String authenticationId = authenticationKeyGenerator.extractKey(authentication);
		try
		{
			accessTokenModel = oauthTokenService.getAccessTokenForAuthentication(authenticationId);
			accessToken = deserializeAccessToken((byte[]) accessTokenModel.getToken());
		}
		catch (final ClassCastException e)
		{
			LOG.warn("Could not extract access token for authentication " + authentication);
			oauthTokenService.removeAccessTokenForAuthentication(authenticationId);
		}
		catch (final UnknownIdentifierException e)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.debug("Failed to find access token for authentication " + authentication);
			}
		}

		if (accessToken != null
				&& !authenticationId.equals(authenticationKeyGenerator.extractKey(deserializeAuthentication((byte[]) accessTokenModel
						.getAuthentication()))))
		{
			removeAccessToken(accessToken.getValue());
			// Keep the store consistent (maybe the same user is represented by this authentication but the details have
			// changed)
			storeAccessToken(accessToken, authentication);
		}
		return accessToken;
	}

	@Override
	public void storeAccessToken(final OAuth2AccessToken token, final OAuth2Authentication authentication)
	{
		OAuthRefreshTokenModel refreshTokenModel = null;
		if (token.getRefreshToken() != null)
		{
			final String refreshTokenKey = extractTokenKey(token.getRefreshToken().getValue());
			try
			{
				refreshTokenModel = oauthTokenService.getRefreshToken(refreshTokenKey);
			}
			catch (final UnknownIdentifierException e)
			{
				refreshTokenModel = oauthTokenService.saveRefreshToken(refreshTokenKey,
						serializeRefreshToken(token.getRefreshToken()), serializeAuthentication(authentication));
			}
		}

		oauthTokenService.saveAccessToken(extractTokenKey(token.getValue()), serializeAccessToken(token),
				authenticationKeyGenerator.extractKey(authentication), serializeAuthentication(authentication), authentication
						.isClientOnly() ? null : authentication.getName(), authentication.getAuthorizationRequest().getClientId(),
				refreshTokenModel);
	}

	@Override
	public OAuth2AccessToken readAccessToken(final String tokenValue)
	{
		OAuth2AccessToken accessToken = null;

		try
		{
			final OAuthAccessTokenModel accessTokenModel = oauthTokenService.getAccessToken(extractTokenKey(tokenValue));
			accessToken = deserializeAccessToken((byte[]) accessTokenModel.getToken());
		}
		catch (final ClassCastException e)
		{
			LOG.warn("Failed to deserialize access token for  " + tokenValue);
			removeAccessToken(tokenValue);
		}
		catch (final UnknownIdentifierException e)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info("Failed to find access token for token " + tokenValue);
			}
		}

		return accessToken;
	}

	@Override
	public void removeAccessToken(final OAuth2AccessToken token)
	{
		removeAccessToken(token.getValue());
	}

	public void removeAccessToken(final String tokenValue)
	{
		oauthTokenService.removeAccessToken(extractTokenKey(tokenValue));
	}

	@Override
	public OAuth2Authentication readAuthentication(final OAuth2AccessToken token)
	{
		return readAuthentication(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(final String token)
	{
		OAuth2Authentication authentication = null;

		try
		{
			final OAuthAccessTokenModel accessTokenModel = oauthTokenService.getAccessToken(extractTokenKey(token));
			authentication = deserializeAuthentication((byte[]) accessTokenModel.getAuthentication());
		}
		catch (final ClassCastException e)
		{
			LOG.warn("Failed to deserialize authentication for " + token);
			removeAccessToken(token);
		}
		catch (final UnknownIdentifierException e)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info("Failed to find authentication for token " + token);
			}
		}
		return authentication;
	}

	@Override
	public void storeRefreshToken(final OAuth2RefreshToken refreshToken, final OAuth2Authentication authentication)
	{
		oauthTokenService.saveRefreshToken(extractTokenKey(refreshToken.getValue()), serializeRefreshToken(refreshToken),
				serializeAuthentication(authentication));
	}

	@Override
	public OAuth2RefreshToken readRefreshToken(final String token)
	{
		OAuth2RefreshToken refreshToken = null;

		try
		{
			final OAuthRefreshTokenModel refreshTokenModel = oauthTokenService.getRefreshToken(extractTokenKey(token));
			refreshToken = deserializeRefreshToken((byte[]) refreshTokenModel.getToken());
		}
		catch (final ClassCastException e)
		{
			LOG.warn("Failed to deserialize refresh token for token " + token);
			removeRefreshToken(token);
		}
		catch (final UnknownIdentifierException e)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info("Failed to find refresh token for token " + token);
			}
		}

		return refreshToken;
	}

	@Override
	public void removeRefreshToken(final OAuth2RefreshToken token)
	{
		removeRefreshToken(token.getValue());
	}

	public void removeRefreshToken(final String token)
	{
		oauthTokenService.removeRefreshToken(extractTokenKey(token));
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(final OAuth2RefreshToken token)
	{
		return readAuthenticationForRefreshToken(token.getValue());
	}

	public OAuth2Authentication readAuthenticationForRefreshToken(final String value)
	{
		OAuth2Authentication authentication = null;
		try
		{
			final OAuthRefreshTokenModel refreshTokenModel = oauthTokenService.getRefreshToken(extractTokenKey(value));
			authentication = deserializeAuthentication((byte[]) refreshTokenModel.getAuthentication());
		}
		catch (final ClassCastException e)
		{
			LOG.warn("Failed to deserialize authentication for refresh token " + value);
			removeRefreshToken(value);
		}
		catch (final UnknownIdentifierException e)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info("Failed to find refresh token for " + value);
			}
		}

		return authentication;
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(final OAuth2RefreshToken refreshToken)
	{
		removeAccessTokenUsingRefreshToken(refreshToken.getValue());
	}

	public void removeAccessTokenUsingRefreshToken(final String refreshToken)
	{
		oauthTokenService.removeAccessTokenUsingRefreshToken(extractTokenKey(refreshToken));
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(final String clientId)
	{
		final List<OAuth2AccessToken> accessTokenList = new ArrayList<OAuth2AccessToken>();
		final List<OAuthAccessTokenModel> accessTokenModelList = oauthTokenService.getAccessTokensForClient(clientId);
		OAuth2AccessToken accessToken;
		for (final OAuthAccessTokenModel accessTokenModel : accessTokenModelList)
		{
			try
			{
				accessToken = deserializeAccessToken((byte[]) accessTokenModel.getToken());
				accessTokenList.add(accessToken);
			}
			catch (final ClassCastException e)
			{
				//invalid token so we remove it from database
				LOG.warn("Failed to deserialize access token for client : " + clientId);
				oauthTokenService.removeAccessToken(accessTokenModel.getTokenId());
			}
		}

		return accessTokenList;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByUserName(final String userName)
	{
		final List<OAuth2AccessToken> accessTokenList = new ArrayList<OAuth2AccessToken>();

		final List<OAuthAccessTokenModel> accessTokenModelList = oauthTokenService.getAccessTokensForUser(userName);
		OAuth2AccessToken accessToken;
		for (final OAuthAccessTokenModel accessTokenModel : accessTokenModelList)
		{
			try
			{
				accessToken = deserializeAccessToken((byte[]) accessTokenModel.getToken());
				accessTokenList.add(accessToken);
			}
			catch (final ClassCastException e)
			{
				//invalid token so we remove it from database
				LOG.warn("Failed to deserialize access token for user : " + userName);
				oauthTokenService.removeAccessToken(accessTokenModel.getTokenId());
			}
		}

		return accessTokenList;
	}

	protected String extractTokenKey(final String value)
	{
		return value;
		//		if (value == null)
		//		{
		//			return null;
		//		}
		//		MessageDigest digest;
		//		try
		//		{
		//			digest = MessageDigest.getInstance("MD5");
		//		}
		//		catch (final NoSuchAlgorithmException e)
		//		{
		//			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		//		}
		//
		//		try
		//		{
		//			final byte[] bytes = digest.digest(value.getBytes("UTF-8"));
		//			return String.format("%032x", new BigInteger(1, bytes));
		//		}
		//		catch (final UnsupportedEncodingException e)
		//		{
		//			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		//		}
	}

	protected byte[] serializeAccessToken(final OAuth2AccessToken token)
	{
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeRefreshToken(final OAuth2RefreshToken token)
	{
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeAuthentication(final OAuth2Authentication authentication)
	{
		return SerializationUtils.serialize(authentication);
	}

	protected OAuth2AccessToken deserializeAccessToken(final byte[] token)
	{
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2RefreshToken deserializeRefreshToken(final byte[] token)
	{
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2Authentication deserializeAuthentication(final byte[] authentication)
	{
		return SerializationUtils.deserialize(authentication);
	}

	public OAuthTokenService getOauthTokenService()
	{
		return oauthTokenService;
	}

	@Required
	public void setOauthTokenService(final OAuthTokenService oauthTokenService)
	{
		this.oauthTokenService = oauthTokenService;
	}

}
