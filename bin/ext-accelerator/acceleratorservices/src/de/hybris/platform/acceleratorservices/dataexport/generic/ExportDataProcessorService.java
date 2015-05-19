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

import de.hybris.platform.acceleratorservices.dataexport.generic.config.PipelineConfig;
import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;

import org.springframework.integration.Message;


/**
 * ExportDataProcessorService. Main service that queries, looks up and converts data.
 */
public interface ExportDataProcessorService
{
	/**
	 * Lookup the bean with the name specified
	 * 
	 * @param beanName
	 *           the bean name
	 * @return the bean
	 */
	Object lookupBean(String beanName);

	/**
	 * Compute the filename of the generated file for the export event.
	 * 
	 * @param message
	 * @param pipelineConfig
	 *           the configuration used in this export
	 * @return the filename
	 */
	String computeFilename(Message<?> message, PipelineConfig pipelineConfig);

	/**
	 * Search for a list of items.
	 * 
	 * @param message
	 * @param pipelineConfig
	 *           object that holds the export configuration
	 * @return List of item PK's
	 * @throws Throwable
	 */
	List<PK> search(Message<ExportDataEvent> message, PipelineConfig pipelineConfig) throws Throwable;

	/**
	 * Get the Item model for the specified PK
	 * 
	 * @param pk
	 * @return Item model for the specified PK
	 */
	ItemModel lookupItemForPk(PK pk);

	/**
	 * Convert the Item model to a POJO
	 * 
	 * @param message
	 *           message that contains the Item model
	 * @param pipelineConfig
	 *           object that holds the export configuration. Should hold the converter
	 * @return the POJO representing the Item model
	 */
	Object convertItem(Message<? extends ItemModel> message, PipelineConfig pipelineConfig);

	/**
	 * Convert a POJO to an object that could be outputted to file.
	 * 
	 * @param records
	 *           List of POJO's
	 * @param pipelineConfig
	 *           pipelineConfig object that holds the export configuration. Should hold the output converter
	 * @return an object that can be sent to a writer
	 */
	Object convertOutput(List<Object> records, PipelineConfig pipelineConfig);
}
