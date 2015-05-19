/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.ycommercewebservices.interceptors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercewebservicescommons.cache.CacheControl;
import de.hybris.platform.commercewebservicescommons.cache.CacheControlDirective;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.HandlerMethod;


/**
 * Test suite for @link{de.hybris.platform.ycommercewebservices.interceptors.CacheControlHandlerInterceptor}
 */
@UnitTest
public class CacheControlHandlerInterceptorTest
{
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";
	private CacheControlHandlerInterceptor interceptor;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private HandlerMethod handler;
	private CacheControlController cacheController = new CacheControlController();
	private PrivateCacheController privateCacheController = new PrivateCacheController();

	@Before
	public void setUp()
	{
		interceptor = new CacheControlHandlerInterceptor();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	public void testPrivateOnlyUsingClassAnnotation() throws Exception
	{
		// given
		handler = new HandlerMethod(privateCacheController, privateCacheController.getClass().getMethod("testPrivateOnly"));
		request.setMethod(HttpMethod.GET.name());

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Assert.assertNotNull(response.getHeader(HEADER_CACHE_CONTROL));
		Assert.assertEquals(response.getHeader(HEADER_CACHE_CONTROL), "private");
	}

	@Test
	public void testPublicOnlyWhenMethodAnnotationOverridesClassAnnotation() throws Exception
	{
		// given
		handler = new HandlerMethod(privateCacheController, privateCacheController.getClass().getMethod("testPublicOnly"));
		request.setMethod(HttpMethod.HEAD.name());

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Assert.assertNotNull(response.getHeader(HEADER_CACHE_CONTROL));
		Assert.assertEquals(response.getHeader(HEADER_CACHE_CONTROL), "public");
	}

	@Test
	public void testSingleDirectiveWithMaxAge() throws Exception
	{
		// given
		handler = new HandlerMethod(cacheController, cacheController.getClass().getMethod("singleDirectiveWithMaxAge"));
		request.setMethod(HttpMethod.GET.name());

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Assert.assertNotNull(response.getHeader(HEADER_CACHE_CONTROL));
		Assert.assertTrue(response.getHeader(HEADER_CACHE_CONTROL).contains("public"));
		Assert.assertTrue(response.getHeader(HEADER_CACHE_CONTROL).contains("max-age=100"));
	}

	@Test
	public void testMultipleDirectives() throws Exception
	{
		// given
		handler = new HandlerMethod(cacheController, cacheController.getClass().getMethod("multipleDirectives"));
		request.setMethod(HttpMethod.HEAD.name());

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Assert.assertNotNull(response.getHeader(HEADER_CACHE_CONTROL));
		Assert.assertTrue(response.getHeader(HEADER_CACHE_CONTROL).contains("public"));
		Assert.assertTrue(response.getHeader(HEADER_CACHE_CONTROL).contains("proxy-revalidate"));
		Assert.assertTrue(response.getHeader(HEADER_CACHE_CONTROL).contains("max-age=300"));
		Assert.assertTrue(response.getHeader(HEADER_CACHE_CONTROL).contains("s-maxage=100"));
	}

	@Test
	public void testNoCache() throws Exception
	{
		// given
		handler = new HandlerMethod(cacheController, cacheController.getClass().getMethod("noCache"));
		request.setMethod(HttpMethod.GET.name());

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Assert.assertNotNull(response.getHeader(HEADER_CACHE_CONTROL));
		Assert.assertTrue(response.getHeader(HEADER_CACHE_CONTROL).contains("no-cache"));
		Assert.assertTrue(response.getHeader(HEADER_CACHE_CONTROL).contains("max-age=0"));
	}

	@Test
	public void testIfNoCacheWhenHttpMethodOtherThanGETorHEAD() throws Exception
	{
		// given
		handler = new HandlerMethod(cacheController, cacheController.getClass().getMethod("singleDirectiveWithMaxAge"));
		request.setMethod(HttpMethod.POST.name());

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Assert.assertNull(response.getHeader(HEADER_CACHE_CONTROL));
	}

	@Controller
	private class CacheControlController
	{

		@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 100)
		public String singleDirectiveWithMaxAge()
		{
			return "";
		}

		@CacheControl(directive =
		{ CacheControlDirective.PUBLIC, CacheControlDirective.PROXY_REVALIDATE }, maxAge = 300, sMaxAge = 100)
		public String multipleDirectives()
		{
			return "";
		}

		@CacheControl(directive = CacheControlDirective.NO_CACHE, maxAge = 0)
		public String noCache()
		{
			return "";
		}
	}

	@Controller
	@CacheControl(directive = CacheControlDirective.PRIVATE)
	private class PrivateCacheController
	{
		public String testPrivateOnly()
		{
			return "";
		}

		@CacheControl(directive = CacheControlDirective.PUBLIC)
		public String testPublicOnly()
		{
			return "";
		}
	}
}
