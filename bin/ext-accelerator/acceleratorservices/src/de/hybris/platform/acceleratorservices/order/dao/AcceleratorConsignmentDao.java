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
package de.hybris.platform.acceleratorservices.order.dao;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.Collection;
import java.util.List;

/**
 * Data access to {@link de.hybris.platform.ordersplitting.model.ConsignmentModel}
 */
public interface AcceleratorConsignmentDao
{

    /**
     * Find consignments for given statuses
     *
     * @param statuses the consignment's current statuses
     * @param sites the sites to look consignment for
     * @return the list fo consignments
     */
    List<ConsignmentModel> findConsignmentsForStatus(List<ConsignmentStatus> statuses, Collection<BaseSiteModel> sites);
}
