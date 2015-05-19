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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


@UnitTest
public class ImageValueProviderTest extends PropertyFieldValueProviderTestBase
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ImageValueProviderTest.class);

	@Mock
	private MediaService mediaService;
	@Mock
	private MediaContainerService mediaContainerService;
	@Mock
	private IndexedProperty indexedProperty;

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return null;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new ImageValueProvider());
		configureBase();

		((ImageValueProvider) getPropertyFieldValueProvider()).setMediaService(mediaService);
		((ImageValueProvider) getPropertyFieldValueProvider()).setMediaContainerService(mediaContainerService);
		((ImageValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);
		((ImageValueProvider) getPropertyFieldValueProvider()).setMediaFormat("format");

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof ImageValueProvider);
	}

	@Test
	public void testInvalidProduct() throws FieldValueProviderException
	{
		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testNoImage() throws FieldValueProviderException
	{
		final ProductModel product = mock(ProductModel.class);
		final MediaFormatModel mediaFormatModel = mock(MediaFormatModel.class);

		given(mediaService.getFormat("format")).willReturn(mediaFormatModel);
		given(product.getGalleryImages()).willReturn(Collections.<MediaContainerModel> emptyList());

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, product);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testProductImageValueProvider() throws FieldValueProviderException
	{
		final ProductModel product = mock(ProductModel.class);
		final MediaFormatModel mediaFormatModel = mock(MediaFormatModel.class);
		final MediaContainerModel mediaContainerModel = mock(MediaContainerModel.class);
		final MediaModel mediaModel = mock(MediaModel.class);

		given(mediaService.getFormat("format")).willReturn(mediaFormatModel);
		given(product.getGalleryImages()).willReturn(Collections.singletonList(mediaContainerModel));
		given(mediaContainerService.getMediaForFormat(mediaContainerModel, mediaFormatModel)).willReturn(mediaModel);
		given(mediaModel.getURL()).willReturn("url");
		given(fieldNameProvider.getFieldNames(indexedProperty, null)).willReturn(Collections.singletonList("field"));

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, product);

		Assert.assertNotNull(result);
		Assert.assertTrue(1 == result.size());

		final FieldValue fieldValue = result.iterator().next();

		Assert.assertEquals("field", fieldValue.getFieldName());
		Assert.assertEquals("url", fieldValue.getValue());
	}

	@Test
	public void testVariantProductImageValueProvider() throws FieldValueProviderException
	{
		final ProductModel product = mock(ProductModel.class);
		final VariantProductModel variantProduct = mock(VariantProductModel.class);
		final MediaFormatModel mediaFormatModel = mock(MediaFormatModel.class);
		final MediaContainerModel mediaContainerModel = mock(MediaContainerModel.class);
		final MediaModel mediaModel = mock(MediaModel.class);

		given(mediaService.getFormat("format")).willReturn(mediaFormatModel);
		given(variantProduct.getBaseProduct()).willReturn(product);
		given(product.getGalleryImages()).willReturn(Collections.singletonList(mediaContainerModel));
		given(mediaContainerService.getMediaForFormat(mediaContainerModel, mediaFormatModel)).willReturn(mediaModel);
		given(mediaModel.getURL()).willReturn("url");
		given(fieldNameProvider.getFieldNames(indexedProperty, null)).willReturn(Collections.singletonList("field"));

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, variantProduct);

		Assert.assertNotNull(result);
		Assert.assertTrue(1 == result.size());

		final FieldValue fieldValue = result.iterator().next();

		Assert.assertEquals("field", fieldValue.getFieldName());
		Assert.assertEquals("url", fieldValue.getValue());
	}

}
