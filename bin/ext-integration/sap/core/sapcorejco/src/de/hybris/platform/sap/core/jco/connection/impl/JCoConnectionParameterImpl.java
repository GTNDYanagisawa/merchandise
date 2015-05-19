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
package de.hybris.platform.sap.core.jco.connection.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.sap.core.jco.connection.JCoConnectionEventListener;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameter;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameters;



/**
 * JCo Connection parameter.
 */
public class JCoConnectionParameterImpl implements JCoConnectionParameter
{

	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(JCoConnectionParameterImpl.class.getName());

	/**
	 * Parent parameter map.
	 */
	private JCoConnectionParameters connectionParameters = null;

	/**
	 * Name of the function module which is configured by this parameter.
	 */
	private String functionModule = null;
	/**
	 * If cacheType is set to {@link JCoManagedFunctionCache.DEFAULT_CACHE_TYPE} cache is activated for this function
	 * module configuration.
	 */
	private String cacheType = null;
	/**
	 * Event listener.
	 */
	private JCoConnectionEventListener eventListener = null;
	/**
	 * Trace debug information before function call.
	 */
	private boolean traceBefore = false;
	/**
	 * Trace debug information after function call.
	 */
	private boolean traceAfter = false;
	/**
	 * Replaces function module name.
	 */
	private String functionModuleToBeReplaced = null;
	/**
	 * Comma separated list of import parameters which should be excluded from the trace.
	 */
	private String traceExcludeImportParameters = null;
	/**
	 * Comma separated list of export parameters which should be excluded from the trace.
	 */
	private String traceExcludeExportParameters = null;
	/**
	 * Comma separated list of table parameters which should be excluded from the trace.
	 */
	private String traceExcludeTableParameters = null;
	/**
	 * List value of traceExcludeImportParameters.
	 */
	private List<String> traceExcludeImportParametersList = null;
	/**
	 * List value of traceExcludeExportParameters.
	 */
	private List<String> traceExcludeExportParametersList = null;
	/**
	 * List value of traceExcludeTableParameters.
	 */
	private List<String> traceExcludeTableParametersList = null;



	/**
	 * Constructor.
	 */
	public JCoConnectionParameterImpl()
	{
		super();
	}

	/**
	 * Setter for connectionParameters.
	 * 
	 * @param connectionParameters
	 *           connectionParameters
	 */
	@Required
	public void setConnectionParameters(final JCoConnectionParameters connectionParameters)
	{
		this.connectionParameters = connectionParameters;
	}



	/**
	 * Getter for connectionParameters.
	 * 
	 * @return connectionParameters
	 */
	public JCoConnectionParameters getConnectionParameters()
	{
		return connectionParameters;
	}


	@Override
	public String getFunctionModule()
	{
		return functionModule;
	}


	@Override
	public String getCacheType()
	{

		return cacheType;
	}

	/**
	 * Sets the Cache Type.
	 * 
	 * @param cacheType
	 *           Cache Type to set
	 */
	public void setCacheType(final String cacheType)
	{
		this.cacheType = cacheType;
	}


	@Override
	public boolean getTraceBefore()
	{

		return traceBefore;
	}

	/**
	 * Sets the Trace Before.
	 * 
	 * @param traceBefore
	 *           Trace Before to set
	 */
	public void setTraceBefore(final boolean traceBefore)
	{
		this.traceBefore = traceBefore;
	}

	@Override
	public boolean getTraceAfter()
	{

		return traceAfter;
	}

	/**
	 * Sets the Trace After.
	 * 
	 * @param traceAfter
	 *           Trace After to set
	 */
	public void setTraceAfter(final boolean traceAfter)
	{
		this.traceAfter = traceAfter;
	}

	@Override
	public JCoConnectionEventListener getEventListener()
	{

		return eventListener;
	}

	/**
	 * Sets the Event Listener class name.
	 * 
	 * @param eventListener
	 *           Event Listener class name to set
	 */
	public void setEventListener(final JCoConnectionEventListener eventListener)
	{
		this.eventListener = eventListener;
	}


	@Override
	public String getFunctionModuleToBeReplaced()
	{
		return functionModuleToBeReplaced;
	}

	@Override
	public String getTraceExcludeImportParameters()
	{
		return traceExcludeImportParameters;
	}

	@Override
	public String getTraceExcludeExportParameters()
	{
		return traceExcludeExportParameters;
	}

	@Override
	public String getTraceExcludeTableParameters()
	{
		return traceExcludeTableParameters;
	}

	@Override
	public List<String> getTraceExcludeExportParametersList()
	{
		return traceExcludeExportParametersList;
	}

