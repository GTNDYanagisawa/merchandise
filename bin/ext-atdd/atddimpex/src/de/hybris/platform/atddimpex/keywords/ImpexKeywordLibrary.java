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
package de.hybris.platform.atddimpex.keywords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.platform.atddengine.framework.RobotTest;
import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.atddengine.keywords.ImpExAdaptor;
import de.hybris.platform.atddengine.keywords.ImpExAdaptorAware;
import de.hybris.platform.atddengine.keywords.RobotTestContext;
import de.hybris.platform.atddengine.keywords.RobotTestContextAware;
import de.hybris.platform.atddengine.templates.TemplateProcessor;
import de.hybris.platform.atddengine.templates.TemplateProcessorFactory;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;


public class ImpexKeywordLibrary extends AbstractKeywordLibrary implements ImpExAdaptorAware, RobotTestContextAware
{
	private static final String DEFAULT_ENCODING = "UTF-8";

	private static final Logger LOG = Logger.getLogger(ImpexKeywordLibrary.class);

	private static final String PROP_REPORT_PATH = "atddengine.report-path";

	private ImpExAdaptor impExAdaptor;

	private OutputStream impExLogOutputStream;

	private TemplateProcessor impexTemplateProcessor;

	private RobotTestContext robotTestContext;

	private static final String IMPEX_LOG_LINE_SEPERATOR = System.getProperty("line.separator");

	private static final String IMPEX_LOG_RESOURCE_BANNER = String.format("# %s %s", StringUtils.repeat("#", 80),
			IMPEX_LOG_LINE_SEPERATOR);

	@Autowired
	private TemplateProcessorFactory templateProcessorFactory;

	@Autowired
	private ModelService modelService;

	private String legacyModeMarker;

	public String generateImpExFromTemplate(final String templatePath, final String[] params) throws IOException
	{
		final Map<String, Object> binding = new HashMap<String, Object>();

		if (params.length % 2 != 0)
		{
			throw new IllegalArgumentException("Given parameters must be a multiple of 2");
		}

		for (int i = 0; i < params.length; i += 2)
		{
			binding.put(params[i], params[i + 1]);
		}

		final StringWriter writer = new StringWriter();
		try
		{
			getImpexTemplateProcessor().processTemplate(writer, templatePath, binding);
		}
		catch (final IOException e)
		{
			LOG.error("Unable to retrieve the ImpEx template processor", e); // this is necessary since the python layer swallows the stack trace
			throw e;
		}

		return writer.toString();
	}

	private OutputStream getImpExLogOutputStream(final String resourceName) throws IOException
	{
		if (impExLogOutputStream == null)
		{
			final RobotTest robotTest = robotTestContext.getCurrentRobotTest();

			if (robotTest == null)
			{
				throw new IllegalStateException("KeywordMethods must only be called within a valid RobotTestContext");
			}
			else
			{
				final String testSuiteName = robotTest.getTestSuite().getName().replaceAll(Pattern.quote(" "), "_");

				final String impExLogPath = String.format("%s/%s/%s-data.impex", robotTestContext.getProjectName(), testSuiteName,
						robotTest.getName());
				final File impExLogFile = new File(Config.getParameter(PROP_REPORT_PATH), impExLogPath);

				impExLogOutputStream = FileUtils.openOutputStream(impExLogFile);
			}
		}
		else
		{
			IOUtils.write(IMPEX_LOG_LINE_SEPERATOR, impExLogOutputStream, DEFAULT_ENCODING);
		}

		IOUtils.write(IMPEX_LOG_RESOURCE_BANNER, impExLogOutputStream, DEFAULT_ENCODING);
		IOUtils.write(String.format("# Import from %s %s", resourceName, IMPEX_LOG_LINE_SEPERATOR), impExLogOutputStream,
				DEFAULT_ENCODING);
		IOUtils.write(IMPEX_LOG_RESOURCE_BANNER, impExLogOutputStream, DEFAULT_ENCODING);

		impExLogOutputStream.flush();

		return impExLogOutputStream;
	}

	protected TemplateProcessor getImpexTemplateProcessor() throws IOException
	{
		if (impexTemplateProcessor == null)
		{
			impexTemplateProcessor = templateProcessorFactory.createTemplateProcessor();
		}

		return impexTemplateProcessor;
	}

	private void importImpExFromInputStream(final String resourceName, InputStream inputStream) throws ImpExException, IOException
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Importing [" + resourceName + "]");

