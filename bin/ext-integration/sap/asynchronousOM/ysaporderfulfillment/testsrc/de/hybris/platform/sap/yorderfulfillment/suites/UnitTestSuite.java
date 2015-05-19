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

package de.hybris.platform.sap.yorderfulfillment.suites;


import de.hybris.platform.sap.yorderfulfillment.actions.SendOrderToDataHubActionTest;
import de.hybris.platform.sap.yorderfulfillment.actions.SetCompletionStatusActionTest;
import de.hybris.platform.sap.yorderfulfillment.actions.SetConfirmationStatusActionTest;
import de.hybris.platform.sap.yorderfulfillment.actions.UpdateERPOrderStatusActionTest;
import de.hybris.platform.sap.yorderfulfillment.jobs.OrderCancelRepairJobTest;
import de.hybris.platform.sap.yorderfulfillment.jobs.OrderExchangeRepairJobTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses(
{ UpdateERPOrderStatusActionTest.class, SetConfirmationStatusActionTest.class, SendOrderToDataHubActionTest.class,
		SetCompletionStatusActionTest.class, OrderExchangeRepairJobTest.class, OrderCancelRepairJobTest.class })
public class UnitTestSuite
{
	// Intentionally left blank
}
