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
package de.hybris.platform.sap.core.requestsequencer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.StopWatch;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.configurer.impl.ConfigurerEntitiesListImpl;
import de.hybris.platform.sap.core.requestsequencer.configurer.impl.UrlPatternImpl;


/**
 * Test class for RequestSequencerFilter.
 */
@UnitTest
public class RequestSequencerFilterTest
{

	private static String URL_PATTERN1 = ".*/airlines.*";
	private static String URL_PATTERN2 = ".*/connections.*";
	//private static  String URL_PATTERN3 = ".*/powertools.*";

	private static String URI_MATCHES = "http://localhost:9001/demoapp/airlines?site=demo";
	private static String URI_NOT_MATCHES = "http://localhost:9001/demoapp/xyz";


	//	/**
	//	 * Simulates simultaneous requests by starting two threads.
	//	 *
	//	 * @throws IOException
	//	 *            IOException
	//	 * @throws ServletException
	//	 *            ServletException
	//	 * @throws InterruptedException
	//	 *            InterruptedException
	//	 */
	//	@Test
	//	public void doFilterTest() throws IOException, ServletException, InterruptedException
	//	{
	//
	//		final RequestSequencerFilter classUnderTest = new RequestSequencerFilter();
	//
	//		final MockHttpSession mockSession = new MockHttpSession();
	//
	//		// Objects for simulate a long processing request
	//		final MockHttpServletResponse mockResponseSleep = new MockHttpServletResponse();
	//		final MockHttpServletRequest mockRequestSleep = new MockHttpServletRequest();
	//		final MockFilterChainForRequestSequencerFilter mockFilterChainSleep = new MockFilterChainForRequestSequencerFilter();
	//		mockFilterChainSleep.sleepTime = 1000;
	//		mockRequestSleep.setSession(mockSession);
	//		final SimulateRequest requestSleep = new SimulateRequest("sleeping thread", mockResponseSleep, mockRequestSleep,
	//				classUnderTest, mockFilterChainSleep);
	//		final Thread threadSleep = new Thread(requestSleep);
	//
	//		// Objects for simulate waiting request
	//		final MockHttpServletResponse mockResponseWait = new MockHttpServletResponse();
	//		final MockHttpServletRequest mockRequestWait = new MockHttpServletRequest();
	//		final MockFilterChainForRequestSequencerFilter mockFilterChainWait = new MockFilterChainForRequestSequencerFilter();
	//		mockRequestWait.setSession(mockSession);
	//
	//		final SimulateRequest requestWait = new SimulateRequest("waiting thread", mockResponseWait, mockRequestWait,
	//				classUnderTest, mockFilterChainWait);
	//		final Thread threadWait = new Thread(requestWait);
	//
	//		threadSleep.start();
	//		threadWait.start();
	//
	//		threadSleep.join();
	//		threadWait.join();
	//
	//		//System.out.println("success sleep " + requestSleep.getTotalTimeSeconds() + " secs.");
	//		//System.out.println("success wait " + requestWait.getTotalTimeSeconds() + " secs.");
	//
	//		assertTrue("Wating request was not synchronized", requestWait.getTotalTimeMillis() > mockFilterChainSleep.sleepTime - 100); // 100 is just a value which should cover administration overhead of the JVM to start the threads
	//
	//
	//	}


	/**
	 * tests the matching of the URL Pattern which is a regular expression. <br>
	 * IncludeUrlPattern: empty <br>
	 * ExludeUrlPattern: empty
	 */
	@Test
	public void testEmptyIncludeUrlPatternWithEmptyExcludeUrlPattern()
	{
		final RequestSequencerFilter classUnderTest = new RequestSequencerFilter();
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		final ConfigurerEntitiesListImpl<Pattern> includeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();
		final ConfigurerEntitiesListImpl<Pattern> excludeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();

		mockRequest.setRequestURI(URI_MATCHES);
		classUnderTest.setUrlIncludePatternList(includeUrlPatterns);
		classUnderTest.setUrlExcludePatternList(excludeUrlPatterns);


		assertFalse(classUnderTest.matchesInlcudeUrlRegExPattern(mockRequest, classUnderTest.getUrlIncludePatternList())
				&& !classUnderTest.matchesExcludeUrlRegExPattern(mockRequest, classUnderTest.getUrlExcludePatternList()));

	}

