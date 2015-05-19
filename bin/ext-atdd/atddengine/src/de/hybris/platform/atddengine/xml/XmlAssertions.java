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
package de.hybris.platform.atddengine.xml;

import static org.custommonkey.xmlunit.XMLUnit.setIgnoreComments;
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace;
import static org.junit.Assert.fail; 
import java.io.IOException;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLAssert;
import org.xml.sax.SAXException;


public class XmlAssertions
{
	private static final Logger LOG = Logger.getLogger(XmlAssertions.class);

	/**
	 * Asserts that the given XPath expression evaluates to the expected value when applied to the target XML.
	 * 
	 * @param message
	 *           failure message
	 * @param targetXml
	 *           the target XML to apply the XPath expression to
	 * @param xPathExpression
	 *           the XPath expression to apply
	 * @param expectedValue
	 *           the expected value
	 */
	public static void assertXPathEvaluatesTo(final String message, final String targetXml, final String xPathExpression,
			final String expectedValue)
	{
		assertXmlEqual(message, expectedValue, XmlUtils.evaluateXPath(targetXml, xPathExpression));
	}

	/**
	 * Asserts that the given XPath expression evaluates to the expected value when applied to the target XML. Before
	 * comparing the given XSL transformation is applied to the expected value as well as the targetXml.
	 * 
	 * @param message
	 *           failure message
	 * @param targetXml
	 *           the target XML to apply the XPath expression to
	 * @param xPathExpression
	 *           the XPath expression to apply
	 * @param expectedValue
	 *           the expected value
	 * @param transformation
	 *           resource path to an XML transformation to be applied to both expected value and target XML
	 */
	public static void assertXPathEvaluatesTo(final String message, final String targetXml, final String xPathExpression,
			final String expectedValue, final String transformation)
	{
		final String expected = transformation == null ? expectedValue : XmlUtils.applyTransformationResource(expectedValue,
				transformation);
		final String xml = transformation == null ? targetXml : XmlUtils.applyTransformationResource(targetXml, transformation);

		assertXPathEvaluatesTo(message, xml, xPathExpression, expected);
	}

	/**
	 * Asserts that two XML snippets are equal.
	 * 
	 * @param message
	 *           failure message
	 * @param expectedXml
	 *           the expected XML (snippet)
	 * @param actualXml
	 *           the actual XML (snippet)
	 */
	public static void assertXmlEqual(final String message, final String expectedXml, final String actualXml)
	{
		LOG.info("EXPECTED XML: " + expectedXml);
		LOG.info("ACTUAL XML: " + actualXml);
		setIgnoreWhitespace(true);
		setIgnoreComments(true);
		try
		{
			XMLAssert.assertXMLEqual(message + "\n\n### EXPECTED XML: ###\n" + expectedXml + "\n### ACTUAL XML: ###\n" + actualXml
					+ "\n\n", expectedXml, actualXml);
		}
		catch (final SAXException e)
		{
			LOG.error("An exception occured while comparing XML files", e);
			fail(message);
		}
		catch (final IOException e)
		{
			LOG.error("An exception occured while comparing XML files", e);
			fail(message);
		}
	}

	/**
	 * Asserts that two XML snippets are equal. Before comparing the given XSL transformation is applied to the expected
	 * as well as the actual XML.
	 * 
	 * @param message
	 *           failure message
	 * @param expectedXml
	 *           the expected XML (snippet)
	 * @param actualXml
	 *           the actual XML (snippet)
	 * @param transformation
	 *           resource path to an XML transformation to be applied to both expected and actual XML
	 */
	public static void assertXmlEqual(final String message, final String expectedXml, final String actualXml,
			final String transformation)
	{
		final String expected = transformation == null ? expectedXml : XmlUtils.applyTransformationResource(expectedXml,
				transformation);
		final String actual = transformation == null ? actualXml : XmlUtils.applyTransformationResource(actualXml, transformation);

		assertXmlEqual(message, expected, actual);
	}

	/**
	 * Asserts that two XML snippets are equal.
	 * 
	 * @param expectedXml
	 *           the expected XML
	 * @param actualXml
	 *           the actual XML
	 */
	public static void assertXmlEqual(final String expectedXml, final String actualXml)
	{
		assertXmlEqual("The provided XML does not match the expected value", expectedXml, actualXml);
	}

}
