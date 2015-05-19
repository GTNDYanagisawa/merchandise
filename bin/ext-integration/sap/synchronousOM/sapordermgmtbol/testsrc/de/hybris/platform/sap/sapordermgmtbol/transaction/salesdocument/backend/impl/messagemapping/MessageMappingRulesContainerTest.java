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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingRulesContainer;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.SAXException;


@UnitTest
@SuppressWarnings("javadoc")
public class MessageMappingRulesContainerTest extends SapordermanagmentBolSpringJunitTest
{

	@Test
	public void testUninialised()
	{
		final MessageMappingRulesContainer rc = new MessageMappingRulesContainerImpl();

		try
		{
			rc.mostNarrow(new MessageMappingRule.Pattern("anything", "999", "I"));
			fail("expected IllegalStateException.class ");
		}
		catch (final IllegalStateException e)
		{
			// success
		}
	}

	@Test
	public void testAttrPatternDegreeComparator()
	{
		final MessageMappingRulesContainerImpl.PatternDegreeDescComparator c = new MessageMappingRulesContainerImpl.PatternDegreeDescComparator();
		assertTrue(c.compare(MessageMappingRulesParserTest.RULE_AV_E_MAP_ALL, MessageMappingRulesParserTest.RULE_AV_E_ATRxxx1) > 0);
		assertTrue(c.compare(MessageMappingRulesParserTest.RULE_AV_E_ATRxxx1, MessageMappingRulesParserTest.RULE_AV_E_ATRxxx1) == 0);
		assertTrue(c.compare(MessageMappingRulesParserTest.RULE_AV_E_ATRxx1x, MessageMappingRulesParserTest.RULE_AV_E_ATRxxx1) < 0);
	}


	public void testIdentification0Rules() throws SAXException, IOException, SAPHybrisCacheException
	{
		final MessageMappingRulesContainerImpl rc = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER);
		final MessageMappingRulesLoaderImpl messageMappingRulesLoader = new MessageMappingRulesLoaderImpl();
		rc.messageMappingCacheAccess = new MockCacheAccess();
		rc.setMessageMappingRulesLoader(messageMappingRulesLoader);
		messageMappingRulesLoader.setMessageMappingFileInputStream(new ByteArrayInputStream(
				MessageMappingRulesParserTest.XML_FILE_CONTENT_0RULE.getBytes()));

		rc.initMessageMappingRulesContainer();

		assertEquals(true, rc.isHideNonErrorMsg());

