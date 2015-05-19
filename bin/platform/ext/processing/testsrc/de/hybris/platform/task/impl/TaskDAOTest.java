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
package de.hybris.platform.task.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;


@IntegrationTest
public class TaskDAOTest extends HybrisJUnit4Test
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(TaskDAOTest.class.getName());
	private TaskDAO dao;

	private String getTablePrefix()
	{
		return jaloSession.getTenant().getDataSource().getTablePrefix();
	}

	@Test
	public void testConditionConsumeQuery()
	{
		dao = new TaskDAO(jaloSession.getTenant());
		Assert.assertEquals("UPDATE " + getTablePrefix() + "taskconditions SET p_consumed = ? WHERE p_task = ?",
				dao.getConditionConsumeQuery());
	}

	@Test
	public void testConditionFetchQuery()
	{
		dao = new TaskDAO(jaloSession.getTenant());
		Assert.assertEquals("SELECT PK FROM " + getTablePrefix() + "taskconditions WHERE p_uniqueid = ?",
				dao.getConditionFetchQuery());
	}

	@Test
	public void testConditionFulfillQuery()
	{
		dao = new TaskDAO(jaloSession.getTenant());
		Assert.assertEquals("UPDATE " + getTablePrefix()
				+ "taskconditions SET p_fulfilled = ? WHERE p_uniqueid = ? AND p_consumed = ?", dao.getConditionFulfillQuery());
	}

	@Test
	public void testConditionMatchQuery()
	{
		dao = new TaskDAO(jaloSession.getTenant());
		Assert.assertEquals("UPDATE " + getTablePrefix() + "taskconditions SET p_task = ? WHERE p_task IS NULL AND p_uniqueid = ?",
				dao.getConditionMatchQuery());
	}

	@Test
	public void testLockQuery()
	{
		dao = new TaskDAO(jaloSession.getTenant());
		Assert.assertEquals("UPDATE " + getTablePrefix()
				+ "tasks SET p_runningonclusternode = ? WHERE p_runningonclusternode = ? AND PK = ? AND p_executiontimemillis <= ? ",
				dao.getLockQuery());
	}

	@Test
	public void testUnlockQuery()
	{
		dao = new TaskDAO(jaloSession.getTenant());
		Assert.assertEquals("UPDATE " + getTablePrefix() + "tasks SET p_runningonclusternode = ? WHERE PK = ?",
				dao.getUnlockQuery());
	}
}
