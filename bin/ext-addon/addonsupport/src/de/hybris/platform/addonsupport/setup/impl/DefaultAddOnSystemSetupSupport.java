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
package de.hybris.platform.addonsupport.setup.impl;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.addonsupport.setup.AddOnSystemSetupSupport;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.ArrayList;
import java.util.List;


public class DefaultAddOnSystemSetupSupport extends AbstractSystemSetup implements AddOnSystemSetupSupport
{

	@Override
	public List<SystemSetupParameter> getInitializationOptions()
	{

		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_SITES, "Import Sites", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_CATALOGS, "Sync Products & Content Catalogs", false));
		params.add(createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", false));

		return params;

	}

	@Override
	public boolean synchronizeContentCatalog(final SystemSetupContext context, final String catalogName)
	{
		logInfo(context, "Begin synchronizing Content Catalog [" + catalogName + "] - synchronizing");


		boolean result = true;


		final PerformResult syncCronJobResult = executeCatalogSyncJob(context, catalogName);
		if (isSyncRerunNeeded(syncCronJobResult))
		{
			logInfo(context, "Catalog catalog [" + catalogName + "] sync has issues.");
			result = false;
		}


		logInfo(context, "Done synchronizing Content Catalog [" + catalogName + "]");
		return result;
	}

	@Override
	public boolean synchronizeProductCatalog(final SystemSetupContext context, final String catalogName)
	{
		logInfo(context, "Begin synchronizing Product Catalog [" + catalogName + "] - synchronizing");


		final PerformResult syncCronJobResult = executeCatalogSyncJob(context, catalogName);
		if (isSyncRerunNeeded(syncCronJobResult))
		{
			logInfo(context, "Product catalog [" + catalogName + "] sync has issues.");
			return false;
		}


		logInfo(context, "Done synchronizing Product Catalog [" + catalogName + "]");
		return true;
	}

	@Override
	public boolean getBooleanSystemSetupParameter(final SystemSetupContext context, final String key)
	{
		return super.getBooleanSystemSetupParameter(context, key);
	}

}
