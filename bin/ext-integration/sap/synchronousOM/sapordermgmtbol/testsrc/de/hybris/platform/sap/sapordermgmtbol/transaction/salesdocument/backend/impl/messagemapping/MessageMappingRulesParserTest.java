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

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRule.Pattern;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRule.Result;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


@SuppressWarnings("javadoc")
public class MessageMappingRulesParserTest extends TestCase
{

	static public void ruleAppendAttribute(final StringBuilder sb, final String name, final String value)
	{
		sb.append(" ").append(name).append("=\"").append(value).append("\" ");
	}

	static public String ruleString(final MessageMappingRule rule)
	{

		final StringBuilder sb = new StringBuilder();
		sb.append("<message>\n");

		final Pattern patern = rule.getPattern();
		sb.append("<source");
		final String[][] attrs =
		{
		{ patern.getBeClass(), MessageMappingRulesParserImpl.ATTRIBUTE.ID }, //
				{ patern.getBeNumber(), MessageMappingRulesParserImpl.ATTRIBUTE.NUMBER }, //
				{ patern.getBeSeverity(), MessageMappingRulesParserImpl.ATTRIBUTE.SEVERITY }, //

				{ patern.getBeV1(), MessageMappingRulesParserImpl.ATTRIBUTE.MSGV1 }, //
				{ patern.getBeV2(), MessageMappingRulesParserImpl.ATTRIBUTE.MSGV2 }, //
				{ patern.getBeV3(), MessageMappingRulesParserImpl.ATTRIBUTE.MSGV3 }, //
				{ patern.getBeV4(), MessageMappingRulesParserImpl.ATTRIBUTE.MSGV4 }, //
		};

		for (int i = 0; i < attrs.length; ++i)
		{
			if (attrs[i][0] != null)
			{
				ruleAppendAttribute(sb, attrs[i][1], attrs[i][0]);
			}
		}
		sb.append("/>").append("\n");

		final Result result = rule.getResult();
		if (result.isHide())
		{
			sb.append("<hide/>\n");
		}
		else
		{
			sb.append("<destination");
			if (rule.getResult().getSeverity() != null)
			{
				ruleAppendAttribute(sb, MessageMappingRulesParserImpl.ATTRIBUTE.SEVERITY, rule.getResult().getSeverity().toString());
			}
			if (rule.getResult().getResourceKey() != null)
			{
				ruleAppendAttribute(sb, MessageMappingRulesParserImpl.ATTRIBUTE.RES_KEY_SHORT, rule.getResult().getResourceKey());
			}
			sb.append("/>\n");
		}
		sb.append("</message>\n");
		return sb.toString();
	}

	static public String ruleString(final Boolean hideNoNErrorMessages, final MessageMappingRule[] rules)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<messages>\n");
		sb.append("<global_config ");
		if (hideNoNErrorMessages != null)
		{
			ruleAppendAttribute(sb, MessageMappingRulesParserImpl.ATTRIBUTE.HIDE_NON_ERROR_MESSAGES, hideNoNErrorMessages.toString());
		}
		sb.append("/>\n");

