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
package de.hybris.platform.acceleratorservices.dataexport.generic;

import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;

import org.springframework.integration.Message;


/**
 * ExportDataHistory Service. Records events during an export of data.
 */
public interface ExportDataHistoryService
{
	/**
	 * Record the beginning of an export.
	 * 
	 * @param payload
	 *           The object that contains the information required to export data
	 * @param timestamp
	 *           The time the export started
	 * @return {@link ExportDataEvent}
	 */
	ExportDataEvent recordExportStart(ExportDataEvent payload, Long timestamp);

	/**
	 * Recording the end of the generation of data.
	 * 
	 * @param message
	 * @param ede
	 *           Export data event that contains the cronjobs code
	 * @param count
	 *           The size of the list of generated items
	 */
	void recordExportEnd(Message<?> message, ExportDataEvent ede, Integer count);
}
