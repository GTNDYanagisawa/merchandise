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
package de.hybris.platform.yb2bacceleratorstorefront.controllers.misc;

import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.AbstractController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;


/**
 * Controller for store session.
 */
@Controller
@Scope("tenant")
@RequestMapping("/_s")
public class StoreSessionController extends AbstractController
{
	private static final String REDIRECT_PREFIX = "redirect:";

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "urlEncoderService")
	private UrlEncoderService urlEncoderService;

	@RequestMapping(value = "/language", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String selectLanguage(@RequestParam("code") final String isoCode, final HttpServletRequest request)
	{
		final String previousLanguage = storeSessionFacade.getCurrentLanguage().getIsocode();
		storeSessionFacade.setCurrentLanguage(isoCode);
		userFacade.syncSessionLanguage();
		return (urlEncoderService.isLanguageEncodingEnabled()) ? getReturnRedirectUrlForUrlEncoding(request, previousLanguage,
				storeSessionFacade.getCurrentLanguage().getIsocode()) : getReturnRedirectUrlWithoutReferer(request);
	}

	@RequestMapping(value = "/currency", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String selectCurrency(@RequestParam("code") final String isoCode, final HttpServletRequest request)
	{
		final String previousCurrency = storeSessionFacade.getCurrentCurrency().getIsocode();
		storeSessionFacade.setCurrentCurrency(isoCode);
		userFacade.syncSessionCurrency();
		return (urlEncoderService.isCurrencyEncodingEnabled()) ? getReturnRedirectUrlForUrlEncoding(request, previousCurrency,
				storeSessionFacade.getCurrentCurrency().getIsocode()) : getReturnRedirectUrlWithoutReferer(request);
	}

	protected String getReturnRedirectUrl(final HttpServletRequest request)
	{
		final String referer = request.getHeader("Referer");
		if (referer != null && !referer.isEmpty())
		{
			return REDIRECT_PREFIX + referer;
		}
		return REDIRECT_PREFIX + '/';
	}

    protected String getReturnRedirectUrlWithoutReferer(final HttpServletRequest request)
    {
        final String referer = StringUtils.remove(request.getRequestURL().toString(), request.getServletPath());
        if (referer != null && !referer.isEmpty())
        {
            return REDIRECT_PREFIX + referer;
        }
        return REDIRECT_PREFIX + '/';
    }

	protected String getReturnRedirectUrlForUrlEncoding(final HttpServletRequest request, final String old, final String current)
	{
        String referer = StringUtils.remove(request.getRequestURL().toString(), request.getServletPath());
        if (!StringUtils.endsWith(referer, "/"))
        {
            referer = referer + "/";
        }
		if (referer != null && !referer.isEmpty() && StringUtils.contains(referer, "/" + old))
		{
			return REDIRECT_PREFIX + StringUtils.replace(referer, "/" + old, "/" + current);
		}
        return REDIRECT_PREFIX + referer;
	}

	@ExceptionHandler(UnknownIdentifierException.class)
	public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request)
	{
		RequestContextUtils.getOutputFlashMap(request).put("message", exception.getMessage());
		return REDIRECT_PREFIX + "/404";
	}
}
