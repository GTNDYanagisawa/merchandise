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
package de.hybris.platform.sap.core.jco.rec;

/**
 * This enum represents the possible states for the JCoRecorder mode.
 */
public enum JCoRecMode
{
	/**
	 * The JCoRecorder redirects the calls to the backend.
	 */
	OFF,

	/**
	 * The JcoRecorder redirects the calls to the backend and writes the data of the backend-calls to a repository file.
	 */
	RECORDING,

	/**
	 * The JCoRecorder intersects the calls to the backend and will rather look up the data from the parsed repository
	 * file.
	 */
	PLAYBACK
}
