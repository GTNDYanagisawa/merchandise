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
package sap.hybris.integration.models.services;

/**
 *	Accessing sales area data for SAP back end communication
 */
public interface SalesAreaService {
	
	/**
	 * @return Current sales organization. 
	 * No bundling available, it is the same one for order creation or master data maintenance.
	 */
	public String getSalesOrganization();
	
	/**
	 * @return Current distribution channel for order creation. The channel maintained in SAP Configuration
	 */
	public String getDistributionChannel();
	
	/**
	 * @return Current channel for condition maintenance
	 */
	public String getDistributionChannelForConditions();
	
	/**
	 * @return Current channel for customer or material master access.
	 */
	public String getDistributionChannelForCustomerMaterial();
	
	/**
	 * @return Current division for order creation. The division maintained in SAP Configuration
	 */
	public String getDivision();
	
	/**
	 * @return Current division for condition maintenance
	 */
	public String getDivisionForConditions();
	
	/**
	 * @return Current division for customer or material master access
	 */
	public String getDivisionForCustomerMaterial();

}
