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
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link ProductGalleryImagesPopulator}
 */
@UnitTest
public class ProductGalleryImagesPopulatorTest
{
	private static final String MEDIA_FORMAT_1 = "thumb";
	private static final String MEDIA_FORMAT_QUALIFIER_1 = "96x96";
	private static final String MEDIA_FORMAT_2 = "zoom";
	private static final String MEDIA_FORMAT_QUALIFIER_2 = "545x545";

	@Mock
	private MediaService mediaService;
	@Mock
	private MediaContainerService mediaContainerService;
	@Mock
	private ImageFormatMapping imageFormatMapping;
	@Mock
	private Converter<MediaModel, ImageData> imageConverter;
	@Mock
	private ModelService modelService;

	private List<String> imageFormats;
	private ProductGalleryImagesPopulator productGalleryImagesPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		imageFormats = new ArrayList<String>();
		imageFormats.add(MEDIA_FORMAT_1);
		imageFormats.add(MEDIA_FORMAT_2);
		productGalleryImagesPopulator = new ProductGalleryImagesPopulator();
		productGalleryImagesPopulator.setModelService(modelService);
		productGalleryImagesPopulator.setImageConverter(imageConverter);
		productGalleryImagesPopulator.setImageFormatMapping(imageFormatMapping);
		productGalleryImagesPopulator.setImageFormats(imageFormats);
		productGalleryImagesPopulator.setMediaContainerService(mediaContainerService);
		productGalleryImagesPopulator.setMediaService(mediaService);
	}


	@Test
	public void testPopulate()
	{
		final ProductModel source = mock(ProductModel.class);
		final MediaContainerModel mediaContainerModel = mock(MediaContainerModel.class);
		final MediaFormatModel thumbFormat = mock(MediaFormatModel.class);
		final MediaFormatModel zoomFormat = mock(MediaFormatModel.class);
		final MediaModel thumbMedia = mock(MediaModel.class);
		final MediaModel zoomMedia = mock(MediaModel.class);
		final ImageData thumbImage = mock(ImageData.class);
		final ImageData zoomImage = mock(ImageData.class);
		final ImageData existingImage = mock(ImageData.class);

		given(mediaService.getFormat(MEDIA_FORMAT_QUALIFIER_1)).willReturn(thumbFormat);
		given(mediaService.getFormat(MEDIA_FORMAT_QUALIFIER_2)).willReturn(zoomFormat);
		given(mediaContainerService.getMediaForFormat(mediaContainerModel, thumbFormat)).willReturn(thumbMedia);
		given(mediaContainerService.getMediaForFormat(mediaContainerModel, zoomFormat)).willReturn(zoomMedia);
		given(imageConverter.convert(thumbMedia)).willReturn(thumbImage);
		given(imageConverter.convert(zoomMedia)).willReturn(zoomImage);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(MEDIA_FORMAT_1)).willReturn(MEDIA_FORMAT_QUALIFIER_1);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(MEDIA_FORMAT_2)).willReturn(MEDIA_FORMAT_QUALIFIER_2);
		given(modelService.getAttributeValue(source, ProductModel.GALLERYIMAGES)).willReturn(
				Collections.singletonList(mediaContainerModel));

		final ProductData result = new ProductData();
		final List<ImageData> images = new ArrayList<ImageData>();
		images.add(existingImage);
		result.setImages(images);
		productGalleryImagesPopulator.populate(source, result);

		Assert.assertEquals(3, result.getImages().size());
		Assert.assertTrue(result.getImages().contains(thumbImage));
		Assert.assertTrue(result.getImages().contains(zoomImage));
	}


	@Test
	public void testPopulateVariantProduct()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);
		final MediaContainerModel mediaContainerModel = mock(MediaContainerModel.class);
		final MediaFormatModel thumbFormat = mock(MediaFormatModel.class);
		final MediaFormatModel zoomFormat = mock(MediaFormatModel.class);
		final MediaModel thumbMedia = mock(MediaModel.class);
		final MediaModel zoomMedia = mock(MediaModel.class);
		final ImageData thumbImage = mock(ImageData.class);
		final ImageData zoomImage = mock(ImageData.class);
		final ImageData existingImage = mock(ImageData.class);

		given(source.getBaseProduct()).willReturn(baseProduct);
		given(mediaService.getFormat(MEDIA_FORMAT_QUALIFIER_1)).willReturn(thumbFormat);
		given(mediaService.getFormat(MEDIA_FORMAT_QUALIFIER_2)).willReturn(zoomFormat);
		given(mediaContainerService.getMediaForFormat(mediaContainerModel, thumbFormat)).willReturn(thumbMedia);
		given(mediaContainerService.getMediaForFormat(mediaContainerModel, zoomFormat)).willReturn(zoomMedia);
		given(imageConverter.convert(thumbMedia)).willReturn(thumbImage);
		given(imageConverter.convert(zoomMedia)).willReturn(zoomImage);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(MEDIA_FORMAT_1)).willReturn(MEDIA_FORMAT_QUALIFIER_1);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(MEDIA_FORMAT_2)).willReturn(MEDIA_FORMAT_QUALIFIER_2);
		given(modelService.getAttributeValue(source, ProductModel.GALLERYIMAGES)).willReturn(Collections.emptyList());
		given(modelService.getAttributeValue(baseProduct, ProductModel.GALLERYIMAGES)).willReturn(
				Collections.singletonList(mediaContainerModel));

		final ProductData result = new ProductData();
		final List<ImageData> images = new ArrayList<ImageData>();
		images.add(existingImage);
		result.setImages(images);
		productGalleryImagesPopulator.populate(source, result);

		Assert.assertEquals(3, result.getImages().size());
		Assert.assertTrue(result.getImages().contains(thumbImage));
		Assert.assertTrue(result.getImages().contains(zoomImage));
	}

}
