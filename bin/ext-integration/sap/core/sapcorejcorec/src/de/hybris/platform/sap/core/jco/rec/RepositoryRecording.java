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

import com.sap.conn.jco.JCoFunction;


/**
 * Interface for the RECORDING mode of the JCoRecorder.<br/>
 * There should be an implementation for every version of the repository-file.<br/>
 * For instance:
 * <ul>
 * <li>JCoRecRepository for originally version of the repository-file.</li>
 * <li>RepositoryRecording100 for the first revised repository-file version.</li>
 * </ul>
 * 
 */
public interface RepositoryRecording
{

	/**
	 * While recording, this method helps to add a (new) function to the Repository.
	 * 
	 * @param function
	 *           the JCoFunction that should be added.
	 * @return Returns a number that indicates the position in the requesting/execution-order.
	 */
	public int putFunction(JCoFunction function);

	/**
	 * Saves the collected data in a file on disk.
	 * 
	 * @throws JCoRecException
	 *            if an error (e.g. I/O exception) occurs during saving.
	 */
	public void writeRepositoryToFile() throws JCoRecException;
}
