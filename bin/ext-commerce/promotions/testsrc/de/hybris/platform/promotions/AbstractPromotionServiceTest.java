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
package de.hybris.platform.promotions;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import org.junit.Before;
import org.junit.Ignore;


@Ignore
@SuppressWarnings("PMD")
public abstract class AbstractPromotionServiceTest extends ServicelayerTransactionalTest
{

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
		importCsv("/test/promotionTestData.csv", "windows-1252");
	}

}
