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
package de.hybris.platform.integration.cis.avs.impl;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.integration.cis.avs.constants.CisavsConstants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Config;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultShowSuggestedAddressesStrategyTest extends ServicelayerTest
{
	private DefaultShowSuggestedAddressesStrategy defaultShowSuggestedAddressesStrategy = new DefaultShowSuggestedAddressesStrategy();

	@Test
	public void shouldReturnTrue()
	{
		Config.setParameter(CisavsConstants.AVS_SHOW_SUGGESTED_ADDRESSES_PROP, "true");
		Assert.assertTrue(defaultShowSuggestedAddressesStrategy.shouldAddressSuggestionsBeShown());
	}

	@Test
	public void shouldReturnFalse()
	{
		Config.setParameter(CisavsConstants.AVS_SHOW_SUGGESTED_ADDRESSES_PROP, "false");
		Assert.assertFalse(defaultShowSuggestedAddressesStrategy.shouldAddressSuggestionsBeShown());
	}
}
