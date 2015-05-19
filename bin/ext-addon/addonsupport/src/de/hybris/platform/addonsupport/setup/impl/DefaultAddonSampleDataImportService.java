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
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.addonsupport.setup.AddOnSampleDataImportService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.ArrayList;
import java.util.List;


public class DefaultAddonSampleDataImportService extends AbstractSystemSetup implements AddOnSampleDataImportService
{

	private static final String IMPORT_SAMPLE_DATA = "importSampleData";
	private static final String BTG_EXTENSION_NAME = "btg";
	private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";


	@Override
	public void importSampleData(final String extensionName, final SystemSetupContext context, final List<ImportData> importData,
			final boolean solrReindex)
	{

		if (getBooleanSystemSetupParameter(context, IMPORT_SAMPLE_DATA))
		{
			importCommonData(context, extensionName);

			for (final ImportData importd : importData)
			{
				importProductCatalog(context, extensionName, importd.getProductCatalogName());
			}

			for (final ImportData importd : importData)
			{
				for (final String contentCatalogName : importd.getContentCatalogNames())
				{
					importContentCatalog(context, extensionName, contentCatalogName);
					importStore(context, extensionName, contentCatalogName);
				}
			}

			for (final ImportData importd : importData)
			{
				for (final String storeName : importd.getStoreNames())
				{
					importStoreLocations(context, extensionName, storeName);

				}

				importStoreInitialData(context, extensionName, importd.getStoreNames(), importd.getProductCatalogName(),
						importd.getContentCatalogNames(), solrReindex);

			}
		}
	}


	/**
	 * Imports Common Data
	 */
	protected void importCommonData(final SystemSetupContext context, final String importDirectory)
	{
		logInfo(context, "Importing Common Data...");

		final String importRoot = "/" + importDirectory + "/import";

		importImpexFile(context, importRoot + "/common/user-groups.impex", false);

		final List<String> loadedExtensionNames = getLoadedExtensionNames();
		if (isExtensionLoaded(loadedExtensionNames, "cmscockpit"))
		{
			importImpexFile(context, importRoot + "/cockpits/cmscockpit/cmscockpit-users.impex", false);
		}

		if (isExtensionLoaded(loadedExtensionNames, "productcockpit"))
		{
			importImpexFile(context, importRoot + "/cockpits/productcockpit/productcockpit-users.impex", false);
		}

		if (isExtensionLoaded(loadedExtensionNames, "cscockpit"))
		{
			importImpexFile(context, importRoot + "/cockpits/cscockpit/cscockpit-users.impex", false);
		}

		if (isExtensionLoaded(loadedExtensionNames, "reportcockpit"))
		{
			importImpexFile(context, importRoot + "/cockpits/reportcockpit/reportcockpit-users.impex", false);

			if (isExtensionLoaded(loadedExtensionNames, "mcc"))
			{
				importImpexFile(context, importRoot + "/cockpits/reportcockpit/reportcockpit-mcc-links.impex", false);
			}
		}

		// support a general script for extra uncategorized stuff
		importImpexFile(context, importRoot + "/common/common-addon-extra.impex", false);

	}

	protected boolean isExtensionLoaded(final List<String> loadedExtensionNames, final String extensionNameToCheck)
	{
		return loadedExtensionNames.contains(extensionNameToCheck);
	}

	protected void importProductCatalog(final SystemSetupContext context, final String importDirectory, final String catalogName)
	{
		logInfo(context, "Begin importing Product Catalog [" + catalogName + "]");

		// Load Units
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/classifications-units.impex", false);

		// Load Categories
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/categories.impex", false);

		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/categories-classifications.impex", false);

		// Load Suppliers
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/suppliers.impex", false);
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/suppliers-media.impex", false);

		// Load medias for Categories as Suppliers loads some new Categories
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/categories-media.impex", false);

		// Load Products
		importImpexFile(context,
				"/" + importDirectory + "/import/productCatalogs/" + catalogName + "ProductCatalog/products.impex", false);

		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/products-media.impex", false);
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/products-classifications.impex", false);

		// Load Products Relations
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/products-relations.impex", false);

		// Load Products Fixes
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/products-fixup.impex", false);

		// Load Prices
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/products-prices.impex", false);

		// Load Stock Levels
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/products-stocklevels.impex", false);

		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/products-pos-stocklevels.impex", false);

		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/products-tax.impex", false);


		// Load Users Access Rights for specific catalog
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName + "ProductCatalog/users.impex",
				false);

