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
package de.hybris.platform.acceleratorservices.dataimport.batch.converter;

import java.util.Map;


/**
 * Interface for converting a CSV file into impex.
 */
public interface ImpexConverter
{
	/**
	 * Retrieves the impex header.
	 * 
	 * @return impex import header
	 */
	String getHeader();

	/**
	 * Converts a CSV row to impex.
	 * 
	 * @param row
	 *           a CSV row containing column indexes and values
	 * @param sequenceId
	 * @return a converted impex line
	 */
	String convert(Map<Integer, String> row, Long sequenceId);

	/**
	 * Evaluate a single row and return a false, if the row should be filtered.
	 * 
	 * @param row
	 * @return false, if the row should not be converted
	 */
	boolean filter(Map<Integer, String> row);

	/**
	 * Returns the type, if defined.
	 * 
	 * @return type
	 */
	String getType();

}
