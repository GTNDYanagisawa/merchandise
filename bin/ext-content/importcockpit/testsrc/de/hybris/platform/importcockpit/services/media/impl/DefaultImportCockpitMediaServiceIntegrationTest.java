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

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.importcockpit.model.ImportCockpitCronJobModel;
import de.hybris.platform.importcockpit.model.ImportCockpitInputMediaModel;
import de.hybris.platform.importcockpit.model.ImportCockpitJobModel;
import de.hybris.platform.importcockpit.model.mappingview.SourceColumnModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DefaultImportCockpitMediaServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private DefaultImportCockpitMediaService defaultImportCockpitMediaService;

	@Resource
	private MediaService mediaService;

	@Resource
	private ModelService modelService;

	private InputStream input;

	private InputStream emptyInput;

	private ImportCockpitInputMediaModel media;

	private ImportCockpitCronJobModel icCronJobModel;

	@Before
	public void setUp() throws ImpExException, FileNotFoundException
	{
		//importCsv("/test/ProductTest.csv", "utf-8");
		media = new ImportCockpitInputMediaModel();
		media.setCode("testImportMedia");

		modelService.save(media);
		input = ServicelayerTest.class.getResourceAsStream("/test/ProductTest.csv");
		emptyInput = ServicelayerTest.class.getResourceAsStream("/test/ProductTest_Empty.csv");
		mediaService.setStreamForMedia(media, input);

		final ImportCockpitJobModel jobModel = new ImportCockpitJobModel();
		jobModel.setCode("testJobModel");

		icCronJobModel = new ImportCockpitCronJobModel();
		icCronJobModel.setCode("testCronJobModel");
		icCronJobModel.setInputMedia(media);
		icCronJobModel.setJob(jobModel);
		modelService.save(icCronJobModel);


	}

	@Test
	public void testGetSourceColumnNames()
	{
		List<String> columnNames = defaultImportCockpitMediaService.getSourceColumnNames(media);
		Assert.assertNotNull(columnNames);
		Assert.assertEquals(11, columnNames.size());
		Assert.assertTrue(columnNames.contains("Product name"));

		mediaService.setStreamForMedia(media, emptyInput);
		columnNames = defaultImportCockpitMediaService.getSourceColumnNames(media);
		Assert.assertNotNull(columnNames);
		Assert.assertTrue(columnNames.isEmpty());
	}

	@Test
	public void testGetSourceColumnData()
	{
		List<SourceColumnModel> columns = defaultImportCockpitMediaService.getSourceColumnData(icCronJobModel);
		Assert.assertNotNull(columns);

		mediaService.setStreamForMedia(media, emptyInput);
		columns = defaultImportCockpitMediaService.getSourceColumnData(icCronJobModel);
		Assert.assertNotNull(columns);
		Assert.assertTrue(columns.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateImpExImportCockpitMedia()
	{
		defaultImportCockpitMediaService.createImpExImportCockpitMedia("");

	}
}
