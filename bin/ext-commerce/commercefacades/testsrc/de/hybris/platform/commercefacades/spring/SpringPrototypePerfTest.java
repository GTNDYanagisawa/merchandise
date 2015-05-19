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
package de.hybris.platform.commercefacades.spring;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *   Test performance of looking up prototype bean vs object instance creation.
 *
 */
@IntegrationTest
@Ignore("Don't run in CI")
public class SpringPrototypePerfTest extends ServicelayerTest
{
	private int loops = 100000;

	@Override
	@Before
	public void init()
	{
		Registry.activateMasterTenant();
	}

	@Test
	public void testPerf()
	{
		long start = System.currentTimeMillis();
		for (int i = 0; i < loops; i++)
		{
			Registry.getApplicationContext().getBean("productData", ProductData.class);
		}
		System.out.println("Spring prototype = " + (System.currentTimeMillis() - start) + " ms");

		start = System.currentTimeMillis();
		for (int i = 0; i < loops; i++)
		{
			new ProductData();
		}
		System.out.println("Constructor = " + (System.currentTimeMillis() - start) + " ms");
	}
}
