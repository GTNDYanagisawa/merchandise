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
package de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.impl.erp;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class BasketERPTest extends SapordermanagmentBolSpringJunitTest
{

	private BasketERP classUnderTest;

	@Test
	public void testBeanInstantiation()
	{

		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.BEAN_ID_BE_CART_ERP);

		assertNotNull(classUnderTest.getAdditionalPricing());

	}


}
