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
package de.hybris.platform.importcockpit.services.media.impl;

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.importcockpit.model.ImportCockpitInputMediaModel;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.util.CSVReader;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultImportCockpitMediaServiceTest
{
	@Mock
	private DefaultImportCockpitMediaService defaultImportCockpitMediaService;

	@Mock
	private CSVReader csvReader;

	private ImportCockpitInputMediaModel media;

	@Before
	public void setUp() throws UnsupportedEncodingException, JaloBusinessException
	{
		MockitoAnnotations.initMocks(this);
		media = new ImportCockpitInputMediaModel(ItemContextBuilder.createDefaultBuilder(ImportCockpitInputMediaModel.class)
				.build());
		Mockito.when(defaultImportCockpitMediaService.createMediaReader(media)).thenReturn(csvReader);
		Mockito.when(Boolean.valueOf(defaultImportCockpitMediaService.validateInputFile(media))).thenCallRealMethod();
	}

	@Test
	public void testValidateInputFile()
	{
		Assert.assertFalse(defaultImportCockpitMediaService.validateInputFile(null));
		verify(csvReader, Mockito.never()).readNextLine();

		defaultImportCockpitMediaService.validateInputFile(media);
		Assert.assertTrue(defaultImportCockpitMediaService.validateInputFile(media));
	}

}
