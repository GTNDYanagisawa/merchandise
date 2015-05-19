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
package de.hybris.platform.cybersource;

import de.hybris.platform.cybersource.commands.CybersourceCommandTest;
import de.hybris.platform.cybersource.commands.IsApplicableCommandTest;
import de.hybris.platform.cybersource.services.card.CardAuthorizationTest;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImplTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
{
//
		UtilTest.class, //
		IsApplicableCommandTest.class, //
		CardAuthorizationTest.class, //
		CybersourceCommandTest.class, //
		DefaultCardPaymentServiceImplTest.class //
})
public class CybersourceTestSuite
{
	//
}
