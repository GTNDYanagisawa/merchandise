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
package de.hybris.platform.b2b.punchout.actions;

import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.yb2bacceleratorfacades.flow.B2BCheckoutFlowFacade;

import org.cxml.CXML;
import org.springframework.beans.factory.annotation.Required;


/**
 * Prepares a cart for processing/populating by setting the required details.
 */
public class PrepareCartPurchaseOrderProcessingAction implements PunchOutProcessingAction<CXML, CartModel>
{

	private B2BCheckoutFlowFacade b2bCheckoutFlowFacade;
	private PunchOutConfigurationService punchOutConfigurationService;

	@Override
	public void process(final CXML input, final CartModel output)
	{
		output.setPunchOutOrder(Boolean.TRUE);

		final String paymentType = CheckoutPaymentType.ACCOUNT.getCode();
		final String costCenterCode = getPunchOutConfigurationService().getDefaultCostCenter();

		b2bCheckoutFlowFacade.setPaymentTypeSelectedForCheckout(paymentType);
		b2bCheckoutFlowFacade.setCostCenterForCart(costCenterCode, output.getCode());
	}

	public B2BCheckoutFlowFacade getB2bCheckoutFlowFacade()
	{
		return b2bCheckoutFlowFacade;
	}

	@Required
	public void setB2bCheckoutFlowFacade(final B2BCheckoutFlowFacade b2bCheckoutFlowFacade)
	{
		this.b2bCheckoutFlowFacade = b2bCheckoutFlowFacade;
	}

	public PunchOutConfigurationService getPunchOutConfigurationService()
	{
		return punchOutConfigurationService;
	}

	@Required
	public void setPunchOutConfigurationService(final PunchOutConfigurationService punchOutConfigurationService)
	{
		this.punchOutConfigurationService = punchOutConfigurationService;
	}


}