	@Override
	public List<String> getTraceExcludeTableParametersList()
	{
		return traceExcludeTableParametersList;
	}


	@Override
	public List<String> getTraceExcludeImportParametersList()
	{
		return traceExcludeImportParametersList;
	}

	/**
	 * Splits tokens form a comma separated string and put theim into a list.
	 * 
	 * @param commaSeparatedString
	 *           comma separated string
	 * @return List with values.
	 */
	protected List<String> createListFromCommaSeparatedValue(final String commaSeparatedString)
	{
		if (commaSeparatedString == null || commaSeparatedString.isEmpty())
		{
			return Collections.unmodifiableList(new ArrayList<String>(0));
		}
		return Collections.unmodifiableList(Arrays.asList(commaSeparatedString.split(",")));
	}



	/**
	 * Setter for functionModule.
	 * 
	 * @param functionModule
	 *           functionModule
	 */
	@Required
	public void setFunctionModule(final String functionModule)
	{
		this.functionModule = functionModule;
	}


	/**
	 * Setter for functionModuleToBeReplaced.
	 * 
	 * @param functionModuleToBeReplaced
	 *           functionModuleToBeReplaced
	 */
	public void setFunctionModuleToBeReplaced(final String functionModuleToBeReplaced)
	{
		this.functionModuleToBeReplaced = functionModuleToBeReplaced;
	}


	/**
	 * Setter for traceExcludeImportParameters.
	 * 
	 * @param traceExcludeImportParameters
	 *           traceExcludeImportParameters
	 */
	public void setTraceExcludeImportParameters(final String traceExcludeImportParameters)
	{
		this.traceExcludeImportParameters = traceExcludeImportParameters;
	}


	/**
	 * Setter for traceExcludeExportParameters.
	 * 
	 * @param traceExcludeExportParameters
	 *           traceExcludeExportParameters
	 */
	public void setTraceExcludeExportParameters(final String traceExcludeExportParameters)
	{
		this.traceExcludeExportParameters = traceExcludeExportParameters;
	}


	/**
	 * Setter for traceExcludeTableParameters.
	 * 
	 * @param traceExcludeTableParameters
	 *           traceExcludeTableParameters
	 */
	public void setTraceExcludeTableParameters(final String traceExcludeTableParameters)
	{
		this.traceExcludeTableParameters = traceExcludeTableParameters;
	}


	/**
	 * Setter for traceExcludeImportParametersList.
	 * 
	 * @param traceExcludeImportParametersList
	 *           traceExcludeImportParametersList
	 */
	public void setTraceExcludeImportParametersList(final List<String> traceExcludeImportParametersList)
	{
		this.traceExcludeImportParametersList = traceExcludeImportParametersList;
	}


	/**
	 * Setter for traceExcludeExportParametersList.
	 * 
	 * @param traceExcludeExportParametersList
	 *           traceExcludeExportParametersList
	 */
	public void setTraceExcludeExportParametersList(final List<String> traceExcludeExportParametersList)
	{
		this.traceExcludeExportParametersList = traceExcludeExportParametersList;
	}


	/**
	 * Setter for traceExcludeTableParametersList.
	 * 
	 * @param traceExcludeTableParametersList
	 *           traceExcludeTableParametersList
	 */
	public void setTraceExcludeTableParametersList(final List<String> traceExcludeTableParametersList)
	{
		this.traceExcludeTableParametersList = traceExcludeTableParametersList;
	}


	@Override
	public String toString()
	{
		return "JCoConnectionParameterImpl [functionModule=" + functionModule + ", cacheType=" + cacheType + ", eventListener="
				+ eventListener + ", traceBefore=" + traceBefore + ", traceAfter=" + traceAfter + ", functionModuleToBeReplaced="
				+ functionModuleToBeReplaced + ", traceExcludeImportParameters=" + traceExcludeImportParameters
				+ ", traceExcludeExportParameters=" + traceExcludeExportParameters + ", traceExcludeTableParameters="
				+ traceExcludeTableParameters + ", traceExcludeImportParametersList=" + traceExcludeImportParametersList
				+ ", traceExcludeExportParametersList=" + traceExcludeExportParametersList + ", traceExcludeTableParametersList="
				+ traceExcludeTableParametersList + "]";
	}

	/**
	 * Init method. Adds itself to the parent map.
	 */
	public void init()
	{
		this.traceExcludeImportParametersList = createListFromCommaSeparatedValue(traceExcludeImportParameters);
		this.traceExcludeExportParametersList = createListFromCommaSeparatedValue(traceExcludeExportParameters);
		this.traceExcludeTableParametersList = createListFromCommaSeparatedValue(traceExcludeTableParameters);
		this.connectionParameters.addConnectionParameter(this);
	}

}
