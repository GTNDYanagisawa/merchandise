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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import java.util.Map;

import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.tc.logging.Category;

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatusOrder;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ProcessingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

/**
 * Strategy implementation for function module ERP_LORD_GET_ALL, valid for ERP
 * releases >= ECC605. This product does not support lower releases, lower
 * release support needs to build via extensions!
 */
public class GetAllStrategyERP605 extends GetAllStrategyERP {

	/**
	 * Logging instance
	 */
	protected static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(GetAllStrategyERP605.class.getName());
	/**
	 * Log category
	 */
	protected static final Category category = null;

	@Override
	protected void determineStatus(final Header head,
			final JCoStructure esHvStatComV, final JCoTable ttItemVstatComV,
			final ObjectInstances objInstMap, final Map<String, Item> itemMap) {

		char dlvIStatus, giIStatus, rjIStatus, ordInvIStatus, dlvInvIStatus, prcStatus = ' ';
		EStatus totalShippingStatus = EStatus.NOT_RELEVANT;
		EStatus totalBillingStatus = EStatus.NOT_RELEVANT;

		if (head.isDocumentTypeOrder()) {

			final int numLines = ttItemVstatComV.getNumRows();
			for (int j = 0; j < numLines; j++) {

				ttItemVstatComV.setRow(j);
				final Item item = getParentItem(
						ttItemVstatComV.getString("HANDLE"), objInstMap,
						itemMap);

				final String dlvStatus = JCoHelper.getString(ttItemVstatComV,
						"LFSTA").length() == 0 ? " " : JCoHelper.getString(
						ttItemVstatComV, "LFSTA");
				final String giStatus = JCoHelper.getString(ttItemVstatComV,
						"WBSTA").length() == 0 ? " " : JCoHelper.getString(
						ttItemVstatComV, "WBSTA");
				final String rjStatus = JCoHelper.getString(ttItemVstatComV,
						"ABSTA").length() == 0 ? " " : JCoHelper.getString(
						ttItemVstatComV, "ABSTA");
				final String ordInvoiceStatus = JCoHelper.getString(
						ttItemVstatComV, "FKSAA").length() == 0 ? " "
						: JCoHelper.getString(ttItemVstatComV, "FKSAA");
				final String dlvInvoiceStatus = JCoHelper.getString(
						ttItemVstatComV, "FKSTA").length() == 0 ? " "
						: JCoHelper.getString(ttItemVstatComV, "FKSTA");
				final String procStatus = JCoHelper.getString(ttItemVstatComV,
						"GBSTA").length() == 0 ? " " : JCoHelper.getString(
						ttItemVstatComV, "GBSTA");
				dlvIStatus = dlvStatus.charAt(0);
				giIStatus = giStatus.charAt(0);
				rjIStatus = rjStatus.charAt(0);
				dlvInvIStatus = dlvInvoiceStatus.charAt(0);
				ordInvIStatus = ordInvoiceStatus.charAt(0);
				prcStatus = procStatus.charAt(0);

				final ShippingStatus shipStatus = getShippingStatusBean();
				shipStatus.init(EStatus.getStatusType(dlvIStatus),
						EStatus.getStatusType(giIStatus),
						EStatus.getStatusType(rjIStatus));

				item.setShippingStatus(shipStatus);

				// set billing status
				final BillingStatus billStatus = getBillingItemStatus();
				billStatus.init(EStatus.getStatusType(dlvIStatus),
						EStatus.getStatusType(ordInvIStatus),
						EStatus.getStatusType(dlvInvIStatus),
						EStatus.getStatusType(rjIStatus));

				item.setBillingStatus(billStatus);

				final OverallStatusOrder overallStatus = getOverallStatusBean();
				overallStatus.init(EStatus.getStatusType(prcStatus),
						shipStatus, billStatus,
						EStatus.getStatusType(rjIStatus));

				item.setOverallStatus(overallStatus);

				final ProcessingStatus processingStatus = getProcessingStatus();
				processingStatus.init(EStatus.getStatusType(prcStatus));
				item.setProcessingStatus(processingStatus);
			}

			// cumulate item status (shipping and billing) for setting it at
			// header level
			for (final Map.Entry<String, Item> entry : itemMap.entrySet()) {
				final Item cItem = entry.getValue();
				totalBillingStatus = EStatus.cumulateStatus(cItem
						.getBillingStatus().getStatus(), totalBillingStatus);
				totalShippingStatus = EStatus.cumulateStatus(cItem
						.getShippingStatus().getStatus(), totalShippingStatus);
			}

			// set shipping status
			final ShippingStatus shipStatus = getShippingStatusBean();
			shipStatus.init(totalShippingStatus);

			head.setShippingStatus(shipStatus);
			// set billing status
			final BillingStatus billStatus = (BillingStatus) genericFactory
					.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BILLING_HEADER_STATUS);

			billStatus.init(totalBillingStatus);

			head.setBillingStatus(billStatus);
			// set overall status-this won't be cumulated
			final String procStatus = JCoHelper
					.getString(esHvStatComV, "GBSTK").length() == 0 ? " "
					: JCoHelper.getString(esHvStatComV, "GBSTK");
			final String rjHeaderStatus = JCoHelper.getString(esHvStatComV,
					"ABSTK").length() == 0 ? " " : JCoHelper.getString(
					esHvStatComV, "ABSTK");
			rjIStatus = rjHeaderStatus.charAt(0);
			prcStatus = procStatus.charAt(0);
			// set processing status

			final OverallStatusOrder overallStatus = getOverallStatusBean();
			overallStatus.init(EStatus.getStatusType(prcStatus), shipStatus,
					billStatus, EStatus.getStatusType(rjIStatus));

			head.setOverallStatus(overallStatus);

		}
	}

	ProcessingStatus getProcessingStatus() {
		return (ProcessingStatus) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PROCESSING_STATUS);
	}

	BillingStatus getBillingItemStatus() {
		return (BillingStatus) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BILLING_ITEM_STATUS);
	}

	ShippingStatus getShippingStatusBean() {
		return (ShippingStatus) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SHIPPING_STATUS);
	}

	OverallStatusOrder getOverallStatusBean() {
		return (OverallStatusOrder) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_OVERALL_STATUS_ORDER);
	}

}
