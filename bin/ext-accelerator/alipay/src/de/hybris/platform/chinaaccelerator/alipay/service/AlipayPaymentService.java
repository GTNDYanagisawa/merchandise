/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.service;

import java.util.Map;

import de.hybris.platform.chinaaccelerator.alipay.data.AlipayNotifyInfoData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayReturnData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.AlipayTradeStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.PaymentService;


public interface AlipayPaymentService extends PaymentService
{
	public boolean initiate(OrderModel order);
	
	public boolean handleResponse(OrderModel order, AlipayNotifyInfoData notifyData, Map<String, String> notifyDataMa, boolean isMobile);

	public boolean handleResponse(OrderModel orderModel,
			AlipayReturnData returnData);
	
	public boolean closeTrade(OrderModel orderModel);

	public AlipayTradeStatus checkTrade(OrderModel orderModel);

	public String getRequestUrl(OrderModel orderModel);
	
	public void saveErrorCallback(OrderModel orderModel, String errorCode);
}
