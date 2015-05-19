/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.order.impl;

import de.hybris.platform.chinaaccelerator.services.order.ChinaOrderService;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.impl.DefaultOrderService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;


public class ChinaOrderServiceImpl extends DefaultOrderService implements ChinaOrderService
{

	private static final String EXPIRED_ORDER_QUERY = "SELECT {pk} from {Order} where {status} = ({{ select {pk} from {OrderStatus} where {code} =?status }}) AND {modifiedtime} <= ?date and {versionID} is null";
	private static final String UNEXPORTED_ORDER_QUERY = "SELECT {pk} from {Order} where {status} in (?status) AND {versionID} is null AND ({exportStatus} IS NULL or {exportStatus} != ?exportedStatus)";
	private static final String FIND_ORDERS_BY_CODE_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME
			+ "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.CODE + "} = ?code AND {"
			+ OrderModel.VERSIONID + "} is null AND {" + OrderModel.ORIGINALVERSION + "} is null";
	private static final String EXPIRED_ORDER_QUERY_PARAM_STATUS = "status";
	private static final String UNEXPORTED_ORDER_QUERY_STATUS = "status";
	private static final String ORDER_CODE = "code";
	private static final String UNEXPORTED_ORDER_QUERY_EXPORTED_STATUS = "exportedStatus";
	private static final String EXPIRED_ORDER_QUERY_PARAM_DATE = "date";

	private FlexibleSearchService searchService;
	private final Logger LOG = Logger.getLogger(ChinaOrderServiceImpl.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OrderModel> getExpiredOrderForStatus(final OrderStatus orderStatus, final Date expiredDate)
	{
		Assert.assertNotNull(orderStatus);
		Assert.assertNotNull(expiredDate);

		List<OrderModel> orderModels = null;

		final FlexibleSearchQuery query = new FlexibleSearchQuery(EXPIRED_ORDER_QUERY);

		query.addQueryParameter(EXPIRED_ORDER_QUERY_PARAM_STATUS, orderStatus.getCode());
		query.addQueryParameter(EXPIRED_ORDER_QUERY_PARAM_DATE, expiredDate);

		final SearchResult<OrderModel> results = getSearchService().search(query);

		if (results != null && results.getCount() > 0)
		{

			orderModels = results.getResult();

		}

		return orderModels;
	}

	@Override
	public List<OrderModel> getUnexportedOrderForStatus(final OrderStatus... orderStatus)
	{
		Assert.assertNotNull(orderStatus);

		List<OrderModel> orderModels = null;

		final FlexibleSearchQuery query = new FlexibleSearchQuery(UNEXPORTED_ORDER_QUERY);

		query.addQueryParameter(UNEXPORTED_ORDER_QUERY_STATUS, Arrays.asList(orderStatus));
		query.addQueryParameter(UNEXPORTED_ORDER_QUERY_EXPORTED_STATUS, ExportStatus.EXPORTED);

		final SearchResult<OrderModel> results = getSearchService().search(query);

		if (results != null && results.getCount() > 0)
		{
			orderModels = results.getResult();
		}

		return orderModels;
	}

	@Override
	public OrderModel getOrderByCode(final String code)
	{
		Assert.assertNotNull(code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORDERS_BY_CODE_STORE_QUERY);

		query.addQueryParameter(ORDER_CODE, code);

		try
		{
			final OrderModel result = getSearchService().searchUnique(query);
			return result;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.info("No result for the given query for :" + code);
		}
		return null;
	}

	/**
	 * @return the searchService
	 */
	public FlexibleSearchService getSearchService()
	{
		return searchService;
	}

	/**
	 * @param searchService
	 *           the searchService to set
	 */
	public void setSearchService(final FlexibleSearchService searchService)
	{
		this.searchService = searchService;
	}
}
