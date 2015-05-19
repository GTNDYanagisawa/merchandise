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
package de.hybris.platform.importcockpit.components.listview.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.components.listview.ListViewAction.Context;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.importcockpit.model.ImportCockpitCronJobModel;
import de.hybris.platform.importcockpit.services.cronjob.ImportCockpitCronJobService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AbstractJobListViewActionTest
{
	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private AbstractJobListViewAction action;

	@Mock
	private ImportCockpitCronJobService importCockpitCronJobService;

	@Mock
	private Context context;

	@Mock
	private TypedObject contextItem;

	@Before
	public void seetUp()
	{
		MockitoAnnotations.initMocks(this);
		when(Boolean.valueOf(importCockpitCronJobService.isRunning(Matchers.<CronJobModel> any()))).thenReturn(Boolean.TRUE);
		action.setImportCockpitCronJobService(importCockpitCronJobService);
	}

	@Test
	public void testIsRunning()
	{
		when(context.getItem()).thenReturn(contextItem);
		when(contextItem.getObject()).thenReturn(Matchers.<ImportCockpitCronJobModel> any());
		action.isRunning(context);
		verify(importCockpitCronJobService).isRunning(Matchers.<CronJobModel> any());
		verify(context).getItem();
		verify(contextItem).getObject();
	}
}