	/**
	 * tests the matching of the URL Pattern which is a regular expression. <br>
	 * IncludeUrlPattern: URLPATTERN1, URLPATTERN2 <br>
	 * ExludeUrlPattern: empty
	 */
	@Test
	public void testIncludeUrlPatternWithEmptyExcludeUrlPattern()
	{
		final RequestSequencerFilter classUnderTest = new RequestSequencerFilter();
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		final ConfigurerEntitiesListImpl<Pattern> includeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();
		final ConfigurerEntitiesListImpl<Pattern> excludeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();

		final List<String> includeRegExList = new ArrayList<String>();
		includeRegExList.add(URL_PATTERN1);
		includeRegExList.add(URL_PATTERN2);

		final UrlPatternImpl includeUrlPattern = new UrlPatternImpl();
		includeUrlPattern.setIncludeUrlPatternList(includeUrlPatterns);
		includeUrlPattern.setIncludeUrlRegExList(includeRegExList);

		includeUrlPattern.init();

		mockRequest.setRequestURI(URI_MATCHES);
		classUnderTest.setUrlIncludePatternList(includeUrlPatterns);
		classUnderTest.setUrlExcludePatternList(excludeUrlPatterns);

		assertTrue(classUnderTest.matchesInlcudeUrlRegExPattern(mockRequest, classUnderTest.getUrlIncludePatternList())
				&& !classUnderTest.matchesExcludeUrlRegExPattern(mockRequest, classUnderTest.getUrlExcludePatternList()));
	}

	/**
	 * tests the matching of the URL Pattern which is a regular expression. <br>
	 * IncludeUrlPattern: URLPATTERN1, URLPATTERN2 <br>
	 * ExludeUrlPattern: empty
	 */
	@Test
	public void testEmptyIncludeUrlPatternWithExcludeUrlPattern()
	{
		final RequestSequencerFilter classUnderTest = new RequestSequencerFilter();
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		final ConfigurerEntitiesListImpl<Pattern> includeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();
		final ConfigurerEntitiesListImpl<Pattern> excludeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();

		final List<String> excludeRegExList = new ArrayList<String>();
		excludeRegExList.add(URL_PATTERN1);

		final UrlPatternImpl excludeUrlPattern = new UrlPatternImpl();
		excludeUrlPattern.setExcludeUrlRegExList(excludeRegExList);
		excludeUrlPattern.setExcludeUrlPatternList(excludeUrlPatterns);
		excludeUrlPattern.init();

		mockRequest.setRequestURI(URI_MATCHES);
		classUnderTest.setUrlIncludePatternList(includeUrlPatterns);
		classUnderTest.setUrlExcludePatternList(excludeUrlPatterns);

		assertFalse(classUnderTest.matchesInlcudeUrlRegExPattern(mockRequest, classUnderTest.getUrlIncludePatternList())
				&& !classUnderTest.matchesExcludeUrlRegExPattern(mockRequest, classUnderTest.getUrlExcludePatternList()));

	}



	/**
	 * tests the matching of the URL Pattern which is a regular expression. <br>
	 * IncludeUrlPattern: URLPATTERN1, URLPATTERN2<br>
	 * ExludeUrlPattern: URLPATTERN1
	 */
	@Test
	public void testUrlPatternWithSameExcludePatternAndURIMatchesTheURLPattern()
	{
		final RequestSequencerFilter classUnderTest = new RequestSequencerFilter();
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setRequestURI(URI_MATCHES);

		createUrlPatternWithSameExcludePattern(classUnderTest);

		assertFalse(classUnderTest.matchesInlcudeUrlRegExPattern(mockRequest, classUnderTest.getUrlIncludePatternList())
				&& !classUnderTest.matchesExcludeUrlRegExPattern(mockRequest, classUnderTest.getUrlExcludePatternList()));

	}

