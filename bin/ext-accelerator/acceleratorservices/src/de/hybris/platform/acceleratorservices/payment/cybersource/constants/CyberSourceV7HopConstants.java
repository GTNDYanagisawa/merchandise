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
package de.hybris.platform.acceleratorservices.payment.cybersource.constants;

import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;


/**
 * 
 */
public interface CyberSourceV7HopConstants extends PaymentConstants
{
	interface HopAppearanceProperties
	{
		String BACKGROUND_URL = "hop.cybersource.appearance.backgroundImageURL";
		String COLOR_SCHEME = "hop.cybersource.appearance.colorScheme";
		String BAR_COLOR = "hop.cybersource.appearance.barColor";
		String BAR_TEXT_COLOR = "hop.cybersource.appearance.barTextColor";
		String MESSAGE_BOX_BACKGROUND_COLOR = "hop.cybersource.appearance.messageBoxBackgroundColor";
		String REQUIRED_FIELD_COLOR = "hop.cybersource.appearance.requiredFieldColor";
	}
}
