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
package de.hybris.platform.commercewebservicescommons.oauth2.token.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commercewebservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.commercewebservicescommons.model.OAuthRefreshTokenModel;
import de.hybris.platform.commercewebservicescommons.oauth2.token.OAuthTokenService;
import de.hybris.platform.commercewebservicescommons.oauth2.token.dao.OAuthTokenDao;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class DefaultOAuthTokenService extends AbstractBusinessService implements OAuthTokenService
{
	private OAuthTokenDao oauthTokenDao;
	private SearchRestrictionService searchRestrictionService;

	@Override
	public OAuthAccessTokenModel getAccessToken(final String id)
	{
		validateParameterNotNull(id, "Parameter 'id' must not be null!");
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				searchRestrictionService.disableSearchRestrictions();
				try
				{
					return oauthTokenDao.findAccessTokenById(id);
				}
				catch (final ModelNotFoundException e)
				{
					throw new UnknownIdentifierException(e);
				}
			}
		});
	}

	@Override
	public void saveAccessToken(final OAuthAccessTokenModel token)
	{
		validateParameterNotNull(token, "Parameter 'token' must not be null!");
		getModelService().save(token);
	}

	@Override
	public OAuthRefreshTokenModel getRefreshToken(final String id)
	{
		validateParameterNotNull(id, "Parameter 'id' must not be null!");
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				searchRestrictionService.disableSearchRestrictions();
				try
				{
					return oauthTokenDao.findRefreshTokenById(id);
				}
				catch (final ModelNotFoundException e)
				{
					throw new UnknownIdentifierException(e);
				}
			}
		});
	}

	@Override
	public void saveRefreshToken(final OAuthRefreshTokenModel token)
	{
		validateParameterNotNull(token, "Parameter 'token' must not be null!");
		getModelService().save(token);
	}

	@Override
	public void removeAccessToken(final String id)
	{
		validateParameterNotNull(id, "Parameter 'id' must not be null!");
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				searchRestrictionService.disableSearchRestrictions();
				try
				{
					final OAuthAccessTokenModel accessTokenModel = oauthTokenDao.findAccessTokenById(id);
					getModelService().remove(accessTokenModel);
				}
				catch (final ModelNotFoundException e)
				{
					throw new UnknownIdentifierException(e);
				}
			}
		});
	}

	@Override
	public void removeRefreshToken(final String id)
	{
		validateParameterNotNull(id, "Parameter 'id' must not be null!");

		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				searchRestrictionService.disableSearchRestrictions();
				try
				{
					final OAuthRefreshTokenModel refreshTokenModel = oauthTokenDao.findRefreshTokenById(id);
					getModelService().remove(refreshTokenModel);
				}
				catch (final ModelNotFoundException e)
				{
					throw new UnknownIdentifierException(e);
				}
			}
		});
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(final String refreshTokenId)
	{
		validateParameterNotNull(refreshTokenId, "Parameter 'refreshTokenId' must not be null!");
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				searchRestrictionService.disableSearchRestrictions();
				final List<OAuthAccessTokenModel> accessTokenList = oauthTokenDao.findAccessTokenListByRefreshTokenId(refreshTokenId);
				for (final OAuthAccessTokenModel accessToken : accessTokenList)
				{
					getModelService().remove(accessToken);
				}
			}
		});
	}

	@Override
	public OAuthAccessTokenModel getAccessTokenForAuthentication(final String authenticationId)
	{
		validateParameterNotNull(authenticationId, "Parameter 'authenticationId' must not be null!");
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				searchRestrictionService.disableSearchRestrictions();
				try
				{
					return oauthTokenDao.findAccessTokenByAuthenticationId(authenticationId);
				}
				catch (final ModelNotFoundException e)
				{
					throw new UnknownIdentifierException(e);
				}
			}
		});
	}

	@Override
	public List<OAuthAccessTokenModel> getAccessTokensForClient(final String clientId)
	{
		validateParameterNotNull(clientId, "Parameter 'clientId' must not be null!");
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				searchRestrictionService.disableSearchRestrictions();
				return oauthTokenDao.findAccessTokenListForClient(clientId);
			}
		});
	}

	@Override
	public List<OAuthAccessTokenModel> getAccessTokensForUser(final String userName)
	{
		validateParameterNotNull(userName, "Parameter 'clientId' must not be null!");
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				searchRestrictionService.disableSearchRestrictions();
				return oauthTokenDao.findAccessTokenListForUser(userName);
			}
		});
	}

	@Override
	public void removeAccessTokenForAuthentication(final String authenticationId)
	{
		validateParameterNotNull(authenticationId, "Parameter 'authenticationId' must not be null!");
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				searchRestrictionService.disableSearchRestrictions();
				final OAuthAccessTokenModel accessToken = oauthTokenDao.findAccessTokenByAuthenticationId(authenticationId);
				getModelService().remove(accessToken);
			}
		});
	}

	@Override
	public OAuthRefreshTokenModel saveRefreshToken(final String refreshTokenId, final Object refreshToken,
			final Object authentication)
	{
		validateParameterNotNull(refreshTokenId, "Parameter 'refreshTokenId' must not be null!");

		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				searchRestrictionService.disableSearchRestrictions();
				OAuthRefreshTokenModel refreshTokenModel;
				try
				{
					refreshTokenModel = oauthTokenDao.findRefreshTokenById(refreshTokenId);
				}
				catch (final ModelNotFoundException e)
				{
					refreshTokenModel = getModelService().create(OAuthRefreshTokenModel.class);
					refreshTokenModel.setTokenId(refreshTokenId);
				}

				refreshTokenModel.setToken(refreshToken);
				refreshTokenModel.setAuthentication(authentication);
				getModelService().save(refreshTokenModel);
				return refreshTokenModel;
			}
		});
	}

	@Override
	public OAuthAccessTokenModel saveAccessToken(final String accessTokenId, final Object accessToken,
			final String authenticationId, final Object authentication, final String userName, final String clientId,
			final OAuthRefreshTokenModel refreshTokenModel)
	{
		validateParameterNotNull(accessTokenId, "Parameter 'accessTokenId' must not be null!");

		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				searchRestrictionService.disableSearchRestrictions();
				OAuthAccessTokenModel accessTokenModel;
				try
				{
					accessTokenModel = oauthTokenDao.findAccessTokenById(accessTokenId);
				}
				catch (final ModelNotFoundException e)
				{
					accessTokenModel = getModelService().create(OAuthAccessTokenModel.class);
					accessTokenModel.setTokenId(accessTokenId);
				}

				accessTokenModel.setToken(accessToken);
				accessTokenModel.setAuthenticationId(authenticationId);
				accessTokenModel.setAuthentication(authentication);
				accessTokenModel.setUserName(userName);
				accessTokenModel.setClientId(clientId);
				accessTokenModel.setRefreshToken(refreshTokenModel);
				getModelService().save(accessTokenModel);
				return accessTokenModel;
			}
		});
	}

	public OAuthTokenDao getOauthTokenDao()
	{
		return oauthTokenDao;
	}

	@Required
	public void setOauthTokenDao(final OAuthTokenDao oauthTokenDao)
	{
		this.oauthTokenDao = oauthTokenDao;
	}

	public SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

}
