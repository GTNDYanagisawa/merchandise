/**
 *
 */
package de.hybris.platform.sap.productconfig.frontend.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.impex.systemsetup.ImpExSystemSetup;
import de.hybris.platform.sap.productconfig.frontend.constants.Sapproductconfigb2baddonConstants;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;


@SystemSetup(extension = Sapproductconfigb2baddonConstants.EXTENSIONNAME)
public class ProductConfigSetup extends AbstractSystemSetup
{

	public static final String POWERTOOLS = "powertools";
	public static final String IMPORT_SYNC_CATALOGS = "syncProducts&ContentCatalogs";


	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		final String impexPatternKey = context.getExtensionName() + "." + ImpExSystemSetup.PARAMETER_PROJECT;
		final String oldPatternCfg = Config.getParameter(impexPatternKey);
		Config.setParameter(impexPatternKey, "resources/impex/powertools/projectdata*.impex");
		//execute IMPEX before CATALOG SYNC
		logInfo(context, "############# SAP PRODUCT CONFIG STARTING IMPEX IMPORT ##############");
		final ImpExSystemSetup impexImporter = new ImpExSystemSetup();
		impexImporter.createAutoImpexProjectData(context);
		logInfo(context, "############# SAP PRODUCT CONFIG END IMPEX IMPORT ##############");

		//execute CATALOG SYNC after IMPEX
		final boolean syncCatalogs = getBooleanSystemSetupParameter(context, IMPORT_SYNC_CATALOGS);
		if (syncCatalogs)
		{
			boolean executeSync = true;
			int syncCounter = 1;
			logInfo(context, "############# SAP PRODUCT CONFIG STARTING CATALOG SYNC ##############");
			while (executeSync && syncCounter <= 5)
			{
				logInfo(context, syncCounter + " try to trigger catalog sync");
				final PerformResult productSyncReult = executeCatalogSyncJob(context, POWERTOOLS + "ProductCatalog");
				final PerformResult contentSyncReult = executeCatalogSyncJob(context, POWERTOOLS + "ContentCatalog");
				logInfo(context, "ContentCatalog sync result: " + contentSyncReult.getResult());
				logInfo(context, "ProductCatalog sync result: " + productSyncReult.getResult());
				executeSync = !CronJobResult.SUCCESS.equals(contentSyncReult.getResult())
						|| !CronJobResult.SUCCESS.equals(productSyncReult.getResult());
				syncCounter++;
			}
			logInfo(context, "############# SAP PRODUCT CONFIG END CATALOG SYNC ##############");
		}
		else
		{
			logInfo(context, "SAP PRODUCT CONFIG CATALOG SYNC NOT REQUESTED!");
		}
		Config.setParameter(impexPatternKey, oldPatternCfg);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.acceleratorservices.setup.AbstractSystemSetup#getInitializationOptions()
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();
		params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_CATALOGS, "Sync Products & Content Catalogs", true));
		return params;
	}

}
