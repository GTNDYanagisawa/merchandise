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
package de.hybris.platform.sap.core.common.util;

import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Test for Default Generic Factory.
 */
@UnitTest
@ContextConfiguration(locations =
{ "DefaultGenericFactoryTest-spring.xml" })
public class DefaultGenericFactoryTest extends SapcoreSpringJUnitTest
{

	/**
	 * Remove session scoped bean test.
	 */
	@Test
	public void testRemoveSessionScopeBean()
	{
		final Object beanBefore = genericFactory.getBean("testBean");
		genericFactory.removeBean("testBean");
		final Object beanAfter = genericFactory.getBean("testBean");
		assertNotSame(beanBefore, beanAfter);
	}

}
