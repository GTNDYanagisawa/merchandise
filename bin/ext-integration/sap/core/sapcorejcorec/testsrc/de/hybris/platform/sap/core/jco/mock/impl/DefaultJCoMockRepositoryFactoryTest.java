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
/**
 * 
 */
package de.hybris.platform.sap.core.jco.mock.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.sap.conn.jco.JCoTable;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepositoryFactory;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.core.jco.rec.SapcoreJCoRecJUnitTest;
import de.hybris.platform.util.Utilities;


/**
 * Test class for {@link DefaultJCoMockRepositoryFactory}.
 */
@UnitTest
public class DefaultJCoMockRepositoryFactoryTest extends SapcoreJCoRecJUnitTest
{

	@Resource
	private JCoMockRepositoryFactory classUnderTest;

	/**
	 * Tests simple mocking of a datastructure.
	 * 
	 * Uses file DefaultJCoMockRepositoryFactoryTest.testSimpleMocking.xml for defining testdata.
	 * 
	 * @throws JCoRecException
	 *            JCoRecException
	 * @throws URISyntaxException
	 *            URISyntaxException
	 * @throws IOException
	 *            IOException
	 */
	@Test
	public void testSimpleMocking() throws JCoRecException, URISyntaxException, IOException
	{


		Assert.assertNotNull(classUnderTest);

		final File file = getTestDataFile("testsrc/de/hybris/platform/sap/core/jco/mock/impl/DefaultJCoMockRepositoryFactoryTest.testSimpleMocking.xml");

		final JCoMockRepository mockRepository = classUnderTest.getMockRepository(file);
		Assert.assertNotNull(mockRepository);

		final JCoTable table = mockRepository.getTable("TT_TEST");

		Assert.assertNotNull(table);
		table.firstRow();

		Assert.assertEquals("Hello", table.getString("TESTSTRING"));
		Assert.assertEquals(42, table.getLong("TESTNUMBER"));
		table.nextRow();

		Assert.assertEquals("World", table.getString("TESTSTRING"));
		Assert.assertEquals(24, table.getLong("TESTNUMBER"));

	}

	/**
	 * Reads a file from the directory.
	 * 
	 * @param filename
	 *           filename
	 * @return file
	 * @throws IOException
	 *            IOException
	 */
	private File getTestDataFile(final String filename) throws IOException
	{
		final File extensionDirectory = Utilities.getExtensionInfo("sapcorejcorec").getExtensionDirectory();
		final String filePath = extensionDirectory.getCanonicalPath() + File.separator + filename;

		final File file = new File(filePath);
		if (!file.exists())
		{
			Assert.fail("Testfail does not exist");
		}
		return file;

	}

}
