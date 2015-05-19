package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.constants.SapcarintegrationConstants;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderEntryData;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreData;
import de.hybris.platform.sap.sapcarintegration.enums.OrderChannelTypeEnum;
import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelOrderHistoryExtractorService;
import de.hybris.platform.sap.sapcarintegration.utils.DateUtil;

public class DefaultMultichannelOrderHistoryExtractorService extends DefaultCarOrderHistoryExtractorService implements
		MultichannelOrderHistoryExtractorService {

	
	private CarConfigurationService carConfigurationService;
	

	public CarConfigurationService getCarConfigurationService() {
		return carConfigurationService;
	}

	@Required
	public void setCarConfigurationService(
			CarConfigurationService carConfigurationService) {
		this.carConfigurationService = carConfigurationService;
	}

	@Override
	public List<CarMultichannelOrderHistoryData> extractMultichannelOrders(PaginationData paginationData,
			ODataFeed feed) {
		
		List<CarMultichannelOrderHistoryData> orders = null;

		final List<ODataEntry> entries = feed.getEntries();

		if (entries != null && entries.size() > 0)
		{
			orders = new ArrayList<CarMultichannelOrderHistoryData>();
			// extract total pages
			paginationData.setTotalNumberOfResults(feed.getFeedMetadata().getInlineCount());

			for (final Iterator<ODataEntry> iterator = entries.iterator(); iterator.hasNext();)
			{

				final ODataEntry oDataEntry = iterator.next();

				orders.add(extractMultichannelOrder(oDataEntry));

			}

		}
		
		if (orders == null) {
			
			return Collections.emptyList() ;
		}
		
		return orders;

	}
	
	protected CarMultichannelOrderHistoryData extractMultichannelOrder(final ODataEntry entry)
	{


		final CarMultichannelOrderHistoryData order = new CarMultichannelOrderHistoryData();

		final Map<String, Object> props = entry.getProperties();

		if (props != null)
		{
			// store name
			order.setPurchaseOrderNumber(props.get("TransactionNumber") != null ? props.get("TransactionNumber").toString() : "");
			
			
			String businessDayDate = props.get("BusinessDayDate") != null ? props.get("BusinessDayDate").toString() : "";
				
			order.setBusinessDayDate(businessDayDate);
			
			if (props.get("TransactionIndex") != null && !StringUtils.isEmpty(props.get("TransactionIndex").toString())) {
				order.setTransactionIndex (Integer.valueOf(props.get("TransactionIndex").toString()));
			}
			
			// set transaction date
			String beginDateStr = SapcarintegrationConstants.DEFAULT_DATE;
			
			if (props.get("CreationDate") != null && !StringUtils.isEmpty(props.get("CreationDate").toString())) {
				beginDateStr = props.get("CreationDate").toString();
			}
			
			String beginTimeStr = SapcarintegrationConstants.DEFAULT_TIME;
			
			if (props.get("CreationTime") != null && !StringUtils.isEmpty(props.get("CreationTime").toString())) {
				beginTimeStr = props.get("CreationTime").toString();
			}
			
			// constract transaction date by adding date to time
			order.setTransactionDate(new Date (DateUtil.formatDate(beginDateStr).getTime() + DateUtil.formatTime(beginTimeStr).getTime()));
			
			order.setPurchaseOrderNumber(props.get("TransactionNumber") != null ? props.get("TransactionNumber").toString() : "");
			order.setOrderChannel(props.get("OrderChannel") != null ? props.get("OrderChannel").toString() : "");
			order.setOrderChannelName(props.get("OrderChannelName") != null ? props.get("OrderChannelName").toString() : "");
			
			if (props.get("RetailStoreID") != null && !StringUtils.isEmpty(props.get("RetailStoreID"))) {
				final CarStoreData storeData = new CarStoreData();
				storeData.setStoreId(props.get("RetailStoreID").toString());
				order.setStore(storeData);
			}
			
			//extract sales area
			order.setSalesOrganization(props.get("SalesOrganization") != null ? props.get("SalesOrganization").toString() : "");
			order.setDistributionChannel(props.get("DistributionChannel") != null ? props.get("DistributionChannel").toString() : "");
			order.setOrganizationDivision(props.get("OrganizationDivision") != null ? props.get("OrganizationDivision").toString() : "");
			
			// extract document type
			order.setSalesDocumentType(props.get("SalesDocumentType") != null ? props.get("SalesDocumentType").toString() : "");
			
			// process status
			order.setOverallOrderProcessStatus(props.get("OverallOrderProcessStatus") != null ? props.get("OverallOrderProcessStatus").toString() : "");
			order.setOverallOrderProcessStatusDesc(props.get("OverallOrderProcessStatusDesc") != null ? props.get("OverallOrderProcessStatusDesc").toString() : "");
			
			order.setOrderChannelTypeEnum(determineOrderChannelType(order));
			
			order.setSubTotal(extractTotalNetAmount(props));
			
			order.setTotalTax(extractTaxAmount(props));
			
			order.setTotalPriceWithTax(createPrice(order.getSubTotal().getValue().add(order.getTotalTax().getValue()), order.getSubTotal().getCurrencyIso()));
		}


		return order;

	}


	@Override
	public CarMultichannelOrderHistoryData extractSalesDocumentHeader(
			ODataFeed feed) {
		final List<ODataEntry> entries = feed.getEntries();

		CarMultichannelOrderHistoryData order = null;

		if (entries != null)
		{

			order = extractMultichannelOrder(feed.getEntries().iterator().next());

		}

		return order;
		
	}

	@Override
	public void extractSalesDocumentEntries(CarMultichannelOrderHistoryData order,
			ODataFeed feed) {
		final List<CarOrderEntryData> orderEntries = new ArrayList<CarOrderEntryData>();

		final List<ODataEntry> entries = feed.getEntries();

		int entryNumber = 0;

		if (entries != null)
		{

			for (final Iterator<ODataEntry> iterator = entries.iterator(); iterator.hasNext();)
			{

				entryNumber++;
				final ODataEntry oDataEntry = iterator.next();

				final CarOrderEntryData orderEntry = extractOrderEntry(oDataEntry, entryNumber);

				if (orderEntry != null)
				{
					orderEntries.add(orderEntry);
				}


			}

		}

		order.setOrderEntries(orderEntries);
		
		return ;
	}
	
	@Override
	protected CarOrderEntryData extractOrderEntry(final ODataEntry odataEntry, final int entryNumber)
	{


		final Map<String, Object> props = odataEntry.getProperties();

		if (props != null)
		{

			final CarOrderEntryData orderEntry = new CarOrderEntryData();
			final BigDecimal quantity = props.get("OrderQuantity") != null ? new BigDecimal(props.get(
					"OrderQuantity").toString()) : BigDecimal.ONE;			
			orderEntry.setQuantity(quantity);
			orderEntry.setEntryNumber(entryNumber);
			orderEntry.setUnitOfMeasure(props.get("UnitOfMeasure_E") != null ? props.get("UnitOfMeasure_E").toString() : "");
			
			PriceData netAmount = extractTotalNetAmount(props);
			PriceData taxAmount = extractTaxAmount(props);
			
			// base prices
			orderEntry.setBasePrice(createBasePrice(netAmount, quantity));
			orderEntry.setBasePriceWithTax(createBasePriceWithTax(netAmount, taxAmount, quantity));
			
			// totals
			orderEntry.setTotalPrice(netAmount);
			orderEntry.setTotalPriceWithTax(createPriceWithTax(netAmount.getValue(), taxAmount.getValue(), netAmount.getCurrencyIso()));
		
			// extract product data
			final ProductData productData = new ProductData();
			productData.setCode(props.get("Article") != null ? props.get("Article").toString() : "");
			productData.setName(props.get("ArticleName") != null ? props.get("ArticleName").toString() : "");
			//orderEntry.set
			orderEntry.setProduct(productData);
			

			return orderEntry;

		}

		return null;


	}
	
	@Override
	PriceData extractTotalNetAmount(Map<String, Object> props) {
		BigDecimal total = new BigDecimal(props.get("TotalNetAmount") != null ? props.get("TotalNetAmount").toString() : "0");
		String currency = props.get("TransactionCurrency") != null ? props.get("TransactionCurrency").toString() : "";
		return createPrice(total, currency);
	}

	@Override
	PriceData extractTaxAmount(Map<String, Object> props) {
		BigDecimal taxAmount = new BigDecimal(props.get("TaxAmount") != null ? props.get("TaxAmount")
				.toString() : "0");
		String currency = props.get("TransactionCurrency") != null ? props.get("TransactionCurrency").toString() : "";
		return createPrice(taxAmount, currency);
		
		
		
	}
	
	/**
	 * utility method to determine order Type
	 * storeId != null ---> POS Order
	 * salesOrg + DisChannel + DocumentType == Current Store Config ---> Hybris web Order
	 * Otherwise its an SD ORder
	 * @param order
	 * @return
	 */
	protected OrderChannelTypeEnum determineOrderChannelType(CarMultichannelOrderHistoryData order){
		
		if (order.getStore() != null && order.getStore().getStoreId() != null &&  !order.getStore().getStoreId().isEmpty()) {
			
			return OrderChannelTypeEnum.POS;
			
		// check if the order is made from current web shop	
		} else if (order.getSalesOrganization() != null && 
				 order.getDistributionChannel()!=null && 
				 order.getSalesDocumentType() != null &&
			     getCarConfigurationService().getSalesOrganization().equalsIgnoreCase(order.getSalesOrganization())&&
			     getCarConfigurationService().getDistributionChannel().equalsIgnoreCase(order.getDistributionChannel()) &&
			     getCarConfigurationService().getTransactionType().equalsIgnoreCase(order.getSalesDocumentType())
				){
			
			return OrderChannelTypeEnum.WEB;
			
		}else{
			
			return OrderChannelTypeEnum.SD;	
			
		}
				
	}
	
}
