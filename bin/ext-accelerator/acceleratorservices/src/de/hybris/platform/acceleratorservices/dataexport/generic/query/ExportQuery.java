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
package de.hybris.platform.acceleratorservices.dataexport.generic.query;


import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;
import de.hybris.platform.core.PK;

import java.util.List;

import org.springframework.integration.Message;


/**
 * ExportQuery interface. Used to search for Hybris types.
 */
public interface ExportQuery
{
	/**
	 * Search for a list of PK's to send to the next step in the chain
	 * 
	 * @param message
	 *           The Spring message in the chain
	 * @param exportDataEvent
	 *           The object that contains the type of event and all relevant data to search
	 * @return list of PK's
	 * @throws Throwable
	 */
	List<PK> search(Message<?> message, ExportDataEvent exportDataEvent) throws Throwable;
}
