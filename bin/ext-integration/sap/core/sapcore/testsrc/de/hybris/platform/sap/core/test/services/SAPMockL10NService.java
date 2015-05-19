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
package de.hybris.platform.sap.core.test.services;

import de.hybris.platform.sap.core.configuration.GlobalConfigurationManager;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.i18n.impl.CompositeResourceBundle;
import de.hybris.platform.servicelayer.i18n.impl.DefaultL10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.localization.TypeLocalization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Mock implementation for {@link L10NService}.
 */
public class SAPMockL10NService extends DefaultL10NService
{

	private static final Logger LOG = Logger.getLogger(SAPMockL10NService.class);

	private static final String DEFAULT_RESOURCE_BUNDLE_ENCODING = "UTF-8";

	private String localizationFolder = "localization";

	private List<String> languages = new ArrayList<String>();

	/**
	 * Global configuration manager.
	 */
	private GlobalConfigurationManager globalConfigurationManager;

	/**
	 * Injection setter for the global configuration manager.
	 * 
	 * @param globalConfigurationManager
	 *           global configuration manager
	 */
	public void setGlobalConfigurationManager(final GlobalConfigurationManager globalConfigurationManager)
	{
		this.globalConfigurationManager = globalConfigurationManager;
	}

	/**
	 * Injection setter for localization folder.
	 * 
	 * @param localizationFolder
	 *           localization folder
	 */
	public void setLocalizationFolder(final String localizationFolder)
	{
		this.localizationFolder = localizationFolder;
	}

	/**
	 * Injection setter for localization folder.
	 * 
	 * @param languages
	 *           list of languages
	 */
	public void setLanguages(final List<String> languages)
	{
		this.languages = languages;
	}

	@Override
	public ResourceBundle getResourceBundle()
	{
		return getResourceBundle(new Locale[]
		{ Locale.ENGLISH });
	}

	/**
	 * Returns a map of localization properties for all set languages.
	 * 
	 * @return map of localization properties per language
	 */
	protected Map<String, Properties> getLocalizationsInternal()
	{
		final Map<String, Properties> isocodesLocalizations = new HashMap<String, Properties>();
		for (final String language : languages)
		{
			isocodesLocalizations.put(language, loadLocalizations(language));
		}
		return isocodesLocalizations;
	}

	@Override
	public ResourceBundle getResourceBundle(final Locale[] locales)
	{
		List<ResourceBundle> bundles = null;
		final Map<String, Properties> localizations = getLocalizationsInternal();
		for (final Locale locale : locales)
		{
			final Properties properties = localizations.get(locale.toString());
			if (properties != null)
			{
				//to be sure that the properties intended for one language will not occur twice
				localizations.remove(locale);
				final ResourceBundle bundle = createBundle(properties);
				if (bundles == null)
				{
					bundles = new ArrayList<ResourceBundle>();
				}
				bundles.add(bundle);
			}
		}

		return CompositeResourceBundle.getBundle(bundles);
	}

	@Override
	public String getLocalizedString(final String resKey, final Object[] arguments)
	{
		final String value = getLocalizedString(resKey);
		if (value != null /* DEL-145: && !value.equals(key) */&& arguments != null && !(arguments.length == 0))
		{
			final Locale locale = Locale.ENGLISH;
			final MessageFormat messageFormat = new MessageFormat(value, locale);
			return messageFormat.format(arguments, new StringBuffer(), null).toString();
		}
		return value;
	}

	@Override
	public ResourceBundle getResourceBundle(final String baseName)
	{
		return getResourceBundle(baseName, new Locale[]
		{ Locale.ENGLISH });
	}

	@Override
	public ResourceBundle getResourceBundle(final String baseName, final Locale[] locales)
	{
		return super.getResourceBundle(baseName, locales);
	}

	@Override
	public void setI18nService(final I18NService i18nService)
	{
	}

	@Override
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
	}

	@Override
	public void setModelService(final ModelService modelService)
	{
	}

	/**
	 * Creates a {@link ResourceBundle} for the given properties.
	 * 
	 * @param properties
	 *           localization properties
	 * @return {@link ResourceBundle}
	 */
	private ResourceBundle createBundle(final Properties properties)
	{
		ResourceBundle bundle = null;

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			final String comments = null;
			properties.store(out, comments);
			bundle = new PropertyResourceBundle(new ByteArrayInputStream(out.toByteArray()));
		}
		catch (final IOException ex)
		{
			// cannot happen
			throw new SystemException("Error when creating resource bundle!", ex);
		}

		return bundle;
	}

	/**
	 * Loads localization properties for a given language.
	 * 
	 * @param lang
	 *           language
	 * @return localization properties
	 */
	private Properties loadLocalizations(final String lang)
	{
		final Properties props = new Properties();
		final Set<String> moduleIds = globalConfigurationManager.getModuleIds();
		final Set<String> extensionNames = new HashSet<String>();
		for (final String moduleId : moduleIds)
		{
			extensionNames.addAll(globalConfigurationManager.getExtensionNames(moduleId));
		}
		final LinkedList<String> c = new LinkedList<String>(extensionNames);

		for (final String extName : c)
		{
			final String locFileName = "/" + localizationFolder + "/" + extName + "-locales_" + lang.toLowerCase() + ".properties";
			try
			{
				final InputStream fis = TypeLocalization.class.getResourceAsStream(locFileName);
				if (fis != null)
				{
					props.load(new InputStreamReader(fis, DEFAULT_RESOURCE_BUNDLE_ENCODING));
					fis.close();
				}
				else
				{
					LOG.debug("No type localization (" + lang.toLowerCase() + ") found for extension " + extName);
				}
			}

			catch (final IOException e)
			{
				e.printStackTrace(); //NOPMD
			}
		}

		final Properties convertedProps = new Properties();
		for (final Enumeration<?> keys = props.keys(); keys.hasMoreElements();)
		{
			final String key = (String) keys.nextElement();
			if (key != null)
			{
				convertedProps.setProperty(key.toLowerCase(), props.getProperty(key));
			}
		}
		return convertedProps;
	}

}
