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
package de.hybris.platform.importcockpit.components.contentbrowser.mapping.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.importcockpit.model.ImportCockpitCronJobModel;
import de.hybris.platform.importcockpit.model.mappingview.MappingModel;
import de.hybris.platform.importcockpit.services.mapping.ImportCockpitMappingService;
import de.hybris.platform.importcockpit.session.mapping.impl.MappingBrowserModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.zkoss.zul.Textbox;

@UnitTest
public class DefaultSaveMappingComponentTest
{

	private static final String MAPPING_NAME = "theName";

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private DefaultSaveMappingComponent saveMappingComponent;

	@Mock
	private ImportCockpitMappingService mappingService;

	@Mock
	private Textbox mappingTextBox;

	@Mock
	private MappingBrowserModel mappingBrowserModel;

	@Mock
	private ImportCockpitCronJobModel importJob;

	@Mock
	private MappingModel mapping;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		saveMappingComponent.setMappingService(mappingService);
		when(saveMappingComponent.getMappingNameBox()).thenReturn(mappingTextBox);
		when(mappingTextBox.getText()).thenReturn(MAPPING_NAME);
		when(saveMappingComponent.getMapping()).thenReturn(mapping);
		when(saveMappingComponent.getBrowserModel()).thenReturn(mappingBrowserModel);
		when(mappingBrowserModel.getImportCockpitCronJob()).thenReturn(importJob);
		when(Boolean.valueOf(saveMappingComponent.isSaveAs())).thenReturn(Boolean.FALSE);
	}

	@Test
	public void testUpdateModel() throws InterruptedException
	{
		doNothing().when(mappingService).validateAndSaveMapping(Mockito.<MappingModel> any(), Mockito.<String> any(),
				Mockito.<ImportCockpitCronJobModel> any());
		saveMappingComponent.updateModel();
		verify(mappingService).validateAndSaveMapping(mapping, MAPPING_NAME, importJob, false);
		verify(mappingBrowserModel).setNewMapping(false);
	}

}
