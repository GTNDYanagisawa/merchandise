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
package de.hybris.platform.sap.core.bol.businessobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.bol.backend.BackendBusinessObjectBase;
import de.hybris.platform.sap.core.bol.test.SapcorebolSpringJUnitTest;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Test for Default Generic Factory.
 */
@SuppressWarnings("javadoc")
@ContextConfiguration(locations =
{ "BusinessObjectBaseBEDeterminationTest-spring.xml" })
@UnitTest
public class BusinessObjectBaseBEDeterminationTest extends SapcorebolSpringJUnitTest
{

	// BE Injection Test
	@Resource(name = "testBusinessObjectBaseBEInjection")
	private BusinessObjectBase boForBEInjection;
	@Resource(name = "testBackendBusinessObjectBaseBEInjection")
	private BackendBusinessObjectBase beForBEInjection;

	// BE Single Implementation Test
	@Resource(name = "testBusinessObjectBaseBESingleImplementation")
	private BusinessObjectBase boForBESingleImpl;
	@Resource(name = "testBackendBusinessObjectBaseBESingleImplementation")
	private BackendBusinessObjectBase beForBESingleImpl;

	// BE Determination Test
	@Resource(name = "testBusinessObjectBaseBEDetermination")
	private BusinessObjectBase boForBEDetermination;
	@Resource(name = "testBackendBusinessObjectBaseBEDeterminationCRM")
	private BackendBusinessObjectBase beForBEDeterminationCRM;
	@Resource(name = "testBackendBusinessObjectBaseBEDeterminationERP")
	private BackendBusinessObjectBase beForBEDeterminationERP;

	// BE Determination Test
	@Resource(name = "testBusinessObjectBaseBENotUniqueDetermination")
	private BusinessObjectBase boForBENotUniqueDetermination;

	// BE Determination No Implementation Found Test
	@Resource(name = "testBusinessObjectBaseBEDeterminationNotFound")
	private BusinessObjectBase boForBEDeterminationNotFound;

	@Test
	public void testBEInjection() throws BackendException
	{
		final BackendBusinessObject be = boForBEInjection.getBackendBusinessObject();
		assertSame(be, beForBEInjection);
	}

	@Test
	public void testBEInjectionInitializeException() throws BackendException
	{
		try
		{
			boForBEInjection.getBackendBusinessObject(true);
			Assert.fail("BackendDeterminationRuntimeException expected when initializing injected backend object!");
		}
		catch (final BackendDeterminationRuntimeException e)
		{
			// ok
			final String message = e.getMessage();
			assertEquals(
					"Initializing of the backend object works only in determination mode! Here, the backend object has been set explicitly using injection or coding.",
					message);
		}
	}

	@Test
	public void testBESetByCodingInitializeException() throws BackendException
	{
		boForBESingleImpl.setBackendObject(beForBEInjection);
		try
		{
			boForBESingleImpl.getBackendBusinessObject(true);
			Assert.fail("BackendDeterminationRuntimeException expected when initializing injected backend object!");
		}
		catch (final BackendDeterminationRuntimeException e)
		{
			// ok
			final String message = e.getMessage();
			assertEquals(
					"Initializing of the backend object works only in determination mode! Here, the backend object has been set explicitly using injection or coding.",
					message);
		}
	}

	@Test
	public void testBESingleImplementation() throws BackendException
	{
		final BackendBusinessObject be = boForBESingleImpl.getBackendBusinessObject();
		assertSame(be, beForBESingleImpl);
	}

	@Test
	public void testBESingleImplementationInitialize() throws BackendException
	{
		final BackendBusinessObject beBefore = boForBESingleImpl.getBackendBusinessObject();
		final BackendBusinessObject beAfter = boForBESingleImpl.getBackendBusinessObject(true);
		assertNotSame(beBefore, beAfter);
	}

	@Test
	public void testBEDeterminationCRM() throws BackendException
	{
		boForBEDetermination.setBackendType("CRM");
		final BackendBusinessObject be = boForBEDetermination.getBackendBusinessObject();
		assertSame(be, beForBEDeterminationCRM);
	}

	@Test
	public void testBEDeterminationCRMInitialize() throws BackendException
	{
		boForBEDetermination.setBackendType("CRM");
		final BackendBusinessObject beBefore = boForBEDetermination.getBackendBusinessObject();
		final BackendBusinessObject beAfter = boForBEDetermination.getBackendBusinessObject(true);
		assertNotSame(beBefore, beAfter);
	}

	@Test
	public void testBEDeterminationERP() throws BackendException
	{
		final BackendBusinessObject be = boForBEDetermination.getBackendBusinessObject();
		assertSame(be, beForBEDeterminationERP);
	}

	@Test
	public void testBEDeterminationERPInitialize() throws BackendException
	{
		final BackendBusinessObject beBefore = boForBEDetermination.getBackendBusinessObject();
		final BackendBusinessObject beAfter = boForBEDetermination.getBackendBusinessObject(true);
		assertNotSame(beBefore, beAfter);
	}

	@Test
	public void testBENotUniqueDetermination() throws BackendException
	{
		boForBENotUniqueDetermination.setBackendType("CRM");
		try
		{
			boForBENotUniqueDetermination.getBackendBusinessObject();
			Assert.fail("BackendDeterminationRuntimeException expected when requesting backend object first time since determination is not unique!");
		}
		catch (final BackendDeterminationRuntimeException e)
		{
			final String message = e.getMessage();
			assertEquals(
					"Determination of the backend object failed since there exists multiple implementations for backend interface 'interface de.hybris.platform.sap.core.bol.businessobject.test.be.TestBackendInterfaceBENotUniqueDetermination'!  --> Bean 'testBackendBusinessObjectBaseBENotUniqueDetermination'; Bean 'testBackendBusinessObjectBaseBENotUniqueDeterminationCRM' [@BackendType('CRM')]; ",
					message);
		}
	}

	@Test
	public void testBEDeterminationNotFound() throws BackendException
	{
		try
		{
			boForBEDeterminationNotFound.getBackendBusinessObject();
			Assert.fail("BackendDeterminationRuntimeException expected when requesting backend object first time since determination is not unique!");
		}
		catch (final BackendDeterminationRuntimeException e)
		{
			final String message = e.getMessage();
			assertEquals(
					"Determination of the backend object failed since no implementation for backend interface 'interface de.hybris.platform.sap.core.bol.businessobject.test.be.TestBackendInterfaceBEDeterminationNotFound' has been found!",
					message);
		}
	}

}
