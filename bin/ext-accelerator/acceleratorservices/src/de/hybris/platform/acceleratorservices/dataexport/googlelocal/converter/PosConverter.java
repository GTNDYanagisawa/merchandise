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
package de.hybris.platform.acceleratorservices.dataexport.googlelocal.converter;

import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;
import de.hybris.platform.acceleratorservices.dataexport.googlelocal.model.Business;
import de.hybris.platform.basecommerce.enums.WeekDay;
import de.hybris.platform.commerceservices.converter.impl.AbstractConverter;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.model.OpeningDayModel;
import de.hybris.platform.storelocator.model.OpeningScheduleModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.model.WeekdayOpeningDayModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.Message;


/**
 * Converter for {@link PointOfServiceModel}.
 */
public class PosConverter extends AbstractConverter<Message<PointOfServiceModel>, Business>
{
	//categories of the store defined by google
	private String businessCategories;
	private Map<String, String> internationalToInitialDigitCode = new HashMap<String, String>();
	public static final int FIRST_SPLIT_INDEX = 3;
	public static final int SECOND_SPLIT_INDEX = 6;

	protected String getBusinessCategories()
	{
		return businessCategories;
	}

	@Required
	public void setBusinessCategories(final String businessCategories)
	{
		this.businessCategories = businessCategories;
	}

	protected Map<String, String> getInternationalToInitialDigitCode()
	{
		return internationalToInitialDigitCode;
	}

	// Optional
	public void setInternationalToInitialDigitCode(final Map<String, String> internationalToInitialDigitCode)
	{
		this.internationalToInitialDigitCode = internationalToInitialDigitCode;
	}

	/**
	 * generate a string that represents the opening times in the format:
	 * 2:10:00:18:00,3:10:00:18:00,4:10:00:18:00,5:10:00:18:00,6:10:00:18:00,7:12:00:20:00
	 * 
	 */
	protected String generateOpeningHours(final Collection<OpeningDayModel> openingDateModels)
	{
		final StringBuilder openingHours = new StringBuilder();
		if (openingDateModels != null)
		{
			final ArrayList<OpeningDayModel> list = new ArrayList<OpeningDayModel>(openingDateModels);
			for (int i = 0; i < list.size(); i++)
			{
				final OpeningDayModel openingDateModel = list.get(i);
				if (openingDateModel instanceof WeekdayOpeningDayModel)
				{
					final WeekdayOpeningDayModel weekdayOpeningDay = (WeekdayOpeningDayModel) openingDateModel;
					final WeekDay ofWeek = weekdayOpeningDay.getDayOfWeek();
					final Date openingTime = weekdayOpeningDay.getOpeningTime();
					final Date closingTime = weekdayOpeningDay.getClosingTime();
					final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
					openingHours.append(i == 0 ? "" : ",");
					openingHours.append(ofWeek.ordinal() + 1).append(':').append(sdf.format(openingTime)).append(':')
							.append(sdf.format(closingTime));
				}

			}
		}

		return openingHours.toString();
	}

	/**
	 * Hyphens are recommended; however, parentheses are also accepted (xxx-xxx-xxxx or (xxx) xxx-xxxx).
	 * 
	 */
	protected String generatePhoneNumber(final String phone1)
	{
		if (phone1 != null)
		{
			final StringBuffer stringBuffer = new StringBuffer();
			String mainPhone = "";

			//if international
			if (phone1.charAt(0) == '+')
			{
				final int index = phone1.indexOf(' ');
				final String countryCode = phone1.substring(0, index);
				if (getInternationalToInitialDigitCode().containsKey(countryCode))
				{
					mainPhone = phone1.replace(countryCode, getInternationalToInitialDigitCode().get(countryCode));
				}
			}
			mainPhone = mainPhone.replace(" ", "");
			//according to the google doc, numbers should be 10 digits long
			for (int i = 0; i < mainPhone.length(); i++)
			{
				if (i == FIRST_SPLIT_INDEX || i == SECOND_SPLIT_INDEX)
				{
					stringBuffer.append('-');
				}
				stringBuffer.append(mainPhone.charAt(i));
			}
			return stringBuffer.toString();
		}

		return "";
	}

	@Override
	protected Business createTarget()
	{
		return new Business();
	}

	@Override
	public void populate(final Message<PointOfServiceModel> message, final Business business)
	{
		final PointOfServiceModel pointOfServiceModel = message.getPayload();
		if (pointOfServiceModel != null)
		{
			business.setStoreCode(pointOfServiceModel.getPk().getLongValueAsString());
			business.setName(pointOfServiceModel.getName());

			final AddressModel addressModel = pointOfServiceModel.getAddress();
			if (addressModel != null)
			{
				business.setMainPhone(generatePhoneNumber(addressModel.getPhone1()));
				business.setAddressLine1(addressModel.getLine1());
				business.setCity(addressModel.getTown());
				business.setState(addressModel.getRegion() != null ? addressModel.getRegion().getName() : "");
				business.setPostalCode(addressModel.getPostalcode());
				business.setCountryCode(addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : "");
				business.setHomePage(addressModel.getUrl());
				business.setAddressLine2(addressModel.getLine2());

			}
			business.setCategory(getBusinessCategories());
			final OpeningScheduleModel openingScheduleModel = pointOfServiceModel.getOpeningSchedule();
			if (openingScheduleModel != null)
			{
				final Collection<OpeningDayModel> openingDateModels = openingScheduleModel.getOpeningDays();
				final String openingHours = generateOpeningHours(openingDateModels);
				business.setHours(openingHours);
			}

			business.setDescription(pointOfServiceModel.getDescription());

			String currency = "";
			if (message.getHeaders().get("event") instanceof ExportDataEvent)
			{
				final ExportDataEvent ede = (ExportDataEvent) message.getHeaders().get("event");
				currency = ede.getCurrency().getIsocode();
			}
			business.setCurrency(currency);
			business.setLongitude(pointOfServiceModel.getLongitude().toString());
			business.setLatitude(pointOfServiceModel.getLatitude().toString());
		}
	}
}
