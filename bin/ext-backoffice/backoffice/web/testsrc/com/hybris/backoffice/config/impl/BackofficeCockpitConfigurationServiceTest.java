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
package com.hybris.backoffice.config.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * @author johannesdoberer
 * 
 */
public class BackofficeCockpitConfigurationServiceTest
{
	private BackofficeCockpitConfigurationService backofficeCockpitConfigurationService; // NOPMD
	@Mock
	private MediaService mediaService;
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionService catalogVersionService; // NOPMD

	@Before
	public void setUp()
	{
		backofficeCockpitConfigurationService = new BackofficeCockpitConfigurationService();
		mediaService = Mockito.mock(MediaService.class);
		modelService = Mockito.mock(ModelService.class);
		catalogVersionService = Mockito.mock(CatalogVersionService.class);
		backofficeCockpitConfigurationService.setMediaService(mediaService);
		backofficeCockpitConfigurationService.setCatalogVersionService(catalogVersionService);
		backofficeCockpitConfigurationService.setModelService(modelService);
	}

	@Test
	public void getConfigFileInputStreamTest() throws FileNotFoundException
	{
		final InputStream inputStream = Mockito.mock(InputStream.class);
		final MediaModel media = Mockito.mock(MediaModel.class);
		media.setCode(Mockito.anyString());
		Mockito.when(mediaService.getMedia(Mockito.anyString())).thenReturn(media);
		Mockito.when(backofficeCockpitConfigurationService.getCockpitNGConfig()).thenReturn(media);
		Mockito.when(mediaService.getStreamFromMedia(media)).thenReturn(inputStream);
		Assert.assertNotNull(backofficeCockpitConfigurationService.getConfigFileInputStream());
	}

	@Test
	public void getLastModificationTest()
	{
		final MediaModel media = Mockito.mock(MediaModel.class);
		media.setCode(Mockito.anyString());
		final Date date = Mockito.mock(Date.class);
		Mockito.when(mediaService.getMedia(Mockito.anyString())).thenReturn(media);
		Mockito.when(backofficeCockpitConfigurationService.getCockpitNGConfig()).thenReturn(media);
		Mockito.when(backofficeCockpitConfigurationService.getCockpitNGConfig().getModifiedtime()).thenReturn(date);
		final long expected = Mockito.anyLong();
		Assert.assertEquals(expected, backofficeCockpitConfigurationService.getLastModification());
	}

	@Test
	public void getCockpitNGConfigTest()
	{
		final MediaModel media = Mockito.mock(MediaModel.class);
		media.setCode(Mockito.anyString());
		Mockito.when(mediaService.getMedia(Mockito.anyString())).thenReturn(media);
		Assert.assertNotNull(backofficeCockpitConfigurationService.getCockpitNGConfig());
		Assert.assertEquals(media, backofficeCockpitConfigurationService.getCockpitNGConfig());
	}

	@Test
	public void createConfigFileTest()
	{
		final CatalogUnawareMediaModel media = Mockito.mock(CatalogUnawareMediaModel.class);
		Mockito.when(modelService.create(CatalogUnawareMediaModel.class)).thenReturn(media);
		Assert.assertNotNull(backofficeCockpitConfigurationService.createConfigFile());
		Assert.assertEquals(media, backofficeCockpitConfigurationService.createConfigFile());
	}
}
