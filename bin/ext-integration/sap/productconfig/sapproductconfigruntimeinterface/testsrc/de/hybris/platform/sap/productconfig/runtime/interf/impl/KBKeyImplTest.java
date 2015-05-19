package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;


public class KBKeyImplTest
{

	@Test
	public void testEquals()
	{
		final Date aSecondAgo = new Date(new Date().getTime() - 1000);
		KBKeyImpl key1 = new KBKeyImpl("A", "B", "C", "D", aSecondAgo);

		assertTrue(key1.equals(key1));
		assertFalse(key1.equals(null));
		assertFalse(key1.equals("DUMMY"));

		KBKeyImpl key2 = new KBKeyImpl("A", "B", "C", "D", aSecondAgo);
		assertTrue(key1.equals(key2));

		key2 = new KBKeyImpl("A", "B", "C", "D", null);
		assertFalse(key1.equals(key2));
		assertFalse(key2.equals(key1));

		key2 = new KBKeyImpl("A", "B", "C", null, aSecondAgo);
		assertFalse(key1.equals(key2));
		assertFalse(key2.equals(key1));

		key2 = new KBKeyImpl("A", "B", null, "D", aSecondAgo);
		assertFalse(key1.equals(key2));
		assertFalse(key2.equals(key1));

		key2 = new KBKeyImpl("A", null, "C", "D", aSecondAgo);
		assertFalse(key1.equals(key2));
		assertFalse(key2.equals(key1));

		key2 = new KBKeyImpl(null, "B", "C", "D", aSecondAgo);
		assertFalse(key1.equals(key2));
		assertFalse(key2.equals(key1));

		key1 = new KBKeyImpl("A", "B", "C", null, aSecondAgo);
		key2 = new KBKeyImpl("A", "B", "C", null, aSecondAgo);
		assertTrue(key1.equals(key2));
		assertTrue(key2.equals(key1));

		key1 = new KBKeyImpl("A", "B", null, "D", aSecondAgo);
		key2 = new KBKeyImpl("A", "B", null, "D", aSecondAgo);
		assertTrue(key1.equals(key2));
		assertTrue(key2.equals(key1));

		key1 = new KBKeyImpl("A", null, "C", "D", aSecondAgo);
		key2 = new KBKeyImpl("A", null, "C", "D", aSecondAgo);
		assertTrue(key1.equals(key2));
		assertTrue(key2.equals(key1));

		key1 = new KBKeyImpl(null, "B", "C", "D", aSecondAgo);
		key2 = new KBKeyImpl(null, "B", "C", "D", aSecondAgo);
		assertTrue(key1.equals(key2));
		assertTrue(key2.equals(key1));

		key1 = new KBKeyImpl("A", "B", "C", "D", null);
		key2 = new KBKeyImpl("A", "B", "C", "D", null);
		assertTrue(key1.equals(key2));
		assertTrue(key2.equals(key1));

	}

	@Test
	public void testHashCode()
	{
		final KBKeyImpl key = new KBKeyImpl(null, null, null, null, new Date(0l));
		assertEquals(28629151, key.hashCode());
	}

	@Test
	public void testToString()
	{
		final KBKeyImpl key = new KBKeyImpl(null, null, null, null, new Date(0l));
		assertEquals("KBKeyImpl [productCode=null, kbName=null, kbLogsys=null, kbVersion=null, date=Thu Jan 01 01:00:00 CET 1970]",
				key.toString());
	}
}
