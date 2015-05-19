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

import de.hybris.platform.sap.core.common.configurer.ConfigurerEntitiesList;
import de.hybris.platform.sap.core.common.util.LogDebug;
import de.hybris.platform.util.StopWatch;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;


/**
 * Filter for sequencing ServletRequests. <br>
 *
 */
/**
 *
 */
/**
 *
 */
public class RequestSequencerFilter extends GenericFilterBean
{
	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(RequestSequencerFilter.class.getName());

	/**
	 * Name of the session attribute, to hold the counter of type AtomicInteger, that is used to track the number of
	 * request waiting to be processed.
	 */
	protected static String SESSION_REQUEST_QUEUE_COUNTER_ATTRIB = RequestSequencerFilter.class.getName() + ".RequestQueueCounter"; // NOPMD

	/** Name of the session attribute, used to sync requests for the same session. */
	protected static String SESSION_REQUEST_SYNC_OBJECT = RequestSequencerFilter.class.getName() + ".RequestSyncObject"; // NOPMD


	/**
	 * a list of RegEx URL Patterns which are used for checking if the request is matching and can be performed with the
	 * filter.
	 */
	private ConfigurerEntitiesList<Pattern> urlIncludePatternList = null;


	/**
	 * a List of RegEx URL Patterns for which the filter shall not be performed.
	 */
	private ConfigurerEntitiesList<Pattern> urlExcludePatternList = null;




	/**
	 * @param urlRegExList
	 *           a list of include URL Patterns
	 */
	public void setUrlIncludePatternList(final ConfigurerEntitiesList<Pattern> urlRegExList)
	{
		this.urlIncludePatternList = urlRegExList;
	}

	/**
	 * @return the list of RegEx Pattern objects for URL include patterns
	 */
	public ConfigurerEntitiesList<Pattern> getUrlIncludePatternList()
	{
		return urlIncludePatternList;
	}

	/**
	 * @param urlRegExList
	 *           a list of exclude URL Patterns
	 */
	public void setUrlExcludePatternList(final ConfigurerEntitiesList<Pattern> urlRegExList)
	{
		this.urlExcludePatternList = urlRegExList;
	}

	/**
	 * @return the list of RegEx Pattern objects for URL exclude patterns
	 */
	public ConfigurerEntitiesList<Pattern> getUrlExcludePatternList()
	{
		return urlExcludePatternList;
	}



	/**
	 * Sequences the HttpServletRequests.<br>
	 * <p>
	 * This filter ensures, that for one session only one request is processed at time. This is done by a monitor object
	 * in the session attributes (see {@link #getSessionRequestMonitorObject(HttpSession)}) for details.
	 * </p>
	 * 
	 * @param request
	 *           the servlet request
	 * @param response
	 *           the servlet response
	 * @param filterChain
	 *           the filter filterChain
	 * @throws IOException
	 *            IOException
	 * @throws ServletException
	 *            ServletException
	 * 
	 */
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException
	{

		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpSession session = httpRequest.getSession(false);

		if (session == null)
		{
			filterChain.doFilter(request, response);
			log.debug("Session is null, request not sequenced");
			return;
		}



		if (matchesInlcudeUrlRegExPattern(httpRequest, this.urlIncludePatternList)
				&& !matchesExcludeUrlRegExPattern(httpRequest, this.urlExcludePatternList))
		{
			log.debug("URL Pattern has matched. Request sequencing is performed.");

			initializeSessionWhenNecessary(session);

			if (log.isDebugEnabled())
			{
				synchronizeRequestWithLog(httpRequest, response, filterChain, session);
			}
			else
			{
				synchronizeRequest(httpRequest, response, filterChain, session);
			}

			return;
		}

		log.debug("No URL pattern has matched");
		filterChain.doFilter(request, response);

	}

