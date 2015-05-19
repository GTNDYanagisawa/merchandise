package com.hybris.datahub.core.facades.impl.converter;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.datahub.core.facades.ImportError;
import com.hybris.datahub.core.facades.ImportTestUtils;
import com.hybris.datahub.core.io.TextFile;

import java.io.IOException;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Tests how the log error parser parses messages reported in the error log and binds them to the corresponding lines of
 * the source impex script.
 */
@SuppressWarnings("javadoc")
@UnitTest
public class ErrorLogParserUnitTest
{
	/**
	 * Simulates content of the error log being parsed.
	 */
	private static final String ERROR_LOG_CONTENT = "14.01.2014 15:48:27: ERROR: line 7 at main script: An error occured";
	/**
	 * Simulates the file, in which ImportService stores impex script being imported.
	 */
	private static final TextFile impex = new TextFile(System.getProperty("java.io.tmpdir"), "test.impex");

	@BeforeClass
	public static void setUpBeforeAllTests() throws IOException
	{
		impex.save(impexScript());
	}

	private static String impexScript()
	{
		return ImportTestUtils.toText(
		/* line 1: */"##########################",
		/* line 2: */"INSERT_UPDATE Category;;description[lang=en];name[lang=en];code[unique=true];$catalogVersion",
		/* line 3: */";single|Category|008043d7-fa38-4a26-96c0-52a411fde1cd;<ignore>;Material;3912;",
		/* line 4: */"",
		/* line 5: */"##########################",
		/* line 6: */"INSERT_UPDATE Product;;description[lang=de];name[lang=de];code[unique=true];unit(code);$catalogVersion",
		/* line 7: */";single|Product|3e5c5997-00ab-499c-8987-1e5ee3435bcf;<ignore>;Andover Jacke;95385;pack;");
	}

	@AfterClass
	public static void cleanAfterAllTests() throws IOException
	{
		impex.delete();
	}

	private final ErrorLogParser parser = new ErrorLogParser();

	@Test
	public void testNoErrorLogResultsInEmptyErrorsCollection() throws IOException
	{
		final String log = null;
		final Collection<ImportError> errors = parser.parse(log, impex);

		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testEmptyErrorLogResultsInEmptyErrorsCollection() throws IOException
	{
		final String log = "";
		final Collection<ImportError> errors = parser.parse(log, impex);

		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testParserCreatesAnImportErrorForTheProblemReportedInTheLog() throws IOException
	{
		final Collection<ImportError> errors = parser.parse(ERROR_LOG_CONTENT, impex);
		Assert.assertEquals(1, errors.size());
	}

	@Test
	public void testParserClassifiesTheErrorByTheErrorLogMessage() throws IOException
	{
		final ImportError err = parser.parse(ERROR_LOG_CONTENT, impex).iterator().next();
		Assert.assertNotNull(err.getCode());
	}

	@Test
	public void testCorrectlyAttachesTheScriptLineBasedOnTheLineNumberReportedInTheErrorLogMessage() throws IOException
	{
		final ImportError err = parser.parse(ERROR_LOG_CONTENT, impex).iterator().next();

		Assert.assertEquals(impex.readLine(7), err.getScriptLine());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScriptFileIsNeverExpectedToBeNull() throws IOException
	{
		parser.parse(ERROR_LOG_CONTENT, null);
	}
}