		// support a general script for extra uncategorized script
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + catalogName
				+ "ProductCatalog/products-addon-extra.impex", false);

	}

	protected void importContentCatalog(final SystemSetupContext context, final String importDirectory, final String catalogName)
	{
		logInfo(context, "Begin importing Content Catalog [" + catalogName + "]");

		final String importRoot = "/" + importDirectory + "/import";

		importImpexFile(context, importRoot + "/contentCatalogs/" + catalogName + "ContentCatalog/cms-content.impex", false);
		importImpexFile(context, importRoot + "/contentCatalogs/" + catalogName + "ContentCatalog/cms-mobile-content.impex", false);
		importImpexFile(context, importRoot + "/contentCatalogs/" + catalogName + "ContentCatalog/email-content.impex", false);

		// support a general script for extra uncategorized script
		importImpexFile(context, importRoot + "/contentCatalogs/" + catalogName + "ContentCatalog/cms-addon-extra.impex", false);

		logInfo(context, "Done importing Content Catalog [" + catalogName + "]");
	}

	protected void importStoreLocations(final SystemSetupContext context, final String importDirectory, final String storeName)
	{
		logInfo(context, "Begin importing store [" + storeName + "]");

		final String importRoot = "/" + importDirectory + "/import";
		importImpexFile(context, importRoot + "/stores/" + storeName + "/points-of-service-media.impex", false);
		importImpexFile(context, importRoot + "/stores/" + storeName + "/points-of-service.impex", false);

		// support a general script for extra uncategorized script
		importImpexFile(context, importRoot + "/stores/" + storeName + "/points-of-service-addon-extra.impex", false);

		logInfo(context, "Done importing store [" + storeName + "]");
	}



	protected void importStore(final SystemSetupContext context, final String extensionName, final String storeName)
	{
		logInfo(context, "Begin importing store [" + storeName + "]");

		importImpexFile(context, "/" + extensionName + "/import/stores/" + storeName + "/store.impex", false);
		importImpexFile(context, "/" + extensionName + "/import/stores/" + storeName + "/site.impex", false);

		logInfo(context, "Done importing store [" + storeName + "]");
	}

	protected void importStoreInitialData(final SystemSetupContext context, final String importDirectory,
			final List<String> storeNames, final String productCatalog, final List<String> contentCatalogs, final boolean solrReindex)
	{
		final String importRoot = "/" + importDirectory + "/import";

		for (final String storeName : storeNames)
		{

			logInfo(context, "Begin importing store [" + storeName + "]");

			logInfo(context, "Begin importing advanced personalization rules for [" + storeName + "]");


			if (isExtensionLoaded(BTG_EXTENSION_NAME))
			{
				importImpexFile(context, importRoot + "/stores/" + storeName + "/btg.impex", false);
			}


			logInfo(context, "Begin importing warehouses for [" + storeName + "]");

			importImpexFile(context, importRoot + "/stores/" + storeName + "/warehouses.impex", false);
		}

		// perform product sync job
		final boolean productSyncSuccess = synchronizeProductCatalog(context, productCatalog, true);
		if (!productSyncSuccess)
		{
			logInfo(context, "Product catalog synchronization for [" + productCatalog
					+ "] did not complete successfully, that's ok, we will rerun it after the content catalog sync.");
		}

		for (final String storeName : storeNames)
		{
			importImpexFile(context, importRoot + "/stores/" + storeName + "/solr.impex", false);
		}


		// perform content sync jobs
		for (final String contentCatalog : contentCatalogs)
		{
			synchronizeContentCatalog(context, contentCatalog, true);
		}

		if (!productSyncSuccess)
		{
			// Rerun the product sync if required
			logInfo(context, "Rerunning product catalog synchronization for [" + productCatalog + "]");
			if (!synchronizeProductCatalog(context, productCatalog, true))
			{
				logError(context, "Rerunning product catalog synchronization for [" + productCatalog
						+ "], failed please consult logs for more details.", null);
			}
		}

		// Load reviews after synchronization is done
		importImpexFile(context, "/" + importDirectory + "/import/productCatalogs/" + productCatalog
				+ "ProductCatalog/reviews.impex", false);

		for (final String storeName : storeNames)
		{
			// Load promotions after synchronization is done
			importImpexFile(context, "/" + importDirectory + "/import/stores/" + storeName + "/promotions.impex", false);

			if (solrReindex)
			{
				// Index product data
				logInfo(context, "Begin SOLR re-index [" + storeName + "]");
				executeSolrIndexerCronJob(storeName + "Index", true);
				logInfo(context, "Done SOLR re-index [" + storeName + "]");
			}

			if (getBooleanSystemSetupParameter(context, ACTIVATE_SOLR_CRON_JOBS))
			{
				logInfo(context, "Activating SOLR index job for [" + productCatalog + "]");
				activateSolrIndexerCronJobs(productCatalog + "Index");
			}
		}
	}


	@Override
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA, "Import Sample Data", true));
		params.add(createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", true));

		return params;
	}

	protected boolean synchronizeProductCatalog(final SystemSetupContext context, final String catalogName, final boolean sync)
	{
		logInfo(context, "Begin synchronizing Product Catalog [" + catalogName + "] - "
				+ (sync ? "synchronizing" : "initializing job"));

		createProductCatalogSyncJob(context, catalogName + "ProductCatalog");

		boolean result = true;

		if (sync)
		{
			final PerformResult syncCronJobResult = executeCatalogSyncJob(context, catalogName + "ProductCatalog");
			if (isSyncRerunNeeded(syncCronJobResult))
			{
				logInfo(context, "Product catalog [" + catalogName + "] sync has issues.");
				result = false;
			}
		}

		logInfo(context, "Done " + (sync ? "synchronizing" : "initializing job") + " Product Catalog [" + catalogName + "]");
		return result;
	}

	protected boolean synchronizeContentCatalog(final SystemSetupContext context, final String catalogName, final boolean sync)
	{
		logInfo(context, "Begin synchronizing Content Catalog [" + catalogName + "] - "
				+ (sync ? "synchronizing" : "initializing job"));

		createContentCatalogSyncJob(context, catalogName + "ContentCatalog");

		boolean result = true;

		if (sync)
		{
			final PerformResult syncCronJobResult = executeCatalogSyncJob(context, catalogName + "ContentCatalog");
			if (isSyncRerunNeeded(syncCronJobResult))
			{
				logInfo(context, "Catalog catalog [" + catalogName + "] sync has issues.");
				result = false;
			}
		}

		logInfo(context, "Done " + (sync ? "synchronizing" : "initializing job") + " Content Catalog [" + catalogName + "]");
		return result;
	}


	protected boolean isExtensionLoaded(final String extensionNameToCheck)
	{
		final List<String> loadedExtensionNames = getLoadedExtensionNames();
		return loadedExtensionNames.contains(extensionNameToCheck);
	}

	protected List<String> getLoadedExtensionNames()
	{
		return Registry.getCurrentTenant().getTenantSpecificExtensionNames();
	}
}
