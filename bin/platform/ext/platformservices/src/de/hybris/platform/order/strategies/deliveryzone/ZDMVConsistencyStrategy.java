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
package de.hybris.platform.order.strategies.deliveryzone;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;

import java.util.Map;
import java.util.Set;


/**
 * Strategy for consistency check for {@link ZoneDeliveryModeModel}.
 * 
 * @spring.bean zdmvConsistencyStrategy
 */
public interface ZDMVConsistencyStrategy
{

	/**
	 * Gets all {@link CountryModel}s and its corresponding {@link ZoneModel}s when the {@link CountryModel} belongs to
	 * more than one {@link ZoneModel}.
	 * 
	 * @param zones
	 *           the zones
	 * @return a <code>Map</code> which contains {@link CountryModel} and its {@link ZoneModel}s, when the
	 *         {@link CountryModel} belongs to more than one {@link ZoneModel}. <b>Collections.EMPTY_MAP</b> otherwise.
	 */
	Map<CountryModel, Set<ZoneModel>> getAmbiguousCountriesForZones(final Set<ZoneModel> zones);

}
