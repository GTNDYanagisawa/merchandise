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

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.tc.logging.Severity;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.util.MessageUtil;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.BackendExceptionECOERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.BackendMessageMapper;

/**
 * Base class for strategy implementations dealing with ERP LO-API calls
 */
public class BaseStrategyERP {

	/**
	 * Factory to access SAP session beans
	 */
	protected GenericFactory genericFactory;

	/**
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory() {
		return genericFactory;
	}

	/**
	 * @param genericFactory
	 *            the genericFactory to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}

	/**
	 * Number of decimal digits used for currency values in R/3 systems.
	 * <p>
	 * See transaction OY04.
	 */
	public static final int ERP_CURRENCY_DEFAULT_DECIMAL_DIGITS = 2;

	/**
	 * Logging instance
	 */
	protected static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(BaseStrategyERP.class.getName());

	/**
	 * Represents true in ABAP
	 */
	protected static final String XFLAG = "X";
	/**
	 * Represents false in ABAP
	 */
	protected static final String FALSE = "F";

	private static final String DIRECTION_IN = " - IN: ";
	private static final String DIRECTION_OUT = " - OUT: ";

	private ERPLO_APICustomerExits custExit;

	/**
	 * @param custExit
	 *            the custExit to set
	 */
	public void setCustExit(final ERPLO_APICustomerExits custExit) {
		this.custExit = custExit;
	}

	private BackendMessageMapper messageMapper;

	/**
	 * @return the messageMapper
	 */
	public BackendMessageMapper getMessageMapper() {
		return messageMapper;
	}

	/**
	 * Indicator that marks a message to be hidden
	 */
	public static final String HIDE_MESSAGE = "Hide";

	/**
	 * Represents the data returned by a call to the function module.
	 * 
	 * @version 1.0
	 */
	public static class ReturnValue {
		/**
		 * Return code of a FM call
		 */
		protected String myReturnCode;

		/**
		 * Creates a new instance
		 * 
		 * @param messages
		 *            Table containing the messages returned from the backend
		 * @param returnCode
		 *            The return code returned from the backend
		 */
		public ReturnValue(final JCoTable messages, final String returnCode) {

			myReturnCode = returnCode;
		}

		/**
		 * Creates a new instance
		 * 
		 * @param message
		 *            Structure containing only one message (Structure can be
		 *            different as for the messages table)
		 * @param messages
		 *            Table containing the messages returned from the backend
		 * @param returnCode
		 *            The return code returned from the backend
		 */
		public ReturnValue(final JCoTable messages, final JCoStructure message,
				final String returnCode) {

			myReturnCode = returnCode;
		}

		/**
		 * Creates a new instance
		 * 
		 * @param returnCode
		 *            The return code returned from the backend
		 */
		public ReturnValue(final String returnCode) {

			myReturnCode = returnCode;
		}

		/**
		 * @return Return code from a JCO FM call
		 */
		public String getReturnCode() {
			return myReturnCode;
		}

	}

	/**
	 * Checks if an attribute is initial and throws an exception if this is the
	 * case.
	 * 
	 * @param attribute
	 *            if it is a string, emptyness is checked in addition
	 * @param attributeName
	 * @throws BackendExceptionECOERP
	 */
	protected static void checkAttributeEmpty(final Object attribute,
			final String attributeName) throws BackendExceptionECOERP {
		boolean throwMessage = false;
		if (attribute == null) {
			throwMessage = true;
		} else if (attribute instanceof String) {
			final String attributeAsString = (String) attribute;
			if (attributeAsString.isEmpty()) {
				throwMessage = true;
			}
		} else if (attribute instanceof TechKey) {
			final TechKey attributeAsKey = (TechKey) attribute;
			if (attributeAsKey.isInitial()) {
				throwMessage = true;
			}
		}
		if (throwMessage) {
			final Message message = new Message(Message.ERROR,
					"sapsalestransactions.erp.missingattr");
			throw new BackendExceptionECOERP(message.getMessageText(LocaleUtil
					.getLocale()) + ": " + attributeName);
		}

	}

	/**
	 * Logs a RFC-call into the log file of the application.
	 * 
	 * @param functionName
	 *            the name of the JCo function that was executed
	 * @param input
	 *            input data for the function module. You may set this parameter
	 *            to <code>null</code> if you don't have any data to be logged.
	 * @param output
	 *            Log4 * output data for the function module. You may set this
	 *            parameter to <code>null</code> if you don't have any data to
	 *            be logged.
	 * @param log
	 *            the logging context to be used
	 */
	public static void logCall(final String functionName,
			final JCoRecord input, final JCoRecord output,
			final Log4JWrapper log) {

		creatRecordLogEntry(functionName, input, DIRECTION_IN, log);
		creatRecordLogEntry(functionName, output, DIRECTION_OUT, log);

	}

	static void creatRecordLogEntry(final String functionName,
			final JCoRecord record, final String direction,
			final Log4JWrapper log) {
		final StringBuffer buffer = new StringBuffer();
		createBufferForLogging(functionName, record, direction, buffer);

		log.debug(buffer.toString());

	}

