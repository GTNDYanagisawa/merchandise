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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.productconfig.runtime.interf.model.BaseModel;

import org.junit.Test;


public class BaseModelImplTest
{
	private final BaseModel model = new BaseModelImpl();

	@Test
	public void testClone()
	{

		final String string1 = "String1";
		final String string2 = "String2";
		final String string3 = "String3";
		model.putExtensionData("1", string1);
		model.putExtensionData("2", string2);
		model.putExtensionData("3", string3);

		final BaseModel clonedModel = model.clone();

		final String clonedString1 = clonedModel.getExtensionData("1");
		final String clonedString2 = clonedModel.getExtensionData("2");
		final String clonedString3 = clonedModel.getExtensionData("3");

		assertFalse(model == clonedModel);
		assertTrue(string1 == clonedString1);
		assertTrue(string2 == clonedString2);
		assertTrue(string3 == clonedString3);

		final String newString1 = "New String 1";
		clonedModel.putExtensionData("1", newString1);
		assertFalse(string1 == clonedModel.getExtensionData("1"));
	}
}
