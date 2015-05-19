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
package de.hybris.platform.sap.sapordermgmtservices.unittests;



import de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl.DefaultBolCartFacadeUnitTest;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl.DefaultBolOrderFacadeUnitTest;
import de.hybris.platform.sap.sapordermgmtservices.cart.impl.DefaultAddToCartStrategyTest;
import de.hybris.platform.sap.sapordermgmtservices.cart.impl.DefaultCartRestorationServiceTest;
import de.hybris.platform.sap.sapordermgmtservices.cart.impl.DefaultCartServiceTest;
import de.hybris.platform.sap.sapordermgmtservices.checkout.impl.DefaultCheckoutServiceUnitTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultAbstractOrderEntryPopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultAbstractOrderPopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultCartPopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultDeliveryModePopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultMessagePopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultOrderPopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultScheduleLinePopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.impl.DefaultBackendAvailabilityServiceTest;
import de.hybris.platform.sap.sapordermgmtservices.messagemappingcallback.DefaultInternalNumberReplacementMsgMappingCallbackTest;
import de.hybris.platform.sap.sapordermgmtservices.messagemappingcallback.DefaultSapProductIdReplacementMsgMappingCallbackTest;
import de.hybris.platform.sap.sapordermgmtservices.order.impl.DefaultOrderServiceTest;
import de.hybris.platform.sap.sapordermgmtservices.prodconf.impl.DefaultProductConfigurationServiceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses(
{ DefaultCartServiceTest.class, // 
		DefaultCartRestorationServiceTest.class, //
		DefaultBolCartFacadeUnitTest.class, //
		DefaultBolOrderFacadeUnitTest.class, //
		DefaultAbstractOrderPopulatorTest.class, //
		DefaultAbstractOrderEntryPopulatorTest.class, //
		DefaultCartPopulatorTest.class, //
		DefaultOrderPopulatorTest.class, //
		DefaultCheckoutServiceUnitTest.class, //
		DefaultDeliveryModePopulatorTest.class, //
		DefaultMessagePopulatorTest.class, //
		DefaultScheduleLinePopulatorTest.class, //
		DefaultSapProductIdReplacementMsgMappingCallbackTest.class, //
		DefaultBackendAvailabilityServiceTest.class, //
		DefaultInternalNumberReplacementMsgMappingCallbackTest.class, //
		DefaultProductConfigurationServiceTest.class, //
		DefaultAddToCartStrategyTest.class, //
		DefaultOrderServiceTest.class //

})
public class UnitTestSuite
{
	//
}
