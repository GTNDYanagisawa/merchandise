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
 */
package com.hybris.datahub.core.facades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import com.hybris.datahub.core.dto.ItemImportTaskData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultItemImportTaskRunningFacadeUnitTest
{
	@Mock
	private ModelService modelService;

	@Mock
	private TaskService taskService;

	final private DefaultItemImportTaskRunningFacade facade = new DefaultItemImportTaskRunningFacade();

	@Before
	public void setUp()
	{
		facade.setModelService(modelService);
		facade.setTaskService(taskService);
	}

	@Test
	public void testScheduleImportTask()
	{
		final ItemImportTaskData data = Mockito.mock(ItemImportTaskData.class);

		final TaskModel task = Mockito.mock(TaskModel.class);
		Mockito.doReturn(task).when(modelService).create(TaskModel.class);

		facade.scheduleImportTask(data);

		Mockito.verify(modelService).create(TaskModel.class);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScheduleImportTas_kWithNullItemImportTaskData()
	{
		facade.scheduleImportTask(null);
	}
}
