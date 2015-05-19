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
package de.hybris.platform.assistedservicestorefront.aspect;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.ServletException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.channel.ChannelDecisionManagerImpl;
import org.springframework.security.web.access.channel.InsecureChannelProcessor;
import org.springframework.security.web.access.channel.SecureChannelProcessor;


/**
 * Aspect for Spring Security channel decision concerns.
 */
@Aspect
public class ChannelDecisionAspect
{
	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;

	/**
	 * Around advice for the decide method of classes in the org.springframework.security.web.access.channel package,
	 * forcing HTTPS for all requests.
	 * 
	 * @param joinPoint
	 *           the join point
	 * @throws Throwable
	 *            any exceptions thrown during advice execution
	 */
	@Around("execution(public void org.springframework.security.web.access.channel.*.decide(..))")
	public void decideAround(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		if (isAssistedServiceMode(joinPoint))
		{
			if (joinPoint.getTarget() instanceof SecureChannelProcessor)
			{
				processSecureChannelProcessor(joinPoint);
			}
			else if (joinPoint.getTarget() instanceof InsecureChannelProcessor)
			{
				// do nothing - we don't want the insecure processor to redirect to HTTP
			}
			else if (joinPoint.getTarget() instanceof ChannelDecisionManagerImpl)
			{
				processChannelDecisionManager(joinPoint);
			}
			else
			{
				joinPoint.proceed();
			}
		}
		else
		{
			// if the assisted service mode is off just call the regular logic
			joinPoint.proceed();
		}
	}

	private boolean isAssistedServiceMode(final ProceedingJoinPoint joinPoint)
	{
		boolean assistedServiceModeRequested = false;
		if (joinPoint.getArgs()[0] instanceof FilterInvocation)
		{
			final FilterInvocation invocation = (FilterInvocation) joinPoint.getArgs()[0];
			assistedServiceModeRequested = Boolean.parseBoolean(invocation.getHttpRequest().getParameter("asm"));
		}
		return assistedServiceModeRequested || assistedServiceFacade.isAssistedServiceModeLaunched();
	}

	private void processSecureChannelProcessor(final ProceedingJoinPoint joinPoint) throws IOException, ServletException
	{
		final SecureChannelProcessor processor = (SecureChannelProcessor) joinPoint.getTarget();
		final FilterInvocation invocation = (FilterInvocation) joinPoint.getArgs()[0];

		/* redirect any insecure request to HTTPS independent of its config */
		if (!invocation.getHttpRequest().isSecure())
		{
			processor.getEntryPoint().commence(invocation.getHttpRequest(), invocation.getHttpResponse());
		}
	}

	private void processChannelDecisionManager(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		final Collection<ConfigAttribute> config = (Collection<ConfigAttribute>) joinPoint.getArgs()[1];
		final Collection<ConfigAttribute> filteredConfig = new ArrayList();

		/*
		 * filter out all "ANY_CHANNEL" attributes, so that the ChannelDecisionManager delegates all invocations to its
		 * ChannelProcessors
		 */
		for (final ConfigAttribute attribute : config)
		{
			if (!"ANY_CHANNEL".equals(attribute.getAttribute()))
			{
				filteredConfig.add(attribute);
			}
		}

		final Object[] args =
		{ joinPoint.getArgs()[0], filteredConfig };
		joinPoint.proceed(args);
	}
}
