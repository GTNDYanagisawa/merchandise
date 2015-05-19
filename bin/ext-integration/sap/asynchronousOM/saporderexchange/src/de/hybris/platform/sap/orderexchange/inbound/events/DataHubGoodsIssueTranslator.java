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
package de.hybris.platform.sap.orderexchange.inbound.events;


import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundDeliveryHelper;


/**
 * Translator for Goods Issue process. It updates the consignments and finalized status
 */
public class DataHubGoodsIssueTranslator extends DataHubTranslator<DataHubInboundDeliveryHelper>
{
	@SuppressWarnings("javadoc")
	public static final String HELPER_BEAN = "sapDataHubInboundDeliveryHelper";
	
	@SuppressWarnings("javadoc")
	public DataHubGoodsIssueTranslator() {
		super(HELPER_BEAN);
	}
	
	@Override
	public void performImport(final String delivInfo, final Item processedItem) throws ImpExException
	{
		String orderCode = null;

		try
		{
			orderCode = processedItem.getAttribute(DataHubInboundConstants.CODE).toString();
		}
		catch (final JaloSecurityException e)
		{
			throw new ImpExException(e);
		}

		if (delivInfo != null && !delivInfo.equals(DataHubInboundConstants.IGNORE))
		{
			final String goodsIssueDate = getInboundHelper().determineGoodsIssueDate(delivInfo);
			final String warehouseId = getInboundHelper().determineWarehouseId(delivInfo);
			getInboundHelper().processDeliveryAndGoodsIssue(orderCode, warehouseId, goodsIssueDate);
		}
	}
}
