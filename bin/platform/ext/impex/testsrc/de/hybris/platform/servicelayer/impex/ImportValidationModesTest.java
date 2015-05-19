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
package de.hybris.platform.servicelayer.impex;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.imp.ImpExImportReader;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.util.CSVReader;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("deprecation")
@IntegrationTest
public class ImportValidationModesTest extends ServicelayerTest
{
	private static final Charset ENCODING = Charsets.UTF_8;
	private static final EnumerationValue STRICT_IMPORT_VALIDATION_MODE = ImpExManager.getImportStrictMode();
	private static final EnumerationValue RELAXED_IMPORT_VALIDATION_MODE = ImpExManager.getImportRelaxedMode();
	private String impexLegacyModeFlagToRestore;

	@Before
	public void setUp()
	{
		impexLegacyModeFlagToRestore = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
	}

	@After
	public void tearDown()
	{
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, impexLegacyModeFlagToRestore);
	}

	@Test
	public void shouldUseStrictValidationModeByDefaultWhenRunningInLegacyMode() throws UnsupportedEncodingException
	{
		enableImpexLegacyMode();
		final CSVReader csvReader = givenCSVReader();

		final ImpExImportReader impExImportReader = new ImpExImportReader(csvReader);

		assertThat(impExImportReader.getValidationMode()).isEqualTo(STRICT_IMPORT_VALIDATION_MODE);
	}

	@Test
	public void shouldUseRelaxedValidationModeByDefaultWhenRunningInNonLegacyMode() throws UnsupportedEncodingException
	{
		disableImpexLegacyMode();
		final CSVReader csvReader = givenCSVReader();

		final ImpExImportReader impExImportReader = new ImpExImportReader(csvReader);

		assertThat(impExImportReader.getValidationMode()).isEqualTo(RELAXED_IMPORT_VALIDATION_MODE);
	}

	@Test
	public void shouldUseRelaxedValidationModeSetByUserWhenRunningInLegacyMode() throws UnsupportedEncodingException
	{
		enableImpexLegacyMode();
		final CSVReader csvReader = givenCSVReader();

		final ImpExImportReader impExImportReader = new ImpExImportReader(csvReader);
		impExImportReader.setValidationMode(RELAXED_IMPORT_VALIDATION_MODE);

		assertThat(impExImportReader.getValidationMode()).isEqualTo(RELAXED_IMPORT_VALIDATION_MODE);
	}

	@Test
	public void shouldUseStrictValidationModeSetByUserWhenRunningInLegacyMode() throws UnsupportedEncodingException
	{
		enableImpexLegacyMode();
		final CSVReader csvReader = givenCSVReader();

		final ImpExImportReader impExImportReader = new ImpExImportReader(csvReader);
		impExImportReader.setValidationMode(STRICT_IMPORT_VALIDATION_MODE);

		assertThat(impExImportReader.getValidationMode()).isEqualTo(STRICT_IMPORT_VALIDATION_MODE);
	}

	@Test
	public void shouldUseRelaxedValidationModeSetByUserWhenRunningInNonLegacyMode() throws UnsupportedEncodingException
	{
		disableImpexLegacyMode();
		final CSVReader csvReader = givenCSVReader();

		final ImpExImportReader impExImportReader = new ImpExImportReader(csvReader);
		impExImportReader.setValidationMode(RELAXED_IMPORT_VALIDATION_MODE);

		assertThat(impExImportReader.getValidationMode()).isEqualTo(RELAXED_IMPORT_VALIDATION_MODE);
	}

	@Test
	public void shouldUseStrictValidationModeSetByUserWhenRunningInNonLegacyMode() throws UnsupportedEncodingException
	{
		disableImpexLegacyMode();
		final CSVReader csvReader = givenCSVReader();

		final ImpExImportReader impExImportReader = new ImpExImportReader(csvReader);
		impExImportReader.setValidationMode(STRICT_IMPORT_VALIDATION_MODE);

		assertThat(impExImportReader.getValidationMode()).isEqualTo(STRICT_IMPORT_VALIDATION_MODE);
	}

	@Test
	public void shouldReachServiceLayerWhenRunningInNonLegacyMode()
	{
		final InputStream impexStream = givenImpexStream();

		try
		{
			importStream(impexStream, ENCODING.name(), "testImpex", false);
		}
		catch (final ImpExException expectedImpExException)
		{
			final Throwable expectedModelSavingException = Utilities.getRootCauseOfType(expectedImpExException,
					ModelSavingException.class);
			assertThat(expectedModelSavingException).isNotNull().isInstanceOf(ModelSavingException.class);
			assertThat(expectedModelSavingException.getCause()).isNotNull().isInstanceOf(InterceptorException.class);
			return;
		}
		fail("Exception was expected.");
	}

	private CSVReader givenCSVReader() throws UnsupportedEncodingException
	{
		return new CSVReader(givenImpexStream(), ENCODING.name());
	}

	private InputStream givenImpexStream()
	{
		return IOUtils.toInputStream("INSERT_UPDATE Title;name[unique=true, lang=en];\n;test;\n", ENCODING);
	}

	private void enableImpexLegacyMode()
	{
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, Boolean.TRUE.toString());
	}

	private void disableImpexLegacyMode()
	{
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, Boolean.FALSE.toString());
	}
}
