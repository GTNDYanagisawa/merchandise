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
package de.hybris.platform.b2b.punchout.populators.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.apache.commons.lang.StringUtils;
import org.cxml.Address;
import org.cxml.CountryCode;
import org.cxml.Email;
import org.cxml.Fax;
import org.cxml.Phone;
import org.cxml.PostalAddress;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates an {@link AddressModel} instance out of a CXML {@link Address}.
 */
public class DefaultAddressModelPopulator implements Populator<Address, AddressModel>
{

	private CommonI18NService commonI18NService;
	private String phoneSegmentSeparator = " ";

	@Override
	public void populate(final Address source, final AddressModel target) throws ConversionException
	{
		final PostalAddress postalAddress = source.getPostalAddress();

		final String countryIsoCode = postalAddress.getCountry().getIsoCountryCode();
		// f.y.i.: Each complete ISO 3166-2 code consists of two parts, separated by a hyphen:
		// The 1st part is the ISO 3166-1 alpha-2 code of the country;
		// The 2nd part is a string of up to three alphanumeric characters, which is usually obtained from national 
		// sources and stems from coding systems already in use in the country concerned, but may also be developed by 
		// the ISO itself.
		final String stateIsoCode = countryIsoCode + "-" + postalAddress.getState();

		target.setStreetname(postalAddress.getStreet().iterator().next().getvalue());
		target.setTown(postalAddress.getCity());
		target.setCountry(commonI18NService.getCountry(countryIsoCode));
		target.setFirstname(postalAddress.getName());
		target.setPostalcode(postalAddress.getPostalCode());

		target.setRegion(commonI18NService.getRegion(target.getCountry(), stateIsoCode));

		target.setPhone1(toPhoneString(source.getPhone()));
		target.setFax(toFaxString(source.getFax()));
		target.setEmail(toEmailString(source.getEmail()));
	}

	protected String toEmailString(final Email email)
	{
		if (email != null)
		{
			return email.getvalue();
		}
		return null;
	}

	protected String toFaxString(final Fax fax)
	{
		if (fax != null)
		{
			for (final Object obj : fax.getTelephoneNumberOrURLOrEmail())
			{
				if (obj instanceof Phone)
				{
					return toPhoneString((Phone) obj);
				}
			}
		}
		return null;
	}

	protected String toPhoneString(final Phone phone)
	{
		if (phone != null && phone.getTelephoneNumber() != null)
		{
			final CountryCode countryCode = phone.getTelephoneNumber().getCountryCode();
			final String areaCode = phone.getTelephoneNumber().getAreaOrCityCode();
			final String number = phone.getTelephoneNumber().getNumber();
			final String extension = phone.getTelephoneNumber().getExtension();

			final StringBuilder phoneNumberBuilder = new StringBuilder();
			if (countryCode != null)
			{
				phoneNumberBuilder.append(countryCode.getvalue());
				phoneNumberBuilder.append(getPhoneSegmentSeparator());
			}
			phoneNumberBuilder.append(areaCode);
			phoneNumberBuilder.append(getPhoneSegmentSeparator());
			phoneNumberBuilder.append(number);
			if (StringUtils.isNotBlank(extension))
			{
				phoneNumberBuilder.append(" ext. ");
				phoneNumberBuilder.append(extension);
			}
			return phoneNumberBuilder.toString();
		}
		return null;
	}

	protected String getPhoneSegmentSeparator()
	{
		return phoneSegmentSeparator;
	}

	public void setPhoneSegmentSeparator(final String phoneSegmentSeparator)
	{
		this.phoneSegmentSeparator = phoneSegmentSeparator;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
