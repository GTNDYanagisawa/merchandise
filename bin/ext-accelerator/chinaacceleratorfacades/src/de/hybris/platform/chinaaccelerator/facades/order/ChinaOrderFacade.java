/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.order;

import de.hybris.platform.chinaaccelerator.alipay.data.AlipayNotifyInfoData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayReturnData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.AlipayTradeStatus;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.OrderStatus;

import java.util.Map;


public interface ChinaOrderFacade extends OrderFacade
{
	/**
	 * Returns the order history of the current user for given statuses.
	 * 
	 * @param pageableData
	 *           paging information
	 * @param statuses
	 *           array of order statuses to filter the results
	 * @return The order history of the current user.
	 */
	FacetSearchPageData<SearchStateData, OrderHistoryData> getPagedOrderHistoryForStatuses(SearchStateData searchStateData,
			PageableData pageableData, OrderStatus... statuses);

	/**
	 * Alipay asynchronized call for Notify the hybris for the Trade status.
	 * */
	public boolean handleResponse(final AlipayNotifyInfoData notifyData, Map param);


	/**
	 * Alipay synchronized call for responding the request.
	 * */
	public boolean handleResponse(final AlipayReturnData returnData);

	/**
	 * Call for close the trade based on the Order Code, i.e. out_trade_no.
	 * 
	 * @param orderCode
	 * */
	public boolean closeTrade(final String orderCode);

	/**
	 * Call for checking the current trade statue of the given Order code, i.e. out_trade_no.
	 * 
	 * @param orderCode
	 */
	public AlipayTradeStatus checkTradeStatus(String orderCode);


	/**
	 * @param orderCode
	 */
	public String getRequestUrl(String orderCode);

	/**
	 * @param parameterMap
	 */
	void handleErrorResponse(Map parameterMap);

	public void cancelOrder(final String code);

	public void sendConfirmEmail(final String code);
}
