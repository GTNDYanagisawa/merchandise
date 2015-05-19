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
package de.hybris.platform.commercefacades.i18n.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Facade for handling Region and Country Data.
 */
public class DefaultI18NFacade implements I18NFacade
{
	private CommonI18NService commonI18NService;
	private Converter<RegionModel, RegionData> regionConverter;
	private Converter<CountryModel, CountryData> countryConverter;

	@Override
	public RegionData getRegion(final String countryIso, final String regionIso)
	{
		try
		{
			final CountryModel countryModel = getCommonI18NService().getCountry(countryIso);
			if (countryModel != null && countryModel.getRegions() != null && !countryModel.getRegions().isEmpty())
			{
				final RegionModel regionModel = getCommonI18NService().getRegion(countryModel, regionIso);
				return getRegionConverter().convert(regionModel);
			}
			return null;
		}
		catch (final UnknownIdentifierException e)
		{
			return null;
		}
	}

	@Override
	public List<RegionData> getRegionsForCountryIso(final String countryIso)
	{
		final CountryModel countryModel = getCommonI18NService().getCountry(countryIso);
		final List<RegionModel> regions = new ArrayList<RegionModel>(countryModel.getRegions());
		Collections.sort(regions, RegionNameComparator.INSTANCE);
		return Converters.convertAll(regions, getRegionConverter());
	}

	@Override
	public Map<String, List<RegionData>> getRegionsForAllCountries()
	{
		final List<String> countryIsoCodes = new ArrayList<String>();
		for (final CountryModel country : getCommonI18NService().getAllCountries())
		{
			if (country.getRegions() != null && !country.getRegions().isEmpty())
			{
				countryIsoCodes.add(country.getIsocode());
			}
		}

		return getRegionsForCountries(countryIsoCodes);
	}

	@Override
	public Map<String, List<RegionData>> getRegionsForCountries(final Collection<String> countryIsoCodes)
	{
		final Map<String, List<RegionData>> regionsForCountries = new HashMap<String, List<RegionData>>();
		for (final String countryIsoCode : countryIsoCodes)
		{
			final List<RegionData> regions = getRegionsForCountryIso(countryIsoCode);
			regionsForCountries.put(countryIsoCode, regions);
		}

		return regionsForCountries;
	}

	@Override
	public CountryData getCountryForIsocode(final String countryIso)
	{
		validateParameterNotNullStandardMessage("countryIso", countryIso);
		return getCountryConverter().convert(getCommonI18NService().getCountry(countryIso));
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected Converter<RegionModel, RegionData> getRegionConverter()
	{
		return regionConverter;
	}

	@Required
	public void setRegionConverter(final Converter<RegionModel, RegionData> regionConverter)
	{
		this.regionConverter = regionConverter;
	}

	protected Converter<CountryModel, CountryData> getCountryConverter()
	{
		return countryConverter;
	}

	@Required
	public void setCountryConverter(final Converter<CountryModel, CountryData> countryConverter)
	{
		this.countryConverter = countryConverter;
	}

	protected static class RegionNameComparator extends AbstractComparator<RegionModel>
	{
		public static final RegionNameComparator INSTANCE = new RegionNameComparator();

		@Override
		protected int compareInstances(final RegionModel regionModel1, final RegionModel regionModel2)
		{
			// Compare based on region name
			return compareValues(regionModel1.getName(), regionModel2.getName(), Boolean.TRUE.booleanValue());
		}
	}
}
