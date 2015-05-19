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

import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.commons.translator.RenderersFactory;
import de.hybris.platform.commons.translator.RenderersFactoryFromFile;
import de.hybris.platform.commons.translator.Translator;
import de.hybris.platform.commons.translator.TranslatorConfiguration;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.print.comet.constants.CometConstants;
import de.hybris.platform.print.comet.utils.CometPrintTools;
import de.hybris.platform.print.jalo.TextBlock;
import de.hybris.platform.print.jalo.link.Crossreference;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.util.Utilities;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the itemlink translator functionality
 */
public class TranslatorItemlinkTest extends HybrisJUnit4TransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TranslatorTest.class.getName());

	// name of configuration file that should be in the same source folder as this class
	private static final String HTML_PARSERS_CONFIGURATION_FILE_NAME = "/print/translator/translator_parsers_html.xml";
	// name of configuration file that should be in the same source folder as this class
	private static final String INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME = "/print/translator/translator_renderers_indesign.xml";
	// name of file with properties
	public final static String INDESIGN_PROPERTIES_FILENAME = "/print/translator/indesign.properties";
	// name of file with rules of replacing html entities to indesign language
	public final static String INDESIGN_ENTITIES_REPLACE_FILENAME = "/print/translator/indesign_replace.properties";

	@SuppressWarnings("unused")
	private TranslatorConfiguration config;
	private Translator translator;

	private static Item product;
	private static final String PRODUCT_CODE = "myProductCode";
	private static final String PRODUCT_NAME_EN = "myProductName";
	private static final String PRODUCT_NAME_DE = "meinProduktName";
	private static final String CATALOG_ID = "LinkTest-TestCatalog-ID";
	private static final String CATALOGVERSION_ID = "LinkTest-TestCatalogVersion-ID";
	private static final String LINK_ID = "LinkTest-LinkID";
	private static final String REPLACEMENT_TYPE_ID = "LinkTest-ReplacementType";

	private static Item textBlock;
	private static Item crossreference;
	private static String TEXTBLOCK_CODE = "myTextBlock";
	private static String ITEMLINK_TEXT_EN = "See Page <a href='" + Utilities.ITEMLINK_PROTOCOLL_SPECIFIER
			+ Utilities.ITEMLINK_TYPE_PREFIX + "Product" + Utilities.ITEMLINK_ELEMENT_SEPARATOR + "code"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + PRODUCT_CODE + Utilities.ITEMLINK_ELEMENT_SEPARATOR + "name"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + PRODUCT_NAME_EN + Utilities.ITEMLINK_ELEMENT_SEPARATOR + "catalogVersion"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + Utilities.ITEMLINK_ITEMATTRIBUTE_START + "version"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + CATALOGVERSION_ID + Utilities.ITEMLINK_ELEMENT_SEPARATOR + "catalog"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + Utilities.ITEMLINK_ITEMATTRIBUTE_START + "id"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + CATALOG_ID + Utilities.ITEMLINK_ITEMATTRIBUTE_END
			+ Utilities.ITEMLINK_ITEMATTRIBUTE_END + Utilities.ITEMLINK_ELEMENT_SEPARATOR + Utilities.ITEMLINK_LINK_ID
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + LINK_ID + Utilities.ITEMLINK_ELEMENT_SEPARATOR
			+ Utilities.ITEMLINK_REPLACEMENT_TYPE + Utilities.ITEMLINK_VALUE_SEPARATOR + REPLACEMENT_TYPE_ID
			+ "'>XXX</a> for details";
	private static String ITEMLINK_TEXT_DE = "Siehe Seite <a href='" + Utilities.ITEMLINK_PROTOCOLL_SPECIFIER
			+ Utilities.ITEMLINK_TYPE_PREFIX + "Product" + Utilities.ITEMLINK_ELEMENT_SEPARATOR + "code"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + PRODUCT_CODE + Utilities.ITEMLINK_ELEMENT_SEPARATOR + "name"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + PRODUCT_NAME_DE + Utilities.ITEMLINK_ELEMENT_SEPARATOR + "catalogVersion"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + Utilities.ITEMLINK_ITEMATTRIBUTE_START + "version"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + CATALOGVERSION_ID + Utilities.ITEMLINK_ELEMENT_SEPARATOR + "catalog"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + Utilities.ITEMLINK_ITEMATTRIBUTE_START + "id"
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + CATALOG_ID + Utilities.ITEMLINK_ITEMATTRIBUTE_END
			+ Utilities.ITEMLINK_ITEMATTRIBUTE_END + Utilities.ITEMLINK_ELEMENT_SEPARATOR + Utilities.ITEMLINK_LINK_ID
			+ Utilities.ITEMLINK_VALUE_SEPARATOR + LINK_ID + Utilities.ITEMLINK_ELEMENT_SEPARATOR
			+ Utilities.ITEMLINK_REPLACEMENT_TYPE + Utilities.ITEMLINK_VALUE_SEPARATOR + REPLACEMENT_TYPE_ID
			+ "'>XXX</a> f�r details";
	private static String XREF_QUALIFIER = "";
	private static String XREF_DISPLAYDETAILS = "";
	private static String XREF_ATTRIBUTE = TextBlock.TEXT;


	@Before
	public void setUp() throws Exception
	{
		final CatalogManager catalogMan = CatalogManager.getInstance();
		final C2LManager c2lMan = C2LManager.getInstance();
		final TypeManager typeMan = TypeManager.getInstance();
		final JaloSession jaloSession = JaloSession.getCurrentSession();

		// Creating Languages for test
		final Language langEN = c2lMan.getLanguageByIsoCode("en");
		assertNotNull(langEN);
		Language langDE = null;
		try
		{
			langDE = c2lMan.getLanguageByIsoCode("de");
		}
		catch (final JaloItemNotFoundException ex)
		{
			langDE = c2lMan.createLanguage("de");
		}
		assertNotNull(langDE);

		// Creating SessionContexts for test
		final SessionContext ctxEN = jaloSession.createSessionContext();
		ctxEN.setLanguage(langEN);
		final SessionContext ctxDE = jaloSession.createSessionContext();
		ctxDE.setLanguage(langDE);

		// Create Catalog + CatalogVersion
		final Catalog catalog = catalogMan.createCatalog(CATALOG_ID);
		assertNotNull(catalog);
		final CatalogVersion catalogVersion = catalogMan.createCatalogVersion(catalog, CATALOGVERSION_ID, langDE);
		assertNotNull(catalogVersion);

		// Create Product
		final ComposedType productType = typeMan.getComposedType(Product.class);
		final HashMap parameters = new HashMap();
		parameters.put(Product.CODE, PRODUCT_CODE);
		product = productType.newInstance(parameters);
		((Product) product).setName(ctxEN, PRODUCT_NAME_EN);
		((Product) product).setName(ctxDE, PRODUCT_NAME_DE);
		catalogMan.setCatalogVersion((Product) product, catalogVersion);
		assertNotNull(product);

		// Create TestBlock that references to the product
		final ComposedType textBlockType = typeMan.getComposedType(TextBlock.class);
		parameters.clear();
		parameters.put(TextBlock.CODE, TEXTBLOCK_CODE);
		textBlock = textBlockType.newInstance(parameters);
		((TextBlock) textBlock).setText(ctxEN, ITEMLINK_TEXT_EN);
		((TextBlock) textBlock).setText(ctxDE, ITEMLINK_TEXT_DE);
		assertNotNull(textBlock);

		// Create Crossreference object
		final ComposedType crossreferenceType = typeMan.getComposedType(Crossreference.class);
		parameters.clear();
		parameters.put(Crossreference.QUALIFIER, XREF_QUALIFIER);
		parameters.put(Crossreference.DISPLAYDETAILS, XREF_DISPLAYDETAILS);
		parameters.put(Crossreference.ATTRIBUTE, XREF_ATTRIBUTE);
		parameters.put(Crossreference.SOURCE, textBlock);
		parameters.put(Crossreference.TARGET, product);
		parameters.put(Crossreference.ID, LINK_ID);
		crossreference = crossreferenceType.newInstance(parameters);
		assertNotNull(crossreference);

		// Create translator configuration
		TranslatorConfiguration config;
		InputStream translatorRenderersIndesign_IS = null;
		InputStream indesignProperties_IS = null;
		InputStream translatorParsersHtml_IS = null;
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

			final HashMap<String, Object> additionalContextProperties = new HashMap<String, Object>(2);
			additionalContextProperties.put("sourceItemPK", textBlock.getPK().getLongValueAsString());
			additionalContextProperties.put("cometPrintTools", new CometPrintTools());
			translator.addAllContextProperties(additionalContextProperties);
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
	 * Test the itemlink translation
	 */
	@Test
	public void testItemlinkTranslator()
	{
		final C2LManager c2lMan = C2LManager.getInstance();
		final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		ctx.setLanguage(c2lMan.getLanguageByIsoCode("en"));

		String htmlInput = ITEMLINK_TEXT_EN;
		String out = "See Page <w2: " + CometConstants.CROSSREFERENCE_SOURCE_PLACEHOLDER_ID + ", "
				+ CometConstants.ProductListNodeType.Product.id() + ", " + "0, "
				+ // ID2 
				"0, "
				+ // ID3
				"'" + textBlock.getPK().getLongValueAsString() + "', " + "infos1 '" + product.getPK().getLongValueAsString() + ":"
				+ LINK_ID + ":" + REPLACEMENT_TYPE_ID + "', " + "infos2 ''" + "> </w2> for details";
		String result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);

		ctx.setLanguage(c2lMan.getLanguageByIsoCode("de"));
		htmlInput = ITEMLINK_TEXT_DE;
		out = "Siehe Seite <w2: " + CometConstants.CROSSREFERENCE_SOURCE_PLACEHOLDER_ID + ", "
				+ CometConstants.ProductListNodeType.Product.id() + ", " + "0, "
				+ // ID2 
				"0, "
				+ // ID3
				"'" + textBlock.getPK().getLongValueAsString() + "', " + "infos1 '" + product.getPK().getLongValueAsString() + ":"
				+ LINK_ID + ":" + REPLACEMENT_TYPE_ID + "', " + "infos2 ''" + "> </w2> f�r details";
		result = translator.renderTextFromNode(translator.createNodesTree(htmlInput));
		assertEquals(out, result);
	}
}
