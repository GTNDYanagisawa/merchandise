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
package de.hybris.platform.cockpit.reports.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.reports.model.JasperMediaModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultJasperMediaServiceTest
{
	private final DefaultJasperMediaService jasperMediaService = new DefaultJasperMediaService();

	@Mock
	private ModelService modelService;
	@Mock
	private MediaService mediaService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		jasperMediaService.setMediaService(mediaService);
		jasperMediaService.setModelService(modelService);
	}

	@Test
	public void testCreateJasperMedia()
	{
		//given
		final MediaFolderModel folder = mock(MediaFolderModel.class);
		final String code = "testCode";
		final JasperMediaModel mockJasperMedia = mock(JasperMediaModel.class);
		when(modelService.create(JasperMediaModel.class)).thenReturn(mockJasperMedia);

		//when
		final JasperMediaModel jasperMedia = jasperMediaService.createJasperMedia(folder, code);

		//then
		assertThat(jasperMedia).isNotNull();
		assertThat(jasperMedia).isSameAs(mockJasperMedia);
		verify(mockJasperMedia).setCode("testCode");
		verify(mockJasperMedia).setFolder(folder);
		verifyNoMoreInteractions(mockJasperMedia);
	}

	@Test
	public void testGetJasperReportsMediaFolder()
	{
		//given
		final MediaFolderModel mockFolder = mock(MediaFolderModel.class);
		when(mediaService.getFolder("jasperreports")).thenReturn(mockFolder);

		//when
		final MediaFolderModel folder = jasperMediaService.getJasperReportsMediaFolder();

		//then
		assertThat(folder).isNotNull();
		assertThat(folder).isSameAs(mockFolder);
	}
}
