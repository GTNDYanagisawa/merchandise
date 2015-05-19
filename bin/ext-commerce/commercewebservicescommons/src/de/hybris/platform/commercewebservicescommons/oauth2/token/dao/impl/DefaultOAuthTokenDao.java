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
package de.hybris.platform.commercewebservicescommons.oauth2.token.dao.impl;

import de.hybris.platform.commercewebservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.commercewebservicescommons.model.OAuthRefreshTokenModel;
import de.hybris.platform.commercewebservicescommons.oauth2.token.dao.OAuthTokenDao;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefaultOAuthTokenDao extends AbstractItemDao implements OAuthTokenDao
{

	private final static String FIND_ACCESS_TOKEN_BY_ID = "SELECT {" + OAuthAccessTokenModel.PK + "} FROM {"
			+ OAuthAccessTokenModel._TYPECODE + "} WHERE {" + OAuthAccessTokenModel.TOKENID + "} = ?id ";

	private final static String FIND_REFRESH_TOKEN_BY_ID = "SELECT {" + OAuthRefreshTokenModel.PK + "} FROM {"
			+ OAuthRefreshTokenModel._TYPECODE + "} WHERE {" + OAuthRefreshTokenModel.TOKENID + "} = ?id ";

	private final static String FIND_ACCESS_TOKEN_BY_AUTHENTICATION_ID = "SELECT {" + OAuthAccessTokenModel.PK + "} FROM {"
			+ OAuthAccessTokenModel._TYPECODE + "} WHERE {" + OAuthAccessTokenModel.AUTHENTICATIONID + "} = ?id ";

	private final static String FIND_ACCESS_TOKEN_BY_REFRESH_TOKEN_ID = "SELECT {accessToken:" + OAuthAccessTokenModel.PK
			+ "} FROM {" + OAuthAccessTokenModel._TYPECODE + " as accessToken JOIN " + OAuthRefreshTokenModel._TYPECODE
			+ " as refreshToken ON {accessToken:" + OAuthAccessTokenModel.REFRESHTOKEN + "} = {refreshToken:"
			+ OAuthRefreshTokenModel.PK + "}} WHERE {refreshToken:" + OAuthRefreshTokenModel.TOKENID + "} = ?id ";

	private final static String FIND_ACCESS_TOKEN_BY_CLIENT_ID = "SELECT {" + OAuthAccessTokenModel.PK + "} FROM {"
			+ OAuthAccessTokenModel._TYPECODE + "} WHERE {" + OAuthAccessTokenModel.CLIENTID + "} = ?clientId ";

	private final static String FIND_ACCESS_TOKEN_BY_USER_NAME = "SELECT {" + OAuthAccessTokenModel.PK + "} FROM {"
			+ OAuthAccessTokenModel._TYPECODE + "} WHERE {" + OAuthAccessTokenModel.USERNAME + "} = ?userName ";

	@Override
	public OAuthAccessTokenModel findAccessTokenById(final String id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return searchUnique(new FlexibleSearchQuery(FIND_ACCESS_TOKEN_BY_ID, params));
	}


	@Override
	public OAuthRefreshTokenModel findRefreshTokenById(final String id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return searchUnique(new FlexibleSearchQuery(FIND_REFRESH_TOKEN_BY_ID, params));
	}

	@Override
	public OAuthAccessTokenModel findAccessTokenByRefreshTokenId(final String refreshTokenId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", refreshTokenId);
		return searchUnique(new FlexibleSearchQuery(FIND_ACCESS_TOKEN_BY_REFRESH_TOKEN_ID, params));
	}

	@Override
	public List<OAuthAccessTokenModel> findAccessTokenListByRefreshTokenId(final String refreshTokenId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", refreshTokenId);
		return doSearch(FIND_ACCESS_TOKEN_BY_REFRESH_TOKEN_ID, params, OAuthAccessTokenModel.class);
	}

	@Override
	public OAuthAccessTokenModel findAccessTokenByAuthenticationId(final String authenticationId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", authenticationId);
		return searchUnique(new FlexibleSearchQuery(FIND_ACCESS_TOKEN_BY_AUTHENTICATION_ID, params));
	}

	@Override
	public List<OAuthAccessTokenModel> findAccessTokenListForClient(final String clientId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("clientId", clientId);
		return doSearch(FIND_ACCESS_TOKEN_BY_CLIENT_ID, params, OAuthAccessTokenModel.class);
	}

	@Override
	public List<OAuthAccessTokenModel> findAccessTokenListForUser(final String userName)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		return doSearch(FIND_ACCESS_TOKEN_BY_USER_NAME, params, OAuthAccessTokenModel.class);
	}

	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);
		}

		fQuery.setResultClassList(Collections.singletonList(resultClass));

		final SearchResult<T> searchResult = search(fQuery);
		return searchResult.getResult();
	}
}
