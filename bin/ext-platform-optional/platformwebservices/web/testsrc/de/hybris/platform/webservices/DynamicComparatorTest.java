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
package de.hybris.platform.webservices;

import static org.junit.Assert.assertTrue;

import de.hybris.platform.webservices.objectgraphtransformer.DynamicComparator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 */
public class DynamicComparatorTest
{

	private TestClass smallerInstance;
	private TestClass biggerInstance;

	@Before
	public void setUp()
	{
		smallerInstance = new TestClass("aaaa", 3);
		biggerInstance = new TestClass("bbbb", 1);
	}

	/**
	 * Tests if DynamicComparator is able to compare two javaBeans (that do NOT implement java.lang.Comparable) using a property
	 * that returns a Comparable result.
	 */
	@Test
	public void testExistingProperty()
	{
		final DynamicComparator dynamicComparator = new DynamicComparator("name");
		final TestClass biggerInstance2 = new TestClass("bbbb", 3);

		assertTrue("Invalid comparison result:", dynamicComparator.compare(smallerInstance, biggerInstance) < 0);
		assertTrue("Invalid comparison result:", dynamicComparator.compare(biggerInstance, smallerInstance) > 0);
		assertTrue("Invalid comparison result:", dynamicComparator.compare(biggerInstance, biggerInstance2) == 0);

	}

	/**
	 * Tests that DynamicComparator throws an Exception when trying to compare two javaBeans (that do NOT implement
	 * java.lang.Comparable) using a non-existing property.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNonExistingProperty()
	{
		final DynamicComparator dynamicComparator = new DynamicComparator("color");
		assertTrue("Invalid comparison result:", dynamicComparator.compare(smallerInstance, biggerInstance) < 0);
	}

	/**
	 * Tests that DynamicComparator throws an Exception when trying to compare two javaBeans (that do NOT implement
	 * java.lang.Comparable) using a property that does NOT return a Comparable result
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNonComparableProperty()
	{
		final DynamicComparator dynamicComparator = new DynamicComparator("size");
		assertTrue("Invalid comparison result:", dynamicComparator.compare(smallerInstance, biggerInstance) < 0);
	}

	/**
	 * Tests the "fallback" mechanism of DynamicComparator when trying to compare two javaBeans using non-existing property. If the
	 * comparison property cannot be found AND javaBeans implement java.lang.Comparable, comparator tries to compare javaBeans
	 * directly using their compareTo() method.
	 */
	@Test
	public void testDirectComparisonFallback()
	{
		final DynamicComparator dynamicComparator = new DynamicComparator("aaaa");
		final ComparableTestClass smallerInstance = new ComparableTestClass(1);
		final ComparableTestClass biggerInstance = new ComparableTestClass(2);
		assertTrue("Invalid comparison result:", dynamicComparator.compare(smallerInstance, biggerInstance) < 0);
	}


	/**
	 * Tests if DynamicComparator is capable of comparing instances of anonymous inner classes (java reflection visibility issues)
	 * For now it does not work. It is a well-known issue with reflection, because anonymous inner classes are compiler-generated
	 * with package private visibility and of course any "outside" class cannot access it reflectively (without trying to change
	 * access permissions).
	 */
	@Test
	@Ignore("PLA-11441")
	public void testAnonymousInnerClasses()
	{
		final DynamicComparator dynamicComparator = new DynamicComparator("nickName");
		final TestBeanInterface smallerInstance = new TestBeanInterface()
		{
			@Override
			public String getNickName()
			{
				return "aaa";
			}
		};

		final TestBeanInterface biggerInstance = new TestBeanInterface()
		{
			@Override
			public String getNickName()
			{
				return "bbb";
			}
		};

		assertTrue("Invalid comparison result:", dynamicComparator.compare(smallerInstance, biggerInstance) < 0);
	}


	public static class TestClass
	{
		String name;
		int size;

		public TestClass()
		{
			//default constructor
		}

		/**
		 * @param name
		 * @param size
		 */
		public TestClass(final String name, final int size)
		{
			super();
			this.name = name;
			this.size = size;
		}

		/**
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(final String name)
		{
			this.name = name;
		}

		/**
		 * @return the size
		 */
		public int getSize()
		{
			return size;
		}

		/**
		 * @param size the size to set
		 */
		public void setSize(final int size)
		{
			this.size = size;
		}
	}

	public static class ComparableTestClass implements Comparable<ComparableTestClass>
	{

		private int value;

		/**
		 * @param value
		 */
		public ComparableTestClass(final int value)
		{
			super();
			this.value = value;
		}

		@Override
		public int compareTo(final ComparableTestClass object)
		{
			return Integer.valueOf(this.value).compareTo(Integer.valueOf(object.value));
		}

		/**
		 * @return the value
		 */
		public int getValue()
		{
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(final int value)
		{
			this.value = value;
		}


	}


	public interface TestBeanInterface
	{
		String getNickName();
	}

}
