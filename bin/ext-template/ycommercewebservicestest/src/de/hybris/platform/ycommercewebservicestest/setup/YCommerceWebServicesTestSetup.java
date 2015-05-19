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
 */
package de.hybris.platform.ycommercewebservicestest.setup;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.ycommercewebservicestest.constants.YcommercewebservicestestConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.io.InputStream;
import java.util.List;


@SystemSetup(extension = YcommercewebservicestestConstants.EXTENSIONNAME)
public class YCommerceWebServicesTestSetup
{
	private static final Logger LOG = Logger.getLogger(YCommerceWebServicesTestSetup.class);

	private String fileEncoding = "UTF-8";

	private SynchronizationService synchronizationService;
	private ImportService importService;
	private SolrIndexerService solrIndexerService;

	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		loadSampleData();
		getSynchronizationService().createProductCatalogSyncJob("wsTestProductCatalog");
		getSynchronizationService().executeCatalogSyncJob("wsTestProductCatalog");
		importImpexFile("/ycommercewebservicestest/import/promotions.impex", false); //must be loaded after synchronization
		importImpexFile("/ycommercewebservicestest/import/user-orders.impex", false);
		importImpexFile("/ycommercewebservicestest/import/reviews.impex", false);
		getSolrIndexerService().executeSolrIndexerCronJob("wsTestIndex");
	}

	protected void loadSampleData()
	{
		importImpexFile("/ycommercewebservicestest/import/base.impex", false);
		importImpexFile("/ycommercewebservicestest/import/countries.impex", false);
		importImpexFile("/ycommercewebservicestest/import/user-groups.impex", false);
		importImpexFile("/ycommercewebservicestest/import/delivery-modes.impex", false);
		importImpexFile("/ycommercewebservicestest/import/catalog.impex", false);
		importImpexFile("/ycommercewebservicestest/import/categories.impex", false);
		importImpexFile("/ycommercewebservicestest/import/classifications-units.impex", false);
		importImpexFile("/ycommercewebservicestest/import/categories-classifications.impex", false);
		importImpexFile("/ycommercewebservicestest/import/suppliers.impex", false);
		importImpexFile("/ycommercewebservicestest/import/products.impex", false);
		importImpexFile("/ycommercewebservicestest/import/products-classifications.impex", false);
		importImpexFile("/ycommercewebservicestest/import/products-prices.impex", false);
		importImpexFile("/ycommercewebservicestest/import/products-media.impex", false);
		importImpexFile("/ycommercewebservicestest/import/products-stocklevels.impex", false);
		importImpexFile("/ycommercewebservicestest/import/products-pos-stocklevels.impex", false);
		importImpexFile("/ycommercewebservicestest/import/products-relations.impex", false);
		importImpexFile("/ycommercewebservicestest/import/store.impex", false);
		importImpexFile("/ycommercewebservicestest/import/site.impex", false);
		importImpexFile("/ycommercewebservicestest/import/points-of-service.impex", false);
		importImpexFile("/ycommercewebservicestest/import/solr.impex", false);
		importImpexFile("/ycommercewebservicestest/import/vouchers.impex", false);

		loadOptionalSampleData();
	}

	protected void loadOptionalSampleData()
	{
		final List<String> extensionNames = Registry.getCurrentTenant().getTenantSpecificExtensionNames();
		if (extensionNames.contains("acceleratorwebservicesaddon"))
		{
			importImpexFile("/ycommercewebservicestest/import/acceleratorwebservicesaddon/solr.impex", true);
		}
	}

	protected void importImpexFile(final String file, final boolean legacyMode)
	{
		final String message = "Importing [" + file + "]...";
		try (InputStream resourceAsStream = getClass().getResourceAsStream(file))
		{
			if (resourceAsStream == null)
			{
				LOG.error(message + "ERROR (MISSING FILE)");
			}
			else
			{
				LOG.info(message);

				final ImportConfig importConfig = new ImportConfig();
				importConfig.setScript(new StreamBasedImpExResource(resourceAsStream, getFileEncoding()));
				importConfig.setLegacyMode(Boolean.valueOf(legacyMode));
				importConfig.setEnableCodeExecution(Boolean.valueOf(true));

				final ImportResult importResult = getImportService().importData(importConfig);
				if (importResult.isError())
				{
					LOG.error(message + " FAILED");
				}

			}
		}
		catch (final Exception e)
		{
			LOG.error(message + " FAILED", e);
		}
	}

	public SynchronizationService getSynchronizationService()
	{
		return synchronizationService;
	}

	@Required
	public void setSynchronizationService(final SynchronizationService synchronizationService)
	{
		this.synchronizationService = synchronizationService;
	}

	public SolrIndexerService getSolrIndexerService()
	{
		return solrIndexerService;
	}

	@Required
	public void setSolrIndexerService(final SolrIndexerService solrIndexerService)
	{
		this.solrIndexerService = solrIndexerService;
	}

	public String getFileEncoding()
	{
		return fileEncoding;
	}

	public void setFileEncoding(final String fileEncoding)
	{
		this.fileEncoding = fileEncoding;
	}

	public ImportService getImportService()
	{
		return importService;
	}

	@Required
	public void setImportService(final ImportService importService)
	{
		this.importService = importService;
	}

}
