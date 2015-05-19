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
package de.hybris.platform.commercewebservicescommons.oauth2.token.dao;

import de.hybris.platform.commercewebservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.commercewebservicescommons.model.OAuthRefreshTokenModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * Dao for {@link OAuthAccessTokenModel} and {@link OAuthRefreshTokenModel} access.
 * 
 * @spring.bean oauthTokenDao
 * 
 */
public interface OAuthTokenDao extends Dao
{

	/**
	 * Finds access token by given identifier (key)
	 * 
	 * @param id
	 *           token identifier
	 * @return access token with given id
	 * @throws ModelNotFoundException
	 *            if access token with given id doesn't exist
	 * 
	 */
	public OAuthAccessTokenModel findAccessTokenById(String id);

	/**
	 * Finds refresh token by given identifier (key)
	 * 
	 * @param id
	 *           refresh token identifier
	 * @return refresh token with given id
	 * @throws ModelNotFoundException
	 *            if refresh token with given id doesn't exist
	 */
	public OAuthRefreshTokenModel findRefreshTokenById(String id);

	/**
	 * Finds access token by identifier of the refresh token
	 * 
	 * @param refreshTokenId
	 *           refresh token identifier
	 * @return access token
	 * @throws ModelNotFoundException
	 *            if there is no access token related to refresh token or refresh token with given identifier doesn't
	 *            exist
	 * @throws AmbiguousIdentifierException
	 *            if there is multiple access token related to refresh token
	 */
	public OAuthAccessTokenModel findAccessTokenByRefreshTokenId(String refreshTokenId);

	/**
	 * Finds access token by authentication identifier (key)
	 * 
	 * @param authenticationId
	 *           authentication identifier
	 * @return access token
	 * @throws ModelNotFoundException
	 *            if there is no access token related to given authentication
	 * @throws AmbiguousIdentifierException
	 *            if there is multiple access token related to given authentication
	 */
	public OAuthAccessTokenModel findAccessTokenByAuthenticationId(String authenticationId);

	/**
	 * Finds access tokens related to refresh token with given identifier
	 * 
	 * @param refreshTokenId
	 *           - refresh token identifier
	 * @return list of access tokens related to refresh token or empty list
	 */
	public List<OAuthAccessTokenModel> findAccessTokenListByRefreshTokenId(String refreshTokenId);

	/**
	 * Finds access tokens for client
	 * 
	 * @param clientId
	 *           client identifier
	 * @return list of access tokens for client or empty list
	 */
	public List<OAuthAccessTokenModel> findAccessTokenListForClient(String clientId);

	/**
	 * Finds access tokens for user with given user name
	 * 
	 * @param userName
	 *           user name
	 * @return list of access tokens for user or empty list
	 */
	public List<OAuthAccessTokenModel> findAccessTokenListForUser(String userName);
}
