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
package de.hybris.platform.sap.core.jco.rec.version000;

import de.hybris.platform.sap.core.jco.rec.version000.impl.JCoRecRepository;

import java.io.File;


/**
 * The interface for the JCoRecorder XML parser.
 */
public interface JCoRecXMLParser
{
	/**
	 * Parsing of a single funtion from a file.
	 * 
	 * @param repo
	 *           the repository that should contain the function afterwards.
	 * @param functionKey
	 *           of the function that should be parsed from the file.
	 * @param f
	 *           the file that coantins the function that should be parsed.
	 * @throws JCoRecXMLParserException
	 *            depends on the implementation.
	 */
	void parse(JCoRecRepository repo, String functionKey, File f) throws JCoRecXMLParserException;

	/**
	 * Parsing a whole file at once.
	 * 
	 * @param repo
	 *           the repository that should contain the data from the file afterwards.
	 * @param f
	 *           the file that coantins the data that should be parsed.
	 * @throws JCoRecXMLParserException
	 *            depends on the implementation.
	 */
	void parse(JCoRecRepository repo, File f) throws JCoRecXMLParserException;
}
