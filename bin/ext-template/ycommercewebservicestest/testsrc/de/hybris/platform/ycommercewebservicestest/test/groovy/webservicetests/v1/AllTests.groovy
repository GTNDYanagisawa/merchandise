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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1

import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([MiscTests.class,
FlowTests.class, CustomerTests.class, CartTests.class, CatalogTests.class,
OAuth2Tests.class, ProductTests.class, OCCDemo_Oauth2.class, LogoutTests.class,
CustomerGroupTests.class, HttpsRestrictedUrlsTests.class, StoresTests.class,
AddressTest.class, PromotionTests.class, VoucherTests.class, OrderTests.class, GuestCheckoutTests.class, ErrorTests.class])
class AllTests {
	@BeforeClass
	public static void setUpClass() {
		//dummy setup class, if its not provided parent class is not created
	}
}
