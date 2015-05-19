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
package de.hybris.platform.print.commons.test.jalo;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.commons.translator.RenderersFactory;
import de.hybris.platform.commons.translator.RenderersFactoryFromFile;
import de.hybris.platform.commons.translator.Translator;
import de.hybris.platform.commons.translator.TranslatorConfiguration;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests common translator functionality for plainText configuration
 */
public class TranslatorPlainTextTest
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TranslatorPlainTextTest.class.getName());

	// name of configuration file that should be in the same source folder as this class
	private static final String HTML_PARSERS_CONFIGURATION_FILE_NAME = "/commons/translator/translator_parsers_html.xml";
	// name of configuration file that should be in the same source folder as this class
	private static final String INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME = "/commons/translator/translator_renderers_plaintext.xml";
	private final static String NONBREAKING_SPACE = "<0x00A0>";


	private TranslatorConfiguration config;
	private Translator translator;

	@Before
	public void setUp() throws Exception
	{
		InputStream translatorRenderersIndesign_IS = null;
		InputStream translatorParsersHtml_IS = null;
		try
		{
			//load configuration and initialize translator
			translatorRenderersIndesign_IS = Translator.class.getResourceAsStream(INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME);
			final RenderersFactory renderersFactory = new RenderersFactoryFromFile(translatorRenderersIndesign_IS, null);
			translatorParsersHtml_IS = Translator.class.getResourceAsStream(HTML_PARSERS_CONFIGURATION_FILE_NAME);
			config = new TranslatorConfiguration(translatorParsersHtml_IS, renderersFactory);
			translator = new Translator(config);
		}
		finally
		{
			if (translatorRenderersIndesign_IS != null)
			{
				try
				{
					translatorRenderersIndesign_IS.close();
				}
				catch (final IOException e)
				{
					// fine
				}
			}
			if (translatorParsersHtml_IS != null)
			{
				try
				{
					translatorParsersHtml_IS.close();
				}
				catch (final IOException e)
				{
					// fine
				}
			}
		}
	}

	/**
	 * new line tag translation test The BR tag should be translated to a so called "forced LineBreak"
	 */
	@Test
	public void testNewLine()
	{
		final String htmlInput = "First line<br/>Second line";
		final String expected = "First line\nSecond line";
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
		final String expected = "First line\nSecond line";
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
	 * bold,underline,italic,strike,newline tags test.
	 */

	@Test
	public void testTags1()
	{
		final String htmlInput = "<b>Bold</b><u>Underline</u><i>Italic</i><s>Strike</s><br/>NewLine";
		final String expected = "Bold" + "Underline" + "Italic" + "Strike" + "\nNewLine";
		final String result = translator.translate(htmlInput);
		assertEquals(expected, result);
	}

	/**
	 * div,paragraph,span tags test.
	 */

	@Test
	public void testTags2()
	{
		final String htmlInput = "<div>Div</div><p>Paragraph</p><span>Span</span>";
		final String expected = "\nDiv\n" + "\nParagraph\n" + "Span";
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
		final String expected = "aa" + "bb" + "cc";
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
				+ // C1 Controls and Latin-1 Supplement, 128 - 255 (x0080 bis x00FF)
				"nbsp/white space [&nbsp;] [&#160;], "
				+ // nonbreaking space, not only a white space
				"cent symbol [&cent;] [&#162;], " + "currency symbol [&curren;] [&#164;], " + "copyright [&copy;] [&#169;], "
				+ "degree symbol [&deg;] [&#176;], " + "super two/square [&sup2;] [&#178;], "
				+ "paragraph symbol [&para;] [&#182;], " + "capital U umlaut [&Uuml;] [&#220;], "
				+ "small o umlaut [&ouml;] [&#246;], " +

				// Latin Extended-A, 256 - 383 (x0100 bis x017F)
				"small OE ligature [&oelig;] [&#339;], " + "small S with caron (Hacek) [&scaron;] [&#353;], " +

				// others
				"greek Theta [&Theta;] [&#920;], " + "permills [&permil;] [&#8240;], " + "empty set [&empty;] [&#8709;],";

		String expected = "Translated entities in the format [Name-entity] [Number-entity]: "
				+ // C1 Controls and Latin-1 Supplement, 128 - 255 (x0080 bis x00FF)
				"nbsp/white space [" + NONBREAKING_SPACE + "] ["
				+ NONBREAKING_SPACE
				+ "], "
				+ // nonbreaking space, not only a white space
				"cent symbol [\u00a2] [\u00a2], " + "currency symbol [\u00a4] [\u00a4], " + "copyright [\u00a9] [\u00a9], "
				+ "degree symbol [\u00b0] [\u00b0], " + "super two/square [\u00b2] [\u00b2], "
				+ "paragraph symbol [\u00b6] [\u00b6], " + "capital U umlaut [\u00dc] [\u00dc], "
				+ "small o umlaut [\u00f6] [\u00f6], " +

				// Latin Extended-A, 256 - 383 (x0100 bis x017F)
				"small OE ligature [\u0153] [\u0153], " + "small S with caron (Hacek) [\u0161] [\u0161], " +

				// others
				"greek Theta [\u0398] [\u0398], " + "permills [\u2030] [\u2030], " + "empty set [\u2205] [\u2205],";

		String result = translator.translate(htmlInput);
		assertEquals(expected, result);

		//entities and tags
		htmlInput = "<b>aa&nbsp;bb&pound;<b>cc</b>&Aring;</b>";
		expected = "aa" + NONBREAKING_SPACE + "bb\u00a3cc\u00c5";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		//span with defined class
		htmlInput = "<span class=\"Velocity\">aa&nbsp;bb&pound;<b>cc</b>&Aring;</span>";
		expected = "aa" + NONBREAKING_SPACE + "bb\u00a3cc\u00c5";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		//span without defined class
		htmlInput = "<span>aa&nbsp;bb&pound;<b>cc</b>&Aring;</span>";
		expected = "aa" + NONBREAKING_SPACE + "bb\u00a3cc\u00c5";
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
		final String htmlInput = "<p class=\"aParaStyle\">This is a <b>tiny little</b> example text</p>";
		final String expected = "\nThis is a tiny little example text\n";
		final String result = translator.translate(htmlInput);
		assertEquals(expected, result);
	}


	/**
	 * Tests nested and nesting tags test case for JIRA task http://jira.hybris.de/browse/PLA-6334
	 */
	@Test
	public void testNestedCharStylesPLA6334()
	{// nested HTML tags 1 
		final String htmlInput = "<i>1<i>2</i>3<b>4</b>5</i>6";
		final String expected = "1" + "2" + "3" + "4" + "5" + "6";
		final String result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// nested HTML tags 1 
		final String htmlInput2 = "<b>1<i>2</i>3<b>4</b>5</b>6";
		final String expected2 = "1" + "2" + "3" + "4" + "5" + "6";
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
		String expected = "1" + "2" + "3" + "4" + "5" + "6";
		String result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// nested HTML tags 2 
		htmlInput = "<i class=\"myItalicStyle1\">1<i>2</i>3<i><i class=\"myItalicStyle2\">4</i></i><i>5</i>6</i>7";
		expected = "1" + "2" + "3" + "4" + "5" + "6" + "7";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// nested HTML tags 3 
		htmlInput = "<p class=\"aParaStyle\">This <b>is a tiny <i>little</i> example</b> text</p>";
		expected = "\nThis is a tiny little example text\n";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);

		// mixed nested HTML tags 1
		htmlInput = "<strong>This <strike>is a <span class=\"mySpanStyle\">tiny</span> little</strike> example</strong> text";
		expected = "This is a tiny little example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);
		// TODO: activate Tests again and remove log/warning

		// mixed nested HTML tags 2
		htmlInput = "<i>This <strike>is a <span class=\"mySpanStyle\">tiny</span> little</strike> example</i> text";
		expected = "This is a tiny little example text";
		result = translator.translate(htmlInput);
		assertEquals(expected, result);
		// TODO: activate Tests again and remove log/warning

		// mixed nested HTML tags 3 
		htmlInput = "<span class=\"mySpanStyle\">This is a <span class=\"anotherSpanStyle\"><b>tiny <u>little</u></b></span> <i>example</i> text</span>";
		expected = "This is a tiny little example text";
		result = translator.translate(htmlInput);
		// assertEquals( expected, result );
		// TODO: activate Tests again and remove log/warning
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
	public void testParagraphBreakPLA6318()
	{
		//TC2
		String htmlInput = "<p><b><p>TC2</p></b></p>";
		String indesignExpected = "\n\nTC2\n\n";
		String indesignResult = translator.translate(htmlInput);
		assertEquals(indesignExpected, indesignResult);
		//TC3
		htmlInput = "<p>TC3</p>";
		indesignExpected = "\nTC3\n";
		indesignResult = translator.translate(htmlInput);
		assertEquals(indesignExpected, indesignResult);
		//TC4
		htmlInput = "<b><p>TC4</p></b>";
		indesignExpected = "\nTC4\n";
		indesignResult = translator.translate(htmlInput);
		assertEquals(indesignExpected, indesignResult);
	}

}
