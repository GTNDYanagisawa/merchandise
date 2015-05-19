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
package de.hybris.platform.sap.core.jco.connection;

import java.util.List;



/**
 * JCo Connection parameter interface.
 */
public interface JCoConnectionParameter
{

	/**
	 * Gets the Function Module Name.
	 * 
	 * @return Function Module Name
	 */
	public abstract String getFunctionModule();


	/**
	 * Gets the Cache Type.
	 * 
	 * @return Cache Type
	 */
	public abstract String getCacheType();

	/**
	 * Gets the Trace Before.
	 * 
	 * @return Trace Before
	 */
	public abstract boolean getTraceBefore();

	/**
	 * Gets the Trace After.
	 * 
	 * @return Trace After
	 */
	public abstract boolean getTraceAfter();

	/**
	 * Gets the Event Listener class name.
	 * 
	 * @return Event Listener class name
	 */
	public abstract JCoConnectionEventListener getEventListener();


	/**
	 * Returns the function module which should be replaced.
	 * 
	 * @return Replaced function module name
	 */
	public String getFunctionModuleToBeReplaced();

	/**
	 * Used as parameter to configure which import parameter should not be trace in function module trace.
	 * 
	 * @return excluded import parameters
	 */
	public String getTraceExcludeImportParameters();

	/**
	 * Used as parameter to configure which import parameter should not be trace in function module trace.
	 * 
	 * @return excluded export parameters
	 */
	public String getTraceExcludeExportParameters();

	/**
	 * Used as parameter to configure which import parameter should not be trace in function module trace.
	 * 
	 * @return excluded table parameters
	 */
	public String getTraceExcludeTableParameters();


	/**
	 * Used as parameter to configure which import parameter should not be trace in function module trace.
	 * 
	 * @return excluded import parameters as unmodifiable list
	 */
	public List<String> getTraceExcludeImportParametersList();

	/**
	 * Used as parameter to configure which import parameter should not be trace in function module trace.
	 * 
	 * @return excluded export parameters as unmodifiable list
	 */
	public List<String> getTraceExcludeExportParametersList();

	/**
	 * Used as parameter to configure which import parameter should not be trace in function module trace.
	 * 
	 * @return excluded table parameters as unmodifiable list
	 */
	public List<String> getTraceExcludeTableParametersList();

}
