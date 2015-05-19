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

import com.sap.conn.jco.JCoTable;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.BackendUtil;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.IncompletionMapper;

/**
 * Maps the incompletion log from ERP SD to BOL messages during LO-API read
 */
public class IncompletionMapperImpl implements IncompletionMapper {

	private BackendUtil backendUtil;

	/**
	 * Message refers to header level
	 */
	protected static final String FIELD_HEADER = "000000";
	/**
	 * Indicates: Issue is related to product configuration
	 */
	protected static String FIELD_CFG = "CUOBJ";

	/**
	 * Dummy message class for incompletion messages
	 */
	protected static String MSG_CLASS_INCOMPLETION = "INC";

	@Override
	public void mapLogToMessage(final JCoTable incompLog,
			final JCoTable messages, final ItemList items) {
		if (incompLog.isEmpty()) {
			return;
		}
		incompLog.firstRow();
		do {
			final String fieldName = incompLog.getString("FDNAM");
			if (considerLogEntry(fieldName)) {
				messages.appendRow();
				final String posNr = incompLog.getString("POSNR");
				if (isHeaderLevel(posNr)) {
					mapAttributes(incompLog, messages);
				} else {
					mapItemMessage(incompLog, messages, items);
				}
			}
		} while (incompLog.nextRow());

	}

	/**
	 * Map incompletion message on item level. Messages on sub item level will
	 * not be considered.
	 * 
	 * @param incompLog
	 *            JCO table of incompletion log, data will be read from current
	 *            record
	 * @param messages
	 *            JCO table of messages, data will be written into current
	 *            record
	 * @param items
	 *            Sales document items
	 */
	protected void mapItemMessage(final JCoTable incompLog,
			final JCoTable messages, final ItemList items) {
		final Item item = backendUtil.findItem(items,
				incompLog.getString("POSNR"));
		if ((item != null) && TechKey.isEmpty(item.getParentId())) {
			mapAttributes(incompLog, messages);
			messages.setValue("MSGV2", item.getProductId());
		}

	}

	/**
	 * Maps attributes between incompletion log and messages. The data written
	 * to the message table must match the message mapping customizing in
	 * messages.xml
	 * 
	 * @param incompLog
	 *            JCO table of incompletion log, data will be read from current
	 *            record
	 * @param messages
	 *            JCO table of messages, data will be written into current
	 *            record
	 */
	protected void mapAttributes(final JCoTable incompLog,
			final JCoTable messages) {
		messages.setValue("MSGTY", "I");
		messages.setValue("MSGID",
				MSG_CLASS_INCOMPLETION + incompLog.getString("TBNAM"));
		messages.setValue("MSGNO", "000");
		messages.setValue("MSGV1", incompLog.getString("FDNAM"));
		messages.setValue("T_MSG", incompLog.getString("FIELD_DESCR"));
	}

	//

	/**
	 * Do we consider this entry in the incompletion log? This depends on entry
	 * in FDNAM, the standard implementation only takes care of issues related
	 * to CUOBJ
	 * 
	 * @param fieldName
	 * @return Do we process this entry?
	 */
	protected boolean considerLogEntry(final String fieldName) {
		return (fieldName != null && fieldName.equals(FIELD_CFG));
	}

	/**
	 * Checks: Message is on header level
	 * 
	 * @param fieldHeader
	 * @return Header level?
	 */
	protected boolean isHeaderLevel(final String fieldHeader) {
		return (fieldHeader != null && fieldHeader.equals(FIELD_HEADER));
	}

	/**
	 * @return the backendUtil
	 */
	public BackendUtil getBackendUtil() {
		return backendUtil;
	}

	/**
	 * @param backendUtil
	 *            the backendUtil to set
	 */
	public void setBackendUtil(final BackendUtil backendUtil) {
		this.backendUtil = backendUtil;
	}
}
