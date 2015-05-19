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

import de.hybris.platform.addonsupport.impex.AddonConfigDataImportType;
import de.hybris.platform.addonsupport.setup.AddOnConfigDataImportService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultAddOnConfigDataImportService implements AddOnConfigDataImportService
{
	private SetupImpexService setupImpexService;

	/**
	 * @return the setupImpexService
	 */
	public SetupImpexService getSetupImpexService()
	{
		return setupImpexService;
	}

	/**
	 * @param setupImpexService
	 *           the setupImpexService to set
	 */
	@Required
	public void setSetupImpexService(final SetupImpexService setupImpexService)
	{
		this.setupImpexService = setupImpexService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hybris.addon.support.setup.AddonProjectDataImportService#executeImport(java.lang.String,
	 * com.hybris.addon.support.impex.AddonProjectDataImportType, com.hybris.addon.support.impex.ImpexMacroParameterData,
	 * boolean, boolean)
	 */
	@Override
	public boolean executeImport(final String extensionName, final AddonConfigDataImportType importType,
			final ImpexMacroParameterData macroParameters)
	{
		if (AddonConfigDataImportType.CONTENT.equals(importType))
		{
			return executeCMSImport(extensionName, macroParameters);
		}
		else if (AddonConfigDataImportType.PRODUCT.equals(importType))
		{
			return executeProductImport(extensionName, macroParameters);
		}
		else if (AddonConfigDataImportType.SOLR.equals(importType))
		{
			return executeSOLRImport(extensionName, macroParameters);
		}
		else if (AddonConfigDataImportType.STORE.equals(importType))
		{
			return executeStoreImport(extensionName, macroParameters);
		}
		return false;
	}

	protected boolean executeProductImport(final String extensionName, final ImpexMacroParameterData macroParameters)
	{
		final String path = "/" + extensionName + "/import/productCatalogs/template/";
		return getSetupImpexService().importImpexFile(path + "catalog.impex", macroParameters, false, false);
	}

	protected boolean executeCMSImport(final String extensionName, final ImpexMacroParameterData macroParameters)
	{
		final String path = "/" + extensionName + "/import/contentCatalogs/template/";
		boolean imported = false;
		imported |= getSetupImpexService().importImpexFile(path + "catalog.impex", macroParameters, false, false);

		if (macroParameters.getSupportedUiExperienceLevels() != null)
		{
			for (final UiExperienceLevel level : macroParameters.getSupportedUiExperienceLevels())
			{
				String suffix = StringUtils.EMPTY;
				if (!level.equals(UiExperienceLevel.DESKTOP))
				{
					suffix = "-" + StringUtils.lowerCase(level.getCode());
				}

				imported |= getSetupImpexService().importImpexFile(path + "cms-content" + suffix + ".impex", macroParameters, false,
						false);
			}
		}

		imported |= getSetupImpexService().importImpexFile(path + "email-content.impex", macroParameters, false, false);

		return imported;
	}

	protected boolean executeSOLRImport(final String extensionName, final ImpexMacroParameterData macroParameters)
	{
		final String path = "/" + extensionName + "/import/solr/template/";
		boolean imported = false;
		imported |= getSetupImpexService().importImpexFile(path + "solr.impex", macroParameters, false, false);
		imported |= getSetupImpexService().importImpexFile(path + "solrtrigger.impex", macroParameters, false, false);
		return imported;
	}

	protected boolean executeStoreImport(final String extensionName, final ImpexMacroParameterData macroParameters)
	{
		final String path = "/" + extensionName + "/import/stores/template/";
		boolean imported = false;
		imported |= getSetupImpexService().importImpexFile(path + "store.impex", macroParameters, false, false);
		imported |= getSetupImpexService().importImpexFile(path + "site.impex", macroParameters, false, false);
		return imported;
	}

}
