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
package de.hybris.platform.sap.ysapordermgmtb2baddon.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class ysapordermgmtb2baddonManager extends Generatedysapordermgmtb2baddonManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( ysapordermgmtb2baddonManager.class.getName() );
	
	public static final ysapordermgmtb2baddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ysapordermgmtb2baddonManager) em.getExtension(de.hybris.platform.sap.ysapordermgmtb2baddon.constants.ysapordermgmtb2baddonConstants.EXTENSIONNAME);
	}
	
}
