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
package de.hybris.platform.sap.core.bol.logging;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;


/**
 * Log4JWrapper.
 */
public class Log4JWrapper
{

	/**
	 * Logger object.
	 */
	private Logger logger = null; //NOPMD - can't be final

	/**
	 * Constructor for creating a logging object.
	 * 
	 * @param name
	 *           logging object name
	 */
	protected Log4JWrapper(final String name)
	{
		logger = Logger.getLogger(name);
	}

	/**
	 * Adds an additional appender to logging object.
	 * 
	 * @param appender
	 *           logging appender
	 */
	protected void addAppender(final Appender appender)
	{
		logger.addAppender(appender);
	}

	/**
	 * The method to retrieve the logging object. The name should contain the package name and class name.
	 * <p>
	 * It will be ensured that only one instance is created
	 * </p>
	 * 
	 * @param name
	 *           - the name of the current logger.
	 * @return the logger instance
	 */
	public static Log4JWrapper getInstance(final String name)
	{
		return new Log4JWrapper(name);
	}

	/**
	 * Generates a log message for the given category.
	 * 
	 * @param severity
	 *           the severity of the message
	 * @param category
	 *           the log category
	 * @param message
	 *           the message
	 */
	public void log(final int severity, final LogCategory category, final String message)
	{
		final String logMessage = getLogMessage(category, message, null);
		logMessage(severity, logMessage);
	}

	/**
	 * Log message according to the given severity.
	 * 
	 * @param severity
	 *           log severity
	 * @param message
	 *           message to be logged
	 */
	private void logMessage(final int severity, final String message)
	{
		switch (severity)
		{
			case LogSeverity.DEBUG:
				logger.debug(message);
				break;
			case LogSeverity.WARNING:
				logger.warn(message);
				break;
			case LogSeverity.ERROR:
				logger.error(message);
				break;
			case LogSeverity.INFO:
				logger.info(message);
				break;
			case LogSeverity.FATAL:
				logger.fatal(message);
				break;
		}
	}

	/**
	 * Generates a log a message for the given severity and category by merging the given args with the message string.
	 * Placeholders for the arguments in the message string must look like {i} (e.g. {0}) where i is the index in the
	 * args array.<br>
	 * 
	 * @param severity
	 *           the message severity
	 * @param category
	 *           the log category
	 * @param message
	 *           the message
	 * @param args
	 *           the arguments of the message
	 */
	public void log(final int severity, final LogCategory category, String message, final Object[] args)
	{
		message = getLogMessage(category, message, args);
		logMessage(severity, message);
	}

	/**
	 * Generates a trace message for the given severity.<br>
	 * 
	 * @param severity
	 *           the message severity
	 * @param message
	 *           the message
	 */
	public void trace(final int severity, final String message)
	{
		logger.trace(getTraceMessage(severity, message, null));
	}

	/**
	 * Generates a trace message for the given severity by merging the given args with the message string. Placeholders
	 * for the arguments in the message string must look like {i} (e.g. {0}) where i is the index in the args array.<br>
	 * 
	 * @param severity
	 *           the message severity
	 * @param message
	 *           the message
	 * @param args
	 *           the arguments of the message
	 */
	public void trace(final int severity, final String message, final Object[] args)
	{

		logger.trace(getTraceMessage(severity, message, args));
	}

	/**
	 * Generates a trace message for the given severity and exception.<br>
	 * 
	 * @param severity
	 *           the message severity
	 * @param message
	 *           the message
	 * @param exc
	 *           the exception
	 */
	public void traceThrowable(final int severity, final String message, final Throwable exc)
	{
		logger.trace(getTraceMessage(severity, message, null), exc);
	}

	/**
	 * Generates a trace message for the given severity and exception by merging the given args with the message string.
	 * Placeholders for the arguments in the message string must look like {i} (e.g. {0}) where i is the index in the
	 * args array.<br>
	 * 
	 * @param severity
	 *           the message severity
	 * @param exc
	 *           the exception
	 * @param message
	 *           the message
	 * @param args
	 *           the arguments of the message
	 */
	public void traceThrowable(final int severity, final Throwable exc, final String message, final Object[] args)
	{
		logger.trace(getTraceMessage(severity, message, args), exc);
	}

	// --------------------------------------------------------------------------
	// GENERIC TRACES
	// --------------------------------------------------------------------------

	/**
	 * Should be used when an exception is thrown. The exception is written using its method toString with severity
	 * Severity.ERROR.
	 * 
	 * @param t
	 *           an occurring exception
	 */
	public void throwing(final Throwable t)
	{
		logger.error("Exception", t);
	}

