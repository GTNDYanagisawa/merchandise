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

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;




@SuppressWarnings(
{ "javadoc", "deprecation" })
public class DefaultScheduleLinePopulatorTest
{

	/**
	 * 
	 */
	private Date COMMITTED_DATE;
	private DefaultScheduleLinePopulator classUnderTest;

	@Before
	public void setUp() throws BusinessObjectException
	{
		LocaleUtil.setLocale(Locale.US);
		classUnderTest = new DefaultScheduleLinePopulator();

		final Calendar cal = Calendar.getInstance();
		cal.set(2014, 03, 15);
		COMMITTED_DATE = cal.getTime();

		final I18NService i18NService = EasyMock.createMock(I18NService.class);
		EasyMock.expect(i18NService.getCurrentLocale()).andReturn(Locale.US);
		classUnderTest.setI18NService(i18NService);



		final Converter converter = EasyMock.createMock(Converter.class);
		EasyMock.expect(converter.convertUnitKey2UnitID(null)).andReturn(null);
		EasyMock.expect(converter.getUnitScale("ST")).andReturn(2);
		final GenericFactory factory = EasyMock.createMock(GenericFactory.class);
		EasyMock.expect(factory.getBean(SapcommonbolConstants.ALIAS_BO_CONVERTER)).andReturn(converter);
		classUnderTest.setGenericFactory(factory);
		EasyMock.replay(i18NService, converter, factory);
	}

	@Test
	public void testBeanInstanciation()
	{
		Assert.assertNotNull(classUnderTest);
	}



	@Test
	public void testFormatDate()
	{
		final String formattedDate = classUnderTest.getFormattedDate(COMMITTED_DATE, Locale.US);
		assertEquals("April 15, 2014", formattedDate);
	}

	@Test
	public void testFormateDouble()
	{
		final String formatedValue1 = classUnderTest.formatDouble(15.5);
		final String formatedValue2 = classUnderTest.formatDouble(24.0);
		final String formatedValue3 = classUnderTest.formatDouble(25.01);
		final String formatedValue4 = classUnderTest.formatDouble(29.004);
		final String formatedValue5 = classUnderTest.formatDouble(29.987);

		assertEquals("15.5", formatedValue1);
		assertEquals("24", formatedValue2);
		assertEquals("25.01", formatedValue3);
		assertEquals("29.00", formatedValue4);
		assertEquals("29.99", formatedValue5);
	}

	@Test
	public void testFormatBigDecimal()
	{
		final BigDecimal quantity = new BigDecimal("1.21");
		final String unit = "ST";
		final String converted = classUnderTest.convertBigDecimalToString(quantity, unit);
		//US Locale!
		assertEquals("1.21", converted);
	}

	@Test
	public void testFormatBigDecimalNoDecimalSep()
	{
		final BigDecimal quantity = new BigDecimal("1.00");
		final String unit = "ST";
		final String converted = classUnderTest.convertBigDecimalToString(quantity, unit);
		//US Locale!
		assertEquals("1", converted);
	}

}
