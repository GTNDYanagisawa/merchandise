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
package de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.checkout;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.yb2bacceleratorfacades.flow.B2BCheckoutFlowFacade;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.AbstractPageController;

import javax.annotation.Resource;

/**
 * Base controller for all checkout page controllers. Provides common functionality for all checkout page controllers.
 */
public abstract class AbstractCheckoutController extends AbstractPageController
{
	@Resource(name = "b2bCheckoutFlowFacade")
	private B2BCheckoutFlowFacade checkoutFlowFacade;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	protected B2BCheckoutFlowFacade getCheckoutFlowFacade()
	{
		return checkoutFlowFacade;
	}

	protected I18NFacade getI18NFacade()
	{
		return i18NFacade;
	}

	/**
	 * Checks if there are any items in the cart.
	 * 
	 * @return returns true if items found in cart.
	 */
	protected boolean hasItemsInCart()
	{
		final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

		return (cartData.getEntries() != null && !cartData.getEntries().isEmpty());
	}
}
