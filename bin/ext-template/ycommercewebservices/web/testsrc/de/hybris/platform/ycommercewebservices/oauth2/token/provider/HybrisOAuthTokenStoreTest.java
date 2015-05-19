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
package de.hybris.platform.ycommercewebservices.oauth2.token.provider;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercewebservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.commercewebservicescommons.model.OAuthRefreshTokenModel;
import de.hybris.platform.commercewebservicescommons.oauth2.token.OAuthTokenService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@UnitTest
public class HybrisOAuthTokenStoreTest
{
	protected static String ACCESS_TOKEN_ID = "1";
	protected static String ACCESS_TOKEN_VALUE = "accessTokenValue";
	protected static String REFRESH_TOKEN_ID = "2";
	protected static String REFRESH_TOKEN_VALUE = "refreshTokenValue";
	protected static String AUTHENTICATION_ID = "userAuthId";
	protected static String CLIENT_ID = "clientId";
	protected static String USER_NAME = "userName";

	protected static String NOT_EXISTING_TOKEN = "notExisitngToken";

	@Mock
	OAuthTokenService oauthTokenService;
	HybrisOAuthTokenStore tokenStore;

	DefaultOAuth2AccessToken oauthAccessToken;
	OAuth2RefreshToken oauthRefreshToken;
	OAuth2Authentication oauthAuthentication;
	Authentication authentication;

	OAuthAccessTokenModel accessTokenModel;
	OAuthRefreshTokenModel refreshTokenModel;
	List<OAuthAccessTokenModel> accessTokenModelList;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		tokenStore = new HybrisOAuthTokenStore();
		tokenStore.setOauthTokenService(oauthTokenService);

		oauthRefreshToken = new DefaultOAuth2RefreshToken(REFRESH_TOKEN_VALUE);
		oauthAccessToken = new DefaultOAuth2AccessToken(ACCESS_TOKEN_VALUE);
		oauthAccessToken.setRefreshToken(oauthRefreshToken);
		authentication = new MockAuthentication(USER_NAME, false);
		oauthAuthentication = new OAuth2Authentication(new DefaultAuthorizationRequest(CLIENT_ID, null), authentication);

		accessTokenModel = new OAuthAccessTokenModel();
		accessTokenModel.setTokenId(ACCESS_TOKEN_ID);
		accessTokenModel.setToken(SerializationUtils.serialize(oauthAccessToken));
		accessTokenModel.setAuthenticationId(AUTHENTICATION_ID);
		accessTokenModel.setAuthentication(SerializationUtils.serialize(oauthAuthentication));
		accessTokenModel.setClientId(CLIENT_ID);
		accessTokenModel.setUserName(USER_NAME);
		refreshTokenModel = new OAuthRefreshTokenModel();
		refreshTokenModel.setTokenId(REFRESH_TOKEN_ID);
		refreshTokenModel.setToken(SerializationUtils.serialize(oauthRefreshToken));
		refreshTokenModel.setAuthentication(SerializationUtils.serialize(oauthAuthentication));
		accessTokenModel.setRefreshToken(refreshTokenModel);

