package de.hybris.platform.sap.sapcarintegration.services;

import java.util.Date;
import java.util.List;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;


public interface CarOrderHistoryService {

	/**
	 * return a list of order data with only header data filled
	 * @param customerNumber
	 * @return
	 */
	@Deprecated
	abstract List<CarOrderHistoryData> readOrdersForCustomer(String customerNumber, String sortBy);
	

	/**
	 * read paginated order history for a customer
	 * @param customerNumber
	 * @param paginationData
	 * 
	 * @return {@link List<CarOrderHistoryData>}
	 */
	abstract List<CarOrderHistoryData> readOrdersForCustomer(String customerNumber, PaginationData paginationData);

	/**
	 * return order history data with header, store and item data filled
	 * @param businessDayDate
	 * @param storeId
	 * @param transactionIndex
	 * @return
	 */
	@Deprecated
	abstract CarOrderHistoryData readOrderDetails(Date businessDayDate, String storeId,
			Integer transactionIndex);
	
	/**
	 * return order history data with header, store and item data filled
	 * @param businessDayDate
	 * @param storeId
	 * @param transactionIndex
	 * @param customerNumber
	 * @return
	 */
	abstract CarOrderHistoryData readOrderDetails(String businessDayDate, String storeId,
			Integer transactionIndex, String customerNumber);


}
