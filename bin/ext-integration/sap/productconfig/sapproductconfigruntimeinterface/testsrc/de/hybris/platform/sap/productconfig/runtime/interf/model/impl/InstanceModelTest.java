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

import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class InstanceModelTest
{
	private InstanceModelImpl instanceModel;

	@Before
	public void setup()
	{
		instanceModel = new InstanceModelImpl();
	}

	@Test
	public void testRetrieveCsticGroups()
	{
		final List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createCstic("A"));
		cstics.add(createCstic("B"));
		cstics.add(createCstic("C"));
		cstics.add(createCstic("D"));

		instanceModel.setCstics(cstics);

		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		final CsticGroupModel grpA = createCsticGroup("1", "Grp A", "A", "B");
		final CsticGroupModel grpB = createCsticGroup("2", "Grp B", "C", "D");
		csticGroups.add(grpA);
		csticGroups.add(grpB);
		instanceModel.setCsticGroups(csticGroups);

		final List<CsticGroup> groups = instanceModel.retrieveCsticGroupsWithCstics();
		assertEquals(2, groups.size());

		final CsticGroup groupA = groups.get(0);
		final List<CsticModel> csticGroupA = groupA.getCstics();
		assertEquals(2, csticGroupA.size());
		assertEquals(grpA.getName(), groupA.getName());
		assertEquals(grpA.getDescription(), groupA.getDescription());

		assertEquals("A", csticGroupA.get(0).getName());
		assertEquals("B", csticGroupA.get(1).getName());


	}

	@Test
	public void testGetDisplayName()
	{
		final String displayname = instanceModel.getDisplayName("langDepName", "name");
		assertEquals("langDepName", displayname);
	}

	@Test
	public void testGetDisplayNameEmpty()
	{
		final String displayname = instanceModel.getDisplayName("", "name");
		assertEquals("[name]", displayname);
	}

	@Test
	public void testGetDisplayNameNull()
	{
		final String displayname = instanceModel.getDisplayName(null, "name");
		assertEquals("[name]", displayname);
	}



	private CsticModel createCstic(final String csticName)
	{
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);

		return cstic;
	}

	private CsticGroupModel createCsticGroup(final String groupName, final String description, final String... csticNames)
	{
		final List<String> csticNamesInGroup = new ArrayList<>();
		for (final String csticName : csticNames)
		{
			csticNamesInGroup.add(csticName);
		}

		final CsticGroupModel csticGroup = new CsticGroupModelImpl();
		csticGroup.setName(groupName);
		csticGroup.setDescription(description);
		csticGroup.setCsticNames(csticNamesInGroup);

		return csticGroup;
	}
}
