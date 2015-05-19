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
package de.hybris.platform.sap.core.jco.monitor.jaxb;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.annotation.Resource;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.monitor.JCoMonitorException;
import de.hybris.platform.sap.core.jco.test.SapcoreJCoJUnitTest;
import de.hybris.platform.sap.core.test.TestFileUtility;


/**
 * Test for {@link JCoMonitorJAXBHandlerTest}.
 */
@UnitTest
@SuppressWarnings("javadoc")
public class JCoMonitorJAXBHandlerTest extends SapcoreJCoJUnitTest
{

	@Resource(name = "sapCoreJCoMonitorJAXBHandler")
	private JCoMonitorJAXBHandler classUnderTest;

	@Override
	public void setUp()
	{
		super.setUp();

	}

	@Test
	public void testGenerateClustersAndSnapshot() throws JCoMonitorException
	{
		final File file = TestFileUtility.getFile("sapcorejco", "resources/test/jcoConnectionsClusterSnapshot.xml");
		final Nodes testNodes = classUnderTest.generateNodes(file);
		final String testSnapshot = classUnderTest.generateSnapshot(testNodes);
		final Nodes resultNodes = classUnderTest.generateNodes(testSnapshot);

		assertEquals(testNodes.getNode().size(), resultNodes.getNode().size());
		assertEquals(testNodes.getNode().get(0).getJcoConnections().getJcoConnection().size(), resultNodes.getNode().get(0)
				.getJcoConnections().getJcoConnection().size());
		assertEquals(testNodes.getNode().get(1).getJcoConnections().getJcoConnection().size(), resultNodes.getNode().get(1)
				.getJcoConnections().getJcoConnection().size());
	}
}
