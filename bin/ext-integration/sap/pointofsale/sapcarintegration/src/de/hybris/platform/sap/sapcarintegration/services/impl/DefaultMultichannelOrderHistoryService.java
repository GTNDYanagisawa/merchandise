package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.util.List;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelDataProviderService;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelOrderHistoryExtractorService;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelOrderHistoryService;

public class DefaultMultichannelOrderHistoryService extends DefaultCarOrderHistoryService implements
		MultichannelOrderHistoryService {
	

	private MultichannelDataProviderService multichannelDataProviderService;
	
	private MultichannelOrderHistoryExtractorService multichannelOrderHistoryExtractorService;
	
	
	public MultichannelDataProviderService getMultichannelDataProviderService() {
		return multichannelDataProviderService;
	}

	@Required
	public void setMultichannelDataProviderService(
			MultichannelDataProviderService multichannelDataProviderService) {
		this.multichannelDataProviderService = multichannelDataProviderService;
	}

	
	public MultichannelOrderHistoryExtractorService getMultichannelOrderHistoryExtractorService() {
		return multichannelOrderHistoryExtractorService;
	}

	@Required
	public void setMultichannelOrderHistoryExtractorService(
			MultichannelOrderHistoryExtractorService multichannelOrderHistoryExtractorService) {
		this.multichannelOrderHistoryExtractorService = multichannelOrderHistoryExtractorService;
	}

	@Override
	public CarMultichannelOrderHistoryData readSalesDocumentDetails(
			String customerNumber, String transactionNumber) {

		ODataFeed headerFeed = getMultichannelDataProviderService()
				.readSalesDocumentHeaderFeed(customerNumber, transactionNumber);

		// read and extract header data
		CarMultichannelOrderHistoryData order = getMultichannelOrderHistoryExtractorService()
				.extractSalesDocumentHeader(headerFeed);

		ODataFeed itemFeed = getMultichannelDataProviderService()
				.readSalesDocumentItemFeed(customerNumber, transactionNumber);
		// read and extract item data
		getMultichannelOrderHistoryExtractorService()
				.extractSalesDocumentEntries(order, itemFeed);
		return order;
	}

	@Override
	public List<CarMultichannelOrderHistoryData> readMultiChannelTransactionsForCustomer(String customerNumber, PaginationData paginationData) {
		//
		ODataFeed feed = getMultichannelDataProviderService()
				.readMultiChannelTransactionsFeed(customerNumber, paginationData);

		// extract trancations
		return getMultichannelOrderHistoryExtractorService().extractMultichannelOrders(paginationData,
				feed);
	}
	

}
