/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.telcostore.setup;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.commercesearch.model.SolrHeroProductDefinitionModel;
import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateStatusModel;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Telco specific implementation of the {@link SampleDataImportService}:
 * <p>
 * <ul>
 * <li>imports additional sample data for subscriptions and bundles
 * <li>adds the types {@link BundleTemplateModel} and {@link BundleTemplateStatusModel} as new root types to the catalog
 * sync job
 * <ul>
 * <p>
 */
public class TelcoSampleDataImportService extends SampleDataImportService
{
	private TypeService typeService;
	private ModelService modelService;

	@Override
	protected void importProductCatalog(final String extensionName, final String productCatalogName)
	{
		// Load Billing Plans
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/billingPlans.impex", extensionName,
						productCatalogName), false);

		// Load Subscription Terms
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/subscriptionterms.impex", extensionName,
						productCatalogName), false);

		// Load Usage Units
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/usageunits.impex", extensionName,
						productCatalogName), false);

		super.importProductCatalog(extensionName, productCatalogName);

		// Load Entitlements
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/entitlements.impex", extensionName,
						productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-entitlements.impex", extensionName,
						productCatalogName), false);

		// Load per Usage Pricing
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/usage-charges.impex", extensionName,
						productCatalogName), false);

		// Load Bundle Template data
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/bundletemplates.impex", extensionName,
						productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/bundletemplates-products.impex", extensionName,
						productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/bundletemplates-selectioncriteria.impex",
						extensionName, productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/bundletemplates-pricerules.impex",
						extensionName, productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/bundletemplates-disablerules.impex",
						extensionName, productCatalogName), false);

	}

	@Override
	public boolean synchronizeProductCatalog(final AbstractSystemSetup systemSetup, final SystemSetupContext context,
			final String catalogName, final boolean syncCatalogs)
	{
		systemSetup.logInfo(context, String.format("Begin synchronizing Product Catalog [%s]", catalogName));

		getSetupSyncJobService().createProductCatalogSyncJob(String.format("%sProductCatalog", catalogName));

		final List<SyncItemJobModel> syncItemJobs = getCatalogVersionService().getCatalogVersion(
				String.format("%sProductCatalog", catalogName), CatalogManager.OFFLINE_VERSION).getSynchronizations();

		if (CollectionUtils.isNotEmpty(syncItemJobs))
		{
			for (final SyncItemJobModel syncItemJob : syncItemJobs)
			{
				final List<ComposedTypeModel> rootTypes = new ArrayList<ComposedTypeModel>(syncItemJob.getRootTypes());
				final ComposedTypeModel bundleTemplateStatus = typeService.getComposedTypeForClass(BundleTemplateStatusModel.class);
				final ComposedTypeModel bundleTemplate = typeService.getComposedTypeForClass(BundleTemplateModel.class);
				final ComposedTypeModel solrHeroProductDefinition = typeService
						.getComposedTypeForClass(SolrHeroProductDefinitionModel.class);
				rootTypes.add(0, bundleTemplateStatus);
				rootTypes.add(0, bundleTemplate);
				rootTypes.add(0, solrHeroProductDefinition);
				syncItemJob.setRootTypes(rootTypes);
				modelService.save(syncItemJob);
			}
		}

		if (syncCatalogs)
		{
			final PerformResult syncCronJobResult = getSetupSyncJobService().executeCatalogSyncJob(
					String.format("%sProductCatalog", catalogName));
			if (isSyncRerunNeeded(syncCronJobResult))
			{
				systemSetup.logInfo(context, String.format("Product Catalog [%s] sync has issues.", catalogName));
				return false;
			}
		}

		return true;
	}

	/**
	 * @return the typeService
	 */
	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
