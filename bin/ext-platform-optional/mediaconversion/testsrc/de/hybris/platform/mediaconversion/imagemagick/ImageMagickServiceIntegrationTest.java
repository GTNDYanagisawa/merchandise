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

import java.io.File;
import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

/**
 * @author pohl
 */
@IntegrationTest
public class ImageMagickServiceIntegrationTest extends ServicelayerBaseTest
{

	@Resource
	private ImageMagickService imageMagickService;
	
	@Test
	public void testSimpleConvert() throws Exception
	{
		final File tmpFile=File.createTempFile("test_", ".gif");
		this.imageMagickService.convert(Arrays.asList("logo:", tmpFile.getAbsolutePath()));
		Assert.assertTrue("File created.", tmpFile.isFile());
		Assert.assertTrue("File exists.", tmpFile.exists());
		Assert.assertTrue("File is readable.", tmpFile.canRead());
		Assert.assertTrue("File is deleted.", tmpFile.delete());
	}
}
