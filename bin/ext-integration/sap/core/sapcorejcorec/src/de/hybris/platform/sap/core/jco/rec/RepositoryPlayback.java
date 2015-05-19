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
import com.sap.conn.jco.JCoRecord;


/**
 * Interface for the PLAYBACK mode of the JCoRecorder.<br/>
 * There should be an implementation for every version of the repository-file.<br/>
 * For instance:
 * <ul>
 * <li>JCoRecRepository for originally version of the repository-file.</li>
 * <li>RepositoryPlayback100 for the first revised repository-file version.</li>
 * </ul>
 * 
 */
public interface RepositoryPlayback
{
	/**
	 * Retrieves a JCoFunction from the recorded back-end data.
	 * 
	 * @param functionName
	 *           the name of the JoFunction.
	 * @return Returns the requested JCoFunction.
	 * @throws JCoRecException
	 *            if there is no function with the given name in the recorded data.
	 */
	public JCoFunction getFunction(String functionName) throws JCoRecException;

	/**
	 * Retrieves a JCoRecord from the recorded/written repository-file.
	 * 
	 * @param recordName
	 *           the name of the JCoRecord.
	 * @return Returns the requested JCoRecord.
	 * @throws JCoRecException
	 *            if there is no function with the given name in the recorded/written data.
	 */
	public JCoRecord getRecord(String recordName) throws JCoRecException;
}