		for (final MessageMappingRule rule : rules)
		{
			sb.append(ruleString(rule));
		}
		sb.append("</messages>");
		return sb.toString();
	}

	static final Boolean HIDE_NON_ERROR_MESSAGES = new Boolean(false);

	public static final String AV2CL = "AV2",//
			AV2NUM = "001", //
			AV2SEV = "E", //
			AV2ATR1 = "atr1", //
			AV2ATR2 = "atr2";

	@SuppressWarnings("boxing")
	static final MessageMappingRule //
			RULE_COMPLETE = new MessageMappingRule(new MessageMappingRule.Pattern("V1", "001", "W"), new MessageMappingRule.Result(
					'E', "some.key1")), //
			RULE_ANY_SEVERITY = new MessageMappingRule(new MessageMappingRule.Pattern("V1", "001", "*"),
					new MessageMappingRule.Result('E', "some.key4")), //
			RULE_ANY_NUMBER_ZV2 = new MessageMappingRule(new MessageMappingRule.Pattern("ZV2", null, "W"),
					new MessageMappingRule.Result('E', "some.key3")), //
			RULE_NO_KEY = new MessageMappingRule(new MessageMappingRule.Pattern("V4", "005", "W"), new MessageMappingRule.Result(
					'E', "")), //
			RULE_PASS_THROUGH = new MessageMappingRule(new MessageMappingRule.Pattern("V5", "006", "*"),
					new MessageMappingRule.Result(null, "")), //
			WRONG_RULE_ANY_CLASS = new MessageMappingRule(new MessageMappingRule.Pattern("*", "001", "W"),
					new MessageMappingRule.Result('E', "some.key2")), //
			RULE_ZV_E_EMPTY = new MessageMappingRule(new MessageMappingRule.Pattern("ZV2", "011", "E"),
					new MessageMappingRule.Result(null, null)), //
			RULE_ZV_W_EMPTY = new MessageMappingRule(new MessageMappingRule.Pattern("ZV2", "012", "W"),
					new MessageMappingRule.Result(null, null)), //
			RULE_ZV_E_REPLACE_KEY = new MessageMappingRule(new MessageMappingRule.Pattern("ZV2", "013", "W"),
					new MessageMappingRule.Result('W', "some.key4")), //
			RULE_ZV_E_REPLACE_SEVERITY = new MessageMappingRule(new MessageMappingRule.Pattern("ZV2", "014", "W"),
					new MessageMappingRule.Result('E', null)), //
			RULE_ZV_E_MAP_ALL = new MessageMappingRule(new MessageMappingRule.Pattern("ZV2", "015", "W"),
					new MessageMappingRule.Result('E', "some.key4")), //
			RULE_ZV_W_HIDE = new MessageMappingRule(new MessageMappingRule.Pattern("ZV2", "016", "E"),
					new MessageMappingRule.Result(true)), //
			RULE_AV_E_MAP_ALL = new MessageMappingRule(
					new MessageMappingRule.Pattern(AV2CL, AV2NUM, AV2SEV, null, null, null, null), //
					new MessageMappingRule.Result('E', "some.key.xxxx")), //
			RULE_AV_E_ATR1xxx = new MessageMappingRule(new MessageMappingRule.Pattern(AV2CL, AV2NUM, AV2SEV, AV2ATR1, null, null,
					null), //
					new MessageMappingRule.Result('E', "some.key.1xxx")), RULE_AV_E_ATRx1xx = new MessageMappingRule(
					new MessageMappingRule.Pattern(AV2CL, AV2NUM, AV2SEV, null, AV2ATR1, null, null), //
					new MessageMappingRule.Result('E', "some.key.x1xx")), RULE_AV_E_ATRxx1x = new MessageMappingRule(
					new MessageMappingRule.Pattern(AV2CL, AV2NUM, AV2SEV, null, null, AV2ATR1, null), //
					new MessageMappingRule.Result('E', "some.key.xx1x")), RULE_AV_E_ATRxxx1 = new MessageMappingRule(
					new MessageMappingRule.Pattern(AV2CL, AV2NUM, AV2SEV, null, null, null, AV2ATR1), //
					new MessageMappingRule.Result('E', "some.key.xxx1")), RULE_AV_E_ATR11xx = new MessageMappingRule(
					new MessageMappingRule.Pattern(AV2CL, AV2NUM, AV2SEV, AV2ATR1, AV2ATR1, null, null), //
					new MessageMappingRule.Result('E', "some.key.11xx")), RULE_AV_E_ATR11x1 = new MessageMappingRule(
					new MessageMappingRule.Pattern(AV2CL, AV2NUM, AV2SEV, AV2ATR1, AV2ATR1, null, AV2ATR1), //
					new MessageMappingRule.Result('E', "some.key.11x1")), RULE_AV_E_ATR2xxx = new MessageMappingRule(
					new MessageMappingRule.Pattern(AV2CL, AV2NUM, AV2SEV, AV2ATR2, null, null, null), //
					new MessageMappingRule.Result('E', "some.key2xxx")), RULE_AV_E_ATR21xx = new MessageMappingRule(
					new MessageMappingRule.Pattern(AV2CL, AV2NUM, AV2SEV, AV2ATR2, AV2ATR1, null, null), //
					new MessageMappingRule.Result('E', "some.key21xx"));

	static final MessageMappingRule NRULES_SET[] =
	//
	{ RULE_COMPLETE, //
			// test fields match rules
			RULE_ANY_SEVERITY, //
			RULE_ANY_NUMBER_ZV2, //
			// test mapping rules
			RULE_NO_KEY, //
			RULE_PASS_THROUGH, //
			RULE_ZV_E_EMPTY, //
			RULE_ZV_W_EMPTY, //
			RULE_ZV_E_REPLACE_KEY, //
			RULE_ZV_E_REPLACE_SEVERITY, //
			RULE_ZV_E_MAP_ALL, //
			RULE_ZV_W_HIDE,
			// test attribute match rules
			RULE_AV_E_MAP_ALL, //
			RULE_AV_E_ATR1xxx, //
			RULE_AV_E_ATRx1xx, //
			RULE_AV_E_ATRxx1x, //
			RULE_AV_E_ATRxxx1, //
			RULE_AV_E_ATR11xx, //
			RULE_AV_E_ATR11x1, //
			RULE_AV_E_ATR2xxx, //
			RULE_AV_E_ATR21xx, //
	};

	static final MessageMappingRule DUBLICATE_RULES_SET[] =
	//
	{ RULE_COMPLETE, //
			RULE_COMPLETE, //
			RULE_AV_E_ATR21xx, //
			RULE_AV_E_ATR21xx, //
	};

	@SuppressWarnings("boxing")
	final static String XML_FILE_CONTENT_0RULE = ruleString(!HIDE_NON_ERROR_MESSAGES, new MessageMappingRule[0]);

	final static String XML_FILE_CONTENT_NRULE = ruleString(HIDE_NON_ERROR_MESSAGES, NRULES_SET);

	final static String XML_FILE_CONTENT_DUBLICATES = ruleString(HIDE_NON_ERROR_MESSAGES, DUBLICATE_RULES_SET);

	public MessageMappingRulesParserImpl phrase(final String xmlContent) throws SAXException, IOException
	{
		final XMLReader parser = XMLReaderFactory.createXMLReader();
		final MessageMappingRulesParserImpl msgHandler = new MessageMappingRulesParserImpl();
		parser.setContentHandler(msgHandler);
		parser.parse(new InputSource(new ByteArrayInputStream(xmlContent.getBytes())));
		return msgHandler;
	}

	@Test
	public void testPharasedError_noPattern() throws IOException
	{
		try
		{
			phrase("<messages><message></message></messages>");
			fail("no SAXException thrown");
		}
		catch (final SAXException ex)
		{
			// success
		}
	}

	@Test
	public void testPharasedError_noRule() throws IOException
	{
		try
		{
			phrase("<messages><message><source id=\"SLS_LORD\"></message></messages>");
			fail("no SAXException thrown");
		}
		catch (final SAXException ex)
		{
			// success
		}
	}

	@Test
	public void testPharasedError_souceOutsideMessage() throws IOException
	{
		try
		{
			phrase("<messages><source id=\"SLS_LORD\"></messages>");
			fail("no SAXException thrown");
		}
		catch (final SAXException ex)
		{
			// success
		}
	}

	@Test
	public void testPharasedError_destinationOutsideMessage() throws IOException
	{
		try
		{
			phrase("<messages><destination resource_key=\"XXXXX\" /></messages>");
			fail("no SAXException thrown");
		}
		catch (final SAXException ex)
		{
			// success
		}
	}

	@Test
	public void testPharasedError_hideOutsideMessage() throws IOException
	{
		try
		{
			phrase("<messages></hide></messages>");
			fail("no SAXException thrown");
		}
		catch (final SAXException ex)
		{
			// success
		}
	}


	@Test
	public void testPharased0Rules() throws SAXException, IOException
	{
		final MessageMappingRulesParserImpl obj = phrase(XML_FILE_CONTENT_0RULE);

		assertEquals(true, obj.hideNonErrorMsg);

		assertNotNull(obj.rulesList);
		assertEquals(0, obj.rulesList.size());
	}

	@Test
	public void testPhrasedNRules() throws SAXException, IOException
	{
		final MessageMappingRulesParserImpl obj = phrase(XML_FILE_CONTENT_NRULE);
		final List<MessageMappingRule> rl = obj.rulesList;

		assertEquals(false, obj.hideNonErrorMsg);

		assertNotNull(rl);
		assertEquals(NRULES_SET.length, rl.size());

		// find all rules
		for (final MessageMappingRule rule : NRULES_SET)
		{
			assertTrue(rule.toString(), rl.contains(rule));
		}
	}

	@Test
	public void testToString() throws SAXException, IOException
	{
		final MessageMappingRulesParserImpl obj = phrase(XML_FILE_CONTENT_NRULE);

		assertNotNull(obj);
		final String str = obj.toString();

		// number of rule plus 2 lines
		assertEquals(NRULES_SET.length + 1, str.length() - str.replaceAll("\n", "").length());

		// find all rules
		for (final MessageMappingRule rule : NRULES_SET)
		{
			assertTrue(rule.toString(), str.contains(rule.toString()));
		}
	}
}
