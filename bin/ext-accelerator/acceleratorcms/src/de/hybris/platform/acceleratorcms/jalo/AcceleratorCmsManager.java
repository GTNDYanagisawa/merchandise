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
package de.hybris.platform.acceleratorcms.jalo;

import de.hybris.platform.acceleratorcms.constants.AcceleratorCmsConstants;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

import org.apache.log4j.Logger;


/**
 * Do not use. Please use {@link SystemSetup}.
 */
@SuppressWarnings("PMD")
public class AcceleratorCmsManager extends GeneratedAcceleratorCmsManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(AcceleratorCmsManager.class.getName());

	public static final AcceleratorCmsManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (AcceleratorCmsManager) em.getExtension(AcceleratorCmsConstants.EXTENSIONNAME);
	}

}
