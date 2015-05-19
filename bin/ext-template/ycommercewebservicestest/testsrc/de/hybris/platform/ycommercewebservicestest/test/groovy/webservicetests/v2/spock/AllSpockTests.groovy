package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.access.AccessRightsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.access.OAuth2Test
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartDeliveryTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartEntriesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartMergeTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartPromotionsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartResourceTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartVouchersTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.GuestsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.OrderPlacementTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.catalogs.CatalogsResourceTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.customergroups.CustomerGroupsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.errors.ErrorTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.export.ExportTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.filters.CartMatchingFilterTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.filters.UserMatchingFilterTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.flows.AddressBookFlow
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.flows.CartFlowTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.general.StateTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.CardTypesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.CurrenciesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.DeliveryCountriesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.LanguagesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.TitlesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.orders.OrdersTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.products.ProductResourceTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.products.ProductsStockTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.promotions.PromotionsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.stores.StoresTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.UserAccountTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.UserAddressTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.UserOrdersTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.UserPaymentsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.UsersResourceTest

import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([AccessRightsTest, OAuth2Test, StateTest, CartDeliveryTest, CartMergeTest, CartEntriesTest, CartPromotionsTest,
CartResourceTest, CartVouchersTest, GuestsTest, OrderPlacementTest, CatalogsResourceTest, CustomerGroupsTest, ErrorTest, ExportTest,
AddressBookFlow, CartFlowTest, CardTypesTest, CurrenciesTest, DeliveryCountriesTest, LanguagesTest, TitlesTest, OrdersTest, ProductResourceTest, ProductsStockTest, PromotionsTest, StoresTest, UserAccountTest,
UserAddressTest, UserOrdersTest, UserPaymentsTest, UsersResourceTest, CartMatchingFilterTest, UserMatchingFilterTest])
class AllSpockTests {
	@BeforeClass
	public static void setUpClass() {
		//dummy setup class, if its not provided parent class is not created
	}
}
