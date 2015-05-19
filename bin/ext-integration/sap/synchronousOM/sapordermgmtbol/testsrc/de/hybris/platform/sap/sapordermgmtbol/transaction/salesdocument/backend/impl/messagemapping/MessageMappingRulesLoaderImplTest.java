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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRulesContainerImpl.Key;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;


@UnitTest
@SuppressWarnings("javadoc")
public class MessageMappingRulesLoaderImplTest
{


	public MessageMappingRulesLoaderImpl classUnderTest;


	@Before
	public void setUp()
	{
		classUnderTest = new MessageMappingRulesLoaderImpl();
	}

	@Test
	public void testloadMessageMappingRulesContainerFile()
	{
		assertNotNull(classUnderTest.loadMessageMappingRulesContainerFile());
	}

	@Test
	public void test() throws BackendRuntimeException, SAXException, IOException
	{
		final Map<Key, List<MessageMappingRule>> rules = classUnderTest.loadRules();
		assertNotNull(rules);
		assertTrue(rules.size() > 0);

	}

	@Test
	public void testDuplicatePatterns() throws SAXException, IOException
	{

		classUnderTest.setMessageMappingFileInputStream(new ByteArrayInputStream(
				MessageMappingRulesParserTest.XML_FILE_CONTENT_DUBLICATES.getBytes()));

		try
		{
			classUnderTest.loadRules();
			fail("expected BackendRuntimeException.class reporting dublicates");
		}
		catch (final BackendRuntimeException e)
		{
			// success
			final String msg = e.getMessage();
			// number of duplicate rules
			assertEquals(MessageMappingRulesParserTest.DUBLICATE_RULES_SET.length / 2, msg.length()
					- msg.replaceAll("\n", "").length());
		}
	}





}
