package com.hybris.datahub.core.facades;

import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.servicelayer.impex.ImportResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mockito.Mockito;


/**
 * Utilities to help with testing.
 */
public class ImportTestUtils
{

	/**
	 * Simulates an import error with the specified message.
	 *
	 * @param msg an error message for the error.
	 * @return an import error
	 */
	public static ImportError error(final String msg)
	{
		final ImportError err = Mockito.mock(ImportError.class);
		Mockito.doReturn(msg).when(err).getMessage();
		return err;
	}

	/**
	 * Simulates an import error with the specified error code and the message.
	 *
	 * @param code an error code to use for the error.
	 * @param msg an error message for the error.
	 * @return an import error
	 */
	public static ImportError error(final ErrorCode code, final String msg)
	{
		final ImportError err = error(msg);
		Mockito.doReturn(code).when(err).getCode();
		return err;
	}

	/**
	 * Simulates an import error for each message submitted.
	 *
	 * @param messages an array of messages to convert to import errors
	 * @return a collection of import errors with the specified messages.
	 */
	public static Collection<ImportError> errors(final String... messages)
	{
		final List<ImportError> errors = new ArrayList<>(messages.length);
		for (final String msg : messages)
		{
			errors.add(error(msg));
		}
		return errors;
	}

	/**
	 * Mocks an import result, which is wired up with the CronJobModel and WorkMedia. The result is neither successful
	 * nor erroneous and should be further mocked for that purpose.
	 *
	 * @param impexFileLoc path to the source of the impex file being imported, which is relative to the hybris server data
	 * directory.
	 * @return the mocked result.
	 */
	public static ImportResult importResult(final String impexFileLoc)
	{
		final ImpExMediaModel media = Mockito.mock(ImpExMediaModel.class);
		Mockito.doReturn(impexFileLoc).when(media).getLocation();

		final ImpExImportCronJobModel job = Mockito.mock(ImpExImportCronJobModel.class);
		Mockito.doReturn(media).when(job).getWorkMedia();

		final ImportResult res = Mockito.mock(ImportResult.class);
		Mockito.doReturn(job).when(res).getCronJob();
		
		final String tenantId = "master";
		Mockito.doReturn(tenantId).when(job).getTenantId();

		return res;
	}

	/**
	 * Mocks a successful import result.
	 *
	 * @return the mocked result.
	 */
	public static ImportResult successfulImportResult()
	{
		final ImportResult res = importResult(null);
		makeResultSuccessful(res);
		return res;
	}

	/**
	 * Takes mock of an <code>ImportResult</code> and finishes stubbing to simulate success import result.
	 *
	 * @param result a result mock to stub.
	 */
	public static void makeResultSuccessful(final ImportResult result)
	{
		Mockito.doReturn(true).when(result).isSuccessful();
		Mockito.doReturn(false).when(result).isError();
		Mockito.doReturn(false).when(result).hasUnresolvedLines();
	}

	/**
	 * Mocks an import result with errors present in the error log.
	 *
	 * @param impexFileLoc location of the impex source file being imported.
	 * @param errLogText content of the error log to mock in the result.
	 * @return the mocked result
	 */
	public static ImportResult importResultWithLogErrors(final String impexFileLoc, final String errLogText)
	{
		final ImportResult res = importResult(impexFileLoc);
		makeResultWithErrors(res, errLogText, null);
		return res;
	}

	/**
	 * Mocks an import result with errors present in the preview. Whenever reference violations are reported in the
	 * preview, the error log contains a generic error about reference violation. Therefore the error log content should
	 * passed too.
	 *
	 * @param impexFileLoc location of the impex source file being imported.
	 * @param errLogText content of the error log to mock in the result.
	 * @param preview content of the preview to mock in the result.
	 * @return the mocked result
	 */
	public static ImportResult importResultWithPreviewErrors(final String impexFileLoc, final String errLogText,
			final String preview)
	{
		final ImportResult res = importResult(impexFileLoc);
		makeResultWithErrors(res, errLogText, preview);
		return res;
	}

	/**
	 * Takes mock of an <code>ImportResult</code> and finishes stubbing to simulate errors present in the result.
	 *
	 * @param res a mock of the <code>ImportResult</code> to finish stubbing.
	 * @param errLogText content of the error log to mock in the import result.
	 */
	public static void makeResultWithErrors(final ImportResult res, final String errLogText)
	{
		makeResultWithErrors(res, errLogText, null);
	}

	/**
	 * Takes mock of an <code>ImportResult</code> and finishes stubbing to simulate errors present in the result.
	 *
	 * @param res a mock of the <code>ImportResult</code> to finish stubbing.
	 * @param errLogText content of the error log to mock in the import result.
	 * @param preview content of the preview to mock in the import result or <code>null</code>, if only error log errors
	 * should be simulated.
	 */
	public static void makeResultWithErrors(final ImportResult res, final String errLogText, final String preview)
	{
		Mockito.doReturn(false).when(res).isSuccessful();
		Mockito.doReturn(true).when(res).isError();
		Mockito.doReturn(preview != null).when(res).hasUnresolvedLines();

		final ImpExImportCronJobModel job = res.getCronJob();
		Mockito.doReturn(errLogText).when(job).getLogText();

		final ImpExMediaModel media = job.getWorkMedia();
		Mockito.doReturn(preview).when(media).getPreview();
		// the preview comes from 2 places in the ImportResult structure, so simulate the second one for flexible implementation
		Mockito.doReturn(media).when(res).getUnresolvedLines();
	}

	/**
	 * Converts separate strings into a multi-line text, where each string becomes a separate line.
	 *
	 * @param lines lines of text to merge together. Pass empty strings for empty (blank) lines in the resulting text.
	 * @return text consisting of the lines combined together.
	 */
	public static String toText(final String... lines)
	{
		return StringUtils.join(lines, System.lineSeparator());
	}
}
