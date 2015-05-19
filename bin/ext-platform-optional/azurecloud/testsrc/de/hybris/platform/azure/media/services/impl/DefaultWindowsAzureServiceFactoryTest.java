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
package de.hybris.platform.azure.media.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.media.exceptions.ExternalStorageServiceException;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.core.storage.StorageException;


@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ CloudBlobClient.class, CloudBlobContainer.class })
public class DefaultWindowsAzureServiceFactoryTest
{
	private static final String COMPUTED_CONTAINER_NAME = "sys-master-foo-bar";
	private static final String FOLDER_QUALIFIER = "fooBar";
	private static final String CONTAINER_NAME = "foo_Bar";
	private static final String CONNECTION_STRING_KEY = DefaultWindowsAzureServiceFactory.CONNECTION_STRING_KEY;
	private static final String CONTAINER_ADDRESS_KEY = DefaultWindowsAzureServiceFactory.CONTAINER_ADDRESS_KEY;
	private static final String CONNECTION_STRING_VAL = "DefaultEndpointsProtocol=http;AccountName=foo;AccountKey=BxNMbuu+uwNAQHvbDSaNhfCxgA==";
	private static final String PUBLIC_BASE_URL_KEY = DefaultWindowsAzureServiceFactory.PUBLIC_BASE_URL_KEY;
	private static final String PUBLIC_BASE_URL_VAL = "foobar.blob.core.windows.net";

	@Mock
	private MediaStorageConfigService storageConfigService;
	@Mock
	private MediaFolderConfig mediaFolderConfig;
	@Mock
	private CloudBlobClient blobClient;
	@Mock
	private CloudBlobContainer blobContainer;
	private DefaultWindowsAzureServiceFactory serviceFactory;

	@Before
	public void setUp() throws Exception
	{
		serviceFactory = new DefaultWindowsAzureServiceFactory()
		{
			@Override
			public void setTenantPrefix()
			{
				tenantPrefix = "sys-master";
			}
		};
		serviceFactory.setTenantPrefix();

		given(storageConfigService.getConfigForFolder(FOLDER_QUALIFIER)).willReturn(mediaFolderConfig);
	}


	@Test
	public void shouldThrowExternalStorageServiceExceptionWhenConnectionStringIsBlankOrNull()
	{
		// given
		given(mediaFolderConfig.getParameter(CONNECTION_STRING_KEY)).willReturn(null);

		try
		{
			// when
			serviceFactory.getCloudBlobClient(mediaFolderConfig);
			fail("should throw ExternalStorageServiceException");
		}
		catch (final ExternalStorageServiceException e)
		{
			// then
			assertThat(e).hasMessage("Windows Azure specific configuration not found [key: " + CONNECTION_STRING_KEY + " was empty");
		}
	}

	@Test
	public void shouldReturnCloudBlobClientForFolder()
	{
		// given
		given(mediaFolderConfig.getParameter(CONNECTION_STRING_KEY)).willReturn(CONNECTION_STRING_VAL);

		// when
		final CloudBlobClient blobClient = serviceFactory.getCloudBlobClient(mediaFolderConfig);

		// then
		assertThat(blobClient).isNotNull();
	}

	@Test
	public void shouldReturnContainerForFolder() throws URISyntaxException, StorageException
	{
		// given
		given(mediaFolderConfig.getParameter(CONTAINER_ADDRESS_KEY)).willReturn(CONTAINER_NAME);
		given(blobClient.getContainerReference(COMPUTED_CONTAINER_NAME)).willReturn(blobContainer);

		// when
		final CloudBlobContainer container = serviceFactory.getContainerForFolder(mediaFolderConfig, blobClient);

		// then
		assertThat(container).isNotNull();
	}

	@Test
	public void shouldReturnConfiguredBaseURLForFolder()
	{
		// given
		given(mediaFolderConfig.getParameter(CONTAINER_ADDRESS_KEY)).willReturn(CONTAINER_NAME);
		given(mediaFolderConfig.getParameter(PUBLIC_BASE_URL_KEY)).willReturn(PUBLIC_BASE_URL_VAL);

		// when
		final String baseURL = serviceFactory.getFolderBaseURL(mediaFolderConfig);

		// then
		assertThat(baseURL).isNotNull().isNotEmpty();
		assertThat(baseURL).isEqualTo(PUBLIC_BASE_URL_VAL + "/" + COMPUTED_CONTAINER_NAME);
	}
}
