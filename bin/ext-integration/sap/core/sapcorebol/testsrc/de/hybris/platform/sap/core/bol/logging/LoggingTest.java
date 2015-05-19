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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;


/**
 * Logging test.
 */
@SuppressWarnings("javadoc")
@UnitTest
public class LoggingTest
{

	private static final String DEBUG_MESSAGE = "Debug Message";
	private static final String INFO_MESSAGE = "Info Message";
	private static final String WARNING_MESSAGE = "Warning Message";
	private static final String ERROR_MESSAGE = "Error Message";
	private static final String FATAL_MESSAGE = "Fatal Message";
	private static final String MESSAGE_WITH_ARGUMENTS = "This is message with args: {0} {1}";
	private static final String MESSAGE_WITH_ARGUMENTS_REPLACED = "This is message with args: arg0 arg1";
	private static final String EXCEPTION_MESSAGE = "This is a message for an exception logging test!";

	private final Log4JWrapper sapLogger = Log4JWrapper.getInstance(LoggingTest.class.getName());
	private final TestAppender testAppender = new TestAppender();

	private final Log4JWrapper sapLogger2 = Log4JWrapper.getInstance("TestName");
	private final TestAppender testAppender2 = new TestAppender();

	@Before
	public void init()
	{
		sapLogger.addAppender(testAppender);
		sapLogger2.addAppender(testAppender2);
	}

	@Test
	public void testHybrisLogging()
	{
		// creating the logger instance
		final Logger hybrisLogger = Logger.getLogger(LoggingTest.class.getName());

		assertNotNull(hybrisLogger);
		hybrisLogger.setLevel(Level.WARN);

		hybrisLogger.addAppender(testAppender);
		hybrisLogger.debug("This should NOT be displayed");
		hybrisLogger.info("This should NOT be displayed");
		hybrisLogger.warn("This should be displayed");

		// check that only one element is in the list of events (as level is
		// warn and thus INFO and DEBUG should not appear)
		assertEquals(testAppender.eventsList.size(), 1);

	}

	@Test
	public void testDifferentLoggerForDifferentNames()
	{
		final Logger logger1 = sapLogger.getLogger();
		final Logger logger2 = sapLogger2.getLogger();
		assertNotSame(logger1, logger2);
		assertNotSame(logger1.getName(), logger2.getName());
	}

	@Test
	public void testLogSeverityLevelInfo()
	{
		sapLogger.setLevel(Level.INFO);

		sapLogger.log(LogSeverity.DEBUG, null, DEBUG_MESSAGE);
		sapLogger.log(LogSeverity.INFO, null, INFO_MESSAGE);
		sapLogger.log(LogSeverity.WARNING, null, WARNING_MESSAGE);
		assertEquals(testAppender.eventsList.size(), 2);

	}

	@Test
	public void testLogSeverityLevelError()
	{
		sapLogger.setLevel(Level.ERROR);
		sapLogger.log(LogSeverity.DEBUG, null, DEBUG_MESSAGE);
		sapLogger.log(LogSeverity.INFO, null, INFO_MESSAGE);
		sapLogger.log(LogSeverity.WARNING, null, WARNING_MESSAGE);

		// No entry should be created
		assertEquals(testAppender.eventsList.size(), 0);

	}

