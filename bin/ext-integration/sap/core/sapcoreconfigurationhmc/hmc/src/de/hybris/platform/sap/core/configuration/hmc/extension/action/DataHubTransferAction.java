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
package de.hybris.platform.sap.core.configuration.hmc.extension.action;

import de.hybris.platform.hmc.util.action.ActionEvent;
import de.hybris.platform.hmc.util.action.ActionResult;
import de.hybris.platform.hmc.util.action.ItemAction;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.sap.core.common.util.GenericFactoryProvider;
import de.hybris.platform.sap.core.configuration.datahub.DataHubInitialLoadHandler;
import de.hybris.platform.sap.core.configuration.datahub.DataHubTransferLog;
import de.hybris.platform.sap.core.configuration.hmc.constants.SapcoreconfigurationhmcConstants;
import de.hybris.platform.util.localization.Localization;

import java.util.Collection;

import org.apache.log4j.Logger;


/**
 * Class is used to trigger a ping request for a selected RFC destination in hMC.
 */
public class DataHubTransferAction extends ItemAction
{

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(DataHubTransferAction.class.getName());

	private static final String ITEM_CODE_TYPE = "type.";

	private static final String ITEM_CODE_NAME = ".name";

	/**
	 * Triggers initial data transfer to data hub.
	 * 
	 * @param actionEvent
	 *           action event
	 * @return {@link ActionResult}
	 * @throws JaloBusinessException
	 *            action exception
	 */
	@Override
	public ActionResult perform(final ActionEvent actionEvent) throws JaloBusinessException
	{
		final DataHubInitialLoadHandler initialLoadHandler = GenericFactoryProvider.getInstance().getBean(
				"sapCoreDataHubInitialLoadHandler");
		LOG.info("Execute initial upload of configuration data to data hub");
		final Collection<DataHubTransferLog> transferLogs = initialLoadHandler.executeInitialLoad();
		return getActionResult(transferLogs);
	}

	/**
	 * Returns the action result based on the passed transfer logs.
	 * 
	 * @param transferLogs
	 *           transfer log collection
	 * @return {@link ActionResult}
	 */
	private ActionResult getActionResult(final Collection<DataHubTransferLog> transferLogs)
	{
		final StringBuffer actionMessageBuffer = new StringBuffer();
		int actionResult = ActionResult.OK;
		boolean refresh = true;
		for (final DataHubTransferLog transferLog : transferLogs)
		{
			final String itemCode = transferLog.getItemCode();
			final boolean isItemLog = (itemCode != null && !itemCode.isEmpty());
			if (isItemLog)
			{
				actionMessageBuffer.append(getNameForItemCode(transferLog.getItemCode())).append(":")
						.append(SapcoreconfigurationhmcConstants.CRLF);
			}
			if (transferLog.transferFailed())
			{
				actionResult = ActionResult.FAILED;
				refresh = false;
			}
			final Collection<String> logCollection = transferLog.getLog();
			for (final String logEntry : logCollection)
			{
				if (isItemLog)
				{
					actionMessageBuffer.append(" - ");
				}
				actionMessageBuffer.append(logEntry).append(SapcoreconfigurationhmcConstants.CRLF);
			}
		}
		actionMessageBuffer.insert(0, SapcoreconfigurationhmcConstants.CRLF);
		if (actionResult == ActionResult.FAILED)
		{
			actionMessageBuffer.insert(0, Localization.getLocalizedString("ActionResult.DataHubTransfer.failed"));
		}
		else
		{
			actionMessageBuffer.insert(0, Localization.getLocalizedString("ActionResult.DataHubTransfer.success"));
		}
		return new ActionResult(actionResult, actionMessageBuffer.toString(), refresh);
	}

	/**
	 * Returns the name for the itemCode if available otherwise the itemCode.
	 * 
	 * @param itemCode
	 *           item code
	 * @return name for the itemCode or the itemCode
	 */
	private String getNameForItemCode(final String itemCode)
	{
		final StringBuffer resourceKeyBuffer = new StringBuffer();
		resourceKeyBuffer.append(ITEM_CODE_TYPE).append(itemCode).append(ITEM_CODE_NAME);
		if (Localization.getLocalizedString(resourceKeyBuffer.toString()).startsWith(ITEM_CODE_TYPE))
		{
			final StringBuffer notFoundBuffer = new StringBuffer();
			notFoundBuffer.append("[").append(itemCode).append("]");
			return notFoundBuffer.toString();
		}
		return Localization.getLocalizedString(resourceKeyBuffer.toString());
	}

}
