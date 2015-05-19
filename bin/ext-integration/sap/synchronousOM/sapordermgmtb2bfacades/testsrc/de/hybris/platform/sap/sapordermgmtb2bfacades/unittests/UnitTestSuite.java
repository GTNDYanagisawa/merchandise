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
package de.hybris.platform.sap.sapordermgmtb2bfacades.unittests;

import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.DefaultCartFacadeTest;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.DefaultCartRestorationFacadeTest;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.populator.DefaultStandardOrderEntryPopulatorTest;
import de.hybris.platform.sap.sapordermgmtb2bfacades.checkout.impl.DefaultB2BCheckoutFacadeTest;
import de.hybris.platform.sap.sapordermgmtb2bfacades.impl.DefaultProductImageHelperTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 *
 */
@RunWith(Suite.class)
@SuiteClasses(
{ DefaultB2BCheckoutFacadeTest.class, //
		DefaultCartFacadeTest.class, //
		DefaultCartRestorationFacadeTest.class, DefaultStandardOrderEntryPopulatorTest.class, //
		DefaultProductImageHelperTest.class })
public class UnitTestSuite
{
	//
}
