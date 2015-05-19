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
package de.hybris.platform.sap.core.configuration.datahub.interceptor;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.datahub.DataHubTransferConfiguration;
import de.hybris.platform.sap.core.configuration.datahub.DataHubTransferConfigurationManager;
import de.hybris.platform.sap.core.configuration.datahub.RemoveModelMap;
import de.hybris.platform.sap.core.configuration.populators.GenericModel2MapPopulator;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import java.util.HashMap;
import java.util.List;


/**
 * Performed when an item gets removed within the hmc.
 */
public class ConfigurationItemRemoveInterceptor implements RemoveInterceptor<ItemModel>
{

	private RemoveModelMap removeModelMap = null;
	private GenericModel2MapPopulator model2MapPopulator = null;
	private DataHubTransferConfigurationManager dataHubManager;


	@Override
	public void onRemove(final ItemModel model, final InterceptorContext ctx) throws InterceptorException
	{

		final List<DataHubTransferConfiguration> dataHubTransferConfigurations = dataHubManager
				.getDataHubTransferConfigurations(model.getItemtype());

		if (dataHubTransferConfigurations != null && dataHubTransferConfigurations.size() > 0)
		{
			// the attributes of the model classes are implemented lazy load. That mean if the attribute is not used then
			// the value is not loaded to the model class. 
			// The next statement loads the attributes of the model class because these values 
			// are needed when the item gets deleted in the datahub. Please see class ConfigurationSaveListener
			model2MapPopulator.populate(model, new HashMap<String, Object>());
			removeModelMap.addModelToBeDeleted(model.getPk(), model);
		}
	}

	@SuppressWarnings("javadoc")
	public void setRemoveModelMap(final RemoveModelMap modelMap)
	{
		this.removeModelMap = modelMap;
	}


	@SuppressWarnings("javadoc")
	public void setGenericModel2MapPopulator(final GenericModel2MapPopulator model2MapPopulator)
	{
		this.model2MapPopulator = model2MapPopulator;
	}

	@SuppressWarnings("javadoc")
	public void setDataHubManager(final DataHubTransferConfigurationManager dataHubManager)
	{
		this.dataHubManager = dataHubManager;
	}

}
