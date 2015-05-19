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
package de.hybris.platform.sap.sappricingproductcarouseladdon.controllers;

import de.hybris.platform.sap.sappricingproductcarouseladdon.model.SapProductCarouselComponentModel;


/**
 */
public interface SappricingproductcarouseladdonControllerConstants
{
	// implement here controller constants used by this extension
	/**
	 * Controller request mapping view
	 */
	public static final String VIEW = "/view/" + SapProductCarouselComponentModel._TYPECODE + "Controller";

	/**
	 * Controller
	 */
	public static final String CONTROLLER = SapProductCarouselComponentModel._TYPECODE + "Controller";

}
