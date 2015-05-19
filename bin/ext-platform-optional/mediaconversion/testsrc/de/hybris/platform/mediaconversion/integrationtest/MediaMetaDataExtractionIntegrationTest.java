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
package de.hybris.platform.mediaconversion.integrationtest;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.MediaMetaDataService;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import junit.framework.Assert;


/**
 * @author pohl
 * 
 */
@IntegrationTest
public class MediaMetaDataExtractionIntegrationTest extends ServicelayerBaseTest
{

	@Resource
	private MediaService mediaService;

	@Resource
	private ModelService modelService;

	@Resource
	private MediaMetaDataService mediaMetaDataService;

	@Test
	public void testTextMetadataExtraction() throws Exception
	{
		final MediaModel text = modelService.create(MediaModel.class);
		text.setCode("some.txt");
		text.setCatalogVersion(TestDataFactory.someCatalogVersion(this.modelService));
		modelService.save(text);
		mediaService.setStreamForMedia(text, new ByteArrayInputStream("Hallo Welt!".getBytes("UTF-8")), "some.txt", "text/plain");
		modelService.refresh(text);

		Assert.assertEquals("Mimetype correct.", "text/plain", text.getMime());
		Assert.assertNotNull("Metadata is not null.", text.getMetaData());
		Assert.assertTrue("Metadata is empty.", text.getMetaData().isEmpty());
		this.mediaMetaDataService.extractAllMetaData(text);
		Assert.assertNotNull("Metadata is still not null.", text.getMetaData());
		Assert.assertTrue("Metadata is still empty.", text.getMetaData().isEmpty());

		final Map<String, Map<String, String>> metaData = this.mediaMetaDataService.getAllMetaData(text);
		Assert.assertNotNull("Metadata map is not null.", metaData);
		Assert.assertTrue("Metadata map is empty.", metaData.isEmpty());
	}

	@Test
	public void testImageMetadataExtraction() throws Exception
	{
		for (final TestMedia mediaInfo : TestMedia.values())
		{
			final MediaModel media = mediaInfo.createMedia(this.modelService, this.mediaService, null);
			Assert.assertNotNull("Although no metadata is available we do not return null.", media.getMetaData());
			Assert.assertTrue("...but the collection is empty.", media.getMetaData().isEmpty());
			Assert.assertNotNull("Convenience method getAllMetaData does not return null.",
					this.mediaMetaDataService.getAllMetaData(media));
			Assert.assertTrue("Convenience method getAllMetaData returns empty collection.", this.mediaMetaDataService
					.getAllMetaData(media).isEmpty());
			Assert.assertNotNull("Convenience method getMetaData does not return null (also for nonsense).",
					this.mediaMetaDataService.getMetaData(media, "pupu"));
			Assert.assertTrue("Convenience method getMetaData returns empty collection.",
					this.mediaMetaDataService.getMetaData(media, "pupu").isEmpty());

			this.mediaMetaDataService.extractAllMetaData(media);
			Assert.assertNotNull("never return null.", media.getMetaData());
			Assert.assertFalse("And not empty no more.", media.getMetaData().isEmpty());
			final Map<String, Map<String, String>> metadata = this.mediaMetaDataService.getAllMetaData(media);
			Assert.assertNotNull("metadata map not null.", metadata);
			Assert.assertFalse("metadata map not empty.", metadata.isEmpty());
			Assert.assertTrue("Has 'image' metadata", metadata.containsKey("image"));
			Assert.assertEquals("Has 'exif' metadata", mediaInfo.hasExifMetaData(), metadata.containsKey("exif"));
			Assert.assertNotNull("metadata identified width exists", metadata.get("image").get("width"));
			Assert.assertEquals("metadata identified width equals", mediaInfo.getWidth(),
					Integer.parseInt(metadata.get("image").get("width")));
			final Map<String, String> imageMetadata = this.mediaMetaDataService.getMetaData(media, "image");
			Assert.assertNotNull("metadata identified height exists", imageMetadata.get("height"));
			Assert.assertEquals("metadata identified height equals", mediaInfo.getHeight(),
					Integer.parseInt(imageMetadata.get("height")));

			Assert.assertEquals("'imageMetadata' equal", metadata.get("image"), imageMetadata);

			// other metadata to check
			Assert.assertTrue("identified colorspace", imageMetadata.get("colorspace").endsWith("RGB"));
			Assert.assertEquals("identified fileformat", mediaInfo.getFileFormat(), imageMetadata.get("fileformat"));
		}
	}
}