	/**
	 * tests the matching of the URL Pattern which is a regular expression. <br>
	 * IncludeUrlPattern: URLPATTERN1, URLPATTERN2<br>
	 * ExludeUrlPattern: URLPATTERN1
	 */
	@Test
	public void testUrlPatternWithSameExcludePatternAndURINotMatchesTheURLPattern()
	{
		final RequestSequencerFilter classUnderTest = new RequestSequencerFilter();
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setRequestURI(URI_NOT_MATCHES);

		createUrlPatternWithSameExcludePattern(classUnderTest);


		assertFalse(classUnderTest.matchesInlcudeUrlRegExPattern(mockRequest, classUnderTest.getUrlIncludePatternList())
				&& !classUnderTest.matchesExcludeUrlRegExPattern(mockRequest, classUnderTest.getUrlExcludePatternList()));
	}


	/**
	 * @param classUnderTest
	 *           the RequestSequencerFilter
	 */
	private void createUrlPatternWithSameExcludePattern(final RequestSequencerFilter classUnderTest)
	{
		final ConfigurerEntitiesListImpl<Pattern> includeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();
		final ConfigurerEntitiesListImpl<Pattern> excludeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();

		final List<String> includeRegExList = new ArrayList<String>();
		includeRegExList.add(URL_PATTERN1);
		includeRegExList.add(URL_PATTERN2);

		final List<String> excludeRegExList = new ArrayList<String>();
		excludeRegExList.add(URL_PATTERN1);

		final UrlPatternImpl urlPattern = new UrlPatternImpl();
		urlPattern.setIncludeUrlPatternList(includeUrlPatterns);
		urlPattern.setExcludeUrlPatternList(excludeUrlPatterns);

		urlPattern.setIncludeUrlRegExList(excludeRegExList);
		urlPattern.setExcludeUrlRegExList(excludeRegExList);
		urlPattern.init();

		classUnderTest.setUrlIncludePatternList(includeUrlPatterns);
		classUnderTest.setUrlExcludePatternList(excludeUrlPatterns);
	}

	/**
	 * tests the matching of the URL Pattern which is a regular expression. <br>
	 * IncludeUrlPattern: URLPATTERN1, URLPATTERN2<br>
	 * ExludeUrlPattern: URLPATTERN2
	 */
	@Test
	public void testUrlPatternWithDifferentExcludePatternAndURIMatchesTheURLPattern()
	{
		final RequestSequencerFilter classUnderTest = new RequestSequencerFilter();
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setRequestURI(URI_MATCHES);

		createUrlPatternWithDifferentExcludePattern(classUnderTest);

		assertTrue(classUnderTest.matchesInlcudeUrlRegExPattern(mockRequest, classUnderTest.getUrlIncludePatternList())
				&& !classUnderTest.matchesExcludeUrlRegExPattern(mockRequest, classUnderTest.getUrlExcludePatternList()));


	}

	/**
	 * tests the matching of the URL Pattern which is a regular expression. <br>
	 * IncludeUrlPattern: URLPATTERN1, URLPATTERN2<br>
	 * ExludeUrlPattern: URLPATTERN2
	 */
	@Test
	public void testUrlPatternWithDifferentExcludePatternAndURINotMatchesTheURLPattern()
	{
		final RequestSequencerFilter classUnderTest = new RequestSequencerFilter();
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setRequestURI(URI_NOT_MATCHES);

		createUrlPatternWithDifferentExcludePattern(classUnderTest);

		assertFalse(classUnderTest.matchesInlcudeUrlRegExPattern(mockRequest, classUnderTest.getUrlIncludePatternList())
				&& !classUnderTest.matchesExcludeUrlRegExPattern(mockRequest, classUnderTest.getUrlExcludePatternList()));

	}


