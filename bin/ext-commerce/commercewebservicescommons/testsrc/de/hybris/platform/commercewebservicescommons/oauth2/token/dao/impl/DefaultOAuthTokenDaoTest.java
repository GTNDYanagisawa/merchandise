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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercewebservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.commercewebservicescommons.model.OAuthRefreshTokenModel;
import de.hybris.platform.commercewebservicescommons.oauth2.token.dao.OAuthTokenDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultOAuthTokenDaoTest extends ServicelayerTransactionalTest
{
	protected static String ACCESS_TOKEN_ID = "1";
	protected static String ACCESS_TOKEN_OBJECT = "accessTokenObject";
	protected static String REFRESH_TOKEN_ID = "2";
	protected static String REFRESH_TOKEN_OBJECT = "refreshTokenObject";
	protected static String AUTHENTICATION_ID = "userAuthId";
	protected static String AUTHENTICATION_OBJECT = "userAuthObject";
	protected static String CLIENT_ID = "clientId";
	protected static String USER_NAME = "userName";

	protected static String ACCESS_TOKEN_ID_2 = "3";
	protected static String ACCESS_TOKEN_OBJECT_2 = "accessTokenObject2";
	protected static String REFRESH_TOKEN_ID_2 = "4";
	protected static String REFRESH_TOKEN_OBJECT_2 = "refreshTokenObject2";
	protected static String AUTHENTICATION_ID_2 = "userAuthId2";
	protected static String AUTHENTICATION_OBJECT_2 = "userAuthObject2";
	protected static String CLIENT_ID_2 = "clientId2";
	protected static String USER_NAME_2 = "userName2";

	protected static String NOT_EXISTING_ID = "notExistingId";

	@Resource(name = "defaultOAuthTokenDao")
	protected OAuthTokenDao oauthTokenDao;

	@Resource
	protected ModelService modelService;

	@Before
	public void setUp()
	{
		createAccessToken(ACCESS_TOKEN_ID, ACCESS_TOKEN_OBJECT, AUTHENTICATION_ID, AUTHENTICATION_OBJECT, CLIENT_ID, USER_NAME,
				REFRESH_TOKEN_ID, REFRESH_TOKEN_OBJECT);
	}

	private void createAccessToken(final String accessTokenId, final Object accessTokenObject, final String authenticationId,
			final Object authenticationObject, final String clientId, final String userName, final String refreshTokenId,
			final Object refreshTokenObject)
	{
		final OAuthAccessTokenModel accessToken = modelService.create(OAuthAccessTokenModel.class);
		accessToken.setTokenId(accessTokenId);
		accessToken.setToken(accessTokenObject);
		accessToken.setAuthenticationId(authenticationId);
		accessToken.setAuthentication(authenticationObject);
		accessToken.setClientId(clientId);
		accessToken.setUserName(userName);

		final OAuthRefreshTokenModel refreshToken = modelService.create(OAuthRefreshTokenModel.class);
		refreshToken.setTokenId(refreshTokenId);
		refreshToken.setToken(refreshTokenObject);
		refreshToken.setAuthentication(authenticationObject);
		accessToken.setRefreshToken(refreshToken);
		modelService.save(accessToken);
	}

	private void createAccessToken(final String accessTokenId, final Object accessTokenObject, final String authenticationId,
			final Object authenticationObject, final String clientId, final String userName,
			final OAuthRefreshTokenModel refreshToken)
	{
		final OAuthAccessTokenModel accessToken = modelService.create(OAuthAccessTokenModel.class);
		accessToken.setTokenId(accessTokenId);
		accessToken.setToken(accessTokenObject);
		accessToken.setAuthenticationId(authenticationId);
		accessToken.setAuthentication(authenticationObject);
		accessToken.setClientId(clientId);
		accessToken.setUserName(userName);

		accessToken.setRefreshToken(refreshToken);
		modelService.save(accessToken);
	}

	@Test
	public void findAccessTokenByIdTest()
	{
		final OAuthAccessTokenModel accessToken = oauthTokenDao.findAccessTokenById(ACCESS_TOKEN_ID);
		Assert.assertEquals(ACCESS_TOKEN_ID, accessToken.getTokenId());
		Assert.assertEquals(ACCESS_TOKEN_OBJECT, accessToken.getToken());
		Assert.assertEquals(AUTHENTICATION_ID, accessToken.getAuthenticationId());
		Assert.assertEquals(AUTHENTICATION_OBJECT, accessToken.getAuthentication());
		Assert.assertEquals(CLIENT_ID, accessToken.getClientId());
		Assert.assertEquals(USER_NAME, accessToken.getUserName());
		Assert.assertNotNull(accessToken.getRefreshToken());
		Assert.assertEquals(REFRESH_TOKEN_ID, accessToken.getRefreshToken().getTokenId());
		Assert.assertEquals(REFRESH_TOKEN_OBJECT, accessToken.getRefreshToken().getToken());
		Assert.assertEquals(AUTHENTICATION_OBJECT, accessToken.getRefreshToken().getAuthentication());
	}

	@Test(expected = ModelNotFoundException.class)
	public void findNotExistingAccessTokenTest()
	{
		oauthTokenDao.findAccessTokenById(NOT_EXISTING_ID);
	}

	@Test
	public void findRefreshTokenByIdTest()
	{
		final OAuthRefreshTokenModel refreshToken = oauthTokenDao.findRefreshTokenById(REFRESH_TOKEN_ID);
		Assert.assertEquals(REFRESH_TOKEN_ID, refreshToken.getTokenId());
		Assert.assertEquals(REFRESH_TOKEN_OBJECT, refreshToken.getToken());
		Assert.assertEquals(AUTHENTICATION_OBJECT, refreshToken.getAuthentication());
	}

	@Test(expected = ModelNotFoundException.class)
	public void findNotExistingRefreshTokenTest()
	{
		oauthTokenDao.findRefreshTokenById(NOT_EXISTING_ID);
	}

	@Test
	public void findAccessTokenByRefreshTokenIdTest()
	{
		final OAuthAccessTokenModel accessToken = oauthTokenDao.findAccessTokenByRefreshTokenId(REFRESH_TOKEN_ID);
		Assert.assertEquals(ACCESS_TOKEN_ID, accessToken.getTokenId());
		Assert.assertNotNull(accessToken.getRefreshToken());
		Assert.assertEquals(REFRESH_TOKEN_ID, accessToken.getRefreshToken().getTokenId());
	}

	@Test(expected = ModelNotFoundException.class)
	public void findAccessTokenForNotExistingRefreshTokenTest()
	{
		oauthTokenDao.findAccessTokenByRefreshTokenId(NOT_EXISTING_ID);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void findMultipleAccessTokenByRefreshTokenIdTest()
	{
		final OAuthRefreshTokenModel refreshToken = oauthTokenDao.findRefreshTokenById(REFRESH_TOKEN_ID);
		createAccessToken(ACCESS_TOKEN_ID_2, ACCESS_TOKEN_OBJECT_2, AUTHENTICATION_ID_2, AUTHENTICATION_OBJECT_2, CLIENT_ID_2,
				USER_NAME_2, refreshToken);

		oauthTokenDao.findAccessTokenByRefreshTokenId(REFRESH_TOKEN_ID);
	}

	@Test
	public void findAccessTokenListByRefreshTokenIdTest()
	{
		List<OAuthAccessTokenModel> accessTokenList = oauthTokenDao.findAccessTokenListByRefreshTokenId(REFRESH_TOKEN_ID);
		Assert.assertEquals(1, accessTokenList.size());
		Assert.assertEquals(ACCESS_TOKEN_ID, accessTokenList.get(0).getTokenId());
		Assert.assertNotNull(accessTokenList.get(0).getRefreshToken());
		Assert.assertEquals(REFRESH_TOKEN_ID, accessTokenList.get(0).getRefreshToken().getTokenId());

		createAccessToken(ACCESS_TOKEN_ID_2, ACCESS_TOKEN_OBJECT_2, AUTHENTICATION_ID_2, AUTHENTICATION_OBJECT_2, CLIENT_ID_2,
				USER_NAME_2, accessTokenList.get(0).getRefreshToken());

		accessTokenList = oauthTokenDao.findAccessTokenListByRefreshTokenId(REFRESH_TOKEN_ID);
		Assert.assertEquals(2, accessTokenList.size());
		Assert.assertNotNull(accessTokenList.get(0).getRefreshToken());
		Assert.assertEquals(REFRESH_TOKEN_ID, accessTokenList.get(0).getRefreshToken().getTokenId());
		Assert.assertNotNull(accessTokenList.get(1).getRefreshToken());
		Assert.assertEquals(REFRESH_TOKEN_ID, accessTokenList.get(1).getRefreshToken().getTokenId());
	}

	@Test
	public void findAccessTokenListForNotExistingRefreshTokenTest()
	{
		final List<OAuthAccessTokenModel> accessTokenList = oauthTokenDao.findAccessTokenListByRefreshTokenId(NOT_EXISTING_ID);
		Assert.assertTrue(accessTokenList.isEmpty());
	}

	@Test(expected = ModelNotFoundException.class)
	public void findAccessTokenForNotExistingAuthenticationTest()
	{
		oauthTokenDao.findAccessTokenByAuthenticationId(NOT_EXISTING_ID);
	}

	@Test
	public void findAccessTokenListForClientTest()
	{
		List<OAuthAccessTokenModel> accessTokenList = oauthTokenDao.findAccessTokenListForClient(CLIENT_ID);
		Assert.assertEquals(1, accessTokenList.size());
		Assert.assertEquals(ACCESS_TOKEN_ID, accessTokenList.get(0).getTokenId());
		Assert.assertEquals(CLIENT_ID, accessTokenList.get(0).getClientId());

		createAccessToken(ACCESS_TOKEN_ID_2, ACCESS_TOKEN_OBJECT_2, AUTHENTICATION_ID_2, AUTHENTICATION_OBJECT_2, CLIENT_ID,
				USER_NAME_2, REFRESH_TOKEN_ID_2, REFRESH_TOKEN_OBJECT_2);

		accessTokenList = oauthTokenDao.findAccessTokenListForClient(CLIENT_ID);
		Assert.assertEquals(2, accessTokenList.size());
		Assert.assertEquals(CLIENT_ID, accessTokenList.get(0).getClientId());
		Assert.assertEquals(CLIENT_ID, accessTokenList.get(1).getClientId());
	}

	@Test
	public void findAccessTokenListForNotExistingClientTest()
	{
		final List<OAuthAccessTokenModel> accessTokenList = oauthTokenDao.findAccessTokenListForClient(NOT_EXISTING_ID);
		Assert.assertTrue(accessTokenList.isEmpty());
	}

	@Test
	public void findAccessTokenListForUserTest()
	{
		List<OAuthAccessTokenModel> accessTokenList = oauthTokenDao.findAccessTokenListForUser(USER_NAME);
		Assert.assertEquals(1, accessTokenList.size());
		Assert.assertEquals(ACCESS_TOKEN_ID, accessTokenList.get(0).getTokenId());
		Assert.assertEquals(USER_NAME, accessTokenList.get(0).getUserName());

		createAccessToken(ACCESS_TOKEN_ID_2, ACCESS_TOKEN_OBJECT_2, AUTHENTICATION_ID_2, AUTHENTICATION_OBJECT_2, CLIENT_ID_2,
				USER_NAME, REFRESH_TOKEN_ID_2, REFRESH_TOKEN_OBJECT_2);

		accessTokenList = oauthTokenDao.findAccessTokenListForUser(USER_NAME);
		Assert.assertEquals(2, accessTokenList.size());
		Assert.assertEquals(USER_NAME, accessTokenList.get(0).getUserName());
		Assert.assertEquals(USER_NAME, accessTokenList.get(1).getUserName());
	}

	@Test
	public void findAccessTokenListForNotExistingUserNameTest()
	{
		final List<OAuthAccessTokenModel> accessTokenList = oauthTokenDao.findAccessTokenListForUser(NOT_EXISTING_ID);
		Assert.assertTrue(accessTokenList.isEmpty());
	}
}
