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

import de.hybris.platform.integration.cis.avs.constants.CisavsConstants;
import de.hybris.platform.integration.cis.avs.strategies.SuggestedAddressesAmountStrategy;
import de.hybris.platform.util.Config;


/**
 * Implementation of SuggestedAddressesAmountStrategy using a property.
 */
public class DefaultSuggestedAddressesAmountStrategy implements SuggestedAddressesAmountStrategy
{

	@Override
	public int getSuggestedAddressesAmountToDisplay()
	{
		return Config.getInt(CisavsConstants.AVS_SUGGESTED_ADDRESS_AMOUNT, 10);
	}
}
