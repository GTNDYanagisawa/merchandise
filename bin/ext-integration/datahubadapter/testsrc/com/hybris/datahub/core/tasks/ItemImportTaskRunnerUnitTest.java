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
package com.hybris.datahub.core.tasks;

import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.facades.ItemImportFacade;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import junit.framework.Assert;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ItemImportTaskRunnerUnitTest
{
	private final ItemImportTaskRunner taskRunner = new ItemImportTaskRunner();

	private static final String POOL_NAME = "testpool";
	private static final Long PUBLICATION_ID = 1l;
	private static final String CALLBACK_URL = "http://localhost/callback";
	private static final byte[] IMPEX_CONTENT = "INSERT_UPDATE value, value, value".getBytes();
	private static final String USER = "user name";
	private static final String LANGUAGE = "ja";

	@Mock
	private ItemImportFacade importFacade;

	@Mock
	private SessionService sessionService;

	@Mock
	private TaskModel taskModel;

	@Mock
	private TaskService taskService;

	private ItemImportTaskData taskData;

	private java.util.Map<java.lang.String, Object> sessionAttrs;

	@Before
	public void setup()
	{
		sessionAttrs = new HashMap<java.lang.String, Object >();
		sessionAttrs.put("user", USER);
		sessionAttrs.put("language", LANGUAGE);
		taskData = new ItemImportTaskData(POOL_NAME, PUBLICATION_ID, CALLBACK_URL, IMPEX_CONTENT, sessionAttrs);

		Mockito.doReturn(taskData).when(taskModel).getContext();

		taskRunner.setImportFacade(importFacade);
		taskRunner.setSessionService(sessionService);
	}

	@Test
	public void testRun() throws Exception
	{
		taskRunner.run(taskService, taskModel);

		Mockito.verify(sessionService).closeCurrentSession();
		Mockito.verify(sessionService).setAttribute("user", USER);
		Mockito.verify(sessionService).setAttribute("language", LANGUAGE);

		Mockito.verify(importFacade).importItems(Mockito.eq(taskData.getPoolName()), Mockito.eq(taskData.getPublicationId()), Mockito.eq(taskData.getResultCallbackUrl()), any(ByteArrayInputStream.class));

		Mockito.verify(sessionService).closeCurrentSession();
	}
}
