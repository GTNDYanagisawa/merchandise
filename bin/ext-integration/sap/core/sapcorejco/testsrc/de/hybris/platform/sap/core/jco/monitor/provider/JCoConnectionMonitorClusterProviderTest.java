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

package de.hybris.platform.sap.core.jco.monitor.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;
import de.hybris.platform.sap.core.jco.monitor.JCoMonitorException;
import de.hybris.platform.sap.core.jco.monitor.jaxb.JCoMonitorJAXBHandler;
import de.hybris.platform.sap.core.jco.monitor.jaxb.Nodes;
import de.hybris.platform.sap.core.jco.test.SapcoreJCoJUnitTest;
import de.hybris.platform.sap.core.test.TestFileUtility;


/**
 * Test class for JCoConnectionMonitorLocalProviderTest.
 */
@UnitTest
@ContextConfiguration(locations =
{ "../JCoConnectionMonitorTest-spring.xml" })
@SuppressWarnings("javadoc")
public class JCoConnectionMonitorClusterProviderTest extends SapcoreJCoJUnitTest
{

	@Resource(name = "sapCoreJCoMonitorJAXBHandler")
	private JCoMonitorJAXBHandler jaxbHandler;

	@Resource(name = "sapCoreJCoConnectionMonitorClusterProvider")
	private JCoConnectionMonitorClusterProvider classUnderTest;

	private static final long snapshotTimestamp = 1412176327000l;


	@Override
	public void setUp()
	{
		try
		{
			classUnderTest.initializeCache(getSnapshotsPerCluster(), snapshotTimestamp);
		}
		catch (JCoMonitorException | IOException e)
		{
			throw new CoreBaseRuntimeException("Exception when initializing cluster provider: " + e.getMessage(), e);
		}
	}

	@Override
	public void tearDown()
	{
		super.tearDown();
		classUnderTest.reset();
	}

	@Test
	public void testGetSnapshotXML() throws JCoMonitorException, IOException
	{
		final String snapshotXML = classUnderTest.getSnapshotXML(getSnapshotsPerCluster(), snapshotTimestamp);
		assertNotNull(snapshotXML);
		final File file = TestFileUtility.getFile("sapcorejco", "resources/test/jcoConnectionsClusterSnapshot.xml");
		final Nodes clusters = jaxbHandler.generateNodes(file);
		final String testSnapshotXML = jaxbHandler.generateSnapshot(clusters);
		assertEquals(testSnapshotXML, snapshotXML);
	}

	@Test
	public void testCreateSnapshotFile() throws JCoMonitorException, IOException
	{
		final File snapshotFile = classUnderTest.createSnapshotFile(getSnapshotsPerCluster(), snapshotTimestamp);
		assertNotNull(snapshotFile);
		assertTrue(snapshotFile.exists());
		Nodes clusters = jaxbHandler.generateNodes(snapshotFile);
		final String snapshot = jaxbHandler.generateSnapshot(clusters);
		final File file = TestFileUtility.getFile("sapcorejco", "resources/test/jcoConnectionsClusterSnapshot.xml");
		clusters = jaxbHandler.generateNodes(file);
		final String testSnapshot = jaxbHandler.generateSnapshot(clusters);
		assertEquals(testSnapshot, snapshot);
	}

	@Test
	public void testGetNodesCount()
	{
		final Integer nodesCount = classUnderTest.getNodesCount();
		assertEquals(2, nodesCount.intValue());
	}

	@Test
	public void testGetNodesWithoutResultCount()
	{
		final Integer nodesResultCount = classUnderTest.getNodesWithoutResultCount();
		assertEquals(0, nodesResultCount.intValue());
	}

	@Test
	public void testGetTotalCount()
	{
		final Integer totalCount = classUnderTest.getTotalCount();
		assertEquals(7, totalCount.intValue());
	}

	@Test
	public void testGetLongRunnerCount()
	{
		final Integer longLifetimeCount = classUnderTest.getLongRunnerCount();
		assertEquals(2, longLifetimeCount.intValue());
	}

	@Test
	public void testGetPoolLimitReachedCount()
	{
		final Integer poolLimitReached = classUnderTest.getPoolLimitReachedCount();
		assertEquals(1, poolLimitReached.intValue());
	}

	private Map<Integer, String> getSnapshotsPerCluster() throws IOException
	{
		final HashMap<Integer, String> snapshots = new HashMap<Integer, String>();
		snapshots.put(0, readFile("resources/test/jcoConnectionsSnapshotCluster0.xml"));
		snapshots.put(1, readFile("resources/test/jcoConnectionsSnapshotCluster1.xml"));
		return snapshots;
	}

	@SuppressWarnings("resource")
	private String readFile(final String filename) throws IOException
	{
		final File file = TestFileUtility.getFile("sapcorejco", filename);
		BufferedReader bufferedReader;
		bufferedReader = new BufferedReader(new FileReader(file));
		final StringBuilder sb = new StringBuilder();
		String line = bufferedReader.readLine();
		while (line != null)
		{
			sb.append(line);
			sb.append(System.lineSeparator());
			line = bufferedReader.readLine();
		}
		return sb.toString();
	}

}
