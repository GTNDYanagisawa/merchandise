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
package de.hybris.platform.sap.core.test.property;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * Property Access Factory test.
 */
@UnitTest
public class PropertyAccessFactoryTest
{
	/**
	 * Tests creation of property access.
	 */
	@Test
	public void testCreatePropertyAccess()
	{
		final PropertyAccessFactory classUnderTest = new PropertyAccessFactory();
		Assert.assertNotNull(classUnderTest.createPropertyAccess());
	}

}
