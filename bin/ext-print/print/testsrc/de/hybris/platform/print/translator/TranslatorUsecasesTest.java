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

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests certain (project related) translation usecases with the HTML-to-InDesign-TaggedText configuration
 */
public class TranslatorUsecasesTest
{

	private static final Logger LOG = Logger.getLogger(TranslatorUsecasesTest.class.getName()); //NOPMD

	// name of configuration file that should be in the same source folder as this class
	private static final String HTML_PARSERS_CONFIGURATION_FILE_NAME = "/print/translator/translator_parsers_html.xml";
	// name of configuration file that should be in the same source folder as this class
	private static final String INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME = "/print/translator/translator_renderers_indesign.xml";
	// name of file with properties
	public final static String INDESIGN_PROPERTIES_FILENAME = "/print/translator/indesign.properties";

	private TranslatorConfiguration config;
	private Translator translator;

	// The "forced line break" character
	private final static String FORCED_LINEBREAK = "<0x000A>";
	// The "paragraph break"
	private final static String PARAGRAPH_BREAK = String.valueOf((char) Integer.parseInt("000D", 16)) //NOPMD
			+ (char) Integer.parseInt("000A", 16);
	// The "Nonbreaking Space" tag
	private final static String NONBREAKING_SPACE = "<0x00A0>";


