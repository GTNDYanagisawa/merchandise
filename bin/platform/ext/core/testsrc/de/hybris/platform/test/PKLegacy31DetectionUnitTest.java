/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;

import org.junit.Test;


@UnitTest
public class PKLegacy31DetectionUnitTest
{

	@Test
	public void testGetCounterWithoutTenant()
	{
		final PK dummy = PK.createFixedCounterPK(1, 1234);
		final Tenant currentTenant = Registry.getCurrentTenantNoFallback();
		try
		{
			Registry.unsetCurrentTenant();

			dummy.getCounter(); // boom
		}
		finally
		{
			if (currentTenant != null)
			{
				Registry.setCurrentTenant(currentTenant);
			}
		}
	}

}
