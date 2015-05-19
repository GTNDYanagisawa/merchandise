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
package de.hybris.platform.sap.core.jco.rec.impl;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;


/**
 * Takes a repository file containing a function with a table that doesn't have any rows.
 */
public class EmptyTableTest
{

	private final File xmlFile = new File("testsrc/de/hybris/platform/sap/core/jco/rec/impl/emptytable100.xml");

	/**
	 * Try to access empty table.
	 * 
	 * @throws BackendException
	 *            BackendException.
	 */
	@Test
	public void testEmptyTable() throws BackendException
	{
		final RepositoryPlayback repoPlay = new RepositoryPlaybackFactoryImpl(xmlFile).createRepositoryPlayback();
		final ConnectionDelegator connection = new ConnectionDelegator(null, JCoRecMode.PLAYBACK, repoPlay, null);
		final JCoFunction function = connection.getFunction("F1");

		Assert.assertNotNull(function);
		final JCoParameterList list = function.getImportParameterList();
		Assert.assertNotNull(list);
		final JCoTable table = list.getTable("T1");
		Assert.assertNotNull(table);
	}
}
