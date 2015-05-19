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
package de.hybris.platform.azure.media.storage;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.azure.media.services.WindowsAzureServiceFactory;
import de.hybris.platform.media.exceptions.MediaRemovalException;
import de.hybris.platform.media.exceptions.MediaStoreException;
import de.hybris.platform.media.services.MediaLocationHashService;
import de.hybris.platform.media.storage.MediaMetaData;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.media.storage.impl.StoredMediaData;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.microsoft.windowsazure.services.blob.client.BlobProperties;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.StorageException;


@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ CloudBlobClient.class, CloudBlobContainer.class, CloudBlockBlob.class, BlobProperties.class })
public class WindowsAzureBlobStorageStrategyTest
{
	private static final String FOLDER_QUALIFIER = "fooBar";
	private static final String MEDIA_ID = "123456";
	private static final String REAL_FILENAME = "foo.jpg";
	private static final String MIME = "image/jpeg";
	private static final String FOLDER_PATH = "foo";
	private static final Long MEDIA_SIZE = Long.valueOf(123456);

	@Mock
	private MediaLocationHashService locationHashService;
	@Mock
	private MediaFolderConfig folderConfig;
	@Mock
	private WindowsAzureServiceFactory serviceFactory;
	@Mock
	private CloudBlobClient blobClient;
	@Mock
	private CloudBlockBlob blob;
	@Mock
	private CloudBlobContainer blobContainer;
	@Mock
	private BlobProperties blobProperties;
	@Mock
	private CloudBlobContainer container1; // NOPMD
	@Mock
	private InputStream dataStream;
	private WindowsAzureBlobStorageStrategy strategy;

