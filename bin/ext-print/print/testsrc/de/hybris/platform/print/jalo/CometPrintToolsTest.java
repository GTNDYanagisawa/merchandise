/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.print.jalo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.print.comet.OS;
import de.hybris.platform.print.comet.constants.CometConstants;
import de.hybris.platform.print.comet.utils.CometPrintTools;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests for CometPrintToolsTest
 */
public class CometPrintToolsTest extends HybrisJUnit4TransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CometPrintToolsTest.class.getName());

	private static String PREFIX_NAME = "testPrefix";
	private static String PREFIX_NAME_ID = "idTestPrefix";
	private static String PREFIX_NAME_NAMED = "namedTestPrefix";
	private static String PREFIX_PATH_WIN = "c:\\hybris";
	private static String PREFIX_PATH_MAC = "/home/hybris";

	@Before
	public void setUp() throws Exception
	{
		final PrintManager manager = PrintManager.getInstance();

		// Creating PathPrefix for test
		final PathPrefix aPrefix = manager.createPathPrefix(PREFIX_NAME, PREFIX_PATH_WIN, PREFIX_PATH_MAC);
		assertNotNull(aPrefix);

		final PathPrefix aPrefixWithID = manager.createPathPrefix(PREFIX_NAME_ID, PREFIX_PATH_WIN + "\\{"
				+ PathPrefix.VARIABLE_QUALIFIER_USERID + "}", PREFIX_PATH_MAC + "/{" + PathPrefix.VARIABLE_QUALIFIER_USERID + "}");
		assertNotNull(aPrefixWithID);

		final PathPrefix aPrefixWithName = manager
				.createPathPrefix(PREFIX_NAME_NAMED, PREFIX_PATH_WIN + "\\{" + PathPrefix.VARIABLE_QUALIFIER_USERNAME + "}",
						PREFIX_PATH_MAC + "/{" + PathPrefix.VARIABLE_QUALIFIER_USERNAME + "}");
		assertNotNull(aPrefixWithName);
	}


	// Test windows path
	@Test
	public void testPathPrefixForWindows()
	{
		final SessionContext ctx = jaloSession.getSessionContext();

		ctx.setAttribute(CometConstants.SessionQualifiers.CLIENT_OS, OS.WINDOWS);
		final String in = PathPrefix.VARIABLES_OPEN_TAG + PathPrefix.VARIABLE_QUALIFIER_PATHPREFIX + ":" + PREFIX_NAME
				+ PathPrefix.VARIABLES_CLOSE_TAG + "\\further\\path\\and\\filename.extension";
		final String out = PREFIX_PATH_WIN + "\\further\\path\\and\\filename.extension";
		final String result = CometPrintTools.getCompletePath(ctx, in);
		assertEquals(out, result);
	}

	// Test macintosh path
	@Test
	public void testPathPrefixForMac()
	{
		final SessionContext ctx = jaloSession.getSessionContext();

		ctx.setAttribute(CometConstants.SessionQualifiers.CLIENT_OS, OS.MACOSX);
		final String in = PathPrefix.VARIABLES_OPEN_TAG + PathPrefix.VARIABLE_QUALIFIER_PATHPREFIX + ":" + PREFIX_NAME
				+ PathPrefix.VARIABLES_CLOSE_TAG + "/further/path/and/filename.extension";
		final String out = PREFIX_PATH_MAC + "/further/path/and/filename.extension";
		final String result = CometPrintTools.getCompletePath(ctx, in);
		assertEquals(out, result);
	}

	// Test windows path with user.ID
	@Test
	public void testPathPrefixForWindowsWithUserID()
	{
		final SessionContext ctx = jaloSession.getSessionContext();

		ctx.setAttribute(CometConstants.SessionQualifiers.CLIENT_OS, OS.WINDOWS);
		final String in = PathPrefix.VARIABLES_OPEN_TAG + PathPrefix.VARIABLE_QUALIFIER_PATHPREFIX + ":" + PREFIX_NAME_ID
				+ PathPrefix.VARIABLES_CLOSE_TAG + "\\further\\path\\and\\filename.extension";
		final String out = PREFIX_PATH_WIN + "\\" + ctx.getUser().getUID() + "\\further\\path\\and\\filename.extension";
		final String result = CometPrintTools.getCompletePath(ctx, in);
		assertEquals(out, result);
	}

	// Test macintosh path with user.ID
	@Test
	public void testPathPrefixForMacWithUserID()
	{
		final SessionContext ctx = jaloSession.getSessionContext();

		ctx.setAttribute(CometConstants.SessionQualifiers.CLIENT_OS, OS.MACOSX);
		final String in = PathPrefix.VARIABLES_OPEN_TAG + PathPrefix.VARIABLE_QUALIFIER_PATHPREFIX + ":" + PREFIX_NAME_ID
				+ PathPrefix.VARIABLES_CLOSE_TAG + "/further/path/and/filename.extension";
		final String out = PREFIX_PATH_MAC + "/" + ctx.getUser().getUID() + "/further/path/and/filename.extension";
		final String result = CometPrintTools.getCompletePath(ctx, in);
		assertEquals(out, result);
	}

	// Test windows path with user.name
	@Test
	public void testPathPrefixForWindowsWithUserName()
	{
		final SessionContext ctx = jaloSession.getSessionContext();

		ctx.setAttribute(CometConstants.SessionQualifiers.CLIENT_OS, OS.WINDOWS);
		final String in = PathPrefix.VARIABLES_OPEN_TAG + PathPrefix.VARIABLE_QUALIFIER_PATHPREFIX + ":" + PREFIX_NAME_NAMED
				+ PathPrefix.VARIABLES_CLOSE_TAG + "\\further\\path\\and\\filename.extension";
		final String out = PREFIX_PATH_WIN + "\\" + ctx.getUser().getName() + "\\further\\path\\and\\filename.extension";
		final String result = CometPrintTools.getCompletePath(ctx, in);
		assertEquals(out, result);
	}

	// Test macintosh path with user.name
	@Test
	public void testPathPrefixForMacWithUserName()
	{
		final SessionContext ctx = jaloSession.getSessionContext();

		ctx.setAttribute(CometConstants.SessionQualifiers.CLIENT_OS, OS.MACOSX);
		final String in = PathPrefix.VARIABLES_OPEN_TAG + PathPrefix.VARIABLE_QUALIFIER_PATHPREFIX + ":" + PREFIX_NAME_NAMED
				+ PathPrefix.VARIABLES_CLOSE_TAG + "/further/path/and/filename.extension";
		final String out = PREFIX_PATH_MAC + "/" + ctx.getUser().getName() + "/further/path/and/filename.extension";
		final String result = CometPrintTools.getCompletePath(ctx, in);
		assertEquals(out, result);
	}
}
