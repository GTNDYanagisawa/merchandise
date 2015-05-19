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
package de.hybris.platform.sap.core.configuration.rfc;





/**
 * Interface to access SAP RFC destination data.
 */
public interface RFCDestinationService
{

	/**
	 * Returns the SAP RFC Destination for the given name.
	 * 
	 * @param destinationName
	 *           RFC Destination name
	 * @return list
	 */
	public RFCDestination getRFCDestination(String destinationName);

}
