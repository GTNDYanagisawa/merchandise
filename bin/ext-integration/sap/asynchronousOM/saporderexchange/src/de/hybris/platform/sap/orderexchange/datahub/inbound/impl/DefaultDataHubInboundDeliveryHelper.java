/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.sap.orderexchange.datahub.inbound.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.ConsignmentCreationException;
import de.hybris.platform.ordersplitting.ConsignmentService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundDeliveryHelper;


/**
 * Default Data Hub Inbound helper for delivery related notifications
 */
public class DefaultDataHubInboundDeliveryHelper extends AbstractDataHubInboundHelper implements DataHubInboundDeliveryHelper
{
	private ConsignmentService consignmentService;

	@SuppressWarnings("javadoc")
	public ConsignmentService getConsignmentService()
	{
		return consignmentService;
	}

	@SuppressWarnings("javadoc")
	public void setConsignmentService(final ConsignmentService consignmentService)
	{
		this.consignmentService = consignmentService;
	}

	@Override
	public void processDeliveryAndGoodsIssue(final String orderCode, final String warehouseId, final String goodsIssueDate)
	{
		final OrderModel order = readOrder(orderCode);

		if (goodsIssueDate == null)
		{
			processDeliveryCreation(warehouseId, order);
		}
		else
		{
			processDeliveryGoodsIssue(warehouseId, goodsIssueDate, order);
		}
	}

	public String determineWarehouseId(final String deliveryInfo)
	{
		final int delivIndex = deliveryInfo.indexOf(DataHubInboundConstants.STR);
		return delivIndex > 0 ? deliveryInfo.substring(0, delivIndex) : null;
	}

	public String determineGoodsIssueDate(final String deliveryInfo)
	{
		final int delivIndex = deliveryInfo.indexOf(DataHubInboundConstants.STR) + 1;
		final int delviLength = deliveryInfo.length();
		String result = null;
		if (delviLength != delivIndex)
		{
			final String delivDate = deliveryInfo.substring(delivIndex, delviLength);
			if (!delivDate.equals(DataHubInboundConstants.DATE_NOT_SET))
			{
				result = deliveryInfo.substring(delivIndex, delviLength);
			}
		}
		return result;
	}

	protected void processDeliveryCreation(final String warehouseId, final OrderModel order)
	{
		ConsignmentModel consignment;

		if (order.getConsignments().isEmpty())
		{
			consignment = createConsignmentAndPopulate(warehouseId, order);
			getModelService().save(consignment);
		}
		getBusinessProcessService().triggerEvent(DataHubInboundConstants.CONSIGNMENT_CREATION_EVENTNAME_PREFIX + order.getCode());
	}


	protected ConsignmentModel createConsignmentAndPopulate(final String warehouseId, final OrderModel order)
	{
		final ConsignmentModel consignment = createConsignment(warehouseId, order);
		final Date namedDeliverydate = determineEarliestDeliveryDate(order);
		if (namedDeliverydate != null)
		{
			consignment.setNamedDeliveryDate(namedDeliverydate);
		}
		consignment.setDeliveryMode(order.getDeliveryMode());
		return consignment;
	}

	protected Date determineEarliestDeliveryDate(final AbstractOrderModel order)
	{
		Date minDeliveryDate = null;
		for (final AbstractOrderEntryModel orderEntry : order.getEntries())
		{
			final Date deliveryDate = orderEntry.getNamedDeliveryDate();
			if (minDeliveryDate == null)
			{
				minDeliveryDate = deliveryDate;
			}
			else if (deliveryDate != null && minDeliveryDate.compareTo(deliveryDate) > 0)
			{
				minDeliveryDate = deliveryDate;
			}
		}
		return minDeliveryDate;
	}

	protected ConsignmentModel createConsignment(final String warehouseId, final OrderModel order)
	{
		try
		{
			final ConsignmentModel consignment = consignmentService.createConsignment(order, order.getCode(), order.getEntries());
			consignment.setShippingAddress(order.getDeliveryAddress());
			consignment.setStatus(ConsignmentStatus.WAITING);
			setWarehouseOfConsignment(warehouseId, order, consignment);
			return consignment;
		}
		catch (final ConsignmentCreationException e)
		{
			throw new IllegalArgumentException("IDoc processor " + this.getClass().getName()
					+ " could not create consignment for order: " + order.getCode(), e);
		}
	}

	protected void setWarehouseOfConsignment(final String warehouseId, final AbstractOrderModel order,
			final ConsignmentModel consignment)
	{
		final String erpDeliverPlant = warehouseId;
		for (final WarehouseModel warehouse : order.getStore().getWarehouses())
		{
			if (warehouse.getCode().equals(erpDeliverPlant))
			{
				consignment.setWarehouse(warehouse);
				return;
			}
		}
	}

	protected void processDeliveryGoodsIssue(final String warehouseId, final String issueDate, final OrderModel order)
	{
		if (order.getConsignments().isEmpty())
		{
			final ConsignmentModel consignment = createConsignmentAndPopulate(warehouseId, order);
			mapDeliveryToConsignment(issueDate, consignment, order);
		}
		else
		{
			for (final ConsignmentModel consignment : order.getConsignments())
			{
				mapDeliveryToConsignment(issueDate, consignment, order);
			}
		}
		getModelService().saveAll();
		getBusinessProcessService().triggerEvent(DataHubInboundConstants.GOODS_ISSUE_EVENTNAME_PREFIX + order.getCode());
	}

	protected void mapDeliveryToConsignment(final String issueDate, final ConsignmentModel consignment, final OrderModel order)
	{
		consignment.setShippingDate(convertStringToDate(issueDate));
		for (final ConsignmentEntryModel consignmentEntry : consignment.getConsignmentEntries())
		{
			final Integer orderEntryNumber = consignmentEntry.getOrderEntry().getEntryNumber();
			final Long quantity = order.getEntries().get(orderEntryNumber).getQuantity();

			consignmentEntry.setShippedQuantity(quantity);
		}
	}

	protected Date convertStringToDate(final String dateString)
	{
		Date date = null;
		if (isDateSet(dateString))
		{
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DataHubInboundConstants.IDOC_DATE_FORMAT);
			try
			{
				date = simpleDateFormat.parse(dateString);
			}
			catch (final ParseException e)
			{
				throw new IllegalArgumentException("Date " + dateString + " can not be converted to a date", e);
			}
		}
		return date;
	}

	protected boolean isDateSet(final String date)
	{
		return !(date == null || date.isEmpty() || DataHubInboundConstants.DATE_NOT_SET.equals(date));
	}

}
