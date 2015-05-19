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

import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacade;
import de.hybris.platform.yb2bacceleratorcore.checkout.flow.B2BCheckoutFlowStrategy;
import de.hybris.platform.yb2bacceleratorcore.checkout.pci.B2BCheckoutPciStrategy;
import de.hybris.platform.yb2bacceleratorcore.enums.B2BCheckoutFlowEnum;
import de.hybris.platform.yb2bacceleratorcore.enums.B2BCheckoutPciOptionEnum;
import de.hybris.platform.yb2bacceleratorfacades.flow.B2BCheckoutFlowFacade;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BCheckoutFlowFacade}. Delegates resolving the checkout flow to an injected
 * {@link B2BCheckoutFlowStrategy}.
 * 
 * @since 4.6
 * @spring.bean checkoutFlowFacade
 */
public class DefaultB2BCheckoutFlowFacade extends DefaultB2BCheckoutFacade implements B2BCheckoutFlowFacade
{
	private B2BCheckoutFlowStrategy checkoutFlowStrategy;
	private B2BCheckoutPciStrategy b2BCheckoutPciStrategy;

	@Override
	public B2BCheckoutFlowEnum getCheckoutFlow()
	{
		return getCheckoutFlowStrategy().getCheckoutFlow();
	}

	@Override
	public B2BCheckoutPciOptionEnum getSubscriptionPciOption()
	{
		return getCheckoutPciStrategy().getSubscriptionPciOption();
	}

	protected B2BCheckoutFlowStrategy getCheckoutFlowStrategy()
	{
		return checkoutFlowStrategy;
	}

	@Required
	public void setCheckoutFlowStrategy(final B2BCheckoutFlowStrategy strategy)
	{
		this.checkoutFlowStrategy = strategy;
	}

	protected B2BCheckoutPciStrategy getCheckoutPciStrategy()
	{
		return this.b2BCheckoutPciStrategy;
	}

	@Required
	public void setCheckoutPciStrategy(final B2BCheckoutPciStrategy strategy)
	{
		this.b2BCheckoutPciStrategy = strategy;
	}
}
