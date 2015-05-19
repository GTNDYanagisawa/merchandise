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
package de.hybris.platform.b2bacceleratorfacades.order;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderHistoryEntryData;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.workflow.enums.WorkflowActionType;

import java.util.List;


public interface B2BOrderFacade extends OrderFacade
{
	/**
	 * Gets the schedule Cart Data for replenishment given a code.
	 * 
	 * @param code
	 *           Unique job identifier
	 * @param user
	 *           A customer assigned to the cart
	 * @return ScheduledCartData for given code and user
	 */
	ScheduledCartData getReplenishmentOrderDetailsForCode(String code, String user);

	/**
	 * Retrieves replenishment cron jobs associated to the session user.
	 * 
	 * @return Replenishment order history.
	 */
	List<ScheduledCartData> getReplenishmentHistory();

	/**
	 * Retrieves replenishment cron jobs associated to the session user with paging support
	 * 
	 * @param pageableData
	 *           pagination information
	 * @return Replenishment order history.
	 */
	SearchPageData<ScheduledCartData> getPagedReplenishmentHistory(PageableData pageableData);


	/**
	 * Stops the order replenishment process.
	 * 
	 * @param jobCode
	 *           A unique code for the replenishment cron job.
	 * @param user
	 *           A customer assigned to the cart
	 */
	void cancelReplenishment(String jobCode, String user);

	/**
	 * Retrieves all the scheduled order for a given jobCode.
	 * 
	 * @param jobCode
	 *           A cron job code
	 * @param user
	 *           A customer assigned to the cart
	 * @return Orders associated to a schedule job.
	 */
	List<? extends OrderHistoryData> getReplenishmentOrderHistory(String jobCode, String user);

	/**
	 * Retrieves the order list for approval dashboard
	 * 
	 * @return All orders pending approval
	 */
	List<B2BOrderApprovalData> getOrdersForApproval();

	/**
	 * Retrieves the order list for approval dashboard
	 * 
	 * @return All orders pending approval
	 */
	SearchPageData<B2BOrderApprovalData> getPagedOrdersForApproval(WorkflowActionType[] actionTypes, PageableData pageableData);

	B2BOrderApprovalData getOrderApprovalDetailsForCode(String code);

	B2BOrderApprovalData setOrderApprovalDecision(B2BOrderApprovalData b2bOrderApprovalData);

	SearchPageData<? extends OrderHistoryData> getPagedReplenishmentOrderHistory(String jobCode, PageableData pageableData);

	List<B2BOrderHistoryEntryData> getOrderHistoryEntryData(String orderCode);

	void createAndSetNewOrderFromRejectedQuote(String orderCode);

	void createAndSetNewOrderFromNegotiateQuote(String orderCode, String comment);

	void createAndSetNewOrderFromApprovedQuote(String orderCode, String comment);

	void cancelOrder(String orderCode, String comment);

	void addAdditionalComment(String orderCode, String comment);
}
