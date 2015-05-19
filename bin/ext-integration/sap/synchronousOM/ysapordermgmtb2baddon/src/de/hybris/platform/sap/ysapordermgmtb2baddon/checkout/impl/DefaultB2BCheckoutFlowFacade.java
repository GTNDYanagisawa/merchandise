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
package de.hybris.platform.sap.ysapordermgmtb2baddon.checkout.impl;

import de.hybris.platform.sap.sapordermgmtb2bfacades.checkout.impl.DefaultB2BCheckoutFacade;
import de.hybris.platform.yb2bacceleratorcore.enums.B2BCheckoutFlowEnum;
import de.hybris.platform.yb2bacceleratorcore.enums.B2BCheckoutPciOptionEnum;
import de.hybris.platform.yb2bacceleratorfacades.flow.B2BCheckoutFlowFacade;


/**
 * 
 */
public class DefaultB2BCheckoutFlowFacade extends DefaultB2BCheckoutFacade implements B2BCheckoutFlowFacade
{
	B2BCheckoutFlowFacade b2bCheckoutFlowFacade = null;

	/**
	 * @return the b2bCheckoutFlowFacade
	 */
	public B2BCheckoutFlowFacade getB2bCheckoutFlowFacade()
	{
		return b2bCheckoutFlowFacade;
	}

	/**
	 * @param b2bCheckoutFlowFacade
	 *           the b2bCheckoutFlowFacade to set
	 */
	public void setB2bCheckoutFlowFacade(final B2BCheckoutFlowFacade b2bCheckoutFlowFacade)
	{
		this.b2bCheckoutFlowFacade = b2bCheckoutFlowFacade;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.yb2bacceleratorfacades.flow.B2BCheckoutFlowFacade#getCheckoutFlow()
	 */
	@Override
	public B2BCheckoutFlowEnum getCheckoutFlow()
	{
		return b2bCheckoutFlowFacade.getCheckoutFlow();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.yb2bacceleratorfacades.flow.B2BCheckoutFlowFacade#getSubscriptionPciOption()
	 */
	@Override
	public B2BCheckoutPciOptionEnum getSubscriptionPciOption()
	{
		handleNotSupportedException();
		return null;
	}

}
