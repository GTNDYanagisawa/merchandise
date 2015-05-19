package com.hybris.datahub.core.facades.impl.converter;

import static com.hybris.datahub.core.facades.ImportTestUtils.successfulImportResult;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.impex.ImportResult;

import com.hybris.datahub.core.facades.ErrorCode;
import com.hybris.datahub.core.facades.ImportError;
import com.hybris.datahub.core.facades.ImportTestUtils;
import com.hybris.datahub.core.facades.ItemImportResult;
import com.hybris.datahub.core.io.TextFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests how converter builds the import result and handles the import errors. The error parsing of the error log and
 * the preview fields reported in the import result is simulated for more comprehensive case coverage.
 */
@SuppressWarnings("javadoc")
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SourceAndErrorCombiningConverterUnitTest
{
	private static final String PS = File.pathSeparator;
	private static final String IMPEX_FILE_PATH = "impex" + PS + "media" + PS + "test.impex";
	private static final String TENANT_MEDIA_DIR = "sys_master";

	/**
	 * This error log content is used to simulate unstructured error message that cannot be parsed.
	 */
	private static final String UNSTRUCTURED_ERROR_LOG = "some unexpected and unstructured message";
	/**
	 * This simulates a normal error log content that is parsed into {@link #LOG_ERROR}
	 */
	private static final String ERROR_LOG = "Error log will never be parsed because result is mocked";
	/**
	 * This simulates a preview error that is parsed into {@link #PREVIEW_ERROR}
	 */
	private static final String PREVIEW = "Preview log will never be parsed because result is mocked";


	/**
	 * Returned by the parsers whenever the import result is successful
	 */
	private static final Collection<ImportError> no_errors = new ArrayList<>();
	/**
	 * Returned by the log parser whenever the import result contains {@link #ERROR_LOG} for the error log text.
	 */
	private static final ImportError LOG_ERROR = ImportTestUtils.error(ErrorCode.INVALID_DATA_FORMAT,
			"An error reported in the error log");
	/**
	 * Returned by the preview parser whenever the import result contains {@link #PREVIEW} for the preview
	 */
	private static final ImportError PREVIEW_ERROR = ImportTestUtils.error(ErrorCode.REFERENCE_VIOLATION,
			"An error reported in the preview");

	@Mock
	private ErrorLogParser errorLogParser;
	@Mock
	private PreviewParser previewParser;
	@InjectMocks
	private final SourceAndErrorCombiningConverter converter = new SourceAndErrorCombiningConverter();

	@Before
	public void setUp() throws IOException
	{
		setUpErrorLogParser();
		setUpPreviewParser();
	}

	private void setUpErrorLogParser() throws IOException
	{
		final Collection<ImportError> errors = Arrays.asList(LOG_ERROR);
		Mockito.doReturn(no_errors).when(errorLogParser).parse(eq((String) null), any(TextFile.class));
		Mockito.doReturn(no_errors).when(errorLogParser).parse(eq(UNSTRUCTURED_ERROR_LOG), any(TextFile.class));
		Mockito.doReturn(errors).when(errorLogParser).parse(eq(ERROR_LOG), any(TextFile.class));
	}

	private void setUpPreviewParser()
	{
		final Collection<ImportError> errors = Arrays.asList(PREVIEW_ERROR);
		Mockito.doReturn(no_errors).when(previewParser).parse(null);
		Mockito.doReturn(errors).when(previewParser).parse(PREVIEW);
	}

	@Test
	public void testConvertsNullImportResultToNullItemsImportResult()
	{
		final ItemImportResult result = converter.convert(null);
		Assert.assertNull(result);
	}

	@Test
	public void testConvertsSuccessfulImportResult()
	{
		final ItemImportResult result = converter.convert(successfulImportResult());

		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrors().isEmpty());
	}

	@Test
	public void testDoesNotAttemptToParseLogsWhenResultIsSuccessful() throws IOException
	{
		converter.convert(successfulImportResult());

		Mockito.verify(errorLogParser, never()).parse(anyString(), any(TextFile.class));
		Mockito.verify(previewParser, never()).parse(anyString());
	}

	@Test
	public void testConvertedResultIsErrorWhenErrorLogReportsErrors()
	{
		final ItemImportResult result = converter.convert(importResultWithLogErrors());

		Assert.assertFalse(result.isSuccessful());
	}

	@Test
	public void testConvertedResultContainsErrorsReportedInTheErrorLog()
	{
		final ItemImportResult result = converter.convert(importResultWithLogErrors());
		final Collection<ImportError> errors = result.getErrors();

		Assert.assertEquals(1, errors.size());
		Assert.assertTrue(errors.contains(LOG_ERROR));
	}

	private ImportResult importResultWithLogErrors()
	{
		return ImportTestUtils.importResultWithLogErrors(IMPEX_FILE_PATH, ERROR_LOG);
	}

	@Test(expected = ErrorParsingException.class)
	public void testParsingExceptionIsThrownWhenLogParsingCrashes() throws IOException
	{
		Mockito.doThrow(new IOException()).when(errorLogParser).parse(eq(ERROR_LOG), any(TextFile.class));

		converter.convert(importResultWithLogErrors());
	}

	@Test
	public void testErrorLogParsingRetrievesTheImpexBeingImportedFromTheLocationReportedInTheImportResult() throws IOException
	{
		converter.convert(importResultWithLogErrors());

		final ArgumentCaptor<TextFile> file = ArgumentCaptor.forClass(TextFile.class);
		Mockito.verify(errorLogParser).parse(eq(ERROR_LOG), file.capture());
		Assert.assertTrue(file.getValue().getPath().endsWith(IMPEX_FILE_PATH));
		Assert.assertTrue(file.getValue().getPath().contains(TENANT_MEDIA_DIR));
	}

	@Test
	public void testConvertedResultIsErrorWhenPreviewReportsErrors()
	{
		final ItemImportResult res = converter.convert(importResultWithPreviewErrors());

		Assert.assertFalse(res.isSuccessful());
	}

	@Test
	public void testConvertedResultContainsErrorsReportedInThePreview()
	{
		final Collection<ImportError> errors = converter.convert(importResultWithPreviewErrors()).getErrors();

		Assert.assertEquals(1, errors.size());
		Assert.assertTrue(errors.contains(PREVIEW_ERROR));
	}

	@Test(expected = ErrorParsingException.class)
	public void testParsingExceptionIsThrownWhenPreviewParsingCrashes()
	{
		Mockito.doThrow(new NumberFormatException()).when(previewParser).parse(PREVIEW);

		converter.convert(importResultWithPreviewErrors());
	}

	private ImportResult importResultWithPreviewErrors()
	{
		return ImportTestUtils.importResultWithPreviewErrors(IMPEX_FILE_PATH, ERROR_LOG, PREVIEW);
	}

	@Test
	public void testCreatesUnclassifiedErrorWhenErrorLogCannotBeParsed()
	{
		final String errorLog = "some unexpected and unstructured message";
		final ImportResult res = ImportTestUtils.importResultWithLogErrors(IMPEX_FILE_PATH, errorLog);

		final ItemImportResult result = converter.convert(res);

		Assert.assertFalse("Result is successful", result.isSuccessful());
		final Collection<ImportError> errors = result.getErrors();
		Assert.assertFalse("Errors not reported", errors.isEmpty());

		final ImportError err = errors.iterator().next();
		Assert.assertSame("Error was classified", ErrorCode.UNCLASSIFIED, err.getCode());
		Assert.assertEquals("Message is not the full log content", UNSTRUCTURED_ERROR_LOG, err.getMessage());
	}
}
