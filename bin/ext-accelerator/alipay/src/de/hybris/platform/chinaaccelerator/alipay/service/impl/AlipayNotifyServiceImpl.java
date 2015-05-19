/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.service.impl;

import java.util.Date;

import de.hybris.platform.basecommerce.enums.StockLevelUpdateType;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.AlipayTradeStatus;
import de.hybris.platform.chinaaccelerator.alipay.service.AlipayNotifyService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordercancel.jalo.OrderStatusUpdateRecord;
import de.hybris.platform.ordercancel.model.OrderStatusUpdateRecordModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.model.StockLevelHistoryEntryModel;
import de.hybris.platform.tx.Transaction;

import javax.annotation.Resource;

import org.apache.log4j.Logger;


public class AlipayNotifyServiceImpl implements AlipayNotifyService
{

	private static final Logger LOG = Logger.getLogger(AlipayNotifyServiceImpl.class.getName());
	@Resource
	private ModelService modelService;
	
	@Resource
	private StockService stockService;
	
	@Resource
	private WarehouseService warehouseService;
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private BusinessProcessService businessProcessService;

	private static final String RELEASE_DECREMENT_COMMENT_CUSTOMER = "RELEASE and decrement by customer";
	public void executeAction(final OrderModel order)
	{
		final String result = checkPaymentTransaction(order);
		if ("OK".equals(result))
		{
			setOrderStatusAction(order, OrderStatus.PAYMENT_CAPTURED);
			order.setPaymentStatus(PaymentStatus.PAID);
		}
		else
		{
			setOrderStatusAction(order, OrderStatus.CANCELLED);
			final OrderProcessModel orderProcessModel = (OrderProcessModel)businessProcessService.createProcess(
					"sendOrderCancelledEmailProcess-" + order.getCode() + "-" + System.currentTimeMillis(),
					"sendOrderCancelledEmailProcess");
			orderProcessModel.setOrder(order);
			modelService.save(orderProcessModel);
			businessProcessService.startProcess(orderProcessModel);
		}
		orderService.saveOrder(order);
	}

	public String checkPaymentTransaction(final OrderModel order)
	{
		LOG.info(order.getCode());
		if (order.getPaymentTransactions() != null && order.getPaymentTransactions().size() > 0)
		{
			final PaymentTransactionModel transaction = order.getPaymentTransactions().get(0);
			if (transaction instanceof AlipayPaymentTransactionModel)
			{
				final AlipayPaymentTransactionModel alipayTrans = (AlipayPaymentTransactionModel) transaction;
				if (alipayTrans.getLatestTradeStatus().equals(AlipayTradeStatus.TRADE_SUCCESS.name()))
				{
					/** Transaction complete, and available for refund */
					return "OK";
				}
				else if (alipayTrans.getLatestTradeStatus().equals(AlipayTradeStatus.TRADE_FINISHED.name()))
				{
					/** Transaction complete, and not available for refund, however, this is what the Mobile returns */
					//TODO checking with Alipay on this at moment
					return "OK";
				}
				else if (alipayTrans.getLatestTradeStatus().equals(AlipayTradeStatus.TRADE_CLOSED.name()))
				{
					/**
					 * Unpaid transactions closed after a period of time. Transactions closed after a full refund.
					 */
					return "NOK";
				}
			}

		}
		return "NOK";
	}


	public void setOrderStatusAction(final OrderModel order, final OrderStatus orderStatus)
	{
		order.getModificationRecords();
		OrderStatusUpdateRecordModel orderStatusUpdateRecordModel = modelService.create(OrderStatusUpdateRecordModel.class);
		orderStatusUpdateRecordModel.setStatus(orderStatus);
		orderStatusUpdateRecordModel.setOrder(order);
		modelService.save(orderStatusUpdateRecordModel);
		//order.getModificationRecords().add(orderStatusUpdateRecordModel);
		order.setStatus(orderStatus);
	}
	
	private boolean updateStockLevel(final OrderModel order, final boolean decrement, final boolean release)
	{

		boolean result = true;

		try
		{
			txUpdateStockLevel(order, decrement, release);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}
	
	private void txUpdateStockLevel(final OrderModel order, final boolean decrement, final boolean release)
			throws Exception
	{
		if (order != null && !order.getEntries().isEmpty())
		{

			for (final AbstractOrderEntryModel orderEntry : order.getEntries())
			{
				LOG.info(orderEntry.getProduct().getCode());
				
				final StockLevelModel stockLevel = stockService.getStockLevel(orderEntry.getProduct(), getDefaultWareHouse());
				if (stockLevel != null)
				{
					if (release)
					{
						stockService.release(orderEntry.getProduct(), getDefaultWareHouse(), orderEntry.getQuantity().intValue(),
								RELEASE_DECREMENT_COMMENT_CUSTOMER);
					}

					final int current = stockLevel.getAvailable();

					int adjusted = 0;

					if (decrement)
					{
						adjusted = current - orderEntry.getQuantity().intValue();
					}
					else
					{
						adjusted = current + orderEntry.getQuantity().intValue();
					}

					if (adjusted < 0)
					{
						LOG.warn("Stocklevel for: " + orderEntry.getProduct().getCode()
								+ " is negative after adjustment, setting to zero...");
						adjusted = 0;
					}

					updateStockLevel(stockLevel, adjusted);

				}

			}
		}
	}
	
	private WarehouseModel getDefaultWareHouse()
	{
		WarehouseModel w =  warehouseService.getDefWarehouse().iterator().next();
		LOG.info(w.getPk());
		return w;
		
	}
	
	private void updateStockLevel(final StockLevelModel stockLevelModel, final int actualAmount)
	{

		stockLevelModel.setAvailable(actualAmount);
		modelService.save(stockLevelModel);
		createStockLevelHistoryEntry(stockLevelModel, actualAmount, stockLevelModel.getReserved(),
				StockLevelUpdateType.CUSTOMER_RELEASE, RELEASE_DECREMENT_COMMENT_CUSTOMER);
	}
	
	
	private StockLevelHistoryEntryModel createStockLevelHistoryEntry(final StockLevelModel stockLevel, final int actual,
			final int reserved, final StockLevelUpdateType updateType, final String comment)
	{
		if (stockLevel == null)
		{
			throw new IllegalArgumentException("stock level cannot be null.");
		}
		if (actual < 0)
		{
			throw new IllegalArgumentException("actual amount cannot be negative.");
		}

		final StockLevelHistoryEntryModel historyEntry = (StockLevelHistoryEntryModel) modelService.create(
				StockLevelHistoryEntryModel.class);
		historyEntry.setStockLevel(stockLevel);
		historyEntry.setActual(actual);
		historyEntry.setReserved(reserved);
		historyEntry.setUpdateType(updateType);
		if (comment != null)
		{
			historyEntry.setComment(comment);
		}
		historyEntry.setUpdateDate(new Date());
		modelService.save(historyEntry);
		return historyEntry;
	}
}
