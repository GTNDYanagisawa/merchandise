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
package de.hybris.platform.print.translator.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.commons.jalo.CommonsManager;
import de.hybris.platform.commons.jalo.translator.JaloTranslatorConfiguration;
import de.hybris.platform.commons.translator.RenderersFactory;
import de.hybris.platform.commons.translator.RenderersFactoryFromDB;
import de.hybris.platform.commons.translator.Translator;
import de.hybris.platform.commons.translator.TranslatorConfiguration;
import de.hybris.platform.commons.translator.nodes.AbstractNode;
import de.hybris.platform.commons.translator.nodes.SimpleNode;
import de.hybris.platform.commons.translator.parsers.HtmlSimpleParser;
import de.hybris.platform.core.Constants;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationType;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.print.util.translator.EscapingIndesignPrerenderer;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * this class tests renderers defined in database (they are imported from CSV file) <br/>
 */
public class HmcTranslatorTest extends HybrisJUnit4TransactionalTest
{

	/** Used encodings **/
	protected EnumerationValue utf8, windows1252;

	// name of file with properties
	public final static String INDESIGN_PROPERTIES_FILENAME = "/print/translator/indesign.properties";
	// The "forced line break" character
	private final static String FORCED_LINEBREAK = "<0x000A>";
	// The "paragraph break"
	private final static String PARAGRAPH_BREAK = String.valueOf((char) Integer.parseInt("000D", 16)) //NOPMD
			+ (char) Integer.parseInt("000A", 16);
	// The "Nonbreaking Space" tag
	@SuppressWarnings("unused")
	private final static String NONBREAKING_SPACE = "<0x00A0>";
	private TranslatorConfiguration config;
	private Translator translator;

	@Before
	public void setUp() throws Exception
	{

		final EnumerationType encodingEnum = EnumerationManager.getInstance().getEnumerationType(Constants.ENUMS.ENCODINGENUM);
		try
		{
			utf8 = EnumerationManager.getInstance().getEnumerationValue(encodingEnum, "utf-8");
		}
		catch (final JaloItemNotFoundException e)
		{
			utf8 = EnumerationManager.getInstance().createEnumerationValue(encodingEnum, "utf-8");
			assertNotNull(utf8);
		}

		final InputStream is = CommonsManager.class.getResourceAsStream("/print/translator/startData.csv");
		ImpExManager.getInstance().importData(is, "utf-8", true);

		final JaloTranslatorConfiguration translatorConfig = CommonsManager.getInstance().getJaloTranslatorConfigurationByCode(
				"DefaultTranslatorConfiguration");
		assertNotNull(translatorConfig);

		final RenderersFactory renderersFactory = new RenderersFactoryFromDB(translatorConfig, JaloSession.getCurrentSession()
				.getSessionContext());
		// renderersFactory cannot be null!!!
		//		if (renderersFactory. == null)
		//		{
		//			throw new JaloItemNotFoundException("Did not find RenderersFactory for renderers group [" + RENDERERS_GROUP_CODE
		//					+ "] and properties [" + RENDERERS_PROPERTIES_CODE + "]", 0);
		//		}

		config = new TranslatorConfiguration(translatorConfig.getParserPropertiesAsMap(), renderersFactory);
		config.addPrerenderer(new EscapingIndesignPrerenderer());
		translator = new Translator(config);
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

		children.add(new SimpleNode("bold", "bold", htmlSimpleParser.generateAttributes("")));
		children.add(new SimpleNode("textNode", "normal", htmlSimpleParser.generateAttributes("")));
		children.add(new SimpleNode("bold", "bold", htmlSimpleParser.generateAttributes("")));
		children.add(new SimpleNode("textNode", "normal", htmlSimpleParser.generateAttributes("")));

		mainNode.setChildNodes(children);

		final String expected = "<cTypeface:Bold>bold<cTypeface:>normal<cTypeface:Bold>bold<cTypeface:>normal";
		final String result = translator.renderTextFromNode(mainNode);
		assertEquals(expected, result);
	}

