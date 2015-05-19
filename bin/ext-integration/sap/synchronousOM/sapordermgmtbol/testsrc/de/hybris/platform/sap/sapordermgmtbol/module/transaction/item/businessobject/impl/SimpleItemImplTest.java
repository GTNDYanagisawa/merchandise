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
package de.hybris.platform.sap.sapordermgmtbol.module.transaction.item.businessobject.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.SimpleItemImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.SimpleItem;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SimpleItemImplTest extends SapordermanagmentBolSpringJunitTest
{
	@Resource(name = "sapOrdermgmtDefaultItem")
	private SimpleItemImpl classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
	}

	@Test
	public void testSetterGetter()
	{
		classUnderTest.setParentId(new TechKey("123"));
		Assert.assertEquals("123", classUnderTest.getParentId().getIdAsString());
		classUnderTest.setProductId("abc");
		Assert.assertEquals("abc", classUnderTest.getProductId());
		classUnderTest.setProductGuid(new TechKey("456"));
		Assert.assertEquals("456", classUnderTest.getProductGuid().getIdAsString());
		classUnderTest.setDescription("descr");
		Assert.assertEquals("descr", classUnderTest.getDescription());
		classUnderTest.setQuantity(BigDecimal.TEN);
		Assert.assertTrue(BigDecimal.TEN.compareTo(classUnderTest.getQuantity()) == 0);
	}

	@Test
	public void test_getTypedExtensionMap()
	{
		final Map<String, Object> typedExtensionMap = classUnderTest.getTypedExtensionMap();
		Assert.assertNotNull(typedExtensionMap);
		typedExtensionMap.put("test_key", "test_value");
		Assert.assertEquals("test_value", typedExtensionMap.get("test_key"));
	}

	@Test
	public void test_getLastQuantity()
	{
		Assert.assertNull(classUnderTest.getLastQuantity());
		classUnderTest.setQuantity(BigDecimal.ONE);
		Assert.assertNull(classUnderTest.getLastQuantity());
		classUnderTest.setQuantity(null);
		Assert.assertSame(BigDecimal.ONE, classUnderTest.getLastQuantity());

	}

	@Test
	public void testCompareSmaller()
	{
		classUnderTest.setNumberInt(10);
		final SimpleItem itemToCompare = new SimpleItemImpl();
		itemToCompare.setNumberInt(20);

		final int result = classUnderTest.compareTo(itemToCompare);
		Assert.assertEquals(-1, result);
	}

	@Test
	public void testCompareGreater()
	{
		classUnderTest.setNumberInt(30);
		final SimpleItem itemToCompare = new SimpleItemImpl();
		itemToCompare.setNumberInt(20);

		final int result = classUnderTest.compareTo(itemToCompare);
		Assert.assertEquals(1, result);
	}

}
