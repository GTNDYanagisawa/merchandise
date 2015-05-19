/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.mobileservices;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.mobileservices.facade.Code2DService;
import de.hybris.platform.mobileservices.facade.DecodeBarcodeException;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * The Class TestQRCodes.
 */
public class QRCodesTest extends ServicelayerTest
{

	/** The code2d service. */
	@Resource
	private Code2DService code2dService;

	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(QRCodesTest.class.getName());

	/** The google1. */
	private BufferedImage google1 = null;

	/** The google2. */
	private BufferedImage google2 = null;

	/** The google3. */
	private BufferedImage google3 = null;

	/**
	 * Sets the up.
	 * @throws Exception
	 *            the exception
	 */
	@Before
	public void setUp() throws Exception
	{

		google1 = ImageIO.read(new ClassPathResource("/test/qrgoogle1.png").getFile());
		google2 = ImageIO.read(new ClassPathResource("/test/qrgoogle2.png").getFile());
		google3 = ImageIO.read(new ClassPathResource("/test/qrgoogle3.png").getFile());


	}



	/**
	 * Test envis ready.
	 * @throws Exception
	 *            the exception
	 */
	@Test
	public void testEnvisReady() throws Exception
	{
		assertNotNull("images loaded", google1);
		assertNotNull("images loaded", google2);
		assertNotNull("images loaded", google3);
	}

	/**
	 * Decode test1.
	 * @throws DecodeBarcodeException
	 * @throws Exception
	 *            the exception
	 */
	@Test
	public void decodeTest1() throws DecodeBarcodeException
	{
		final String result = code2dService.decodeQrCode(google1);
		LOG.info("Decoded:" + result);
		assertTrue("decode fail", "http://www.google.com".equals(result));
	}

	/**
	 * Decode test2.
	 * @throws DecodeBarcodeException
	 * @throws Exception
	 *            the exception
	 */
	@Test
	public void decodeTest2() throws DecodeBarcodeException
	{
		final String result = code2dService.decodeQrCode(google2);
		LOG.info("Decoded:" + result);
		assertTrue("decode fail", "http://www.google.com".equals(result));
	}

	/**
	 * Decode test3.
	 * @throws DecodeBarcodeException
	 * @throws Exception
	 *            the exception
	 */
	@Test
	public void decodeTest3() throws DecodeBarcodeException
	{
		final String result = code2dService.decodeQrCode(google3);
		LOG.info("Decoded:" + result);
		assertTrue("decode fail", "http://www.google.com".equals(result));
	}

	/**
	 * Encode test.
	 * @throws Exception
	 *            the exception
	 */
	@Test
	public void encodeTest() throws Exception // NOPMD by willy on 24/11/09 12:11
	{
		final BufferedImage google = code2dService.encodeQrCode("http://www.google.com", google3.getWidth(), google3.getHeight());
		assertNotNull("encode fails", google);
		try
		{
			ImageIO.write(google, "png", File.createTempFile("outputqrgoogle3", "png"));

		}
		catch (final Exception x)
		{
			LOG.info("Could not write generated encoded image to filesystem");
		}
		final String result = code2dService.decodeQrCode(google);
		assertTrue("encode fails", "http://www.google.com".equals(result));
	}


}
