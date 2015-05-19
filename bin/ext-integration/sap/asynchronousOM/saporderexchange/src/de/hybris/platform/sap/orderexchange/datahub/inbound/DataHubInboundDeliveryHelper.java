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
package de.hybris.platform.sap.orderexchange.datahub.inbound;


/**
 * Data Hub Inbound Helper for Delivery related notifications
 */
public interface DataHubInboundDeliveryHelper
{

	/**
	 * @param orderCode
	 * @param warehouseId
	 * @param goodsIssueDate
	 */
	void processDeliveryAndGoodsIssue(String orderCode, String warehouseId, String goodsIssueDate);

	/**
	 * @param deliveryInfo
	 * @return warehouse ID based on provided delivery information from Data Hub
	 */
	String determineWarehouseId(String deliveryInfo);

	/**
	 * @param deliveryInfo
	 * @return goods issue date based on provided delivery information from Data Hub
	 */
	String determineGoodsIssueDate(String deliveryInfo);

}