	/**
	 * Traces message of severity Severity.DEBUG and appends a string denoting the name of the entered method. This
	 * method must be balanced with a call to exiting when leaving the traced method, for example exiting().
	 * 
	 * @param method
	 *           name of sublocation
	 */
	public void entering(final String method)
	{
		logger.debug("Entering: " + method);
	}

	/**
	 * Traces message of severity Severity.DEBUG which indicates that execution is about to leave this method location.
	 * This method is to be balanced with a call to the method entering().
	 */
	public void exiting()
	{
		logger.debug("Exiting");
	}

	// --------------------------------------------------------------------------
	// DEBUG Tracing
	// --------------------------------------------------------------------------

	/**
	 * Logs a message object with the {@link Priority#DEBUG DEBUG} priority.
	 * 
	 * @param message
	 *           the message object to log using plain English.
	 */
	public void debug(final Object message)
	{
		logger.debug(message);

	}

	/**
	 * Trace a message object with the {@link Priority#DEBUG DEBUG} priority, by merging the given args with the message
	 * string. Placeholders for the arguments in the message string must look like {i} (e.g. {0}) where i is the index in
	 * the args array.
	 * 
	 * @param message
	 *           the message object to log using plain english.
	 * @param args
	 *           the Objects to merge with the given message.
	 */
	public void debug(final String message, final Object[] args)
	{
		logger.debug(getTraceMessage(0, message, args));
	}

	/**
	 * Trace a message object with the {@link Priority#DEBUG DEBUG} priority, by merging the given argument with the
	 * message string. Placeholder for the argument in the message string must be {0}. This method does not require to be
	 * surrounded with an if statement to check if debug is enabled, to inhibit unnecessary memory consumption.
	 * 
	 * @param message
	 *           the message object to log using plain English.
	 * @param arg0
	 *           the argument Object to merge with the given message for Placeholder {0}.
	 */
	public void debugWithArgs(final String message, final Object arg0)
	{
		logger.debug(getTraceMessage(0, message, new Object[]
		{ arg0 }));
	}

	/**
	 * Trace a message object with the {@link Priority#DEBUG DEBUG} priority, by merging the given arguments with the
	 * message string. Placeholder for the argument in the message string must be {0} and {1}. This method does not
	 * require to be surrounded with an if statement to check if debug is enabled, to inhibit unnecessary memory
	 * consumption.
	 * 
	 * @param message
	 *           the message object to log using plain English.
	 * @param arg0
	 *           the argument Object to merge with the given message for Placeholder {0}.
	 * @param arg1
	 *           the argument Object to merge with the given message for Placeholder {1}.
	 */
	public void debugWithArgs(final String message, final Object arg0, final Object arg1)
	{
		logger.debug(getTraceMessage(0, message, new Object[]
		{ arg0, arg1 }));
	}

	/**
	 * Trace a message object with the {@link Priority#DEBUG DEBUG} priority, by merging the given arguments with the
	 * message string. Placeholder for the argument in the message string must be {0}, {1} and {2}. This method does not
	 * require to be surrounded with an if statement to check if debug is enabled, to inhibit unnecessary memory
	 * consumption.
	 * 
	 * @param message
	 *           the message object to log using plain English.
	 * @param arg0
	 *           the argument Object to merge with the given message for Placeholder {0}.
	 * @param arg1
	 *           the argument Object to merge with the given message for Placeholder {1}.
	 * @param arg2
	 *           the argument Object to merge with the given message for Placeholder {2}.
	 */
	public void debugWithArgs(final String message, final Object arg0, final Object arg1, final Object arg2)
	{
		logger.debug(getTraceMessage(0, message, new Object[]
		{ arg0, arg1, arg2 }));
	}

	/**
	 * Trace a message object with the {@link Priority#DEBUG DEBUG} priority, by merging the given arguments with the
	 * message string. Placeholder for the argument in the message string must be {0}, {1}, {2} and {3}. This method does
	 * not require to be surrounded with an if statement to check if debug is enabled, to inhibit unnecessary memory
	 * consumption.
	 * 
	 * @param message
	 *           the message object to log using plain English.
	 * @param arg0
	 *           the argument Object to merge with the given message for Placeholder {0}.
	 * @param arg1
	 *           the argument Object to merge with the given message for Placeholder {1}.
	 * @param arg2
	 *           the argument Object to merge with the given message for Placeholder {2}.
	 * @param arg3
	 *           the argument Object to merge with the given message for Placeholder {3}.
	 */
	public void debugWithArgs(final String message, final Object arg0, final Object arg1, final Object arg2, final Object arg3)
	{
		logger.debug(getTraceMessage(0, message, new Object[]
		{ arg0, arg1, arg2, arg3 }));
	}

