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
package de.hybris.platform.gridfs.media.storage;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.media.services.MediaLocationHashService;
import de.hybris.platform.media.storage.MediaMetaData;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.media.storage.impl.StoredMediaData;
import de.hybris.platform.util.MediaUtil;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.MongoDbFactory;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GridFSMediaStorageStrategyTest
{
	private static final String TENANT_CONTAINER_PREFIX = "sys-master";
	private static final String MEDIA_ID = "123456";
	private static final String REAL_FILENAME = "foo.jpg";
	private static final String MEDIA_LOCATION = MEDIA_ID + MediaUtil.FILE_SEP + REAL_FILENAME;
	private static final String MIME = "image/jpeg";
	private static final String FOLDER_QUALIFIER = "fooBar";
	private static final long FILE_SIZE = 12345;

	@Mock
	private MongoDbFactory dbFactory;
	@Mock
	private MediaLocationHashService mediaLocationHashService;
	@Mock
	private MediaStorageConfigService storageConfig;
	@Mock
	private MediaFolderConfig folderConfig;
	@Mock
	private InputStream dataStream;
	@Mock
	private GridFS gridFS;
	@Mock
	private GridFSInputFile gridFsFile;
	@Mock
	private GridFSDBFile gridFsDbFile;

	private GridFSMediaStorageStrategy strategy;

	@Before
	public void setUp() throws Exception
	{
		strategy = new GridFSMediaStorageStrategy(dbFactory, mediaLocationHashService)
		{
			@Override
			public void setTenantPrefix()
			{
				tenantPrefix = TENANT_CONTAINER_PREFIX;
			}

			@Override
			protected GridFS buildGridFsInstance(final String bucketName)
			{
				return gridFS;
			}
		};
		strategy.setTenantPrefix();

		given(storageConfig.getConfigForFolder(FOLDER_QUALIFIER)).willReturn(folderConfig);
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
			assertThat(e).hasMessage("config is required!");
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
	public void shouldStoreMediaUsingGridfsStrategy()
	{
		// given
		given(gridFS.createFile(dataStream, MEDIA_LOCATION, true)).willReturn(gridFsFile);
		given(Long.valueOf(gridFsFile.getLength())).willReturn(Long.valueOf(FILE_SIZE));

		// when
		final StoredMediaData storedMediaData = strategy.store(folderConfig, MEDIA_ID, buildMediaMetaData(MIME, REAL_FILENAME),
				dataStream);

		// then
		assertThat(storedMediaData).isNotNull();

		assertThat(storedMediaData.getLocation()).isEqualTo(MEDIA_LOCATION);
		assertThat(storedMediaData.getSize()).isEqualTo(Long.valueOf(FILE_SIZE));
		verify(gridFsFile).save();
		verify(gridFsFile).setContentType(MIME);
	}


	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFolderConfigIsNullOnDeleteMedia()
	{
		// given 
		final MediaFolderConfig folderConfig = null;

		try
		{
			// when
			strategy.delete(folderConfig, MEDIA_LOCATION);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("config is required!");
		}
	}


	@Test
	public void shouldThrowIllegalArgumentExceptionWhenLocationIsNullOnDeleteMedia()
	{
		// given
		final String location = null;

		try
		{
			// when
			strategy.delete(folderConfig, location);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("location is required!");
		}
	}

	@Test
	public void shouldDeleteMediaFromStorage()
	{
		// when
		strategy.delete(folderConfig, MEDIA_LOCATION);

		// then
		verify(gridFS).remove(MEDIA_LOCATION);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFolderConfigIsNullOnGettingMediaAsStream()
	{
		// given
		final MediaFolderConfig folderConfig = null;

		try
		{
			// when
			strategy.getAsStream(folderConfig, MEDIA_LOCATION);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("config is required!");
		}
	}


	@Test
	public void shouldThrowIllegalArgumentExceptionWhenLocationIsNullOnGettingMediaAsStream()
	{
		// given
		final String location = null;

		try
		{
			// when
			strategy.getAsStream(folderConfig, location);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("location is required!");
		}
	}

	@Test
	public void shouldGetMediaAsStream()
	{
		// given
		given(gridFS.findOne(MEDIA_LOCATION)).willReturn(gridFsDbFile);
		given(gridFsDbFile.getInputStream()).willReturn(dataStream);

		// when
		final InputStream mediaAsStream = strategy.getAsStream(folderConfig, MEDIA_LOCATION);

		// then
		assertThat(mediaAsStream).isNotNull().isEqualTo(dataStream);
	}

	@Test
	public void shouldThrowUnsupportedOperationExceptionWhenGettingMediaAsFile()
	{
		try
		{
			// when
			strategy.getAsFile(folderConfig, MEDIA_LOCATION);
			fail("Should throw IllegalArgumentException");
		}
		catch (final UnsupportedOperationException e)
		{
			// then
			assertThat(e).hasMessage("Obtaining media as file is not supported for GridFS storage. Use getMediaAsStream method.");
		}
	}

	private Map<String, Object> buildMediaMetaData(final String mime, final String originalName)
	{
		final Map<String, Object> metaData = new HashMap<String, Object>();
		metaData.put(MediaMetaData.MIME, mime);
		metaData.put(MediaMetaData.FILE_NAME, originalName);
		return metaData;
	}
}
