package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2

import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([CartMatchingFilterTests.class, CartTests.class, CatalogTests.class, CustomerGroupTests.class,
	ErrorTests.class, ExportTests.class, FlowTests.class, GuestsTest.class, MiscTests.class, OAuth2Tests.class, OrderTests.class,
	ProductTests.class, PromotionTests.class, StoresTests.class, UserMatchingFilterTests.class, UsersTest.class,
	VoucherTests.class])
class AllTests {
	@BeforeClass
	public static void setUpClass() {
		//dummy setup class, if its not provided parent class is not create`d
	}
}
