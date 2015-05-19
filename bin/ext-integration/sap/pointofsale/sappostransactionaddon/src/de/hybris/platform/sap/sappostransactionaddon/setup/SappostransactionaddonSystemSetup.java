/**
 * 
 */
package de.hybris.platform.sap.sappostransactionaddon.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.CoreDataImportedEvent;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.sap.sappostransactionaddon.constants.SappostransactionaddonConstants;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class provides hooks into the system's initialization and update processes.
 */
@SystemSetup(extension = SappostransactionaddonConstants.EXTENSIONNAME)
public class SappostransactionaddonSystemSetup extends AbstractSystemSetup
{

	public static final String IMPORT_SYNC_CATALOGS = "contentCatalogs";

	public static final String APPAREL = "apparel";
	public static final String APPAREL_UK = "apparel-uk";
	public static final String APPAREL_DE = "apparel-de";
	public static final String ELECTRONICS = "electronics";

	/**
	 * Generates the Dropdown and Multi-Select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();
		params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_CATALOGS, "Import Content Catalog", true));
		return params;
	}

	/**
	 * This method will be called during the system initialization.
	 * 
	 * @param context
	 *           provides the selected parameters and values
	 */
	@SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL, extension = SappostransactionaddonConstants.EXTENSIONNAME)
	public void createProjectData(final SystemSetupContext context)
	{
		// Import POS transactions related CMS components and types for B2C stores
		importContentCatalog(context, APPAREL_UK);
		importContentCatalog(context, APPAREL_DE);
		importContentCatalog(context, ELECTRONICS);

		final List<ImportData> importData = new ArrayList<ImportData>();

		final ImportData apparelImportData = new ImportData();
		apparelImportData.setProductCatalogName(APPAREL);
		apparelImportData.setContentCatalogNames(Arrays.asList(APPAREL_UK, APPAREL_DE));
		apparelImportData.setStoreNames(Arrays.asList(APPAREL_UK, APPAREL_DE));
		importData.add(apparelImportData);

		final ImportData electronicsImportData = new ImportData();
		electronicsImportData.setProductCatalogName(ELECTRONICS);
		electronicsImportData.setContentCatalogNames(Arrays.asList(ELECTRONICS));
		electronicsImportData.setStoreNames(Arrays.asList(ELECTRONICS));
		importData.add(electronicsImportData);

		// Send an event to notify any AddOns that the data import is complete
		getEventService().publishEvent(new CoreDataImportedEvent(context, importData));
	}

	protected void importContentCatalog(final SystemSetupContext context, final String catalogName)
	{
		logInfo(context, "Begin importing catalog [" + catalogName + "]");

		// Import content catalog 
		importImpexFile(context, "/sappostransactionaddon/import/contentCatalogs/" + catalogName
				+ "ContentCatalog/cms-content.impex", true);

		logInfo(context, "Done importing catalog [" + catalogName + "]");

		// Synchronize content catalog
		synchronizeContentCatalog(context, catalogName);
	}

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

}
