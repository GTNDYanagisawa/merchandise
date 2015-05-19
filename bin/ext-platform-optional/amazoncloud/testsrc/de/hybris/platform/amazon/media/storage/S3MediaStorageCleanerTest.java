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
package de.hybris.platform.amazon.media.storage;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.amazon.media.services.S3StorageServiceFactory;
import de.hybris.platform.amazon.media.services.impl.DefaultS3StorageServiceFactory;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.GlobalMediaStorageConfig;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;

import java.util.Set;

import javax.annotation.Resource;

import org.jets3t.service.S3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multi.SimpleThreadedStorageService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


@UnitTest
public class S3MediaStorageCleanerTest
{
	private static final String FOLDER_QUALIFIER1 = "foobar1";
	private static final String FOLDER_QUALIFIER2 = "foobar2";
	private static final String TENANT_CONTAINER_PREFIX = "sys-junit";
	private static final String BUCKET_NAME1 = TENANT_CONTAINER_PREFIX + "-" + FOLDER_QUALIFIER1;
	private static final String BUCKET_NAME2 = TENANT_CONTAINER_PREFIX + "-" + FOLDER_QUALIFIER2;

	@Resource(name = "s3MediaStorage")
	private S3MediaStorageCleaner cleaner;

	@Mock
	private SimpleThreadedStorageService simpleThreadedStorageService;
	@Mock
	private S3StorageServiceFactory serviceFactory;
	@Mock
	private S3Bucket s3Bucket1, s3Bucket2;
	@Mock
	private S3Service s3Service;
	@Mock
	private S3Object s3Objcet, s3Object2, s3Object3;
	@Mock
	private MediaFolderConfig folderConfig1, folderConfig2;
	@Mock
	private MediaStorageConfigService storageConfigService;
	@Mock
	private GlobalMediaStorageConfig mediaStorageConfig;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		cleaner = new S3MediaStorageCleaner()
		{
			@Override
			public void setTenantPrefix()
			{
				tenantPrefix = TENANT_CONTAINER_PREFIX;
			}
		};
		cleaner.setTenantPrefix();
		cleaner.setStorageConfigService(storageConfigService);
		cleaner.setS3StorageServiceFactory(serviceFactory);

		given(storageConfigService.getDefaultStrategyId()).willReturn("s3MediaStorageStrategy");
		given(storageConfigService.getGlobalSettingsForStrategy("s3MediaStorageStrategy")).willReturn(mediaStorageConfig);
		given(mediaStorageConfig.getParameter(DefaultS3StorageServiceFactory.BUCKET_ID_KEY, String.class)).willReturn("fooBar");
		given(mediaStorageConfig.getParameter(DefaultS3StorageServiceFactory.ACCESS_KEY, String.class)).willReturn("12345");
		given(mediaStorageConfig.getParameter(DefaultS3StorageServiceFactory.SECRET_ACCESS_KEY, String.class)).willReturn("Secret");
		given(mediaStorageConfig.getParameter(DefaultS3StorageServiceFactory.ENDPOINT_KEY, String.class)).willReturn("fooBarBaz");

		final Set<MediaFolderConfig> foldersConfig = Sets.<MediaFolderConfig> newHashSet(folderConfig1, folderConfig2);
		given(storageConfigService.getFolderConfigsForStrategy("s3MediaStorageStrategy")).willReturn(foldersConfig);
		given(folderConfig1.getFolderQualifier()).willReturn(FOLDER_QUALIFIER1);
		given(folderConfig2.getFolderQualifier()).willReturn(FOLDER_QUALIFIER2);
		given(serviceFactory.getS3Service("12345", "Secret", "fooBarBaz")).willReturn(s3Service);
		given(serviceFactory.getS3Bucket(s3Service, "fooBar")).willReturn(s3Bucket1);
	}

	@Test
	public void shouldCleanStorageOnInitializationWhenS3IsDefaultStrategy() throws Exception
	{
		// given
		given(serviceFactory.getS3ServiceForFolder(folderConfig1)).willReturn(s3Service);
		given(serviceFactory.getS3ServiceForFolder(folderConfig2)).willReturn(s3Service);
		given(serviceFactory.getS3BucketForFolder(folderConfig1, s3Service)).willReturn(s3Bucket1);
		given(serviceFactory.getS3BucketForFolder(folderConfig2, s3Service)).willReturn(s3Bucket2);
		given(s3Bucket1.getName()).willReturn(BUCKET_NAME1);
		given(s3Bucket2.getName()).willReturn(BUCKET_NAME2);

		final S3Object[] s3Objects = new S3Object[]
		{ s3Objcet, s3Object2, s3Object3 };
		final S3Object[] s3Objects2 = new S3Object[] {};
		given(s3Service.listObjects(BUCKET_NAME1, TENANT_CONTAINER_PREFIX, null)).willReturn(s3Objects);
		given(s3Service.listObjects(BUCKET_NAME2, TENANT_CONTAINER_PREFIX, null)).willReturn(s3Objects2);
		given(serviceFactory.getMultiThreadedS3Service(s3Service)).willReturn(simpleThreadedStorageService);

		// when
		cleaner.onInitialize();

		// then
		verify(simpleThreadedStorageService, times(2)).deleteObjects(BUCKET_NAME1, s3Objects);
		verify(simpleThreadedStorageService, times(0)).deleteObjects(BUCKET_NAME2, s3Objects2);
	}

}
