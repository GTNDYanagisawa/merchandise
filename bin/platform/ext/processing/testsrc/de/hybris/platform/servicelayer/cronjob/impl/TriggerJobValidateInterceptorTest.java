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
package de.hybris.platform.servicelayer.cronjob.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;
import de.hybris.platform.cronjob.model.BatchJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link TriggerModel}'s {@link JobModel} assignment.
 */
@IntegrationTest
public class TriggerJobValidateInterceptorTest extends ServicelayerBaseTest
{

	private static final String TEST_TARGET_VERSION = "TEST_TARGET_VERSION";
	private static final String TEST_SOURCE_VERSION = "TEST_SOURCE_VERSION";
	private static final String TEST_CATALOG = "TEST_CATALOG";
	private static final String TEST_JOB = "TEST_JOB";

	@Resource
	private TriggerJobValidateInterceptor triggerJobValidator;

	@Resource
	private ModelService modelService;

	@Test
	public void testAssignInvalidJobModel()
	{

		final TriggerModel model = new TriggerModel();
		final JobModel jobModel = new BatchJobModel();
		jobModel.setCode(TEST_JOB);
		modelService.save(jobModel);
		model.setJob(jobModel);

		try
		{
			modelService.save(model);

		}
		catch (final ModelSavingException e)
		{
			if (!checkException(
					e,
					InterceptorException.class,
					"Assigned Job either does not implements de.hybris.platform.cronjob.jalo.TriggerableJob or is not an instance of de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel!"))
			{
				Assert.fail("Should be not able to assign not TriggerableJob or ServicelayerJobModel");
			}
		}
	}

	@Test
	public void testAssignValidServicelayerJobModel() throws Throwable
	{
		final TriggerModel model = new TriggerModel();
		final ServicelayerJobModel jobModel = new ServicelayerJobModel();
		jobModel.setCode(TEST_JOB);
		jobModel.setSpringId("TEST_ID");
		modelService.save(jobModel);
		model.setJob(jobModel);

		try
		{
			modelService.save(model);
		}
		catch (final ModelSavingException e)
		{
			if (!checkException(
					e,
					InterceptorException.class,
					"Assigned Job either does not implements de.hybris.platform.cronjob.jalo.TriggerableJob or is not an instance of de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel!"))
			{
				Assert.fail("Should be able to assign ServicelayerJobModel");
			}
		}
	}

	@Test
	public void testAssignNullModel() throws Throwable
	{
		final TriggerModel model = new TriggerModel();
		model.setJob(null);
		try
		{
			modelService.save(model);
		}
		catch (final ModelSavingException e)
		{
			if (!checkException(e, JaloInvalidParameterException.class,
					"Cannot create trigger! No value for CronJob OR Job is given. Need only one value!"))
			{
				Assert.fail("Should not be able to assign null");
			}
		}
	}

	@Test
	public void testAssignValidTriggerableJob() throws Throwable
	{
		final TriggerModel model = new TriggerModel();
		final CatalogVersionSyncJobModel jobModel = new CatalogVersionSyncJobModel();
		final CatalogModel catalog = new CatalogModel();
		catalog.setId(TEST_CATALOG);
		modelService.save(catalog);
		final CatalogVersionModel source = new CatalogVersionModel();
		source.setCatalog(catalog);
		source.setVersion(TEST_SOURCE_VERSION);
		modelService.save(source);
		jobModel.setSourceVersion(source);
		final CatalogVersionModel target = new CatalogVersionModel();
		jobModel.setTargetVersion(target);
		target.setCatalog(catalog);
		target.setVersion(TEST_TARGET_VERSION);
		modelService.save(target);
		jobModel.setCode(TEST_JOB);
		modelService.save(jobModel);
		model.setJob(jobModel);

		try
		{
			modelService.save(model);
		}
		catch (final ModelSavingException e)
		{
			if (!checkException(
					e,
					InterceptorException.class,
					"Assigned Job either does not implements de.hybris.platform.cronjob.jalo.TriggerableJob or is not an instance of de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel!"))
			{
				Assert.fail("Should be able to assign TriggerableJob");
			}
		}
	}

	private boolean checkException(final ModelSavingException e, final Class exceptionClass, final String exceptionMessage)
	{
		if (e.getCause().getClass().equals(exceptionClass))
		{
			if (exceptionClass.equals(InterceptorException.class))
			{
				return ((InterceptorException) e.getCause()).getInterceptor().equals(triggerJobValidator)
						&& ((InterceptorException) e.getCause()).getMessage().contains(exceptionMessage);
			}
			else if (exceptionClass.equals(JaloInvalidParameterException.class))
			{
				return e.getCause().getMessage().contains(exceptionMessage);
			}
		}
		return false;
	}
}