	/**
	 * @param functionName
	 * @param record
	 * @param direction
	 * @param buffer
	 */
	static void createBufferForLogging(final String functionName,
			final JCoRecord record, final String direction,
			final StringBuffer buffer) {
		boolean recordOK = true;

		if (record instanceof JCoTable) {
			final JCoTable inputTable = (JCoTable) record;
			if (inputTable.getNumRows() <= 0) {
				recordOK = false;
			}
		}

		if ((record != null) && recordOK) {

			buffer.append("::").append(functionName).append("::")
					.append(direction).append(record.getMetaData().getName())
					.append(" * ");
			final JCoFieldIterator iterator = record.getFieldIterator();
			while (iterator.hasNextField()) {
				final JCoField field = iterator.nextField();
				buffer.append(field.getName()).append("='")
						.append(field.getString()).append("' ");
			}

		}
	}

	/**
	 * Traces a function module call
	 * 
	 * @param functionName
	 *            Name of RFC function
	 * @param input
	 *            Import attributes
	 * @param output
	 *            Export attributes
	 */
	protected static void logCall(final String functionName,
			final JCoRecord input, final JCoRecord output) {
		logCall(functionName, input, output, sapLogger);
	}

	/**
	 * Can we recover from this error situation?
	 * 
	 * @param esError
	 *            Error structure of type TDS_ERROR
	 * @return true if we can proceed after LO-API LOAD
	 */
	protected boolean isRecoverableHeaderError(final JCoStructure esError) {
		final String messageId = esError.getString("MSGID");
		final String messageNumber = esError.getString("MSGNO");
		final String firstParameter = esError.getString("MSGV1");
		// SLS_LORD / 018 / VDATU: Required delivery date must be specified. Can
		// be corrected
		final boolean isRecoverable = "SLS_LORD".equals(messageId)
				&& "018".equals(messageNumber)
				&& "VDATU".equals(firstParameter);
		if (sapLogger.isDebugEnabled()) {
			sapLogger.debug("Error message during load can be handled: "
					+ isRecoverable);
		}
		return isRecoverable;
	}

	/**
	 * Logs and attaches messages to a given document.
	 * 
	 * @param bob
	 *            Target business object the messages must be applied to
	 * 
	 * @param msgTable
	 *            Table with messages
	 * @param singleMsg
	 *            Structure containing one single message
	 * @throws BackendException
	 */

	protected void dispatchMessages(final BusinessObject bob,
			final JCoTable msgTable, final JCoStructure singleMsg)
			throws BackendException {

		sapLogger.entering("dispatchMessages");
		messageMapper.map(bob, singleMsg, msgTable);
		sapLogger.exiting();
	}

	/**
	 * Use this method to check a certain message had been issued
	 * 
	 * @param msgType
	 *            message type
	 * @param msgId
	 *            message id
	 * @param msgNo
	 *            message number
	 * @param msgV1
	 *            the 1st variable of the message to replace
	 * @param msgV2
	 *            the 2nd variable of the message to replace
	 * @param msgV3
	 *            the 3rd variable of the message to replace
	 * @param msgV4
	 *            the 4th variable of the message to replace
	 * @param msgTable
	 *            jCoTable containing jCo message from back end
	 * @param singleMsg
	 *            a single jCo message
	 * @return true, if the specified message is contained in msgTable
	 */
	public static boolean hasMessage(final String msgType, final String msgId,
			final String msgNo, final String msgV1, final String msgV2,
			final String msgV3, final String msgV4, final JCoTable msgTable,
			final JCoStructure singleMsg) {
		boolean retVal = false;

		retVal = MessageUtil.hasMessage(msgType, msgId, msgNo, msgV1, msgV2,
				msgV3, msgV4, singleMsg);

		if (!retVal) {
			retVal = MessageUtil.hasMessage(msgType, msgId, msgNo, msgV1,
					msgV2, msgV3, msgV4, msgTable);
		}

		return retVal;
	}

	/**
	 * Ease the creation of the customer exit implementation. If desired,
	 * implementation can be changed by overwriting this method.
	 * 
	 * @return the customer exit implementation
	 */
	protected ERPLO_APICustomerExits getCustExit() {
		return custExit;
	}

	/**
	 * @param messageMapper
	 *            Maps backend message to BOL messages (refer to messages.xml)
	 */
	public void setMessageMapper(final BackendMessageMapper messageMapper) {
		this.messageMapper = messageMapper;
	}

	/**
	 * @param esError
	 */
	void logErrorMessageAndThrowException(final JCoStructure esError) {
		final String msgId = esError.getString("MSGID");
		final String msgNo = esError.getString("MSGNO");
		final String text = esError.getString("TEXT");
		final Object[] msgArgs = new Object[] { msgId, msgNo, text };
		final String message = "Non recoverable error when accessing sales document";
		sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS, message
				+ ": {0},{1},{2}", msgArgs);
		throw new ApplicationBaseRuntimeException(message);
	}

}
