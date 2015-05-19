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
package de.hybris.platform.integration.cis.sampledata.jalo;

import de.hybris.platform.integration.cis.sampledata.constants.CissampledataConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class CissampledataManager extends GeneratedCissampledataManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( CissampledataManager.class.getName() );
	
	public static final CissampledataManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CissampledataManager) em.getExtension(CissampledataConstants.EXTENSIONNAME);
	}
	
}
