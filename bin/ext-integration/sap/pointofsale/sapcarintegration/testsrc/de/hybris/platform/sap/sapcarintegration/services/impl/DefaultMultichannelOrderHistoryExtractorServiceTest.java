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
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreAddress;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelDataProviderService;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelOrderHistoryExtractorService;


@ContextConfiguration(locations =
{ "classpath:test/sapcarintegration-test-spring.xml" })
public class DefaultMultichannelOrderHistoryExtractorServiceTest extends SapcoreSpringJUnitTest
{

	@Resource(name = "multichannelOrderHistoryExtractorService")
	private MultichannelOrderHistoryExtractorService extractorService;

	@Resource(name = "multichannelDataProviderService")
	private MultichannelDataProviderService dataProviderService;


	@Test
	public void testExtractMultichannelOrders() throws Exception
	{

		final String customerNumber = "0000001000";

		final PaginationData paginationData = new PaginationData();
		
		final ODataFeed feed = dataProviderService.readMultiChannelTransactionsFeed(customerNumber, paginationData);


		final List<CarMultichannelOrderHistoryData> orders = extractorService.extractMultichannelOrders(paginationData, feed);
		
		assertNotNull(orders);
		
		assertTrue(orders.size() > 0);
		
		assertEquals(orders.get(0).getStore().getStoreId(), "R308");

	}

	@Test
	public void testExtractSalesDocumentHeader() throws Exception
	{

		String customerNumber = "0000001000";
		String transactionNumber = "0000000115";
		
		final ODataFeed feed = dataProviderService.readSalesDocumentHeaderFeed(customerNumber, transactionNumber );
		
		CarMultichannelOrderHistoryData order = extractorService.extractSalesDocumentHeader(feed);
		
		assertNotNull(order);
		
	}
	
	@Test
	public void testExtractSalesDocumentEntries(){
		
		String customerNumber = "0000001000";		
		String transactionNumber= "0000000115";
		final ODataFeed orderFeed = dataProviderService.readSalesDocumentHeaderFeed(customerNumber, transactionNumber);

		CarMultichannelOrderHistoryData order = extractorService.extractSalesDocumentHeader(orderFeed);
		
		final ODataFeed itemFeed = dataProviderService.readSalesDocumentItemFeed(customerNumber, transactionNumber);
		
		extractorService.extractSalesDocumentEntries(order, itemFeed);
		
		assertNotNull(order.getOrderEntries());
		
		assertTrue(order.getOrderEntries().size() > 0);
		
		assertNotNull(order.getOrderEntries().get(0).getProduct().getCode());
	}
	
		

}
