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
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictModelImpl;
import de.hybris.platform.testframework.Assert;

import org.junit.Test;


@UnitTest
public class ConflictModelImplTest
{
	private final ConflictModel model = new ConflictModelImpl();

	@Test
	public void testConflictTest()
	{
		final String conflictText = "conflict1";
		model.setText(conflictText);

		assertEquals(conflictText, model.getText());
	}

	@Test
	public void testClone()
	{
		final ConflictModel clone = model.clone();

		final String conflictText = "conflict1";
		model.setText(conflictText);

		Assert.assertNotEquals(model.getText(), clone.getText());
	}


	@Test
	public void testCloneMustBeEquals() throws Exception
	{
		final String conflictText = "conflict1";
		model.setText(conflictText);

		final ConflictModel clone = model.clone();

		assertEquals("Clone must be equal", model, clone);

	}

	@Test
	public void testCloneMustHaveSomeHashCode() throws Exception
	{
		final String conflictText = "conflict1";
		model.setText(conflictText);

		final ConflictModel clone = model.clone();

		assertEquals("Clone must be equal", model.hashCode(), clone.hashCode());

	}

}
