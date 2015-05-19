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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.testframework.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class CsticModelImplTest
{
	private CsticModel model;

	@Before
	public void setUp()
	{
		model = new CsticModelImpl();
	}

	@Test
	public void testSetSingleValue()
	{
		assertFalse(model.isChangedByFrontend());
		assertEquals(0, model.getAssignedValues().size());

		model.setSingleValue("newValue");
		assertTrue(model.isChangedByFrontend());
		assertEquals(1, model.getAssignedValues().size());
		assertEquals("newValue", model.getSingleValue());
	}

	@Test
	public void testSetSingleValue_notChanged()
	{
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("newValue");
		model.setAssignedValuesWithoutCheckForChange(Collections.singletonList(value));
		assertFalse(model.isChangedByFrontend());
		assertEquals(1, model.getAssignedValues().size());

		model.setSingleValue("newValue");
		assertFalse(model.isChangedByFrontend());
		assertEquals(1, model.getAssignedValues().size());
		assertEquals("newValue", model.getAssignedValues().get(0).getName());
	}


	@Test
	public void testAddValue()
	{
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("anotherValue");
		model.setAssignedValuesWithoutCheckForChange(Collections.singletonList(value));
		assertFalse(model.isChangedByFrontend());
		assertEquals(1, model.getAssignedValues().size());

		model.addValue("newValue");
		assertTrue(model.isChangedByFrontend());
		assertEquals(2, model.getAssignedValues().size());
		assertEquals("newValue", model.getAssignedValues().get(1).getName());
	}

	@Test
	public void testRemoveExistingValue()
	{
		final String value = "value";
		model.addValue(value);
		model.setChangedByFrontend(false);

		model.removeValue(value);

		assertTrue("Model was changed", model.isChangedByFrontend());
		assertEquals("Wrong number of values", 0, model.getAssignedValues().size());
	}

	@Test
	public void testRemoveNonExistingValue()
	{
		model.addValue("value1");
		model.setChangedByFrontend(false);

		model.removeValue("value2");

		assertFalse("Model was not changed", model.isChangedByFrontend());
		assertEquals("Wrong number of values", 1, model.getAssignedValues().size());
	}

	@Test
	public void testAddValue_notChanged()
	{
		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		CsticValueModel value = new CsticValueModelImpl();
		value.setName("anotherValue");
		assignedValues.add(value);
		value = new CsticValueModelImpl();
		value.setName("newValue");
		assignedValues.add(value);
		model.setAssignedValuesWithoutCheckForChange(assignedValues);
		assertFalse(model.isChangedByFrontend());
		assertEquals(2, model.getAssignedValues().size());

		model.addValue("newValue");
		assertFalse(model.isChangedByFrontend());
		assertEquals(2, model.getAssignedValues().size());
		assertEquals("newValue", model.getAssignedValues().get(1).getName());
	}

	@Test
	public void testAddConflict()
	{
		boolean hasConflicts = model.hasConflicts();
		assertFalse("The model has no conflicts initially", hasConflicts);

		final ConflictModel conflict = new ConflictModelImpl();
		model.addConflict(conflict);

		hasConflicts = model.hasConflicts();
		assertTrue("The model has one conflict", hasConflicts);
	}

	@Test
	public void testConflictCollection()
	{
		ConflictModel conflict = new ConflictModelImpl();
		model.addConflict(conflict);
		conflict = new ConflictModelImpl();
		model.addConflict(conflict);

		final Collection<ConflictModel> conflicts = model.getConflicts();
		assertEquals("2 conflicts expected", 2, conflicts.size());
	}

	@Test(expected = Exception.class)
	public void testImmutable()
	{
		final ConflictModel conflict = new ConflictModelImpl();
		model.getConflicts().add(conflict);
	}

	@Test
	public void testCloneConflicts()
	{
		ConflictModel conflict = new ConflictModelImpl();
		model.addConflict(conflict);

		final CsticModel clone = model.clone();
		assertEquals("same number of conflicts", model.getConflicts().size(), clone.getConflicts().size());

		conflict = new ConflictModelImpl();
		model.addConflict(conflict);

		Assert.assertNotEquals("different number of conflicts expected", Integer.valueOf(model.getConflicts().size()),
				Integer.valueOf(clone.getConflicts().size()));

	}

	@Test
	public void testClearConflicts()
	{
		final ConflictModel conflict = new ConflictModelImpl();
		model.addConflict(conflict);

		assertEquals("Before clear: 1 conflict", 1, model.getConflicts().size());

		model.clearConflicts();

		assertEquals("After clear: 0 conflict", 0, model.getConflicts().size());
	}

	@Test
	public void testEquals()
	{
		final CsticModel cstic1 = new CsticModelImpl();
		assertFalse(cstic1.equals(null));
		assertFalse(cstic1.equals("FALSE"));

		final CsticModel cstic2 = new CsticModelImpl();

		assertTrue(cstic1.equals(cstic2));
	}

	@Test
	public void testClone()
	{
		final CsticModel cstic = new CsticModelImpl();
		cstic.setAuthor(CsticModel.AUTHOR_SYSTEM);

		final CsticModel clonedCstic = cstic.clone();
		assertEquals(cstic.getAuthor(), clonedCstic.getAuthor());
	}
}
