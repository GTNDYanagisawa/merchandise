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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.servicelayer.i18n.I18NService;


public class DummyProvider implements ConfigurationProvider
{

	private I18NService i18NService;

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		return null;
	}

	@Override
	public boolean updateConfiguration(final ConfigModel model)
	{
		return true;
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId)
	{
		return null;
	}

	@Override
	public String retrieveExternalConfiguration(final String configId)
	{
		return null;
	}

	public I18NService getI18NService()
	{
		return i18NService;
	}

	public void setI18NService(final I18NService i18nService)
	{
		this.i18NService = i18nService;
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider#createConfigurationFromExternalSource
	 * (de.hybris.platform.sap.productconfig.runtime.interf.KBKey, java.lang.String)
	 */
	@Override
	public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider#releaseSession(java.lang.String)
	 */
	@Override
	public void releaseSession(final String configId)
	{
		//
	}

}
