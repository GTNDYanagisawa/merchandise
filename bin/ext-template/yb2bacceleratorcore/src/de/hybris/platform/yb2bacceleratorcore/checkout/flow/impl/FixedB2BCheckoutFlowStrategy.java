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
package de.hybris.platform.yb2bacceleratorcore.checkout.flow.impl;

import de.hybris.platform.yb2bacceleratorcore.checkout.flow.B2BCheckoutFlowStrategy;
import de.hybris.platform.yb2bacceleratorcore.enums.B2BCheckoutFlowEnum;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * Uses fixed {@link B2BCheckoutFlowEnum} as result. Used most likely on the end of checkout flow strategy chain.
 * 
 * @since 4.6
 */
public class FixedB2BCheckoutFlowStrategy implements B2BCheckoutFlowStrategy
{
	private B2BCheckoutFlowEnum checkoutFlow;

	@Override
	public B2BCheckoutFlowEnum getCheckoutFlow()
	{
		return checkoutFlow;
	}

	@Required
	public void setCheckoutFlow(final B2BCheckoutFlowEnum checkoutFlow)
	{
		this.checkoutFlow = checkoutFlow;
	}
}
