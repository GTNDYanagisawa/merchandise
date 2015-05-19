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
package de.hybris.platform.sap.core.bol.logging;

/**
 * Provide the constants for the supported log categories.
 */
public interface LogCategories
{

	// Application categories

	/**
	 * Please use for application issues.
	 */
	public static LogCategory APPLICATIONS = new LogCategory("APPLICATIONS");
	/**
	 * Please use for application backup issues.
	 */
	public static LogCategory APPS_COMMON_BACKUP = new LogCategory("APPS_COMMON_BACKUP");

	/**
	 * Please use for application archiving issues.
	 */
	public static LogCategory APPS_COMMON_ARCHIVING = new LogCategory("APPS_COMMON_ARCHIVING");

	/**
	 * Please use for application resource issues.
	 */
	public static LogCategory APPS_COMMON_RESOURCES = new LogCategory("APPS_COMMON_RESOURCES");

	/**
	 * Please use for application security issues.
	 */
	public static LogCategory APPS_COMMON_SECURITY = new LogCategory(".APPS_COMMON_SECURITY");

	/**
	 * Please use for application fail over issues.
	 */
	public static LogCategory APPS_COMMON_FAILOVER = new LogCategory("APPS_COMMON_FAILOVER");

	/**
	 * Please use for application infrastructure issues.
	 */
	public static LogCategory APPS_COMMON_INFRASTRUCTURE = new LogCategory("APPS_COMMON_INFRASTRUCTURE");

	/**
	 * Please use for application configuration issues.
	 */
	public static LogCategory APPS_COMMON_CONFIGURATION = new LogCategory("APPS_COMMON_CONFIGURATION");

	/**
	 * Please use for application performance issues.
	 */
	public static LogCategory APPS_PERFORMANCE = new LogCategory("APPS_PERFORMANCE");

	/**
	 * Please use for application UI issues.
	 */
	public static LogCategory APPS_USER_INTERFACE = new LogCategory("APPS_USER_INTERFACE");

	/**
	 * Please use for application business logic issues.
	 */
	public static LogCategory APPS_BUSINESS_LOGIC = new LogCategory("APPS_BUSINESS_LOGIC");

	/**
	 * Please use for application ui controller issues.
	 */
	public static LogCategory APPS_USER_INTERFACE_CONTROLLER = new LogCategory("APPS_USER_INTERFACE_CONTROLLER");

	// Framework categories

	/**
	 * Please use for common framework issues.
	 */
	public static LogCategory APPS_COMMON_CORE = new LogCategory("APPS_COMMON_CORE");

	/**
	 * Please use for BOL framework issues.
	 */
	public static LogCategory APPS_COMMON_WCF_BOL = new LogCategory("APPS_COMMON_CORE_BOL");

	/**
	 * Please use for configuration framework issues.
	 */
	public static LogCategory APPS_COMMON_CORE_CONFIGURATION = new LogCategory("APPS_COMMON_CORE_CONFIGURATION");

}
