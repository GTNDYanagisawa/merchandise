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
package de.hybris.platform.yb2bacceleratorfacades.flow.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.yb2bacceleratorcore.enums.B2BCheckoutFlowEnum;
import de.hybris.platform.yb2bacceleratorcore.enums.B2BCheckoutPciOptionEnum;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Specialised version of the DefaultB2BCheckoutFlowFacade that allows the checkout flow and pci strategy to be
 * overridden in the session. This is primarily used for demonstration purposes and you may not need to use this
 * sub-class in your environment.
 */
public class SessionOverrideB2BCheckoutFlowFacade extends DefaultB2BCheckoutFlowFacade
{
	private static final Logger LOG = Logger.getLogger(SessionOverrideB2BCheckoutFlowFacade.class);

	public static final String B2B_SESSION_KEY_CHECKOUT_FLOW = "B2BSessionOverrideCheckoutFlow-CheckoutFlow";
	public static final String B2B_SESSION_KEY_SUBSCRIPTION_PCI_OPTION = "B2BSessionOverrideCheckoutFlow-SubscriptionPciOption";


	private SessionService sessionService;

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Override
	public B2BCheckoutFlowEnum getCheckoutFlow()
	{
		final B2BCheckoutFlowEnum sessionOverride = getSessionService().getAttribute(B2B_SESSION_KEY_CHECKOUT_FLOW);
		if (sessionOverride != null)
		{
			LOG.info("B2B Session Override CheckoutFlow [" + sessionOverride + "]");
			return sessionOverride;
		}

		// Fallback to default
		return super.getCheckoutFlow();
	}

	@Override
	public B2BCheckoutPciOptionEnum getSubscriptionPciOption()
	{
		final B2BCheckoutPciOptionEnum sessionOverride = getSessionService().getAttribute(B2B_SESSION_KEY_SUBSCRIPTION_PCI_OPTION);
		if (sessionOverride != null)
		{
			LOG.info("B2B Session Override SubscriptionPciOption [" + sessionOverride + "]");
			return sessionOverride;
		}

		// Fallback to default
		return super.getSubscriptionPciOption();
	}

	public static void resetSessionOverrides()
	{
		final SessionService sessionService = getStaticSessionService();
		sessionService.removeAttribute(B2B_SESSION_KEY_CHECKOUT_FLOW);
		sessionService.removeAttribute(B2B_SESSION_KEY_SUBSCRIPTION_PCI_OPTION);
	}

	public static void setSessionOverrideCheckoutFlow(final B2BCheckoutFlowEnum checkoutFlow)
	{
		getStaticSessionService().setAttribute(B2B_SESSION_KEY_CHECKOUT_FLOW, checkoutFlow);
	}

	public static void setSessionOverrideSubscriptionPciOption(final B2BCheckoutPciOptionEnum checkoutPciOption)
	{
		getStaticSessionService().setAttribute(B2B_SESSION_KEY_SUBSCRIPTION_PCI_OPTION, checkoutPciOption);
	}

	protected static SessionService getStaticSessionService()
	{
		return Registry.getApplicationContext().getBean("sessionService", SessionService.class);
	}
}
