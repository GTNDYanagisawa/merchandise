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
package de.hybris.platform.acceleratorstorefrontcommons.jalo;

import de.hybris.platform.acceleratorstorefrontcommons.constants.AcceleratorstorefrontcommonsConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class AcceleratorstorefrontcommonsManager extends GeneratedAcceleratorstorefrontcommonsManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( AcceleratorstorefrontcommonsManager.class.getName() );
	
	public static final AcceleratorstorefrontcommonsManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (AcceleratorstorefrontcommonsManager) em.getExtension(AcceleratorstorefrontcommonsConstants.EXTENSIONNAME);
	}
	
}
