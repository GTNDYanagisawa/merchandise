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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.sap.core.configuration.dao.GenericConfigurationDao;
import de.hybris.platform.sap.core.configuration.model.SAPAdministrationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.datahub.core.rest.DataHubCommunicationException;


/**
 * This class handles the initial load of all data hub transfer configurations to the data hub.
 * 
 */
public class DataHubInitialLoadHandler
{
	private static final Logger LOG = Logger.getLogger(DataHubInitialLoadHandler.class.getName());

	private DataHubTransferConfigurationManager dataHubManager;
	private DataHubTransferHandler dataHubTransferHandler;
	private GenericConfigurationDao genericConfigurationDao;
	private ModelService modelService;
	private Boolean dataHubOutboundEnabled;

	/**
	 * Executes the initial load of all registered configuration transfer objects.
	 * 
	 * @return collection of {@link DataHubTransferLog}
	 */
	public Collection<DataHubTransferLog> executeInitialLoad()
	{
		final Collection<DataHubTransferLog> transferLogs = new ArrayList<DataHubTransferLog>();
		final Map<String, List<DataHubTransferConfiguration>> allDataHubConfigurations = dataHubManager
				.getAllDataHubConfigurations();
		boolean failed = false;

		if (!isDataHubOutboundEnabled())
		{
			final DataHubTransferLog dataHubTransferLog = new DataHubTransferLog();
			dataHubTransferLog.addLog(Localization.getLocalizedString("dataTransfer.dataHub.notEnabled"));
			dataHubTransferLog.setTransferFailed(true);
			transferLogs.add(dataHubTransferLog);
			return transferLogs;
		}
		for (final Entry<String, List<DataHubTransferConfiguration>> dataHubConfigurationEntry : allDataHubConfigurations
				.entrySet())
		{

			final DataHubTransferLog dataHubTransferLog = new DataHubTransferLog();

			final String code = dataHubConfigurationEntry.getKey();
			// first delete the items on the data hub
			try
			{
				dataHubTransferHandler.invokeDeleteContent(code, dataHubConfigurationEntry.getValue(), dataHubTransferLog);
				// Get all modes for requested type
				final List<ItemModel> models = genericConfigurationDao.getAllModelsForCode(code);
				// Transfer models to data hub
				dataHubTransferHandler.invokeTransfer(code, models, dataHubConfigurationEntry.getValue(), dataHubTransferLog);

				if (dataHubTransferLog.transferFailed())
				{
					failed = true;
				}
				transferLogs.add(dataHubTransferLog);

			}
			catch (final DataHubCommunicationException ex)
			{
				final String logMessage = ex.getLocalizedMessage();
				final DataHubTransferException dataHubTransferException = new DataHubTransferException(logMessage, ex);
				dataHubTransferLog.addLog(logMessage);
				dataHubTransferLog.setTransferException(dataHubTransferException);
				LOG.fatal(code + ": " + logMessage);

				transferLogs.add(dataHubTransferLog);
			}
			catch (final Exception ex)
			{
				final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.intialUpload.exception")
						+ ex.getLocalizedMessage();
				dataHubTransferLog.addLog(logMessage);
				dataHubTransferLog.setTransferException(new DataHubTransferException(logMessage, ex));
				LOG.fatal(code + ": " + logMessage, ex);

				transferLogs.add(dataHubTransferLog);
			}

		}
		// Update last initial upload time stamp
		if (!failed)
		{
			final List<ItemModel> sapAdministrationModels = genericConfigurationDao
					.getAllModelsForCode(SapcoreconfigurationConstants.ITEM_CODE_SAP_ADMINISTRATION);
			if (sapAdministrationModels.size() >= 1)
			{
				final SAPAdministrationModel sapAdministrationModel = (SAPAdministrationModel) sapAdministrationModels.get(0);
				sapAdministrationModel.setCore_lastDataHubInitialLoad(new Date());
				modelService.save(sapAdministrationModel);
			}
		}

		return transferLogs;
	}

	/**
	 * Injection setter for {@link DataHubTransferConfigurationManager}.
	 * 
	 * @param dataHubManager
	 *           the data hub manager to set
	 */
	@Required
	public void setDataHubManager(final DataHubTransferConfigurationManager dataHubManager)
	{
		this.dataHubManager = dataHubManager;
	}

	/**
	 * Injection setter for {@link DataHubTransferHandler}.
	 * 
	 * @param dataHubTransferHandler
	 *           the data hub transfer handler to set
	 */
	@Required
	public void setDataHubTransferHandler(final DataHubTransferHandler dataHubTransferHandler)
	{
		this.dataHubTransferHandler = dataHubTransferHandler;
	}

	/**
	 * Injection setter for {@link GenericConfigurationDao}.
	 * 
	 * @param genericConfigurationDao
	 *           the genericConfigurationDAO to set
	 */
	@Required
	public void setGenericConfigurationDao(final GenericConfigurationDao genericConfigurationDao)
	{
		this.genericConfigurationDao = genericConfigurationDao;
	}

	/**
	 * Injection setter for {@link ModelService}.
	 * 
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Checks if the data hub outbound for configuration is enabled. <br>
	 * The enablement is done by setting property <code>sapcoreconfiguration.datahuboutbound.enabled</code> to true
	 * (default: false).
	 * 
	 * @return the dataHubOutboundEnabled
	 */
	private boolean isDataHubOutboundEnabled()
	{
		if (dataHubOutboundEnabled == null)
		{
			dataHubOutboundEnabled = Config.getBoolean("sapcoreconfiguration.datahuboutbound.enabled", false);
		}
		return dataHubOutboundEnabled;
	}

}
