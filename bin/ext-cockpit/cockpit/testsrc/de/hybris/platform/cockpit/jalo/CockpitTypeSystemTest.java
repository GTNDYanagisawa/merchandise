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
package de.hybris.platform.cockpit.jalo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttribute;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeUnit;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeValue;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystem;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystemVersion;
import de.hybris.platform.cockpit.jalo.template.CockpitItemTemplate;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.TypeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * JUnit Tests for the Cockpit extension
 */
@IntegrationTest
public class CockpitTypeSystemTest extends CockpitTest
{
	private Language english;
	private SessionContext enCtx;

	private ClassificationSystem system1, system2;
	private ClassificationSystemVersion classificationSystemVersion1, classificationSystemVersion2;
	private ClassificationClass clHifi, clLoudspeaker, clAmplifier, clCdplayer;
	private ClassificationAttribute attrColor, attrWeight, attrSupplier, attrFormats, attrBookshelf, attrPower;
	private ClassificationAttributeValue vRed, vGreen, vYellow, vFormatCd, vFormatMp3, vFormatWma;
	private ClassificationAttributeUnit uWatt, uKg;
	private Product product1;
	private CockpitItemTemplate tplHifi, tplAmplifier;
	private TypeService typeService;

	private EnumerationValue T_STRING, T_BOOLEAN, T_NUMBER, T_ENUM;

	@Before
	public void setUp()
	{

		english = getOrCreateLanguage("en");
		enCtx = jaloSession.createSessionContext();
		enCtx.setLanguage(english);
		T_STRING = EnumerationManager.getInstance().getEnumerationValue(CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.STRING);
		T_BOOLEAN = EnumerationManager.getInstance().getEnumerationValue(CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.BOOLEAN);
		T_NUMBER = EnumerationManager.getInstance().getEnumerationValue(CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.NUMBER);
		T_ENUM = EnumerationManager.getInstance().getEnumerationValue(CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.ENUM);
	}

	@After
	public void tearDown()
	{
		typeService = null;
	}

	protected void createSystems() throws ConsistencyCheckException
	{
		system1 = CatalogManager.getInstance().createClassificationSystem("System1");
		classificationSystemVersion1 = system1.createSystemVersion("v1", english);
		system2 = CatalogManager.getInstance().createClassificationSystem("System2");
		classificationSystemVersion2 = system2.createSystemVersion("v2", english);
	}

	protected void createClasses() throws ConsistencyCheckException
	{
		assertNotNull(system1);
		assertNotNull(classificationSystemVersion1);
		assertNotNull(system2);
		assertNotNull(classificationSystemVersion2);

		clHifi = classificationSystemVersion1.createClass("Hifi");
		clLoudspeaker = classificationSystemVersion1.createClass("Loudspeakers");
		clLoudspeaker.setSuperClass(clHifi);
		clAmplifier = classificationSystemVersion1.createClass("Amplifiers");
		clAmplifier.setSuperClass(clHifi);
		clCdplayer = classificationSystemVersion1.createClass("CDPlayers");
		clCdplayer.setSuperClass(clHifi);
	}

	protected void createAttributes() throws ConsistencyCheckException
	{
		attrColor = classificationSystemVersion1.createClassificationAttribute("color");
		vRed = classificationSystemVersion1.createClassificationAttributeValue("red");
		vRed.setName(enCtx, "Red");
		vGreen = classificationSystemVersion1.createClassificationAttributeValue("green");
		vGreen.setName(enCtx, "Green");
		vYellow = classificationSystemVersion1.createClassificationAttributeValue("yellow");
		vYellow.setName(enCtx, "Yellow");

		attrWeight = classificationSystemVersion1.createClassificationAttribute("weight");

		attrSupplier = classificationSystemVersion1.createClassificationAttribute("supplier");

		attrFormats = classificationSystemVersion1.createClassificationAttribute("formats");
		vFormatCd = classificationSystemVersion1.createClassificationAttributeValue("cd");
		vFormatCd.setName(enCtx, "CD");
		vFormatMp3 = classificationSystemVersion1.createClassificationAttributeValue("mp3");
		vFormatMp3.setName(enCtx, "MP3");
		vFormatWma = classificationSystemVersion1.createClassificationAttributeValue("WMA");
		vFormatWma.setName(enCtx, "WMA");

		attrBookshelf = classificationSystemVersion1.createClassificationAttribute("bookshelf");
		attrPower = classificationSystemVersion1.createClassificationAttribute("power");

		uKg = classificationSystemVersion1.createAttributeUnit("kg", "kg");
		uWatt = classificationSystemVersion1.createAttributeUnit("W", "W");
	}

	protected void assignAttributes()
	{
		clHifi.assignAttribute(attrWeight, T_NUMBER, uKg, null, 0);
		clHifi.assignAttribute(attrSupplier, T_STRING, null, null, 0);
		clHifi.assignAttribute(attrColor, T_ENUM, null, Arrays.asList(new Object[]
		{ vRed, vGreen, vYellow }), 0);
		clLoudspeaker.assignAttribute(attrBookshelf, T_BOOLEAN, null, null, 0);
		clAmplifier.assignAttribute(attrPower, T_NUMBER, uWatt, null, 0);
		clCdplayer.assignAttribute(attrFormats, T_ENUM, null, Arrays.asList(new Object[]
		{ vFormatCd, vFormatMp3, vFormatWma }), 0);
	}

	protected TypeService getTypeService()
	{
		if (this.typeService == null)
		{
			this.typeService = (TypeService) applicationContext.getBean("cockpitTypeService");
		}
		return this.typeService;
	}

	@Test
	public void testTypeSystem() throws ConsistencyCheckException
	{
		createSystems();
		createClasses();
		createAttributes();
		assignAttributes();

		final Map<String, Object> values = new HashMap<String, Object>();
		values.put(CockpitItemTemplate.RELATEDTYPE, TypeManager.getInstance().getComposedType(Product.class));
		values.put(CockpitItemTemplate.CODE, "DefaultTemplate");
		CockpitManager.getInstance().createCockpitItemTemplate(values);
		values.put(CockpitItemTemplate.CODE, "HifiTemplate");
		tplHifi = CockpitManager.getInstance().createCockpitItemTemplate(values);
		List<ClassificationClass> classes = new ArrayList<ClassificationClass>();
		classes.add(clHifi);
		tplHifi.setClassificationClasses(classes);
		values.put(CockpitItemTemplate.CODE, "AmplifierTemplate");
		tplAmplifier = CockpitManager.getInstance().createCockpitItemTemplate(values);
		classes = new ArrayList<ClassificationClass>();
		classes.add(clAmplifier);
		tplAmplifier.setClassificationClasses(classes);

		product1 = ProductManager.getInstance().createProduct("Product1");
		clLoudspeaker.addProduct(product1);

		final TypeService typeService = getTypeService();
		final BaseType baseType = typeService.getBaseType("Product");
		assertEquals(baseType.getCode(), "Product");

		final TypedObject typedObject = typeService.wrapItem(product1.getPK());
		assertNotNull(typedObject);
		assertEquals(typedObject.getType(), baseType);

		assertEquals(typedObject.getExtendedTypes(),
				Collections.singletonList(typeService.getExtendedType("system1/v1/Loudspeakers")));

		final Set<ObjectTemplate> expectedTemplates = new HashSet<ObjectTemplate>();
		expectedTemplates.add(typeService.getObjectTemplate("Product.DefaultTemplate"));
		expectedTemplates.add(typeService.getObjectTemplate("Product.HifiTemplate"));
		assertEquals(expectedTemplates, new HashSet(typedObject.getPotentialTemplates()));
	}
}
