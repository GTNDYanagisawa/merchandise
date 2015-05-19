// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.chinaaccelerator.alipay.data.AlipayNotifyInfoData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayReturnData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.AlipayTradeStatus;
import de.hybris.platform.chinaaccelerator.alipay.service.AlipayPaymentService;
import de.hybris.platform.chinaaccelerator.facades.order.impl.ChinaOrderFacadeImpl;
import de.hybris.platform.chinaaccelerator.services.order.ChinaOrderService;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Matchers;
import org.mockito.Mockito;


public class ChinaOrderFacadeUnitTest
{

	private static final Logger LOG = Logger.getLogger(ChinaOrderFacadeUnitTest.class);

	@Test
	public void testHandleResponseToNotify()
	{
		final ChinaOrderService orderService = Mockito.mock(ChinaOrderService.class);
		final AlipayPaymentService alipayPaymentService = Mockito.mock(AlipayPaymentService.class);
		final OrderModel orderModel = Mockito.mock(OrderModel.class);
		final AlipayReturnData alipayReturnData = Mockito.mock(AlipayReturnData.class);

		BDDMockito.when(alipayPaymentService.handleResponse(Matchers.any(OrderModel.class), Matchers.any(AlipayReturnData.class)))
				.thenReturn(Boolean.TRUE);
		BDDMockito.when(orderService.getOrderByCode(Matchers.any(String.class))).thenReturn(orderModel);

		final ChinaOrderFacadeImpl extOrderFacade = new ChinaOrderFacadeImpl();
		extOrderFacade.setAlipayPaymentService(alipayPaymentService);
		extOrderFacade.setOrderService(orderService);
		assertEquals(extOrderFacade.handleResponse(alipayReturnData), true);
	}

	@Test
	public void testCloseTrade()
	{
		final ChinaOrderService orderService = Mockito.mock(ChinaOrderService.class);
		final AlipayPaymentService alipayPaymentService = Mockito.mock(AlipayPaymentService.class);
		final OrderModel orderModel_1 = Mockito.mock(OrderModel.class);
		final OrderModel orderModel_2 = Mockito.mock(OrderModel.class);
		BDDMockito.when(alipayPaymentService.closeTrade(orderModel_1)).thenReturn(Boolean.TRUE);
		BDDMockito.when(alipayPaymentService.closeTrade(orderModel_2)).thenReturn(Boolean.FALSE);
		BDDMockito.when(orderService.getOrderByCode("true")).thenReturn(orderModel_1);
		BDDMockito.when(orderService.getOrderByCode("false")).thenReturn(orderModel_2);
		final ChinaOrderFacadeImpl extOrderFacade = new ChinaOrderFacadeImpl();
		extOrderFacade.setAlipayPaymentService(alipayPaymentService);
		extOrderFacade.setOrderService(orderService);
		assertEquals(extOrderFacade.closeTrade("true"), true);
		assertEquals(extOrderFacade.closeTrade("false"), false);
	}

	@Test
	public void testCheckTradeStatus()
	{
		final ChinaOrderService orderService = Mockito.mock(ChinaOrderService.class);
		final AlipayPaymentService alipayPaymentService = Mockito.mock(AlipayPaymentService.class);
		final OrderModel orderModel_1 = Mockito.mock(OrderModel.class);
		final OrderModel orderModel_2 = Mockito.mock(OrderModel.class);
		BDDMockito.when(alipayPaymentService.checkTrade(orderModel_1)).thenReturn(AlipayTradeStatus.TRADE_CLOSED);
		BDDMockito.when(alipayPaymentService.checkTrade(orderModel_2)).thenReturn(AlipayTradeStatus.TRADE_SUCCESS);
		BDDMockito.when(orderService.getOrderByCode(AlipayTradeStatus.TRADE_CLOSED.toString())).thenReturn(orderModel_1);
		BDDMockito.when(orderService.getOrderByCode(AlipayTradeStatus.TRADE_SUCCESS.toString())).thenReturn(orderModel_2);
		final ChinaOrderFacadeImpl extOrderFacade = new ChinaOrderFacadeImpl();
		extOrderFacade.setAlipayPaymentService(alipayPaymentService);
		extOrderFacade.setOrderService(orderService);
		assertEquals(extOrderFacade.checkTradeStatus(AlipayTradeStatus.TRADE_CLOSED.toString()), AlipayTradeStatus.TRADE_CLOSED);
		assertEquals(extOrderFacade.checkTradeStatus(AlipayTradeStatus.TRADE_SUCCESS.toString()), AlipayTradeStatus.TRADE_SUCCESS);
	}

	@Test
	public void testHandleResponse()
	{
		final ChinaOrderService orderService = Mockito.mock(ChinaOrderService.class);
		final AlipayPaymentService alipayPaymentService = Mockito.mock(AlipayPaymentService.class);
		final OrderModel orderModel_1 = Mockito.mock(OrderModel.class);
		final OrderModel orderModel_2 = Mockito.mock(OrderModel.class);

		BDDMockito.when(
				alipayPaymentService.handleResponse(Matchers.eq(orderModel_1), Matchers.any(AlipayNotifyInfoData.class),
						Matchers.any(Map.class), Matchers.anyBoolean())).thenReturn(false);
		BDDMockito.when(
				alipayPaymentService.handleResponse(Matchers.eq(orderModel_2), Matchers.any(AlipayNotifyInfoData.class),
						Matchers.any(Map.class), Matchers.anyBoolean())).thenReturn(true);

		BDDMockito.when(orderService.getOrderByCode("1")).thenReturn(orderModel_1);
		BDDMockito.when(orderService.getOrderByCode("2")).thenReturn(orderModel_2);

		final AlipayNotifyInfoData notifyData_1 = new AlipayNotifyInfoData();
		notifyData_1.setOut_trade_no("1");

		final AlipayNotifyInfoData notifyData_2 = new AlipayNotifyInfoData();
		notifyData_2.setOut_trade_no("2");

		final ChinaOrderFacadeImpl extOrderFacade = new ChinaOrderFacadeImpl();
		extOrderFacade.setAlipayPaymentService(alipayPaymentService);
		extOrderFacade.setOrderService(orderService);
		assertEquals(extOrderFacade.handleResponse(notifyData_1, new HashMap()), false);
		assertEquals(extOrderFacade.handleResponse(notifyData_2, new HashMap()), true);
	}

}