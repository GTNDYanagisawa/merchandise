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
package de.hybris.platform.servicelayer.impex;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelInitializationException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.impex.impl.FileBasedImpExResource;
import de.hybris.platform.servicelayer.impex.impl.MediaBasedImpExResource;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.Config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultImportServiceIntegrationTest extends ServicelayerTest
{
	@Resource
	private ImportService importService;
	@Resource
	private ModelService modelService;
	@Resource
	private MediaService mediaService;
	private String legacyModeBackup;

	@Before
	public void setUp() throws Exception
	{
		legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		createCoreData();
	}

	@After
	public void setLegacyMode()
	{
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);
	}

	@Test
	public void testFileBasedImportResource() throws IOException
	{
		final String data = "INSERT Language;isocode;active\n;test;true";
		final File testFile = File.createTempFile("test", "test");
		final PrintWriter writer = new PrintWriter(testFile);
		writer.write(data);
		writer.close();

		final ImpExResource res = new FileBasedImpExResource(testFile, "windows-1252");
		final ImpExMediaModel media = res.getMedia();
		assertNotNull(media);
		assertArrayEquals(data.getBytes(), mediaService.getDataFromMedia(media));
		assertEquals("windows-1252", media.getEncoding().getCode().toLowerCase());
	}

	@Test
	public void testStreamBasedImportResource() throws IOException
	{
		final String data = "INSERT Language;isocode;active\n;test;true";

		final ImpExResource res = new StreamBasedImpExResource(new ByteArrayInputStream(data.getBytes()),
				CSVConstants.HYBRIS_ENCODING);
		final ImpExMediaModel media = res.getMedia();
		assertNotNull(media);
		assertArrayEquals(data.getBytes(), mediaService.getDataFromMedia(media));
	}

	@Test
	public void testMediaBasedImportResource() throws IOException
	{
		final String data = "INSERT Language;isocode;active\n;test;true";
		final ImpExMediaModel newMedia = modelService.create(ImpExMediaModel._TYPECODE);
		try
		{
			modelService.initDefaults(newMedia);
		}
		catch (final ModelInitializationException e)
		{
			throw new SystemException(e);
		}
		modelService.save(newMedia);
		mediaService.setDataForMedia(newMedia, data.getBytes());

		final ImpExResource res = new MediaBasedImpExResource(newMedia);
		final ImpExMediaModel media = res.getMedia();
		assertNotNull(media);
		assertArrayEquals(data.getBytes(), mediaService.getDataFromMedia(media));
	}

	@Test
	public void testImportByResource()
	{
		final ImpExResource mediaRes = new StreamBasedImpExResource(new ByteArrayInputStream(
				"INSERT Language;isocode;active\n;test;true".getBytes()), CSVConstants.HYBRIS_ENCODING);
		final ImportResult result = importService.importData(mediaRes);
		assertNotNull(result);
		assertNotNull("Language 'test' was not imported", getOrCreateLanguage("test"));
	}

	@Test
	public void testImportByConfig()
	{
		final ImpExResource mediaRes = new StreamBasedImpExResource(new ByteArrayInputStream(
				"INSERT Language;isocode;active\n;test;true".getBytes()), CSVConstants.HYBRIS_ENCODING);

		final ImportConfig config = new ImportConfig();
		config.setScript(mediaRes);

		final ImportResult result = importService.importData(config);
		assertNotNull(result);
		assertTrue(result.isSuccessful());
		assertFalse(result.isError());
		assertFalse(result.hasUnresolvedLines());
		assertTrue(result.isFinished());
		assertNotNull("Language 'test' was not imported", getOrCreateLanguage("test"));
	}

	@Test
	public void testImportByConfigWithError()
	{
		internalImportByConfigWithError(false);
	}

	@Test
	public void testImportByConfigWithErrorFailSafe()
	{
		internalImportByConfigWithError(true);
	}

	private void internalImportByConfigWithError(final boolean failOnError)
	{
		try
		{
			TestUtils.disableFileAnalyzer();
			final ImpExResource mediaRes = new StreamBasedImpExResource(new ByteArrayInputStream(
					"UPDATE Language;isocode[unique=true];active\n;test;true".getBytes()), CSVConstants.HYBRIS_ENCODING);

			final ImportConfig config = new ImportConfig();
			config.setScript(mediaRes);
			config.setFailOnError(failOnError);

			final ImportResult result = importService.importData(config);
			assertNotNull(result);
			assertFalse(result.isSuccessful());
			assertTrue(result.isError());
			assertTrue(result.hasUnresolvedLines());
			assertNotNull(result.getUnresolvedLines());
			assertTrue(result.isFinished());
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testCreateExportImportJobs()
	{
		//innerTestType("ZoneDeliveryMode");
		try
		{
			final ItemModel modelItem = modelService.create("ImpExImportCronJob");

			modelService.save(modelItem);
			//System.out.println(modelService.getSource(modelItem));
			Assert.assertEquals(ImpExManager.getInstance().createDefaultImpExImportCronJob().getComposedType(),
					((Item) modelService.getSource(modelItem)).getComposedType());

		}
		finally
		{
			//nothing here
		}

		try
		{
			final ItemModel modelItem = modelService.create("ImpExExportCronJob");

			modelService.save(modelItem);
			//System.out.println(modelService.getSource(modelItem));
			Assert.assertEquals(ImpExManager.getInstance().createDefaultExportCronJob().getComposedType(),
					((Item) modelService.getSource(modelItem)).getComposedType());

		}
		finally
		{
			//nothing here
		}
	}


	@Test
	public void shouldImportScriptWithLegacyModeOnWhenGlobalSwitchIsOffUsingImportConfig()
	{
		// given
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
		final ImpExResource mediaRes = new StreamBasedImpExResource(new ByteArrayInputStream(
				"INSERT Language;isocode;active\n;test;true".getBytes()), CSVConstants.HYBRIS_ENCODING);
		final ImportConfig config = new ImportConfig();
		config.setLegacyMode(Boolean.TRUE);
		config.setSynchronous(true);
		config.setFailOnError(true);
		config.setScript(mediaRes);
		config.setRemoveOnSuccess(false);

		// when
		final ImportResult importResult = importService.importData(config);

		// then
		assertThat(importResult.isFinished()).isTrue();
		assertThat(importResult.isError()).isFalse();
		assertThat(Boolean.parseBoolean(Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY))).isFalse();
		assertThat(importResult.getCronJob().getLegacyMode()).isTrue();
	}

	@Test
	public void shouldImportScriptWithLegacyModeOffWhenGlobalSwitchIsOnUsingImportConfig()
	{
		// given
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		final ImpExResource mediaRes = new StreamBasedImpExResource(new ByteArrayInputStream(
				"INSERT Language;isocode;active\n;test;true".getBytes()), CSVConstants.HYBRIS_ENCODING);
		final ImportConfig config = new ImportConfig();
		config.setLegacyMode(Boolean.FALSE);
		config.setSynchronous(true);
		config.setFailOnError(true);
		config.setScript(mediaRes);
		config.setRemoveOnSuccess(false);

		// when
		final ImportResult importResult = importService.importData(config);

		// then
		assertThat(importResult.isFinished()).isTrue();
		assertThat(importResult.isError()).isFalse();
		assertThat(Boolean.parseBoolean(Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY))).isTrue();
		assertThat(importResult.getCronJob().getLegacyMode()).isFalse();
	}

	@Test
	public void shouldImportScriptWithGlobalModeWhenImportConfigHasLegacyModeNull()
	{
		// given
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		final ImpExResource mediaRes = new StreamBasedImpExResource(new ByteArrayInputStream(
				"INSERT Language;isocode;active\n;test;true".getBytes()), CSVConstants.HYBRIS_ENCODING);
		final ImportConfig config = new ImportConfig();
		config.setSynchronous(true);
		config.setFailOnError(true);
		config.setScript(mediaRes);
		config.setRemoveOnSuccess(false);

		// when
		final ImportResult importResult = importService.importData(config);

		// then
		assertThat(importResult.isFinished()).isTrue();
		assertThat(importResult.isError()).isFalse();
		assertThat(Boolean.parseBoolean(Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY))).isTrue();
		assertThat(importResult.getCronJob().getLegacyMode()).isNull();
	}

	@Test
	public void shouldSeeFinishedStateForAsncImport() throws InterruptedException
	{
		// given
		final ImportConfig config = new ImportConfig();
		config.setScript(//
		"INSERT Title;code[unique=true]\n"//
				+ ";foo;\n" //
				+ "\"#% java.lang.Thread.sleep(2000);\";\n" //
				+ ";bar;\n" //
		);
		config.setSynchronous(false);
		config.setFailOnError(true);
		config.setRemoveOnSuccess(false);
		config.setEnableCodeExecution(Boolean.TRUE);

		// when
		final long now = System.currentTimeMillis();
		final ImportResult importResult = importService.importData(config);
		final long maxWait = System.currentTimeMillis() + (30 * 1000);
		do
		{
			Thread.sleep(500);
		}
		while (!importResult.isFinished() && System.currentTimeMillis() < maxWait);

		// then
		assertThat(System.currentTimeMillis() - now).isGreaterThanOrEqualTo(2000);
		assertThat(importResult.isFinished()).isTrue();
		assertThat(importResult.isError()).isFalse();
	}
}
