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
import de.hybris.platform.acceleratorservices.dataimport.batch.task.HeaderSetupTask;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link HeaderSetupTask}
 */
@UnitTest
public class HeaderSetupTaskTest
{
	private static final String TEST_CATEGORY = "category";
	private static final boolean TEST_NET = true;

	@Mock
	private File file;
	private HeaderSetupTask task;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		task = new HeaderSetupTask();
		task.setCatalog(TEST_CATEGORY);
		task.setNet(TEST_NET);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testErr()
	{
		task.execute(null);
	}

	@Test
	public void testSuccess()
	{
		final BatchHeader header = task.execute(file);
		Assert.assertEquals(file, header.getFile());
		Assert.assertNull(header.getSequenceId());
		Assert.assertNull(header.getTransformedFiles());
		Assert.assertEquals(TEST_CATEGORY, header.getCatalog());
		Assert.assertEquals(TEST_NET, header.isNet());
	}
}
