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
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.CharacteristicValue;
import de.hybris.platform.sap.productconfig.runtime.interf.external.ContextAttribute;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;
import de.hybris.platform.sap.productconfig.runtime.interf.external.PartOfRelation;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;

import java.util.List;

import org.junit.Test;


@UnitTest
public class ConfigurationImplTest
{
	ConfigurationImpl classUnderTest = new ConfigurationImpl();

	@Test
	public void testRootInstance()
	{
		final Instance rootInstance = new InstanceImpl();
		classUnderTest.setRootInstance(rootInstance);
		assertEquals(rootInstance, classUnderTest.getRootInstance());
	}

	@Test
	public void testGetInstances()
	{
		final List<Instance> instances = classUnderTest.getInstances();
		assertNotNull(instances);
	}

	@Test
	public void testPartOfRelations()
	{
		final List<PartOfRelation> partOfRelations = classUnderTest.getPartOfRelations();
		assertNotNull(partOfRelations);
	}

	@Test
	public void testCharacteristicValues()
	{
		final List<CharacteristicValue> values = classUnderTest.getCharacteristicValues();
		assertNotNull(values);
	}

	@Test
	public void testContextAttributes()
	{
		final List<ContextAttribute> values = classUnderTest.getContextAttributes();
		assertNotNull(values);
	}

	@Test
	public void testAddInstance()
	{
		final Instance instance = new InstanceImpl();
		classUnderTest.addInstance(instance);
		final List<Instance> instances = classUnderTest.getInstances();
		assertNotNull(instances);
		assertEquals(1, instances.size());
	}

	@Test
	public void testAddPartOfRelation()
	{
		final PartOfRelation partOfRelation = new PartOfRelationImpl();
		classUnderTest.addPartOfRelation(partOfRelation);
		final List<PartOfRelation> partOfRelations = classUnderTest.getPartOfRelations();
		assertNotNull(partOfRelations);
		assertEquals(1, partOfRelations.size());
	}


	@Test
	public void testAddCsticValues()
	{
		final CharacteristicValue csticValue = new CharacteristicValueImpl();
		classUnderTest.addCharacteristicValue(csticValue);
		final List<CharacteristicValue> csticValuess = classUnderTest.getCharacteristicValues();
		assertNotNull(csticValuess);
		assertEquals(1, csticValuess.size());
	}

	@Test
	public void testAddContextAttribute()
	{
		final ContextAttribute contextAttribute = new ContextAttributeImpl();
		classUnderTest.addContextAttribute(contextAttribute);
		final List<ContextAttribute> contextAttributes = classUnderTest.getContextAttributes();
		assertNotNull(contextAttributes);
		assertEquals(1, contextAttributes.size());
	}

	@Test
	public void testKBKey()
	{
		final KBKey kbKey = new KBKeyImpl("TEST_PRODUCT");
		classUnderTest.setKbKey(kbKey);
		assertEquals(kbKey, classUnderTest.getKbKey());
	}
}
