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
import de.hybris.platform.print.util.translator.EscapingIndesignPrerenderer;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests common translator functionality with the HTML-to-InDesign-TaggedText configuration
 */
public class TranslatorTest
{

	private static final Logger LOG = Logger.getLogger(TranslatorTest.class.getName()); //NOPMD

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
	// The "Nonbreaking Space" tag
	private final static String NONBREAKING_SPACE = "<0x00A0>";

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
			config.addPrerenderer(new EscapingIndesignPrerenderer());
			translator = new Translator(config);
		}
		catch (final RuntimeException e)
		{
			throw e;
		}
		finally
		{
			translatorRenderersIndesign_IS.close();
			indesignProperties_IS.close();
			translatorParsersHtml_IS.close();
		}
	}

	/**
	 * new line tag translation test The BR tag should be translated to a so called "forced LineBreak"
	 */
	@Test
	public void testNewLine()
	{
		final String htmlInput = "First line<br/>Second line";
		final String expected = "First line" + FORCED_LINEBREAK + "Second line";
		final String result = translator.translate(htmlInput);
		assertEquals(expected, result);
	}

	/**
	 * new line tag translation test The BR tag should be translated to a so called "forced LineBreak". This is test for unclosed
	 * single br tag - \<br\>
	 * . .
	 */
	@Test
	public void testNewLineWOTagClose()
	{
		final String htmlInput = "First line<br>Second line";
		final String expected = "First line" + FORCED_LINEBREAK + "Second line";
		final String result = translator.translate(htmlInput);
		assertEquals(expected, result);
	}

	/**
	 * empty paragraph tag test.
	 */
	@Test
	public void testEmptyParagraph()
	{
		final String htmlInput = "First line<p/>Second line";
		final String expected = "First line" + "Second line";
		final String result = translator.translate(htmlInput);
		assertEquals(expected, result);
	}

	/**
	 * nested bold tag translation test
	 */
	@Test
	public void testTagDefinedInXML()
	{
		final String htmlInput = "<b>aa<b>bb</b>cc</b>";
		final String expected = "<cTypeface:Bold>aa<cTypeface:>" + "<cTypeface:Bold>bb<cTypeface:>"
				+ "<cTypeface:Bold>cc<cTypeface:>";
		final String result = translator.translate(htmlInput);
		assertEquals(expected, result);
	}

	/**
	 * Entities testing
	 */
	@Test
	public void testEntity()
	{
		String htmlInput = "Translated entities in the format [Name-entity] [Number-entity]: "
				+
				// C1 Controls and Latin-1 Supplement, 128 - 255 (x0080 bis x00FF)
				"nbsp/white space [&nbsp;] [&#160;], "
				+ // nonbreaking space, not only a white space
				"cent symbol [&cent;] [&#162;], " + "currency symbol [&curren;] [&#164;], " + "copyright [&copy;] [&#169;], "
				+ "degree symbol [&deg;] [&#176;], " + "super two/square [&sup2;] [&#178;], "
				+ "paragraph symbol [&para;] [&#182;], " + "capital U umlaut [&Uuml;] [&#220;], "
				+ "small o umlaut [&ouml;] [&#246;], "
				+

				// Latin Extended-A, 256 - 383 (x0100 bis x017F)
				"small OE ligature [&oelig;] [&#339;], " + "small S with caron (Hacek) [&scaron;] [&#353;], "
				+

				// others
				"greek Theta [&Theta;] [&#920;], " + "permills [&permil;] [&#8240;], " + "empty set [&empty;] [&#8709;],"
				+ "less than(<) [&lt;] [&#60;]." + "greater than(>) [&gt;] [&#62;].";
		String expected = "Translated entities in the format [Name-entity] [Number-entity]: "
				+
				// C1 Controls and Latin-1 Supplement, 128 - 255 (x0080 bis x00FF)
				"nbsp/white space [" + NONBREAKING_SPACE
				+ "] ["
				+ NONBREAKING_SPACE
				+ "], "
				+ // nonbreaking space, not only a white space
				"cent symbol [\u00a2] [\u00a2], " + "currency symbol [\u00a4] [\u00a4], " + "copyright [\u00a9] [\u00a9], "
				+ "degree symbol [\u00b0] [\u00b0], " + "super two/square [\u00b2] [\u00b2], "
				+ "paragraph symbol [\u00b6] [\u00b6], " + "capital U umlaut [\u00dc] [\u00dc], "
				+ "small o umlaut [\u00f6] [\u00f6], "
				+

				// Latin Extended-A, 256 - 383 (x0100 bis x017F)
				"small OE ligature [\u0153] [\u0153], " + "small S with caron (Hacek) [\u0161] [\u0161], "
				+

				// others
				"greek Theta [\u0398] [\u0398], " + "permills [\u2030] [\u2030], " + "empty set [\u2205] [\u2205],"
				+ "less than(\\<) [\\<] [\\<]." + // "<" has to be escaped in InDesign
				"greater than(\\>) [\\>] [\\>]."; // ">" has to be escaped in InDesign
		String result = translator.translate(htmlInput);
		assertEquals(expected, result);


		//entities and tags
		htmlInput = "<b>aa&nbsp;bb&pound;<b>cc</b>&Aring;</b>";
		expected = "<cTypeface:Bold>aa" + NONBREAKING_SPACE
				+ "bb\u00a3<cTypeface:><cTypeface:Bold>cc<cTypeface:><cTypeface:Bold>\u00c5<cTypeface:>";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		//span with defined class
		htmlInput = "<span class=\"Velocity\">aa&nbsp;bb&pound;<b>cc</b>&Aring;</span>";
		expected = "<CharStyle:Velocity>aa" + NONBREAKING_SPACE + "bb\u00a3<cTypeface:Bold>cc<cTypeface:>\u00c5<CharStyle:>";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		//span without defined class
		htmlInput = "<span>aa&nbsp;bb&pound;<b>cc</b>&Aring;</span>";
		expected = "aa" + NONBREAKING_SPACE + "bb\u00a3<cTypeface:Bold>cc<cTypeface:>\u00c5";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);
	}


	/**
	 * Tests the closing CharStyle tag for certain HTML tags
	 */
	@Test
	public void testClosingCharStyle()
	{
		// Test <span>
		String htmlInput = "This is a <span class=\"mySpanStyle\">tiny little</span> example text";
		String expected = "This is a <CharStyle:mySpanStyle>tiny little<CharStyle:> example text";
		String result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Test <b>
		htmlInput = "This is a <b>tiny little</b> example text";
		expected = "This is a <cTypeface:Bold>tiny little<cTypeface:> example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Test <strong>
		htmlInput = "This is a <strong>tiny little</strong> example text";
		expected = "This is a <cTypeface:Bold>tiny little<cTypeface:> example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Test <i>
		htmlInput = "This is a <i>tiny little</i> example text";
		expected = "This is a <cTypeface:Italic>tiny little<cTypeface:> example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Test <u>
		htmlInput = "This is a <u>tiny little</u> example text";
		expected = "This is a <cUnderline:1>tiny little<cUnderline:> example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Test <s>
		htmlInput = "This is a <s>tiny little</s> example text";
		expected = "This is a <cStrikethru:1>tiny little<cStrikethru:> example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Test <strike>
		htmlInput = "This is a <strike>tiny little</strike> example text";
		expected = "This is a <cStrikethru:1>tiny little<cStrikethru:> example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Test <sup>
		htmlInput = "This is a <sup>tiny little</sup> example text";
		expected = "This is a <cPosition:Superscript>tiny little<cPosition:> example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Test <sub>
		htmlInput = "This is a <sub>tiny little</sub> example text";
		expected = "This is a <cPosition:Subscript>tiny little<cPosition:> example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);
	}


	/**
	 * Tests the creation of multiple ParaStyle tags within one script/translation
	 */
	@Test
	public void testMultipleParaStyles()
	{
		// No additional ParaStyle required
		String htmlInput = "<p class=\"aParaStyle\">This is a <b>tiny little</b> example text</p>";
		String expected = "<ParaStyle:aParaStyle>This is a <cTypeface:Bold>tiny little<cTypeface:> example text";
		String result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Additional ParaStyle required
		htmlInput = "<p class=\"aParaStyle\">This is a <ul><li>tiny</li><li>little</li></ul>" + "example text</p>";
		expected = "<ParaStyle:aParaStyle>This is a " + PARAGRAPH_BREAK
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>tiny<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>little<bnListType:>" + PARAGRAPH_BREAK
				+ "<ParaStyle:aParaStyle>example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// Mixing both cases
		htmlInput = "<p class=\"aParaStyle\">This is a <ul><li><b>tiny</b></li><li>little</li></ul> example text</p>";
		expected = "<ParaStyle:aParaStyle>This is a " + PARAGRAPH_BREAK
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet><cTypeface:Bold>tiny<cTypeface:><bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>little<bnListType:>" + PARAGRAPH_BREAK
				+ "<ParaStyle:aParaStyle> example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);
	}


	/**
	 * Tests nested and nesting tags test case for JIRA task http://jira.hybris.de/browse/PLA-6334
	 */
	@Test
	public void testNestedCharStyles_PLA_6334()
	{// nested HTML tags 1
		final String htmlInput = "<i>1<i>2</i>3<b>4</b>5</i>6";
		final String expected = "<cTypeface:Italic>1<cTypeface:>" + "<cTypeface:Italic>2<cTypeface:>"
				+ "<cTypeface:Italic>3<cTypeface:>" + "<cTypeface:Bold Italic>4<cTypeface:>" + "<cTypeface:Italic>5<cTypeface:>"
				+ "6";
		final String result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// nested HTML tags 1
		final String htmlInput2 = "<b>1<i>2</i>3<b>4</b>5</b>6";
		final String expected2 = "<cTypeface:Bold>1<cTypeface:>" + "<cTypeface:Bold Italic>2<cTypeface:>"
				+ "<cTypeface:Bold>3<cTypeface:>" + "<cTypeface:Bold>4<cTypeface:>" + "<cTypeface:Bold>5<cTypeface:>" + "6";
		final String result2 = translator.translate(htmlInput2);
		assertEquals(expected2, result2);
	}

	/**
	 * Tests nested and nesting tags
	 */
	@Test
	public void testNestedCharStyles()
	{
		// nested HTML tags 1
		String htmlInput = "<i>1<i>2</i>3<b>4</b>5</i>6";
		String expected = "<cTypeface:Italic>1<cTypeface:>" + "<cTypeface:Italic>2<cTypeface:>" + "<cTypeface:Italic>3<cTypeface:>"
				+ "<cTypeface:Bold Italic>4<cTypeface:>" + "<cTypeface:Italic>5<cTypeface:>" + "6";
		String result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// nested HTML tags 2
		htmlInput = "<i class=\"myItalicStyle1\">1<i>2</i>3<i><i class=\"myItalicStyle2\">4</i></i><i>5</i>6</i>7";
		expected = "<cTypeface:Italic>1<cTypeface:>" + "<cTypeface:Italic>2<cTypeface:>" + "<cTypeface:Italic>3<cTypeface:>"
				+ "<cTypeface:Italic><cTypeface:><cTypeface:Italic>4<cTypeface:>" + "<cTypeface:Italic>5<cTypeface:>"
				+ "<cTypeface:Italic>6<cTypeface:>" + "7";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// nested HTML tags 3
		htmlInput = "<p class=\"aParaStyle\">This <b>is a tiny <i>little</i> example</b> text</p>";
		expected = "<ParaStyle:aParaStyle>This <cTypeface:Bold>is a tiny <cTypeface:><cTypeface:Bold Italic>little<cTypeface:><cTypeface:Bold> example<cTypeface:> text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// mixed nested HTML tags 1
		htmlInput = "<strong>This <strike>is a <span class=\"mySpanStyle\">tiny</span> little</strike> example</strong> text";
		expected = "<cTypeface:Bold>This <cStrikethru:1>is a <cStrikethru:><cTypeface:><CharStyle:mySpanStyle><cTypeface:Bold><cStrikethru:1>tiny<cStrikethru:><cTypeface:><CharStyle:><cTypeface:Bold><cStrikethru:1> little<cStrikethru:> example<cTypeface:> text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);
		// TODO: activate Tests again and remove log/warning

		// mixed nested HTML tags 2
		htmlInput = "<i>This <strike>is a <span class=\"mySpanStyle\">tiny</span> little</strike> example</i> text";
		expected = "<cTypeface:Italic>This <cStrikethru:1>is a <cStrikethru:><cTypeface:><CharStyle:mySpanStyle><cTypeface:Italic><cStrikethru:1>tiny<cStrikethru:><cTypeface:><CharStyle:><cTypeface:Italic><cStrikethru:1> little<cStrikethru:> example<cTypeface:> text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);
		// TODO: activate Tests again and remove log/warning

		// mixed nested HTML tags 3
		htmlInput = "<span class=\"mySpanStyle\">This is a <span class=\"anotherSpanStyle\"><b>tiny <u>little</u></b></span> <i>example</i> text</span>";
		expected = "<CharStyle:mySpanStyle>This is a <CharStyle:><CharStyle:anotherSpanStyle><cTypeface:Bold>tiny <cUnderline:1>little<cUnderline:><cTypeface:><CharStyle:><CharStyle:mySpanStyle> <cTypeface:Italic>example<cTypeface:> text<CharStyle:>";
		result = translator.translate(htmlInput);
		// assertEquals( expected, result );
		// TODO: activate Tests again and remove log/warning
	}

	/**
	 * Tests translation of ul/li structures in case as in <a href="http://jira.hybris.de/browse/PLA-6439">PLA-6439</a>
	 */
	@Test
	public void testTableEmptyULItems_PLA_6439()
	{
		final String htmlInput = "<ul><li>item1</li><li>item2<li></ul>";
		final String out = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		final String expected = "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>item1<bnListType:><ParaStyle:NormalParagraphStyle><bnListType:Bullet>item2<bnListType:><ParaStyle:NormalParagraphStyle><bnListType:Bullet><bnListType:>";
		assertEquals(expected, out);
		final String htmlInputEmpty = "<ul></ul><ul><li></li></ul>";
		final String outEmpty = translator.renderTextFromNode(translator.createNodesTree(htmlInputEmpty));
		final String expected2 = PARAGRAPH_BREAK + "<ParaStyle:NormalParagraphStyle><bnListType:Bullet><bnListType:>";
		assertEquals(expected2, outEmpty);

	}

	/**
	 * Test of the using of paragraph.vm script especially considering <a href="http://jira.hybris.de/browse/PLA-6318">PLA-6318</a> <br/>
	 * Following test cases have been defined: <br/>
	 * TC1: no part of the table, no first paragraph, main node - paragraph break <b>(impossible case)</b><br/>
	 * TC2: no part of the table, no first paragraph, no main node - paragraph break<br/>
	 * TC3: no part of the table, first paragraph, main node - <b>no</b> paragraph break <br/>
	 * TC4: no part of the table, first paragraph, no main node - paragraph break <br/>
	 * TC5: part of the table, no first paragraph - paragraph break <b>[PLA-6318]</b><br/>
	 * TC6: part of the table, first paragraph - <b>no</b> paragraph break <b>[PLA-6318]</b><br/>
	 */
	@Test
	public void testParagraphBreak_PLA_6318()
	{
		//TC2
		String htmlInput = "<p><b><p>TC2</p></b></p>";
		String indesignExpected = "<ParaStyle:NormalParagraphStyle><cTypeface:Bold><ParaStyle:NormalParagraphStyle>TC2<cTypeface:>";
		String indesignResult = translator.translate(htmlInput);
		assertEquals(indesignExpected, indesignResult);
		//TC3
		htmlInput = "<p>TC3</p>";
		indesignExpected = "<ParaStyle:NormalParagraphStyle>TC3";
		indesignResult = translator.translate(htmlInput);
		assertEquals(indesignExpected, indesignResult);
		//TC4
		htmlInput = "<b><p>TC4</p></b>";
		indesignExpected = "<cTypeface:Bold><ParaStyle:NormalParagraphStyle>TC4<cTypeface:>";
		indesignResult = translator.translate(htmlInput);
		assertEquals(indesignExpected, indesignResult);

		//TC5
		htmlInput = "<table><tr><td><p><b><p>TC6</p></b></p></td></tr></table>";
		indesignExpected = "<TableStart:1,1:0:0<tCellDefaultCellType:Text>><ColStart:><RowStart:><CellStart:1,1><ParaStyle:NormalParagraphStyle><cTypeface:Bold><ParaStyle:NormalParagraphStyle>TC6<cTypeface:><CellEnd:><RowEnd:><TableEnd:>";
		indesignResult = translator.translate(htmlInput);
		assertEquals(indesignExpected, indesignResult);
		//TC6
		htmlInput = "<table><tr><td><p>TC6</p></td></tr></table>";
		indesignExpected = "<TableStart:1,1:0:0<tCellDefaultCellType:Text>><ColStart:><RowStart:><CellStart:1,1><ParaStyle:NormalParagraphStyle>TC6<CellEnd:><RowEnd:><TableEnd:>";
		indesignResult = translator.translate(htmlInput);
		assertEquals(indesignExpected, indesignResult);
	}

}
