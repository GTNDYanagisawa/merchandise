/*
 *  
 * [y] hybris Platform
 *  
 * Copyright (c) 2000-2011 hybris AG
 * All rights reserved.
 *  
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *  
 */
package de.hybris.platform.chinaaccelerator.services.jalo;

//import de.hybris.platform.chinaaccelerator.jalo.GeneratedChinaacceleratorServicesManager;
import de.hybris.platform.chinaaccelerator.services.constants.ChinaacceleratorServicesConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

import org.apache.log4j.Logger;


@SuppressWarnings("PMD")
public class ChinaacceleratorServicesManager extends GeneratedChinaacceleratorServicesManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ChinaacceleratorServicesManager.class.getName());

	public static final ChinaacceleratorServicesManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ChinaacceleratorServicesManager) em.getExtension(ChinaacceleratorServicesConstants.EXTENSIONNAME);
	}

}
