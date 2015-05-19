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
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;


/**
 * Retrieves hybris data relevant for the configuration and pricing engine.
 */
public interface PricingConfigurationParameter
{
	/**
	 * Retrieves the value of the flag for activating of pricing on the product configuration page. If inactive, no
	 * pricing information is shown.
	 * 
	 * @return the value of the flag for activating of pricing
	 */
	public boolean isPricingSupported();

	/**
	 * Retrieves the pricing procedure used for pricing.
	 * 
	 * @return the pricing procedure
	 */
	public String getPricingProcedure();

	/**
	 * Retrieves the target for the base price. This is the purpose assigned to the condition function relevant for
	 * determining the base price of configurable products
	 * 
	 * @return the target for the base price
	 */
	public String getTargetForBasePrice();

	/**
	 * Retrieves the target for the option price. This is the purpose assigned to the condition function relevant for
	 * determining the total of the price-relevant options selected.
	 * 
	 * @return the target for the option price
	 */
	public String getTargetForSelectedOptions();

	/**
	 * Retrieves the sales organization.
	 * 
	 * @return the sales organization
	 */
	public String getSalesOrganization();

	/**
	 * Retrieves the distribution channel used for condition determination.
	 * 
	 * @return the the distribution channel
	 */
	public String getDistributionChannelForConditions();

	/**
	 * Retrieves the division used for condition determination.
	 * 
	 * @return the the division
	 */
	public String getDivisionForConditions();

	/**
	 * Retrieves the SAP currency code for a given <code>CurrencyModel</code>.
	 * 
	 * @param currencyModel
	 *           currency model
	 * @return the SAP currency code
	 */
	public String retrieveCurrencySapCode(CurrencyModel currencyModel);

	/**
	 * Retrieves the SAP unit of measure code for a given <code>UnitModel</code>.
	 * 
	 * @param unitModel
	 *           unit model
	 * @return the SAP unit of measure code
	 */
	public String retrieveUnitSapCode(UnitModel unitModel);

}
