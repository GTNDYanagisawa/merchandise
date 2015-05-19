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

package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import java.util.Hashtable;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;


/**
 * Defines pricing and context relevant interactions with SSC configuration an pricing engine.
 */
public interface ConfigurationContextAndPricingWrapper
{
	/**
	 * Prepares pricing context.
	 * 
	 * @param session
	 *           SSC session
	 * @param configId
	 *           configuration Id
	 * @param kbKey
	 *           knowledge base data
	 */
	public void preparePricingContext(final IConfigSession session, final String configId, final KBKey kbKey)
			throws InteractivePricingException;

	/**
	 * Starts price calculation is configuration and pricing engine and put retrieved prices to the configuration model.
	 * 
	 * @param session
	 *           SSC session
	 * @param configId
	 *           configuration Id
	 * @param configModel
	 *           configuration model
	 */
	public void processPrice(final IConfigSession session, String configId, final ConfigModel configModel)
			throws InteractivePricingException;

	/**
	 * Retrieves configuration context.
	 * 
	 * @param kbKey
	 *           knowledge base data
	 * @return the configuration context
	 */
	public Hashtable<String, String> retrieveConfigurationContext(KBKey kbKey); // NOPMD
}
