/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.order.impl;

import de.hybris.platform.chinaaccelerator.alipay.constants.PaymentConstants;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayNotifyInfoData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayReturnData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.AlipayTradeStatus;
import de.hybris.platform.chinaaccelerator.alipay.service.AlipayPaymentService;
import de.hybris.platform.chinaaccelerator.alipay.util.AlipayServiceUtil;
import de.hybris.platform.chinaaccelerator.facades.order.ChinaOrderFacade;
import de.hybris.platform.chinaaccelerator.services.order.ChinaOrderService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.order.impl.DefaultOrderFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.converter.Converters;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordercancel.model.OrderStatusUpdateRecordModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;


public class ChinaOrderFacadeImpl extends DefaultOrderFacade implements ChinaOrderFacade
{
	private AlipayPaymentService alipayPaymentService;
	private OrderService orderService;

	@Resource
	private ModelService modelService;

	@Resource
	private BusinessProcessService businessProcessService;
	private final Logger LOG = Logger.getLogger(ChinaOrderFacadeImpl.class);

	@Override
	public FacetSearchPageData<SearchStateData, OrderHistoryData> getPagedOrderHistoryForStatuses(
			final SearchStateData searchStateData, final PageableData pageableData, final OrderStatus... statuses)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final SearchPageData<OrderModel> orderResults = getCustomerAccountService().getOrderList(currentCustomer, currentBaseStore,
				statuses, pageableData);

