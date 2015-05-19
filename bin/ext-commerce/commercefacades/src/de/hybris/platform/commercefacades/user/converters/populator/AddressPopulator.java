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
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.Map;


/**
 * Converter implementation for {@link de.hybris.platform.core.model.user.AddressModel} as source and
 * {@link de.hybris.platform.commercefacades.user.data.AddressData} as target type.
 */
public class AddressPopulator implements Populator<AddressModel, AddressData>
{
	private Map<String, Converter<AddressModel, StringBuilder>> addressFormatConverterMap;
	private Converter<AddressModel, StringBuilder> defaultAddressFormatConverter;

	@Override
	public void populate(final AddressModel source, final AddressData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getPk() != null)
		{
			target.setId(source.getPk().toString());
		}

		target.setBillingAddress(Boolean.TRUE.equals(source.getBillingAddress()));
		target.setShippingAddress(Boolean.TRUE.equals(source.getShippingAddress()));
		target.setVisibleInAddressBook(Boolean.TRUE.equals(source.getVisibleInAddressBook()));

		if (source.getTitle() != null)
		{
			target.setTitle(source.getTitle().getName());
			target.setTitleCode(source.getTitle().getCode());
		}
		target.setFirstName(source.getFirstname());
		target.setLastName(source.getLastname());

		target.setCompanyName(source.getCompany());
		target.setLine1(source.getLine1());
		target.setLine2(source.getLine2());
		target.setTown(source.getTown());
		target.setPostalCode(source.getPostalcode());
		target.setPhone(source.getPhone1());
		target.setEmail(source.getEmail());

		final CountryModel countryModel = source.getCountry();
		String isoCode = null;
		if (countryModel != null)
		{
			final CountryData countryData = new CountryData();
			countryData.setIsocode(countryModel.getIsocode());
			isoCode = countryModel.getIsocode();
			countryData.setName(countryModel.getName());
			target.setCountry(countryData);
		}

		final RegionModel regionModel = source.getRegion();
		if (regionModel != null)
		{
			final RegionData regionData = new RegionData();
			regionData.setIsocode(regionModel.getIsocode());
			regionData.setIsocodeShort(regionModel.getIsocodeShort());
			regionData.setName(regionModel.getName());
			if (regionModel.getCountry() != null)
			{
				regionData.setCountryIso(regionModel.getCountry().getIsocode());
			}
			target.setRegion(regionData);
		}

		final Converter<AddressModel, StringBuilder> addressFormatConverter;
		if (addressFormatConverterMap.containsKey(isoCode))
		{
			addressFormatConverter = addressFormatConverterMap.get(isoCode);
		}
		else
		{
			addressFormatConverter = getDefaultAddressFormatConverter();
		}

		target.setFormattedAddress(addressFormatConverter.convert(source).toString());
	}

	protected Map<String, Converter<AddressModel, StringBuilder>> getAddressFormatConverterMap()
	{
		return addressFormatConverterMap;
	}

	@Required
	public void setAddressFormatConverterMap(final Map<String, Converter<AddressModel, StringBuilder>> addressFormatConverterMap)
	{
		this.addressFormatConverterMap = addressFormatConverterMap;
	}

	protected Converter<AddressModel, StringBuilder> getDefaultAddressFormatConverter()
	{
		return defaultAddressFormatConverter;
	}

	@Required
	public void setDefaultAddressFormatConverter(final Converter<AddressModel, StringBuilder> defaultAddressFormatConverter)
	{
		this.defaultAddressFormatConverter = defaultAddressFormatConverter;
	}
}
