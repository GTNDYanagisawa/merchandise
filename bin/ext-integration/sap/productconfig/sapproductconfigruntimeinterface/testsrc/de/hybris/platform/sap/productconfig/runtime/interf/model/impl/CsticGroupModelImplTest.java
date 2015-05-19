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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.testframework.Assert;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


@UnitTest
public class CsticGroupModelImplTest
{
	public final static String CSTIC_1 = "CSTIC_1";
	public final static String CSTIC_2 = "CSTIC_2";

	public final static String NAME = "1";
	public final static String DESCRIPTION = "Group Description";

	private final CsticGroupModel csticGroupModel = new CsticGroupModelImpl();

	@Test
	public void testCsticGroupTest()
	{
		fillCsticGroup();

		assertEquals(NAME, csticGroupModel.getName());
		assertEquals(DESCRIPTION, csticGroupModel.getDescription());
		assertEquals(CSTIC_1, csticGroupModel.getCsticNames().get(0));
		assertEquals(CSTIC_2, csticGroupModel.getCsticNames().get(1));
	}

	@Test
	public void testClone()
	{

		fillCsticGroup();

		final CsticGroupModel clonedCsticGroup = csticGroupModel.clone();

		clonedCsticGroup.setName("2");
		clonedCsticGroup.setDescription("Other Description");
		final List<String> csticNames = new ArrayList<String>();
		csticNames.add(CSTIC_1 + "XXX");
		csticNames.add(CSTIC_2 + "XXX");
		clonedCsticGroup.setCsticNames(csticNames);

		Assert.assertNotEquals(csticGroupModel.getName(), clonedCsticGroup.getName());
		Assert.assertNotEquals(csticGroupModel.getDescription(), clonedCsticGroup.getDescription());
		Assert.assertNotEquals(csticGroupModel.getCsticNames().get(0), clonedCsticGroup.getCsticNames().get(0));
		Assert.assertNotEquals(csticGroupModel.getCsticNames().get(1), clonedCsticGroup.getCsticNames().get(1));
	}


	@Test
	public void testCloneMustBeEquals() throws Exception
	{
		fillCsticGroup();

		final CsticGroupModel clonedCsticGroup = csticGroupModel.clone();

		assertEquals("Clone must be equal", csticGroupModel, clonedCsticGroup);
	}

	@Test
	public void testCloneMustHaveSomeHashCode() throws Exception
	{
		fillCsticGroup();

		final CsticGroupModel clonedCsticGroup = csticGroupModel.clone();

		assertEquals("Clone must be equal", csticGroupModel.hashCode(), clonedCsticGroup.hashCode());

	}

	protected void fillCsticGroup()
	{

		final List<String> csticNames = new ArrayList<String>();
		csticNames.add(CSTIC_1);
		csticNames.add(CSTIC_2);

		csticGroupModel.setName(NAME);
		csticGroupModel.setDescription(DESCRIPTION);
		csticGroupModel.setCsticNames(csticNames);

	}

}