		return convertSearchPageData(searchStateData, orderResults, getOrderHistoryConverter());

	}


	protected FacetSearchPageData<SearchStateData, OrderHistoryData> convertSearchPageData(final SearchStateData searchStateData,
			final SearchPageData<OrderModel> source, final Converter<OrderModel, OrderHistoryData> converter)
	{
		final FacetSearchPageData<SearchStateData, OrderHistoryData> result = new FacetSearchPageData<SearchStateData, OrderHistoryData>();
		result.setCurrentQuery(searchStateData);
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	/**
	 * Alipay asynchronized call for Notify the hybris for the Trade status.
	 * */
	@Override
	public boolean handleResponse(final AlipayNotifyInfoData notifyData, final Map param)
	{
		final Map<String, String> map = AlipayServiceUtil.transRequestParam2Map(param);
		return handleResponseToNotify(notifyData, map, false);
	}

	@Override
	public OrderData getOrderDetailsForCode(final String code)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCheckoutCustomerStrategy().isAnonymousCheckout() ? getCustomerAccountService()
				.getOrderDetailsForGUID(code, baseStoreModel) : getCustomerAccountService().getOrderForCode(
				(CustomerModel) getUserService().getCurrentUser(), code, baseStoreModel);
		if (orderModel == null)
		{
			throw new UnknownIdentifierException("Order with orderGUID " + code + " not found for current user in current BaseStore");
		}


		return getOrderConverter().convert(orderModel);
	}

	@Override
	public void sendConfirmEmail(final String code)
	{
		final OrderModel orderModel = ((ChinaOrderService) orderService).getOrderByCode(code);
		if (orderModel == null)
		{
			throw new UnknownIdentifierException("Order with orderGUID " + code + " not found for current user in current BaseStore");
		}

		final OrderProcessModel orderProcessModel = (OrderProcessModel) businessProcessService.createProcess(
				"orderConfirmationEmailProcess-" + orderModel.getCode() + "-" + System.currentTimeMillis(),
				"orderConfirmationEmailProcess");
		orderProcessModel.setOrder(orderModel);
		modelService.save(orderProcessModel);
		businessProcessService.startProcess(orderProcessModel);
	}

	@Override
	public void cancelOrder(final String code)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCheckoutCustomerStrategy().isAnonymousCheckout() ? getCustomerAccountService()
				.getOrderDetailsForGUID(code, baseStoreModel) : getCustomerAccountService().getOrderForCode(
				(CustomerModel) getUserService().getCurrentUser(), code, baseStoreModel);
		if (orderModel == null)
		{
			throw new UnknownIdentifierException("Order with orderGUID " + code + " not found for current user in current BaseStore");
		}

		orderModel.getModificationRecords();
		final OrderStatusUpdateRecordModel orderStatusUpdateRecordModel = modelService.create(OrderStatusUpdateRecordModel.class);
		orderStatusUpdateRecordModel.setStatus(OrderStatus.CANCELLED);
		orderStatusUpdateRecordModel.setOrder(orderModel);
		modelService.save(orderStatusUpdateRecordModel);
		//order.getModificationRecords().add(orderStatusUpdateRecordModel);
		orderModel.setStatus(OrderStatus.CANCELLED);

		final OrderProcessModel orderProcessModel = (OrderProcessModel) businessProcessService.createProcess(
				"sendOrderCancelledEmailProcess-" + orderModel.getCode() + "-" + System.currentTimeMillis(),
				"sendOrderCancelledEmailProcess");
		orderProcessModel.setOrder(orderModel);
		modelService.save(orderProcessModel);
		businessProcessService.startProcess(orderProcessModel);

		orderService.saveOrder(orderModel);
	}

	/**
	 * @param notifyData
	 * @param map
	 * @param isMobile
	 */
	private boolean handleResponseToNotify(final AlipayNotifyInfoData notifyData, final Map<String, String> map,
			final boolean isMobile)
	{
		final String out_trade_no = notifyData.getOut_trade_no();
		if (out_trade_no != null && !out_trade_no.isEmpty())
		{
			final OrderModel orderModel = ((ChinaOrderService) getOrderService()).getOrderByCode(notifyData.getOut_trade_no());
			if (orderModel != null)
			{
				final boolean flag = getAlipayPaymentService().handleResponse(orderModel, notifyData, map, isMobile);

				return flag;
			}
		}
		else
		{
			LOG.error("Not able to get valid out_trade_number.");
		}
		return false;
	}

	/**
	 * Alipay synchronized call for responding the request.
	 * */
	@Override
	public boolean handleResponse(final AlipayReturnData returnData)
	{
		final OrderModel orderModel = ((ChinaOrderService) getOrderService()).getOrderByCode(returnData.getOut_trade_no());
		if (orderModel != null)
		{
			return getAlipayPaymentService().handleResponse(orderModel, returnData);
		}
		return false;
	}

	/**
	 * Call for close the trade based on the Order Code, i.e. out_trade_no.
	 * 
	 * @param orderCode
	 * */
	@Override
	public boolean closeTrade(final String orderCode)
	{
		final OrderModel orderModel = ((ChinaOrderService) getOrderService()).getOrderByCode(orderCode);
		if (orderModel != null)
		{
			return getAlipayPaymentService().closeTrade(orderModel);
		}
		return false;
	}

	/**
	 * Call for checking the current trade statue of the given Order code, i.e. out_trade_no.
	 * 
	 * @param orderCode
	 */
	@Override
	public AlipayTradeStatus checkTradeStatus(final String orderCode)
	{
		final OrderModel orderModel = ((ChinaOrderService) getOrderService()).getOrderByCode(orderCode);
		if (orderModel != null)
		{
			return getAlipayPaymentService().checkTrade(orderModel);
		}
		return null;
	}

	/**
	 * @return the alipayPaymentService
	 */
	public AlipayPaymentService getAlipayPaymentService()
	{
		return alipayPaymentService;
	}


	/**
	 * @param alipayPaymentService
	 *           the alipayPaymentService to set
	 */
	public void setAlipayPaymentService(final AlipayPaymentService alipayPaymentService)
	{
		this.alipayPaymentService = alipayPaymentService;
	}


	/**
	 * @return the orderService
	 */
	public OrderService getOrderService()
	{
		return orderService;
	}


	/**
	 * @param orderService
	 *           the orderService to set
	 */
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}



	@Override
	public String getRequestUrl(final String orderCode)
	{
		final OrderModel orderModel = ((ChinaOrderService) getOrderService()).getOrderByCode(orderCode);
		if (orderModel != null)
		{
			return getAlipayPaymentService().getRequestUrl(orderModel);
		}
		return "";
	}

	@Override
	public void handleErrorResponse(final Map parameterMap)
	{
		final Map<String, String> errorNotify = AlipayServiceUtil.transRequestParam2Map(parameterMap);
		final String orderCode = errorNotify.get(PaymentConstants.ErrorHandler.OUT_TRADE_NO);
		final String errorCode = errorNotify.get(PaymentConstants.ErrorHandler.ERROR_CODE);
		final OrderModel orderModel = ((ChinaOrderService) getOrderService()).getOrderByCode(orderCode);
		getAlipayPaymentService().saveErrorCallback(orderModel, errorCode);
	}



}
