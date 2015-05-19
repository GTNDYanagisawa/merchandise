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
package de.hybris.platform.sap.sapcommonbol.common.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMapping;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMappingKey;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMapping;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMappingKey;


/**
 * Provides common configuration settings of the application like e.g. the sales area. Only those attributes should be
 * here which are relevant for many modules.
 * 
 */
public interface CommonConfiguration extends BusinessObject
{

	/**
	 * @return the distribution channel
	 */
	public String getDistributionChannel();

	/**
	 * Sets the distribution channel
	 * 
	 * @param arg
	 *           distribution channel
	 */
	public void setDistributionChannel(String arg);

	/**
	 * Returns the division
	 * 
	 * @return division
	 */
	public String getDivision();

	/**
	 * Sets the division
	 * 
	 * @param arg
	 *           division
	 */
	public void setDivision(String arg);

	/**
	 * Returns the sales organisation
	 * 
	 * @return salesOrganisation
	 */
	public String getSalesOrganisation();

	/**
	 * Sets the sales organisation
	 * 
	 * @param arg
	 *           sales organisation
	 */
	public void setSalesOrganisation(String arg);

	/**
	 * Returns the currency
	 * 
	 * @return currency
	 */
	public String getCurrency();

	/**
	 * Sets the currency
	 * 
	 * @param currency
	 */
	public void setCurrency(String currency);

	/**
	 * Fetching WEC debug attribute. Note that this is not read from a configuration but set into this bean at runtime,
	 * depending on the context. <br>
	 * Available e.g. in sales context. <br>
	 * In the UI layer, one should derive this setting from the runtime instead.
	 * 
	 * @return WEC debug attribute
	 */
	public boolean getWecDebug();

	/**
	 * Sets the WEC debug parameter from the UI layer. *
	 * 
	 * @param wecDebug
	 */
	public void setWecDebug(boolean wecDebug);

	/**
	 * Gets the back end mapping for the sales org and division.<br>
	 * 
	 * @param key
	 *           the includes the sales org and division
	 * @return the mapping data, alternative division for condition, customer, document type
	 */
	public DivisionMapping getDivisionMapping(DivisionMappingKey key);

	/**
	 * Gets the back end mapping for the sales org and distribution channel.<br>
	 * 
	 * @param key
	 *           the includes the sales org and distribution channel
	 * @return the mapping data, alternative division for condition, customer/material, document type
	 */
	public DistChannelMapping getDistChannelMapping(DistChannelMappingKey key);

	/**
	 * Factory-method to create a mapping key.<br>
	 * 
	 * @param originalSalesOrg
	 *           sales organisation
	 * @param originalDistChannel
	 *           distribution channel
	 * @return key for distribution channel mapping
	 */
	public DistChannelMappingKey getDistChannelMappingKey(String originalSalesOrg, String originalDistChannel);

	/**
	 * Factory-method to create a mapping key.<br>
	 * 
	 * @param originalSalesOrg
	 *           sales organisation
	 * @param originalDivison
	 *           division
	 * @return key for division mapping
	 */
	public DivisionMappingKey getDivisionMappingKey(String originalSalesOrg, String originalDivison);
}
