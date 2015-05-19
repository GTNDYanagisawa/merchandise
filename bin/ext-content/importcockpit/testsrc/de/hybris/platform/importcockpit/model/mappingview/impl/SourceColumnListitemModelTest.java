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
package de.hybris.platform.importcockpit.model.mappingview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.importcockpit.model.mappingview.SourceColumnModel;

import org.junit.Test;

@UnitTest
public class SourceColumnListitemModelTest
{

	@Test
	public void testSourceColumnListitemEquals()
	{
		final SourceColumnModel col1 = new SourceColumnListitemModel("id1", "label1");
		final SourceColumnModel col2 = new SourceColumnListitemModel("id2", "label2");
		final SourceColumnModel col3 = new SourceColumnListitemModel("id1", "label1");
		final SourceColumnModel col4 = new SourceColumnListitemModel("id1", "label1");

		assertTrue("Reflexive", col1.equals(col1));
		assertTrue("Symmetric", col1.equals(col3));
		assertTrue("Symmetric", col3.equals(col1));
		assertEquals("equals() == true => hashes should be same", col1.hashCode(), col3.hashCode());
		assertTrue("Transitive", col3.equals(col4));
		assertTrue("Transitive", col1.equals(col4));
		assertFalse("Different id and label", col1.equals(col2));
		assertFalse("Types do not match", col1.equals(this));
	}

	@Test
	public void testSourceColumnListitemConstructorsAndHashCode()
	{
		try
		{
			SourceColumnModel column = new SourceColumnListitemModel(null, null);
			column.hashCode();
			column = new SourceColumnListitemModel("id", null);
			column.hashCode();
			column = new SourceColumnListitemModel("id");
			column.hashCode();
			column = new SourceColumnListitemModel();
			column.hashCode();
		}
		catch (final NullPointerException npe)
		{
			fail("Label and id may be null; using them in hashCode must be null safe.");
		}
	}

}
