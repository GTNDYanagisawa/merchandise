package de.hybris.platform.sap.sapcarintegration.services;

import java.util.List;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;


public interface MultichannelOrderHistoryExtractorService extends
		CarOrderHistoryExtractorService {

	abstract CarMultichannelOrderHistoryData extractSalesDocumentHeader(final ODataFeed feed);
	
	abstract void extractSalesDocumentEntries(final CarMultichannelOrderHistoryData order, final ODataFeed feed);

	abstract List<CarMultichannelOrderHistoryData> extractMultichannelOrders(
			PaginationData paginationData, ODataFeed feed);

}
