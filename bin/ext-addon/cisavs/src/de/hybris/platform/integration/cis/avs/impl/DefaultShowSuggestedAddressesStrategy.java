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
import de.hybris.platform.integration.cis.avs.strategies.ShowSuggestedAddressesStrategy;
import de.hybris.platform.util.Config;


/**
 * Implementation of ShowSuggestedAddressesStrategy using a property.
 */
public class DefaultShowSuggestedAddressesStrategy implements ShowSuggestedAddressesStrategy
{
	@Override
	public boolean shouldAddressSuggestionsBeShown()
	{
		return "true".equals(Config.getParameter(CisavsConstants.AVS_SHOW_SUGGESTED_ADDRESSES_PROP));
	}
}