	/**
	 * Synchronizes request based on a attribute in the session.<br>
	 * When trace is switched on, the
	 * {@link #synchronizeRequestWithLog(HttpServletRequest, ServletResponse, FilterChain, HttpSession)} is called
	 * instead.
	 * 
	 * @param request
	 *           request
	 * @param response
	 *           response
	 * @param chain
	 *           chain
	 * @param session
	 *           session
	 * @throws IOException
	 *            IOException
	 * @throws ServletException
	 *            ServletException
	 */
	protected void synchronizeRequest(final HttpServletRequest request, final ServletResponse response, final FilterChain chain,
			final HttpSession session) throws IOException, ServletException
	{

		final ReentrantLock lock = getSessionRequestMonitorObject(session);
		try
		{
			lock.lock();
			chain.doFilter(request, response);
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Synchronizes request based on a attribute in the session and logs state.<br>
	 * Provides the same functionality as
	 * {@link #synchronizeRequest(HttpServletRequest, ServletResponse, FilterChain, HttpSession)} but includes trace
	 * information.
	 * 
	 * @param request
	 *           request
	 * @param response
	 *           response
	 * @param chain
	 *           chain
	 * @param session
	 *           session
	 * @throws IOException
	 *            IOException
	 * @throws ServletException
	 *            ServletException
	 */
	protected void synchronizeRequestWithLog(final HttpServletRequest request, final ServletResponse response,
			final FilterChain chain, final HttpSession session) throws IOException, ServletException
	{

		final String urlToLog = "";

		if (log.isDebugEnabled())
		{
			prepareUrlLogEntry(request.getRequestURL().toString());
		}

		LogDebug.debug(log, "Entering synchronizeRequestWithLog for {0}", Thread.currentThread().getName());
		LogDebug.debug(log, "Queuing   HTTPRequest for session: {0}, RequestCounter: {1}, Thread: {2}, URL: {3}", session.getId(),
				getSessionRequestQueueCounter(session).getAndIncrement(), Thread.currentThread().getName(), urlToLog);

		LogDebug.debug(log, "##### 1 ##### Value of requestcounter {0}, hascode is {1}", getSessionRequestQueueCounter(session)
				.get(), getSessionRequestQueueCounter(session).hashCode());

		final StopWatch watch = new StopWatch("synchronizeRequestWithLog");

		final ReentrantLock lock = getSessionRequestMonitorObject(session);
		try
		{
			lock.lock();

			LogDebug.debug(log, "##### 2 ##### Value of requestcounter {0}, hascode is {1}", getSessionRequestQueueCounter(session)
					.get(), getSessionRequestQueueCounter(session).hashCode());

			LogDebug.debug(log,
					"Executing HTTPRequest for session: {0}, RequestCounter: {1}, Waited: {2} ms, Thread: {3}, URL: {4}", new Object[]
					{ session.getId(), getSessionRequestQueueCounter(session).decrementAndGet(), watch.stop(),
							Thread.currentThread().getName(), urlToLog });

			LogDebug.debug(log, "Processing request for {0}", Thread.currentThread().getName());

			chain.doFilter(request, response);
		}
		finally
		{
			lock.unlock();
		}


		LogDebug.debug(log, "Exiting synchronizeRequestWithLog for {0}", Thread.currentThread().getName());
	}

	/**
	 * Gets the queue counter object from the session.<br>
	 * 
	 * @param session
	 *           session
	 * @return Request Queue counter
	 */
	protected AtomicInteger getSessionRequestQueueCounter(final HttpSession session)
	{
		return (AtomicInteger) session.getAttribute(SESSION_REQUEST_QUEUE_COUNTER_ATTRIB);
	}

	/**
	 * Gets the request monitor object from the session.<br>
	 * 
	 * @param session
	 *           session
	 * @return Request Monitor Object
	 */
	protected ReentrantLock getSessionRequestMonitorObject(final HttpSession session)
	{
		return (ReentrantLock) session.getAttribute(SESSION_REQUEST_SYNC_OBJECT);
	}

	/**
	 * Initialize session when necessary.<br>
	 * <p>
	 * The synchronization in general is done on an session attribute and not on the session object itself. Synchronizing
	 * on the session object itself can produce deadlocks, when in other framework parts or inside the application a
	 * Synchronization on the session object is done, too.
	 * </p>
	 * But for the initialization of the session, we can synchronize on the session object, only. But at this time, no
	 * application coding has been called, even no JSF coding, so that no deadlocks should occur.
	 * 
	 * @param session
	 *           the current HttpSession object
	 */
	protected void initializeSessionWhenNecessary(final HttpSession session)
	{

		// If session is already initialized, we can return;
		if (getSessionRequestMonitorObject(session) != null)
		{
			return;
		}

		// This session synchronization happens for the first request(s), only.
		synchronized (session)
		{

			// Return, when session was initialized by another thread meanwhile
			if (getSessionRequestMonitorObject(session) != null)
			{
				return;
			}

			// Do the initialization
			LogDebug.debug(log, "initializing session in Thread {0}", Thread.currentThread().getName());
			session.setAttribute(SESSION_REQUEST_QUEUE_COUNTER_ATTRIB, new AtomicInteger(0));
			session.setAttribute(SESSION_REQUEST_SYNC_OBJECT, new ReentrantLock(true));
		}
	}


	/**
	 * Checks if the include URL Patterns matches the requested URI.
	 * 
	 * @param httpRequest
	 *           the HTTP request
	 * @param urlPatterns
	 *           a list of compiled include URL Patterns
	 * @return true when the URL Pattern matches the URI Request
	 */
	protected boolean matchesInlcudeUrlRegExPattern(final HttpServletRequest httpRequest,
			final ConfigurerEntitiesList<Pattern> urlPatterns)
	{
		log.debug("Checking include RegEx URL patterns");
		return matchesUrlRegExPattern(httpRequest, urlPatterns);
	}

	/**
	 * Checks if the exclude URL Patterns matches the requested URI.
	 * 
	 * @param httpRequest
	 *           the HTTP request
	 * @param urlPatterns
	 *           a list of compiled exclude URL Patterns
	 * @return true when the URL Pattern matches the URI Request
	 */
	protected boolean matchesExcludeUrlRegExPattern(final HttpServletRequest httpRequest,
			final ConfigurerEntitiesList<Pattern> urlPatterns)
	{
		log.debug("Checking exclude RegEx URL patterns");
		return matchesUrlRegExPattern(httpRequest, urlPatterns);
	}

	/**
	 * @param httpRequest
	 *           the HTTP request
	 * @param urlPatterns
	 *           a list of compiled URL Patterns
	 * @return true when the URL Pattern matches the URI Request
	 */
	private boolean matchesUrlRegExPattern(final HttpServletRequest httpRequest, final ConfigurerEntitiesList<Pattern> urlPatterns)
	{
		boolean urlPatternHasMatched = false;

		final String urlToLog = "";

		if (log.isDebugEnabled())
		{
			prepareUrlLogEntry(httpRequest.getRequestURI());
		}

		for (final Pattern urlPattern : urlPatterns.getEntities())
		{
			final Matcher urlMatcher = urlPattern.matcher(httpRequest.getRequestURI());

			if (urlMatcher.matches())
			{
				log.debug("RequestSequencerFilter: URL Pattern: " + urlPattern.pattern().toString() + " fits to the requested URI: "
						+ urlToLog + ".");
				urlPatternHasMatched = true;

			}
			else
			{
				log.debug("RequestSequencerFilter: URL Pattern: " + urlPattern.pattern().toString()
						+ " does NOT fit to the requested URI: " + urlToLog + ".");

			}
		}

		return urlPatternHasMatched;
	}


	/**
	 * Prepares a URL provided as string for logging.
	 * 
	 * @param urLToLog
	 *           the URL to log as String
	 * @return a well formed string for the log
	 */
	private String prepareUrlLogEntry(final String urLToLog)
	{
		return preventLogForging(urLToLog);
	}

	/**
	 * Log Forging attacks can appear if input is directly inserted into logs without validation. Main scenario is the
	 * adding of additional or malicious log entries using CRLF characters in the input.
	 * 
	 * This method replaces CRLF characters from entries that shall be logged and are not further validated using e.g.
	 * black- or whitelists of characters.
	 * 
	 * @param stringToLog
	 *           the string that shall be logged
	 * @return the string without CRLF characters
	 */
	private String preventLogForging(final String stringToLog)
	{
		return stringToLog.replace("\n", "").replace("\r", "").replace("%0A", "").replace("%0D", "").replace("%0a", "")
				.replace("%0d", "");
	}

}
