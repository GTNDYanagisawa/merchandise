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
package de.hybris.platform.importcockpit.services.classification.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.importcockpit.daos.ImportCockpitCClassificationDao;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultImportCockpitClassificationServiceTest
{

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private DefaultImportCockpitClassificationService iccService;
	@Mock
	private ClassificationSystemVersionModel classificationSysVersion;
	@Mock
	private ImportCockpitCClassificationDao importCockpitClassificationDao;
	@Mock
	private List<ClassificationClassModel> classDaoResult;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		iccService.setImportCockpitClassificationDao(importCockpitClassificationDao);
		when(importCockpitClassificationDao.findClassificationClasses(Mockito.<ClassificationSystemVersionModel> any()))
				.thenReturn(classDaoResult);
	}

	@Test
	public void testFindClassificationClasses()
	{
		assertThat(iccService.findClassificationClasses(classificationSysVersion)).isEqualTo(classDaoResult);
		verifyZeroInteractions(classDaoResult);
	}

}
