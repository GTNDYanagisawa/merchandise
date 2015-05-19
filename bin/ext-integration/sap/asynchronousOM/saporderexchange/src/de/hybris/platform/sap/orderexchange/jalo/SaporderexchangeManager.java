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

package de.hybris.platform.sap.orderexchange.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.orderexchange.constants.SaporderexchangeConstants;

import org.apache.log4j.Logger;


@SuppressWarnings("javadoc")
public class SaporderexchangeManager extends GeneratedSaporderexchangeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SaporderexchangeManager.class.getName());

	public static final SaporderexchangeManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SaporderexchangeManager) em.getExtension(SaporderexchangeConstants.EXTENSIONNAME);
	}

}
