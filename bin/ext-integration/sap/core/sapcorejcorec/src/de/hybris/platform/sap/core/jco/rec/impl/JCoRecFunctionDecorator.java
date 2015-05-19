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
package de.hybris.platform.sap.core.jco.rec.impl;

import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.RecorderUtils;

import org.apache.log4j.Logger;

import com.sap.conn.jco.AbapClassException.Mode;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoParameterList;


/**
 * Design of this class follows the decorator pattern although it is not exactly the class hierarchy mentioned in
 * http://en.wikipedia.org/wiki/Decorator_pattern. <br>
 * Execution of the actual backend calls is delegated to {@link #decoratedFunction}, the execution against the XML
 * repository is implemented here.
 */
public class JCoRecFunctionDecorator implements JCoFunction
{

	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(JCoRecFunctionDecorator.class.getName());


	private static final long serialVersionUID = -4626071511384483622L;

	private final JCoRecMode mode;
	private final String functionKey;

	/**
	 * The {@link JCoFunction} that is executed against the backend or taken from the repository file.
	 */
	private JCoFunction decoratedFunction;

	/**
	 * Getter for the delegated function.
	 * 
	 * @return Returns the delegated function.
	 */
	public JCoFunction getDecoratedFunction()
	{
		return decoratedFunction;
	}

	/**
	 * Setter for the delegated function.
	 * 
	 * @param function
	 *           the new value for the delegated function.
	 */
	protected void setDecoratedFunction(final JCoFunction function)
	{
		if (function instanceof JCoRecFunctionDecorator)
		{
			decoratedFunction = ((JCoRecFunctionDecorator) function).getDecoratedFunction();
		}
		else
		{
			decoratedFunction = function;
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param function
	 *           this function will be delegated/wrapped.
	 * @param counter
	 *           the execution counter of the wrapped function.
	 * @param mode
	 *           the current mode of the JCoRecorder.
	 */
	public JCoRecFunctionDecorator(final JCoFunction function, final int counter, final JCoRecMode mode)
	{
		this.decoratedFunction = function;

		this.mode = mode;
		this.functionKey = RecorderUtils.getFunctionKey(function.getName(), counter);
	}

	/**
	 * Processes the actual execute-method.
	 * 
	 * @param destination
	 *           the destination where to execute the function.
	 * @throws JCoException
	 *            if an error occurs while executing on the back-end.
	 */
	private void executeWrapper(final JCoDestination destination) throws JCoException
	{
		if (mode == JCoRecMode.PLAYBACK)
		{
			return;
		}

		this.decoratedFunction.execute(destination);
	}

	@Override
	public void execute(final JCoDestination destination) throws JCoException
	{
		executeWrapper(destination);
	}

	/**
	 * Unsupported method.
	 * 
	 * @param destination
	 *           Unsupported method.
	 * @param tid
	 *           Unsupported method.
	 * @throws JCoException
	 *            Unsupported method.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#execute(com.sap.conn.jco.JCoDestination, java.lang.String)
	 */
	@Override
	public void execute(final JCoDestination destination, final String tid) throws JCoException
	{
		throw new UnsupportedOperationException("This method is NOT supported!");
	}

	/**
	 * Unsupported method.
	 * 
	 * @param destination
	 *           Unsupported method.
	 * @param tid
	 *           Unsupported method.
	 * @param queueName
	 *           Unsupported method.
	 * @throws JCoException
	 *            Unsupported method.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#execute(com.sap.conn.jco.JCoDestination, java.lang.String, java.lang.String)
	 */
	@Override
	public void execute(final JCoDestination destination, final String tid, final String queueName) throws JCoException
	{
		throw new UnsupportedOperationException("This method is NOT supported!");
	}

	@Override
	public JCoParameterList getImportParameterList()
	{
		return decoratedFunction.getImportParameterList();
	}

	@Override
	public JCoParameterList getExportParameterList()
	{
		return decoratedFunction.getExportParameterList();
	}

	@Override
	public JCoParameterList getChangingParameterList()
	{
		return decoratedFunction.getChangingParameterList();
	}

	@Override
	public JCoParameterList getTableParameterList()
	{
		return decoratedFunction.getTableParameterList();
	}

	@Override
	public AbapException getException(final String arg0)
	{
		return decoratedFunction.getException(arg0);
	}

	@Override
	public AbapException[] getExceptionList()
	{
		return decoratedFunction.getExceptionList();
	}

	@Override
	public JCoFunctionTemplate getFunctionTemplate()
	{
		return decoratedFunction.getFunctionTemplate();
	}

	@Override
	public String toXML()
	{
		return this.decoratedFunction.toXML();
	}


	/**
	 * Generates the function key for this instance.
	 * 
	 * @return Returns the result from {@link RecorderUtils#getFunctionKey(String, int)} with the name of the decorated
	 *         function and the local counter as parameter.
	 */
	public String getFunctionKey()
	{
		return functionKey;
	}

	@Override
	public String getName()
	{
		return decoratedFunction.getName();
	}

	@Override
	public String toString()
	{
		return super.toString() + " with key " + getFunctionKey();
	}

	@Override
	public boolean isAbapClassExceptionEnabled()
	{
		throw new JCoRecRuntimeException("Method is not delegated due to a bug "
				+ "(see https://GTP.wdf.sap.corp/sap/bc/webdynpro/qce/"
				+ "msg_gui_edit?sap-language=E&csinsta=0120031469&mnumm=0000221893&myear=2012)");
	}

	@Override
	public void setAbapClassExceptionMode(final Mode arg0)
	{
		throw new JCoRecRuntimeException("Method is not delegated due to a bug "
				+ "(see https://GTP.wdf.sap.corp/sap/bc/webdynpro/qce/"
				+ "msg_gui_edit?sap-language=E&csinsta=0120031469&mnumm=0000221893&myear=2012)");
	}
}
