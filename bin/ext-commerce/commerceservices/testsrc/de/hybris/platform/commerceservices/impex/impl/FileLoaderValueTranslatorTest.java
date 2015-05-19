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
package de.hybris.platform.commerceservices.impex.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import junit.framework.Assert;

import org.junit.Test;


@IntegrationTest
public class FileLoaderValueTranslatorTest extends ServicelayerTransactionalTest
{
	@Test
	public void testJarFileLoad() throws Exception
	{
		final FileLoaderValueTranslator fileLoaderValueTranslator = new FileLoaderValueTranslator();
		final String file = "jar:de.hybris.platform.commerceservices.jalo.CommerceServicesManager&/commerceservices/test/fileLoaderTranslatorTest.impex";
		final String input = fileLoaderValueTranslator.importData(file);

		// Calculate the total expected file length including operating system line terminators
		final int expectedFileLineDataLength = 1575;
		final int expectedFileNumberOfLines = 29;
		final int expectFileLineTerminatorSize = System.getProperty("line.separator").length() * expectedFileNumberOfLines;
		final int expectedFileLength = expectedFileLineDataLength + expectFileLineTerminatorSize;

		Assert.assertEquals("Invalid file length", expectedFileLength, input.length());
	}
}
