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
package de.hybris.platform.integration.cis.tax.service;


import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;


/**
 * Service for calculating taxes using CIS tax service.
 */
public interface CisTaxCalculationService
{
	/**
	 * Calculate taxes for an order.
	 * 
	 * @param abstractOrder
	 *           order to calculate taxes for
	 * @return an ExternalTaxDocument that represents the taxes
	 */
	ExternalTaxDocument calculateExternalTaxes(final AbstractOrderModel abstractOrder);

}
