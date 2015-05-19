/*CC
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
package de.hybris.platform.amazon.media.url;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.amazon.media.services.S3StorageServiceFactory;
import de.hybris.platform.media.MediaSource;
import de.hybris.platform.media.exceptions.ExternalStorageServiceException;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.testframework.TestUtils;

import java.util.Date;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class S3MediaURLStrategyTest
{
	private static final String ACCESS_KEY = "accessKeyID";
	private static final String SECRET_ACCESS_KEY = "secretAccessKey";
	private static final String ACCESS_KEY_VALUE = "fooBar";
	private static final String SECRET_ACCESS_KEY_VALUE = "secretFooBar";
	private static final String FOLDER_QUALIFIER = "foo";
	private static final String BUCKET_ID_KEY = "bucketId";
	private static final String BUCKET_ID = "fakeBucket";
	private static final String LOCATION = "fooBar.png";

	@Mock
	private S3StorageServiceFactory serviceFactory;
	@Mock
	private MediaStorageConfigService storageConfigService;
	@Mock
	private MediaFolderConfig mediaFolderConfig;
	@Mock
	private S3Service s3Service;
	@Mock
	private S3Bucket bucket;
	@Mock
	private MediaSource media;

	private S3MediaURLStrategy s3MediaURLStrategy;

	@Before
	public void createS3MediaURLStrategy() throws Exception
	{
		s3MediaURLStrategy = new S3MediaURLStrategy(serviceFactory);

		given(storageConfigService.getConfigForFolder(FOLDER_QUALIFIER)).willReturn(mediaFolderConfig);
		given(mediaFolderConfig.getParameter(ACCESS_KEY)).willReturn(ACCESS_KEY_VALUE);
		given(mediaFolderConfig.getParameter(SECRET_ACCESS_KEY)).willReturn(SECRET_ACCESS_KEY_VALUE);
		given(mediaFolderConfig.getParameter(BUCKET_ID_KEY)).willReturn(BUCKET_ID);
		given(serviceFactory.getS3ServiceForFolder(mediaFolderConfig)).willReturn(s3Service);
		given(serviceFactory.getS3BucketForFolder(mediaFolderConfig, s3Service)).willReturn(bucket);
		given(bucket.getName()).willReturn(BUCKET_ID);
		given(media.getLocation()).willReturn(LOCATION);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFolderConfigIsNull()
	{
		// given
		final MediaFolderConfig mediaFolderConfig = null;

		try
		{
			// when
			s3MediaURLStrategy.getUrlForMedia(mediaFolderConfig, media);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("Folder config is required to perform this operation");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenMediaSourceIsNull()
	{
		// given
		final MediaSource media = null;

		try
		{
			// when
			s3MediaURLStrategy.getUrlForMedia(mediaFolderConfig, media);
			fail("MediaSource is required to perform this operation");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("MediaSource is required to perform this operation");
		}
	}

	@Test
	public void shouldRenderS3SignedUrlOverHttpsUsingS3ServiceForFolderAndLocation() throws S3ServiceException
	{
		// given
		given(mediaFolderConfig.getParameter(S3MediaURLStrategy.SIGNED_KEY, Boolean.class, Boolean.TRUE)).willReturn(Boolean.TRUE);
		given(mediaFolderConfig.getParameter(S3MediaURLStrategy.SIGNED_VALID_FOR_KEY, Integer.class, Integer.valueOf(10)))
				.willReturn(Integer.valueOf(10));

		// when
		s3MediaURLStrategy.getUrlForMedia(mediaFolderConfig, media);

		// then
		verify(s3Service).createSignedGetUrl(eq(BUCKET_ID), eq(LOCATION), any(Date.class));
	}

	@Test
	public void shouldRenderS3UnsignedUrlUsingS3ServiceForFolderAndLocationWithVHostdAndHttpsEnabled() throws S3ServiceException
	{
		// given
		given(mediaFolderConfig.getParameter(S3MediaURLStrategy.SIGNED_KEY, Boolean.class, Boolean.TRUE)).willReturn(Boolean.FALSE);
		given(mediaFolderConfig.getParameter(S3MediaURLStrategy.VIRTUAL_HOST_KEY, Boolean.class, Boolean.FALSE)).willReturn(
				Boolean.TRUE);
		given(mediaFolderConfig.getParameter(S3MediaURLStrategy.USE_HTTPS_KEY, Boolean.class, Boolean.TRUE)).willReturn(
				Boolean.TRUE);

		// when
		s3MediaURLStrategy.getUrlForMedia(mediaFolderConfig, media);

		// then
		verify(s3Service).createUnsignedObjectUrl(BUCKET_ID, LOCATION, true, true, false);
	}

	@Test
	public void shouldRenderS3UnsignedUrlUsingS3ServiceForFolderAndLocationWithVHostAndHttpsDisabled() throws S3ServiceException
	{
		// given
		given(mediaFolderConfig.getParameter(S3MediaURLStrategy.SIGNED_KEY, Boolean.class, Boolean.TRUE)).willReturn(Boolean.FALSE);
		given(mediaFolderConfig.getParameter(S3MediaURLStrategy.VIRTUAL_HOST_KEY, Boolean.class, Boolean.FALSE)).willReturn(
				Boolean.FALSE);
		given(mediaFolderConfig.getParameter(S3MediaURLStrategy.USE_HTTPS_KEY, Boolean.class, Boolean.TRUE)).willReturn(
				Boolean.FALSE);

		// when
		s3MediaURLStrategy.getUrlForMedia(mediaFolderConfig, media);

		// then
		verify(s3Service).createUnsignedObjectUrl(BUCKET_ID, LOCATION, false, false, false);
	}

	@Test
	public void shouldReturnEmptyUrlWhenS3RelatedServicesWillThrowExternalStorageServiceException() throws ServiceException
	{
		// given
		TestUtils.disableFileAnalyzer("expected error");
		given(serviceFactory.getS3BucketForFolder(mediaFolderConfig, s3Service)).willThrow(
				new ExternalStorageServiceException("foo"));

		// when
		final String urlForMedia = s3MediaURLStrategy.getUrlForMedia(mediaFolderConfig, media);

		// then
		assertThat(urlForMedia).isNotNull().isEmpty();
		TestUtils.enableFileAnalyzer();
	}

}
