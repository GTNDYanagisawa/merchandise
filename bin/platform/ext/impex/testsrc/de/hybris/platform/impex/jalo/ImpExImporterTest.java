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
package de.hybris.platform.impex.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.cronjob.CronJobDumpHandler;
import de.hybris.platform.impex.jalo.cronjob.ImpExImportCronJob;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * Integration test for {@link Importer}
 */
@IntegrationTest
public class ImpExImporterTest extends AbstractImpExTest
{
	private static final Logger LOG = Logger.getLogger(ImpExImporterTest.class);

	@Test
	public void testFullImport()
	{
		try
		{
			final InputStream inputStream = ImpExManager.class.getResourceAsStream("/impex/testfiles/productFull.impex");
			assertNotNull("Can not read from jar file 'productFull.impex'", inputStream);
			final CSVReader reader = new CSVReader(inputStream, windows1252.getCode());
			final Importer importer = new Importer(reader);
			final Product product = (Product) importer.importNext();
			assertNotNull("Imported product was null", product);
			importer.close();
			assertEquals("Dump file is at " + importer.getDumpHandler().getDumpAsFile().getAbsolutePath(), 0,
					importer.getDumpedLineCountPerPass());
		}
		catch (final UnsupportedEncodingException e)
		{
			fail(e.getMessage());
		}
		catch (final ImpExException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test2CycleImport()
	{
		try
		{
			final InputStream inputStream = ImpExManager.class.getResourceAsStream("/impex/testfiles/product2Cycle.impex");
			assertNotNull("Can not read from jar file 'product2Cycle.impex'", inputStream);
			final CSVReader reader = new CSVReader(inputStream, windows1252.getCode());
			final Importer importer = new Importer(reader);
			final Product product = (Product) importer.importNext();
			assertNotNull("Imported product was null", product);
			final Unit unit = (Unit) importer.importNext();
			assertNotNull("Imported unit was null", unit);
			final Product product2 = (Product) importer.importNext();
			assertEquals(product, product2);
			assertEquals(0, importer.getDumpedLineCountPerPass());
			assertEquals(1, importer.getDumpedLineCountOverall());
			importer.close();
		}
		catch (final UnsupportedEncodingException e)
		{
			fail(e.getMessage());
		}
		catch (final ImpExException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testUnresolvedImport()
	{
		Importer importer = null;
		try
		{
			final InputStream inputStream = ImpExManager.class.getResourceAsStream("/impex/testfiles/productUnresolved.impex");
			assertNotNull("Can not read from jar file 'productUnresolved.impex'", inputStream);
			final CSVReader reader = new CSVReader(inputStream, windows1252.getCode());
			importer = new Importer(reader);
			final Product product = (Product) importer.importNext();
			assertNotNull("Imported product was null", product);
			final Product product2 = (Product) importer.importNext();
			assertEquals(product, product2);
			assertEquals(1, importer.getDumpedLineCountPerPass());
			final Product product3 = (Product) importer.importNext();
			assertEquals(product, product3);
			assertEquals(1, importer.getDumpedLineCountPerPass());
			importer.importNext();

			fail("ImpExException expected caused by unresolved lines");
		}
		catch (final UnsupportedEncodingException e)
		{
			fail(e.getMessage());
		}
		catch (final ImpExException e)
		{
			if (e.getErrorCode() != ImpExException.ErrorCodes.CAN_NOT_RESOLVE_ANYMORE)
			{
				fail(e.getMessage());
			}
			importer.close();
			importer.getDumpHandler().getDumpAsFile().delete();
		}
	}

	@Test
	public void testUnresolvedImport2() throws JaloGenericCreationException, JaloAbstractTypeException, JaloItemNotFoundException
	{
		Importer importer = null;
		try
		{
			final InputStream inputStream = ImpExManager.class.getResourceAsStream("/impex/testfiles/productUnresolved.impex");
			assertNotNull("Can not read from jar file 'productUnresolved.impex'", inputStream);
			final CSVReader reader = new CSVReader(inputStream, windows1252.getCode());
			importer = new Importer(reader);
			final Product product = (Product) importer.importNext();
			assertNotNull("Imported product was null", product);
			final Product product2 = (Product) importer.importNext();
			assertEquals(product, product2);
			assertEquals(1, importer.getDumpedLineCountPerPass());
			final Product product3 = (Product) importer.importNext();
			assertEquals(product, product3);
			assertEquals(1, importer.getDumpedLineCountPerPass());
			importer.importAll();

			fail("ImpExException expected caused by unresolved lines");
		}
		catch (final UnsupportedEncodingException e)
		{
			fail(e.getMessage());
		}
		catch (final ImpExException e)
		{
			if (e.getErrorCode() != ImpExException.ErrorCodes.CAN_NOT_RESOLVE_ANYMORE)
			{
				fail(e.getMessage());
			}
			importer.close();
			importer.getDumpHandler().getDumpAsFile().delete();
		}
	}

	@Test
	public void testCronJob() throws UnsupportedEncodingException
	{
		InputStream imputStream = null;
		ImpExImportCronJob job = null;
		try
		{
			imputStream = ImpExManager.class.getResourceAsStream("/impex/testfiles/productUnresolved.impex");
			job = ImpExManager.getInstance().createDefaultImpExImportCronJob();
			final ImpExMedia jobMedia = ImpExManager.getInstance().createImpExMedia("test media", CSVConstants.HYBRIS_ENCODING);
			jobMedia.setRemoveOnSuccess(true);
			job.setJobMedia(jobMedia);
			final Importer importer = new Importer(new CSVReader(imputStream, windows1252.getCode()));
			importer.setDumpHandler(new CronJobDumpHandler(job));
			importer.importAll();
			fail("Impex exception expected");
		}
		catch (final ImpExException e)
		{
			// correct
		}
		catch (final Exception e)
		{
			LOG.error("Unexpected exception", e);
			fail("Unexpected exception: " + e);
		}
		finally
		{
			IOUtils.closeQuietly(imputStream);
			try
			{
				job.remove();
			}
			catch (final ConsistencyCheckException e)
			{
				// remove silently
			}
		}
	}
}
