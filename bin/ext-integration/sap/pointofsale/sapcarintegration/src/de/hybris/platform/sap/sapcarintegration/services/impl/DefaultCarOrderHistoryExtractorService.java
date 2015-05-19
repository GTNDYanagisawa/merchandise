package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderEntryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreAddress;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreData;
import de.hybris.platform.sap.sapcarintegration.services.CarOrderHistoryExtractorService;
import de.hybris.platform.sap.sapcarintegration.utils.DateUtil;


public class DefaultCarOrderHistoryExtractorService extends AbstractCarOrderHistoryBaseExtractor implements CarOrderHistoryExtractorService
{


	@Override
	PriceData extractTotalNetAmount(Map<String, Object> props) {
		BigDecimal total = new BigDecimal(props.get("SalesAmount") != null ? props.get("SalesAmount").toString() : "0");
		String currency = props.get("TransactionCurrency") != null ? props.get("TransactionCurrency").toString() : "";
		return createPrice(total, currency);
	}

	@Override
	PriceData extractTaxAmount(Map<String, Object> props) {
		BigDecimal taxExcludedAmount = new BigDecimal(props.get("TaxExcludedAmount") != null ? props.get("TaxExcludedAmount")
				.toString() : "0");
		BigDecimal distributedHeaderTaxAmount = new BigDecimal(props.get("DistributedHeaderTaxAmount") != null ? props.get(
				"DistributedHeaderTaxAmount").toString() : "0");
		String currency = props.get("TransactionCurrency") != null ? props.get("TransactionCurrency").toString() : "";
		return createPrice(taxExcludedAmount.add(distributedHeaderTaxAmount), currency);
		
		
		
	}
	
	@Override
	public List<CarOrderHistoryData> extractOrders(final ODataFeed feed)
	{


		final List<CarOrderHistoryData> orders = new ArrayList<CarOrderHistoryData>();

		final List<ODataEntry> entries = feed.getEntries();

		if (entries != null)
		{

			for (final Iterator<ODataEntry> iterator = entries.iterator(); iterator.hasNext();)
			{

				final ODataEntry oDataEntry = iterator.next();

				orders.add(extractOrder(oDataEntry));

			}

		}

		return orders;

	}
	
	@Override
	public List<CarOrderHistoryData> extractOrders(final ODataFeed feed, PaginationData paginationData)
	{


		final List<CarOrderHistoryData> orders = new ArrayList<CarOrderHistoryData>();

		final List<ODataEntry> entries = feed.getEntries();

		if (entries != null)
		{
			paginationData.setTotalNumberOfResults(feed.getFeedMetadata().getInlineCount());

			for (final Iterator<ODataEntry> iterator = entries.iterator(); iterator.hasNext();)
			{

				final ODataEntry oDataEntry = iterator.next();

				orders.add(extractOrder(oDataEntry));

			}

		}

		return orders;

	}
	

	@Override
	public CarOrderHistoryData extractOrder(final ODataFeed feed)
	{

		final List<ODataEntry> entries = feed.getEntries();

		CarOrderHistoryData order = null;

		if (entries != null)
		{

			order = extractOrder(feed.getEntries().iterator().next());

		}

		return order;

	}


	protected CarOrderHistoryData extractOrder(final ODataEntry entry)
	{


		final CarOrderHistoryData order = new CarOrderHistoryData();

		final Map<String, Object> props = entry.getProperties();

		if (props != null)
		{
			// store name
			order.setPurchaseOrderNumber(props.get("TransactionNumber") != null ? props.get("TransactionNumber").toString() : "");
			order.setBusinessDayDate(props.get("BusinessDayDate") != null ? props.get("BusinessDayDate").toString() : "");
			order.setFormattedBusinessDayDate(props.get("BusinessDayDate") != null ? props.get("BusinessDayDate").toString() : "");
			
			order.setTransactionDate(DateUtil.formatDateTime(props.get("BeginTimestamp") != null ? props.get("BeginTimestamp").toString() : ""));

			order.setTransactionIndex(props.get("TransactionIndex") != null ? Integer.valueOf(props.get("TransactionIndex").toString())
					: Integer.valueOf(0));
			order.setOperatorId(props.get("OperatorID") != null ? props.get("OperatorID").toString() : "");

			order.setPurchaseOrderNumber(props.get("TransactionNumber") != null ? props.get("TransactionNumber").toString() : "");
			order.setOrderChannelName(props.get("OrderChannel") != null ? props.get("OrderChannel").toString() : "");
			order.setOrderChannelName(props.get("OrderChannelName") != null ? props.get("OrderChannelName").toString() : "");
			// store data
			final CarStoreData storeData = new CarStoreData();
			storeData.setStoreId(props.get("RetailStoreID") != null ? props.get("RetailStoreID").toString() : "");
			storeData.setStoreName(props.get("LocationName") != null ? props.get("LocationName").toString() : "");
			order.setStore(storeData);
			// add price data
			order.setSubTotal(extractTotalNetAmount(props));

			// add tax data
			order.setTotalTax(extractTaxAmount(props));

			// add tax to total
			order.setTotalPriceWithTax(createPrice(order.getSubTotal().getValue().add(order.getTotalTax().getValue()), order.getSubTotal().getCurrencyIso()));

		}


		return order;

	}


	/**
	 * extract entries and append them to the order
	 * 
	 * @param entry
	 * @return
	 */
	@Override
	public void extractOrderEntries(final CarOrderHistoryData order, final ODataFeed feed)
	{

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

	}


	protected CarOrderEntryData extractOrderEntry(final ODataEntry odataEntry, final int entryNumber)
	{


		final Map<String, Object> props = odataEntry.getProperties();

		if (props != null)
		{

			final CarOrderEntryData orderEntry = new CarOrderEntryData();
			final BigDecimal quantity = props.get("SalesQuantityInSalesUnit") != null ? new BigDecimal(props.get(
					"SalesQuantityInSalesUnit").toString()) : BigDecimal.ONE;
			orderEntry.setQuantity(quantity);
			orderEntry.setEntryNumber(entryNumber);

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
			orderEntry.setProduct(productData);

			return orderEntry;

		}

		return null;


	}

	@Override
	public CarStoreAddress extractStoreLocation(final ODataFeed feed)
	{


		final List<ODataEntry> foundEntries = feed.getEntries();

		final ODataEntry entry = foundEntries.iterator().next();

		final Map<String, Object> props = entry.getProperties();

		final CarStoreAddress storeAddress = new CarStoreAddress();

		storeAddress.setHouseNumber(props.get("HouseNumber") != null ? props.get("HouseNumber").toString() : "");
		storeAddress.setStreet(props.get("StreetName") != null ? props.get("StreetName").toString() : "");
		storeAddress.setPoBox(props.get("POBox") != null ? props.get("POBox").toString() : "");
		storeAddress.setZip(props.get("PostalCode") != null ? props.get("PostalCode").toString() : "");
		storeAddress.setCity(props.get("CityName") != null ? props.get("CityName").toString() : "");
		storeAddress.setCountryCode(props.get("Country") != null ? props.get("Country").toString() : "");

		return storeAddress;
	}

}
