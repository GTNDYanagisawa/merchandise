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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.enums.ConversionStatus;
import de.hybris.platform.mediaconversion.model.ConversionGroupModel;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Assert;

import org.junit.Test;


/**
 * @author pohl
 */
@IntegrationTest
public class MediaConversionJobIntegrationTest extends AbstractMediaConversionJobIntegrationTest
{

	@Test
	public void testStandardJobRun()
	{
		// all container have a master but no converted medias:
		this.checkAllEmpty(containers);
		// run the job:
		this.cronJobService.performCronJob(generate, true);
		// now all container have everything converted
		this.checkConverted(containers, this.formats, ConversionStatus.CONVERTED);
		// clean up again:
		this.cronJobService.performCronJob(clean, true);
		this.checkAllEmpty(containers);
	}

	@Test
	public void testRestrictedJobRun()
	{
		{
			// let's restrict to one format
			generate.setIncludedFormats(Arrays.asList(this.flip));
			this.modelService.save(generate);
			// run the job:
			this.cronJobService.performCronJob(generate, true);
			// only flip is converted
			this.checkConverted(containers, generate.getIncludedFormats(), ConversionStatus.PARTIALLY_CONVERTED);
		}

		{
			// let's add a format that has an input format
			generate.setIncludedFormats(Arrays.asList(this.flip, this.channel));
			this.modelService.save(generate);
			// run the job:
			this.cronJobService.performCronJob(generate, true);
			// only flip is converted
			this.checkConverted(containers, Arrays.asList(this.flip, this.channel, this.normal),
					ConversionStatus.PARTIALLY_CONVERTED);

			// reset cronjob
			generate.setIncludedFormats(null);
			this.modelService.save(generate);
		}

		{
			// clean up wrong catalogversion
			clean.setCatalogVersion(TestDataFactory.someCatalogVersion(this.modelService));
			this.modelService.save(clean);
			this.cronJobService.performCronJob(clean, true);
			// same as above
			this.checkConverted(containers, Arrays.asList(this.flip, this.channel, this.normal),
					ConversionStatus.PARTIALLY_CONVERTED);

			// clean up one format
			clean.setCatalogVersion(null);
			clean.setIncludedFormats(Arrays.asList(this.channel));
			this.modelService.save(clean);
			this.cronJobService.performCronJob(clean, true);
			// channel is gone
			this.checkConverted(containers, Arrays.asList(this.flip, this.normal), ConversionStatus.PARTIALLY_CONVERTED);

			// clean them all
			clean.setIncludedFormats(null);
			this.modelService.save(clean);
			this.cronJobService.performCronJob(clean, true);
			this.checkAllEmpty(containers);
		}
	}

	@Test
	public void testConversionGroupJobRun()
	{
		final PK groupPK;
		{
			// let's add a ConversionGroup:
			final ConversionGroupModel group = this.modelService.create(ConversionGroupModel.class);
			group.setCode(this.getClass().getSimpleName());
			group.setName("Test group");
			group.setSupportedFormats(new HashSet<ConversionMediaFormatModel>(Arrays.asList(this.normal, this.tunnel, this.flip)));
			this.modelService.save(group);
			groupPK = group.getPk();
			for (final MediaContainerModel con : containers)
			{
				con.setConversionGroup(group);
				this.modelService.save(con);
			}
		}

		// run the job:
		this.cronJobService.performCronJob(generate, true);

		{
			final ConversionGroupModel groupFromCtx = this.modelService.get(groupPK);
			final List<MediaContainerModel> containersFromCtx = new ArrayList<MediaContainerModel>(containers);
			for (final ListIterator<MediaContainerModel> lit = containersFromCtx.listIterator(); lit.hasNext();)
			{
				lit.set(modelService.<MediaContainerModel> get(lit.next().getPk()));
			}

			// now all container have everything converted
			this.checkConverted(containersFromCtx, groupFromCtx.getSupportedFormats(), ConversionStatus.CONVERTED);

			// add a format to the group (with input) and remove normal
			groupFromCtx.setSupportedFormats(new HashSet<ConversionMediaFormatModel>(Arrays.asList(this.channel, this.tunnel,
					this.flip)));
			this.modelService.save(groupFromCtx);
			// now they are only partially converted
			this.checkConverted(containersFromCtx, Arrays.asList(this.normal, this.tunnel, this.flip),
					ConversionStatus.PARTIALLY_CONVERTED);
		}
	}

	private void checkConverted(final Collection<MediaContainerModel> containers,
			final Collection<ConversionMediaFormatModel> expectedFormats, final ConversionStatus status)
	{
		for (final MediaContainerModel con : containers)
		{
			Assert.assertNotNull("Master set on '" + con + "'.", con.getMaster());
			Assert.assertEquals("Master of '" + con + "' has correct catalogVersion.", con.getCatalogVersion(), con.getMaster()
					.getCatalogVersion());
			final Collection<MediaModel> converted = this.mediaConversionService.getConvertedMedias(con);
			Assert.assertNotNull("Converted medias of '" + con + "' are not null.", converted);
			Assert.assertFalse("Converted medias of '" + con + "' are not empty.", converted.isEmpty());
			Assert.assertFalse("Converted medias of '" + con + "' does not contain master.", converted.contains(con.getMaster()));
			Assert.assertEquals("Amount Converted medias of '" + con + "'.", expectedFormats.size(), converted.size());
			for (final MediaModel memo : converted)
			{
				Assert.assertEquals("Same catalogVersion", con.getCatalogVersion(), memo.getCatalogVersion());
				Assert.assertNotNull("original set", memo.getOriginal());
				Assert.assertNotNull("original data pk set", memo.getOriginalDataPK());
				Assert.assertTrue(
						"format '" + memo.getMediaFormat() + "' is in formats " + Arrays.toString(expectedFormats.toArray()),
						expectedFormats.contains(memo.getMediaFormat()));
			}

			Assert.assertEquals("Conversion status of '" + con + "' is '" + status + "'.", status, con.getConversionStatus());
		}
	}

	/**
	 * @param containers
	 */
	private void checkAllEmpty(final Collection<MediaContainerModel> containers)
	{
		for (final MediaContainerModel con : containers)
		{
			Assert.assertNotNull("Master set on '" + con + "'.", con.getMaster());
			Assert.assertEquals("Master of '" + con + "' has correct catalogVersion.", con.getCatalogVersion(), con.getMaster()
					.getCatalogVersion());
			final Collection<MediaModel> converted = this.mediaConversionService.getConvertedMedias(con);
			Assert.assertNotNull("Converted medias of '" + con + "' are not null.", converted);
			Assert.assertTrue("Converted medias of '" + con + "' are empty.", converted.isEmpty());
			Assert.assertEquals("Conversion status of '" + con + "' is unconverted.", ConversionStatus.UNCONVERTED,
					con.getConversionStatus());
		}
	}
}
