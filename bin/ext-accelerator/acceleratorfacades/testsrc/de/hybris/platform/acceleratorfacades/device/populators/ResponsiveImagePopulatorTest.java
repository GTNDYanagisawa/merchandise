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
package de.hybris.platform.acceleratorfacades.device.populators;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ResponsiveImagePopulatorTest
{
	final ResponsiveImagePopulator responsiveImagePopulator = new ResponsiveImagePopulator();

	private final Map<String, Integer> responsiveImageFormats = new HashMap<>();


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		responsiveImageFormats.put("desktop", Integer.valueOf(600));
		responsiveImagePopulator.setResponsiveImageFormats(responsiveImageFormats);
	}


	@Test
	public void testForRegex()
	{
		final MediaModel mediaModel1 = new MediaModel();
		mediaModel1.setCode("Elec_800x320_HomeSpeed_EN_01-480W.jpg");
		final MediaFormatModel mediaFormatModel = new MediaFormatModel();
		mediaFormatModel.setQualifier("desktop");
		mediaModel1.setMediaFormat(mediaFormatModel);
		final ImageData imageData1 = new ImageData();

		responsiveImagePopulator.populate(mediaModel1, imageData1);

		Assert.assertEquals(800, imageData1.getWidth().intValue());
	}

	@Test
	public void testForImproperRegex()
	{
		final MediaModel mediaModel1 = new MediaModel();
		mediaModel1.setCode("test_800Z_landing.jpg");
		final MediaFormatModel mediaFormatModel = new MediaFormatModel();
		mediaFormatModel.setQualifier("desktop");
		mediaModel1.setMediaFormat(mediaFormatModel);
		final ImageData imageData1 = new ImageData();
		responsiveImagePopulator.populate(mediaModel1, imageData1);

		Assert.assertEquals(600, imageData1.getWidth().intValue());
	}

}
