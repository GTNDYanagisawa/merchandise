package de.hybris.platform.sap.sapcarintegration.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreAddress;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;
import de.hybris.platform.sap.sapcarintegration.services.CarOrderHistoryExtractorService;


@ContextConfiguration(locations =
{ "classpath:test/sapcarintegration-test-spring.xml" })
public class DefaultCarOrderHistoryExtractorServiceTest extends SapcoreSpringJUnitTest
{

	@Resource(name = "carOrderHistoryExtractorService")
	private CarOrderHistoryExtractorService extractorService;

	@Resource
	private CarDataProviderService carDataProviderService;


	@Test
	public void testExtractOrders() throws Exception
	{

		final String customerNumber = "0000001000";

		final PaginationData paginationData = new PaginationData();
		
		final ODataFeed feed = carDataProviderService.readHeaderFeed(customerNumber, paginationData);


		final List<CarOrderHistoryData> orders = extractorService.extractOrders(feed, paginationData);

		assertNotNull(orders);
		
		assertTrue(orders.size() > 0);
		
		assertEquals(orders.get(0).getStore().getStoreId(), "R100");

	}

	@Test
	public void testExtractOrder() throws Exception
	{

		String storeId = "R100";
		int transactionIndex = 8;
		String customerNumber = "0000001000";
		String businessDayDate = "20140505";
		
		final ODataFeed orderFeed = carDataProviderService.readHeaderFeed(businessDayDate, storeId, transactionIndex, customerNumber);

		final CarOrderHistoryData order = extractorService.extractOrder(orderFeed);

		assertNotNull(order);
		
		assertEquals(order.getStore().getStoreId(), "R100");

	}


	@Test
	public void testExtractLocation() throws Exception
	{

		final String locationName = "R101";

		final ODataFeed feed = carDataProviderService.readLocaltionFeed(locationName);

		final CarStoreAddress address = extractorService.extractStoreLocation(feed);

		assertNotNull(address);
		
		assertNotNull(address.getCountryCode());

	}

	@Test
	public void testExtractOrderEntries() throws Exception
	{

		String storeId = "R100";
		int transactionIndex = 8;
		String customerNumber = "0000001000";
		String businessDayDate = "20140505";
		
		final ODataFeed orderFeed = carDataProviderService.readHeaderFeed(businessDayDate, storeId, transactionIndex, customerNumber);

		final CarOrderHistoryData order = extractorService.extractOrder(orderFeed);
		
		final ODataFeed itemFeed = carDataProviderService.readHeaderFeed(businessDayDate, storeId, transactionIndex, customerNumber);
		
		extractorService.extractOrderEntries(order, itemFeed);
		
		assertNotNull(order.getOrderEntries());
		
		assertTrue(order.getOrderEntries().size() > 0);
		
		assertNotNull(order.getOrderEntries().get(0).getProduct().getCode());

	}


}
