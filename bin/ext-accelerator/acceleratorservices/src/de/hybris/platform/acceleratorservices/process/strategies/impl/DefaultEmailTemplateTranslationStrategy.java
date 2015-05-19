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
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import de.hybris.platform.acceleratorservices.process.strategies.EmailTemplateTranslationStrategy;
import de.hybris.platform.acceleratorservices.util.collections.ParameterizedHashMap;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;


/**
 * 
 */
public class DefaultEmailTemplateTranslationStrategy implements EmailTemplateTranslationStrategy
{
	private CommonI18NService commonI18NService;
	private MediaService mediaService;
	private String defaultLanguageIso;

	@Override
	public Map<String, Object> translateMessagesForTemplate(final RendererTemplateModel renderTemplate, String languageIso)
	{
		if (languageIso == null)
		{
			languageIso = defaultLanguageIso;
		}

		//Get the location of the properties file
		final List<String> propertiesRootPaths = getPropertiesRootPath(renderTemplate, languageIso);

		//Load property file into context
		final Map<String, Object> messages = new ParameterizedHashMap<String, Object>();
		for (final String path : propertiesRootPaths)
		{
			//Load property file
			final Map<?, ?> properties = loadPropertyfile(path);

			//Add contents to message map in the context
			for (final Entry entry : properties.entrySet())
			{
				messages.put(String.valueOf(entry.getKey()), entry.getValue());
			}
			//Done
		}
		//Done
		return messages;
	}

	protected List<String> getPropertiesRootPath(final RendererTemplateModel renderTemplate, final String languageIso)
	{
		final MediaModel content = renderTemplate.getContent();
		final List<String> messageSources = new ArrayList<String>();
		if (content != null)
		{
			BufferedReader reader = null;
			try
			{
				reader = new BufferedReader(new InputStreamReader(mediaService.getStreamFromMedia(content), "UTF-8"));
				String line = reader.readLine();
				while (StringUtils.isNotEmpty(line))
				{
					String messageSource = null;

					if (line.trim().startsWith("<"))
					{
						break;
					}
					else if (line.contains("## messageSource="))
					{
						messageSource = StringUtils.substring(line, line.indexOf("## messageSource=") + 17);
					}
					else if (line.contains("##messageSource="))
					{
						messageSource = StringUtils.substring(line, line.indexOf("##messageSource=") + 16);
					}

					if (StringUtils.isNotEmpty(messageSource))
					{
						if (messageSource.contains("$lang"))
						{
							messageSource = messageSource.replace("$lang", languageIso);
						}
						messageSources.add(messageSource);
					}
					line = reader.readLine();
				}
				return messageSources;
			}
			catch (final IOException e)
			{
				throw new RendererException("Problem during rendering", e);
			}
			finally
			{
				IOUtils.closeQuietly(reader);
			}
		}
		return messageSources;
	}

	protected Map loadPropertyfile(final String path)
	{
		final Properties properties = new Properties();
		Reader reader = null;
		try
		{
			final Resource propertyResource = getApplicationContext().getResource(path);
			if (propertyResource != null && (propertyResource.exists()) && (propertyResource.isReadable()))
			{
				reader = new InputStreamReader(new BOMInputStream(propertyResource.getInputStream()), "UTF-8");
				properties.load(reader);
			}
		}
		catch (final IOException e)
		{
			throw new RendererException("Problem during rendering", e);
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}

		return properties;
	}

	protected ApplicationContext getApplicationContext()
	{
		return Registry.getApplicationContext();
	}


	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	/**
	 * Default language which is used if languageIso parameter is null
	 * 
	 * @return default language
	 */
	public String getDefaultLanguageIso()
	{
		return defaultLanguageIso;
	}

	/**
	 * Set default language which is used if languageIso parameter is null
	 * 
	 * @param defaultLanguage
	 */
	public void setDefaultLanguageIso(final String defaultLanguageIso)
	{
		this.defaultLanguageIso = defaultLanguageIso;
	}

}
