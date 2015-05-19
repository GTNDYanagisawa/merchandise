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
package de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class HeaderBaseImplTest extends SapordermanagmentBolSpringJunitTest
{

	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_HEADER)
	private HeaderBaseImpl classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		//
	}

	@Test
	public void testConsolidateDate()
	{
		final Date date = new Date();
		final Date consolidatedDate = classUnderTest.consolidateDate(date);

		assertEquals("Dates must be equal", date, consolidatedDate);
	}

	@Test
	public void testConsolidatedDate_farFuture()
	{
		Calendar calendar = new GregorianCalendar(10000, 5, 5);
		final Date date = calendar.getTime();
		final Date consolidatedDate = classUnderTest.consolidateDate(date);

		calendar = new GregorianCalendar(9999, 5, 5);
		final Date expectedDate = calendar.getTime();

		assertEquals("Date must be 5/5/9999", expectedDate, consolidatedDate);

	}

	@Test
	public void testRequestedDeliveryDate()
	{
		final Date date = new Date();
		classUnderTest.setReqDeliveryDate(date);
		final Date consolidatedDate = classUnderTest.getReqDeliveryDate();

		assertEquals("Dates must be equal", date, consolidatedDate);
	}

	@Test
	public void testRequestedDeliveryDate_farFuture()
	{
		Calendar calendar = new GregorianCalendar(10000, 5, 5);
		final Date date = calendar.getTime();
		classUnderTest.setReqDeliveryDate(date);
		final Date consolidatedDate = classUnderTest.getReqDeliveryDate();

		calendar = new GregorianCalendar(9999, 5, 5);
		final Date expectedDate = calendar.getTime();

		assertEquals("Date must be 5/5/9999", expectedDate, consolidatedDate);

	}

	@Test
	public void testRequestedDeliveryDate_test()
	{
		final Date date = null;
		classUnderTest.setReqDeliveryDate(date);
		final Date consolidatedDate = classUnderTest.getReqDeliveryDate();

		assertEquals("Dates must be equal", date, consolidatedDate);
	}

	@Test
	public void testChangeable()
	{
		classUnderTest.setChangeable(false);
	}

}
