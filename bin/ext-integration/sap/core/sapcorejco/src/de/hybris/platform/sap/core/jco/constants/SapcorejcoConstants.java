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
package de.hybris.platform.sap.core.jco.constants;




/**
 * Global class for all Sapcorejco constants. You can add global constants for your extension into this class.
 */
@SuppressWarnings("deprecation")
public final class SapcorejcoConstants extends GeneratedSapcorejcoConstants
{
	/**
	 * Name of the extension.
	 */
	public static final String EXTENSIONNAME = "sapcorejco";

	/**
	 * CRLF.
	 */
	public static final String CRLF = System.getProperty("line.separator");

	/**
	 * RFC destination name prefix.
	 */
	public static final String RFCDESTINATION_NAME_PREFIX = "custom_";

	/**
	 * JCo log sub directory.
	 */
	public static final String JCO_LOG_SUBDIR = "jco";

	/**
	 * JCo connection snapshot file name for single cluster.
	 */
	public static final String JCO_CONNECTION_CLUSTER_SNAPSHOT_FILE_NAME = "jcoConnectionsSnapshot";

	/**
	 * JCo connection snapshot file name for all clusters.
	 */
	public static final String JCO_CONNECTION_CLUSTERS_SNAPSHOT_FILE_NAME = "jcoConnectionsClusterSnapshot";

	/**
	 * XML file suffix.
	 */
	public static final String XML_FILE_SUFFIX = ".xml";

}
