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
package de.hybris.platform.acceleratorservices.jalo;

import de.hybris.platform.acceleratorservices.constants.AcceleratorServicesConstants;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;


/**
 * This is the extension manager of the AcceleratorServices extension. Use {@link SystemSetup} instead.
 */
public class AcceleratorServicesManager extends GeneratedAcceleratorServicesManager
{
	/**
	 * Get the valid instance of this manager.
	 * 
	 * @return the current instance of this manager
	 */
	public static AcceleratorServicesManager getInstance()
	{
		final ExtensionManager extensionManager = JaloSession.getCurrentSession().getExtensionManager();
		return (AcceleratorServicesManager) extensionManager.getExtension(AcceleratorServicesConstants.EXTENSIONNAME);
	}
}
