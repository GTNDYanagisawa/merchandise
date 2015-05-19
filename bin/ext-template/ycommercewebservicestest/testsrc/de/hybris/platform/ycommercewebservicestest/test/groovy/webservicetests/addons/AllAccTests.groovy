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
 */

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.addons

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.addons.v2.ExtendedCartV2Tests

import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([ExtendedCartTests.class,AccProductTests.class, ExtendedCustomersTests.class, ExtendedCustomersTests.class,ExtendedCartV2Tests.class])
class AllAccTests {
	@BeforeClass
	public static void setUpClass() {
		//dummy setup class, if its not provided parent class is not created
	}
}
