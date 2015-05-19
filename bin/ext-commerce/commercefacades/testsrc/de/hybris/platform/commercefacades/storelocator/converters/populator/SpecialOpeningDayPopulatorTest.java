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
package de.hybris.platform.commercefacades.storelocator.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storelocator.data.SpecialOpeningDayData;
import de.hybris.platform.commercefacades.storelocator.data.TimeData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.storelocator.model.SpecialOpeningDayModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SpecialOpeningDayPopulatorTest
{

	private final byte startHour = 9;
	private final byte startMinute = 59;

	private final byte endHour = 2;
	private final byte endMinute = 21;

	private Date start;
	private Date end;

	private static final Locale CURRENT_LOCALE = Locale.UK;
	private final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, CURRENT_LOCALE);

	private TimeDataPopulator timeDataPopulator;
	private AbstractPopulatingConverter<Date, TimeData> timeConverter;

	private SpecialOpeningDayPopulator specialOpeningDayPopulator;
	private AbstractPopulatingConverter<SpecialOpeningDayModel, SpecialOpeningDayData> specialOpeningDayConverter;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		timeDataPopulator = new TimeDataPopulator()
		{
			@Override
			protected DateFormat getDateFormat()
			{
				return dateFormat;
			}
		};
		timeConverter = new ConverterFactory<Date, TimeData, TimeDataPopulator>().create(TimeData.class, timeDataPopulator);

		specialOpeningDayPopulator = new SpecialOpeningDayPopulator()
		{
			@Override
			protected Locale getCurrentLocale()
			{
				return CURRENT_LOCALE;
			}
		};
		specialOpeningDayPopulator.setTimeDataConverter(timeConverter);
		specialOpeningDayConverter = new ConverterFactory<SpecialOpeningDayModel, SpecialOpeningDayData, SpecialOpeningDayPopulator>()
				.create(SpecialOpeningDayData.class, specialOpeningDayPopulator);

		start = initDate(startHour, startMinute);
		end = initDate(endHour, endMinute);
	}

	protected Date initDate(final byte hour, final byte minute)
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		return cal.getTime();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSpecialConverterForNullModel()
	{
		specialOpeningDayConverter.convert(null);
	}

	@Test
	public void testSpecialConverterForCorrectModel()
	{
		final ItemContextBuilder itemContextBuilder = ItemContextBuilder.createDefaultBuilder(SpecialOpeningDayModel.class);
		itemContextBuilder.setLocaleProvider(new StubLocaleProvider(CURRENT_LOCALE));
		final SpecialOpeningDayModel model = new SpecialOpeningDayModel(itemContextBuilder.build());

		model.setClosed(true);
		model.setClosingTime(end);
		model.setMessage("some comment here");
		model.setName("name here");
		model.setOpeningTime(start);
		model.setDate(new Date());
		final SpecialOpeningDayData data = specialOpeningDayConverter.convert(model);

		Assert.assertEquals(endHour, data.getClosingTime().getHour());
		Assert.assertEquals(endMinute, data.getClosingTime().getMinute());

		Assert.assertEquals(model.isClosed(), data.isClosed());
		Assert.assertEquals(model.getDate(), data.getDate());
		Assert.assertEquals(dateFormat.format(model.getDate()), data.getFormattedDate());
		Assert.assertEquals(model.getName(), data.getName());

		Assert.assertEquals(startHour, data.getOpeningTime().getHour());
		Assert.assertEquals(startMinute, data.getOpeningTime().getMinute());
	}

}
