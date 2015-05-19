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
package de.hybris.platform.print.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import de.hybris.platform.commons.translator.RenderersFactory;
import de.hybris.platform.commons.translator.RenderersFactoryFromFile;
import de.hybris.platform.commons.translator.Translator;
import de.hybris.platform.commons.translator.TranslatorConfiguration;
import de.hybris.platform.commons.translator.nodes.AbstractNode;
import de.hybris.platform.commons.translator.nodes.TableNode;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;


/**
 * Test first phase of translating when input text is changed to nodes tree, during each test nodes are counted, thier
 * names and texts are checked. Uses the HTML-to-InDesign-TaggedText configuration
 * 
 * 
 */
public class ParsersTest
{

	// name of configuration file that should be in the same source folder as this class
	private static final String HTML_PARSERS_CONFIGURATION_FILE_NAME = "/print/translator/translator_parsers_html.xml";
	// name of configuration file that should be in the same source folder as this class
	private static final String INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME = "/print/translator/translator_renderers_indesign.xml";
	// name of file with properties
	public final static String INDESIGN_PROPERTIES_FILENAME = "/print/translator/indesign.properties";
	private TranslatorConfiguration config;
	private Translator translator;

	@Before
	public void setUp() throws Exception
	{
		InputStream inputStream1 = null;
		InputStream inputStream2 = null;
		InputStream inputStream3 = null;
		try
		{
			//load configuration and initialize translator
			inputStream1 = Translator.class.getResourceAsStream(INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME);
			inputStream2 = Translator.class.getResourceAsStream(INDESIGN_PROPERTIES_FILENAME);
			final RenderersFactory renderersFactory = new RenderersFactoryFromFile(inputStream1, inputStream2);

			inputStream3 = Translator.class.getResourceAsStream(HTML_PARSERS_CONFIGURATION_FILE_NAME);
			config = new TranslatorConfiguration(inputStream3, renderersFactory);
			translator = new Translator(config);
		}
		catch (final RuntimeException e)
		{
			throw e;
		}
		finally
		{
			inputStream1.close();
			inputStream2.close();
			inputStream3.close();
		}
	}

	/**
	 * Function that help check node properties with passed values text, number of childrens, name
	 */
	private void testNode(final AbstractNode node, final String nodeText, final int childCount, final String nodeName)
	{
		assertEquals(nodeText, node.getNodeText());
		assertEquals(childCount, node.getChildNodes().size());
		assertEquals(nodeName, node.getNodeName());
	}

	@Test
	public void testTextNodes()
	{
		final String in = "This text should go to text node <b>This is bold</b><br/><i>a<b>b</b>c<b>d</b>e</i>";
		final AbstractNode mainNode = translator.createNodesTree(in);

		testNode(mainNode, null, 4, "mainNode");
		testNode(mainNode.getChildNodes().get(0), "This text should go to text node ", 0, "textNode");
		testNode(mainNode.getChildNodes().get(1), "This is bold", 0, "bold");
		testNode(mainNode.getChildNodes().get(2), null, 0, "newLine");
		testNode(mainNode.getChildNodes().get(3), null, 5, "italic");
		testNode(mainNode.getChildNodes().get(3).getChildNodes().get(0), "a", 0, "textNode");
		testNode(mainNode.getChildNodes().get(3).getChildNodes().get(1), "b", 0, "bold");
		testNode(mainNode.getChildNodes().get(3).getChildNodes().get(2), "c", 0, "textNode");
		testNode(mainNode.getChildNodes().get(3).getChildNodes().get(3), "d", 0, "bold");
		testNode(mainNode.getChildNodes().get(3).getChildNodes().get(4), "e", 0, "textNode");
	}

	@Test
	public void testTextNodes2()
	{
		final String in = "a<b><b>c<b>d</b>e<b><b></b></b></b>f</b>";
		final AbstractNode mainNode = translator.createNodesTree(in);

		testNode(mainNode, null, 2, "mainNode");
		testNode(mainNode.getChildNodes().get(0), "a", 0, "textNode");
		testNode(mainNode.getChildNodes().get(1), null, 2, "bold");
		testNode(mainNode.getChildNodes().get(1).getChildNodes().get(0), null, 4, "bold");
		testNode(mainNode.getChildNodes().get(1).getChildNodes().get(1), "f", 0, "textNode");
		testNode(mainNode.getChildNodes().get(1).getChildNodes().get(0).getChildNodes().get(0), "c", 0, "textNode");
		testNode(mainNode.getChildNodes().get(1).getChildNodes().get(0).getChildNodes().get(1), "d", 0, "bold");
		testNode(mainNode.getChildNodes().get(1).getChildNodes().get(0).getChildNodes().get(2), "e", 0, "textNode");
		testNode(mainNode.getChildNodes().get(1).getChildNodes().get(0).getChildNodes().get(3), null, 1, "bold");
		testNode(mainNode.getChildNodes().get(1).getChildNodes().get(0).getChildNodes().get(3).getChildNodes().get(0), "", 0,
				"bold");
	}

	@Test
	public void testTableNode()
	{
		final String in = "<table><tr><td>11</td><td>21</td><td>31</td></tr><tr><td>12</td><td>22</td><td>32</td></tr></table>";
		final AbstractNode mainNode = translator.createNodesTree(in);
		testNode(mainNode, null, 1, "mainNode");
		final TableNode tableNode = (TableNode) mainNode.getChildNodes().get(0);
		assertEquals(3, tableNode.getCols());
		assertEquals(2, tableNode.getRows());
		assertEquals("11", tableNode.getCell(1, 1).getNodeText());
		assertEquals("21", tableNode.getCell(2, 1).getNodeText());
		assertEquals("31", tableNode.getCell(3, 1).getNodeText());
		assertEquals("12", tableNode.getCell(1, 2).getNodeText());
		assertEquals("22", tableNode.getCell(2, 2).getNodeText());
		assertEquals("32", tableNode.getCell(3, 2).getNodeText());
	}

	/**
	 * if tag is not closed then IllegalStateException should be thrown, there is no sens to try to do something with
	 * such a text, it would be only way to build confusing output text should be fixed
	 * 
	 */
	@Test
	public void testCrush()
	{
		try
		{
			final String in = "<b>text</i>";
			/* final AbstractNode mainNode = */translator.createNodesTree(in);
			fail("There should be an error");
		}
		catch (final IllegalStateException ex)
		{
			//assertTrue(true);
		}
	}

	@Test
	public void testCrush2()
	{
		try
		{
			final String in = "<b>text";
			/* final AbstractNode mainNode = */translator.createNodesTree(in);
			fail("There should be an error");
		}
		catch (final IllegalStateException ex)
		{
			//assertTrue(true);
		}
	}

	@Test
	public void testCrush3()
	{
		try
		{
			final String in = "<b>text<i>text2</b>text3</i>";
			/* final AbstractNode mainNode = */translator.createNodesTree(in);
			fail("There should be an error");
		}
		catch (final IllegalStateException ex)
		{
			//assertTrue(true);
		}
	}
}
