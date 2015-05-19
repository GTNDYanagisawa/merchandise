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
package de.hybris.platform.acceleratorservices.cronjob;

import de.hybris.platform.acceleratorservices.enums.SiteMapPageEnum;
import de.hybris.platform.acceleratorservices.model.SiteMapConfigModel;
import de.hybris.platform.acceleratorservices.model.SiteMapLanguageCurrencyModel;
import de.hybris.platform.acceleratorservices.model.SiteMapMediaCronJobModel;
import de.hybris.platform.acceleratorservices.model.SiteMapPageModel;
import de.hybris.platform.acceleratorservices.sitemap.generator.SiteMapGenerator;
import de.hybris.platform.basecommerce.strategies.ActivateBaseSiteInSessionStrategy;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A Cronjob that generates the sitemap media for a given cms site
 */
public class SiteMapMediaJob extends AbstractJobPerformable<SiteMapMediaCronJobModel>
{
	private static final String SITE_MAP_MIME_TYPE = "text/plain";
	private static final Logger LOG = Logger.getLogger(SiteMapMediaJob.class);
	private List<SiteMapGenerator> generators;
	private MediaService mediaService;
	private CMSSiteService cmsSiteService;
	private ActivateBaseSiteInSessionStrategy<CMSSiteModel> activateBaseSiteInSession;

	@Override
	public PerformResult perform(final SiteMapMediaCronJobModel cronJob)
	{

		final List<MediaModel> siteMapMedias = new ArrayList<MediaModel>();
		final CMSSiteModel contentSite = cronJob.getContentSite();

		getCmsSiteService().setCurrentSite(contentSite);
		// set the catalog version for the current session
		getActivateBaseSiteInSession().activate(contentSite);

		final SiteMapConfigModel siteMapConfig = contentSite.getSiteMapConfig();
		final Collection<SiteMapPageModel> siteMapPages = siteMapConfig.getSiteMapPages();
		for (final SiteMapPageModel siteMapPage : siteMapPages)
		{
			final List<File> siteMapFiles = new ArrayList<File>();
			final SiteMapPageEnum pageType = siteMapPage.getCode();
			final SiteMapGenerator generator = this.getGeneratorForSiteMapPage(pageType);

			if (BooleanUtils.isTrue(siteMapPage.getActive()) && generator != null)
			{
				final List models = generator.getData(contentSite);
				final Integer MAX_SITEMAP_LIMIT = cronJob.getSiteMapUrlLimitPerFile();
				if (models.size() > MAX_SITEMAP_LIMIT.intValue())
				{
					final List<List> modelsList = splitUpTheListIfExceededLimit(models, MAX_SITEMAP_LIMIT);
					for (int modelIndex = 0; modelIndex < modelsList.size(); modelIndex++)
					{
						generateSiteMapFiles(siteMapFiles, contentSite, generator, siteMapConfig, modelsList.get(modelIndex), pageType,
								Integer.valueOf(modelIndex));
					}
				}
				else
				{
					generateSiteMapFiles(siteMapFiles, contentSite, generator, siteMapConfig, models, pageType, null);
				}
			}
			else
			{
				LOG.warn(String.format("Skipping SiteMap page %s active %s", siteMapPage.getCode(), siteMapPage.getActive()));
			}
			if (!siteMapFiles.isEmpty())
			{
				for (final File siteMapFile : siteMapFiles)
				{
					siteMapMedias.add(createCatalogUnawareMediaModel(siteMapFile));
				}
			}
		}

		if (!siteMapMedias.isEmpty())
		{
			final Collection<MediaModel> existingSiteMaps = contentSite.getSiteMaps();

			contentSite.setSiteMaps(siteMapMedias);
			modelService.save(contentSite);

			// clean up old sitemap medias
			if (CollectionUtils.isNotEmpty(existingSiteMaps))
			{
				modelService.removeAll(existingSiteMaps);
			}
		}


		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected CatalogUnawareMediaModel createCatalogUnawareMediaModel(final File siteMapFile)
	{
		final CatalogUnawareMediaModel media = modelService.create(CatalogUnawareMediaModel.class);
		media.setCode(siteMapFile.getName());
		modelService.save(media);
		try
		{
			getMediaService().setStreamForMedia(media, new FileInputStream(siteMapFile), siteMapFile.getName(), SITE_MAP_MIME_TYPE);
		}
		catch (final FileNotFoundException e)
		{
			LOG.error(e.getMessage());
		}
		return media;
	}

	protected SiteMapGenerator getGeneratorForSiteMapPage(final SiteMapPageEnum siteMapPageEnum)
	{

		return (SiteMapGenerator) CollectionUtils.find(getGenerators(), new Predicate()
		{
			@Override
			public boolean evaluate(final Object o)
			{
				return ((SiteMapGenerator) o).getSiteMapPageEnum().equals(siteMapPageEnum);
			}
		});
	}

	protected List<List> splitUpTheListIfExceededLimit(final List models, final Integer maxSiteMapUrlLimit)
	{
		final int limit = maxSiteMapUrlLimit.intValue();
		final int modelListSize = models.size() / limit;
		final List<List> modelsList = new ArrayList<>(modelListSize);
		for (int i = 0; i <= modelListSize; i++)
		{
			final int subListToLimit = (i == modelListSize) ? ((i * limit) + (models.size() - (i * limit))) : ((i + 1) * limit);
			Collections.addAll(modelsList, models.subList((i * limit), subListToLimit));
		}
		return modelsList;
	}

	protected void generateSiteMapFiles(final List<File> siteMapFiles, final CMSSiteModel contentSite,
			final SiteMapGenerator generator, final SiteMapConfigModel siteMapConfig, final List<List> models,
			final SiteMapPageEnum pageType, final Integer index)
	{
		for (final SiteMapLanguageCurrencyModel siteMapLanguageCurrency : siteMapConfig.getSiteMapLanguageCurrencies())
		{
			try
			{
				siteMapFiles.add(generator.render(contentSite, siteMapLanguageCurrency.getCurrency(),
						siteMapLanguageCurrency.getLanguage(), siteMapConfig.getSiteMapTemplate(), models, pageType.toString(), index));
			}
			catch (final IOException e)
			{
				LOG.error(e.getMessage());
			}
		}
	}

	protected List<SiteMapGenerator> getGenerators()
	{
		return generators;
	}

	@Required
	public void setGenerators(final List<SiteMapGenerator> generators)
	{
		this.generators = generators;
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

	protected ActivateBaseSiteInSessionStrategy<CMSSiteModel> getActivateBaseSiteInSession()
	{
		return activateBaseSiteInSession;
	}

	@Required
	public void setActivateBaseSiteInSession(final ActivateBaseSiteInSessionStrategy<CMSSiteModel> activateBaseSiteInSession)
	{
		this.activateBaseSiteInSession = activateBaseSiteInSession;
	}

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}
}
