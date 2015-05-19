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
public interface CyberSourceConstants
{
	interface HopProperties extends PaymentConstants.PaymentProperties
	{
		String HOP_TEST_CURRENCY = "hop.cybersource.testCurrency";
		String MERCHANT_ID = "hop.cybersource.merchantID";
		String SHARED_SECRET = "hop.cybersource.sharedSecret";
		String SERIAL_NUMBER = "hop.cybersource.serialNumber";
		String HOP_SETUP_FEE = "hop.cybersource.setupFee";
	}
}
