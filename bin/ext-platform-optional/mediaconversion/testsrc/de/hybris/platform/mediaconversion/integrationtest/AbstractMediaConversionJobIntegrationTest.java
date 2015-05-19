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
package de.hybris.platform.mediaconversion.integrationtest;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.enums.ConversionStatus;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.mediaconversion.model.job.MediaConversionCronJobModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;



/**
 * @author pohl
 * 
 */
@Ignore
public abstract class AbstractMediaConversionJobIntegrationTest extends ServicelayerBaseTest
{
	private static final Logger LOG = Logger.getLogger(AbstractMediaConversionJobIntegrationTest.class);

	@Resource
	MediaConversionService mediaConversionService;

	@Resource
	MediaService mediaService;

	@Resource
	ModelService modelService;

	@Resource
	CronJobService cronJobService;

	ServicelayerJobModel generateJob;
	ServicelayerJobModel cleanJob;

	CatalogVersionModel catalogVersion;

	// the formats
	ConversionMediaFormatModel normal;
	ConversionMediaFormatModel thumbnail;
	ConversionMediaFormatModel tunnel;
	ConversionMediaFormatModel flip;
	ConversionMediaFormatModel channel;

	// all formats
	Set<ConversionMediaFormatModel> formats;

	// the jobs
	MediaConversionCronJobModel generate;
	MediaConversionCronJobModel clean;

	// the containers:
	List<MediaContainerModel> containers;

	@Before
	public void setup()
	{
		LOG.info("Setting up test data.");
		this.generateJob = this.createJob("mediaConversionJob");
		this.cleanJob = this.createJob("deleteConvertedMediasJob");

		this.catalogVersion = TestDataFactory.someCatalogVersion(this.modelService);

		this.normal = this.createFormat("normal", null, "-resize 500x500 -normalize", null);
		this.thumbnail = this.createFormat("thumbnail", "image/gif", "-resize 135x135", normal);
		this.tunnel = this.createFormat("tunnel", "image/jpeg", "-alpha set -background none -vignette 0x4", null);
		this.flip = this.createFormat("flip", "image/png", "-flip", null);
		this.channel = this.createFormat("channel", null, "-colorspace HSL -channel Hue -separate", normal);

		this.formats = new HashSet<ConversionMediaFormatModel>();
		this.formats.add(this.normal);
		this.formats.add(this.thumbnail);
		this.formats.add(this.tunnel);
		this.formats.add(this.flip);
		this.formats.add(this.channel);


		this.generate = this.modelService.create(MediaConversionCronJobModel.class);
		this.generate.setCode("poppy");
		this.generate.setJob(this.generateJob);
		this.generate.setAsynchronous(Boolean.FALSE);
		this.modelService.save(this.generate);

		this.clean = this.modelService.create(MediaConversionCronJobModel.class);
		this.clean.setCode("puppy");
		this.clean.setJob(this.cleanJob);
		this.clean.setAsynchronous(Boolean.FALSE);
		this.modelService.save(this.clean);

		this.containers = new LinkedList<MediaContainerModel>();
		for (final TestMedia medInf : TestMedia.values())
		{
			final MediaContainerModel newC = this.modelService.create(MediaContainerModel.class);
			newC.setQualifier(medInf.name());
			newC.setCatalogVersion(this.catalogVersion);
			this.modelService.save(newC);
			Assert.assertEquals("Conversion status of '" + newC + "' is empty after creation.", ConversionStatus.EMPTY,
					newC.getConversionStatus());
			medInf.createMedia(this.modelService, this.mediaService, newC);
			containers.add(newC);
		}
		Assert.assertFalse("Container initialized.", containers.isEmpty());
	}

	private ServicelayerJobModel createJob(final String beanId)
	{
		final ServicelayerJobModel ret = this.modelService.create(ServicelayerJobModel.class);
		ret.setActive(Boolean.TRUE);
		ret.setCode(this.getClass().getSimpleName() + "_" + beanId);
		ret.setSpringId(beanId);
		this.modelService.save(ret);
		return ret;
	}

	private ConversionMediaFormatModel createFormat(final String code, final String mime, final String conversion,
			final ConversionMediaFormatModel input)
	{
		final ConversionMediaFormatModel ret = this.modelService.create(ConversionMediaFormatModel.class);
		ret.setQualifier(code);
		ret.setConversion(conversion);
		ret.setMimeType(mime);
		ret.setConversionStrategy("imageMagickMediaConversionStrategy");
		ret.setInputFormat(input);
		this.modelService.save(ret);
		return ret;
	}
}
