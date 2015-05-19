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
package de.hybris.platform.servicelayer.keygenerator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.numberseries.NumberSeries;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.keygenerator.impl.PKGenerator;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.keygenerator.impl.SystemTimeGenerator;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class KeyGeneratorTest extends ServicelayerTransactionalBaseTest
{
	private static final Logger LOG = Logger.getLogger(KeyGeneratorTest.class.getName());

	@Resource
	private PersistentKeyGenerator orderCodeGenerator;
	@Resource
	private SystemTimeGenerator systemTimeGenerator;
	private PKGenerator pkGenerator;

	@Before
	public void setUp()
	{
		pkGenerator = new PKGenerator();
	}

	@Test
	public void testPKGenerator()
	{
		try
		{
			pkGenerator.generate();
			fail("UnsupportedOperationException expected");
		}
		catch (final UnsupportedOperationException e) // NOPMD
		{
			//OK
		}

		final PK pk = (PK) pkGenerator.generateFor(Integer.valueOf(1));
		assertNotNull(pk);
		assertEquals(Integer.valueOf(1), Integer.valueOf(pk.getTypeCode()));

		try
		{
			pkGenerator.reset();
			fail("UnsupportedOperationException expected");
		}
		catch (final UnsupportedOperationException e) // NOPMD
		{
			//OK
		}
	}

	@Test
	public void testOrderCodeGenerator()
	{
		resetOrderSeries();

		String number = (String) orderCodeGenerator.generate();
		assertNotNull(number);
		final NumberSeries series = Registry.getCurrentTenant().getSerialNumberGenerator().getInfo("order_code");

		if (series.getTemplate() == "$")
		{
			assertEquals("00000000", number);

			number = (String) orderCodeGenerator.generate();
			assertNotNull(number);
			assertEquals("00000001", number);

			resetOrderSeries();

			number = (String) orderCodeGenerator.generate();
			assertNotNull(number);
			assertEquals("00000000", number);
		}

		try
		{
			orderCodeGenerator.generateFor(new Object());
			fail("UnsupportedOperationException expected");
		}
		catch (final UnsupportedOperationException e) // NOPMD
		{
			//OK
		}
	}

	protected void resetOrderSeries()
	{
		try
		{
			orderCodeGenerator.reset();
		}
		catch (final JaloInvalidParameterException e)
		{
			// fine
		}
	}

	@Test
	public void testSystemTimeGenerator()
	{
		try
		{
			systemTimeGenerator.reset();
			fail("UnsupportedOperationException expected");
		}
		catch (final Exception e)
		{
			// OK
		}

		final Long time = (Long) systemTimeGenerator.generate();
		assertNotNull(time);

		try
		{
			systemTimeGenerator.generateFor(new Object());
			fail("UnsupportedOperationException expected");
		}
		catch (final Exception e)
		{
			// OK
		}
	}


}
