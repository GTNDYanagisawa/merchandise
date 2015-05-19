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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.AlternativeProductImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.AlternativeProductListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.AlternativeProduct;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class AlternativProductListImplTest extends SapordermanagmentBolSpringJunitTest
{
	private final AlternativeProductListImpl classUnderTest = new AlternativeProductListImpl();
	private final String systemProductId = "A";
	final TechKey systemProductGUID = new TechKey(systemProductId);
	final String description = "As";
	final String enteredProductIdType = "X";

	final String substitutionReasonId = "B";


	@Before
	public void init()
	{
		classUnderTest.setGenericFactory(genericFactory);
	}

	@Test
	public void testAddProduct()
	{

		assertTrue(classUnderTest.isEmpty());

		final AlternativeProduct altProd = new AlternativeProductImpl();
		classUnderTest.addAlternativProduct(0, altProd);

		assertEquals(1, classUnderTest.size());
	}

	@Test
	public void testAddProductAtBeginning()
	{

		final AlternativeProduct altProd1 = new AlternativeProductImpl();
		classUnderTest.addAlternativProduct(altProd1);
		assertEquals(1, classUnderTest.size());

		final AlternativeProduct altProd2 = new AlternativeProductImpl();
		classUnderTest.addAlternativProduct(0, altProd2);
		assertEquals(2, classUnderTest.size());

		assertTrue(altProd2 == classUnderTest.getAlternativProduct(0));
	}

	@Test
	public void testAddProductAtEnd()
	{

		final AlternativeProduct altProd1 = new AlternativeProductImpl();
		classUnderTest.addAlternativProduct(altProd1);
		assertEquals(1, classUnderTest.size());

		final AlternativeProduct altProd2 = new AlternativeProductImpl();
		classUnderTest.addAlternativProduct(1, altProd2);
		assertEquals(2, classUnderTest.size());

		assertTrue(altProd2 == classUnderTest.getAlternativProduct(1));
	}

	@Test
	public void testClear()
	{
		classUnderTest.addAlternativProduct(new AlternativeProductImpl());
		assertEquals(1, classUnderTest.size());

		classUnderTest.clear();
		assertEquals(0, classUnderTest.size());
		assertTrue(classUnderTest.isEmpty());
	}

	@Test
	public void testAddAlternativeProduct()
	{
		addProduct();
		assertEquals(classUnderTest.getList().size(), 1);
	}

	@Test
	public void testClone()
	{
		addProduct();
		final AlternativeProductListImpl clone = (AlternativeProductListImpl) classUnderTest.clone();
		assertNotNull(clone);
		assertEquals(classUnderTest.getList().size(), 1);
		assertFalse(classUnderTest.getAlternativProduct(0).equals(clone.getAlternativProduct(0)));
	}

	@Test
	public void testCreate()
	{
		assertNotNull(classUnderTest.createAlternativProduct());
	}

	@Test
	public void testCreateWAttributes()
	{
		assertNotNull(classUnderTest.createAlternativProduct(systemProductId, systemProductGUID, description, enteredProductIdType,
				substitutionReasonId));
	}

	@Test
	public void testIsDetermination()
	{
		assertFalse(classUnderTest.isDeterminationProductList());
	}

	@Test
	public void testIsSubstitution()
	{
		assertFalse(classUnderTest.isSubstituteProductList());
	}

	@Test
	public void testSetList()
	{
		final List<AlternativeProduct> alternativProductListData = Collections.emptyList();
		classUnderTest.setList(alternativProductListData);
		assertEquals(alternativProductListData, classUnderTest.getList());
		assertEquals(alternativProductListData.toString(), classUnderTest.toString());
	}

	/**
	 * 
	 */
	private void addProduct()
	{


		classUnderTest.addAlternativProduct(systemProductId, systemProductGUID, description, enteredProductIdType,
				substitutionReasonId);
	}

}
