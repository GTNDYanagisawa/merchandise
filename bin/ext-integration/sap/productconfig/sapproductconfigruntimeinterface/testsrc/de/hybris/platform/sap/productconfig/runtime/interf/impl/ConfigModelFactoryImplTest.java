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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ConfigModelFactoryImplTest
{


	private ConfigModelFactoryImpl classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ConfigModelFactoryImpl();
	}

	@Test
	public void testCreateInstanceOfConfigModel()
	{
		final ConfigModel configModel = classUnderTest.createInstanceOfConfigModel();
		assertNotNull(configModel);
		assertTrue(configModel instanceof ConfigModelImpl);
	}

	@Test
	public void testCreateInstanceOfInstanceModel()
	{
		final InstanceModel instanceModel = classUnderTest.createInstanceOfInstanceModel();
		assertNotNull(instanceModel);
		assertTrue(instanceModel instanceof InstanceModelImpl);
	}

	@Test
	public void testCreateInstanceOfCsticModel()
	{
		final CsticModel csticModel = classUnderTest.createInstanceOfCsticModel();
		assertNotNull(csticModel);
		assertTrue(csticModel instanceof CsticModelImpl);
	}

	@Test
	public void testCreateInstanceOfCsticValueModel()
	{
		final CsticValueModel csticValueModel = classUnderTest.createInstanceOfCsticValueModel();
		assertNotNull(csticValueModel);
		assertTrue(csticValueModel instanceof CsticValueModelImpl);
	}

	@Test
	public void testCreateInstanceOfCsticGroupModel()
	{
		final CsticGroupModel csticGroupModel = classUnderTest.createInstanceOfCsticGroupModel();
		assertNotNull(csticGroupModel);
		assertTrue(csticGroupModel instanceof CsticGroupModelImpl);
	}

	public void testCreateInstanceOfConflictModel()
	{
		final ConflictModel conflictModel = classUnderTest.createInstanceOfConflictModel();
		assertNotNull(conflictModel);
		assertTrue(conflictModel instanceof ConflictModelImpl);
	}

	public void testCreateInstanceOfPriceModel()
	{
		final PriceModel priceModel = classUnderTest.createInstanceOfPriceModel();
		assertNotNull(priceModel);
		assertTrue(priceModel instanceof PriceModelImpl);
	}

}
