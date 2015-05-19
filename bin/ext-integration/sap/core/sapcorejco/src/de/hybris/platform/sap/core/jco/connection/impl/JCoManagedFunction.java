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

import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameter;

import com.sap.conn.jco.AbapClassException.Mode;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoParameterList;



/**
 * Delegator for JCoFunction. <br>
 * Adds connection parameter.
 */
@SuppressWarnings("serial")
public class JCoManagedFunction implements JCoFunction
{
	/**
	 * Delegator instance.
	 */
	protected JCoFunction delegate; //NOPMD
	/**
	 * Connection parameters.
	 */
	protected JCoConnectionParameter connectionParameter; //NOPMD

	/**
	 * Constructor.
	 * 
	 * @param delegate
	 *           delegate instance
	 * @param connectionParameter
	 *           connection parameters
	 */
	public JCoManagedFunction(final JCoFunction delegate, final JCoConnectionParameter connectionParameter)
	{
		super();
		this.delegate = delegate;
		this.connectionParameter = connectionParameter;
	}

	/**
	 * Checks if connection parameters are available.
	 * 
	 * @return true if connection parameter are available.
	 */
	public boolean hasConnectionParameters()
	{
		return !(this.connectionParameter == null);
	}

	/**
	 * Getter for connection parameter.
	 * 
	 * @return connection parameter or null
	 */
	public JCoConnectionParameter getConnectionParameter()
	{
		return connectionParameter;
	}

	/**
	 * Setter for connection parameter.
	 * 
	 * @param connectionParameter
	 *           connection parameter
	 */
	public void setConnectionParameter(final JCoConnectionParameter connectionParameter)
	{
		this.connectionParameter = connectionParameter;
	}


	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#execute(com.sap.conn.jco.JCoDestination, java.lang.String, java.lang.String)
	 * 
	 * @param destination
	 *           destination
	 * @param tid
	 *           tid
	 * @param queueName
	 *           queueName
	 * @throws JCoException
	 *            JCoException
	 */
	public void execute(final JCoDestination destination, final String tid, final String queueName) throws JCoException
	{
		delegate.execute(destination, tid, queueName);
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#execute(com.sap.conn.jco.JCoDestination, java.lang.String)
	 * @param destination
	 *           destination
	 * @param tid
	 *           tid
	 * @throws JCoException
	 *            JCoException
	 * 
	 */
	public void execute(final JCoDestination destination, final String tid) throws JCoException
	{
		delegate.execute(destination, tid);
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#execute(com.sap.conn.jco.JCoDestination)
	 * @param destination
	 *           destination
	 * @throws JCoException
	 *            JCoException
	 * 
	 */
	public void execute(final JCoDestination destination) throws JCoException
	{
		delegate.execute(destination);
	}


	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#getChangingParameterList()
	 * @return JCoParameterList
	 */
	public JCoParameterList getChangingParameterList()
	{
		return delegate.getChangingParameterList();
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#getException(java.lang.String)
	 * @param key
	 *           key
	 * @return AbapException
	 */
	public AbapException getException(final String key)
	{
		return delegate.getException(key);
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#getExceptionList()
	 * @return AbapException[]
	 */
	public AbapException[] getExceptionList()
	{
		return delegate.getExceptionList();
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#getExportParameterList()
	 * @return JCoParameterList
	 */
	public JCoParameterList getExportParameterList()
	{
		return delegate.getExportParameterList();
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#getFunctionTemplate()
	 * @return JCoFunctionTemplate
	 * 
	 */
	public JCoFunctionTemplate getFunctionTemplate()
	{
		return delegate.getFunctionTemplate();
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#getImportParameterList()
	 * @return JCoParameterList
	 */
	public JCoParameterList getImportParameterList()
	{
		return delegate.getImportParameterList();
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#getName()
	 * @return name
	 */
	public String getName()
	{
		if (hasConnectionParameters())
		{
			final JCoConnectionParameter p = this.getConnectionParameter();
			if (p.getFunctionModuleToBeReplaced() != null && !p.getFunctionModuleToBeReplaced().isEmpty())
			{
				return p.getFunctionModuleToBeReplaced();
			}
		}
		return delegate.getName();
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#getTableParameterList()
	 * @return TableParameterList
	 */
	public JCoParameterList getTableParameterList()
	{
		return delegate.getTableParameterList();
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#isAbapClassExceptionEnabled()
	 * @return isAbapClassExceptionEnabled
	 */
	public boolean isAbapClassExceptionEnabled()
	{
		return delegate.isAbapClassExceptionEnabled();
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#setAbapClassExceptionMode(com.sap.conn.jco.AbapClassException.Mode)
	 * @param mode
	 *           mode
	 */
	public void setAbapClassExceptionMode(final Mode mode)
	{
		delegate.setAbapClassExceptionMode(mode);
	}

	/**
	 * Delegates call to delegator instance.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#toXML()
	 * 
	 * @return xml
	 */
	public String toXML()
	{
		return delegate.toXML();
	}

	/**
	 * Getter for delegated JCoFunction.
	 * 
	 * @return the delegate
	 */
	public JCoFunction getDelegate()
	{
		return delegate;
	}

	/**
	 * Setter for delegated JCoFunction.
	 * 
	 * @param delegate
	 *           the delegate to set
	 */
	public void setDelegate(final JCoFunction delegate)
	{
		this.delegate = delegate;
	}



}
