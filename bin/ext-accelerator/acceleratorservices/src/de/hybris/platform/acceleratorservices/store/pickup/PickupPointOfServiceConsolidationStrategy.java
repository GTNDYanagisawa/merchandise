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
package de.hybris.platform.acceleratorservices.store.pickup;

import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;


/**
 * Service that attempts to return a number of PointsOfService that could be used to consolidate pickup
 */
public interface PickupPointOfServiceConsolidationStrategy
{
	List<PointOfServiceDistanceData> getConsolidationOptions(CartModel cart);
}
