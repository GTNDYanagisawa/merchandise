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
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.MediaMetaDataService;
import de.hybris.platform.mediaconversion.enums.ConversionStatus;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;


/**
 * @author pohl
 * 
 */
@IntegrationTest
public class MediaConversionIntegrationTest extends ServicelayerBaseTest
{
	private static final String[] FORMAT_MIMES =
	{ "image/gif", "image/jpeg", "image/tiff", "image/png" };

	@Resource
	private MediaService mediaService;

	@Resource
	private ModelService modelService;

	@Resource
	private MediaConversionService mediaConversionService;

	@Resource
	private MediaMetaDataService mediaMetaDataService;

	private MediaContainerModel createContainer(final String name)
	{
		final MediaContainerModel container = this.modelService.create(MediaContainerModel.class);
		container.setQualifier(name);
		container.setCatalogVersion(TestDataFactory.someCatalogVersion(this.modelService));
		this.modelService.save(container);

		// container has no master yet
		Assert.assertNull("No master yet.", container.getMaster());
		return container;
	}

	/**
	 * See also <a href="https://jira.hybris.com/browse/MECO-84">MECO-84</a>.
	 */
	@Test
	public void testGetConversionStatus()
	{
		final Map<TestResize, ConversionMediaFormatModel> formats = TestResize.create(this.modelService);
		final MediaContainerModel container = this.createContainer("testGetConversionStatus");
		Assert.assertEquals(ConversionStatus.EMPTY, this.mediaConversionService.getConversionStatus(container));
		Assert.assertEquals("No converted medias.", 0, this.mediaConversionService.getConvertedMedias(container).size());

		final MediaModel master = TestMedia.TULIP.createMedia(this.modelService, this.mediaService, container);
		Assert.assertEquals(ConversionStatus.UNCONVERTED, this.mediaConversionService.getConversionStatus(container));
		Assert.assertEquals("Still no converted medias.", 0, this.mediaConversionService.getConvertedMedias(container).size());

		final MediaModel child = this.mediaConversionService.getOrConvert(container, formats.get(TestResize.HALFSIZE));
		Assert.assertEquals("Child has correct format.", formats.get(TestResize.HALFSIZE), child.getMediaFormat());
		Assert.assertEquals("Child has correct original.", master, child.getOriginal());
		Assert.assertEquals("Child has correct container.", container, child.getMediaContainer());
		Assert.assertTrue("Master contains child.", master.getConvertedMedias().contains(child));
		Assert.assertEquals(ConversionStatus.PARTIALLY_CONVERTED, this.mediaConversionService.getConversionStatus(container));
		Assert.assertEquals("Only one converted media.", 1, this.mediaConversionService.getConvertedMedias(container).size());

		this.mediaConversionService.convertMedias(container);
		Assert.assertEquals(ConversionStatus.CONVERTED, this.mediaConversionService.getConversionStatus(container));
		Assert.assertEquals("All converted medias available.", formats.size(), //
				this.mediaConversionService.getConvertedMedias(container).size());

		// remove a child (MECO-84)
		this.modelService.remove(child);
		Assert.assertEquals(ConversionStatus.PARTIALLY_CONVERTED, this.mediaConversionService.getConversionStatus(container));
		Assert.assertEquals("Only one converted media.", formats.size() - 1, //
				this.mediaConversionService.getConvertedMedias(container).size());

		// remove another one to be sure... ;)
		this.modelService.remove(this.mediaConversionService.getOrConvert(container, formats.get(TestResize.ROTATE_SMALLSIZE)));
		Assert.assertEquals(ConversionStatus.PARTIALLY_CONVERTED, this.mediaConversionService.getConversionStatus(container));
		Assert.assertEquals("Only one converted media.", formats.size() - 2, //
				this.mediaConversionService.getConvertedMedias(container).size());
	}

	@Test
	public void testFormatConversion()
	{
		final Collection<ConversionMediaFormatModel> testFormats = new LinkedList<ConversionMediaFormatModel>();
		for (final String mime : FORMAT_MIMES)
		{
			final ConversionMediaFormatModel format = modelService.create(ConversionMediaFormatModel.class);
			format.setQualifier(mime.substring(mime.indexOf('/') + 1));
			format.setConversionStrategy("imageMagickMediaConversionStrategy");
			format.setMimeType(mime);
			this.modelService.save(format);
			testFormats.add(format);
		}

		for (final TestMedia media : TestMedia.values())
		{
			final MediaContainerModel container = this.createContainer("testFormatConversion_" + media.name());

			final MediaModel master = media.createMedia(this.modelService, this.mediaService, container);
			Assert.assertNotNull("media created", master);
			Assert.assertEquals("Container set", container, master.getMediaContainer());
			Assert.assertEquals("MimeType set correctly", media.getMime(), master.getMime());

			// the created media is the master of the container
			Assert.assertEquals("Master set correctly", master, container.getMaster());

			for (final ConversionMediaFormatModel format : testFormats)
			{
				try
				{
					this.mediaService.getMediaByFormat(container, format);
					Assert.fail("Media in format already available :(");
				}
				catch (final ModelNotFoundException e)
				{
					// ...as expected
				}
				final MediaModel converted = this.mediaConversionService.getOrConvert(container, format);
				Assert.assertNotNull("Media nicely converted", converted);
				Assert.assertEquals("container set", container, converted.getMediaContainer());
				Assert.assertEquals("catalog version set", container.getCatalogVersion(), converted.getCatalogVersion());
				Assert.assertEquals("original set", master, converted.getOriginal());
				Assert.assertEquals("original dataPK set", master.getDataPK(), converted.getOriginalDataPK());

				Assert.assertEquals("mimetype correctly set", format.getMimeType(), converted.getMime());

				Assert.assertEquals("master stays as is", master, container.getMaster());

				Assert.assertEquals("Media available in format now", converted, this.mediaService.getMediaByFormat(container, format));
			}

			final Collection<MediaModel> converted = this.mediaConversionService.getConvertedMedias(container);
			Assert.assertNotNull("converted medias available", converted);
			Assert.assertFalse("converted medias do not contain master", converted.contains(master));
			Assert.assertEquals("amount of converted medias", FORMAT_MIMES.length, converted.size());
		}
	}

