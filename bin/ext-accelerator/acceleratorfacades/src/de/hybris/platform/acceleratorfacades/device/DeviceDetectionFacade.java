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
package de.hybris.platform.acceleratorfacades.device;

import de.hybris.platform.acceleratorfacades.device.data.DeviceData;

import javax.servlet.http.HttpServletRequest;


/**
 * Facade that handles device detection
 */
public interface DeviceDetectionFacade
{
	/**
	 * Initialise the device detection for the specified request.
	 * 
	 * @param request
	 *           the request
	 */
	void initializeRequest(HttpServletRequest request);

	/**
	 * Get the Device that was detected for the current request. Must be called within a request context.
	 * 
	 * @return the detected device data
	 */
	DeviceData getCurrentDetectedDevice();
}