	/**
	 * Log a message object with the <code>DEBUG</code> priority including the stack trace of the {@link Throwable}
	 * <code>t</code> passed as parameter.
	 * <p>
	 * See {@link #debug(Object)} form for more detailed information.
	 * 
	 * @param message
	 *           the message object to log.
	 * @param t
	 *           the exception to log, including its stack trace.
	 */
	public void debug(final Object message, final Throwable t)
	{
		logger.debug(message, t);
	}

	// -------------------------------------------------------------------------
	// ACCESSOR
	// -------------------------------------------------------------------------

	/**
	 * Check whether this location is enabled for the <code>DEBUG</code> priority.
	 * <p>
	 * This function is intended to lessen the computational cost of disabled log debug statements.
	 * <p>
	 * For some <code>cat</code> location object, when you write,
	 * 
	 * <pre>
	 * cat.debug(&quot;This is entry number: &quot; + i);
	 * </pre>
	 * <p>
	 * You incur the cost constructing the message, concatenation in this case, regardless of whether the message is
	 * logged or not.
	 * <p>
	 * If you are worried about speed, then you should write
	 * 
	 * <pre>
	 * if (cat.isDebugEnabled())
	 * {
	 * 	cat.debug(&quot;This is entry number: &quot; + i);
	 * }
	 * </pre>
	 * <p>
	 * This way you will not incur the cost of parameter construction if debugging is disabled for <code>cat</code>. On
	 * the other hand, if the <code>cat</code> is debug enabled, you will incur the cost of evaluating whether the
	 * location is debug enabled twice. Once in <code>isDebugEnabled</code> and once in the <code>debug</code>. This is
	 * an insignificant overhead since evaluating a location takes about 1% of the time it takes to actually log.
	 * 
	 * @return boolean - <code>true</code> if this location is debug enabled, <code>false</code> otherwise.
	 */
	public boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
	}

	/**
	 * Check whether this location is enabled for the info priority. See also {@link #isDebugEnabled}.
	 * 
	 * @return boolean - <code>true</code> if this location is enabled for priority info, <code>false</code> otherwise.
	 */
	public boolean isInfoEnabled()
	{
		return logger.isInfoEnabled();
	}

	/**
	 * Check whether this location is enabled for the warning priority. See also {@link #isDebugEnabled}.
	 * 
	 * @return boolean - <code>true</code> if this location is enabled for priority warning, <code>false</code>
	 *         otherwise.
	 */
	public boolean isWarningEnabled()
	{
		if (logger.isEnabledFor(Level.WARN))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * This methods returns the instance of the logger.
	 * 
	 * @return the logger
	 */
	public Logger getLogger()
	{
		return logger;
	}

	/**
	 * Builds the trace message using the message string and given arguments.
	 * 
	 * @param severity
	 *           the severity
	 * @param message
	 *           the message
	 * @param args
	 *           the arguments
	 * @return the message with included arguments
	 */
	private String getTraceMessage(final int severity, String message, final Object[] args)
	{

		if (args != null && args.length > 0)
		{
			int i = 0;
			for (final Object arg : args)
			{
				final String toReplace = "\\{".concat(String.valueOf(i)).concat("\\}");
				final String sarg = (arg != null) ? arg.toString() : "null";
				message = message.replaceAll(toReplace, sarg);
				i++;
			}
		}

		if (severity > 0)
		{

			final StringBuffer strBuffer = new StringBuffer();
			strBuffer.append(message);
			strBuffer.append(" [Severity: ");
			strBuffer.append(LogSeverity.toString(severity));
			strBuffer.append("] ");
			return strBuffer.toString();
		}

		return message;
	}

	/**
	 * Builds the log message using the message string and given arguments.
	 * 
	 * @param category
	 *           the category
	 * @param message
	 *           the message
	 * @param args
	 *           the message arguments
	 * @return the message including the arguments
	 */
	private String getLogMessage(final LogCategory category, String message, final Object[] args)
	{

		if (args != null && args.length > 0)
		{
			int i = 0;
			for (final Object arg : args)
			{
				final String toReplace = "\\{".concat(String.valueOf(i)).concat("\\}");
				message = message.replaceAll(toReplace, arg.toString());
				i++;
			}
		}

		if (category != null && category.toString().length() > 0)
		{

			final StringBuffer strBuffer = new StringBuffer();
			strBuffer.append(message);
			strBuffer.append(" [Log Category: ");
			strBuffer.append(category.getCategoryValue());
			strBuffer.append("]");
			return strBuffer.toString();
		}

		return message;
	}

	/**
	 * Sets the log level.
	 * 
	 * @param level
	 *           the log level
	 */
	public void setLevel(final Level level)
	{
		logger.setLevel(level);
	}

}