	@Test
	public void testResizing()
	{
		final Map<TestResize, ConversionMediaFormatModel> formats = TestResize.create(this.modelService);

		String prefix;
		for (final TestMedia media : TestMedia.values())
		{
			prefix = media.name() + ": ";
			final MediaContainerModel container = this.createContainer("testResizing_" + media.name());
			final MediaModel master = media.createMedia(this.modelService, this.mediaService, container);
			this.mediaMetaDataService.extractAllMetaData(master);
			final Map<String, String> masterMetaData = this.mediaMetaDataService.getMetaData(master, "image");
			Assert.assertTrue(prefix + "Master media data has width.", masterMetaData.containsKey("width"));
			Assert.assertTrue(prefix + "Master media data has height.", masterMetaData.containsKey("height"));
			final int masterWidth = Integer.parseInt(masterMetaData.get("width"));
			final int masterHeight = Integer.parseInt(masterMetaData.get("height"));

			for (final Map.Entry<TestResize, ConversionMediaFormatModel> entry : formats.entrySet())
			{
				prefix = media.name() + "/" + entry.getKey() + ": ";
				final MediaModel resized = this.mediaConversionService.getOrConvert(container, entry.getValue());
				Assert.assertNotNull(prefix + "Resized to '" + entry.getKey(), resized);
				Assert.assertNotNull(prefix + "resized media has data pk.", resized.getDataPK());
				Assert.assertEquals(prefix + "resized media has same mimetype.", master.getMime(), resized.getMime());
				Assert.assertEquals(prefix + "resized media correct original.", master, resized.getOriginal());
				Assert.assertEquals(prefix + "resized media correct original data pk.", master.getDataPK(),
						resized.getOriginalDataPK());

				// check size
				this.mediaMetaDataService.extractAllMetaData(resized);
				final Map<String, String> resizedMetaData = this.mediaMetaDataService.getMetaData(resized, "image");
				Assert.assertTrue(prefix + "Master media data has width.", resizedMetaData.containsKey("width"));
				Assert.assertTrue(prefix + "Master media data has height.", resizedMetaData.containsKey("height"));
				final int resizedWidth = Integer.parseInt(resizedMetaData.get("width"));
				final int resizedHeight = Integer.parseInt(resizedMetaData.get("height"));
				// add a tolerance of 1 pixel due to rounding differences...
				Assert.assertTrue(
						prefix + "resized to correct width. " + "Expected " + entry.getKey().expectedWidth(masterWidth, masterHeight)
								+ "; Actual: " + resizedWidth,
						Math.abs(entry.getKey().expectedWidth(masterWidth, masterHeight) - resizedWidth) <= 1);
				Assert.assertTrue(
						prefix + "resized to correct height. " + "Expected " + entry.getKey().expectedHeight(masterWidth, masterHeight)
								+ "; Actual: " + resizedHeight,
						Math.abs(entry.getKey().expectedHeight(masterWidth, masterHeight) - resizedHeight) <= 1);
			}
		}
	}

	static enum TestResize
	{
		HALFSIZE("-resize 50%")
		{
			@Override
			int expectedWidth(final int width, final int height)
			{
				return (int) (width / 2.0);
			}

			@Override
			int expectedHeight(final int width, final int height)
			{
				return (int) (height / 2.0);
			}
		},
		ROTATE("-rotate 90")
		{
			@Override
			int expectedWidth(final int width, final int height)
			{
				return height;
			}

			@Override
			int expectedHeight(final int width, final int height)
			{
				return width;
			}
		},
		ROTATE_SMALLSIZE("-resize 20% -rotate 90")
		{
			@Override
			int expectedWidth(final int width, final int height)
			{
				return (int) (height * 0.2);
			}

			@Override
			int expectedHeight(final int width, final int height)
			{
				return (int) (width * 0.2);
			}
		},
		THUMBNAIL("-resize 42x42")
		{

			@Override
			int expectedWidth(final int width, final int height)
			{
				return (int) (width * this.factor(width, height));
			}

			@Override
			int expectedHeight(final int width, final int height)
			{
				return (int) (height * this.factor(width, height));
			}

			private double factor(final int width, final int height)
			{
				return 42.0 / Math.max(width, height);
			}
		};

		private final String conversion;

		private TestResize(final String conv)
		{
			this.conversion = conv;
		}

		String getConversion()
		{
			return conversion;
		}

		abstract int expectedWidth(int width, int height);

		abstract int expectedHeight(int width, int height);

		static Map<TestResize, ConversionMediaFormatModel> create(final ModelService modelService)
		{
			final Map<TestResize, ConversionMediaFormatModel> ret = new EnumMap<TestResize, ConversionMediaFormatModel>(
					TestResize.class);
			for (final TestResize resize : TestResize.values())
			{
				final ConversionMediaFormatModel format = modelService.create(ConversionMediaFormatModel.class);
				format.setConversion(resize.getConversion());
				format.setQualifier(resize.name());
				modelService.save(format);
				ret.put(resize, format);
			}
			return ret;
		}
	}
}
