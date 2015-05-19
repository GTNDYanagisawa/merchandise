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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.util;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessage;

import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoTable;


/**
 * Message utilities
 */
public class MessageUtil
{

	private static Log4JWrapper log = Log4JWrapper.getInstance(MessageUtil.class.getName());

	/**
	 * Use this method to check a certain message had been issued. If the message has been found, the row pointer will be
	 * set correctly.
	 * 
	 * @param msgType
	 *           message type
	 * @param msgId
	 *           message id
	 * @param msgNo
	 *           message number
	 * @param msgV1
	 *           the 1st variable of the message to replace
	 * @param msgV2
	 *           the 2nd variable of the message to replace
	 * @param msgV3
	 *           the 3rd variable of the message to replace
	 * @param msgV4
	 *           the 4th variable of the message to replace
	 * @param messageRecord
	 *           JCO object containing the message
	 * @return true, if message was found
	 */
	public static boolean hasMessage(final String msgType, final String msgId, final String msgNo, final String msgV1,
			final String msgV2, final String msgV3, final String msgV4, final JCoRecord messageRecord)
	{
		log.entering("hasMessage");

		boolean retVal = false;
		if ((messageRecord == null))
		{

			if (log.isDebugEnabled())
			{
				log.debug("No message record to process");
			}
			log.exiting();
			return false; // No message record so no error could be occured?
		}

		final BackendMessage messageToCompare = new BackendMessage(msgType, msgId, msgNo, msgV1, msgV2, msgV3, msgV4);

		int numMessage = 1;
		if (messageRecord instanceof JCoTable)
		{
			numMessage = ((JCoTable) messageRecord).getNumRows();
			((JCoTable) messageRecord).firstRow();
		}


		for (int i = 0; i < numMessage; i++)
		{
			// Create message object
			final BackendMessage backendMessage = new BackendMessage(messageRecord);
			final boolean messageFound = backendMessage.equals(messageToCompare);

			if (messageFound)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Message found (ID='" + msgId + "' Number='" + msgNo + "' Type=" + msgType + "' Var1='" + msgV1
							+ "' Var2='" + msgV2 + "' Var3='" + msgV3 + "' Var4='" + msgV4 + "')");
				}
				retVal = true;
				break;
			}

			if (messageRecord instanceof JCoTable)
			{
				((JCoTable) messageRecord).nextRow();
			}
		}

		log.exiting();
		return retVal;
	}

}
