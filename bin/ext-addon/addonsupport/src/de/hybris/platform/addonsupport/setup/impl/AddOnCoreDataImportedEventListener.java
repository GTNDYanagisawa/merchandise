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


import de.hybris.platform.addonsupport.data.AddonExtensionMetadata;
import de.hybris.platform.addonsupport.impex.AddonConfigDataImportType;
import de.hybris.platform.addonsupport.setup.AddOnConfigDataImportService;
import de.hybris.platform.addonsupport.setup.AddOnSystemSetupSupport;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.CoreDataImportedEvent;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.PopulatorList;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class AddOnCoreDataImportedEventListener extends AbstractEventListener<CoreDataImportedEvent>
{

	private static final Logger LOG = Logger.getLogger(AddOnCoreDataImportedEventListener.class);

	private Converter<AddOnDataImportEventContext, ImpexMacroParameterData> impexMacroParametersConverter;
	private PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> selectedProductCatalogImpexMacroParametersPopulators;
	private PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> selectedBaseSiteImpexMacroParametersPopulators;
	private PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> selectedContentCatalogImpexMacroParametersPopulators;


	private List<SiteChannel> supportedChannels = Arrays.asList(SiteChannel.values());

	private BaseSiteService baseSiteService;
	private CatalogService catalogService;
	private AddOnConfigDataImportService addonConfigDataImportService;
	private AddOnSystemSetupSupport addonSystemSetupSupport;
	private AddonExtensionMetadata addonExtensionMetadata;


	@Override
	protected void onEvent(final CoreDataImportedEvent event)
	{
		final AddOnDataImportEventContext context = new AddOnDataImportEventContext(event, getAddonExtensionMetadata());

		final ImpexMacroParameterData parameterData = getImpexMacroParametersConverter().convert(context);

		if (getAddonSystemSetupSupport().getBooleanSystemSetupParameter(event.getContext(), AddOnSystemSetupSupport.IMPORT_SITES))
		{
			for (final ImportData siteImport : event.getImportData())
			{
				if (LOG.isInfoEnabled())
				{
					LOG.info("importing addon [" + parameterData.getConfigExtensionName() + "] configuration for ["
							+ importDataToString(siteImport) + "]");
				}
				executeSiteImport(context, siteImport, parameterData);
			}
		}
	}

	protected String importDataToString(final ImportData importData)
	{
		return ToStringBuilder.reflectionToString(importData);
	}

	protected void populate(final AddOnDataImportEventContext source, final ImpexMacroParameterData target,
			final PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> list)
	{
		for (final Populator<AddOnDataImportEventContext, ImpexMacroParameterData> populator : list.getPopulators())
		{
			if (populator != null)
			{
				populator.populate(source, target);
			}
		}
	}

	protected void executeSiteImport(final AddOnDataImportEventContext context, final ImportData importData,
			final ImpexMacroParameterData parameterData)
	{
		final CatalogModel productCatalog = getCatalogService().getCatalogForId(
				importData.getProductCatalogName() + "ProductCatalog");
		context.setProductCatalog(productCatalog);
		populate(context, parameterData, getSelectedProductCatalogImpexMacroParametersPopulators());
		final boolean productFilesImported = getAddonConfigDataImportService().executeImport(
				parameterData.getConfigExtensionName(), AddonConfigDataImportType.PRODUCT, parameterData);
		if (productFilesImported
				&& getAddonSystemSetupSupport().getBooleanSystemSetupParameter(context.getSourceEvent().getContext(),
						AddOnSystemSetupSupport.IMPORT_SYNC_CATALOGS))
		{
			getAddonSystemSetupSupport().synchronizeProductCatalog(context.getSourceEvent().getContext(), productCatalog.getId());
		}

		for (final String store : importData.getStoreNames())
		{

			final BaseSiteModel baseSite = getBaseSiteService().getBaseSiteForUID(store);
			if (getSupportedChannels().contains(baseSite.getChannel()))
			{
				if (LOG.isInfoEnabled())
				{
					LOG.info("importing addon [" + parameterData.getConfigExtensionName() + "] configuration for [" + store + "]");
				}

				context.setBaseSite(baseSite);
				populate(context, parameterData, getSelectedBaseSiteImpexMacroParametersPopulators());

				if (baseSite instanceof CMSSiteModel)
				{
					final CMSSiteModel cmsSite = (CMSSiteModel) baseSite;
					for (final String contentCatalogName : importData.getContentCatalogNames())
					{
						final CatalogModel contentCatalog = getCatalogService().getCatalogForId(contentCatalogName + "ContentCatalog");
						if (cmsSite.getContentCatalogs().contains(contentCatalog))
						{
							context.setContentCatalog((ContentCatalogModel) contentCatalog);
							populate(context, parameterData, getSelectedContentCatalogImpexMacroParametersPopulators());
							final boolean contentFilesImported = getAddonConfigDataImportService().executeImport(
									parameterData.getConfigExtensionName(), AddonConfigDataImportType.CONTENT, parameterData);
							if (contentFilesImported
									&& getAddonSystemSetupSupport().getBooleanSystemSetupParameter(context.getSourceEvent().getContext(),
											AddOnSystemSetupSupport.IMPORT_SYNC_CATALOGS))
							{
								getAddonSystemSetupSupport().synchronizeContentCatalog(context.getSourceEvent().getContext(),
										contentCatalog.getId());
							}
							// import stores
							getAddonConfigDataImportService().executeImport(parameterData.getConfigExtensionName(),
									AddonConfigDataImportType.STORE, parameterData);
						}
					}
				}

				final boolean solrFilesImported = getAddonConfigDataImportService().executeImport(
						parameterData.getConfigExtensionName(), AddonConfigDataImportType.SOLR, parameterData);
				if ((solrFilesImported || productFilesImported)
						&& getAddonSystemSetupSupport().getBooleanSystemSetupParameter(context.getSourceEvent().getContext(),
								AddOnSystemSetupSupport.ACTIVATE_SOLR_CRON_JOBS))
				{
					getAddonSystemSetupSupport().executeSolrIndexerCronJob(parameterData.getSiteUid() + "Index", true);
				}

			}
		}

	}

	/**
	 * @return the impexMacroParametersConverter
	 */
	public Converter<AddOnDataImportEventContext, ImpexMacroParameterData> getImpexMacroParametersConverter()
	{
		return impexMacroParametersConverter;
	}


	/**
	 * @param impexMacroParametersConverter
	 *           the impexMacroParametersConverter to set
	 */
	@Required
	public void setImpexMacroParametersConverter(
			final Converter<AddOnDataImportEventContext, ImpexMacroParameterData> impexMacroParametersConverter)
	{
		this.impexMacroParametersConverter = impexMacroParametersConverter;
	}


	/**
	 * @return the addonConfigDataImportService
	 */
	public AddOnConfigDataImportService getAddonConfigDataImportService()
	{
		return addonConfigDataImportService;
	}


	/**
	 * @param addonConfigDataImportService
	 *           the addonConfigDataImportService to set
	 */
	@Required
	public void setAddonConfigDataImportService(final AddOnConfigDataImportService addonConfigDataImportService)
	{
		this.addonConfigDataImportService = addonConfigDataImportService;
	}

	/**
	 * @return the siteSpecificImpexMacroParametersConverter
	 */
	public PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> getSelectedBaseSiteImpexMacroParametersPopulators()
	{
		return selectedBaseSiteImpexMacroParametersPopulators;
	}

	/**
	 * @param selectedBaseSiteImpexMacroParametersPopulators
	 *           the selectedBaseSiteImpexMacroParametersPopulators to set
	 */
	@Required
	public void setSelectedBaseSiteImpexMacroParametersPopulators(
			final PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> selectedBaseSiteImpexMacroParametersPopulators)
	{
		this.selectedBaseSiteImpexMacroParametersPopulators = selectedBaseSiteImpexMacroParametersPopulators;
	}

	/**
	 * @return the siteSpecificImpexMacroParametersConverter
	 */
	public PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> getSelectedContentCatalogImpexMacroParametersPopulators()
	{
		return selectedContentCatalogImpexMacroParametersPopulators;
	}

	@Required
	public void setSelectedContentCatalogImpexMacroParametersPopulators(
			final PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> selectedContentCatalogImpexMacroParametersPopulators)
	{
		this.selectedContentCatalogImpexMacroParametersPopulators = selectedContentCatalogImpexMacroParametersPopulators;
	}

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the productCatalogImpexMacroParametersConverter
	 */
	public PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> getSelectedProductCatalogImpexMacroParametersPopulators()
	{
		return selectedProductCatalogImpexMacroParametersPopulators;
	}

	/**
	 * @param selectedProductCatalogImpexMacroParametersPopulators
	 *           the selectedProductCatalogImpexMacroParametersPopulators to set
	 */
	@Required
	public void setSelectedProductCatalogImpexMacroParametersPopulators(
			final PopulatorList<AddOnDataImportEventContext, ImpexMacroParameterData> selectedProductCatalogImpexMacroParametersPopulators)
	{
		this.selectedProductCatalogImpexMacroParametersPopulators = selectedProductCatalogImpexMacroParametersPopulators;
	}

	/**
	 * @return the catalogService
	 */
	public CatalogService getCatalogService()
	{
		return catalogService;
	}

	/**
	 * @param catalogService
	 *           the catalogService to set
	 */
	@Required
	public void setCatalogService(final CatalogService catalogService)
	{
		this.catalogService = catalogService;
	}


	/**
	 * @return the supportedChannels
	 */
	public List<SiteChannel> getSupportedChannels()
	{
		return supportedChannels;
	}

	/**
	 * @param supportedChannels
	 *           the supportedChannels to set
	 */
	public void setSupportedChannels(final List<SiteChannel> supportedChannels)
	{
		this.supportedChannels = supportedChannels;
	}

	/**
	 * @return the addonSystemSetupSupport
	 */
	public AddOnSystemSetupSupport getAddonSystemSetupSupport()
	{
		return addonSystemSetupSupport;
	}

	/**
	 * @param addonSystemSetupSupport
	 *           the addonSystemSetupSupport to set
	 */
	@Required
	public void setAddonSystemSetupSupport(final AddOnSystemSetupSupport addonSystemSetupSupport)
	{
		this.addonSystemSetupSupport = addonSystemSetupSupport;
	}

	/**
	 * @return the addonExtensionMetadata
	 */
	public AddonExtensionMetadata getAddonExtensionMetadata()
	{
		return addonExtensionMetadata;
	}

	/**
	 * @param addonExtensionMetadata
	 *           the addonExtensionMetadata to set
	 */
	@Required
	public void setAddonExtensionMetadata(final AddonExtensionMetadata addonExtensionMetadata)
	{
		this.addonExtensionMetadata = addonExtensionMetadata;
	}

}
