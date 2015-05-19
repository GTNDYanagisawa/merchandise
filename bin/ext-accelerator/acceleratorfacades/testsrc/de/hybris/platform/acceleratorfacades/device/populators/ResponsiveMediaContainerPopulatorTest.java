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
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@UnitTest
public class ResponsiveMediaContainerPopulatorTest
{
	final ResponsiveMediaContainerPopulator responsiveMediaContainerPopulator = new ResponsiveMediaContainerPopulator();

	@Mock
	private Converter<MediaModel, ImageData> imageConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		responsiveMediaContainerPopulator.setImageConverter(imageConverter);
	}

	@Test
	public void testPopulate()
	{
		final MediaContainerModel mediaContainerModel = mock(MediaContainerModel.class);
		final MediaModel mediaModel1 = mock(MediaModel.class);
		given(mediaModel1.getCode()).willReturn("test-200W.jpg");
		final MediaModel mediaModel2 = mock(MediaModel.class);
		given(mediaModel2.getCode()).willReturn("test-400W.jpg");
		final MediaModel mediaModel3 = mock(MediaModel.class);
		given(mediaModel3.getCode()).willReturn("test-600W.jpg");
		final MediaModel mediaModel4 = mock(MediaModel.class);
		given(mediaModel4.getCode()).willReturn("test-800W.jpg");


		final Collection<MediaModel> mediaCollection = Arrays.asList(mediaModel1, mediaModel2, mediaModel3, mediaModel4);
		given(mediaContainerModel.getMedias()).willReturn(mediaCollection);

		final ImageData imageData1 = mock(ImageData.class);
		given(imageData1.getWidth()).willReturn(Integer.valueOf(200));
		final ImageData imageData2 = mock(ImageData.class);
		given(imageData2.getWidth()).willReturn(Integer.valueOf(400));
		final ImageData imageData3 = mock(ImageData.class);
		given(imageData3.getWidth()).willReturn(Integer.valueOf(600));
		final ImageData imageData4 = mock(ImageData.class);
		given(imageData4.getWidth()).willReturn(Integer.valueOf(800));
		final List<ImageData> imageDataList = new ArrayList<ImageData>();

		given(imageConverter.convert(mediaModel1)).willReturn(imageData1);
		given(imageConverter.convert(mediaModel2)).willReturn(imageData2);
		given(imageConverter.convert(mediaModel3)).willReturn(imageData3);
		given(imageConverter.convert(mediaModel4)).willReturn(imageData4);

		responsiveMediaContainerPopulator.populate(mediaContainerModel, imageDataList);

		Assert.assertTrue(!imageDataList.isEmpty());
		Assert.assertEquals(4, imageDataList.size());
		Assert.assertEquals(imageData1, imageDataList.get(0));
		Assert.assertEquals(imageData2, imageDataList.get(1));
		Assert.assertEquals(imageData3, imageDataList.get(2));
		Assert.assertEquals(imageData4, imageDataList.get(3));
	}


	@Test
	public void testForNullPopulate()
	{
		final MediaContainerModel mediaContainerModel = mock(MediaContainerModel.class);
		final List<ImageData> imageDataList = new ArrayList<ImageData>();
		responsiveMediaContainerPopulator.populate(mediaContainerModel, imageDataList);
		Assert.assertTrue(imageDataList.isEmpty());
	}


}
