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
package de.hybris.platform.commercewebservicescommons.oauth2.token;

import de.hybris.platform.commercewebservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.commercewebservicescommons.model.OAuthRefreshTokenModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;


/**
 * Service for managing OAuth access and refresh tokens
 * 
 * @spring.bean oauthTokenService
 * 
 */
public interface OAuthTokenService
{
	/**
	 * Returns access token for given identifier (key)
	 * 
	 * @param id
	 *           token identifier
	 * @return access token with given id
	 * @throws IllegalArgumentException
	 *            when 'id' parameter is null
	 * @throws UnknownIdentifierException
	 *            when access token with given id doesn't exist
	 */
	OAuthAccessTokenModel getAccessToken(String id);

	/**
	 * Save access token
	 * 
	 * @param accessTokenModel
	 *           Access token model to save
	 */
	void saveAccessToken(OAuthAccessTokenModel accessTokenModel);

	/**
	 * Saves access token attributes in access token model.<br/>
	 * If access token model with given id doesn't exist, new access token model is created.
	 * 
	 * @param accessTokenId
	 *           Access token identifier
	 * @param accessToken
	 *           Access token object
	 * @param authenticationId
	 *           Authentication identifier
	 * @param authentication
	 *           Authentication object
	 * @param userName
	 *           User name
	 * @param clientId
	 *           Client identifier
	 * @param refreshTokenModel
	 *           Refresh token model belong
	 * @return Saved access token model
	 */
	OAuthAccessTokenModel saveAccessToken(String accessTokenId, Object accessToken, String authenticationId,
			Object authentication, String userName, String clientId, OAuthRefreshTokenModel refreshTokenModel);

	/**
	 * Remove access token
	 * 
	 * @param id
	 *           Access token identifier
	 */
	void removeAccessToken(String id);

	/**
	 * Returns refresh token for given identifier (key)
	 * 
	 * @param id
	 *           refresh token identifier
	 * @return refresh token with given id
	 * @throws IllegalArgumentException
	 *            when 'id' parameter is null
	 * @throws UnknownIdentifierException
	 *            when refresh token with given id doesn't exist
	 */
	OAuthRefreshTokenModel getRefreshToken(String id);

	/**
	 * Save refresh token model
	 * 
	 * @param refreshTokenModel
	 *           Model to save
	 */
	void saveRefreshToken(OAuthRefreshTokenModel refreshTokenModel);

	/**
	 * Saves refresh token attributes in refresh token model.<br/>
	 * If refresh token model for given id doesn't exist, new refresh token model is created.
	 * 
	 * @param refreshTokenId
	 *           Refresh token identifier
	 * @param refreshToken
	 *           Refresh token object
	 * @param authentication
	 *           Authentication object
	 * @return Saved refresh token model
	 */
	OAuthRefreshTokenModel saveRefreshToken(String refreshTokenId, Object refreshToken, Object authentication);

	/**
	 * Remove refresh token with given identifier
	 * 
	 * @param id
	 *           - refresh token identifier (key)
	 */
	void removeRefreshToken(String id);

	/**
	 * Removes access tokens related to refresh token
	 * 
	 * @param refreshTokenId
	 *           Refresh token identifier
	 */
	void removeAccessTokenUsingRefreshToken(String refreshTokenId);

	/**
	 * Returns access tokens for client
	 * 
	 * @param clientId
	 *           Client identifier
	 * @return List of access tokens for client or empty list
	 */
	List<OAuthAccessTokenModel> getAccessTokensForClient(String clientId);

	/**
	 * Returns access tokens for user with given user name
	 * 
	 * @param userName
	 *           Sser name
	 * @return List of access tokens for user or empty list
	 */
	List<OAuthAccessTokenModel> getAccessTokensForUser(String userName);

	/**
	 * Returns access token for given authentication
	 * 
	 * @param authenticationId
	 *           authentication identifier
	 * @return access token
	 * @throws IllegalArgumentException
	 *            when authenticationId is null
	 * @throws UnknownIdentifierException
	 *            when there is no access token related to given authentication
	 * @throws AmbiguousIdentifierException
	 *            when there is multiple access token related to given authentication
	 */
	OAuthAccessTokenModel getAccessTokenForAuthentication(String authenticationId);

	/**
	 * Removes access tokens related to authentication
	 * 
	 * @param authenticationId
	 *           authentication identifier
	 * 
	 * @throws IllegalArgumentException
	 *            when authenticationId is null
	 */
	void removeAccessTokenForAuthentication(String authenticationId);

}
