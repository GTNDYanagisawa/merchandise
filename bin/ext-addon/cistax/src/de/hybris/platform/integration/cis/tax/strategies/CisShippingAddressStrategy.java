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
package de.hybris.platform.integration.cis.tax.strategies;

import com.hybris.cis.api.model.CisAddress;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import java.util.List;


/**
 * Interface to determine the correct ship to and ship from addresses for taxation.
 */
public interface CisShippingAddressStrategy
{
	List<CisAddress> getAddresses(AbstractOrderModel abstractOrder);
}
