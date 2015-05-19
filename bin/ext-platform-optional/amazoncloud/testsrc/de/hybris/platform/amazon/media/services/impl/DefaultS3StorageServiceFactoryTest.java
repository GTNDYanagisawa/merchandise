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
package de.hybris.platform.amazon.media.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.media.exceptions.ExternalStorageServiceException;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.GlobalMediaStorageConfig;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;

import java.util.Properties;

import org.jets3t.service.S3Service;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.multi.SimpleThreadedStorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultS3StorageServiceFactoryTest
{
	private static final String FOLDER_QUALIFIER = "fooBar";
	private static final String ACCESS_KEY = DefaultS3StorageServiceFactory.ACCESS_KEY;
	private static final String SECRET_ACCESS_KEY = DefaultS3StorageServiceFactory.SECRET_ACCESS_KEY;
	private static final String ENDPOINT_KEY = DefaultS3StorageServiceFactory.ENDPOINT_KEY;
	private static final String ACCESS_KEY_VAL = "adjkAKLDJaklsdjak";
	private static final String SECRET_ACCESS_KEY_VAL = "JakjdaklJKLDJQ890";
	private static final String BUCKET_ID_KEY = DefaultS3StorageServiceFactory.BUCKET_ID_KEY;
	private static final String BUCKET_ID_VAL = FOLDER_QUALIFIER.toLowerCase();

	@Mock
	private MediaStorageConfigService storageConfigService;
	@Mock
	private S3Service s3Service;
	@Mock
	private S3Bucket s3Bucket;
	@Mock
	private MediaFolderConfig folderConfig;
	@Mock
	private GlobalMediaStorageConfig storageConfig; //NOPMD
	private DefaultS3StorageServiceFactory serviceFactory;

	@Before
	public void setUp() throws Exception
	{
		serviceFactory = new DefaultS3StorageServiceFactory();
		given(storageConfigService.getConfigForFolder(FOLDER_QUALIFIER)).willReturn(folderConfig);
		given(folderConfig.getParameter(ACCESS_KEY)).willReturn(ACCESS_KEY_VAL);
		given(folderConfig.getParameter(SECRET_ACCESS_KEY)).willReturn(SECRET_ACCESS_KEY_VAL);
	}

	@Test
	public void shouldReturnS3ServiceWithoutConfiguredEndpoint()
	{
		// when
		final S3Service s3Service = serviceFactory.getS3ServiceForFolder(folderConfig);

		// then
		assertThat(s3Service).isNotNull();
		assertThat(s3Service.getJetS3tProperties()).isNotNull();
		assertThat(s3Service.getJetS3tProperties().getProperties()).isEmpty();
		assertThat(s3Service.getProviderCredentials().getAccessKey()).isEqualTo(ACCESS_KEY_VAL);
		assertThat(s3Service.getProviderCredentials().getSecretKey()).isEqualTo(SECRET_ACCESS_KEY_VAL);
	}

	@Test
	public void shouldReturnS3ServiceWithConfiguredEuropeEndpoint()
	{
		// given
		given(folderConfig.getParameter(ENDPOINT_KEY)).willReturn("s3-eu-west-1.amazonaws.com");

		// when
		final S3Service s3Service = serviceFactory.getS3ServiceForFolder(folderConfig);

		// then
		assertThat(s3Service).isNotNull();
		assertThat(s3Service.getJetS3tProperties()).isNotNull();
		final Properties properties = s3Service.getJetS3tProperties().getProperties();
		assertThat(properties).isNotEmpty();
		assertThat(properties.get(DefaultS3StorageServiceFactory.S3_ENDPOINT_KEY)).isEqualTo("s3-eu-west-1.amazonaws.com");
		assertThat(properties.get(DefaultS3StorageServiceFactory.DEFAULT_BUCKET_LOCATION_KEY)).isEqualTo("EU");
		assertThat(s3Service.getJetS3tProperties().getProperties());
		assertThat(s3Service.getProviderCredentials().getAccessKey()).isEqualTo(ACCESS_KEY_VAL);
		assertThat(s3Service.getProviderCredentials().getSecretKey()).isEqualTo(SECRET_ACCESS_KEY_VAL);
	}

	@Test
	public void shouldReturnMultithreadedS3Service()
	{
		// when
		final SimpleThreadedStorageService multiThreadedS3Service = serviceFactory.getMultiThreadedS3Service(s3Service);

		// then
		assertThat(multiThreadedS3Service).isNotNull();
	}

	@Test
	public void shouldReturnBucketForFolderWhenIsAccessibleInS3() throws ServiceException
	{
		// given
		given(folderConfig.getParameter(BUCKET_ID_KEY)).willReturn(BUCKET_ID_VAL);
		given(Boolean.valueOf(s3Service.isBucketAccessible(BUCKET_ID_VAL))).willReturn(Boolean.TRUE);
		given(s3Service.getBucket(BUCKET_ID_VAL)).willReturn(s3Bucket);

		// when
		final S3Bucket bucket = serviceFactory.getS3BucketForFolder(folderConfig, s3Service);

		// then
		assertThat(bucket).isNotNull();
	}

	@Test
	public void shouldThrowExternalStorageServiceExceptionWhenBucketIsNotAccessibleOnS3() throws ServiceException
	{
		// given
		given(folderConfig.getParameter(BUCKET_ID_KEY)).willReturn(BUCKET_ID_VAL);
		given(Boolean.valueOf(s3Service.isBucketAccessible(BUCKET_ID_VAL))).willReturn(Boolean.FALSE);

		try
		{
			// when
			serviceFactory.getS3BucketForFolder(folderConfig, s3Service);
			fail("should throw ExternalStorageServiceException");
		}
		catch (final ExternalStorageServiceException e)
		{
			// then
			assertThat(e).hasMessage(
					"Bucket with ID: " + BUCKET_ID_VAL + " is not accessible in S3 service. Check your configuration.");
		}
	}

	@Test
	public void shouldThrowExternalStorageServiceExceptionWhenBucketIdIsEmpty() throws ServiceException
	{
		try
		{
			// when
			serviceFactory.getS3BucketForFolder(folderConfig, s3Service);
			fail("should throw ExternalStorageServiceException");
		}
		catch (final ExternalStorageServiceException e)
		{
			// then
			assertThat(e).hasMessage("Bucket ID not found in S3 configuration");
		}
	}

}