	@Before
	public void setUp() throws Exception
	{
		InputStream inputStream1 = null;
		InputStream inputStream2 = null;
		InputStream inputStream4 = null;
		try
		{
			//load configuration and initialize translator
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
	 * example written by Andreas
	 */
	@Test
	public void testFromAndreas()
	{
		final String htmlInput = "<p class=\"MyTestParaStyle\">Um das iPhone nutzen zu k&ouml;nnen, m&uuml;ssen Sie einen Mobilfunkvertrag &uuml;ber 2 Jahre abschlie&szlig;en."
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
		final String out = "<ParaStyle:MyTestParaStyle>Um das iPhone nutzen zu k\u00F6nnen, m\u00FCssen Sie einen Mobilfunkvertrag \u00FCber 2 Jahre abschlie\u00DFen."
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
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromRainer()
	{
		final String htmlInput = "<div class=\"Body\">The bearing bracket has two open bearings with "
				+ "lubricating nipples <span class=\"Velocity\">(g&aelig;lder for NK 200-500, 200-400, "
				+ "250-330, 250-400, 250-500, 300-360)</span>.</div>";
		final String out = "<ParaStyle:Body>The bearing bracket has two open bearings with "
				+ "lubricating nipples <CharStyle:Velocity>(g\u00E6lder for NK 200-500, 200-400, "
				+ "250-330, 250-400, 250-500, 300-360)<CharStyle:>.";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * another example written by Rainer
	 */
	@Test
	public void testFromRainer2()
	{
		final String htmlInput = "<div class=\"Body\">Once there was a <u>young</u> <s>little</s> translator that never translated any texts. "
				+ "<p class=\"NextPara\">So he decided to migrate to other countries<br/> to find texts that he could translate.</p>"
				+ "<ul><li>In Germany he found a text like <span class=\"BodyBold\">'Hall&ouml;li h&uuml;bscher transl&auml;ter'</span>.</li>"
				+ "<li>In sweden he found a text like <span class=\"BodyBold\">'en, tv&aring;, tree.... Sm&oslash;rebr&oslash;d'</span>.</li>"
				+ "<li>And in japan he found a text like <span class=\"BodyBold\">'&yen; is the currency symbol for the <span class=\"Emphasis\">japanese &curren; yen</span>.'</span></li></ul></div>"
				+ "<div class=\"AnotherBody\">And so he started to translate texts from all over the world. "
				+ "<span class=\"ContinueOne\">And he translated... <span class=\"ContinueTwo\">and translated.... <span class=\"ContinueThree\">and translated, <b>until</b> <i>he recognized <b>that</b> he <u>had translated</u> texts</i> from</span> <b>completely</b> <s>all countries</s> of the</span> world.</span>"
				+ "And this was the moment when he was <ol><li>really happy</li><li>and satisfied.</li></ol></div>";

		final String out = "<ParaStyle:Body>Once there was a <cUnderline:1>young<cUnderline:> <cStrikethru:1>little<cStrikethru:> translator that never translated any texts. "
				+ "<ParaStyle:NextPara>So he decided to migrate to other countries"
				+ FORCED_LINEBREAK
				+ " to find texts that he could translate."
				+ PARAGRAPH_BREAK
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>In Germany he found a text like <CharStyle:BodyBold>'Hall\u00F6li h\u00FCbscher transl\u00E4ter'<CharStyle:>.<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>In sweden he found a text like <CharStyle:BodyBold>'en, tv\u00E5, tree.... Sm\u00F8rebr\u00F8d'<CharStyle:>.<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>And in japan he found a text like <CharStyle:BodyBold>'\u00A5 is the currency symbol for the <CharStyle:><CharStyle:Emphasis>japanese \u00A4 yen<CharStyle:><CharStyle:BodyBold>.'<CharStyle:><bnListType:>"
				+ PARAGRAPH_BREAK
				+ "<ParaStyle:AnotherBody>And so he started to translate texts from all over the world. "
				+ "<CharStyle:ContinueOne>And he translated... <CharStyle:><CharStyle:ContinueTwo>and translated.... <CharStyle:><CharStyle:ContinueThree>and translated, <cTypeface:Bold>until<cTypeface:> <cTypeface:Italic>he recognized <cTypeface:><cTypeface:Bold Italic>that<cTypeface:><cTypeface:Italic> he <cUnderline:1>had translated<cUnderline:> texts<cTypeface:> from<CharStyle:><CharStyle:ContinueTwo> <cTypeface:Bold>completely<cTypeface:> <cStrikethru:1>all countries<cStrikethru:> of the<CharStyle:><CharStyle:ContinueOne> world.<CharStyle:>"
				+ "And this was the moment when he was "
				+ PARAGRAPH_BREAK
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Numbered>really happy<bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Numbered>and satisfied.<bnListType:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromRainer3()
	{
		final String htmlInput = "<div class=\"Body\">Cast iron pump</div><div class=\"Body\">Pos. Component <span class=\"Velocity\">Material</span><br />6 Pump housing <span class=\"Velocity\">Cast iron</span><br />49 Impeller Cast iron<br /></div>";
		final String out = "<ParaStyle:Body>Cast iron pump" + PARAGRAPH_BREAK
				+ "<ParaStyle:Body>Pos. Component <CharStyle:Velocity>Material<CharStyle:>" + FORCED_LINEBREAK
				+ "6 Pump housing <CharStyle:Velocity>Cast iron<CharStyle:>" + FORCED_LINEBREAK + "49 Impeller Cast iron"
				+ FORCED_LINEBREAK;
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcel_NormalTextFlow()
	{
		// normal text flow
		final String htmlInput = "<p>Fliessmittel, das die Anforderungen an ein <strong>Betonzusatzmittel </strong>der Wirkungsgruppe FM gem&auml;ss SN EN 934-2:2001 erf&uuml;llt. Gute Wasserreduktion, zur Betonherstellung im Winter.</p>";
		final String out = "<ParaStyle:NormalParagraphStyle>Fliessmittel, das die Anforderungen an ein <cTypeface:Bold>Betonzusatzmittel <cTypeface:>der Wirkungsgruppe FM gem\u00e4ss SN EN 934-2:2001 erf\u00fcllt. Gute Wasserreduktion, zur Betonherstellung im Winter.";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcel_Lists()
	{
		// Lists
		final String htmlInput = "<ul><li>0.6-1.7 % des Zementgewichtes </li><li>Dichte 1.21 kg/l</li></ul>";
		final String out = "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>0.6-1.7 % des Zementgewichtes <bnListType:>"
				+ "<ParaStyle:NormalParagraphStyle><bnListType:Bullet>Dichte 1.21 kg/l<bnListType:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelSpecialCharacters()
	{
		// Special characters
		final String htmlInput = "<p>Ein Text mit verwendeten Sonderzeichen:</p>"
				+ "<p>C<sub>2</sub>O<sub>3</sub>H<sup>4</sup></p>" + "<p>&ouml; &auml; &uuml; &Ouml; &Auml; &Uuml; &szlig;</p>"
				+ "<p>&nbsp; &reg; &amp; &copy; &trade; &lt; &gt; &ge; &le; &infin;</p>";
		final String out = "<ParaStyle:NormalParagraphStyle>Ein Text mit verwendeten Sonderzeichen:"
				+ "<ParaStyle:NormalParagraphStyle>C<cPosition:Subscript>2<cPosition:>O<cPosition:Subscript>3<cPosition:>H<cPosition:Superscript>4<cPosition:>"
				+ "<ParaStyle:NormalParagraphStyle>\u00f6 \u00e4 \u00fc \u00d6 \u00c4 \u00dc \u00df"
				+ "<ParaStyle:NormalParagraphStyle>" + NONBREAKING_SPACE + " \u00ae & \u00a9 \u2122 \\< \\> \u2265 \u2264 \u221e";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelTableWithHiddenBorderTag()
	{
		// Table with hidden border tag. The border width matches the default value at which to surpress the border tag
		final int border = 4/* 1 */;
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>&nbsp;</td>" + "<td>bla</td>"
				+ "<td>blub</td>" + "</tr>" + "<tr>" + "<td>A<br /></td>" + "<td>x1&nbsp;</td>" + "<td>23&nbsp;</td>" + "</tr>"
				+ "<tr>" + "<td>&nbsp;B</td>" + "<td>&nbsp;x2</td>" + "<td>4352&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;C</td>"
				+ "<td>&nbsp;x3</td>" + "<td>&nbsp;161561</td>" + "</tr>" + "</tbody>" + "</table>";
		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:><ColStart:><ColStart:>" + // column definition for each column
				"<RowStart:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>bla<CellEnd:>"
				+ "<CellStart:1,1>blub<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>A" + FORCED_LINEBREAK
				+ "<CellEnd:>" + "<CellStart:1,1>x1" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>23" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "B<CellEnd:>"
				+ "<CellStart:1,1>" + NONBREAKING_SPACE + "x2<CellEnd:>" + "<CellStart:1,1>4352" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "C<CellEnd:>" + "<CellStart:1,1>"
				+ NONBREAKING_SPACE + "x3<CellEnd:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "161561<CellEnd:>" + "<RowEnd:>"
				+ "<TableEnd:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelTableWithBorder()
	{
		// Table with border
		final int border = 2;
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>&nbsp;</td>" + "<td>bla</td>"
				+ "<td>blub</td>" + "</tr>" + "<tr>" + "<td>A<br /></td>" + "<td>x1&nbsp;</td>" + "<td>23&nbsp;</td>" + "</tr>"
				+ "<tr>" + "<td>&nbsp;B</td>" + "<td>&nbsp;x2</td>" + "<td>4352&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;C</td>"
				+ "<td>&nbsp;x3</td>" + "<td>&nbsp;161561</td>" + "</tr>" + "</tbody>" + "</table>";
		final float borderF = border * 0.25f;
		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:><ColStart:><ColStart:>" + // column definition for each column
				"<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>bla<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>blub<CellEnd:>"
				+ "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF
				+ "><tCellAttrRightStrokeWeight:" + borderF + "><tCellAttrTopStrokeWeight:" + borderF
				+ "><tCellAttrBottomStrokeWeight:" + borderF + ">>A" + FORCED_LINEBREAK + "<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>x1" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>23" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF
				+ "><tCellAttrRightStrokeWeight:" + borderF + "><tCellAttrTopStrokeWeight:" + borderF
				+ "><tCellAttrBottomStrokeWeight:" + borderF + ">>" + NONBREAKING_SPACE + "B<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>" + NONBREAKING_SPACE
				+ "x2<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>4352" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF
				+ "><tCellAttrRightStrokeWeight:" + borderF + "><tCellAttrTopStrokeWeight:" + borderF
				+ "><tCellAttrBottomStrokeWeight:" + borderF + ">>" + NONBREAKING_SPACE + "C<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>" + NONBREAKING_SPACE
				+ "x3<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>" + NONBREAKING_SPACE
				+ "161561<CellEnd:>" + "<RowEnd:>" + "<TableEnd:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelTableWithConnectedCells1()
	{
		// Table with connected cells 1
		final int border = 4/* 1 */;
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>AAAAAAAAAAAA<br /></td>"
				+ "<td>1&nbsp;</td>" + "<td>2&nbsp;</td>" + "</tr>" + "<tr>" + "<td rowspan=\"3\">&nbsp;BBBBBBBBB</td>"
				+ "<td>3&nbsp;</td>" + "<td>4&nbsp;</td>" + "</tr>" + "<tr>" + "<td>5&nbsp;</td>" + "<td>6&nbsp;</td>" + "</tr>"
				+ "<tr>" + "<td>7&nbsp;</td>" + "<td>8&nbsp;</td>" + "</tr>" + "</tbody>" + "</table>";
		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:><ColStart:><ColStart:>" + // column definition for each column
				"<RowStart:>" + "<CellStart:1,1>AAAAAAAAAAAA" + FORCED_LINEBREAK + "<CellEnd:>" + "<CellStart:1,1>1"
				+ NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>2" + NONBREAKING_SPACE + "<CellEnd:>" + "<RowEnd:>"
				+ "<RowStart:>" + "<CellStart:3,1>" + NONBREAKING_SPACE + "BBBBBBBBB<CellEnd:>" + "<CellStart:1,1>3"
				+ NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>4" + NONBREAKING_SPACE + "<CellEnd:>" + "<RowEnd:>"
				+ "<RowStart:>"
				+ "<CellStart:1,1><CellEnd:>"
				+ // This is an empty cell that is "overspanned" by the rowspan=3 cell
				"<CellStart:1,1>5" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>6" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1><CellEnd:>"
				+ // This is an empty cell that is "overspanned" by the rowspan=3 cell
				"<CellStart:1,1>7" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>8" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<RowEnd:>" + "<TableEnd:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelTableWithConnectedCells2()
	{
		// Table with connected cells 2
		final int border = 2;
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>AAAAAAAAAAAA<br /></td>"
				+ "<td>1&nbsp;</td>" + "<td>2&nbsp;</td>" + "</tr>" + "<tr>" + "<td rowspan=\"3\">&nbsp;BBBBBBBBB</td>"
				+ "<td>3&nbsp;</td>" + "<td>4&nbsp;</td>" + "</tr>" + "<tr>" + "<td>5&nbsp;</td>" + "<td>6&nbsp;</td>" + "</tr>"
				+ "<tr>" + "<td>7&nbsp;</td>" + "<td>8&nbsp;</td>" + "</tr>" + "</tbody>" + "</table>";
		final float borderF = border * 0.25f;
		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:><ColStart:><ColStart:>" + // column definition for each column
				"<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>AAAAAAAAAAAA"
				+ FORCED_LINEBREAK + "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF
				+ "><tCellAttrRightStrokeWeight:" + borderF + "><tCellAttrTopStrokeWeight:" + borderF
				+ "><tCellAttrBottomStrokeWeight:" + borderF + ">>1" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>2" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:3,1<tCellAttrLeftStrokeWeight:" + borderF
				+ "><tCellAttrRightStrokeWeight:" + borderF + "><tCellAttrTopStrokeWeight:" + borderF
				+ "><tCellAttrBottomStrokeWeight:" + borderF + ">>" + NONBREAKING_SPACE + "BBBBBBBBB<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>3" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>4" + NONBREAKING_SPACE
				+ "<CellEnd:>"
				+ "<RowEnd:>"
				+ "<RowStart:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:"
				+ borderF
				+ "><tCellAttrRightStrokeWeight:"
				+ borderF
				+ "><tCellAttrTopStrokeWeight:"
				+ borderF
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderF
				+ ">><CellEnd:>"
				+ // This is an empty cell that is "overspanned" by the rowspan=3 cell
				"<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>5" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>6" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF
				+ "><tCellAttrRightStrokeWeight:" + borderF + "><tCellAttrTopStrokeWeight:"
				+ borderF
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderF
				+ ">><CellEnd:>"
				+ // This is an empty cell that is "overspanned" by the rowspan=3 cell
				"<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>7" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderF + "><tCellAttrRightStrokeWeight:" + borderF
				+ "><tCellAttrTopStrokeWeight:" + borderF + "><tCellAttrBottomStrokeWeight:" + borderF + ">>8" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<RowEnd:>" + "<TableEnd:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelTableWithConnectedCells3()
	{
		// Table with connected cells 3
		final int border = 4/* 1 */;
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>AAAAAAAAAAAA<br /></td>"
				+ "<td colspan=\"2\">1&nbsp;2&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;BBBBBBBBB</td>" + "<td>3&nbsp;</td>"
				+ "<td>4&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;</td>" + "<td colspan=\"2\">5&nbsp;6&nbsp;</td>" + "</tr>"
				+ "<tr>" + "<td>&nbsp;</td>" + "<td>7&nbsp;</td>" + "<td>8&nbsp;</td>" + "</tr>" + "</tbody>" + "</table>";
		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:><ColStart:><ColStart:>" + // column definition for each column
				"<RowStart:>" + "<CellStart:1,1>AAAAAAAAAAAA" + FORCED_LINEBREAK + "<CellEnd:>" + "<CellStart:1,2>1"
				+ NONBREAKING_SPACE
				+ "2"
				+ NONBREAKING_SPACE
				+ "<CellEnd:>"
				+ "<CellStart:1,1><CellEnd:>"
				+ // This is an empty cell that is "overspanned" by the colspan=2 cell
				"<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "BBBBBBBBB<CellEnd:>" + "<CellStart:1,1>3"
				+ NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>4" + NONBREAKING_SPACE + "<CellEnd:>" + "<RowEnd:>"
				+ "<RowStart:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,2>5" + NONBREAKING_SPACE + "6"
				+ NONBREAKING_SPACE + "<CellEnd:>"
				+ "<CellStart:1,1><CellEnd:>"
				+ // This is an empty cell that is "overspanned" by the colspan=2 cell
				"<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>7"
				+ NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>8" + NONBREAKING_SPACE + "<CellEnd:>" + "<RowEnd:>"
				+ "<TableEnd:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelTableWithConnectedCells4()
	{
		// Table with connected cells 4
		final int border = 2;
		final float borderf = border * 0.25f;
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>AAAAAAAAAAAA<br /></td>"
				+ "<td colspan=\"2\">1&nbsp;2&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;BBBBBBBBB</td>" + "<td>3&nbsp;</td>"
				+ "<td>4&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;</td>" + "<td colspan=\"2\">5&nbsp;6&nbsp;</td>" + "</tr>"
				+ "<tr>" + "<td>&nbsp;</td>" + "<td>7&nbsp;</td>" + "<td>8&nbsp;</td>" + "</tr>" + "</tbody>" + "</table>";

		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:><ColStart:><ColStart:>" + // column definition for each column
				"<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:" + borderf
				+ "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf
				+ ">>AAAAAAAAAAAA"
				+ FORCED_LINEBREAK
				+ "<CellEnd:>"
				+ "<CellStart:1,2<tCellAttrLeftStrokeWeight:"
				+ borderf
				+ "><tCellAttrRightStrokeWeight:"
				+ borderf
				+ "><tCellAttrTopStrokeWeight:"
				+ borderf
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderf
				+ ">>1"
				+ NONBREAKING_SPACE
				+ "2"
				+ NONBREAKING_SPACE
				+ "<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:"
				+ borderf
				+ "><tCellAttrRightStrokeWeight:"
				+ borderf
				+ "><tCellAttrTopStrokeWeight:"
				+ borderf
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderf
				+ ">><CellEnd:>"
				+ // This is an empty cell that is "overspanned" by the colspan=2 cell
				"<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:"
				+ borderf + "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>"
				+ NONBREAKING_SPACE + "BBBBBBBBB<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf
				+ "><tCellAttrRightStrokeWeight:" + borderf + "><tCellAttrTopStrokeWeight:" + borderf
				+ "><tCellAttrBottomStrokeWeight:" + borderf + ">>3" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:" + borderf
				+ "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>4" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf
				+ "><tCellAttrRightStrokeWeight:" + borderf + "><tCellAttrTopStrokeWeight:" + borderf
				+ "><tCellAttrBottomStrokeWeight:" + borderf + ">>" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<CellStart:1,2<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:" + borderf
				+ "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>5" + NONBREAKING_SPACE
				+ "6" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:"
				+ borderf
				+ "><tCellAttrRightStrokeWeight:"
				+ borderf
				+ "><tCellAttrTopStrokeWeight:"
				+ borderf
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderf
				+ ">><CellEnd:>"
				+ // This is an empty cell that is "overspanned" by the colspan=2 cell
				"<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:"
				+ borderf + "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>"
				+ NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf
				+ "><tCellAttrRightStrokeWeight:" + borderf + "><tCellAttrTopStrokeWeight:" + borderf
				+ "><tCellAttrBottomStrokeWeight:" + borderf + ">>7" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:" + borderf
				+ "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>8" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<RowEnd:>" + "<TableEnd:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelTableWithConnectedCells5()
	{
		// Table with connected cells 5
		final int border = 4;
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>AAAAAAAAAAAA<br /></td>"
				+ "<td>1&nbsp;2&nbsp;</td>" + "<td>&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;BBBBBBBBB</td>"
				+ "<td colspan=\"2\" rowspan=\"3\">3&nbsp;4&nbsp;5&nbsp;6&nbsp;7&nbsp;8&nbsp;</td>" + "</tr>" + "<tr>"
				+ "<td>&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;</td>" + "</tr>" + "</tbody>" + "</table>";
		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:><ColStart:><ColStart:>" + // column definition for each column
				"<RowStart:>" + "<CellStart:1,1>AAAAAAAAAAAA" + FORCED_LINEBREAK + "<CellEnd:>" + "<CellStart:1,1>1"
				+ NONBREAKING_SPACE + "2" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "BBBBBBBBB<CellEnd:>" + "<CellStart:3,2>3"
				+ NONBREAKING_SPACE + "4" + NONBREAKING_SPACE + "5" + NONBREAKING_SPACE + "6" + NONBREAKING_SPACE + "7"
				+ NONBREAKING_SPACE + "8" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<RowEnd:>" + "<TableEnd:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelTableWithConnectedCells6()
	{
		// Table with connected cells 6
		final int borderi = 2;
		final float borderf = borderi * 0.25f;
		final String htmlInput = "<table border=\"" + borderi + "\">" + "<tbody>" + "<tr>" + "<td>AAAAAAAAAAAA<br /></td>"
				+ "<td>1&nbsp;2&nbsp;</td>" + "<td>&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;BBBBBBBBB</td>"
				+ "<td colspan=\"2\" rowspan=\"3\">3&nbsp;4&nbsp;5&nbsp;6&nbsp;7&nbsp;8&nbsp;</td>" + "</tr>" + "<tr>"
				+ "<td>&nbsp;</td>" + "</tr>" + "<tr>" + "<td>&nbsp;</td>" + "</tr>" + "</tbody>" + "</table>";
		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:><ColStart:><ColStart:>" + // column definition for each column
				"<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:" + borderf
				+ "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>AAAAAAAAAAAA"
				+ FORCED_LINEBREAK + "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf
				+ "><tCellAttrRightStrokeWeight:" + borderf + "><tCellAttrTopStrokeWeight:" + borderf
				+ "><tCellAttrBottomStrokeWeight:" + borderf + ">>1" + NONBREAKING_SPACE + "2" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:" + borderf
				+ "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>" + NONBREAKING_SPACE
				+ "<CellEnd:>" + "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf
				+ "><tCellAttrRightStrokeWeight:" + borderf + "><tCellAttrTopStrokeWeight:" + borderf
				+ "><tCellAttrBottomStrokeWeight:" + borderf + ">>" + NONBREAKING_SPACE + "BBBBBBBBB<CellEnd:>"
				+ "<CellStart:3,2<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:" + borderf
				+ "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>3" + NONBREAKING_SPACE
				+ "4" + NONBREAKING_SPACE + "5" + NONBREAKING_SPACE + "6" + NONBREAKING_SPACE + "7" + NONBREAKING_SPACE
				+ "8"
				+ NONBREAKING_SPACE
				+ "<CellEnd:>"
				+ "<CellStart:1,1<tCellAttrLeftStrokeWeight:"
				+ borderf
				+ "><tCellAttrRightStrokeWeight:"
				+ borderf
				+ "><tCellAttrTopStrokeWeight:"
				+ borderf
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderf
				+ ">><CellEnd:>"
				+ // This is an empty cell that is "overspanned"
				"<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:"
				+ borderf + "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>"
				+ NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf
				+ "><tCellAttrRightStrokeWeight:"
				+ borderf
				+ "><tCellAttrTopStrokeWeight:"
				+ borderf
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderf
				+ ">><CellEnd:>"
				+ // This is an empty cell that is "overspanned"
				"<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:" + borderf
				+ "><tCellAttrTopStrokeWeight:"
				+ borderf
				+ "><tCellAttrBottomStrokeWeight:"
				+ borderf
				+ ">><CellEnd:>"
				+ // This is an empty cell that is "overspanned"
				"<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:"
				+ borderf + "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">>"
				+ NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf
				+ "><tCellAttrRightStrokeWeight:" + borderf + "><tCellAttrTopStrokeWeight:" + borderf
				+ "><tCellAttrBottomStrokeWeight:" + borderf + ">><CellEnd:>"
				+ // This is an empty cell that is "overspanned"
				"<CellStart:1,1<tCellAttrLeftStrokeWeight:" + borderf + "><tCellAttrRightStrokeWeight:" + borderf
				+ "><tCellAttrTopStrokeWeight:" + borderf + "><tCellAttrBottomStrokeWeight:" + borderf + ">><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<RowEnd:>" + "<TableEnd:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}

	/**
	 * example written by Rainer
	 */
	@Test
	public void testFromMarcelTableWithAlignment()
	{
		// Table with alignments
		final int border = 4;
		final String htmlInput = "<table border=\"" + border + "\">" + "<tbody>" + "<tr>" + "<td>AAAAAAAAAAAA<br /></td>"
				+ "<td>1&nbsp;2&nbsp;</td>" + "<td>&nbsp;</td>" + "</tr>" + "<tr>" + "<td align=\"center\">&nbsp;BBBBBBBBB</td>"
				+ "<td colspan=\"2\" rowspan=\"3\">3&nbsp;4&nbsp;5&nbsp;6&nbsp;7&nbsp;8&nbsp;</td>" + "</tr>" + "<tr align=\"left\">"
				+ "<td>&nbsp;ccccccc</td>" + "</tr>" + "<tr align=\"right\">" + "<td>&nbsp;ddddd</td>" + "</tr>" + "</tbody>"
				+ "</table>";
		final String out = "<TableStart:4,3:0:0<tCellDefaultCellType:Text>>" + "<ColStart:><ColStart:><ColStart:>" + // column definition for each column
				"<RowStart:>" + "<CellStart:1,1>AAAAAAAAAAAA" + FORCED_LINEBREAK + "<CellEnd:>" + "<CellStart:1,1>1"
				+ NONBREAKING_SPACE + "2" + NONBREAKING_SPACE + "<CellEnd:>" + "<CellStart:1,1>" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<RowEnd:>" + "<RowStart:>" + "<CellStart:1,1>" /* TODO: {MISSING ALIGNMENT TRANSLATION HERE} */+ NONBREAKING_SPACE
				+ "BBBBBBBBB<CellEnd:>" + "<CellStart:3,2>3" + NONBREAKING_SPACE + "4" + NONBREAKING_SPACE + "5" + NONBREAKING_SPACE
				+ "6" + NONBREAKING_SPACE + "7" + NONBREAKING_SPACE + "8" + NONBREAKING_SPACE + "<CellEnd:>"
				+ "<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<RowEnd:>" + "<RowStart:>" + // TODO: {MISSING ALIGNMENT TRANSLATION HERE}
				"<CellStart:1,1>" + NONBREAKING_SPACE + "ccccccc<CellEnd:>" + "<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<RowEnd:>" + "<RowStart:>" + // TODO: {MISSING ALIGNMENT TRANSLATION HERE}
				"<CellStart:1,1>" + NONBREAKING_SPACE + "ddddd<CellEnd:>" + "<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<CellStart:1,1><CellEnd:>" + // This is an empty cell that is "overspanned"
				"<RowEnd:>" + "<TableEnd:>";
		final String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}


}
