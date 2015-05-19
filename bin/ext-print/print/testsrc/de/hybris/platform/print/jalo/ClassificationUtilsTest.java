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
import static junit.framework.Assert.fail;

import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttribute;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeUnit;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeValue;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystem;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystemVersion;
import de.hybris.platform.catalog.jalo.classification.util.FeatureContainer;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.print.util.ClassificationUtils;
import de.hybris.platform.print.util.ClassificationValue;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for extension Print
 */
public class ClassificationUtilsTest extends HybrisJUnit4TransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ClassificationUtilsTest.class.getName());

	private static String PRODUCT_CODE = "myProduct";
	private static String CLASS_SYSTEM_CODE = "myClassificationSystem";
	private static String CLASS_SYSTEM_VERSION_CODE = "myClassificationSystemVersion";
	private static String CLASS_CLASS_CODE = "myClassificationClass";
	private static String CLASS_ATTRIBUTE_COLOR_CODE = "color";
	private static ClassAttributeAssignment CLASS_ATTRIBUTE_COLOR_ASSIGNMENT;
	private static String CLASS_ATTRIBUTE_WEIGHT_CODE = "weight";
	private static String CLASS_ATTRIBUTE_LOC_CODE = "locTxt";
	private static String CLASS_ATTRIBUTE_VALUE_RED_CODE = "red";
	private static String CLASS_ATTRIBUTE_VALUE_RED_NAME_DE = "Rot";
	private static String CLASS_ATTRIBUTE_VALUE_RED_NAME_EN = "Red";
	private static String CLASS_ATTRIBUTE_VALUE_BLUE_CODE = "blue";
	private static String CLASS_ATTRIBUTE_VALUE_BLUE_NAME_DE = "Blau";
	private static String CLASS_ATTRIBUTE_VALUE_BLUE_NAME_EN = "Blue";
	private static String CLASS_ATTRIBUTE_VALUE_LOC_DE = "Ein Text in DE";
	private static String CLASS_ATTRIBUTE_VALUE_LOC_EN = "A Text in EN";
	private static String CLASS_ATTRIBUTE_UNIT_CODE = "kg";
	private static String CLASS_ATTRIBUTE_UNIT_SYMBOL = "kg";
	private static BigDecimal CLASS_ATTRIBUTE_UNIT_VALUE = new BigDecimal("12.345");


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
		//first create a product
		final ComposedType productType = typeMan.getComposedType(Product.class);
		final HashMap parameters = new HashMap();
		parameters.put(Product.CODE, PRODUCT_CODE);
		final Item product = productType.newInstance(parameters);
		assertNotNull(product);

		// creating ClassificationSystem and ClassificationSystemVersion
		final ClassificationSystem classSystem = CatalogManager.getInstance().createClassificationSystem(CLASS_SYSTEM_CODE);
		assertNotNull(classSystem);
		final ClassificationSystemVersion classVersion = classSystem.createSystemVersion(CLASS_SYSTEM_VERSION_CODE, langDE);
		assertNotNull(classVersion);

		// creating ClassificationClass
		final ClassificationClass classificationClass = classVersion.createClass(CLASS_CLASS_CODE);
		assertNotNull(classificationClass);

		// creating ClassificationAttribute
		final ClassificationAttribute attrColor = classVersion.createClassificationAttribute(CLASS_ATTRIBUTE_COLOR_CODE);
		assertNotNull(attrColor);
		final ClassificationAttribute attrWeight = classVersion.createClassificationAttribute(CLASS_ATTRIBUTE_WEIGHT_CODE);
		assertNotNull(attrWeight);
		final ClassificationAttribute attrLocTxt = classVersion.createClassificationAttribute(CLASS_ATTRIBUTE_LOC_CODE);
		assertNotNull(attrLocTxt);

		// creating ClassificationAttributeValues
		final ClassificationAttributeValue valueRed = classVersion
				.createClassificationAttributeValue(CLASS_ATTRIBUTE_VALUE_RED_CODE);
		valueRed.setName(ctxDE, CLASS_ATTRIBUTE_VALUE_RED_NAME_DE);
		valueRed.setName(ctxEN, CLASS_ATTRIBUTE_VALUE_RED_NAME_EN);
		assertNotNull(valueRed);
		final ClassificationAttributeValue valueBlue = classVersion
				.createClassificationAttributeValue(CLASS_ATTRIBUTE_VALUE_BLUE_CODE);
		valueBlue.setName(ctxDE, CLASS_ATTRIBUTE_VALUE_BLUE_NAME_DE);
		valueBlue.setName(ctxEN, CLASS_ATTRIBUTE_VALUE_BLUE_NAME_EN);
		assertNotNull(valueBlue);

		// creating ClassificationAttributeUnit
		final ClassificationAttributeUnit uKg = classVersion.createAttributeUnit(CLASS_ATTRIBUTE_UNIT_CODE,
				CLASS_ATTRIBUTE_UNIT_SYMBOL);
		assertNotNull(uKg);

		// getting ClassificationAttributeTypes
		final EnumerationValue T_NUMBER = EnumerationManager.getInstance().getEnumerationValue(
				CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.NUMBER);

		final EnumerationValue T_STRING = EnumerationManager.getInstance().getEnumerationValue(
				CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.STRING);

		final EnumerationValue T_ENUM = EnumerationManager.getInstance().getEnumerationValue(
				CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.ENUM);

		// assigning ClassificationAttributes to ClassificationClasses
		CLASS_ATTRIBUTE_COLOR_ASSIGNMENT = classificationClass.assignAttribute(attrColor, T_ENUM, null, // no unit
				Arrays.asList(new Object[]
				{ valueRed, valueBlue }), // values
				0 // position
				);

		classificationClass.assignAttribute(attrWeight, T_NUMBER, uKg, // unit
				null, // no values
				1 // position
				);

		classificationClass.assignAttribute(attrLocTxt, T_STRING, null, // no unit
				null, // no values
				2 // position
				);
		classificationClass.setLocalized(attrLocTxt, true);

		// assigning a product to a ClassificationClass
		classificationClass.addProduct((Product) product);

		// assigning values to a product's features
		final FeatureContainer cont = FeatureContainer.load((Product) product);
		cont.getFeature(attrColor).createValue(valueRed);
		cont.getFeature(attrColor).createValue(valueBlue);
		cont.getFeature(attrWeight).createValue(CLASS_ATTRIBUTE_UNIT_VALUE);
		cont.getFeature(attrLocTxt).createValue(ctxDE, CLASS_ATTRIBUTE_VALUE_LOC_DE);
		cont.getFeature(attrLocTxt).createValue(ctxEN, CLASS_ATTRIBUTE_VALUE_LOC_EN);
		cont.store();
	}

	@Test
	public void testClassificationUtils()
	{
		final ProductManager prodMan = ProductManager.getInstance();
		final ClassificationUtils cUtils = PrintManager.getInstance().getClassificationUtils();

		final SessionContext ctx = jaloSession.getSessionContext();
		final Language backupLang = ctx.getLanguage();
		ctx.setLanguage(C2LManager.getInstance().getLanguageByIsoCode("en"));

		final Collection<Product> products = prodMan.getProductsByCode(PRODUCT_CODE);
		if (products == null || products.isEmpty())
		{
			log.info("Could not test VarianTools, did not find variant products");
			return;
		}

		final Product product = products.iterator().next();
		final Collection<ClassificationValue> classificationValues = cUtils.getAllClassificationValues(product);
		assertNotNull(classificationValues);
		assertEquals(3, classificationValues.size());

		for (final ClassificationValue classificationValue : classificationValues)
		{
			final String classificationClassName = classificationValue.getClassificationClassName();
			// List<String> classificationClasses = classificationValue.getClassificationClasses();
			final String name = classificationValue.getName();
			final String value = classificationValue.getValue();
			final List<String> values = classificationValue.getValues();
			final String unitCode = classificationValue.getUnitCode();
			// String unitName = classificationValue.getUnitName();
			final String unitSymbol = classificationValue.getUnitSymbol();
			// String unitType = classificationValue.getUnitType();

			assertEquals(CLASS_CLASS_CODE, classificationClassName);
			// TODO: test classificationClasses list

			if (name.equals(CLASS_ATTRIBUTE_COLOR_CODE))
			{
				// test color attribute values
				assertEquals(CLASS_ATTRIBUTE_VALUE_RED_NAME_EN + ", " + CLASS_ATTRIBUTE_VALUE_BLUE_NAME_EN, value);
				assertEquals(CLASS_ATTRIBUTE_VALUE_RED_NAME_EN, values.get(0));
				assertEquals(CLASS_ATTRIBUTE_VALUE_BLUE_NAME_EN, values.get(1));
			}
			else if (name.equals(CLASS_ATTRIBUTE_WEIGHT_CODE))
			{
				// test weight attribute values
				assertEquals(String.valueOf(CLASS_ATTRIBUTE_UNIT_VALUE), value);
				assertEquals(String.valueOf(CLASS_ATTRIBUTE_UNIT_VALUE), values.get(0));
				assertEquals(CLASS_ATTRIBUTE_UNIT_CODE, unitCode);
				assertEquals(CLASS_ATTRIBUTE_UNIT_SYMBOL, unitSymbol);
			}
			else if (name.equals(CLASS_ATTRIBUTE_LOC_CODE))
			{
				// test loctext attribute values
				assertEquals(CLASS_ATTRIBUTE_VALUE_LOC_EN, value);
				assertEquals(CLASS_ATTRIBUTE_VALUE_LOC_EN, values.get(0));
			}
			else
			{
				fail("Found unknown classification attribute name: " + name);
			}
		}

		final String value = cUtils.getClassificationValue(product, CLASS_ATTRIBUTE_COLOR_ASSIGNMENT, ", ");
		assertEquals(CLASS_ATTRIBUTE_VALUE_RED_NAME_EN + ", " + CLASS_ATTRIBUTE_VALUE_BLUE_NAME_EN, value);

		ctx.setLanguage(backupLang);
	}
}
