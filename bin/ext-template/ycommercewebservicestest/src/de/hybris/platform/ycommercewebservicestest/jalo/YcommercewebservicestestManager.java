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
 */
package de.hybris.platform.ycommercewebservicestest.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.ycommercewebservicestest.constants.YcommercewebservicestestConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class YcommercewebservicestestManager extends GeneratedYcommercewebservicestestManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( YcommercewebservicestestManager.class.getName() );
	
	public static final YcommercewebservicestestManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YcommercewebservicestestManager) em.getExtension(YcommercewebservicestestConstants.EXTENSIONNAME);
	}
	
}