	@Before
	public void setUp() throws Exception
	{
		strategy = new WindowsAzureBlobStorageStrategy();
		strategy.setLocationHashService(locationHashService);
		strategy.setWindowsAzureServiceFactory(serviceFactory);

		given(serviceFactory.getCloudBlobClient(folderConfig)).willReturn(blobClient);
		given(serviceFactory.getContainerForFolder(folderConfig, blobClient)).willReturn(blobContainer);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFolderConfigIsNullOnStoringMedia()
	{
		// given
		final MediaFolderConfig folderConfig = null;

		try
		{
			// when
			strategy.store(folderConfig, MEDIA_ID, Collections.EMPTY_MAP, dataStream);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("folder config is required!");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenMediaIdIsNullOnStoringMedia()
	{
		// given
		final String mediaId = null;

		try
		{
			// when
			strategy.store(folderConfig, mediaId, Collections.EMPTY_MAP, dataStream);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("mediaId is required!");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenMetaDataIsNullOnStoringMedia()
	{
		// given
		final Map<String, Object> metaData = null;

		try
		{
			// when
			strategy.store(folderConfig, MEDIA_ID, metaData, dataStream);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("metaData is required!");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenDataStreamIsNullOnStoringMedia()
	{
		// given
		final InputStream dataStream = null;

		try
		{
			// when
			strategy.store(folderConfig, MEDIA_ID, Collections.EMPTY_MAP, dataStream);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("dataStream is required!");
		}
	}

	@Test
	public void shouldThrowMediaStoreExceptionWhenUnderyingServiceHasThrowedStorageException() throws URISyntaxException,
			StorageException
	{
		// given
		final Map<String, Object> metaData = buildMediaMetaData(MIME, REAL_FILENAME, FOLDER_PATH);
		given(blobContainer.getBlockBlobReference("hec/hac/" + MEDIA_ID + '/' + REAL_FILENAME)).willReturn(blob);
		given(blob.getProperties()).willReturn(blobProperties);
		doThrow(new StorageException("foo", "Foo", 0, null, null)).when(blob).uploadProperties();

		try
		{
			// when
			strategy.store(folderConfig, MEDIA_ID, metaData, dataStream);
			fail("should throw MediaStoreException");
		}
		catch (final MediaStoreException e)
		{
			// then OK
		}
	}

	@Test
	public void shouldThrowMediaStoreExceptionWhenUnderyingServiceHasThrowedURISyntaxException() throws URISyntaxException,
			StorageException
	{
		// given
		final Map<String, Object> metaData = buildMediaMetaData(MIME, REAL_FILENAME, FOLDER_PATH);
		given(blobContainer.getBlockBlobReference("hec/hac/" + MEDIA_ID + '/' + REAL_FILENAME)).willReturn(blob);
		given(blob.getProperties()).willReturn(blobProperties);
		given(blob.getName()).willThrow(new URISyntaxException("foo", "bar"));

		try
		{
			// when
			strategy.store(folderConfig, MEDIA_ID, metaData, dataStream);
			fail("should throw MediaStoreException");
		}
		catch (final MediaStoreException e)
		{
			// then OK
		}
	}

	@Test
	public void shouldThrowMediaStoreExceptionWhenUnderyingServiceHasThrowedIOException() throws URISyntaxException,
			StorageException, IOException
	{
		// given
		final Map<String, Object> metaData = buildMediaMetaData(MIME, REAL_FILENAME, FOLDER_PATH);
		given(blobContainer.getBlockBlobReference("hec/hac/" + MEDIA_ID + '/' + REAL_FILENAME)).willReturn(blob);
		doThrow(new IOException()).when(blob).upload(dataStream, MEDIA_SIZE.longValue());

		try
		{
			// when
			strategy.store(folderConfig, MEDIA_ID, metaData, dataStream);
			fail("should throw MediaStoreException");
		}
		catch (final MediaStoreException e)
		{
			// then OK
		}
	}

	@Test
	public void shouldStoreMediaInAzureStora() throws URISyntaxException, StorageException, IOException
	{
		// given
		final Map<String, Object> metaData = buildMediaMetaData(MIME, REAL_FILENAME, FOLDER_PATH);
		given(blobContainer.getBlockBlobReference("hec/hac/" + MEDIA_ID + '/' + REAL_FILENAME)).willReturn(blob);
		given(blob.getProperties()).willReturn(blobProperties);

		// when
		final StoredMediaData storedMediaData = strategy.store(folderConfig, MEDIA_ID, metaData, dataStream);

		// then
		verify(blob).upload(dataStream, MEDIA_SIZE.longValue());
		verify(blob).getName();
		verify(blobProperties).setCacheControl("public, max-age=3600");
		verify(blobProperties).setContentType(MIME);
		assertThat(storedMediaData).isNotNull();
		assertThat(storedMediaData.getSize()).isEqualTo(MEDIA_SIZE);
	}

	@Test
	public void shouldDeleteMediaFromStorage() throws URISyntaxException, StorageException
	{
		// given
		given(blobContainer.getBlockBlobReference(MEDIA_ID)).willReturn(blob);

		// when
		strategy.delete(folderConfig, MEDIA_ID);

		// then
		verify(blob).delete();
	}

	@Test
	public void shouldThrowMediaRemovalExceptionWhenUnderlyingServiceHasThrowedURISyntaxException() throws URISyntaxException,
			StorageException
	{
		// given
		given(blobContainer.getBlockBlobReference(MEDIA_ID)).willThrow(new URISyntaxException("foo", "bar"));

		try
		{
			// when
			strategy.delete(folderConfig, MEDIA_ID);
			fail("should throw MediaRemovalException");
		}
		catch (final MediaRemovalException e)
		{
			// then OK
		}
	}

	@Test
	public void shouldThrowMediaRemovalExceptionWhenUnderlyingServiceHasThrowedStorageException() throws URISyntaxException,
			StorageException
	{
		// given
		given(blobContainer.getBlockBlobReference(MEDIA_ID)).willThrow(new StorageException("fooBar", "fooBar", 0, null, null));

		try
		{
			// when
			strategy.delete(folderConfig, MEDIA_ID);
			fail("should throw MediaRemovalException");
		}
		catch (final MediaRemovalException e)
		{
			// then OK
		}
	}

	@Test
	public void shouldIgnoreStorageExceptionFromExternalServiceIfBlobWasNotFoundOnDeleteTry() throws URISyntaxException,
			StorageException
	{
		// given
		given(blobContainer.getBlockBlobReference(MEDIA_ID)).willReturn(blob);
		doThrow(new StorageException("blobNotFound", "blobNotFound", 0, null, null)).when(blob).delete();

		// when
		strategy.delete(folderConfig, MEDIA_ID);

		// then
		verify(blob).delete();
	}

	@Test
	public void shouldReturnMediaAsStream() throws URISyntaxException, StorageException
	{
		// given
		given(blobContainer.getBlockBlobReference(MEDIA_ID)).willReturn(blob);

		// when
		strategy.getAsStream(folderConfig, MEDIA_ID);

		// then
		verify(blob).openInputStream();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnGettingMediaAsFile()
	{
		strategy.getAsFile(folderConfig, MEDIA_ID);
	}

	private Map<String, Object> buildMediaMetaData(final String mime, final String originalName, final String folderPath)
	{
		final Map<String, Object> metaData = new HashMap<String, Object>();
		metaData.put(MediaMetaData.MIME, mime);
		metaData.put(MediaMetaData.FILE_NAME, originalName);
		metaData.put(MediaMetaData.FOLDER_PATH, folderPath);
		metaData.put(MediaMetaData.SIZE, MEDIA_SIZE);
		return metaData;
	}

}
