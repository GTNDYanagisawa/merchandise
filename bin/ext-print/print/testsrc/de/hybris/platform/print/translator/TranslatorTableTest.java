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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.commons.constants.CommonsConstants;
import de.hybris.platform.commons.translator.RenderersFactory;
import de.hybris.platform.commons.translator.RenderersFactoryFromFile;
import de.hybris.platform.commons.translator.Translator;
import de.hybris.platform.commons.translator.TranslatorConfiguration;
import de.hybris.platform.commons.translator.nodes.AbstractNode;
import de.hybris.platform.commons.translator.nodes.TableNode;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the table related translator functionality with the HTML-to-InDesign-TaggedText configuration
 * 
 * 
 */
public class TranslatorTableTest
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TranslatorTest.class.getName());

	// name of configuration file that should be in the same source folder as this class
	private static final String INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME = "/print/translator/translator_renderers_indesign.xml";
	// name of file with properties
	public final static String INDESIGN_PROPERTIES_FILENAME = "/print/translator/indesign.properties";
	// name of configuration file that should be in the same source folder as this class
	private static final String HTML_PARSERS_CONFIGURATION_FILE_NAME = "/print/translator/translator_parsers_html.xml";
	// name of file with rules of replacing html entities to indesign language
	public final static String INDESIGN_ENTITIES_REPLACE_FILENAME = "/print/translator/indesign_replace.properties";

	private TranslatorConfiguration config;
	private Translator translator;

	@Before
	public void setUp() throws Exception
	{
		InputStream translatorRenderersIndesign_IS = null;
		InputStream indesignProperties_IS = null;
		InputStream translatorParsersHtml_IS = null;
		// InputStream indesignReplace_IS = null;
		try
		{
			//load configuration and initialize translator
			translatorRenderersIndesign_IS = Translator.class.getResourceAsStream(INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME);
			indesignProperties_IS = Translator.class.getResourceAsStream(INDESIGN_PROPERTIES_FILENAME);
			final RenderersFactory renderersFactory = new RenderersFactoryFromFile(translatorRenderersIndesign_IS,
					indesignProperties_IS);
			translatorParsersHtml_IS = Translator.class.getResourceAsStream(HTML_PARSERS_CONFIGURATION_FILE_NAME);
			config = new TranslatorConfiguration(translatorParsersHtml_IS, renderersFactory);
			translator = new Translator(config);
		}
		finally
		{
			translatorRenderersIndesign_IS.close();
			indesignProperties_IS.close();
			translatorParsersHtml_IS.close();
		}
	}

	/**
	 * Tests simple/basic table translation
	 */
	@Test
	public void testSimpleTableTranslation()
	{
		final String htmlInput = "<table>" + "<tr>" + "<th>title1</th>" + "<th>title2</th>" + "</tr>" + "<tr>" + "<td>text1</td>"
				+ "<td>text2</td>" + "</tr>" + "<tr>" + "<td>text3</td>" + "<td>text4</td>" + "</tr>" + "<tr>" + "<td>text5</td>"
				+ "<td>text6</td>" + "</tr>" + "</table>";
		final String out = "<TableStart:4,2:0:0<tCellDefaultCellType:Text>>"
				+ "<ColStart:><ColStart:>"
				+ // column definition for each column
				"<RowStart:>" + "<CellStart:1,1>title1<CellEnd:>" + "<CellStart:1,1>title2<CellEnd:>" + "<RowEnd:>" + "<RowStart:>"
				+ "<CellStart:1,1>text1<CellEnd:>" + "<CellStart:1,1>text2<CellEnd:>" + "<RowEnd:>" + "<RowStart:>"
				+ "<CellStart:1,1>text3<CellEnd:>" + "<CellStart:1,1>text4<CellEnd:>" + "<RowEnd:>" + "<RowStart:>"
				+ "<CellStart:1,1>text5<CellEnd:>" + "<CellStart:1,1>text6<CellEnd:>" + "<RowEnd:>" + "<TableEnd:>";
		final String input = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, input);
	}

	/**
	 * Tests translation of tables with tablestyle or cellstyle
	 */
	@Test
	public void testTableStyleTranslation()
	{
		//		final String NO_TABLE_STYLE = "\\[No table style\\]";		// Used when no table style is set 
		//		final String NO_CELL_STYLE = "\\[None\\]";					// Used when no cell style is set 
		//		final int NO_CELL_PRIORITY = 0;									// Used as style priotity if no cell style is set

		final String htmlInput = "<table class=\"myTableStyle\">" + "<tbody>" + "<tr class=\"myRowStyle\">"
				+ // InDesign does not support RowStyles. Translate them to CellStyles for each Cell in this row
				"<td>cell1.1</td>" + "<td>cell1.2</td>" + "</tr>" + "<tr>" + "<td>cell2.1</td>"
				+ "<td class=\"myCellStyle\">cell2.2</td>" + "</tr>" + "</tbody>" + "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final String out = "<TableStyle:myTableStyle>" + "<TableStart:2,2:0:0<tCellDefaultCellType:Text>>" + "<ColStart:>"
				+ "<ColStart:>" + "<RowStart:>" + "<CellStyle:myRowStyle><StylePriority:1><CellStart:1,1>cell1.1<CellEnd:>"
				+ "<CellStyle:myRowStyle><StylePriority:1><CellStart:1,1>cell1.2<CellEnd:>" + "<RowEnd:>"
				+ "<RowStart:>"
				+
				// TODO: InDesign >= CS3 supports a NO_CELL_STYLE tag   
				// "<CellStyle:" + NO_CELL_STYLE + "><StylePriority:" + NO_CELL_PRIORITY + "><CellStart:1,1>cell2.1<CellEnd:>" +
				"<CellStart:1,1>cell2.1<CellEnd:>" + "<CellStyle:myCellStyle><StylePriority:1><CellStart:1,1>cell2.2<CellEnd:>"
				+ "<RowEnd:>" + "<TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * Tests translation of tables with width attribute
	 */
	@Test
	public void testTableWidthTranslationA()
	{
		final int width = 333;

		final String htmlInput = "<table width=\"" + width + "\">" + "<tbody>" + "<tr>" + "<td>cell1.1</td>" + "<td>cell1.2</td>"
				+ "</tr>" + "<tr>" + "<td>cell2.1</td>" + "<td>cell2.2</td>" + "</tr>" + "</tbody>" + "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final NumberFormat formatter = new DecimalFormat("#.##############"); // max 14 decimal values
		final String out = "<TableStart:2,2:0:0<tCellDefaultCellType:Text>>"
				+ "<ColStart:<tColAttrWidth:"
				+ formatter.format((width / 2) * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT)
				+ ">>"
				+ //  (width / number-of-columns) * dots-per-millimeter
				"<ColStart:<tColAttrWidth:" + formatter.format((width / 2) * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT) + ">>"
				+ "<RowStart:>" + "<CellStart:1,1>cell1.1<CellEnd:>" + "<CellStart:1,1>cell1.2<CellEnd:>" + "<RowEnd:>"
				+ "<RowStart:>" + "<CellStart:1,1>cell2.1<CellEnd:>" + "<CellStart:1,1>cell2.2<CellEnd:>" + "<RowEnd:>"
				+ "<TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * Tests translation of table with width attribute case when td also has width attribute
	 */
	@Test
	public void testTableWidthTranslationB()
	{
		final int width = 300;
		final int width1 = 100;
		final int width2 = 200;

		final String htmlInput = "<table width=\"" + width + "\">" + "<tbody>" + "<tr>" + "<td width=\"" + width1
				+ "\">cell1.1</td>" + "<td>cell1.2</td>" + "</tr>" + "<tr>" + "<td width=\"" + width2 + "\">cell2.1</td>"
				+ "<td>cell2.2</td>" + "</tr>" + "</tbody>" + "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final NumberFormat formatter = new DecimalFormat("#.##############"); // max 14 decimal values
		final String out = "<TableStart:2,2:0:0<tCellDefaultCellType:Text>>"
				+ "<ColStart:<tColAttrWidth:"
				+ formatter.format(width1 * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT)
				+ ">>"
				+ //  (width / number-of-columns) * dots-per-millimeter
				"<ColStart:<tColAttrWidth:" + formatter.format(width2 * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT) + ">>"
				+ "<RowStart:>" + "<CellStart:1,1>cell1.1<CellEnd:>" + "<CellStart:1,1>cell1.2<CellEnd:>" + "<RowEnd:>"
				+ "<RowStart:>" + "<CellStart:1,1>cell2.1<CellEnd:>" + "<CellStart:1,1>cell2.2<CellEnd:>" + "<RowEnd:>"
				+ "<TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * Tests translation of tables with width attribute case when some td also have width attribute
	 */
	@Test
	public void testTableWidthTranslationC()
	{
		final int width = 300;
		final int width1 = 150;
		final int width2 = 50;

		final String htmlInput = "<table width=\"" + width + "\">" + "<tbody>" + "<tr>" + "<td width=\"" + width1
				+ "\">cell1.1</td>" + "<td width=\"" + width2 + "\">cell1.2</td>" + "<td>cell1.3</td>" + "</tr>" + "<tr>"
				+ "<td>cell2.1</td>" + "<td>cell2.2</td>" + "<td>cell2.3</td>" + "</tr>" + "</tbody>" + "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final NumberFormat formatter = new DecimalFormat("#.##############"); // max 14 decimal values
		final String out = "<TableStart:2,3:0:0<tCellDefaultCellType:Text>>"
				+ "<ColStart:<tColAttrWidth:"
				+ formatter.format(width1 * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT)
				+ ">>"
				+ //  (width / number-of-columns) * dots-per-millimeter
				"<ColStart:<tColAttrWidth:" + formatter.format(width2 * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT) + ">>"
				+ "<ColStart:<tColAttrWidth:"
				+ formatter.format((width - width1 - width2) * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT) + ">>" + "<RowStart:>"
				+ "<CellStart:1,1>cell1.1<CellEnd:>" + "<CellStart:1,1>cell1.2<CellEnd:>" + "<CellStart:1,1>cell1.3<CellEnd:>"
				+ "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>cell2.1<CellEnd:>" + "<CellStart:1,1>cell2.2<CellEnd:>"
				+ "<CellStart:1,1>cell2.3<CellEnd:>" + "<RowEnd:>" + "<TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * Tests translation of tables where td have width attribute
	 */
	@Test
	public void testTableWidthTranslationD()
	{
		final int width1 = 100;
		final int width2 = 80;

		final String htmlInput = "<table>" + "<tbody>" + "<tr>" + "<td width=\"" + width1 + "\">cell1.1</td>" + "<td width=\""
				+ width2 + "\">cell1.2</td>" + "</tr>" + "<tr>" + "<td>cell2.1</td>" + "<td>cell2.2</td>" + "</tr>" + "</tbody>"
				+ "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final NumberFormat formatter = new DecimalFormat("#.##############"); // max 14 decimal values
		final String out = "<TableStart:2,2:0:0<tCellDefaultCellType:Text>>"
				+ "<ColStart:<tColAttrWidth:"
				+ formatter.format(width1 * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT)
				+ ">>"
				+ //  (width / number-of-columns) * dots-per-millimeter
				"<ColStart:<tColAttrWidth:" + formatter.format(width2 * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT) + ">>"
				+ "<RowStart:>" + "<CellStart:1,1>cell1.1<CellEnd:>" + "<CellStart:1,1>cell1.2<CellEnd:>" + "<RowEnd:>"
				+ "<RowStart:>" + "<CellStart:1,1>cell2.1<CellEnd:>" + "<CellStart:1,1>cell2.2<CellEnd:>" + "<RowEnd:>"
				+ "<TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * Tests translation of tables with cellpadding attribute
	 */
	@Test
	public void testTablePadding()
	{
		final int padding = 2;

		final String htmlInput = "<table cellpadding=\"" + padding + "\" >" + "<tbody>" + "<tr>" + "<td>cell1.1</td>"
				+ "<td>cell1.2</td>" + "</tr>" + "<tr>" + "<td>cell2.1</td>" + "<td>cell2.2</td>" + "</tr>" + "</tbody>" + "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final NumberFormat formatter = new DecimalFormat("#.##############"); // max 14 decimal values
		final String paddingOneSide = formatter.format((padding / 2) * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT);
		final String out = "<TableStart:2,2:0:0<tCellDefaultCellType:Text>>" + "<ColStart:>" + "<ColStart:>" + "<RowStart:>"
				+ "<CellStart:1,1<tCellAttrLeftInset:"
				+ paddingOneSide
				+ "><tCellAttrTopInset:"
				+ paddingOneSide
				+ "><tCellAttrRightInset:"
				+ paddingOneSide
				+ "><tCellAttrBottomInset:"
				+ paddingOneSide
				+ ">>cell1.1<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftInset:"
				+ paddingOneSide
				+ "><tCellAttrTopInset:"
				+ paddingOneSide
				+ "><tCellAttrRightInset:"
				+ paddingOneSide
				+ "><tCellAttrBottomInset:"
				+ paddingOneSide
				+ ">>cell1.2<CellEnd:>"
				+ "<RowEnd:>"
				+ "<RowStart:>"
				+ "<CellStart:1,1<tCellAttrLeftInset:"
				+ paddingOneSide
				+ "><tCellAttrTopInset:"
				+ paddingOneSide
				+ "><tCellAttrRightInset:"
				+ paddingOneSide
				+ "><tCellAttrBottomInset:"
				+ paddingOneSide
				+ ">>cell2.1<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftInset:"
				+ paddingOneSide
				+ "><tCellAttrTopInset:"
				+ paddingOneSide
				+ "><tCellAttrRightInset:"
				+ paddingOneSide
				+ "><tCellAttrBottomInset:"
				+ paddingOneSide
				+ ">>cell2.2<CellEnd:>"
				+ "<RowEnd:>" + "<TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * Tests translation of tables with colspan and/or rowspan attributes
	 */
	@Test
	public void testTableColspanAndRowspan()
	{
		final String htmlInput = "<table>" + "<tbody>" + "<tr>" + "<td>cell1.1</td>" + "<td>cell1.2</td>" + "<td>cell1.3</td>"
				+ "</tr>" + "<tr>" + "<td>cell2.1</td>" + "<td rowspan=\"2\">cell2.2_cell3.2</td>" + "<td>cell2.3</td>" + "</tr>"
				+ "<tr>" + "<td>cell3.1</td>" + "<td>cell3.3</td>" + "</tr>" + "<tr>"
				+ "<td colspan=\"3\">cell4.1_cell4.2_cell4.3</td>" + "</tr>" + "</tbody>" + "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:>" + "<ColStart:>" + "<ColStart:>"
				+ "<RowStart:>" + "<CellStart:1,1>cell1.1<CellEnd:>" + "<CellStart:1,1>cell1.2<CellEnd:>"
				+ "<CellStart:1,1>cell1.3<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>cell2.1<CellEnd:>"
				+ "<CellStart:2,1>cell2.2_cell3.2<CellEnd:>" + "<CellStart:1,1>cell2.3<CellEnd:>" + "<RowEnd:>" + "<RowStart:>"
				+ "<CellStart:1,1>cell3.1<CellEnd:>" + "<CellStart:1,1><CellEnd:>"
				+ // This is an empty cell that is "overspanned" by cell2.2 and cell3.2
				"<CellStart:1,1>cell3.3<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,3>cell4.1_cell4.2_cell4.3<CellEnd:>"
				+ "<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned" by cell4.1 and cell4.2 and cell4.3
				"<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned" by cell4.1 and cell4.2 and cell4.3
				"<RowEnd:>" + "<TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * table tests for task PLA-6506
	 */
	@Test
	public void testTableBorders()
	{
		testTableBorder("2");
		testTableBorder("1.75");
		testTableBorder("2.0");
		testTableBorder("4.25");
		testTableBorderSupressed("4"); //suppressed its really border = 1
		testTableBorderSupressed("4.0"); //suppressed its really border = 1
	}

	/**
	 * Tests translation of tables with border attribute
	 */
	public void testTableBorderSupressed(final String border)
	{
		//final int border = 2;		
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>cell1.1</td>" + "<td>cell1.2</td>"
				+ "</tr>" + "<tr>" + "<td>cell2.1</td>" + "<td>cell2.2</td>" + "</tr>" + "</tbody>" + "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final String out = "<TableStart:2,2:0:0<tCellDefaultCellType:Text>><ColStart:><ColStart:>"
				+ "<RowStart:><CellStart:1,1>cell1.1<CellEnd:>" + "<CellStart:1,1>cell1.2<CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1>cell2.1<CellEnd:>" + "<CellStart:1,1>cell2.2<CellEnd:><RowEnd:><TableEnd:>";

		assertEquals(out, in);
	}

	public void testTableBorder(final String border)
	{
		//final int border = 2;
		final float borderFloat = Float.parseFloat(border) * 0.25f;
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>cell1.1</td>" + "<td>cell1.2</td>"
				+ "</tr>" + "<tr>" + "<td>cell2.1</td>" + "<td>cell2.2</td>" + "</tr>" + "</tbody>" + "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final String out = "<TableStart:2,2:0:0<tCellDefaultCellType:Text>>" + "<ColStart:>" + "<ColStart:>" + "<RowStart:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrRightStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrTopStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderFloat
				+ ">>cell1.1<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrRightStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrTopStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderFloat
				+ ">>cell1.2<CellEnd:>"
				+ "<RowEnd:>"
				+ "<RowStart:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrRightStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrTopStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderFloat
				+ ">>cell2.1<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrRightStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrTopStrokeWeight:"
				+ borderFloat
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderFloat
				+ ">>cell2.2<CellEnd:>" + "<RowEnd:>" + "<TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * Tests translation of tables with headers and/or footers
	 */
	@Test
	public void testTableHeadersAndFooters()
	{
		final String htmlInput = "<table>" + "<thead>" + "<tr>" + "<td>header1.1</td>" + "<td>header1.2</td>"
				+ "<td>header1.3</td>" + "</tr>" + "</thead>" + "<tbody>" + "<tr>" + "<td>cell1.1</td>" + "<td>cell1.2</td>"
				+ "<td>cell1.3</td>" + "</tr>" + "<tr>" + "<td>cell2.1</td>" + "<td>cell2.2</td>" + "<td>cell2.3</td>" + "</tr>"
				+ "</tbody>" + "<tfoot>" + "<tr>" + "<td>footer1.1</td>" + "<td>footer1.2</td>" + "<td>footer1.3</td>" + "</tr>"
				+ "</tfoot>" + "</table>";
		final String in = translator.renderTextFromNode(translator.createNodesTree(htmlInput));

		final String out = "<TableStart:4,3:1:1<tCellDefaultCellType:Text>>" + "<ColStart:>" + "<ColStart:>" + "<ColStart:>"
				+ "<RowStart:>" + "<CellStart:1,1>header1.1<CellEnd:>" + "<CellStart:1,1>header1.2<CellEnd:>"
				+ "<CellStart:1,1>header1.3<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>cell1.1<CellEnd:>"
				+ "<CellStart:1,1>cell1.2<CellEnd:>" + "<CellStart:1,1>cell1.3<CellEnd:>" + "<RowEnd:>" + "<RowStart:>"
				+ "<CellStart:1,1>cell2.1<CellEnd:>" + "<CellStart:1,1>cell2.2<CellEnd:>" + "<CellStart:1,1>cell2.3<CellEnd:>"
				+ "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>footer1.1<CellEnd:>" + "<CellStart:1,1>footer1.2<CellEnd:>"
				+ "<CellStart:1,1>footer1.3<CellEnd:>" + "<RowEnd:>" + "<TableEnd:>";

		assertEquals(out, in);
	}

	@Test
	public void testColRowSpanPLA6466()
	{

		final String inputString = "<table border=\"1\"><tbody><tr><td colspan=\"2\" rowspan=\"2\">2x2 Feld</td><td>1</td><td>2</td><td>3</td></tr>"
				+ "<tr><td>4</td><td>5</td><td>6</td></tr>"
				+ "<tr><td>a</td><td>b</td><td colspan=\"3\" rowspan=\"3\">3x3 Feld</td></tr>"
				+ "<tr><td>c</td><td>d</td></tr>"
				+ "<tr><td>e</td><td>f</td></tr></tbody></table>";
		final AbstractNode genericTableNode = translator.createNodesTree(inputString);
		assertNotNull(genericTableNode);
		assertNotNull(genericTableNode.getChildNodeByName("table"));
		assertTrue(genericTableNode.getChildNodeByName("table") instanceof TableNode);
		final TableNode tableNode = (TableNode) genericTableNode.getChildNodeByName("table");
		assertEquals(tableNode.getCols(), 5);
		assertEquals(tableNode.getRows(), 5);
		final String in = translator.renderTextFromNode(tableNode);

		final String out = "<TableStart:5,5:0:0<tCellDefaultCellType:Text>><ColStart:><ColStart:><ColStart:><ColStart:><ColStart:>"
				+ "<RowStart:><CellStart:2,2<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>2x2 Feld<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>1<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>2<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>3<CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>4<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>5<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>6<CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>a<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>b<CellEnd:>"
				+ "<CellStart:3,3<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>3x3 Feld<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>c<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>d<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>e<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>>f<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:0.25><tCellAttrRightStrokeWeight:0.25><tCellAttrTopStrokeWeight:0.25><tCellAttrBottomStrokeWeight:0.25>><CellEnd:><RowEnd:><TableEnd:>";
		assertEquals(out, in);
	}

	/**
	 * <table border="2" >
	 * <tbody>
	 * <tr>
	 * <td colspan="2" rowspan="2">2x2 Feld(1)</td>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td>e</td>
	 * <td>f</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * Should count columns = 4 and rows = 2
	 */
	@Test
	public void testColRowSpan2PLA6466()
	{

		//final String inputString = "<table ><tbody><tr><td colspan=\"2\" rowspan=\"2\">2x2 Feld(1)</td><td>1</td><td colspan=\"2\" rowspan=\"2\">2x2 Feld(3)</td></tr>"
		//		+ "<tr><td>e</td><td>f</td><td>g</td></tr></tbody></table>";
		final String inputString = "<table ><tbody><tr><td colspan=\"2\" rowspan=\"2\">2x2 Feld(1)</td><td>1</td></tr>"
				+ "<tr><td>e</td><td>f</td></tr></tbody></table>";
		final AbstractNode genericTableNode = translator.createNodesTree(inputString);
		assertNotNull(genericTableNode);
		assertNotNull(genericTableNode.getChildNodeByName("table"));
		assertTrue(genericTableNode.getChildNodeByName("table") instanceof TableNode);
		final TableNode tableNode = (TableNode) genericTableNode.getChildNodeByName("table");
		assertEquals(tableNode.getCols(), 4/* 7 */);
		assertEquals(tableNode.getRows(), 2);
		final String in = translator.renderTextFromNode(tableNode);

		final String out = "<TableStart:2,4:0:0<tCellDefaultCellType:Text>><ColStart:><ColStart:><ColStart:><ColStart:>"
				+ "<RowStart:><CellStart:2,2>2x2 Feld(1)<CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>1<CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>e<CellEnd:><CellStart:1,1>f<CellEnd:><RowEnd:><TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * <table border="2" >
	 * <tbody>
	 * <tr>
	 * <td colspan="2" rowspan="2">2x2 Feld(1)</td>
	 * <td>1</td>
	 * <td colspan="2" rowspan="3">2x2 Feld(3)</td>
	 * </tr>
	 * <tr>
	 * <td>e</td>
	 * <td>f</td>
	 * <td>g</td>
	 * </tr>
	 * <tr>
	 * <td>h</td>
	 * <td>i</td>
	 * <td>j</td>
	 * <td>k</td>
	 * <td>l</td>
	 * <td>m</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * test renders above table into model with 8 columns and 3 rows
	 */
	@Test
	public void testColRowSpan3PLA6466()
	{

		final String inputString = "<table><tbody><tr><td colspan=\"2\" rowspan=\"2\">2x2 Feld(1)</td><td>1</td><td colspan=\"2\" rowspan=\"3\">2x2 Feld(3)</td></tr>"
				+ "<tr><td>e</td><td>f</td><td>g</td></tr><tr><td>h</td><td>i</td><td>j</td><td>k</td><td>l</td><td>m</td></tr></tbody></table>";
		final AbstractNode genericTableNode = translator.createNodesTree(inputString);
		assertNotNull(genericTableNode);
		assertNotNull(genericTableNode.getChildNodeByName("table"));
		assertTrue(genericTableNode.getChildNodeByName("table") instanceof TableNode);
		final TableNode tableNode = (TableNode) genericTableNode.getChildNodeByName("table");
		assertEquals(tableNode.getCols(), 8);
		assertEquals(tableNode.getRows(), 3);
		final String in = translator.renderTextFromNode(tableNode);

		final String out = "<TableStart:3,8:0:0<tCellDefaultCellType:Text>><ColStart:><ColStart:><ColStart:><ColStart:><ColStart:><ColStart:><ColStart:><ColStart:>"
				+ "<RowStart:><CellStart:2,2>2x2 Feld(1)<CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>1<CellEnd:><CellStart:3,2>2x2 Feld(3)<CellEnd:><CellStart:1,1><CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>e<CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>f<CellEnd:><CellStart:1,1>g<CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1>h<CellEnd:><CellStart:1,1>i<CellEnd:><CellStart:1,1>j<CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>k<CellEnd:><CellStart:1,1>l<CellEnd:><CellStart:1,1>m<CellEnd:><RowEnd:>"
				+ "<TableEnd:>";

		assertEquals(out, in);
	}

	/**
	 * <table cellspacing="20" cellpadding="5" border="2" >
	 * <tbody>
	 * <tr>
	 * <td colspan="2" rowspan="2">2x2 Feld(1)</td>
	 * <td>1</td>
	 * <td colspan="2" rowspan="2">2x2 Feld(3)</td>
	 * </tr>
	 * <tr>
	 * <td>e</td>
	 * <td>f</td>
	 * <td>g</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	@Test
	public void testColRowSpan4PLA6466()
	{

		final String inputString = "<table><tbody>"
				+ "<tr><td colspan=\"2\" rowspan=\"2\">2x2 Feld(1)</td><td>1</td><td colspan=\"2\" rowspan=\"2\">2x2 Feld(3)</td></tr>"
				+ "<tr><td>e</td><td>f</td><td>g</td></tr></tbody></table>";
		final AbstractNode genericTableNode = translator.createNodesTree(inputString);
		assertNotNull(genericTableNode);
		assertNotNull(genericTableNode.getChildNodeByName("table"));
		assertTrue(genericTableNode.getChildNodeByName("table") instanceof TableNode);
		final TableNode tableNode = (TableNode) genericTableNode.getChildNodeByName("table");
		assertEquals(tableNode.getCols(), 7);
		assertEquals(tableNode.getRows(), 2);
		final String in = translator.renderTextFromNode(tableNode);

		final String out = "<TableStart:2,7:0:0<tCellDefaultCellType:Text>><ColStart:><ColStart:><ColStart:><ColStart:><ColStart:><ColStart:><ColStart:>"
				+ "<RowStart:><CellStart:2,2>2x2 Feld(1)<CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>1<CellEnd:><CellStart:2,2>2x2 Feld(3)<CellEnd:><CellStart:1,1><CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>e<CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>f<CellEnd:><CellStart:1,1>g<CellEnd:><RowEnd:>"
				+ "<TableEnd:>";
		assertEquals(out, in);
	}

	@Test
	public void testColRowSpanPLA6457()
	{

		final String inputString = "<table class=\"tableClassWithSpace \"><tbody><tr><td colspan=\"2\" rowspan=\"2\" class=\"tdClassWithSpace \">2x2 Feld</td><td>1</td><td>2</td><td>3</td></tr>"
				+ "<tr class=\"trClassWithSpace \"><td>4</td><td>5</td><td>6</td></tr>"
				+ "<tr><td>a</td><td>b</td><td colspan=\"3\" rowspan=\"3\">3x3 Feld</td></tr>"
				+ "<tr><td>c</td><td>d</td></tr>"
				+ "<tr><td>e</td><td>f</td></tr></tbody></table>";
		final AbstractNode genericTableNode = translator.createNodesTree(inputString);
		final TableNode tableNode = (TableNode) genericTableNode.getChildNodeByName("table");
		final String in = translator.renderTextFromNode(tableNode);

		final String out = "<TableStyle:tableClassWithSpace>"
				+ "<TableStart:5,5:0:0<tCellDefaultCellType:Text>>"
				+ "<ColStart:><ColStart:><ColStart:><ColStart:><ColStart:>"
				+ "<RowStart:><CellStyle:tdClassWithSpace><StylePriority:1><CellStart:2,2>2x2 Feld<CellEnd:><CellStart:1,1><CellEnd:>"
				+ "<CellStart:1,1>1<CellEnd:><CellStart:1,1>2<CellEnd:><CellStart:1,1>3<CellEnd:><RowEnd:><RowStart:><CellStyle:trClassWithSpace><StylePriority:1><CellStart:1,1><CellEnd:>"
				+ "<CellStyle:trClassWithSpace><StylePriority:1><CellStart:1,1><CellEnd:>"
				+ "<CellStyle:trClassWithSpace><StylePriority:1><CellStart:1,1>4<CellEnd:>"
				+ "<CellStyle:trClassWithSpace><StylePriority:1><CellStart:1,1>5<CellEnd:>"
				+ "<CellStyle:trClassWithSpace><StylePriority:1><CellStart:1,1>6<CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1>a<CellEnd:><CellStart:1,1>b<CellEnd:><CellStart:3,3>3x3 Feld<CellEnd:>"
				+ "<CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1>c<CellEnd:><CellStart:1,1>d<CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1>e<CellEnd:><CellStart:1,1>f<CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><RowEnd:>"
				+ "<TableEnd:>";

		assertEquals(out, in);


	}

	/**
	 * test rendering cellspacing and cellpadding
	 */
	@Test
	public void testColRowSpanPLA5827()
	{

		final int spacing = 20;
		final NumberFormat formatter = new DecimalFormat("#.##############"); // max 14 decimal values
		final String paddingOneSide = formatter.format((spacing) * CommonsConstants.PIXEL_MULTIPLIER_DEFAULT);

		final String inputString = "<table cellspacing=\"" + spacing
				+ "\"><tbody><tr><td colspan=\"2\" rowspan=\"2\">2x2 Feld</td><td>1</td></tr>" +
				//+ "<tr><td>4</td><td>5</td><td>6</td></tr>" //+ "<tr><td>a</td><td>b</td><td>3x3 Feld</td></tr>" //+
				"<tr><td>c</td><td>d</td></tr>" + "<tr><td>e</td><td>f</td></tr></tbody></table>";
		final AbstractNode genericTableNode = translator.createNodesTree(inputString);
		final TableNode tableNode = (TableNode) genericTableNode.getChildNodeByName("table");
		final String in = translator.renderTextFromNode(tableNode);

		final String out = "<TableStart:3,4:0:0<tCellDefaultCellType:Text<tBeforeSpace:"
				+ paddingOneSide
				+ "><tAfterSpace:"
				+ paddingOneSide
				+ ">>><ColStart:><ColStart:><ColStart:><ColStart:>"
				+ "<RowStart:><CellStart:2,2>2x2 Feld<CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>1<CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1><CellEnd:><CellStart:1,1><CellEnd:><CellStart:1,1>c<CellEnd:><CellStart:1,1>d<CellEnd:><RowEnd:>"
				+ "<RowStart:><CellStart:1,1>e<CellEnd:><CellStart:1,1>f<CellEnd:><RowEnd:><TableEnd:>";

		assertEquals(out, in);
	}

}
