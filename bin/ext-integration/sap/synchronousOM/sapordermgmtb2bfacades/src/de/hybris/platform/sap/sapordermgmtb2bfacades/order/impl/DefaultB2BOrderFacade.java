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
package de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl;

import de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderHistoryEntryData;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.sap.sapordermgmtservices.order.OrderService;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.workflow.enums.WorkflowActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.apache.log4j.Logger;


/**
 * 
 */
public class DefaultB2BOrderFacade implements B2BOrderFacade
{
	private ProductImageHelper productImageHelper;


	/**
	 * 
	 */
	private static final String MSG_NOT_SUPPORTED = "Not supported in the context of SAP order management";

	private static final Logger LOG = Logger.getLogger(DefaultB2BOrderFacade.class);

	private OrderService orderService;

	private BackendAvailabilityService backendAvailabilityService;

	/**
	 * @return the backendAvailabilityService
	 */
	public BackendAvailabilityService getBackendAvailabilityService()
	{
		return backendAvailabilityService;
	}

	/**
	 * @param backendAvailabilityService
	 *           the backendAvailabilityService to set
	 */
	public void setBackendAvailabilityService(final BackendAvailabilityService backendAvailabilityService)
	{
		this.backendAvailabilityService = backendAvailabilityService;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#getReplenishmentOrderDetailsForCode(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public ScheduledCartData getReplenishmentOrderDetailsForCode(final String code, final String user)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#getReplenishmentHistory()
	 */
	@Override
	public List<ScheduledCartData> getReplenishmentHistory()
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#getPagedReplenishmentHistory(de.hybris.platform.
	 * commerceservices.search.pagedata.PageableData)
	 */
	@Override
	public SearchPageData<ScheduledCartData> getPagedReplenishmentHistory(final PageableData pageableData)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#cancelReplenishment(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void cancelReplenishment(final String jobCode, final String user)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#getReplenishmentOrderHistory(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<? extends OrderHistoryData> getReplenishmentOrderHistory(final String jobCode, final String user)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#getOrdersForApproval()
	 */
	@Override
	public List<B2BOrderApprovalData> getOrdersForApproval()
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#getPagedOrdersForApproval(de.hybris.platform.workflow
	 * .enums.WorkflowActionType[], de.hybris.platform.commerceservices.search.pagedata.PageableData)
	 */
	@Override
	public SearchPageData<B2BOrderApprovalData> getPagedOrdersForApproval(final WorkflowActionType[] actionTypes,
			final PageableData pageableData)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#getOrderApprovalDetailsForCode(java.lang.String)
	 */
	@Override
	public B2BOrderApprovalData getOrderApprovalDetailsForCode(final String code)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#setOrderApprovalDecision(de.hybris.platform.
	 * b2bacceleratorfacades.order.data.B2BOrderApprovalData)
	 */
	@Override
	public B2BOrderApprovalData setOrderApprovalDecision(final B2BOrderApprovalData b2bOrderApprovalData)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#getPagedReplenishmentOrderHistory(java.lang.String,
	 * de.hybris.platform.commerceservices.search.pagedata.PageableData)
	 */
	@Override
	public SearchPageData<? extends OrderHistoryData> getPagedReplenishmentOrderHistory(final String jobCode,
			final PageableData pageableData)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#getOrderHistoryEntryData(java.lang.String)
	 */
	@Override
	public List<B2BOrderHistoryEntryData> getOrderHistoryEntryData(final String orderCode)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#createAndSetNewOrderFromRejectedQuote(java.lang.
	 * String)
	 */
	@Override
	public void createAndSetNewOrderFromRejectedQuote(final String orderCode)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#createAndSetNewOrderFromNegotiateQuote(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public void createAndSetNewOrderFromNegotiateQuote(final String orderCode, final String comment)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#createAndSetNewOrderFromApprovedQuote(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public void createAndSetNewOrderFromApprovedQuote(final String orderCode, final String comment)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#cancelOrder(java.lang.String, java.lang.String)
	 */
	@Override
	public void cancelOrder(final String orderCode, final String comment)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade#addAdditionalComment(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void addAdditionalComment(final String orderCode, final String comment)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.OrderFacade#getOrderDetailsForCode(java.lang.String)
	 */
	@Override
	public OrderData getOrderDetailsForCode(final String code)
	{	if (isBackendDown())
	{
		throw new ApplicationBaseRuntimeException("Backend Down");
	}
	
		return getOrderDetailsForGUID(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.OrderFacade#getOrderDetailsForGUID(java.lang.String)
	 */
	@Override
	public OrderData getOrderDetailsForGUID(final String guid)
	{
		if (isBackendDown())
		{
			throw new ApplicationBaseRuntimeException("Backend Down");
		}
		
		final OrderData orderData = orderService.getOrderForCode(guid);
		productImageHelper.enrichWithProductImages(orderData);
		return orderData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commercefacades.order.OrderFacade#getOrderHistoryForStatuses(de.hybris.platform.core.enums.
	 * OrderStatus[])
	 */
	@Override
	public List<OrderHistoryData> getOrderHistoryForStatuses(final OrderStatus... statuses)
	{
		if (isBackendDown())
		{
			return Collections.emptyList();
		}
		return orderService.getOrderHistoryForStatuses(statuses);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.OrderFacade#getPagedOrderHistoryForStatuses(de.hybris.platform.
	 * commerceservices.search.pagedata.PageableData, de.hybris.platform.core.enums.OrderStatus[])
	 */
	@Override
	public SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(final PageableData pageableData,
			final OrderStatus... statuses)
	{
		if (isBackendDown())
		{
			final SearchPageData<OrderHistoryData> resultHybrisFormat = new SearchPageData<>();
			resultHybrisFormat.setResults(new ArrayList<OrderHistoryData>());
			
			//Set PaginData 
			final PaginationData pagination = new PaginationData();
			pagination.setCurrentPage(pageableData.getCurrentPage());
			pagination.setPageSize(pageableData.getPageSize());
			pagination.setTotalNumberOfResults(0);
			pagination.setNumberOfPages(0);
			
			resultHybrisFormat.setPagination(pagination);
			
			return resultHybrisFormat;
		}
		return orderService.getPagedOrderHistoryForStatuses(pageableData, statuses);
	}

	/**
	 * @return the productImageHelper
	 */
	public ProductImageHelper getProductImageHelper()
	{
		return productImageHelper;
	}

	/**
	 * @param productImageHelper
	 *           the productImageHelper to set
	 */
	public void setProductImageHelper(final ProductImageHelper productImageHelper)
	{
		this.productImageHelper = productImageHelper;
	}

	/**
	 * @return Is Backend down?
	 */
	public boolean isBackendDown()
	{
		return backendAvailabilityService.isBackendDown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.OrderFacade#getOrderDetailsForCodeWithoutUser(java.lang.String)
	 */
	@Override
	public OrderData getOrderDetailsForCodeWithoutUser(final String code)
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);

	}

}