		accessTokenModelList = new ArrayList<OAuthAccessTokenModel>();
		accessTokenModelList.add(accessTokenModel);
	}

	@Test
	public void testReadAuthentication()
	{
		given(oauthTokenService.getAccessToken(anyString())).willReturn(accessTokenModel);
		final OAuth2Authentication result = tokenStore.readAuthentication(oauthAccessToken);
		Assert.assertEquals(oauthAuthentication, result);
	}

	@Test
	public void testReadAuthenticationForNotExistingToken()
	{
		given(oauthTokenService.getAccessToken(anyString())).willThrow(new UnknownIdentifierException("Unknown identifier"));
		assertNull(tokenStore.readAuthentication(NOT_EXISTING_TOKEN));
	}

	@Test
	public void testReadAccessToken()
	{
		given(oauthTokenService.getAccessToken(anyString())).willReturn(accessTokenModel);
		final OAuth2AccessToken result = tokenStore.readAccessToken(ACCESS_TOKEN_VALUE);
		Assert.assertEquals(oauthAccessToken, result);
	}

	@Test
	public void testReadNotExistingAccessToken()
	{
		given(oauthTokenService.getAccessToken(anyString())).willThrow(new UnknownIdentifierException("Unknown identifier"));
		assertNull(tokenStore.readAccessToken(NOT_EXISTING_TOKEN));
	}

	@Test
	public void testStoreAccessToken()
	{
		given(oauthTokenService.getRefreshToken(anyString())).willThrow(new UnknownIdentifierException("Unknown identifier"));

		tokenStore.storeAccessToken(oauthAccessToken, oauthAuthentication);

		verify(oauthTokenService, times(1)).saveRefreshToken(anyString(), any(), any());
		verify(oauthTokenService, times(1)).saveAccessToken(anyString(), any(byte[].class), anyString(), any(byte[].class),
				eq(USER_NAME), eq(CLIENT_ID), any(OAuthRefreshTokenModel.class));
	}

	@Test
	public void testRemoveAccessToken()
	{
		tokenStore.removeAccessToken(oauthAccessToken);
		verify(oauthTokenService, times(1)).removeAccessToken(anyString());
	}

	@Test
	public void testFindAccessTokensByUserName()
	{
		given(oauthTokenService.getAccessTokensForUser(USER_NAME)).willReturn(accessTokenModelList);
		final Collection<OAuth2AccessToken> result = tokenStore.findTokensByUserName(USER_NAME);
		assertEquals(1, result.size());
		assertEquals(oauthAccessToken, result.toArray()[0]);
	}

	@Test
	public void testFindAccessTokensByClientId()
	{
		given(oauthTokenService.getAccessTokensForClient(CLIENT_ID)).willReturn(accessTokenModelList);
		final Collection<OAuth2AccessToken> result = tokenStore.findTokensByClientId(CLIENT_ID);
		assertEquals(1, result.size());
		assertEquals(oauthAccessToken, result.toArray()[0]);
	}

	@Test
	public void testReadRefreshToken()
	{
		given(oauthTokenService.getRefreshToken(anyString())).willReturn(refreshTokenModel);
		final OAuth2RefreshToken result = tokenStore.readRefreshToken(REFRESH_TOKEN_VALUE);
		Assert.assertEquals(oauthRefreshToken, result);
	}

	@Test
	public void testReadNotExistingRefreshToken()
	{
		given(oauthTokenService.getRefreshToken(anyString())).willThrow(new UnknownIdentifierException("Unknown identifier"));
		assertNull(tokenStore.readRefreshToken(NOT_EXISTING_TOKEN));
	}

	@Test
	public void testStoreRefreshToken()
	{
		tokenStore.storeRefreshToken(oauthRefreshToken, oauthAuthentication);
		verify(oauthTokenService, times(1)).saveRefreshToken(anyString(), any(), any());
	}

	@Test
	public void testReadAuthenticationForRefreshToken()
	{
		given(oauthTokenService.getRefreshToken(anyString())).willReturn(refreshTokenModel);
		final OAuth2Authentication result = tokenStore.readAuthenticationForRefreshToken(REFRESH_TOKEN_VALUE);
		assertEquals(oauthAuthentication, result);
	}

	@Test
	public void testReadAuthenticationForNotExistingRefreshToken()
	{
		given(oauthTokenService.getRefreshToken(anyString())).willThrow(new UnknownIdentifierException("Unknown identifier"));
		assertNull(tokenStore.readAuthenticationForRefreshToken(REFRESH_TOKEN_VALUE));
	}

	@Test
	public void testRemoveRefreshToken()
	{
		tokenStore.removeRefreshToken(oauthRefreshToken);
		verify(oauthTokenService, times(1)).removeRefreshToken(anyString());
	}

    @Test
    public void testGetAccessTokenAmbigiousException()
    {
        given(oauthTokenService.getAccessTokenForAuthentication(anyString())).willThrow(new AmbiguousIdentifierException("Ambiguous identifier"));
        assertNull(tokenStore.getAccessToken(oauthAuthentication));
        verify(oauthTokenService, times(1)).getAccessTokenForAuthentication(anyString());
    }

    @Test
    public void testGetAccessTokenNotFoundException()
    {
        given(oauthTokenService.getAccessTokenForAuthentication(anyString())).willThrow(new UnknownIdentifierException("Unknown identifier"));
        assertNull(tokenStore.getAccessToken(oauthAuthentication));
        verify(oauthTokenService, times(1)).getAccessTokenForAuthentication(anyString());
    }

    @Test
    public void testGetAccessTokenInvalidAuthenticationError()
    {
        given(oauthTokenService.getAccessTokenForAuthentication(anyString())).willThrow(new ClassCastException("Could not extract access token"));
        assertNull(tokenStore.getAccessToken(oauthAuthentication));
        verify(oauthTokenService, times(1)).getAccessTokenForAuthentication(anyString());
    }

    @Test
    public void testGetAccess()
    {
        given(oauthTokenService.getAccessTokenForAuthentication(anyString())).willReturn(accessTokenModel);
        final OAuth2AccessToken accessToken = tokenStore.getAccessToken(oauthAuthentication);

        verify(oauthTokenService, times(1)).getAccessTokenForAuthentication(anyString());
        assertEquals(tokenStore.deserializeAccessToken((byte[]) accessTokenModel.getToken()), accessToken);
    }

	protected static class MockAuthentication extends AbstractAuthenticationToken
	{
		private final String principal;

		public MockAuthentication(final String name, final boolean authenticated)
		{
			super(null);
			setAuthenticated(authenticated);
			this.principal = name;
		}

		@Override
		public Object getCredentials()
		{
			return null;
		}

		@Override
		public Object getPrincipal()
		{
			return this.principal;
		}
	}
}
