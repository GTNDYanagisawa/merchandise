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
package de.hybris.platform.mediaconversion.imagemagick;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author pohl
 */
@IntegrationTest
public class ImageMagickMediaConversionStrategyTest extends HybrisJUnit4Test
{
	private static final Logger LOG = Logger.getLogger(ImageMagickMediaConversionStrategyTest.class);

	@BeforeClass
	public static void logSystem()
	{
		LOG.info("os.name: " + System.getProperty("os.name"));
		LOG.info("os.arch: " + System.getProperty("os.arch"));
	}

	private ImageMagickMediaConversionStrategy imService;

	@Override
	public void init() throws JaloSystemException
	{
		super.init();
		this.imService = Registry.getApplicationContext().getBean("imageMagickMediaConversionStrategy",
				ImageMagickMediaConversionStrategy.class);
	}

	@Test
	public void testExtractFileExtensionNull() throws Exception
	{
		Assert.assertNull("Null gives null.", ImageMagickMediaConversionStrategy.extractFileExtension(null));
	}

	@Test
	public void testExtractFileExtension() throws Exception
	{

		Assert.assertEquals("gif", ImageMagickMediaConversionStrategy.extractFileExtension("animation.gif"));
		Assert.assertEquals("jpg", ImageMagickMediaConversionStrategy.extractFileExtension("someImage.jpg"));

		Assert.assertEquals("jpg", ImageMagickMediaConversionStrategy
				.extractFileExtension("https://wiki.hybris.com/download/attachments/78316110/map.jpg"));
		Assert.assertEquals("jpg", ImageMagickMediaConversionStrategy
				.extractFileExtension("http://picture.immobilienscout24.de/files/basic003/N/115/48/7/115048007-1.jpg?1368494333"));

		Assert.assertEquals("jpg", ImageMagickMediaConversionStrategy
				.extractFileExtension("https://wiki.hybris.com/download/attachments/78316110/map.jpg?some=value&other=stuff"));
		Assert.assertEquals("jpg", ImageMagickMediaConversionStrategy
				.extractFileExtension("https://wiki.hybris.com/map.jpg;jSessionId=951651651656565165166?some=value&other=stuff"));

		Assert.assertEquals("jpg", ImageMagickMediaConversionStrategy
				.extractFileExtension("https://wiki.hybris.com/download/attachments/78316110/map.jpg?some=value&dots=.gif"));
	}

	@Test
	public void testCommandBuilding() throws Exception
	{
		Assert.assertEquals(Arrays.asList("input", "-resize", "50x50", "output"),
				this.imService.buildCommand(null, "input", "output", new DummyFormat("-resize 50x50")));
		Assert.assertEquals(Arrays.asList("input", "-resize", "50x50", "output"),
				this.imService.buildCommand(null, "input", "output", new DummyFormat("        -resize    50x50       ")));

		Assert.assertEquals(Arrays.asList("-debug", "all", "input", "-resize", "50x50", "output"),
				this.imService.buildCommand(null, "input", "output", new DummyFormat("-debug all {input} -resize 50x50 {output}")));
		Assert.assertEquals(Arrays.asList("-debug", "all", "input[0]", "-resize", "50x50", "output"),
				this.imService.buildCommand(null, "input", "output", new DummyFormat("-debug all {input}[0] -resize 50x50 {output}")));
	}

	@Test
	public void testSimpleConversion() throws Exception
	{
		final File original = ImageMagickMediaConversionStrategyTest.file("/mediaconversion/imagemagick/test/wizard/original.gif");
		final File tmp = File.createTempFile("test_", ".gif");
		Assert.assertTrue("is file", tmp.isFile());
		//        Assert.assertFalse("file exists", tmp.exists());
		//        Assert.assertFalse("can read", tmp.canRead());
		this.imService.convert(null, original, tmp, new DummyFormat("-resize 50%"));
		Assert.assertTrue("is file", tmp.isFile());
		Assert.assertTrue("file exists", tmp.exists());
		Assert.assertTrue("can read", tmp.canRead());
		Assert.assertTrue("is gone again", tmp.delete());
	}

	static File file(final String resource) throws Exception
	{
		final URL url = ImageMagickMediaConversionStrategyTest.class.getResource(resource);
		if (url == null)
		{
			throw new FileNotFoundException("Resource '" + resource + "' could not be located.");
		}
		return new File(url.toURI());
	}

	private static final class DummyFormat extends ConversionMediaFormatModel
	{
		private DummyFormat(final String conversion)
		{
			this.setConversion(conversion);
		}
	}
}
