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
package de.hybris.platform.acceleratorservices.dataimport.batch.task.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.AbstractImpexRunnerTask;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.CSVConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link AbstractImpexRunnerTask}
 */
@UnitTest
public class ImpexRunnerTaskTest
{

	@Mock
	private File file;
	private BatchHeader header;
	private AbstractImpexRunnerTask task;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		task = new AbstractImpexRunnerTask()
		{
			@Override
			public SessionService getSessionService()
			{
				return null;
			}

			@Override
			public ImportService getImportService()
			{
				return null;
			}

			@Override
			public ImportConfig getImportConfig()
			{
				return null;
			}
		};
		header = new BatchHeader();
		header.setFile(file);
		header.setEncoding(CSVConstants.HYBRIS_ENCODING);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMissingHeader() throws UnsupportedEncodingException, FileNotFoundException
	{
		task.execute(null);
	}

	@Test
	public void testMissingFile() throws UnsupportedEncodingException, FileNotFoundException
	{
		header.setFile(null);
		task.execute(header);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMissingEncoding() throws UnsupportedEncodingException, FileNotFoundException
	{
		header.setEncoding(null);
		task.execute(header);
	}

}
