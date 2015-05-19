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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class ItemTextMapperTest extends SapordermanagmentBolSpringJunitTest
{

	@Test
	public void testBeanInitializtion()
	{
		final ItemTextMapper cut = (ItemTextMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_TEXT_MAPPER);
		assertNotNull(cut);
	}


}
