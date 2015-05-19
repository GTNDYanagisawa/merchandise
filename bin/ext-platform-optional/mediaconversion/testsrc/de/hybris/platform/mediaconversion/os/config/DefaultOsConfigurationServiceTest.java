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
package de.hybris.platform.mediaconversion.os.config;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaconversion.mock.MockConfigurationService;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;


/**
 * @author pohl
 */
@UnitTest
public class DefaultOsConfigurationServiceTest
{
	//    @BeforeClass
	//    public static void setupLog4J() 
	//    {
	//        BasicConfigurator.configure();
	//    }

	private File file(final String resource) throws URISyntaxException
	{
		final URL url = this.getClass().getResource(resource);
		if (url == null)
		{
			throw new IllegalArgumentException("Resource not found.");
		}
		return new File(url.toURI());
	}

	@Test
	public void testRetrieveOsDirectory() throws Exception
	{
		final DefaultOsConfigurationService service = new DefaultOsConfigurationService();
		service.setConfigurationService(new MockConfigurationService());
		service.setOsName("Windows 7");
		service.setOsArch("amd64");
		final File root = this.file("/os/test/");
		Assert.assertEquals(this.file("/os/test/windows/amd64/"), service.retrieveOsDirectory(root));
		service.setOsName("Linux");
		Assert.assertEquals(this.file("/os/test/linux/x86_64/"), service.retrieveOsDirectory(root));
		service.setOsArch("i86");
		Assert.assertEquals(this.file("/os/test/linux/i386/"), service.retrieveOsDirectory(root));
		service.setOsName("MacOs X");
		Assert.assertEquals(this.file("/os/test/mac_os_x/amd64/"), service.retrieveOsDirectory(root));
		service.setOsArch("amd64");
		Assert.assertEquals(this.file("/os/test/mac_os_x/amd64/"), service.retrieveOsDirectory(root));
		service.setOsName("Mac Os X");
		Assert.assertEquals(this.file("/os/test/mac_os_x/amd64/"), service.retrieveOsDirectory(root));
	}


	@Test
	public void testMatch()
	{
		// OSs

		{
			final File[] files = new File[]
			{ new File("windows"), new File("linux"), new File("mac_os_x") };
			Assert.assertEquals(files[0], DefaultOsConfigurationService.match("windows", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[0], DefaultOsConfigurationService.match("windows 7", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[0], DefaultOsConfigurationService.match("windows xp", files, FileNameExtractor.INSTANCE));

			Assert.assertEquals(files[1], DefaultOsConfigurationService.match("linux", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[1], DefaultOsConfigurationService.match("linux OS", files, FileNameExtractor.INSTANCE));

			Assert.assertEquals(files[2], DefaultOsConfigurationService.match("mac os", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[2], DefaultOsConfigurationService.match("macos", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[2], DefaultOsConfigurationService.match("mac os x", files, FileNameExtractor.INSTANCE));
		}
		// Archs

		{
			final File[] files = new File[]
			{ new File("i386"), new File("amd64") };
			Assert.assertEquals(files[0], DefaultOsConfigurationService.match("x86", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[0], DefaultOsConfigurationService.match("i386", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[0], DefaultOsConfigurationService.match("intel386", files, FileNameExtractor.INSTANCE));

			Assert.assertEquals(files[1], DefaultOsConfigurationService.match("amd64", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[1], DefaultOsConfigurationService.match("x86_64", files, FileNameExtractor.INSTANCE));
		}

		// Archs other

		{
			final File[] files = new File[]
			{ new File("i386"), new File("x86_64") };
			Assert.assertEquals(files[0], DefaultOsConfigurationService.match("x86", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[0], DefaultOsConfigurationService.match("i386", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[0], DefaultOsConfigurationService.match("intel386", files, FileNameExtractor.INSTANCE));

			Assert.assertEquals(files[1], DefaultOsConfigurationService.match("amd64", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[1], DefaultOsConfigurationService.match("x86_64", files, FileNameExtractor.INSTANCE));
			Assert.assertEquals(files[1], DefaultOsConfigurationService.match("i386_64", files, FileNameExtractor.INSTANCE));
		}
	}
}
