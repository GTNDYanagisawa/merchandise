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
package de.hybris.platform.yb2bacceleratorcore.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.yb2bacceleratorcore.constants.YB2BAcceleratorCoreConstants;
import de.hybris.platform.yb2bacceleratorcore.setup.CoreSystemSetup;

import org.apache.log4j.Logger;


/**
 * Don't use. User {@link CoreSystemSetup} instead.
 */
@SuppressWarnings("PMD")
public class YB2BAcceleratorCoreManager extends GeneratedYB2BAcceleratorCoreManager
{
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(YB2BAcceleratorCoreManager.class.getName());

	public static final YB2BAcceleratorCoreManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YB2BAcceleratorCoreManager) em.getExtension(YB2BAcceleratorCoreConstants.EXTENSIONNAME);
	}
}
