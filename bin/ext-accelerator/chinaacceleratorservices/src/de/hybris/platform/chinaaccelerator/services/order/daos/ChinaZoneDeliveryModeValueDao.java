/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-$year hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package de.hybris.platform.chinaaccelerator.services.order.daos;


import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.order.daos.ZoneDeliveryModeValueDao;

import java.util.Collection;


public interface ChinaZoneDeliveryModeValueDao extends ZoneDeliveryModeValueDao
{
	/**
	 * Finds all delivery cost values for the specific district. If nothing found, it will try to look up for the city's,
	 * then the region's (province), then the country's.
	 * 
	 * @param mode
	 *           the zone delivery mode
	 * @param district
	 *           the district
	 * @return all found {@link de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel}s, or empty list if not
	 *         found.
	 */
	Collection<ZoneDeliveryModeValueModel> findDeliveryValues(DeliveryModeModel mode, DistrictModel district);
}
