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
package de.hybris.platform.catalog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.model.classification.ClassificationImportCronJobModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;


/**
 *
 */
public class ClassificationImportCronJobPreparerTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;

	@Test
	public void testClassificationImportCronJobWorks()
	{

		try
		{
			final Language lan = C2LManager.getInstance().getLanguageByIsoCode("en");

			final ItemModel modelItem = new ClassificationImportCronJobModel();//modelService.create(ClassificationImportCronJobModel.class);
			assertTrue(modelItem instanceof ClassificationImportCronJobModel);
			((ClassificationImportCronJobModel) modelItem).setActive(Boolean.TRUE);
			((ClassificationImportCronJobModel) modelItem).setSingleExecutable(Boolean.TRUE);
			((ClassificationImportCronJobModel) modelItem).setChangeRecordingEnabled(Boolean.FALSE);
			((ClassificationImportCronJobModel) modelItem).setEnableCodeExecution(Boolean.TRUE);
			final UserModel userModel = modelService.toModelLayer(UserManager.getInstance().getAdminEmployee());
			((ClassificationImportCronJobModel) modelItem).setSessionUser(userModel);
			final LanguageModel lanModel = modelService.toModelLayer(lan);

			((ClassificationImportCronJobModel) modelItem).setSessionLanguage(lanModel);
			((ClassificationImportCronJobModel) modelItem).setLanguage(lanModel);
			((ClassificationImportCronJobModel) modelItem).setClassificationSystem("eclass");
			((ClassificationImportCronJobModel) modelItem).setVersion("1.0");
			((ClassificationImportCronJobModel) modelItem).setCode("ClassificationImportCronJob-" + System.currentTimeMillis());

			modelService.save(modelItem);
			assertNotNull(((ClassificationImportCronJobModel) modelItem).getJob());
			//System.out.println(modelService.getSource(modelItem));

		}
		finally
		{
			//
		}
	}

	@Test
	public void testClassificationImportCronJobCreateModelFails()
	{

		try
		{
			final Language lan = C2LManager.getInstance().getLanguageByIsoCode("en");

			final ItemModel modelItem = modelService.create(ClassificationImportCronJobModel.class);
			assertTrue(modelItem instanceof ClassificationImportCronJobModel);
			((ClassificationImportCronJobModel) modelItem).setActive(Boolean.TRUE);
			((ClassificationImportCronJobModel) modelItem).setSingleExecutable(Boolean.TRUE);
			((ClassificationImportCronJobModel) modelItem).setChangeRecordingEnabled(Boolean.FALSE);
			((ClassificationImportCronJobModel) modelItem).setEnableCodeExecution(Boolean.TRUE);
			final UserModel userModel = modelService.toModelLayer(UserManager.getInstance().getAdminEmployee());
			((ClassificationImportCronJobModel) modelItem).setSessionUser(userModel);
			final LanguageModel lanModel = modelService.toModelLayer(lan);

			((ClassificationImportCronJobModel) modelItem).setSessionLanguage(lanModel);
			((ClassificationImportCronJobModel) modelItem).setLanguage(lanModel);
			((ClassificationImportCronJobModel) modelItem).setClassificationSystem("eclass");
			((ClassificationImportCronJobModel) modelItem).setVersion("1.0");
			((ClassificationImportCronJobModel) modelItem).setCode("ClassificationImportCronJob-" + System.currentTimeMillis());

			modelService.save(modelItem);
			assertNotNull(((ClassificationImportCronJobModel) modelItem).getJob());
			//			System.out.println(modelService.getSource(modelItem));

		}
		finally
		{
			//
		}
	}
}