		assertEquals(0, rc.rules.size());
		assertNull(rc.mostNarrow(new MessageMappingRule.Pattern("anything", "999", "I")));
	}

	@Test
	public void testIdentificationoRulesConstructor() throws SAXException, IOException, SAPHybrisCacheException
	{
		final MessageMappingRulesContainerImpl rc = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER);
		final MessageMappingRulesLoaderImpl messageMappingRulesLoader = new MessageMappingRulesLoaderImpl();
		rc.messageMappingCacheAccess = new MockCacheAccess();
		rc.setMessageMappingRulesLoader(messageMappingRulesLoader);
		messageMappingRulesLoader.setMessageMappingFileInputStream(new ByteArrayInputStream(
				MessageMappingRulesParserTest.XML_FILE_CONTENT_0RULE.getBytes()));

		rc.initMessageMappingRulesContainer();

		// no exception - success
		assertEquals(0, rc.rules.size());
		assertNull(rc.mostNarrow(new MessageMappingRule.Pattern("ANITHING", "999", "E")));
	}


	public MessageMappingRulesContainerImpl prepareMessageMappingRulesContainerWithTestRules() throws SAPHybrisCacheException
	{
		final MessageMappingRulesContainerImpl messageMappingRulesContainer = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER);
		final MessageMappingRulesLoaderImpl messageMappingRulesLoader = new MessageMappingRulesLoaderImpl();

		messageMappingRulesContainer.messageMappingCacheAccess = new MockCacheAccess();

		messageMappingRulesContainer.setMessageMappingRulesLoader(messageMappingRulesLoader);
		messageMappingRulesLoader.setMessageMappingFileInputStream(new ByteArrayInputStream(
				MessageMappingRulesParserTest.XML_FILE_CONTENT_NRULE.getBytes()));

		messageMappingRulesContainer.initMessageMappingRulesContainer();

		return messageMappingRulesContainer;

	}

	@Test
	public void testIdentificationNRulesFields() throws SAXException, IOException, SAPHybrisCacheException
	{
		final MessageMappingRulesContainerImpl messageMappingRulesContainer = prepareMessageMappingRulesContainerWithTestRules();

		assertEquals(false, messageMappingRulesContainer.isHideNonErrorMsg());

		assertEquals(MessageMappingRulesParserTest.RULE_NO_KEY,
				messageMappingRulesContainer.mostNarrow(MessageMappingRulesParserTest.RULE_NO_KEY.getPattern()));

		assertEquals(MessageMappingRulesParserTest.RULE_COMPLETE,
				messageMappingRulesContainer.mostNarrow(new MessageMappingRule.Pattern("V1", "001", "W")));

		assertEquals(MessageMappingRulesParserTest.RULE_ANY_SEVERITY,
				messageMappingRulesContainer.mostNarrow(new MessageMappingRule.Pattern("V1", "001", "E")));

	}

	@Test
	public void testIdentificationNRulesAttrs_none() throws SAXException, IOException, SAPHybrisCacheException
	{

		final MessageMappingRulesContainerImpl messageMappingRulesContainer = prepareMessageMappingRulesContainerWithTestRules();

		assertEquals(null, messageMappingRulesContainer.mostNarrow(new MessageMappingRule.Pattern(
				MessageMappingRulesParserTest.AV2SEV, MessageMappingRulesParserTest.AV2NUM, MessageMappingRulesParserTest.AV2CL,
				MessageMappingRulesParserTest.AV2ATR1, MessageMappingRulesParserTest.AV2ATR1, MessageMappingRulesParserTest.AV2ATR1,
				MessageMappingRulesParserTest.AV2ATR1)));

	}

	@Test
	public void testIdentificationNRulesAttrs_1xxx() throws SAXException, IOException, SAPHybrisCacheException
	{
		final MessageMappingRulesContainerImpl messageMappingRulesContainer = prepareMessageMappingRulesContainerWithTestRules();

		assertEquals(MessageMappingRulesParserTest.RULE_AV_E_ATR1xxx,
				messageMappingRulesContainer.mostNarrow(new MessageMappingRule.Pattern(MessageMappingRulesParserTest.AV2CL,
						MessageMappingRulesParserTest.AV2NUM, MessageMappingRulesParserTest.AV2SEV,
						MessageMappingRulesParserTest.AV2ATR1, null, null, null)));
	}

	@Test
	public void testIdentificationNRulesAttrs_xxx1() throws SAXException, IOException, SAPHybrisCacheException
	{

		final MessageMappingRulesContainerImpl messageMappingRulesContainer = prepareMessageMappingRulesContainerWithTestRules();
		assertEquals(MessageMappingRulesParserTest.RULE_AV_E_ATRxxx1,
				messageMappingRulesContainer.mostNarrow(new MessageMappingRule.Pattern(MessageMappingRulesParserTest.AV2CL,
						MessageMappingRulesParserTest.AV2NUM, MessageMappingRulesParserTest.AV2SEV, null, null, null,
						MessageMappingRulesParserTest.AV2ATR1)));
	}

	@Test
	public void testIdentificationNRulesAttrs_11xx() throws SAXException, IOException, SAPHybrisCacheException
	{

		final MessageMappingRulesContainerImpl messageMappingRulesContainer = prepareMessageMappingRulesContainerWithTestRules();
		assertEquals(MessageMappingRulesParserTest.RULE_AV_E_ATR11xx,
				messageMappingRulesContainer.mostNarrow(new MessageMappingRule.Pattern(MessageMappingRulesParserTest.AV2CL,
						MessageMappingRulesParserTest.AV2NUM, MessageMappingRulesParserTest.AV2SEV,
						MessageMappingRulesParserTest.AV2ATR1, MessageMappingRulesParserTest.AV2ATR1, null, null)));
	}

	@Test
	public void testIdentificationNRulesAttrs_2xxx() throws SAXException, IOException, SAPHybrisCacheException
	{
		final MessageMappingRulesContainerImpl messageMappingRulesContainer = prepareMessageMappingRulesContainerWithTestRules();
		assertEquals(MessageMappingRulesParserTest.RULE_AV_E_ATR2xxx,
				messageMappingRulesContainer.mostNarrow(new MessageMappingRule.Pattern(MessageMappingRulesParserTest.AV2CL,
						MessageMappingRulesParserTest.AV2NUM, MessageMappingRulesParserTest.AV2SEV,
						MessageMappingRulesParserTest.AV2ATR2, null, null, null)));
	}

	@Test
	public void testIdentificationNRulesAttrs_21xx() throws SAXException, IOException, SAPHybrisCacheException
	{
		final MessageMappingRulesContainerImpl messageMappingRulesContainer = prepareMessageMappingRulesContainerWithTestRules();
		assertEquals(MessageMappingRulesParserTest.RULE_AV_E_ATR21xx,
				messageMappingRulesContainer.mostNarrow(new MessageMappingRule.Pattern(MessageMappingRulesParserTest.AV2CL,
						MessageMappingRulesParserTest.AV2NUM, MessageMappingRulesParserTest.AV2SEV,
						MessageMappingRulesParserTest.AV2ATR2, MessageMappingRulesParserTest.AV2ATR1, null, null)));
	}

	@Test
	public void testBeanInitialization() throws CommunicationException
	{
		final MessageMappingRulesContainerImpl cut = (MessageMappingRulesContainerImpl) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER);

		Assert.assertNotNull(cut.messageMappingRulesLoader);
		assertNotNull(cut.moduleConfigurationAccess);
	}

}
