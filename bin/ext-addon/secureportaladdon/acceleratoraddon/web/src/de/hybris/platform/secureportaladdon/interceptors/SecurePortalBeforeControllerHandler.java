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
package de.hybris.platform.secureportaladdon.interceptors;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.addonsupport.interceptors.BeforeControllerHandlerAdaptee;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.secureportaladdon.constants.SecureportaladdonWebConstants.RequestMappings;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.google.common.net.HttpHeaders;


/**
 * Intercepts incoming requests to check if the current site will require authentication or not. The site can be secured
 * through the HMC by accessing the list of web sites and modifying the attribute requiresAuthentication.
 */
public class SecurePortalBeforeControllerHandler implements BeforeControllerHandlerAdaptee
{
	private static final Logger LOG = Logger.getLogger(SecurePortalBeforeControllerHandler.class);

	private Set<String> unsecuredUris;

	private Set<String> controlUris;

	private CMSSiteService cmsSiteService;

	private UserService userService;

	private String defaultLoginUri;

	private HttpSessionRequestCache httpSessionRequestCache;

	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	private RedirectStrategy redirectStrategy;

	@Required
	public void setDefaultLoginUri(final String defaultLoginUri)
	{
		this.defaultLoginUri = defaultLoginUri;
	}

	@Required
	public void setRedirectStrategy(final RedirectStrategy redirectStrategy)
	{
		this.redirectStrategy = redirectStrategy;
	}

	@Required
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	@Required
	public void setHttpSessionRequestCache(final HttpSessionRequestCache httpSessionRequestCache)
	{
		this.httpSessionRequestCache = httpSessionRequestCache;
	}

	@Required
	public void setUnsecuredUris(final Set<String> unsecuredUris)
	{
		this.unsecuredUris = unsecuredUris;
	}

	@Required
	public void setControlUris(final Set<String> controlUris)
	{
		this.controlUris = controlUris;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Maintains flash attributes to prevent data such as global messages to be lost when you are redirected to the login
	 * page
	 * 
	 * @param request
	 *           Standard HTTP request
	 * @param response
	 *           Standard HTTP response
	 */
	protected void maintainFlashAttributes(final HttpServletRequest request, final HttpServletResponse response)
	{
		final Map<String, ?> lastAttributes = RequestContextUtils.getInputFlashMap(request); // should hold the attributes from your last request       
		if (lastAttributes != null)
		{
			final FlashMap forNextRequest = RequestContextUtils.getOutputFlashMap(request); // will hold the attributes for your next request
			forNextRequest.putAll(lastAttributes);
			forNextRequest.setTargetRequestPath(request.getContextPath() + defaultLoginUri);//forNextRequest.setTargetRequestPath("/yb2bacceleratorstorefront/powertools/en/USD/secured/login");
			RequestContextUtils.getFlashMapManager(request).saveOutputFlashMap(forNextRequest, request, response);
		}
	}

	@Override
	public boolean beforeController(final HttpServletRequest request, final HttpServletResponse response,
			final HandlerMethod handler) throws Exception
	{
		boolean returning = true;

		final boolean siteSecured = isSiteSecured();

		//Make sure that if the user does not have secure token in session, we invalidate the session and website asks for login again.
		if (siteSecured)
		{
			final UserModel user = userService.getCurrentUser();
			final boolean isUserAnonymous = userService.isAnonymousUser(user);

			if (isUriPartOfSet(request, unsecuredUris))
			{
				if (!isUserAnonymous && !isUriPartOfSet(request, controlUris))
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("User is already authenticated and accessing a safe-mapping, hence a useless operation. Redirect to home page instead.");
					}
					redirect(request, response, getRedirectUrlIfAuthenticated());
					returning = false;
				}
				else
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("The request URI '%s' does not require security. Interceptor is done.",
								request.getRequestURI()));
					}
				}
			}
			else if (isUserAnonymous && isNotLoginRequest(request))
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Anonymous user is accessing the URI '%s' which is secured. Redirecting to login page.",
							request.getRequestURI()));
				}

				redirect(request, response, getRedirectUrl(defaultLoginUri, true));
				returning = false;
			}
		}

		return returning;
	}

	protected boolean isNotLoginRequest(final HttpServletRequest request)
	{
		return !request.getRequestURI().contains(defaultLoginUri);
	}


	protected boolean isUriPartOfSet(final HttpServletRequest request, final Set<String> inputSet)
	{

		for (final String input : inputSet)
		{

			final AntPathRequestMatcher matcher = new AntPathRequestMatcher(input);
			if (matcher.matches(request))
			{
				return true;
			}

		}

		return false;

	}

	protected boolean isSiteSecured()
	{
		final CMSSiteModel site = cmsSiteService.getCurrentSite();
		return site.isRequiresAuthentication();
	}

	protected void redirect(final HttpServletRequest request, final HttpServletResponse response, final String targetUrl)
	{

		try
		{

			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Redirecting to url '%s'.", targetUrl));
			}

			redirectStrategy.sendRedirect(request, response, targetUrl);

		}
		catch (final IOException ex)
		{
			LOG.error("Unable to redirect.", ex);
		}

	}

	protected String getRedirectUrl(final String mapping, final boolean secured)
	{
		return siteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteService.getCurrentSite(), secured, mapping);
	}

	protected String getRedirectUrlIfAuthenticated()
	{
		return getRedirectUrl(RequestMappings.HOME, true);
	}

	/**
	 * Method to handle the case that the referer of the request is empty. The execution of this method could be switched
	 * off from project.properties.
	 */
	protected HttpServletRequest hackRefererHeader(final HttpServletRequest request)
	{

		// Since the WebHttpSessionRequestCache of hybris uses the referer header, we need to make sure there is one.
		// If we access the site directly using something like powertools.local:9001/yb2bacceleratorstorefront/ we don't get a referer
		// header sent by the browser! Using the referer header is NOT recommended as it can be removed by firewalls, spoofed etc.
		return new HttpServletRequestWrapper(request)
		{
			@Override
			public String getHeader(final String name)
			{
				if (StringUtils.equalsIgnoreCase(name, HttpHeaders.REFERER))
				{

					final String headerValue = super.getHeader(name);

					if (StringUtils.isNotBlank(headerValue))
					{
						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("Referer header is present! The saved request will use '%s'.", headerValue));
						}
						return headerValue;
					}
					else
					{
						final String url = request.getRequestURL().toString();

						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("Referer header is empty! Creating a the URL '%s' for the SavedRequest.", url));
						}

						return url;
					}
				}
				return super.getHeader(name);
			}

		};

	}

}