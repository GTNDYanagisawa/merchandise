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
package de.hybris.platform.b2bpunchoutaddon.controllers.pages;

import de.hybris.platform.b2bacceleratorfacades.order.B2BCartFacade;
import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.CartPageController;

import javax.annotation.Resource;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Controller for Punch Out Inspect operation.
 */
@Component
@RequestMapping(value = "/punchout/cxml/inspect")
public class DefaultInspectCartController extends CartPageController implements PunchOutController
{

	private static final String INSPECT_PAGE = "/pages/cart/inspect";

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showCart(final Model model) throws CMSItemNotFoundException
	{
		super.prepareDataForPage(model);
		setNotUpdatable(model);
		model.addAttribute("isInspectOperation", true);

		return B2bpunchoutaddonConstants.VIEW_PAGE_PREFIX + INSPECT_PAGE;
	}

	/**
	 * Set items from cart as not updatable.
	 * 
	 * @param model
	 *           Model that will contain the cart.
	 */
	private void setNotUpdatable(final Model model)
	{
		final CartData cartData = cartFacade.getSessionCart();
		for (final OrderEntryData entry : cartData.getEntries())
		{
			entry.setUpdateable(false);
		}
		model.addAttribute("cartData", cartData);
	}
}