	/**
	 * @param classUnderTest
	 *           the RequestSequencerFilter
	 */
	private void createUrlPatternWithDifferentExcludePattern(final RequestSequencerFilter classUnderTest)
	{
		final ConfigurerEntitiesListImpl<Pattern> includeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();
		final ConfigurerEntitiesListImpl<Pattern> excludeUrlPatterns = new ConfigurerEntitiesListImpl<Pattern>();

		final List<String> includeRegExList = new ArrayList<String>();
		includeRegExList.add(URL_PATTERN1);
		includeRegExList.add(URL_PATTERN2);

		final List<String> excludeRegExList = new ArrayList<String>();
		excludeRegExList.add(URL_PATTERN2);

		final UrlPatternImpl urlPattern = new UrlPatternImpl();
		urlPattern.setIncludeUrlPatternList(includeUrlPatterns);
		urlPattern.setExcludeUrlPatternList(excludeUrlPatterns);

		urlPattern.setIncludeUrlRegExList(includeRegExList);
		urlPattern.setExcludeUrlRegExList(excludeRegExList);
		urlPattern.init();

		classUnderTest.setUrlIncludePatternList(includeUrlPatterns);
		classUnderTest.setUrlExcludePatternList(excludeUrlPatterns);

	}



	/**
	 * Mock for filter chain.
	 */
	public class MockFilterChainForRequestSequencerFilter extends MockFilterChain
	{
		/**
		 * Defines how log the filter should interrupt.
		 */
		public int sleepTime = 0; //NOPMD

		@Override
		public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException
		{
			try
			{
				Thread.sleep(sleepTime);
			}
			catch (final InterruptedException e)
			{
				throw new RuntimeException(e); //NOPMD
			}

		}
	}


	/**
	 * Class for simulating a request.
	 */
	public class SimulateRequest implements Runnable
	{
		/**
		 * self-explanatory.
		 */
		final protected String name; //NOPMD
		/**
		 * self-explanatory.
		 */
		final protected MockHttpServletResponse mockResponse;//NOPMD
		/**
		 * self-explanatory.
		 */
		final protected MockHttpServletRequest mockRequest;//NOPMD
		/**
		 * self-explanatory.
		 */
		final protected MockFilterChain mockFilterChain;//NOPMD
		/**
		 * self-explanatory.
		 */
		final protected RequestSequencerFilter filter;//NOPMD
		/**
		 * self-explanatory.
		 */
		final protected StopWatch stopWatch;//NOPMD


		/**
		 * Simulates a request.
		 *
		 * @param name
		 *           name
		 * @param mockResponse
		 *           response
		 * @param mockRequest
		 *           request
		 * @param filter
		 *           filter
		 * @param filterChain
		 *           filter chain
		 */
		public SimulateRequest(final String name, final MockHttpServletResponse mockResponse,
				final MockHttpServletRequest mockRequest, final RequestSequencerFilter filter, final MockFilterChain filterChain)
		{
			super();
			this.name = name;
			this.mockResponse = mockResponse;
			this.mockRequest = mockRequest;
			this.filter = filter;
			this.mockFilterChain = filterChain;
			this.stopWatch = new StopWatch();

		}

		@Override
		public void run()
		{
			//System.out.println(name + " started at " + (new Date().toString()));
			stopWatch.start();
			try
			{
				filter.doFilter(mockRequest, mockResponse, mockFilterChain);
			}
			catch (IOException | ServletException e)
			{
				throw new RuntimeException(e); //NOPMD
			}
			stopWatch.stop();
			//System.out.println(name + " finished at " + (new Date().toString()));
		}

		/**
		 * self-explanatory.
		 *
		 * @return self-explanatory.
		 */
		public double getTotalTimeSeconds()
		{
			return stopWatch.getTotalTimeSeconds();
		}

		/**
		 * self-explanatory.
		 *
		 * @return self-explanatory.
		 */
		public long getTotalTimeMillis()
		{
			return stopWatch.getTotalTimeMillis();
		}


	}



}
