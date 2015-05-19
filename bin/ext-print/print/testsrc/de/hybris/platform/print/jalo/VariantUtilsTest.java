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
package de.hybris.platform.print.jalo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloDuplicateQualifierException;
import de.hybris.platform.jalo.type.Type;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.print.util.VariantUtils;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.variants.constants.VariantsConstants;
import de.hybris.platform.variants.jalo.VariantAttributeDescriptor;
import de.hybris.platform.variants.jalo.VariantProduct;
import de.hybris.platform.variants.jalo.VariantType;
import de.hybris.platform.variants.jalo.VariantsManager;

import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests for VariantUtils
 */
public class VariantUtilsTest extends HybrisJUnit4TransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(VariantUtilsTest.class.getName());

	private final static String VARIANT_BASE_CODE = "myVariantBase";
	private final static String VARIANT_ATTRIBUTE_COLOR_CODE = "color";
	private final static String VARIANT_ATTRIBUTE_COLOR_NAME_DE = "Farbe";
	private final static String VARIANT_ATTRIBUTE_COLOR_NAME_EN = "Color";
	private final static String VARIANT_ATTRIBUTE_SIZE_CODE = "size";
	private final static String VARIANT_ATTRIBUTE_SIZE_NAME_DE = "Größe";
	private final static String VARIANT_ATTRIBUTE_SIZE_NAME_EN = "Size";


	@SuppressWarnings("boxing")
	@Before
	public void setUp() throws Exception
	{
		final C2LManager c2lMan = C2LManager.getInstance();
		final TypeManager typeMan = TypeManager.getInstance();

		// Creating Languages for test
		final Language langEN = c2lMan.getLanguageByIsoCode("en");
		assertNotNull(langEN);
		final Language langDE = c2lMan.createLanguage("de");
		assertNotNull(langDE);

		// Creating SessionContexts for test
		final SessionContext ctxEN = jaloSession.createSessionContext();
		ctxEN.setLanguage(langEN);
		final SessionContext ctxDE = jaloSession.createSessionContext();
		ctxDE.setLanguage(langDE);

		// creating test data for test
		final Type localizedStringType = typeMan.getType("localized:java.lang.String");
		final Type integerType = typeMan.getType("java.lang.Integer");

		final VariantType vt = VariantsManager.getInstance().createVariantType("SweatShirt");
		vt.getAttributeDescriptor(VariantType.CODE).setUnique(true);
		vt.setName(ctxEN, "SweatShirt");
		vt.setName(ctxDE, "SweatShirt");
		assertNotNull(vt);

		// creating variant attributes
		try
		{
			final VariantAttributeDescriptor vadCOLOR = vt.createVariantAttributeDescriptor(VARIANT_ATTRIBUTE_COLOR_CODE,
					localizedStringType, AttributeDescriptor.READ_FLAG + AttributeDescriptor.WRITE_FLAG
							+ AttributeDescriptor.SEARCH_FLAG);
			vadCOLOR.setName(ctxEN, VARIANT_ATTRIBUTE_COLOR_NAME_EN);
			vadCOLOR.setName(ctxDE, VARIANT_ATTRIBUTE_COLOR_NAME_DE);
			vadCOLOR.setPosition(0);
			assertNotNull(vadCOLOR);

			final VariantAttributeDescriptor vadSIZE = vt.createVariantAttributeDescriptor(VARIANT_ATTRIBUTE_SIZE_CODE, integerType,
					AttributeDescriptor.READ_FLAG + AttributeDescriptor.WRITE_FLAG + AttributeDescriptor.SEARCH_FLAG);
			vadSIZE.setName(ctxEN, VARIANT_ATTRIBUTE_SIZE_NAME_EN);
			vadSIZE.setName(ctxDE, VARIANT_ATTRIBUTE_SIZE_NAME_DE);
			vadSIZE.setPosition(1);
			assertNotNull(vadSIZE);
		}
		catch (final JaloDuplicateQualifierException e)
		{
			log.error("Error while creating variant test data for VariantUtils: " + e);
		}

		//first create the base
		final ComposedType productType = typeMan.getComposedType(Product.class);
		final HashMap parameters = new HashMap();
		parameters.put(Product.CODE, VARIANT_BASE_CODE);
		parameters.put(VariantsConstants.Attributes.Product.VARIANTTYPE, vt);
		final Item baseProduct = productType.newInstance(parameters);
		assertNotNull(baseProduct);

		//second create the variants
		parameters.clear();
		parameters.put(VariantProduct.BASEPRODUCT, baseProduct);
		parameters.put(Product.CODE, "myVariantProduct1");
		final Item variantProduct1 = vt.newInstance(parameters);
		variantProduct1.setAttribute(ctxEN, VARIANT_ATTRIBUTE_COLOR_CODE, "Red");
		variantProduct1.setAttribute(ctxDE, VARIANT_ATTRIBUTE_COLOR_CODE, "Rot");
		variantProduct1.setAttribute(VARIANT_ATTRIBUTE_SIZE_CODE, Integer.valueOf(11));
		assertNotNull(variantProduct1);

		parameters.clear();
		parameters.put(VariantProduct.BASEPRODUCT, baseProduct);
		parameters.put(Product.CODE, "myVariantProduct2");
		final Item variantProduct2 = vt.newInstance(parameters);
		variantProduct2.setAttribute(ctxEN, VARIANT_ATTRIBUTE_COLOR_CODE, "Green");
		variantProduct2.setAttribute(ctxDE, VARIANT_ATTRIBUTE_COLOR_CODE, "Grün");
		variantProduct2.setAttribute(VARIANT_ATTRIBUTE_SIZE_CODE, Integer.valueOf(12));
		assertNotNull(variantProduct2);

		parameters.clear();
		parameters.put(VariantProduct.BASEPRODUCT, baseProduct);
		parameters.put(Product.CODE, "myVariantProduct3");
		final Item variantProduct3 = vt.newInstance(parameters);
		variantProduct3.setAttribute(ctxEN, VARIANT_ATTRIBUTE_COLOR_CODE, "Blue");
		variantProduct3.setAttribute(ctxDE, VARIANT_ATTRIBUTE_COLOR_CODE, "Blau");
		variantProduct3.setAttribute(VARIANT_ATTRIBUTE_SIZE_CODE, Integer.valueOf(13));
		assertNotNull(variantProduct3);
	}

	@Test
	public void testVariantUtils()
	{
		final ProductManager prodMan = ProductManager.getInstance();
		final VariantUtils vUtils = PrintManager.getInstance().getVariantUtils();

		final SessionContext ctx = jaloSession.getSessionContext();
		final Language backupLang = ctx.getLanguage();
		ctx.setLanguage(C2LManager.getInstance().getLanguageByIsoCode("en"));

		final Collection<Product> baseProducts = prodMan.getProductsByCode(VARIANT_BASE_CODE);
		if (baseProducts == null || baseProducts.isEmpty())
		{
			log.info("Could not test VarianTools, did not find variant products");
			return;
		}

		final Product baseProduct = baseProducts.iterator().next();

		String out = VARIANT_ATTRIBUTE_COLOR_NAME_EN + ": Blue, Green, Red" + "; " + VARIANT_ATTRIBUTE_SIZE_NAME_EN
				+ ": 11, 12, 13";
		String result = vUtils.getAllVariantAttributeValues(baseProduct, "; ", ": ", ", ");
		assertEquals(out, result);

		out = VARIANT_ATTRIBUTE_COLOR_NAME_EN + ": Blue, Green, Red";
		result = vUtils.getVariantAttributeValue(baseProduct, VARIANT_ATTRIBUTE_COLOR_CODE, ": ", ", ");
		assertEquals(out, result);

		ctx.setLanguage(backupLang);
	}
}
