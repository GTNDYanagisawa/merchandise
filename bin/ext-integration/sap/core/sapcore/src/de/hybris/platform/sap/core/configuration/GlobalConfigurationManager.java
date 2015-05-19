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
package de.hybris.platform.sap.core.configuration;

import java.util.Set;



/**
 * Interface to access the global configuration information.
 * <p>
 * It collects
 * <ul>
 * <li>all sap module ids which are defined in the extensions</li>
 * <li>all extensions names which are related to the module ids</li>
 * </ul>
 * The sap module ids are defined in the project.properties file of in the following format
 * {@code <extensionName>.sap.moduleId=<moduleId>}.
 * </p>
 */
public interface GlobalConfigurationManager
{

	/**
	 * Returns all registered module ids.
	 * 
	 * @return module id array
	 */
	public Set<String> getModuleIds();

	/**
	 * Returns the extension names for the requested module id.
	 * 
	 * @param moduleId
	 *           requested module id
	 * @return set of extension names
	 */
	public Set<String> getExtensionNames(String moduleId);

}
