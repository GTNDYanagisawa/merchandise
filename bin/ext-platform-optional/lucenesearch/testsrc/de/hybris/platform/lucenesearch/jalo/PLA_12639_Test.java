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
package de.hybris.platform.lucenesearch.jalo;

import static de.hybris.platform.testframework.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.jalo.SearchResult;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests PLA-12639. The problem was lucene search indexing fetching attributes directly from database using
 * FlexibleSearch. While this is a good thing for all attributes mapped to item or lp tables we run into data truncation
 * for props table attributes. These value were taken from VALUESTRING1 columns which do only store a maximum of 1000
 * characters.
 * 
 * The fix was done inside lucene indexing logic to exclude 'props' attributes from the onese to be fetched directly.
 */
public class PLA_12639_Test extends HybrisJUnit4Test
{
	private Product product;
	private SessionContext ctxDe, ctxEn;
	private Language langEn, langDe;
	private LuceneIndex index;
	private IndexConfiguration indexConfig; //NOPMD

	private static String TOKEN_DE = "DErDE";
	private static String TOKEN_EN = "ENrEN";
	private static final String FILLWORD = "fillword";


	private static final String REMARKS = "remarks";

	@Before
	public void setUp() throws Exception
	{
		final TypeManager typeManager = jaloSession.getTypeManager();
		final ComposedType productType = typeManager.getComposedType(Product.class);

		final LucenesearchManager manager = LucenesearchManager.getInstance();
		final ProductManager productManager = ProductManager.getInstance();
		final C2LManager c2lm = C2LManager.getInstance();
		assertNotNull(langDe = c2lm.createLanguage("test-de"));
		assertNotNull(langEn = c2lm.createLanguage("test-en"));
		ctxDe = jaloSession.createSessionContext();
		ctxDe.setLanguage(langDe);
		ctxEn = jaloSession.createSessionContext();
		ctxEn.setLanguage(langEn);

		assertNotNull(product = productManager.createProduct("ppp"));
		setRemarks(ctxDe, product, generateRemarksExceedingMaxLength(TOKEN_DE));
		setRemarks(ctxEn, product, generateRemarksExceedingMaxLength(TOKEN_EN));

		assertNotNull(index = manager.createLuceneIndex("PLA-12639-Test"));
		index.createLanguageConfiguration(langDe);
		index.createLanguageConfiguration(langEn);
		index.createIndexConfiguration(productType,
				Arrays.asList(productType.getAttributeDescriptor(Product.CODE), productType.getAttributeDescriptor(REMARKS)));
	}

	// actually I wanted to use the 'remarks' attribute from catalog but didn't like to
	// introduce a hard dependency to catalog extension. So we're directly setting the
	// localized property 'remarks' now.
	void setRemarks(final SessionContext ctx, final Product product, final String value)
	{
		product.setLocalizedProperty(ctx, REMARKS, value);
	}

	@Test
	public void testSearch() throws ParseException
	{
		index.rebuildIndex();

		assertSearchResult(list(product), index.searchItems(ctxDe, "ppp", 0, -1));
		assertSearchResult(list(product), index.searchItems(ctxEn, "ppp", 0, -1));

		assertSearchResult(list(product), index.searchItems(ctxDe, FILLWORD, 0, -1));
		assertSearchResult(list(product), index.searchItems(ctxEn, FILLWORD, 0, -1));

		// test dump property exceeding max property length - PLA-12639
		assertSearchResult(list(product), index.searchItems(ctxDe, TOKEN_DE, 0, -1));
		assertSearchResult(list(product), index.searchItems(ctxEn, TOKEN_EN, 0, -1));
	}

	private String generateRemarksExceedingMaxLength(final String postFix)
	{
		final int propMaxLength = Config.getInt("property.maxlength", 3999);
		final StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i <= propMaxLength; i += (FILLWORD.length() + 1))
		{
			stringBuilder.append(FILLWORD).append(' ');
		}
		stringBuilder.append(postFix);
		return stringBuilder.toString();
	}

	private static void assertSearchResult(final List items, final SearchResult actual)
	{
		final String msg = "expected " + items + " but got " + actual;
		assertEquals(msg, 0, actual.getRequestedStart());
		assertEquals(msg, items.size(), actual.getTotalCount());
		assertEquals(msg, items.size(), actual.getCount());
		assertEquals(msg, items, actual.getResult());
	}
}
