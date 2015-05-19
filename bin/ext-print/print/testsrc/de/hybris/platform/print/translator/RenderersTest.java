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

import de.hybris.platform.commons.translator.RenderersFactory;
import de.hybris.platform.commons.translator.RenderersFactoryFromFile;
import de.hybris.platform.commons.translator.Translator;
import de.hybris.platform.commons.translator.TranslatorConfiguration;
import de.hybris.platform.commons.translator.nodes.AbstractNode;
import de.hybris.platform.commons.translator.nodes.SimpleNode;
import de.hybris.platform.commons.translator.nodes.TableNode;
import de.hybris.platform.commons.translator.parsers.HtmlSimpleParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * In these tests nodes are created by hand (not with parsers) and then by using renderers output code is generated. Uses the
 * HTML-to-InDesign-TaggedText configuration
 */
public class RenderersTest
{
	// name of configuration file that should be in the same source folder as this class
	private static final String HTML_PARSERS_CONFIGURATION_FILE_NAME = "/print/translator/translator_parsers_html.xml";
	// name of configuration file that should be in the same source folder as this class
	private static final String INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME = "/print/translator/translator_renderers_indesign.xml";
	// name of file with properties
	public final static String INDESIGN_PROPERTIES_FILENAME = "/print/translator/indesign.properties";
	// name of file with rules of replacing html entities to indesign language
	public final static String INDESIGN_ENTITIES_REPLACE_FILENAME = "/print/translator/indesign_replace.properties";
	// The "forced line break" character
	private final static String FORCED_LINEBREAK = "<0x000A>";
	// The "paragraph break"
	private final static String PARAGRAPH_BREAK = String.valueOf((char) Integer.parseInt("000D", 16)) //NOPMD
			+ (char) Integer.parseInt("000A", 16);

	private TranslatorConfiguration config;
	private Translator translator;

	@Before
	public void setUp() throws Exception
	{
		InputStream inputStream1 = null;
		InputStream inputStream2 = null;
		InputStream inputStream4 = null;
		try
		{
			inputStream1 = Translator.class.getResourceAsStream(INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME);
			inputStream2 = Translator.class.getResourceAsStream(INDESIGN_PROPERTIES_FILENAME);
			final RenderersFactory renderersFactory = new RenderersFactoryFromFile(inputStream1, inputStream2);

			inputStream4 = Translator.class.getResourceAsStream(HTML_PARSERS_CONFIGURATION_FILE_NAME);
			config = new TranslatorConfiguration(inputStream4, renderersFactory);
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
			inputStream4.close();
		}
	}

	/**
	 * nodes generate by hand and then passed to translator to create output code
	 */
	@Test
	public void testGenerateTextFromNodes()
	{
		// create main node and its children
		final HtmlSimpleParser htmlSimpleParser = new HtmlSimpleParser();
		final SimpleNode mainNode = new SimpleNode("mainNode", null);

		final List<AbstractNode> children = new ArrayList<AbstractNode>();

		children.add(new SimpleNode("textNode", "first line", htmlSimpleParser.generateAttributes("")));
		children.add(new SimpleNode("newLine", null, htmlSimpleParser.generateAttributes("<br/>")));
		children.add(new SimpleNode("textNode", "second line", htmlSimpleParser.generateAttributes("")));
		final SimpleNode divNode = new SimpleNode("div", null, htmlSimpleParser.generateAttributes("<div>"));
		children.add(divNode);

		mainNode.setChildNodes(children);

		//childred for div node
		final List<AbstractNode> children2 = new ArrayList<AbstractNode>();

		children2.add(new SimpleNode("italic", "italic text", htmlSimpleParser.generateAttributes("<i>")));
		children2.add(new SimpleNode("bold", "bold text", htmlSimpleParser.generateAttributes("<b>")));
		children2.add(new SimpleNode("italic", "italic text", htmlSimpleParser.generateAttributes("<i class=\"myOwnClass\">")));
		divNode.setChildNodes(children2);

		final String out = "first line" + FORCED_LINEBREAK + "second line" + PARAGRAPH_BREAK
				+ "<ParaStyle:NormalParagraphStyle><cTypeface:Italic>italic text<cTypeface:>"
				+ "<cTypeface:Bold>bold text<cTypeface:>" + "<cTypeface:Italic>italic text<cTypeface:>";
		final String result = translator.renderTextFromNode(mainNode);

		assertEquals(out, result);
	}


	@Test
	public void testGenerateTextFromTableNode()
	{
		final SimpleNode mainNode = new SimpleNode("mainNode", null);
		final List<AbstractNode> children = new ArrayList<AbstractNode>();
		final TableNode tableNode = new TableNode("table", null);
		final Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("headers", "0");
		attributes.put("body", "3");
		attributes.put("footers", "0");
		tableNode.setAttributes(attributes);
		tableNode.initializeNodesTable(3, 3);
		setSimpleCell(tableNode, "rowNode", null, 0, 1);
		setSimpleCell(tableNode, "cellNode", "11", 1, 1);
		setSimpleCell(tableNode, "cellNode", "21", 2, 1);
		setSimpleCell(tableNode, "rowNode", null, 0, 2);
		setSimpleCell(tableNode, "cellNode", "12", 1, 2);
		setSimpleCell(tableNode, "cellNode", "22", 2, 2);
		setSimpleCell(tableNode, "rowNode", null, 0, 3);
		setSimpleCell(tableNode, "cellNode", "13", 1, 3);
		setSimpleCell(tableNode, "cellNode", "23", 2, 3);

		children.add(tableNode);
		mainNode.setChildNodes(children);

		final String out = "<TableStart:3,2:0:0<tCellDefaultCellType:Text>>"
				+ "<ColStart:><ColStart:>"
				+ // column definition for each column
				"<RowStart:>" + "<CellStart:1,1>11<CellEnd:>" + "<CellStart:1,1>21<CellEnd:>" + "<RowEnd:>" + "<RowStart:>"
				+ "<CellStart:1,1>12<CellEnd:>" + "<CellStart:1,1>22<CellEnd:>" + "<RowEnd:>" + "<RowStart:>"
				+ "<CellStart:1,1>13<CellEnd:>" + "<CellStart:1,1>23<CellEnd:>" + "<RowEnd:>" + "<TableEnd:>";
		final String result = translator.renderTextFromNode(mainNode);

		assertEquals(out, result);
	}


	/**
	 * Check templates defined inside xml
	 */
	@Test
	public void testTemplateInsideXML()
	{
		final SimpleNode simpleNode = new SimpleNode("strong", "text inside strong tag");
		final String out = "<cTypeface:Bold>text inside strong tag<cTypeface:>";
		final String result = translator.renderTextFromNode(simpleNode);

		assertEquals(out, result);
	}


	/**
	 * method that support creating cell for tableNode, it not only create cellNode but also textNode inside
	 */
	private void setSimpleCell(final TableNode tableNode, final String cellName, final String nodeText, final int x, final int y)
	{
		final SimpleNode simpleNode = new SimpleNode(cellName, null);
		final Map<String, String> map = new HashMap<String, String>();
		map.put("colspan", "1");
		map.put("rowspan", "1");
		simpleNode.setAttributes(map);
		simpleNode.setParentNode(tableNode);
		tableNode.setCell(simpleNode, x, y);
		final SimpleNode innerNode = new SimpleNode("textNode", nodeText);
		simpleNode.addChildNode(innerNode);
	}
}
