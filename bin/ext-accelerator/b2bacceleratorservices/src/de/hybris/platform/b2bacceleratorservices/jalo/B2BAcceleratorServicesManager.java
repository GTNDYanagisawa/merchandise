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
package de.hybris.platform.b2bacceleratorservices.jalo;

import de.hybris.platform.b2bacceleratorservices.constants.B2BAcceleratorServicesConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;


/**
 * This is the extension manager of the B2BAcceleratorServices extension.
 */
public class B2BAcceleratorServicesManager extends GeneratedB2BAcceleratorServicesManager
{
	/**
	 * Get the valid instance of this manager.
	 * 
	 * @return the current instance of this manager
	 */
	public static B2BAcceleratorServicesManager getInstance()
	{
		final ExtensionManager extensionManager = JaloSession.getCurrentSession().getExtensionManager();
		return (B2BAcceleratorServicesManager) extensionManager.getExtension(B2BAcceleratorServicesConstants.EXTENSIONNAME);
	}
}