	@Test
	public void testLogDebugMessageWithArg()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.log(LogSeverity.DEBUG, null, MESSAGE_WITH_ARGUMENTS, new Object[]
		{ "arg0", "arg1" });
		assertTrue(testAppender.eventsList.get(0).getMessage().toString().contains(MESSAGE_WITH_ARGUMENTS_REPLACED));
	}

	@Test
	public void testLogErrorCategoryMessage()
	{
		sapLogger.setLevel(Level.ERROR);
		sapLogger.log(LogSeverity.ERROR, LogCategories.APPLICATIONS, ERROR_MESSAGE);

		// One entry should be created
		assertEquals(testAppender.eventsList.size(), 1);
		// Check message text
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains("[Log Category: APPLICATIONS]"));
		assertTrue(message.contains(ERROR_MESSAGE));
	}

	@Test
	public void testTraceSeverityMessage()
	{
		sapLogger.setLevel(Level.TRACE);
		sapLogger.trace(LogSeverity.DEBUG, DEBUG_MESSAGE);

		// One entry should be created
		assertEquals(testAppender.eventsList.size(), 1);
		// Check message text
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains("[Severity: Debug]"));
		assertTrue(message.contains(DEBUG_MESSAGE));
	}

	@Test
	public void testTraceSeverityMessageArgs()
	{
		sapLogger.setLevel(Level.TRACE);
		sapLogger.trace(LogSeverity.WARNING, MESSAGE_WITH_ARGUMENTS, new Object[]
		{ "arg0", "arg1" });

		// One entry should be created
		assertEquals(testAppender.eventsList.size(), 1);
		// Check message text
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains("[Severity: Warning]"));
		assertTrue(message.contains(MESSAGE_WITH_ARGUMENTS_REPLACED));
	}

	/**
	 * This test method is renamed and the @Test annotation is removed because the test throws an RuntimeException.
	 */
	public void renamedTestTraceThrowable()
	{
		sapLogger.setLevel(Level.TRACE);
		sapLogger.traceThrowable(LogSeverity.FATAL, FATAL_MESSAGE, new RuntimeException(EXCEPTION_MESSAGE)); //NOPMD - unnecessary in test-classes
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		final Throwable throwableInformation = testAppender.eventsList.get(0).getThrowableInformation().getThrowable();
		assertTrue(message.contains("[Severity: Fatal]"));
		assertTrue(throwableInformation.toString().contains(EXCEPTION_MESSAGE));
	}

	/**
	 * This test method is renamed and the @Test annotation is removed because the test throws an RuntimeException.
	 */
	public void renamedTestTraceThrowableMessageArgs()
	{
		sapLogger.setLevel(Level.TRACE);
		final RuntimeException exc = new RuntimeException(EXCEPTION_MESSAGE); //NOPMD - unnecessary in test-classes
		sapLogger.traceThrowable(LogSeverity.FATAL, exc, MESSAGE_WITH_ARGUMENTS, new Object[]
		{ "arg0", "arg1" });
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		final Throwable throwableInformation = testAppender.eventsList.get(0).getThrowableInformation().getThrowable();
		assertTrue(message.contains("[Severity: Fatal]"));
		assertTrue(message.contains(MESSAGE_WITH_ARGUMENTS_REPLACED));
		assertTrue(throwableInformation.toString().contains(EXCEPTION_MESSAGE));
	}

	/**
	 * This test method is renamed and the @Test annotation is removed because the test throws an RuntimeException.
	 */
	public void renamedTestThrowing()
	{
		sapLogger.setLevel(Level.TRACE);
		sapLogger.throwing(new RuntimeException(EXCEPTION_MESSAGE)); //NOPMD - unnecessary in test-classes
		final Throwable throwableInformation = testAppender.eventsList.get(0).getThrowableInformation().getThrowable();
		assertTrue(throwableInformation.toString().contains(EXCEPTION_MESSAGE));
	}

	@Test
	public void testEntering()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.entering("testEntering");
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains("Entering: testEntering"));
	}

	@Test
	public void testExiting()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.exiting();
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains("Exiting"));
	}

	@Test
	public void testDebug()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.debug(DEBUG_MESSAGE);
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains(DEBUG_MESSAGE));
	}

	/**
	 * This test method is renamed and the @Test annotation is removed because the test throws an RuntimeException.
	 */
	public void renamedTestDebugMessageThrowable()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.debug(DEBUG_MESSAGE, new RuntimeException(EXCEPTION_MESSAGE)); //NOPMD - unnecessary in test-classes
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		final Throwable throwable = testAppender.eventsList.get(0).getThrowableInformation().getThrowable();
		assertTrue(throwable.toString().contains(EXCEPTION_MESSAGE));
		assertTrue(message.contains(DEBUG_MESSAGE));
	}

	@Test
	public void testDebugArgs()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.debug(MESSAGE_WITH_ARGUMENTS, new Object[]
		{ "arg0", "arg1" });
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains(MESSAGE_WITH_ARGUMENTS_REPLACED));
	}

	@Test
	public void testDebugWith1Arg()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.debugWithArgs("This is message with args: {0}", "arg0");
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains("This is message with args: arg0"));
	}

	@Test
	public void testDebugWith2Args()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.debugWithArgs(MESSAGE_WITH_ARGUMENTS, "arg0", "arg1");
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains(MESSAGE_WITH_ARGUMENTS_REPLACED));
	}

	@Test
	public void testDebugWith3Args()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.debugWithArgs("This is message with args: {0} {1} {2}", "arg0", "arg1", "arg2");
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains("This is message with args: arg0 arg1 arg2"));
	}

	@Test
	public void testDebugWith4Args()
	{
		sapLogger.setLevel(Level.DEBUG);
		sapLogger.debugWithArgs("This is message with args: {0} {1} {2} {3}", "arg0", "arg1", "arg2", "arg3");
		final String message = testAppender.eventsList.get(0).getMessage().toString();
		assertTrue(message.contains("This is message with args: arg0 arg1 arg2 arg3"));
	}

	@Test
	public void testIsDebugEnabled()
	{
		sapLogger.setLevel(Level.DEBUG);
		assertTrue(sapLogger.isDebugEnabled());
		sapLogger.setLevel(Level.ERROR);
		assertFalse(sapLogger.isDebugEnabled());
	}

	@Test
	public void testIsInfoEnabled()
	{
		sapLogger.setLevel(Level.INFO);
		assertTrue(sapLogger.isInfoEnabled());
		sapLogger.setLevel(Level.ERROR);
		assertFalse(sapLogger.isInfoEnabled());
	}

	@Test
	public void testIsWarningEnabled()
	{
		sapLogger.setLevel(Level.WARN);
		assertTrue(sapLogger.isWarningEnabled());
		sapLogger.setLevel(Level.INFO);
		assertTrue(sapLogger.isWarningEnabled());
		sapLogger.setLevel(Level.ERROR);
		assertFalse(sapLogger.isWarningEnabled());
	}

	/**
	 * Test Appender extending {@link AppenderSkeleton}.
	 */
	public class TestAppender extends AppenderSkeleton
	{
		private final List<LoggingEvent> eventsList = new ArrayList<LoggingEvent>();

		@Override
		protected void append(final LoggingEvent event)
		{
			eventsList.add(event);
		}

		@Override
		public void close()
		{
			//
		}

		@Override
		public boolean requiresLayout()
		{
			return false;
		}
	}

}