			final ByteArrayOutputStream impexData = new ByteArrayOutputStream();
			final TeeInputStream consoleTeeInputStream = new TeeInputStream(inputStream, impexData);

			LOG.info("ImpEx contents are:\n" + IOUtils.toString(consoleTeeInputStream, DEFAULT_ENCODING));

			inputStream = new ByteArrayInputStream(impexData.toByteArray());
		}

		final OutputStream logOutputStream = getImpExLogOutputStream(resourceName);
		final InputStream fileFeeInputStream = new TeeInputStream(inputStream, logOutputStream);

		impExAdaptor.importStream(fileFeeInputStream, DEFAULT_ENCODING, resourceName);

		logOutputStream.flush();

		modelService.detachAll();
	}

	private void importImpExFromInputStream(final String resourceName, final InputStream inputStream,
			final String expectedException) throws IOException
	{
		Throwable exception = null;

		try
		{
			importImpExFromInputStream(resourceName, inputStream);
		}
		catch (final Exception e)
		{
			exception = e;
			while (exception.getCause() != null)
			{
				exception = exception.getCause();
			}
			if (exception.getClass().getSimpleName().equals(expectedException))
			{
				LOG.info("Expected [" + expectedException + "] caught: " + exception.getMessage());
			}
			else
			{
				LOG.error(exception.getMessage(), exception);
			}
		}
		finally
		{
			IOUtils.closeQuietly(inputStream);
		}

		if (expectedException == null)
		{
			assertNull("Import of resource " + resourceName + " failed: " + (exception == null ? "" : exception.getMessage()),
					exception);
		}
		else
		{
			assertNotNull("Expected [" + expectedException + "] not thrown", exception);
			assertEquals("Type of thrown exception does not match type of expected exception", expectedException, exception
					.getClass().getSimpleName());
		}
	}

	/**
	 * Imports an ImpEx from a classpath resource
	 * 
	 * @param resourceName
	 *           name of the classpath resource which contains the ImpEx
	 * @throws ImpExException
	 * @throws IOException
	 */
	public void importImpExFromResource(final String resourceName) throws ImpExException, IOException
	{
		final InputStream resourceAsStream = new ClassPathResource(resourceName).getInputStream();
		importImpExFromInputStream(resourceName, resourceAsStream, null);
	}

	/**
	 * Imports an ImpEx from a String variable
	 * 
	 * @param templatePath
	 *           path to the ImpEx velocity template
	 * @param impex
	 *           the ImpEx to import
	 * @param expectedException
	 *           the expected exception type (null if no exception is expected)
	 * @throws UnsupportedEncodingException
	 * @throws ImpExException
	 * @throws IOException
	 */
	public void importImpExFromVariable(final String templatePath, final String impex, final String expectedException)
			throws UnsupportedEncodingException, ImpExException, IOException
	{
		final InputStream inputStream = new ByteArrayInputStream(impex.getBytes(DEFAULT_ENCODING));
		importImpExFromInputStream(templatePath, inputStream, expectedException);
	}

	/**
	 * Switches the legacy mode for ImpEx imports on/off.
	 * 
	 * @param legacyMode
	 *           possible values "true" or "false" (case is ignored)
	 */
	public void setImpExLegacyMode(final String legacyMode)
	{
		if (legacyMode != null)
		{
			final String lowerCase = legacyMode.toLowerCase(Locale.ENGLISH);
			legacyModeMarker = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
			LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeMarker);
			if ("false".equals(lowerCase) || "true".equals(lowerCase))
			{
				Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, lowerCase);
				LOG.info("Set " + ImpExConstants.Params.LEGACY_MODE_KEY + " to:" + legacyModeMarker);
			}
		}
	}

	/**
	 * Resets the legacy mode for ImpEx imports to the setting before the last
	 * {@link ImpexKeywordLibrary#setImpExLegacyMode(String)} call.
	 */
	public void resetImpExLegacyMode()
	{
		if (legacyModeMarker != null && ("false".equals(legacyModeMarker) || "true".equals(legacyModeMarker)))
		{
			Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeMarker);
			LOG.info("Reset " + ImpExConstants.Params.LEGACY_MODE_KEY + " to:" + legacyModeMarker);
			legacyModeMarker = null;
		}
	}

	@Override
	public void setImpExAdaptor(final ImpExAdaptor impexAdaptor)
	{
		this.impExAdaptor = impexAdaptor;
	}

	@Override
	public void setRobotTestContext(final RobotTestContext robotTestContext)
	{
		this.robotTestContext = robotTestContext;
	}
}
