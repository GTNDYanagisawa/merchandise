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
package de.hybris.platform.commercesearch.jalo;

import de.hybris.platform.commercesearch.constants.CommercesearchsampledataConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class CommercesearchsampledataManager extends GeneratedCommercesearchsampledataManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( CommercesearchsampledataManager.class.getName() );
	
	public static final CommercesearchsampledataManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CommercesearchsampledataManager) em.getExtension(CommercesearchsampledataConstants.EXTENSIONNAME);
	}
	
}
