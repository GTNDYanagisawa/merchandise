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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaconversion.mock.MockConfigurationService;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * @author pohl
 */
@UnitTest
public class DefaultMimeMappingStrategyTest
{
	private DefaultMimeMappingStrategy mimeMapping;

	@Before
	public void setup()
	{
		this.mimeMapping = new DefaultMimeMappingStrategy();
		this.mimeMapping.setConfigurationService(new MockConfigurationService());
	}

	private MimeMappingStrategy getMimeMappingStrategy()
	{
		return this.mimeMapping;
	}

	private void test(final String mime, final String ext) throws IOException
	{
		Assert.assertEquals("File extension for '" + mime + "'.", ext, this.getMimeMappingStrategy().fileExtensionForMimeType(mime));
		this.testFileName(mime, "test." + ext);
		this.testFileName(mime, "bla test." + ext);
		this.testFileName(mime, "c:/test." + ext);
		this.testFileName(mime, "c:/bla/test." + ext);
		this.testFileName(mime, "c:/bla/bla test." + ext);
		this.testFileName(mime, "\\bla\\bla test." + ext);
	}

	private void testFileName(final String mime, final String fileName)
	{
		Assert.assertEquals("Mime type for file name '" + fileName + "'.", mime, this.getMimeMappingStrategy()
				.determineMimeTypeByFileName(fileName));
	}

	private void testNull(final String mime, final String fileName) throws IOException
	{
		Assert.assertNull("File extension for unknown '" + mime + "'.", this.getMimeMappingStrategy()
				.fileExtensionForMimeType(mime));
		if (fileName == null)
		{
			try
			{
				this.getMimeMappingStrategy().determineMimeTypeByFileName(null);
				Assert.fail("Getting mime type for invalid file name 'null' should deliver an IllegalArgumentException.");
			}
			catch (final IllegalArgumentException e)
			{
				// OK
			}
		}
		else
		{
			this.testNullFileName(fileName);
		}
	}

	private void testNullFileName(final String fileName)
	{
		Assert.assertNull("Mime type for invalid file name '" + fileName + "'.", this.getMimeMappingStrategy()
				.determineMimeTypeByFileName(fileName));
	}

	@Test
	public void testKnown() throws IOException
	{
		this.test("image/jpeg", "jpg");
		this.test("image/gif", "gif");
		this.test("image/bmp", "bmp");
		this.test("image/png", "png");
		//        this.test("image/svg+xml", "svg");
		this.test("image/tiff", "tif");
		this.test("video/quicktime", "mov");

	}

	@Test
	public void testUnknown() throws IOException
	{
		this.testNull("someything/odd", "test");
		this.testNull("unknown", "test.blub");
		this.testNull(null, null);
	}
}
