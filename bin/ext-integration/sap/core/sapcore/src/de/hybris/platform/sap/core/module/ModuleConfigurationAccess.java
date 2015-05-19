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
package de.hybris.platform.sap.core.module;

import de.hybris.platform.sap.core.configuration.SAPConfigurationService;


/**
 * Interface to access runtime module configuration data.
 */
public interface ModuleConfigurationAccess extends SAPConfigurationService
{

	/**
	 * Returns the module id of the module configuration access.
	 * 
	 * @return Module id
	 */
	public String getModuleId();

}
