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
package de.hybris.platform.security.captcha;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindingResult;


/**
 * An aspect which uses google ReCaptcha api to validate captcha answer on the storefront Registration form.
 */

public class ReCaptchaAspect
{
	private static final String RECAPTCHA_RESPONSE_FIELD_PARAM = "recaptcha_response_field";
	private static final String RECAPTCHA_CHALLENGE_FIELD_PARAM = "recaptcha_challenge_field";
	private static final String RECAPTCHA_PRIVATE_KEY_PROPERTY = "recaptcha.privatekey";
	private static final String RECAPTCHA_PUBLIC_KEY_PROPERTY = "recaptcha.publickey";
	private SiteConfigService siteConfigService;
	private BaseStoreService baseStoreService;

	public Object prepare(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		final List<Object> args = Arrays.asList(joinPoint.getArgs());
		final HttpServletRequest request = (HttpServletRequest) CollectionUtils.find(args,
				PredicateUtils.instanceofPredicate(HttpServletRequest.class));

		if (request != null)
		{
			final boolean captcaEnabledForCurrentStore = isCaptcaEnabledForCurrentStore();
			request.setAttribute("captcaEnabledForCurrentStore", Boolean.valueOf(captcaEnabledForCurrentStore));
			if (captcaEnabledForCurrentStore)
			{
				request.setAttribute("recaptchaPublicKey", getSiteConfigService().getProperty(RECAPTCHA_PUBLIC_KEY_PROPERTY));
			}
		}
		return joinPoint.proceed();
	}

	public Object advise(final ProceedingJoinPoint joinPoint) throws Throwable
	{

		final boolean captcaEnabledForCurrentStore = isCaptcaEnabledForCurrentStore();
		if (captcaEnabledForCurrentStore)
		{
			final List<Object> args = Arrays.asList(joinPoint.getArgs());
			final HttpServletRequest request = (HttpServletRequest) CollectionUtils.find(args,
					PredicateUtils.instanceofPredicate(HttpServletRequest.class));

			if (request != null)
			{
				request.setAttribute("captcaEnabledForCurrentStore", Boolean.valueOf(captcaEnabledForCurrentStore));
				request.setAttribute("recaptchaPublicKey", getSiteConfigService().getProperty(RECAPTCHA_PUBLIC_KEY_PROPERTY));
				final String challengeFieldValue = request.getParameter(RECAPTCHA_CHALLENGE_FIELD_PARAM);
				final String responseFieldValue = request.getParameter(RECAPTCHA_RESPONSE_FIELD_PARAM);
				if ((StringUtils.isBlank(challengeFieldValue) || StringUtils.isBlank(responseFieldValue))
						|| !checkAnswer(request, challengeFieldValue, responseFieldValue))
				{
					// if there is an error add a message to binding result.
					final BindingResult bindingResult = (BindingResult) CollectionUtils.find(args,
							PredicateUtils.instanceofPredicate(BindingResult.class));
					if (bindingResult != null)
					{
						bindingResult.reject("recaptcha.challenge.field.invalid", "Challenge Answer is invalid.");
					}
					request.setAttribute("recaptchaChallangeAnswered", Boolean.FALSE);
				}
			}
		}
		return joinPoint.proceed();
	}

	protected boolean checkAnswer(final HttpServletRequest request, final String challengeFieldValue,
			final String responseFieldValue)
	{
		final ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey(getSiteConfigService().getProperty(RECAPTCHA_PRIVATE_KEY_PROPERTY));
		final ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(request.getRemoteAddr(), challengeFieldValue,
				responseFieldValue);
		return reCaptchaResponse.isValid();
	}

	protected boolean isCaptcaEnabledForCurrentStore()
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		return currentBaseStore != null && Boolean.TRUE.equals(currentBaseStore.getCaptchaCheckEnabled());
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}
}
