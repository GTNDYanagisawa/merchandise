package de.hybris.platform.sap.sapcarintegration.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelOrderHistoryService;


@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultMultichannelOrderHistoryServiceTest extends SapcoreSpringJUnitTest{

	
	@Resource
	private MultichannelOrderHistoryService multichannelOrderHistoryService;
	

	
	
	@Test
	public void testReadMultiChannelTransactionsForCustomer() {
		
		final String customerNumber = "0000001000";

		final PaginationData paginationData = new PaginationData();
		
		final List<CarMultichannelOrderHistoryData> orders = multichannelOrderHistoryService.readMultiChannelTransactionsForCustomer(customerNumber, paginationData);
		
		assertNotNull(orders);
		
		assertTrue(orders.size() > 0);
		
		assertEquals(orders.get(0).getStore().getStoreId(), "R308");
		
		
	}
	
	@Test
	public void testReadSalesDocumentDetails(){
		
		
		String customerNumber = "0000001000";
		String transactionNumber="0000000115";
		
		CarMultichannelOrderHistoryData order = multichannelOrderHistoryService.readSalesDocumentDetails(customerNumber, transactionNumber);

		assertNotNull(order.getOrderEntries());
		
		assertTrue(order.getOrderEntries().size() > 0);
		
		assertNotNull(order.getOrderEntries().get(0).getProduct().getCode());
		
	}
	
	
}
