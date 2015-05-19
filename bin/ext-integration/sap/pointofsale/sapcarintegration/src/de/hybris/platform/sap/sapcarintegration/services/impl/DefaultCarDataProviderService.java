package de.hybris.platform.sap.sapcarintegration.services.impl;


import java.util.Date;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.constants.SapcarintegrationConstants;
import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;
import de.hybris.platform.sap.sapcarintegration.services.CarConnectionService;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;
import de.hybris.platform.sap.sapcarintegration.utils.DateUtil;


/**
 *
 */
public class DefaultCarDataProviderService extends AbstractODataFeedService implements CarDataProviderService
{

	
	public CarConfigurationService getCarConfigurationService() {
		return super.getCarConfigurationService();
	}

	@Required
	public void setCarConfigurationService(
			CarConfigurationService carConfigurationService) {
		super.setCarConfigurationService(carConfigurationService);
	}


	public CarConnectionService getConnectionService() {
		return super.getCarConnectionService();
	}

	@Required
	public void setConnectionService(CarConnectionService connectionService) {
		super.setCarConnectionService(connectionService);
	}

	
	@Override
	@Deprecated
	public ODataFeed readHeaderFeed(String customerNumber, String sortBy){

		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");
		
		// TODO append pagination here
		
		return readFeed(getCarConfigurationService().getRootUrl() + getServiceName(), 
						SapcarintegrationConstants.APPLICATION_JSON,
						SapcarintegrationConstants.POSSALES_ENTITYSETNAME_QUERY, 
						SapcarintegrationConstants.POSSALES_ENTITYSETNAME_RESULT,
						SapcarintegrationConstants.SELECT_POSSALES_HEADER, 
						queryFilter.toString(), 
						null);
	}
	
	@Override
	public ODataFeed readHeaderFeed(String customerNumber, PaginationData paginationData) {
		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");
		queryFilter.append("$skip").append("=").append(paginationData.getCurrentPage() * paginationData.getPageSize());
		queryFilter.append("$top").append("=").append(paginationData.getPageSize());
		queryFilter.append("$inlinecount").append("=").append("allpages");
		
		if (!StringUtils.isEmpty(paginationData.getSort())){
			
			if (paginationData.getSort().contentEquals("byDate")){
				
				queryFilter.append("&$orderby").append("TransactionDate desc");
				
			} else if (paginationData.getSort().contentEquals("byOrderNumber")) {
				queryFilter.append("&$orderby").append("TransactionNumber desc");
			}
			
		}

		return readFeed(getCarConfigurationService().getRootUrl() + getServiceName(), 
						SapcarintegrationConstants.APPLICATION_JSON,
						SapcarintegrationConstants.POSSALES_ENTITYSETNAME_QUERY, 
						SapcarintegrationConstants.POSSALES_ENTITYSETNAME_RESULT,
						SapcarintegrationConstants.SELECT_POSSALES_HEADER, 
						queryFilter.toString(), 
						null);
	}
	
	@Deprecated
	public ODataFeed readHeaderFeed(Date businessDayDate, String storeId,
			Integer transactionIndex){
		return readHeaderFeed(DateUtil.formatDate(businessDayDate), storeId, transactionIndex, null);
	}
	
	
	@Deprecated
	public ODataFeed readItemFeed(Date businessDayDate, String storeId, Integer transactionIndex){
		return this.readItemFeed(DateUtil.formatDate(businessDayDate), storeId, transactionIndex, null);
	}

	
	@Override
	public ODataFeed readLocaltionFeed(String location) {

		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("Location").append(" eq ").append("'").append(location).append("'");
		
		return readFeed(getRootUrl() + getServiceName(), 
				SapcarintegrationConstants.APPLICATION_JSON,
				SapcarintegrationConstants.LOCATION_ENTITYSETNAME_QUERY, 
				SapcarintegrationConstants.LOCATION_ENTITYSETNAME_RESULT,
				SapcarintegrationConstants.SELECT_LOCATION, 
						queryFilter.toString(), 
						null);
	}
	
	
	@Override
	public ODataFeed readHeaderFeed(String businessDayDate, String storeId, Integer transactionIndex, String customerNumber)
	{
		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("BusinessDayDate").append(" eq ").append("'").append(businessDayDate).append("'");
		queryFilter.append(" and ").append("RetailStoreID").append(" eq ").append("'").append(storeId).append("'");
		queryFilter.append(" and ").append("TransactionIndex").append(" eq ").append(transactionIndex);
		if (customerNumber != null)
		{
			queryFilter.append(" and ").append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");
		}

		return readFeed(getRootUrl() + getServiceName(), 
				SapcarintegrationConstants.APPLICATION_JSON, 
				SapcarintegrationConstants.POSSALES_ENTITYSETNAME_QUERY,
				SapcarintegrationConstants.POSSALES_ENTITYSETNAME_RESULT, 
				SapcarintegrationConstants.SELECT_POSSALES_HEADER, 
				queryFilter.toString(), 
				null);
	}

	@Override
	public ODataFeed readItemFeed(String businessDayDate, String storeId, Integer transactionIndex, String customerNumber)
	{

		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("BusinessDayDate").append(" eq ").append("'").append(businessDayDate).append("'");
		queryFilter.append(" and ").append("RetailStoreID").append(" eq ").append("'").append(storeId).append("'");
		queryFilter.append(" and ").append("TransactionIndex").append(" eq ").append(transactionIndex);
		if (customerNumber != null)
		{
			queryFilter.append(" and ").append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");
		}

		return readFeed(getRootUrl() + getServiceName(), 
						SapcarintegrationConstants.APPLICATION_JSON,
						SapcarintegrationConstants.POSSALES_ENTITYSETNAME_QUERY, 
						SapcarintegrationConstants.POSSALES_ENTITYSETNAME_RESULT,
						SapcarintegrationConstants.SELECT_POSSALES_ITEM, 
						queryFilter.toString(), 
						null);
	}

	
}

