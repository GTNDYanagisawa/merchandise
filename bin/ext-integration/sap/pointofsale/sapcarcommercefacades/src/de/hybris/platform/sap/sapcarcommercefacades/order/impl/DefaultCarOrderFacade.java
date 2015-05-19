/**
 *
 */
package de.hybris.platform.sap.sapcarcommercefacades.order.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcarcommercefacades.order.CarOrderConverter;
import de.hybris.platform.sap.sapcarcommercefacades.order.CarOrderFacade;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelOrderHistoryService;
import de.hybris.platform.sap.sapcarintegration.utils.DateUtil;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * @author I827395
 * 
 */
public class DefaultCarOrderFacade implements CarOrderFacade
{

	private MultichannelOrderHistoryService multichannelOrderHistoryService;

	private UserService userService;

	private CarOrderConverter carOrderConverter;



	public MultichannelOrderHistoryService getMultichannelOrderHistoryService()
	{
		return multichannelOrderHistoryService;
	}

	@Required
	public void setMultichannelOrderHistoryService(final MultichannelOrderHistoryService multichannelOrderHistoryService)
	{
		this.multichannelOrderHistoryService = multichannelOrderHistoryService;
	}

	public CarOrderConverter getCarOrderConverter()
	{
		return carOrderConverter;
	}

	public void setCarOrderConverter(final CarOrderConverter carOrderConverter)
	{
		this.carOrderConverter = carOrderConverter;
	}


	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	@Override
	public SearchPageData<CarOrderHistoryData> getPagedOrderHistoryForCustomer(final PageableData pageableData)
	{

		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();

		Assert.notNull(currentCustomer.getCustomerID(), "Parameter source cannot be null.");

		final PaginationData paginationData = createEmptyPagination(pageableData);

		final List<CarOrderHistoryData> orderList = getMultichannelOrderHistoryService().readOrdersForCustomer(
				currentCustomer.getCustomerID(), paginationData);

		updatePagination(paginationData, pageableData);

		// convert orders, calculate totals, format ...
		getCarOrderConverter().convertOrders(orderList);

		final SearchPageData<CarOrderHistoryData> searchPageData = createSearchPageData();

		searchPageData.setPagination(paginationData);

		searchPageData.setSorts(createSorts(pageableData.getSort()));

		searchPageData.setResults(orderList);

		return searchPageData;

	}

	protected List<SortData> createSorts(final String selectedSortCode)
	{
		final List result = new ArrayList(2);

		result.add(createSort("byDate", null, selectedSortCode));
		result.add(createSort("byOrderNumber", null, selectedSortCode));
		return result;
	}

	protected SortData createSort(final String code, final String name, final String selectedSortCode)
	{
		final SortData sortData = createSortData();
		sortData.setCode(code);
		sortData.setName(name);
		sortData.setSelected((selectedSortCode != null) && (selectedSortCode.equals(code)));
		return sortData;
	}

	@Override
	@Deprecated
	public CarOrderHistoryData getOrderDetails(final Date businessDayDate, final String storeId, final Integer transactionIndex)
	{
		return getOrderDetails(DateUtil.formatDate(businessDayDate), storeId, transactionIndex);
	}

	@Override
	public CarOrderHistoryData getOrderDetails(final String businessDayDate, final String storeId, final Integer transactionIndex)
	{

		Assert.notNull(businessDayDate, "Parameter source cannot be null.");
		Assert.notNull(storeId, "Parameter source cannot be null.");
		Assert.notNull(transactionIndex, "Parameter source cannot be null.");

		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		Assert.notNull(currentCustomer.getCustomerID(), "Customer id cannot be null.");


		final CarOrderHistoryData order = getMultichannelOrderHistoryService().readOrderDetails(businessDayDate, storeId,
				transactionIndex, currentCustomer.getCustomerID());

		if (order == null)
		{
			throw new UnknownIdentifierException("Order with orderGUID " + businessDayDate + "Order with orderGUID "
					+ businessDayDate + "Order with orderGUID " + businessDayDate + " not found for current user in current BaseStore");
		}

		// convert order values
		carOrderConverter.convertOrder(order);

		// convert order entries
		carOrderConverter.convertOrderEntries(order.getOrderEntries());

		return order;
	}


	@Override
	public CarMultichannelOrderHistoryData getSalesDocumentDetails(final String TransactionNumber)
	{

		Assert.notNull(TransactionNumber, "Parameter source cannot be null.");

		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		Assert.notNull(currentCustomer.getCustomerID(), "Customer id cannot be null.");

		final CarMultichannelOrderHistoryData order = getMultichannelOrderHistoryService().readSalesDocumentDetails(
				currentCustomer.getCustomerID(), TransactionNumber);

		if (order == null)
		{
			throw new UnknownIdentifierException("Order with orderGUID " + TransactionNumber
					+ " not found for current user in current BaseStore");
		}

		// convert order values
		carOrderConverter.convertOrder(order);

		// convert order entries
		carOrderConverter.convertOrderEntries(order.getOrderEntries());

		return order;
	}

	@Override
	public SearchPageData<CarMultichannelOrderHistoryData> getPagedMultichannelOrderHistoryForCustomer(
			final PageableData pageableData)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();

		Assert.notNull(currentCustomer.getCustomerID(), "Parameter source cannot be null.");

		final PaginationData paginationData = createEmptyPagination(pageableData);


		final List<CarMultichannelOrderHistoryData> orderList = getMultichannelOrderHistoryService()
				.readMultiChannelTransactionsForCustomer(currentCustomer.getCustomerID(), paginationData);

		updatePagination(paginationData, pageableData);

		// convert orders, calculate totals, format ...
		getCarOrderConverter().convertOrdersBase(orderList);

		final SearchPageData<CarMultichannelOrderHistoryData> searchPageData = createSearchPageData();

		searchPageData.setPagination(paginationData);
		searchPageData.setSorts(createSorts(pageableData.getSort()));

		searchPageData.setResults(orderList);

		return searchPageData;

	}


	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	protected <T> SearchPageData<T> createSearchPageData()
	{
		return new SearchPageData();
	}


	//	protected PaginationData createPaginationData()
	//	{
	//		return new PaginationData();
	//	}

	protected SortData createSortData()
	{
		return new SortData();
	}


	protected PaginationData createEmptyPagination(final PageableData pageableData)
	{
		final PaginationData paginationData = new PaginationData();
		paginationData.setPageSize(pageableData.getPageSize());
		paginationData.setSort(pageableData.getSort());
		paginationData.setCurrentPage(pageableData.getCurrentPage());

		return paginationData;
	}

	protected void updatePagination(final PaginationData paginationData, final PageableData pageableData)
	{
		paginationData.setNumberOfPages((int) ((paginationData.getTotalNumberOfResults() - 1) / pageableData.getPageSize() + 1));
		paginationData.setCurrentPage(Math.max(0, Math.min(paginationData.getNumberOfPages(), pageableData.getCurrentPage())));
	}




}
