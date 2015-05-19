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
package de.hybris.platform.integration.cis.tax.constants;

/**
 * Global class for all Cistax constants. You can add global constants for your extension into this class.
 */
public final class CistaxConstants extends GeneratedCistaxConstants
{
	public static final String EXTENSIONNAME = "cistax";

	/**
	 * Product code that we send to CIS tax service tp for the delivery cost.
	 */
	public static final String EXTERNALTAX_DELIVERY_LINEITEM_ID = "delivery line item";

	/**
	 * Product description that we send to CIS tax service tp for the delivery cost.
	 */
	public static final String EXTERNALTAX_DELIVERY_DESCRIPTION = "Delivery Cost";


	private CistaxConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
