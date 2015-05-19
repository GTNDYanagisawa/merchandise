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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;
import de.hybris.platform.sap.sapordermgmtservices.constants.SapordermgmtservicesConstants;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
@UnitTest
@SuppressWarnings("javadoc")
public class DefaultMessagePopulatorTest extends SapordermanagmentBolSpringJunitTest
{

	private final DefaultMessagePopulator classUnderTest = new DefaultMessagePopulator();

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		LocaleUtil.setLocale(Locale.US);
	}

	@Test
	public void testPopulateStatus()
	{
		final CartModificationData target = new CartModificationData();
		final Message source = new Message(Message.ERROR);
		source.setResourceKey("test.key");
		classUnderTest.populate(source, target);
		assertNotNull(target);
		assertEquals(SapordermgmtservicesConstants.STATUS_SAP_ERROR, target.getStatusCode());
	}


	@Test
	public void testPopulateMessage()
	{
		final CartModificationData target = new CartModificationData();
		final Message source = new Message(Message.ERROR);
		source.setResourceKey("test.key");
		classUnderTest.populate(source, target);
		assertNotNull(target);
		final OrderEntryData entry = target.getEntry();
		assertNotNull(entry);
		final ProductData product = entry.getProduct();
		assertNotNull(product);
		assertFalse(product.getName().isEmpty());
	}

}
