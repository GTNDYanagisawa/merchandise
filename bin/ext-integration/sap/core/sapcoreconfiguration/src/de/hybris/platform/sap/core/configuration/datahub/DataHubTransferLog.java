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
package de.hybris.platform.sap.core.configuration.datahub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class handles the log for transfer of configuration models to the data hub.
 */
public class DataHubTransferLog
{
	private String itemCode = "";
	private final List<String> messageLog = new ArrayList<String>();
	private DataHubTransferException transferException = null;
	private Boolean transferFailed = null;

	/**
	 * Adds a message line to the log.
	 * 
	 * @param logMessage
	 *           log message
	 */
	public void addLog(final String logMessage)
	{
		messageLog.add(logMessage);
	}

	/**
	 * Sets the exception which can occur during invoking the transfer.
	 * 
	 * @param transferException
	 *           {@link DataHubTransferException}
	 */
	public void setTransferException(final DataHubTransferException transferException)
	{
		this.transferException = transferException;
		setTransferFailed(true);
	}

	/**
	 * Returns the exception which has occurred during invoking the transfer.
	 * 
	 * @return {@link DataHubTransferException}
	 */
	public DataHubTransferException getTransferException()
	{
		return transferException;
	}

	/**
	 * Sets the transfer failed flag.
	 * 
	 * @param transferFailed
	 *           the transferFailed to set
	 */
	public void setTransferFailed(final Boolean transferFailed)
	{
		this.transferFailed = transferFailed;
	}

	/**
	 * Returns an indicator whether the transfer has failed.
	 * 
	 * @return true if the transfer has failed
	 */
	public boolean transferFailed()
	{
		return (transferFailed != null ? transferFailed : false);
	}

	/**
	 * Sets the item code as string.
	 * 
	 * @param itemCode
	 *           item code as string
	 */
	public void setItemCode(final String itemCode)
	{
		this.itemCode = itemCode;
	}

	/**
	 * Returns the item code as string.
	 * 
	 * @return item code as string
	 */
	public String getItemCode()
	{
		return this.itemCode;
	}

	/**
	 * Returns the log.
	 * 
	 * @return message log
	 */
	public Collection<String> getLog()
	{
		return messageLog;
	}

}
