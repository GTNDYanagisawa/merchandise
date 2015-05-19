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
package de.hybris.platform.integration.ondemand.sampledata.jalo;

import de.hybris.platform.integration.ondemand.sampledata.constants.OndemandsampledataConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class OndemandsampledataManager extends GeneratedOndemandsampledataManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( OndemandsampledataManager.class.getName() );
	
	public static final OndemandsampledataManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (OndemandsampledataManager) em.getExtension(OndemandsampledataConstants.EXTENSIONNAME);
	}
	
}