	/**
	 * example written by Andreas
	 */
	@Test
	public void testFromAndreas()
	{
		final String in = "<p class=\"MyTestParaStyle\">Um das iPhone nutzen zu k&ouml;nnen, m&uuml;ssen Sie einen Mobilfunkvertrag &uuml;ber 2 Jahre abschlie&szlig;en."
				+ "Das Einsteigerpaket ist bereits ab 49 Euro pro Monat erh&auml;ltlich. Alle drei 'Complete'-Tarife bieten unbegrenzten Datenverkehr (E-Mail und Web),"
				+ "Visual Voicemail und Zugang zu den &uuml;ber 8.000 HotSpots von T-Mobile in Deutschland."
				+ "<ul>"
				+ "<li>Monatlicher Grundpreis: 49&euro;</li>"
				+ "<li>Datenflatrate W-LAN/EDGE:	Yes</li>"
				+ "<li>Visual Voicemail: Yes</li>"
				+ "<li>Inklusivminuten: 100</li>"
				+ "<li>Folgepreis pro Minute 60/1 - Sek. Takt: 0,39&euro;</li>"
				+ "<li>Inklusiv-SMS: 40</li>"
				+ "<li>Folgepreis pro Standard-Inlands-SMS: 0,19&euro;</li>"
				+ "<li>Bereitstellungspreis: 25&euro;</li>"
				+ "</ul>"
				+ "Das iPhone ist gleichzeitig auch ein perfekter iPod. Genie&szlig;en Sie Musik, Videos und mehr auf dem 3,5\"-Farbdisplay und bl&auml;ttern Sie mit Cover Flow durch Ihre Albumcover."
				+ "</p>";
		final String expected = "<ParaStyle:MyTestParaStyle>Um das iPhone nutzen zu k\u00F6nnen, m\u00FCssen Sie einen Mobilfunkvertrag \u00FCber 2 Jahre abschlie\u00DFen."
				+ "Das Einsteigerpaket ist bereits ab 49 Euro pro Monat erh\u00E4ltlich. Alle drei 'Complete'-Tarife bieten unbegrenzten Datenverkehr (E-Mail und Web),Visual Voicemail und Zugang zu den \u00FCber 8.000 HotSpots von T-Mobile in Deutschland."
				+ PARAGRAPH_BREAK
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>Monatlicher Grundpreis: 49\u20AC<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>Datenflatrate W-LAN/EDGE:\u0009Yes<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>Visual Voicemail: Yes<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>Inklusivminuten: 100<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>Folgepreis pro Minute 60/1 - Sek. Takt: 0,39\u20AC<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>Inklusiv-SMS: 40<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>Folgepreis pro Standard-Inlands-SMS: 0,19\u20AC<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>Bereitstellungspreis: 25\u20AC<bnListType:>"
				+ PARAGRAPH_BREAK
				+ "<ParaStyle:MyTestParaStyle>Das iPhone ist gleichzeitig auch ein perfekter iPod. Genie\u00DFen Sie Musik, Videos und mehr auf dem 3,5\"-Farbdisplay und bl\u00E4ttern Sie mit Cover Flow durch Ihre Albumcover.";
		final String result = translator.renderTextFromNode(translator.createNodesTree(in));
		assertEquals(expected, result);
	}

	@Test
	public void testCorrectLinesInOutputText()
	{
		final String in = "I'm<br/>" + "testing<b> correct lines <b>in</b> output </b> text<br/>" + "I'm<br/>"
				+ "testing<b> correct lines <i>in</i> output </b> text";
		final String expected = "I'm"
				+ FORCED_LINEBREAK
				+ "testing<cTypeface:Bold> correct lines <cTypeface:><cTypeface:Bold>in<cTypeface:><cTypeface:Bold> output <cTypeface:> text"
				+ FORCED_LINEBREAK
				+ "I'm"
				+ FORCED_LINEBREAK
				+ "testing<cTypeface:Bold> correct lines <cTypeface:><cTypeface:Bold Italic>in<cTypeface:><cTypeface:Bold> output <cTypeface:> text";

		final String result = translator.renderTextFromNode(translator.createNodesTree(in));
		assertEquals(expected, result);
	}

	/**
	 * Simple entity test
	 */
	@Test
	public void testEntity()
	{
		final String in = "&nbsp;1&nbsp;2&nbsp;&iexcl;";
		final String expected = "<0x00A0>1<0x00A0>2<0x00A0>\u00a1";

		final String result = translator.renderTextFromNode(translator.createNodesTree(in));
		assertEquals(expected, result);
	}
}
