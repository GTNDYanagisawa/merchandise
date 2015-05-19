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
package de.hybris.platform.webservices.util.objectgraphtransformer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;



public class ClassLookupMapTest //extends HybrisJUnit4TransactionalTest
{

	@Test
	public void testSimple()
	{
		//initialize with AbstractList and LinkedList
		final Map testMap = this.createClassLookuMap();
		testMap.put(AbstractList.class, "AbstractList");
		testMap.put(LinkedList.class, "LinkedList");

		//test request ArrayList 
		assertFalse(testMap.containsKey(ArrayList.class));
		assertEquals("AbstractList", testMap.get(ArrayList.class));
		assertTrue(testMap.containsKey(ArrayList.class));

		//test request AbstractList 
		assertEquals("AbstractList", testMap.get(AbstractList.class));

		//test request String (must return null)
		assertFalse(testMap.containsKey(String.class));
		assertNull(testMap.get(String.class));
		assertTrue(testMap.containsKey(String.class));

		//test request Object (must return null) 
		assertFalse(testMap.containsKey(Object.class));
		assertNull(testMap.get(Object.class));
		assertTrue(testMap.containsKey(Object.class));

		//put Object and test String again
		testMap.put(Object.class, "Object");
		assertEquals("Object", testMap.get(HashMap.class));
		assertEquals("Object", testMap.get(String.class));
	}

	@Test
	public void testIntfKeyLookup()
	{
		// general NPE check
		Map testMap = this.createClassLookuMap();

		try
		{
			testMap.get(List.class);
		}
		catch (final Exception e)
		{
			Assert.fail(e.toString());
		}

		// interface  
		testMap = this.createClassLookuMap();
		testMap.put(Collection.class, "collection");
		assertEquals("collection", testMap.get(List.class));
	}

	@Test
	public void testClassKeyLookup()
	{

		// class implements interface directly
		Map testMap = this.createClassLookuMap();
		testMap.put(List.class, "list");
		assertEquals("list", testMap.get(ArrayList.class));

		// class indirectly implements interface (as super-interface of an existing interface)
		testMap = this.createClassLookuMap();
		testMap.put(Iterable.class, "iterable");
		assertEquals("iterable", testMap.get(ArrayList.class));

		// a superclass implements interface; and key is an inner class
		testMap.put(Collection.class, "collection");
		final Class key = Arrays.asList("1", "2", "3").subList(0, 1).getClass();
		assertEquals("collection", testMap.get(key));

		// super-interface implementation rules over direct ones 
		testMap = this.createClassLookuMap();
		// super interface for list
		testMap.put(Collection.class, "collection");
		// direct implementation for ArrayList
		testMap.put(Serializable.class, "serializable");

		assertEquals("collection", testMap.get(LinkedList.class));
	}



	@Test
	public void testSimpleWithObject()
	{
		final Map testMap = this.createClassLookuMap();

		// a simple test with 'object', the onliest class with no superclass (except interfaces) 
		testMap.put(Object.class, "object");
		testMap.put(Collection.class, "collection");
		assertEquals("object", testMap.get(Object.class));
		assertEquals("object", testMap.get(String.class));
	}

	@Test
	public void testObjectInheritanceOverIntf()
	{
		final Map testMap = this.createClassLookuMap();
		testMap.put(AbstractCollection.class, "abstractcollection");
		testMap.put(List.class, "list");

		assertEquals("abstractcollection", testMap.get(ArrayList.class));
	}

	@Test
	public void testModifyMap()
	{
		// hierarchy: AbstractCollection -> AbstractList
		final Map testMap = this.createClassLookuMap();

		// add mapping for a base type  (AbstractCollection)
		testMap.put(AbstractCollection.class, "AbstractCollection");

		// request value for a subtype (AbstractList); 
		assertEquals("AbstractCollection", testMap.get(AbstractList.class));

		// now set a mapping for that subtype and after that set another mapping  
		testMap.put(AbstractList.class, "AbstractList");
		testMap.put(Object.class, "AbstractList");

		assertEquals("AbstractList", testMap.get(AbstractList.class));
	}

	@Test
	public void testClear()
	{
		final Map testMap = this.createClassLookuMap();
		testMap.put(Collection.class, "collection");
		testMap.get(ArrayList.class);

		testMap.clear();
		testMap.put(ArrayList.class, "arraylist");
		testMap.put(Collection.class, "collection");

		assertEquals("arraylist", testMap.get(ArrayList.class));
	}

	private Map createClassLookuMap()
	{
		return new CachedClassLookupMap<Object>();
	}


	public void testTest()
	{
		final Map testMap = this.createClassLookuMap();
		testMap.put(Collection.class, "collection");

		assertEquals("collection", testMap.get(List.class));
	}

	public static void main(final String argc[])
	{
		(new ClassLookupMapTest()).testTest();
	}



}
