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

import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;
import de.hybris.platform.commerceservices.setup.impl.DefaultSetupImpexService;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


public class DefaultSetupImpexAddOnService extends DefaultSetupImpexService
{
	private static final Logger LOG = Logger.getLogger(DefaultSetupImpexAddOnService.class);



	@Override
	public boolean importImpexFile(final String file, final Map<String, Object> macroParameters, final boolean errorIfMissing)
	{
		return importImpexFile(file, macroParameters, errorIfMissing, false);
	}

	@Override
	public boolean importImpexFile(final String file, final Map<String, Object> macroParameters, final boolean errorIfMissing,
			final boolean legacyMode)
	{


		final InputStream resourceAsStream = getClass().getResourceAsStream(file);
		if (resourceAsStream == null)
		{
			if (errorIfMissing)
			{
				LOG.error("Importing [" + file + "]... ERROR (MISSING FILE)", null);
			}
			else
			{
				LOG.info("Importing [" + file + "]... SKIPPED (Optional File Not Found)");
			}
			return false;
		}
		else
		{

			importImpexFile(file, getMergedInputStream(macroParameters, resourceAsStream), legacyMode);

			// Try to import language specific impex files
			if (file.endsWith(getImpexExt()))
			{
				final String filePath = file.substring(0, file.length() - getImpexExt().length());

				final List<LanguageModel> languages = getCommonI18NService().getAllLanguages();
				for (final LanguageModel language : languages)
				{
					final String languageFilePath = filePath + "_" + language.getIsocode() + getImpexExt();
					final InputStream languageResourceAsStream = getClass().getResourceAsStream(languageFilePath);
					if (languageResourceAsStream != null)
					{
						importImpexFile(languageFilePath, getMergedInputStream(macroParameters, languageResourceAsStream), legacyMode);
					}
				}
			}
			return true;
		}
	}

	protected String buildMacroHeader(final Map<String, Object> macroParameters)
	{
		// no pun intended with this method name
		final StringBuilder builder = new StringBuilder();
		for (final Entry<String, Object> entry : macroParameters.entrySet())
		{
			final String macroName;
			if (entry.getKey().charAt(0) == '$')
			{
				macroName = entry.getKey();
			}
			else
			{
				macroName = '$' + entry.getKey();
			}

			final Object val = entry.getValue();
			builder.append(macroName).append("=").append(val == null ? "" : String.valueOf(val)).append("\n");
		}
		return builder.toString();
	}

	protected InputStream getMergedInputStream(final Map<String, Object> macroParameters, final InputStream fileStream)
	{

		if (macroParameters != null && !macroParameters.isEmpty())
		{
			final String header = buildMacroHeader(macroParameters);
			return new SequenceInputStream(IOUtils.toInputStream(header), fileStream);
		}
		else
		{
			return fileStream;
		}
	}

	@Override
	public boolean importImpexFile(final String file, final ImpexMacroParameterData macroParameters, final boolean errorIfMissing,
			final boolean legacyMode)
	{

		final Map val;
		try
		{
			val = BeanUtils.describe(macroParameters);
			val.remove("additionalParameterMap");
		}
		catch (final Exception e)
		{
			throw new IllegalArgumentException("failed to introspect macroparameters", e);
		}
		final Map mergedMap = new HashMap<String, String>();
		mergedMap.putAll(val);
		if (macroParameters.getAdditionalParameterMap() != null)
		{
			mergedMap.putAll(macroParameters.getAdditionalParameterMap());
		}
		return importImpexFile(file, mergedMap, errorIfMissing, legacyMode);

	}

	@Override
	public boolean importImpexFile(final String file, final ImpexMacroParameterData macroParameters, final boolean errorIfMissing)
	{
		return importImpexFile(file, macroParameters, errorIfMissing, false);

	}


}
