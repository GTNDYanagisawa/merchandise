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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@SuppressWarnings("javadoc")
public class ObjectTestUtil
{

	public static String cloneString(String str)
	{
		if (str == null)
		{
			return null;
		}
		else
		{
			return new String(str.getBytes().clone());
		}
	}

	public static void assertHashCodeContract(Object[] equal, //
			Object[] equalOpt, //
			Object[] differnt)
	{

		assertHashCodeContractRule1(equal[0]);
		assertHashCodeContractRule2eq(equal);
		if (equalOpt != null)
		{
			assertHashCodeContractRule2eq(equalOpt);
		}
		assertHashCodeContractRule2ne(equal[0], differnt);
		/*
		 * Rule 3 -- test too difficult
		 * 
		 * It is not required that if two objects are unequal according to the equals(java.lang.Object) method, then
		 * calling the hashCode method on each of the two objects must produce distinct integer results. However, the
		 * programmer should be aware that producing distinct integer results for unequal objects may improve the
		 * performance of hash tables.
		 */
	}

	public static final String PRECON = "precondition ";

	public static void assertHashCodeContractRule1(Object obj)
	{
		/*
		 * Rule 1:
		 * 
		 * Whenever it is invoked on the same object more than once during an execution of a Java application, the
		 * hashCode method must consistently return the same integer, provided no information used in equals comparisons
		 * on the object is modified.
		 */
		int hashCode = obj.hashCode();
		for (int i = 5; i > 0; --i)
		{
			assertEquals(hashCode, obj.hashCode());
		}
	}

	public static void assertHashCodeContractRule2eq(Object[] equal)
	{
		/*
		 * Rule 2:
		 * 
		 * If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of
		 * the two objects must produce the same integer result.
		 */
		assertTrue(PRECON, equal.length >= 2);
		for (int i = 1; i < equal.length; ++i)
		{
			String location = "equal [" + i + "] " + equal[i];
			assertEquals(PRECON + location, equal[0], equal[i]);
			assertEquals(location, equal[0].hashCode(), equal[i].hashCode());
		}
	}

	public static void assertHashCodeContractRule2ne(Object equal, Object[] differnt)
	{
		/*
		 * Rule 2:
		 * 
		 * If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of
		 * the two objects must produce the same integer result.
		 */
		assertTrue(PRECON, differnt.length > 0);
		for (int i = 0; i < differnt.length; ++i)
		{
			String location = "unequal [" + i + "] " + differnt[i];
			assertFalse(PRECON + location, equal.equals(differnt[i]));
			assertFalse(location, equal.hashCode() == differnt[i].hashCode());
		}
	}

	public static void assertEqualsContract(Object[] equal, //
			Object[] equalOpt, //
			Object[] differnt)
	{
		assertEqualsContractR1(equal);
		if (equalOpt != null)
		{
			assertEqualsContractR1(equalOpt);
		}
		assertEqualsContractR2(equal, differnt);
		assertEqualsContractR3(equal);
		assertEqualsContractR4(equal, differnt[0]);
		assertEqualsContractR5(equal);
		if (equalOpt != null)
		{
			assertEqualsContractR1(equalOpt);
		}
	}

	public static void assertEqualsContractR1(Object ob)
	{
		/*
		 * Rule1: It is reflexive: for any non-null reference value x, x.equals(x) should return true.
		 */
		assertTrue(ob.equals(ob));
	}

	public static void assertEqualsContractR2(Object[] equal, Object[] differnt)
	{
		/*
		 * Rule 2:
		 * 
		 * It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if
		 * y.equals(x) returns true.
		 */

		assertTrue(PRECON, equal.length >= 2);
		for (int i = 1; i < equal.length; ++i)
		{
			String location = "equal [" + i + "] " + equal[i];
			assertTrue(location, equal[0].equals(equal[i]));
			assertTrue(location, equal[i].equals(equal[0]));
		}
		assertTrue(PRECON, differnt.length >= 1);
		for (int i = 0; i < differnt.length; ++i)
		{
			String location = "equal [" + i + "] " + differnt[i];
			assertFalse(location, equal[0].equals(differnt[i]));
			assertFalse(location, differnt[i].equals(equal[0]));
		}
	}

	public static void assertEqualsContractR3(Object[] equal)
	{
		/*
		 * Rule 3:
		 * 
		 * It is transitive: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z)
		 * returns true, then x.equals(z) should return true.
		 */

		assertTrue(PRECON, equal.length >= 3);
		assertTrue(equal[0].equals(equal[1]));
		assertTrue(equal[2].equals(equal[2]));
		assertTrue(equal[2].equals(equal[0]));
	}

	public static void assertEqualsContractR4(Object[] equal, Object differnt)
	{
		/*
		 * Rule4:
		 * 
		 * It is consistent: for any non-null reference values x and y, multiple invocations of x.equals(y) consistently
		 * return true or consistently return false, provided no information used in equals comparisons on the objects is
		 * modified.
		 */

		assertTrue(PRECON, equal.length >= 2);
		// assertTrue(PRECON, differnt.length >= 1);
		for (int i = 0; i < 5; ++i)
		{
			assertTrue(equal[0].equals(equal[1]));
			assertFalse(equal[0].equals(differnt));
		}
	}

	public static void assertEqualsContractR5(Object[] equal)
	{
		/*
		 * Rule 5:
		 * 
		 * For any non-null reference value x, x.equals(null) should return false
		 */
		Object differntClass = new Object();

		assertTrue(PRECON, equal.length >= 1);
		for (int i = 1; i < equal.length; ++i)
		{
			String location = "equal [" + i + "] " + equal[i];
			assertFalse(location, equal[0].equals(null));
			assertFalse(equal[0].equals(differntClass));
		}
	}

}
